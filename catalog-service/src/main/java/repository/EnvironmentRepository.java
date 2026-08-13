package repository;

import org.springframework.data.repository.CrudRepository;

import domain.Environment;

public interface EnvironmentRepository extends CrudRepository<Environment, Long>{

}
