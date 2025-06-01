import app.windfarm.ParkApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Integration test of the full application. */
@SpringBootTest(classes = ParkApplication.class)
@AutoConfigureMockMvc
public class IntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void getProductionPlanReturnsOkAndJson() throws Exception {
    mockMvc.perform(post("/api/set-market-price").param("marketPrice", "6"))
        .andExpect(status().isOk());

    mockMvc.perform(post("/api/update-production-target").param("delta", "10"))
        .andExpect(status().isOk());

    ResultActions resultActions = mockMvc.perform(get("/api/production-plan"))
        .andExpect(status().isOk());
    verifyProductionPlan(resultActions);
  }

  private static void verifyProductionPlan(ResultActions resultActions) throws Exception {
    String[] expectedIds = {"A", "B", "C", "D", "E"};
    int[] expectedProductions = {0, 2, 0, 0, 5};

    for (int i = 0; i < expectedIds.length; i++) {
      resultActions
          .andExpect(jsonPath(String.format("$[%d].identifier", i)).value(expectedIds[i]))
          .andExpect(jsonPath(String.format("$[%d].expectedProduction", i)).value(expectedProductions[i]));
    }
  }
}
