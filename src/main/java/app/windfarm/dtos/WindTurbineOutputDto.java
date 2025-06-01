package app.windfarm.dtos;

/**
 * Represents the expected energy output of a wind turbine.
 * Encapsulates the data sent by the API in response to GET requests for the production plan.
 *
 * @param identifier the unique identifier of the turbine
 * @param expectedProduction the expected energy production in MWh
 */
public record WindTurbineOutputDto(String identifier, int expectedProduction) {}
