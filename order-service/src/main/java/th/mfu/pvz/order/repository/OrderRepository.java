package th.mfu.pvz.order.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import th.mfu.pvz.order.domain.Order;

/**
 * Spring Data writes the implementation from the method names. No SQL, no
 * EntityManager.
 */
public interface OrderRepository extends CrudRepository<Order, Long> {

    /** Narrows CrudRepository's Iterable to a List. */
    List<Order> findAll();

    /** Derived query: SELECT ... WHERE customer_id = ? */
    List<Order> findByCustomerId(Long customerId);

    List<Order> findByStatus(String status);
}
