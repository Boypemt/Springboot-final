package controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import domain.Environment;
import domain.Plant;
import domain.PlantClass;
import repository.PlantRepository;
import th.mfu.pvz.catalog.CatalogServiceApplication;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = CatalogServiceApplication.class)
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlantRepository plantRepository;

    @Test
    public void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].servedBy", notNullValue()));
    }

    @Test
    public void testGetProductById() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.plantName", is("Peashooter")))
                .andExpect(jsonPath("$.servedBy", notNullValue()));
    }

    @Test
    public void testCreateProduct() throws Exception {
        Plant newPlant = new Plant();
        newPlant.setName("New Test Plant");
        newPlant.setHp(100);
        newPlant.setDmg(10);
        newPlant.setSunCost(50);
        newPlant.setActionSpeed("Normal");
        PlantClass pc = new PlantClass();
        pc.setId(1L);
        newPlant.setPlantClass(pc);
        Environment env = new Environment();
        env.setId(1L);
        newPlant.setEnvironment(env);
        Plant savedPlant = plantRepository.save(newPlant);

        String jsonRequest = "{\"plantId\":" + savedPlant.getId() + ",\"price\":50.00,\"stock\":30}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price", is(50.00)))
                .andExpect(jsonPath("$.stock", is(30)));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        String jsonRequest = "{\"plantId\":1,\"price\":99.00,\"stock\":50}";

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price", is(99.00)))
                .andExpect(jsonPath("$.stock", is(50)));
    }

    @Test
    public void testPatchProductDoesNotWipeUnsentFields() throws Exception {
        // ส่งมาเฉพาะ stock = 100 (ไม่ส่ง price หรือ plantId)
        String patchJson = "{\"stock\":100}";

        mockMvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock", is(100)))
                // พิสูจน์ว่า price และ plantName เดิมยังอยู่ ไม่ถูกล้างเป็น null
                .andExpect(jsonPath("$.price", is(100.00)))
                .andExpect(jsonPath("$.plantName", is("Peashooter")));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/8"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/8"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAdjustStockValid() throws Exception {
        String jsonRequest = "{\"delta\":-5}";

        mockMvc.perform(patch("/api/products/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock", is(20))); // เดิมมี 25 ลบออก 5 เหลือ 20
    }

    @Test
    public void testAdjustStockInvalidNegativeResult() throws Exception {
        String jsonRequest = "{\"delta\":-999}";

        mockMvc.perform(patch("/api/products/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}
