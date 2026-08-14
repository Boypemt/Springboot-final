package th.mfu.pvz.customer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.mfu.pvz.customer.domain.Address;
import th.mfu.pvz.customer.domain.Customer;
import th.mfu.pvz.customer.dto.AddressDTO;
import th.mfu.pvz.customer.dto.CustomerDTO;
import th.mfu.pvz.customer.dto.CustomerRequestDTO;
import th.mfu.pvz.customer.dto.mapper.AddressMapper;
import th.mfu.pvz.customer.dto.mapper.CustomerMapper;
import th.mfu.pvz.customer.repository.AddressRepository;
import th.mfu.pvz.customer.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CustomerController {
    @Autowired private CustomerRepository customerRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private CustomerMapper customerMapper;
    @Autowired private AddressMapper addressMapper;

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> getCustomers() {
        List<CustomerDTO> customers = customerRepository.findAll().stream()
                .map(this::customerDto).collect(Collectors.toList());
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(customerDto(customer), HttpStatus.OK);
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerRequestDTO request) {
        if (customerRepository.existsByUsername(request.getUsername())
                || customerRepository.existsByEmail(request.getEmail())) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        Customer customer = customerRepository.save(customerMapper.toEntity(request));
        return new ResponseEntity<>(customerDto(customer), HttpStatus.CREATED);
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> replaceCustomer(@PathVariable Long id, @RequestBody CustomerRequestDTO request) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        if (isDuplicate(request, id)) return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        customerMapper.replace(request, customer);
        return new ResponseEntity<>(customerDto(customerRepository.save(customer)), HttpStatus.OK);
    }

    @PatchMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> patchCustomer(@PathVariable Long id, @RequestBody CustomerRequestDTO request) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        if (isDuplicate(request, id)) return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        customerMapper.patch(request, customer);
        return new ResponseEntity<>(customerDto(customerRepository.save(customer)), HttpStatus.OK);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (!customerRepository.existsById(id)) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        customerRepository.deleteById(id);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/customers/{id}/addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses(@PathVariable Long id) {
        if (!customerRepository.existsById(id)) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        List<AddressDTO> addresses = addressRepository.findByCustomerId(id).stream()
                .map(addressMapper::toDto).collect(Collectors.toList());
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @PostMapping("/customers/{id}/addresses")
    public ResponseEntity<AddressDTO> createAddress(@PathVariable Long id, @RequestBody AddressDTO request) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        if (Boolean.TRUE.equals(request.getIsDefault())) clearDefaultAddress(customer);
        Address address = addressMapper.toEntity(request);
        address.setId(null);
        address.setCustomer(customer);
        Address saved = addressRepository.save(address);
        return new ResponseEntity<>(addressMapper.toDto(saved), HttpStatus.CREATED);
    }

    @PatchMapping("/addresses/{id}")
    public ResponseEntity<AddressDTO> patchAddress(@PathVariable Long id, @RequestBody AddressDTO request) {
        Address address = addressRepository.findById(id).orElse(null);
        if (address == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        if (Boolean.TRUE.equals(request.getIsDefault())) clearDefaultAddress(address.getCustomer());
        addressMapper.patch(request, address);
        return new ResponseEntity<>(addressMapper.toDto(addressRepository.save(address)), HttpStatus.OK);
    }

    private boolean isDuplicate(CustomerRequestDTO request, Long id) {
        return (request.getUsername() != null && customerRepository.existsByUsernameAndIdNot(request.getUsername(), id))
                || (request.getEmail() != null && customerRepository.existsByEmailAndIdNot(request.getEmail(), id));
    }

    private CustomerDTO customerDto(Customer customer) {
        CustomerDTO dto = customerMapper.toDto(customer);
        customer.getAddresses().stream().filter(address -> Boolean.TRUE.equals(address.getIsDefault())).findFirst()
                .ifPresent(address -> dto.setDefaultAddress(formatAddress(address)));
        return dto;
    }

    private String formatAddress(Address address) {
        List<String> parts = new ArrayList<>();
        addIfPresent(parts, address.getSubDistrict());
        addIfPresent(parts, address.getDistrict());
        addIfPresent(parts, address.getCity());
        addIfPresent(parts, address.getCountry());
        if (address.getZipcode() != null && !address.getZipcode().trim().isEmpty()) {
            if (parts.isEmpty()) parts.add(address.getZipcode());
            else parts.set(parts.size() - 1, parts.get(parts.size() - 1) + " " + address.getZipcode());
        }
        return String.join(", ", parts);
    }

    private void addIfPresent(List<String> parts, String value) {
        if (value != null && !value.trim().isEmpty()) parts.add(value);
    }

    private void clearDefaultAddress(Customer customer) {
        for (Address address : customer.getAddresses()) {
            address.setIsDefault(false);
            addressRepository.save(address);
        }
    }
}
