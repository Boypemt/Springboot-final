package th.mfu.pvz.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * What customer-service sends back. We only declare the fields we use.
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) means customer-service can add a
 * field tomorrow without breaking us - a small piece of loose coupling that
 * matters once two people own two services.
 *
 * Contract: TASKS.md section 2. Owned by Member 3.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerDTO {

    private Long id;
    private String username;
    private String phone;
    private String email;
    private String defaultAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(String defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
