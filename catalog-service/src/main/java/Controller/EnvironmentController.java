package Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import domain.Environment;
import dto.EnvironmentDTO;
import dto.mapper.EnvironmentMapper;
import repository.EnvironmentRepository;

@RestController
@RequestMapping("/api")
public class EnvironmentController {

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private EnvironmentMapper environmentMapper;

    //ดึงรายการสภาพแวดล้อมทั้งหมด
    @GetMapping("/environments")
    public ResponseEntity<List<EnvironmentDTO>> getAllEnvironments() {
        List<Environment> environments = environmentRepository.findAll();
        List<EnvironmentDTO> dtos = new ArrayList<>();
        for (Environment environment : environments) {
            dtos.add(environmentMapper.toDTO(environment));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    //ดึงข้อมูลสภาพแวดล้อมตาม ID
    @GetMapping("/environments/{id}")
    public ResponseEntity<EnvironmentDTO> getEnvironmentById(@PathVariable Long id) {
        Optional<Environment> optionalEnvironment = environmentRepository.findById(id);
        if (optionalEnvironment.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(environmentMapper.toDTO(optionalEnvironment.get()), HttpStatus.OK);
    }

    //เพิ่มสภาพแวดล้อมใหม่
    @PostMapping("/environments")
    public ResponseEntity<EnvironmentDTO> createEnvironment(@RequestBody EnvironmentDTO dto) {
        Environment environment = environmentMapper.toEntity(dto);
        Environment savedEnvironment = environmentRepository.save(environment);
        return new ResponseEntity<>(environmentMapper.toDTO(savedEnvironment), HttpStatus.CREATED);
    }

    //แก้ไขข้อมูลสภาพแวดล้อมทั้งหมด
    @PutMapping("/environments/{id}")
    public ResponseEntity<EnvironmentDTO> updateEnvironment(@PathVariable Long id, @RequestBody EnvironmentDTO dto) {
        Optional<Environment> optionalEnvironment = environmentRepository.findById(id);
        if (optionalEnvironment.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Environment existingEnv = optionalEnvironment.get();
        existingEnv.setEnvname(dto.getEnvname());
        existingEnv.setDescription(dto.getDescription());

        Environment updatedEnv = environmentRepository.save(existingEnv);
        return new ResponseEntity<>(environmentMapper.toDTO(updatedEnv), HttpStatus.OK);
    }

    //แก้ไขข้อมูลสภาพแวดล้อมบางส่วน
    @PatchMapping("/environments/{id}")
    public ResponseEntity<EnvironmentDTO> patchEnvironment(@PathVariable Long id, @RequestBody EnvironmentDTO dto) {
        Optional<Environment> optionalEnvironment = environmentRepository.findById(id);
        if (optionalEnvironment.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Environment existingEnv = optionalEnvironment.get();
        environmentMapper.updateEntityFromDto(dto, existingEnv);

        Environment updatedEnv = environmentRepository.save(existingEnv);
        return new ResponseEntity<>(environmentMapper.toDTO(updatedEnv), HttpStatus.OK);
    }

    //ลบข้อมูลสภาพแวดล้อม
    @DeleteMapping("/environments/{id}")
    public ResponseEntity<Void> deleteEnvironment(@PathVariable Long id) {
        if (!environmentRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        environmentRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
