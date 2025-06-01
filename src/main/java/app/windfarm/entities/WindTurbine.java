package app.windfarm.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/** Object representing a wind turbine. */
@Entity
public class WindTurbine {

  private @Id String identifier;
  private int capacity;
  private int productionCost;

  // no-arg constructor for Spring Boot
  protected WindTurbine() {}

  /**
   * Constructor for a wind turbine.
   *
   * @param identifier the unique identifier of the turbine
   * @param capacity the capacity of the turbine
   * @param productionCost the production cost of the turbine
   */
  public WindTurbine(String identifier, int capacity, int productionCost) {
    this.identifier = identifier;
    this.capacity = capacity;
    this.productionCost = productionCost;
  }

  /**
   * Get the identifier of the turbine.
   *
   * @return the identifier
   */
  public String getIdentifier() {
    return identifier;
  }

  /**
   * Get the capacity of the turbine.
   *
   * @return the capacity measured in MWh
   */
  public int getCapacity() {
    return capacity;
  }

  /**
   * Get the production cost of the turbine.
   *
   * @return the production cost measured in €/MWh
   */
  public int getProductionCost() {
    return productionCost;
  }
}
