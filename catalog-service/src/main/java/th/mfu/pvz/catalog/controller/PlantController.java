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
import th.mfu.pvz.catalog.domain.PlantClass;
import th.mfu.pvz.catalog.dto.PlantDTO;
import th.mfu.pvz.catalog.dto.mapper.PlantMapper;
import th.mfu.pvz.catalog.repository.EnvironmentRepository;
import th.mfu.pvz.catalog.repository.PlantClassRepository;
import th.mfu.pvz.catalog.repository.PlantRepository;

@RestController
@RequestMapping("/api")
public class PlantController {

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private PlantClassRepository plantClassRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private PlantMapper plantMapper;

    @Autowired
    private Environment env;

    private PlantDTO enrichDTO(PlantDTO dto) {
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

    //ดึงรายการพืชทั้งหมด และรองรับ Query Parameter /api/plants?classId=1 หรือ ?environmentId=1 หรือกรองพร้อมกัน
    @GetMapping("/plants")
    public ResponseEntity<List<PlantDTO>> getAllPlants(
            @RequestParam(value = "classId", required = false) Long classId,
            @RequestParam(value = "environmentId", required = false) Long environmentId) {
        List<Plant> plants;
        if (classId != null && environmentId != null) {
            plants = plantRepository.findByPlantClassIdAndEnvironmentId(classId, environmentId);
        } else if (classId != null) {
            plants = plantRepository.findByPlantClassId(classId);
        } else if (environmentId != null) {
            plants = plantRepository.findByEnvironmentId(environmentId);
        } else {
            plants = plantRepository.findAll();
        }

        List<PlantDTO> dtos = new ArrayList<>();
        for (Plant plant : plants) {
            dtos.add(enrichDTO(plantMapper.toDTO(plant)));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    //ดึงข้อมูลพืชตาม ID
    @GetMapping("/plants/{id}")
    public ResponseEntity<PlantDTO> getPlantById(@PathVariable Long id) {
        Optional<Plant> optionalPlant = plantRepository.findById(id);
        if (optionalPlant.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(enrichDTO(plantMapper.toDTO(optionalPlant.get())), HttpStatus.OK);
    }

    //เพิ่มข้อมูลพืชใหม่
    @PostMapping("/plants")
    public ResponseEntity<PlantDTO> createPlant(@RequestBody PlantDTO dto) {
        Plant plant = plantMapper.toEntity(dto);

        if (dto.getClassId() != null) {
            Optional<PlantClass> optionalClass = plantClassRepository.findById(dto.getClassId());
            if (optionalClass.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            plant.setPlantClass(optionalClass.get());
        }

        if (dto.getEnvironmentId() != null) {
            Optional<th.mfu.pvz.catalog.domain.Environment> optionalEnv = environmentRepository.findById(dto.getEnvironmentId());
            if (optionalEnv.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            plant.setEnvironment(optionalEnv.get());
        }

        Plant savedPlant = plantRepository.save(plant);
        return new ResponseEntity<>(enrichDTO(plantMapper.toDTO(savedPlant)), HttpStatus.CREATED);
    }

    //แก้ไขข้อมูลพืชทั้งหมด
    @PutMapping("/plants/{id}")
    public ResponseEntity<PlantDTO> updatePlant(@PathVariable Long id, @RequestBody PlantDTO dto) {
        Optional<Plant> optionalPlant = plantRepository.findById(id);
        if (optionalPlant.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Plant existingPlant = optionalPlant.get();
        existingPlant.setName(dto.getName());
        existingPlant.setDescription(dto.getDescription());
        existingPlant.setHp(dto.getHp());
        existingPlant.setDmg(dto.getDmg());
        existingPlant.setSunCost(dto.getSunCost());
        existingPlant.setActionSpeed(dto.getActionSpeed());

        if (dto.getClassId() != null) {
            Optional<PlantClass> optionalClass = plantClassRepository.findById(dto.getClassId());
            if (optionalClass.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            existingPlant.setPlantClass(optionalClass.get());
        }

        if (dto.getEnvironmentId() != null) {
            Optional<th.mfu.pvz.catalog.domain.Environment> optionalEnv = environmentRepository.findById(dto.getEnvironmentId());
            if (optionalEnv.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            existingPlant.setEnvironment(optionalEnv.get());
        }

        Plant updatedPlant = plantRepository.save(existingPlant);
        return new ResponseEntity<>(enrichDTO(plantMapper.toDTO(updatedPlant)), HttpStatus.OK);
    }

    //แก้ไขข้อมูลพืชบางส่วน
    @PatchMapping("/plants/{id}")
    public ResponseEntity<PlantDTO> patchPlant(@PathVariable Long id, @RequestBody PlantDTO dto) {
        Optional<Plant> optionalPlant = plantRepository.findById(id);
        if (optionalPlant.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Plant existingPlant = optionalPlant.get();
        plantMapper.updateEntityFromDto(dto, existingPlant);

        if (dto.getClassId() != null) {
            Optional<PlantClass> optionalClass = plantClassRepository.findById(dto.getClassId());
            if (optionalClass.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            existingPlant.setPlantClass(optionalClass.get());
        }

        if (dto.getEnvironmentId() != null) {
            Optional<th.mfu.pvz.catalog.domain.Environment> optionalEnv = environmentRepository.findById(dto.getEnvironmentId());
            if (optionalEnv.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            existingPlant.setEnvironment(optionalEnv.get());
        }

        Plant updatedPlant = plantRepository.save(existingPlant);
        return new ResponseEntity<>(enrichDTO(plantMapper.toDTO(updatedPlant)), HttpStatus.OK);
    }

    //ลบข้อมูลพืช
    @DeleteMapping("/plants/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable Long id) {
        if (!plantRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        plantRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ดึงข้อมูลพืชตาม Class ID
    @GetMapping("/plants/class/{classId}")
    public ResponseEntity<List<PlantDTO>> getPlantsByClassId(@PathVariable Long classId) {
        List<Plant> plants = plantRepository.findByPlantClassId(classId);
        List<PlantDTO> dtos = new ArrayList<>();
        for (Plant plant : plants) {
            dtos.add(enrichDTO(plantMapper.toDTO(plant)));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // ดึงข้อมูลพืชตาม Environment ID
    @GetMapping("/plants/environment/{environmentId}")
    public ResponseEntity<List<PlantDTO>> getPlantsByEnvironmentId(@PathVariable Long environmentId) {
        List<Plant> plants = plantRepository.findByEnvironmentId(environmentId);
        List<PlantDTO> dtos = new ArrayList<>();
        for (Plant plant : plants) {
            dtos.add(enrichDTO(plantMapper.toDTO(plant)));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}
