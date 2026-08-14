package th.mfu.pvz.customer.repository;

import org.springframework.data.repository.CrudRepository;
import th.mfu.pvz.customer.domain.Address;
import java.util.List;

public interface AddressRepository extends CrudRepository<Address, Long> {
    List<Address> findAll();
    List<Address> findByCustomerId(Long customerId);
}
