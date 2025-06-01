package app.windfarm.service;

import app.windfarm.dtos.WindTurbineOutputDto;
import app.windfarm.entities.WindTurbine;
import app.windfarm.repository.WindTurbineRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/** Service layer for managing wind turbines and park operations. */
@Service
public class ParkService {

  private final WindTurbineRepository windTurbineRepository;
  private int marketPrice;
  private int productionTarget;

  /**
   * Constructor for the park service.
   *
   * @param windTurbineRepository the wind turbine repository
   */
  public ParkService(WindTurbineRepository windTurbineRepository) {
    this.windTurbineRepository = windTurbineRepository;
    this.marketPrice = 0;
    this.productionTarget = 0;
  }

  /**
   * Get the market price.
   *
   * @return the market price
   */
  public int getMarketPrice() {
    return marketPrice;
  }

  /**
   * Set the market price to the given value.
   *
   * @param marketPrice the market price
   * @throws IllegalArgumentException if given price is negative
   */
  public void setMarketPrice(int marketPrice) {
    if (marketPrice < 0) {
      throw new IllegalArgumentException("Market price must be non-negative.");
    }

    this.marketPrice = marketPrice;
  }


  /**
   * Get the production target.
   *
   * @return the production target
   */
  public int getProductionTarget() {
    return productionTarget;
  }

  /**
   * Set the production target to the given value.
   *
   * @param productionTarget the production target
   * @throws IllegalArgumentException if the production target is negative or exceeds max capacity
   */
  public void setProductionTarget(int productionTarget) {
    int maximumCapacity = computeMaximumCapacity();

    boolean targetTooLow = productionTarget < 0;
    boolean targetTooHigh = productionTarget > maximumCapacity;

    if (targetTooLow || targetTooHigh) {
      String errorMessage = String.format("Production target must be in range [0, %d] but is %d.", maximumCapacity, productionTarget);
      throw new IllegalArgumentException(errorMessage);
    }

    this.productionTarget = productionTarget;
  }

  /**
   * Update the production target by the given value.
   *
   * @param delta the delta increase/decrease in production target
   * @throws IllegalArgumentException if the given delta causes the production target to become negative or exceed max capacity
   */
  public void updateProductionTarget(int delta) {
    setProductionTarget(productionTarget + delta);
  }

  /**
   * Compute the maximum capacity, i.e., total power (MW) the park's turbines can produce per hour.
   *
   * @return the maximum capacity
   */
  private int computeMaximumCapacity() {
    return windTurbineRepository.findAll().stream().mapToInt(WindTurbine::getCapacity).sum();
  }

  /**
   * Compute the production plan based on the market price and production target.
   *
   * @return list of {@link WindTurbineOutputDto}
   */
  public List<WindTurbineOutputDto> computeProductionPlan() {
    List<WindTurbine> allTurbines = getAllWindTurbines();
    List<WindTurbine> sortedProfitableTurbines = filterAndSortTurbines(allTurbines);
    Map<String, Integer> onlineTurbines = selectOnlineTurbines(sortedProfitableTurbines);

    return getProductionPlan(allTurbines, onlineTurbines);
  }

  /**
   * Get a list of all the wind turbines in the park.
   *
   * @return a list of all wind turbines
   */
  public List<WindTurbine> getAllWindTurbines() {
    return windTurbineRepository.findAll();
  }

  /**
   * Get a list of profitable turbines and sort them in ascending order by production cost.
   *
   * @param windTurbines the list of all wind turbines
   * @return list of profitable turbines sorted in ascending order by production cost
   */
  private List<WindTurbine> filterAndSortTurbines(List<WindTurbine> windTurbines) {
    return windTurbines.stream()
        // Requirement 1: Keep the turbine online only if you earn money on it
        .filter(turbine -> turbine.getProductionCost() < marketPrice)
        // Requirement 3: Prioritize the cheapest production first while still respecting the other requirements
        .sorted(Comparator.comparingInt(WindTurbine::getProductionCost))
        .toList();
  }

  /**
   * Get a map of the turbines to run to meet the production target.
   *
   * @param windTurbines the list of wind turbines to select from
   * @return map of turbines to run and their capacity
   */
  private Map<String, Integer> selectOnlineTurbines(List<WindTurbine> windTurbines) {
    Map<String, Integer> onlineTurbines = new HashMap<>();
    int remainingProduction = productionTarget;

    // Requirement 2: Run as many turbines as needed to reach the specified production target
    for (WindTurbine turbine : windTurbines) {
      int turbineCapacity = turbine.getCapacity();
      boolean exceedsProductionTarget = remainingProduction - turbineCapacity < 0;

      if (exceedsProductionTarget) {
        break;
      } else {
        onlineTurbines.put(turbine.getIdentifier(), turbineCapacity);
        remainingProduction -= turbineCapacity;
      }
    }

    return onlineTurbines;
  }

  /**
   * Get a list of all turbines and their expected production.
   *
   * @param allTurbines the list of all turbines in the park
   * @param onlineTurbines the list of online turbines
   * @return list of {@link WindTurbineOutputDto}
   */
  private List<WindTurbineOutputDto> getProductionPlan(List<WindTurbine> allTurbines, Map<String, Integer> onlineTurbines) {
    return allTurbines.stream()
        .map(turbine -> {
          int expectedProduction = onlineTurbines.getOrDefault(turbine.getIdentifier(), 0); // 0 if not running
          return new WindTurbineOutputDto(turbine.getIdentifier(), expectedProduction);
        })
        .collect(Collectors.toList());
  }
}
