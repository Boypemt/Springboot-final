package th.mfu.pvz.catalog.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import th.mfu.pvz.catalog.domain.PlantClass;

public interface PlantClassRepository extends CrudRepository<PlantClass, Long> {
    List<PlantClass> findAll();
}
