package th.mfu.pvz.catalog.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import th.mfu.pvz.catalog.domain.Plant;
import th.mfu.pvz.catalog.domain.Product;
import th.mfu.pvz.catalog.dto.ProductDTO;
import th.mfu.pvz.catalog.dto.StockAdjustmentDTO;
import th.mfu.pvz.catalog.dto.mapper.ProductMapper;
import th.mfu.pvz.catalog.repository.PlantRepository;
import th.mfu.pvz.catalog.repository.ProductRepository;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private Environment env;
    
    //เติมหมายเลขพอร์ตปัจจุบันของ Server (เช่น 8100 หรือ 8101) ใส่ลงในฟิลด์ servedBy ของ DTO
    private ProductDTO enrichDTO(ProductDTO dto) {
        if (dto != null && env != null) {
            String portStr = env.getProperty("server.port");
            if (portStr != null) {
                try {
                    dto.setServedBy(Integer.parseInt(portStr));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return dto;
    }

    //ดึงรายการสินค้าทั้งหมด
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product product : products) {
            dtos.add(enrichDTO(productMapper.toDTO(product)));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    //ดึงข้อมูลสินค้าตาม ID
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(enrichDTO(productMapper.toDTO(optionalProduct.get())), HttpStatus.OK);
    }

    //เพิ่มสินค้าใหม่
    @PostMapping("/products")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
        if (dto.getPlantId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Plant> optionalPlant = plantRepository.findById(dto.getPlantId());
        if (optionalPlant.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Product product = productMapper.toEntity(dto);
        product.setPlant(optionalPlant.get());
        Product savedProduct = productRepository.save(product);

        return new ResponseEntity<>(enrichDTO(productMapper.toDTO(savedProduct)), HttpStatus.CREATED);
    }

    //แก้ไขข้อมูลสินค้าทั้งหมด
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Product existingProduct = optionalProduct.get();
        if (dto.getPlantId() != null) {
            Optional<Plant> optionalPlant = plantRepository.findById(dto.getPlantId());
            if (optionalPlant.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            existingProduct.setPlant(optionalPlant.get());
        }

        existingProduct.setPrice(dto.getPrice());
        existingProduct.setStock(dto.getStock());

        Product updatedProduct = productRepository.save(existingProduct);
        return new ResponseEntity<>(enrichDTO(productMapper.toDTO(updatedProduct)), HttpStatus.OK);
    }

    //แก้ไขข้อมูลสินค้าบางส่วน
    @PatchMapping("/products/{id}")
    public ResponseEntity<ProductDTO> patchProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Product existingProduct = optionalProduct.get();
        productMapper.updateEntityFromDto(dto, existingProduct);

        if (dto.getPlantId() != null) {
            Optional<Plant> optionalPlant = plantRepository.findById(dto.getPlantId());
            if (optionalPlant.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            existingProduct.setPlant(optionalPlant.get());
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return new ResponseEntity<>(enrichDTO(productMapper.toDTO(updatedProduct)), HttpStatus.OK);
    }

    //ลบสินค้า
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        productRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //ปรับเพิ่ม/ลดสต็อกสินค้า (รับ Body {"delta": -3}) (สำหรับ inventory-service)
    @PatchMapping("/products/{id}/stock")
    public ResponseEntity<ProductDTO> adjustStock(@PathVariable Long id, @RequestBody StockAdjustmentDTO adjustmentDTO) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Product product = optionalProduct.get();
        int currentStock = product.getStock() != null ? product.getStock() : 0;
        int delta = (adjustmentDTO != null && adjustmentDTO.getDelta() != null) ? adjustmentDTO.getDelta() : 0;

        int newStock = currentStock + delta;
        if (newStock < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        product.setStock(newStock);
        Product savedProduct = productRepository.save(product);
        return new ResponseEntity<>(enrichDTO(productMapper.toDTO(savedProduct)), HttpStatus.OK);
    }
}
