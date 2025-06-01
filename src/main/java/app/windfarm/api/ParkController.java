package app.windfarm.api;

import app.windfarm.dtos.WindTurbineOutputDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.windfarm.service.ParkService;

import java.util.List;

/** REST API for handling HTTP requests. */
@RestController
@RequestMapping("/api")
public final class ParkController {

  private final ParkService parkService;
  private static final Logger logger = LoggerFactory.getLogger(ParkController.class);

  /**
   * Constructor for the park controller.
   *
   * @param parkService the park service to delegate service logic to
   */
  public ParkController(ParkService parkService) {
    this.parkService = parkService;
  }

  /**
   * Set the market price to the given value.
   *
   * @param marketPrice the market price
   * @return HTTP response
   */
  @PostMapping("/set-market-price")
  public ResponseEntity<Void> setMarketPrice(@RequestParam int marketPrice) {
    parkService.setMarketPrice(marketPrice);

    logger.info("POST /api/set-market-price - Successfully set market price to {}€", marketPrice);

    return ResponseEntity.ok().build();
  }

  /**
   * Update the production target by the given value.
   *
   * @param delta the delta increase/decrease in production target
   * @return HTTP response
   */
  @PostMapping("/update-production-target")
  public ResponseEntity<Void> updateProductionTarget(@RequestParam int delta) {
    parkService.updateProductionTarget(delta);

    int productionTarget = parkService.getProductionTarget();
    logger.info("POST /api/update-production-target - Successfully updated production target to {}MWh", productionTarget);

    return ResponseEntity.ok().build();
  }

  /**
   * Retrieve the production plan as a list of all the turbines with their respective expected production.
   *
   * @return the production plan along with an HTTP response
   */
  @GetMapping("/production-plan")
  public ResponseEntity<List<WindTurbineOutputDto>> getProductionPlan() {
    List<WindTurbineOutputDto> productionPlan = parkService.computeProductionPlan();

    // Prints the plan in the terminal (for easier output verification)
    String formattedProductionPlan = formatProductionPlan(productionPlan);
    logger.info("GET /api/production-plan - Successfully retrieved the production plan \n{}", formattedProductionPlan);

    return ResponseEntity.ok(productionPlan);
  }

  /**
   * Pretty-print the production plan.
   */
  private String formatProductionPlan(List<WindTurbineOutputDto> productionPlan) {
    int marketPrice = parkService.getMarketPrice();
    int productionTarget = parkService.getProductionTarget();
    int sumProduction = 0;

    StringBuilder sb = new StringBuilder();
    sb.append("---------------------------------\n");
    sb.append("| Turbine | Expected production |\n");
    sb.append("---------------------------------\n");

    for (WindTurbineOutputDto turbine : productionPlan) {
      sb.append(String.format("| %-7s | %-19d |\n", turbine.identifier(), turbine.expectedProduction()));
      sumProduction += turbine.expectedProduction();
    }

    sb.append("---------------------------------\n");
    sb.append(String.format(
        "Sum production: %dMWh. Target production: %dMWh. Price limit: %d€.",
        sumProduction, productionTarget, marketPrice
    ));

    return sb.toString();
  }
}
