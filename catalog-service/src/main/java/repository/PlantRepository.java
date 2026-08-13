package repository;

import org.springframework.data.repository.CrudRepository;

import domain.Plant;

public interface PlantRepository extends CrudRepository<Plant, Long> {

}
