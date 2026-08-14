package th.mfu.pvz.catalog.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import th.mfu.pvz.catalog.domain.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findAll();
}
