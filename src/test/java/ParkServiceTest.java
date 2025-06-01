import app.windfarm.dtos.WindTurbineOutputDto;
import app.windfarm.entities.WindTurbine;
import app.windfarm.repository.WindTurbineRepository;
import app.windfarm.service.ParkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Unit tests for the service layer. */
public class ParkServiceTest {

  private ParkService parkService;

  @BeforeEach
  void setUp() {
    WindTurbineRepository windTurbineRepository = Mockito.mock(WindTurbineRepository.class);
    List<WindTurbine> windTurbines = getWindTurbines();
    Mockito.when(windTurbineRepository.findAll()).thenReturn(windTurbines);
    parkService = new ParkService(windTurbineRepository);
  }

  @Test
  void settingMarketPriceWithValidInputUpdatesValueCorrectly() {
    parkService.setMarketPrice(1);
    assertThat(parkService.getMarketPrice()).isEqualTo(1);
  }

  @Test
  void settingMarketPriceWithInvalidInputThrowsException() {
    assertThatThrownBy(() -> parkService.setMarketPrice(-1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Market price must be non-negative.");
  }

  @Test
  void updateProductionTargetWithValidDeltaUpdatesValueCorrectly() {
    parkService.updateProductionTarget(3);
    assertThat(parkService.getProductionTarget()).isEqualTo(3);

    parkService.updateProductionTarget(-1);
    assertThat(parkService.getProductionTarget()).isEqualTo(2);
  }

  @Test
  void updateProductionTargetWithInvalidDeltaThrowsException() {
    assertThatThrownBy(() -> parkService.updateProductionTarget(25))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Production target must be in range");
  }

  @Test
  void computesCorrectProductionPlan() {
    parkService.setMarketPrice(6);
    parkService.updateProductionTarget(10);
    List<WindTurbineOutputDto> outputDtos = parkService.computeProductionPlan();

    assertThat(outputDtos).hasSize(5);
    assertThat(outputDtos.get(0).identifier()).isEqualTo("A");
    assertThat(outputDtos.get(0).expectedProduction()).isEqualTo(0);
  }


  private List<WindTurbine> getWindTurbines() {
    return List.of(
        new WindTurbine("A", 2, 15),
        new WindTurbine("B", 2, 5),
        new WindTurbine("C", 6, 5),
        new WindTurbine("D", 6, 5),
        new WindTurbine("E", 5, 3)
    );
  }
}
