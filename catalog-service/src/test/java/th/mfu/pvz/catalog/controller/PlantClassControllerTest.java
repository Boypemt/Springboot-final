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
public class PlantClassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllClasses() throws Exception {
        mockMvc.perform(get("/api/classes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[0].classname", notNullValue()));
    }

    @Test
    public void testGetClassByIdSuccess() throws Exception {
        mockMvc.perform(get("/api/classes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.classname", is("Attack")));
    }

    @Test
    public void testGetClassByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/classes/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateClass() throws Exception {
        String jsonRequest = "{\"classname\":\"Special\",\"description\":\"Special plant class\"}";

        mockMvc.perform(post("/api/classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.classname", is("Special")))
                .andExpect(jsonPath("$.description", is("Special plant class")));
    }

    @Test
    public void testUpdateClassSuccess() throws Exception {
        String jsonRequest = "{\"classname\":\"Super Attack\",\"description\":\"Upgraded attack class\"}";

        mockMvc.perform(put("/api/classes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.classname", is("Super Attack")))
                .andExpect(jsonPath("$.description", is("Upgraded attack class")));
    }

    @Test
    public void testUpdateClassNotFound() throws Exception {
        String jsonRequest = "{\"classname\":\"NonExistent\",\"description\":\"Description\"}";

        mockMvc.perform(put("/api/classes/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPatchClassSuccess() throws Exception {
        String patchJson = "{\"description\":\"New patched description\"}";

        mockMvc.perform(patch("/api/classes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("New patched description")))
                .andExpect(jsonPath("$.classname", is("Attack")));
    }

    @Test
    public void testPatchClassNotFound() throws Exception {
        String patchJson = "{\"description\":\"New description\"}";

        mockMvc.perform(patch("/api/classes/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteClassSuccess() throws Exception {
        String jsonRequest = "{\"classname\":\"TempClass\",\"description\":\"To be deleted\"}";
        String response = mockMvc.perform(post("/api/classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer createdId = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/api/classes/" + createdId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/classes/" + createdId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteClassNotFound() throws Exception {
        mockMvc.perform(delete("/api/classes/9999"))
                .andExpect(status().isNotFound());
    }
}
