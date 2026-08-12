package th.mfu.pvz.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import th.mfu.pvz.order.dto.CustomerDTO;

/**
 * How this service asks customer-service whether a customer exists.
 *
 * NOBODY IMPLEMENTS THIS INTERFACE. Feign reads the annotations at startup and
 * builds the class: it makes the URL, sends the GET, and turns the JSON into a
 * CustomerDTO.
 *
 * "customer-service" is a NAME - the other service's spring.application.name -
 * not an address. Eureka turns it into a real host and port at the moment of
 * the call. Write a URL here instead and the load balancer becomes impossible.
 */
@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/customers/{id}")
    CustomerDTO getCustomer(@PathVariable("id") Long id);
}
