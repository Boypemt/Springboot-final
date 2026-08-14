package th.mfu.pvz.catalog.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import th.mfu.pvz.catalog.domain.Environment;

public interface EnvironmentRepository extends CrudRepository<Environment, Long> {
    List<Environment> findAll();
}
