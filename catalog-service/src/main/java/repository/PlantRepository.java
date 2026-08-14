package repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import domain.Plant;

public interface PlantRepository extends CrudRepository<Plant, Long> {
    List<Plant> findAll();
    List<Plant> findByPlantClassId(Long classId);
    List<Plant> findByEnvironmentId(Long environmentId);
    List<Plant> findByPlantClassIdAndEnvironmentId(Long classId, Long environmentId);
}
