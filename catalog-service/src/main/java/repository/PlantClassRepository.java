package repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import domain.PlantClass;

public interface PlantClassRepository extends CrudRepository<PlantClass, Long> {
    List<PlantClass> findAll();
}
