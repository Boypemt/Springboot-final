package th.mfu.pvz.customer.repository;

import org.springframework.data.repository.CrudRepository;
import th.mfu.pvz.customer.domain.Customer;
import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    List<Customer> findAll();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
}
