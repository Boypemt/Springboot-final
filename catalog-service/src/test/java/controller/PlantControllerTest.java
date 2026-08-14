package controller;

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
public class PlantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllPlants() throws Exception {
        mockMvc.perform(get("/api/plants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].servedBy", notNullValue()));
    }

    @Test
    public void testGetPlantsByClassFilter() throws Exception {
        mockMvc.perform(get("/api/plants?classId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    public void testGetPlantsByEnvironmentFilter() throws Exception {
        mockMvc.perform(get("/api/plants?environmentId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    public void testGetPlantById() throws Exception {
        mockMvc.perform(get("/api/plants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Peashooter")));
    }

    @Test
    public void testCreatePlant() throws Exception {
        String jsonRequest = "{\"name\":\"Squash\",\"classId\":3,\"environmentId\":1,\"description\":\"Slashes zombies\",\"hp\":300,\"dmg\":1800,\"sunCost\":50,\"actionSpeed\":\"Slow\"}";

        mockMvc.perform(post("/api/plants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Squash")))
                .andExpect(jsonPath("$.sunCost", is(50)));
    }

    @Test
    public void testUpdatePlant() throws Exception {
        String jsonRequest = "{\"name\":\"Super Peashooter\",\"description\":\"Upgraded pea shooter\",\"hp\":400,\"dmg\":30,\"sunCost\":125,\"actionSpeed\":\"Fast\",\"classId\":1,\"environmentId\":1}";

        mockMvc.perform(put("/api/plants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Super Peashooter")))
                .andExpect(jsonPath("$.hp", is(400)));
    }

    @Test
    public void testPatchPlantDoesNotWipeUnsentFields() throws Exception {
        String patchJson = "{\"description\":\"New description for Peashooter\"}";

        mockMvc.perform(patch("/api/plants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("New description for Peashooter")))
                // พิสูจน์ว่า name, hp, dmg เดิมยังอยู่ ไม่ถูกลบเป็น null
                .andExpect(jsonPath("$.name", is("Peashooter")))
                .andExpect(jsonPath("$.hp", is(300)));
    }

    @Test
    public void testDeletePlant() throws Exception {
        mockMvc.perform(delete("/api/plants/8"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/plants/8"))
                .andExpect(status().isNotFound());
    }
}
