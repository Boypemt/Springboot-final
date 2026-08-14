package repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import domain.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findAll();
}
