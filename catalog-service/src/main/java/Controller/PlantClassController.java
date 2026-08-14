package Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import domain.PlantClass;
import dto.PlantClassDTO;
import dto.mapper.PlantClassMapper;
import repository.PlantClassRepository;

@RestController
@RequestMapping("/api")
public class PlantClassController {

    @Autowired
    private PlantClassRepository plantClassRepository;

    @Autowired
    private PlantClassMapper plantClassMapper;

    //ดึงรายการคลาสพืชทั้งหมด
    @GetMapping("/classes")
    public ResponseEntity<List<PlantClassDTO>> getAllClasses() {
        List<PlantClass> plantClasses = plantClassRepository.findAll();
        List<PlantClassDTO> dtos = new ArrayList<>();
        for (PlantClass plantClass : plantClasses) {
            dtos.add(plantClassMapper.toDTO(plantClass));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    //ดึงข้อมูลคลาสพืชตาม ID
    @GetMapping("/classes/{id}")
    public ResponseEntity<PlantClassDTO> getClassById(@PathVariable Long id) {
        Optional<PlantClass> optionalClass = plantClassRepository.findById(id);
        if (optionalClass.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(plantClassMapper.toDTO(optionalClass.get()), HttpStatus.OK);
    }

    //เพิ่มคลาสพืชใหม่
    @PostMapping("/classes")
    public ResponseEntity<PlantClassDTO> createClass(@RequestBody PlantClassDTO dto) {
        PlantClass plantClass = plantClassMapper.toEntity(dto);
        PlantClass savedClass = plantClassRepository.save(plantClass);
        return new ResponseEntity<>(plantClassMapper.toDTO(savedClass), HttpStatus.CREATED);
    }

    //แก้ไขข้อมูลคลาสพืชทั้งหมด
    @PutMapping("/classes/{id}")
    public ResponseEntity<PlantClassDTO> updateClass(@PathVariable Long id, @RequestBody PlantClassDTO dto) {
        Optional<PlantClass> optionalClass = plantClassRepository.findById(id);
        if (optionalClass.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PlantClass existingClass = optionalClass.get();
        existingClass.setClassname(dto.getClassname());
        existingClass.setDescription(dto.getDescription());

        PlantClass updatedClass = plantClassRepository.save(existingClass);
        return new ResponseEntity<>(plantClassMapper.toDTO(updatedClass), HttpStatus.OK);
    }

    //แก้ไขข้อมูลคลาสพืชบางส่วน
    @PatchMapping("/classes/{id}")
    public ResponseEntity<PlantClassDTO> patchClass(@PathVariable Long id, @RequestBody PlantClassDTO dto) {
        Optional<PlantClass> optionalClass = plantClassRepository.findById(id);
        if (optionalClass.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PlantClass existingClass = optionalClass.get();
        plantClassMapper.updateEntityFromDto(dto, existingClass);

        PlantClass updatedClass = plantClassRepository.save(existingClass);
        return new ResponseEntity<>(plantClassMapper.toDTO(updatedClass), HttpStatus.OK);
    }

    //ลบข้อมูลคลาสพืช
    @DeleteMapping("/classes/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        if (!plantClassRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        plantClassRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
