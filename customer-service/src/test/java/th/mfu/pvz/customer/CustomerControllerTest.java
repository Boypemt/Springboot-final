package th.mfu.pvz.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import th.mfu.pvz.customer.repository.CustomerRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"eureka.client.enabled=false"})
@AutoConfigureMockMvc
class CustomerControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private CustomerRepository customerRepository;

    @Test
    void createListPatchAndDeleteCustomer() throws Exception {
        String body = "{\"username\":\"testdave\",\"password\":\"secret\",\"phone\":\"0812345678\",\"email\":\"testdave@pvz.com\"}";
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.username").value("testdave"))
                .andExpect(jsonPath("$.password").doesNotExist());
        Long id = customerRepository.findAll().stream()
                .filter(customer -> "testdave".equals(customer.getUsername())).findFirst().get().getId();
        mockMvc.perform(get("/api/customers")).andExpect(status().isOk()).andExpect(jsonPath("$[?(@.username == 'testdave')].email").value("testdave@pvz.com"));
        mockMvc.perform(patch("/api/customers/{id}", id).contentType(MediaType.APPLICATION_JSON).content("{\"phone\":\"0899999999\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.phone").value("0899999999"));
        mockMvc.perform(delete("/api/customers/{id}", id)).andExpect(status().isNoContent());
    }

    @Test
    void duplicateUsernameReturnsConflict() throws Exception {
        String first = "{\"username\":\"sunny\",\"password\":\"one\",\"email\":\"sunny1@pvz.com\"}";
        String duplicate = "{\"username\":\"sunny\",\"password\":\"two\",\"email\":\"sunny2@pvz.com\"}";
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(first)).andExpect(status().isCreated());
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(duplicate)).andExpect(status().isConflict());
    }
}
