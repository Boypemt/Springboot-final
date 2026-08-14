package th.mfu.pvz.catalog.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import th.mfu.pvz.catalog.CatalogServiceApplication;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = CatalogServiceApplication.class)
@AutoConfigureMockMvc
@Transactional
public class EnvironmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllEnvironments() throws Exception {
        mockMvc.perform(get("/api/environments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[0].envname", notNullValue()));
    }

    @Test
    public void testGetEnvironmentByIdSuccess() throws Exception {
        mockMvc.perform(get("/api/environments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.envname", is("Day")));
    }

    @Test
    public void testGetEnvironmentByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/environments/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateEnvironment() throws Exception {
        String jsonRequest = "{\"envname\":\"Roof\",\"description\":\"Roof environment with slope\"}";

        mockMvc.perform(post("/api/environments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.envname", is("Roof")))
                .andExpect(jsonPath("$.description", is("Roof environment with slope")));
    }

    @Test
    public void testUpdateEnvironmentSuccess() throws Exception {
        String jsonRequest = "{\"envname\":\"Bright Day\",\"description\":\"Sunny daytime lawn\"}";

        mockMvc.perform(put("/api/environments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.envname", is("Bright Day")))
                .andExpect(jsonPath("$.description", is("Sunny daytime lawn")));
    }

    @Test
    public void testUpdateEnvironmentNotFound() throws Exception {
        String jsonRequest = "{\"envname\":\"NonExistent\",\"description\":\"Description\"}";

        mockMvc.perform(put("/api/environments/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPatchEnvironmentSuccess() throws Exception {
        String patchJson = "{\"description\":\"New patched environment description\"}";

        mockMvc.perform(patch("/api/environments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("New patched environment description")))
                .andExpect(jsonPath("$.envname", is("Day")));
    }

    @Test
    public void testPatchEnvironmentNotFound() throws Exception {
        String patchJson = "{\"description\":\"New description\"}";

        mockMvc.perform(patch("/api/environments/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteEnvironmentSuccess() throws Exception {
        String jsonRequest = "{\"envname\":\"TempEnv\",\"description\":\"To be deleted\"}";
        String response = mockMvc.perform(post("/api/environments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer createdId = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/api/environments/" + createdId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/environments/" + createdId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteEnvironmentNotFound() throws Exception {
        mockMvc.perform(delete("/api/environments/9999"))
                .andExpect(status().isNotFound());
    }
}
