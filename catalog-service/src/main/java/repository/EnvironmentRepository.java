package repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import domain.Environment;

public interface EnvironmentRepository extends CrudRepository<Environment, Long> {
    List<Environment> findAll();
}
