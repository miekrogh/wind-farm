import app.windfarm.api.ParkController;
import app.windfarm.dtos.WindTurbineOutputDto;
import app.windfarm.service.ParkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Unit tests for the REST API. */
@WebMvcTest(ParkController.class)
@ContextConfiguration(classes = ParkControllerTest.Config.class)
public class ParkControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ParkService parkService;

  @Test
  void setMarketPriceWithValidInputReturnsOk() throws Exception {
    mockMvc.perform(post("/api/set-market-price").param("marketPrice", "6"))
        .andExpect(status().isOk());

    // verify that the request happened with the specified input
    verify(parkService).setMarketPrice(6);
  }

  @Test
  void updateProductionTargetWithValidInputReturnsOk() throws Exception {
    mockMvc.perform(post("/api/update-production-target").param("delta", "10"))
        .andExpect(status().isOk());

    verify(parkService).updateProductionTarget(10);
  }

  @Test
  void getProductionPlan() throws Exception {
    List<WindTurbineOutputDto> dummyPlan = List.of(
        new WindTurbineOutputDto("A", 2)
    );
    when(parkService.computeProductionPlan()).thenReturn(dummyPlan);

    mockMvc.perform(get("/api/production-plan"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$[0].identifier").value("A"))
        .andExpect(jsonPath("$[0].expectedProduction").value(2));

    verify(parkService).computeProductionPlan();
  }

  /** Test config instead of using application. */
  @Configuration
  @ComponentScan(
      basePackages = "app.windfarm.api",
      includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ParkController.class)
  )
  static class Config {}
}
