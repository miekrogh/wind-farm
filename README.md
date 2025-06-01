## Solution

This project is a simple REST API for controlling a park of wind turbines.
It is built with Java and Spring Boot.

Design choices:
- The application uses an in-memory H2 database and does not persist state between restarts.
- The API is kept very simple and stateless; no authentication, validation, or persistence mechanisms were added.
- Basic logging using Spring’s logging framework to trace incoming requests and system behavior.
- Integration test and unit tests are written with JUnit, Mockito, and Spring Boot's testing framework.
- Docker for containerizing the application.
- A GitHub Actions workflow to automate testing on push or pull requests.


### Project structure
```
src/
├── main/java/app/windfarm/
|   ├── api/
|   |   └── ParkController.java                 // Handles incoming HTTP requests (REST controller)
|   |   └── ControllerExceptionHandler.java     // Maps exceptions to HTTP responses
|   ├── dtos/
|   |   └── WindTurbineOutputDto.java           // DTO representing output per turbine
|   ├── entities/
|   |   └── WindTurbine.java                    // JPA entity representing a wind turbine
|   ├── repository/
|   |   └── WindTurbineRepository.java          // Spring Data JPA repository  
|   ├── service/
|   |   └── ParkService.java                    // Service layer for managing turbines and production
|   └── ParkApplication.java                    // Main application entry point
├── test/java/
|   └── IntegrationTest.java                    // Integration test
|   └── ParkControllerTest.java                 // Unit tests for the REST API
|   └── ParkServiceTest.java                    // Unit tests for the service layer
```


## Run application

The application can be run locally or through Docker. 

Running it locally requires Java JDK 17+ and Maven 3.8+ to be installed on the machine.
To run it locally, execute the following command from the source folder:
```bash
mvn spring-boot:run  
```

To run it through Docker, execute the following:
```bash
docker compose up
```

The application comes preloaded with the five wind turbines. 
The market price and production target are initialized to 0 by the `ParkService` constructor.


### Set market price

In another terminal, execute the following to send a request to the `/set-market-price` endpoint:

```bash
curl -X POST "http://localhost:8080/api/set-market-price?marketPrice=6"
```

Change the input value as needed.
The request is logged in the terminal window running the application.


### Update production target

Execute the following to send a request to the `/update-production-target`

```bash
curl -X POST "http://localhost:8080/api/update-production-target?delta=10"
```

Change the input value as needed.
The request is logged in the terminal window running the application.


### Retrieve production plan

Execute the following to send a request to the `/production-plan` endpoint:

```bash
curl -X GET "http://localhost:8080/api/production-plan"
```

This returns a response in JSON format with the identifier and expected production of each turbine.
The request and the response are also logged in the terminal window running the application, formatted for easier output verification. 

Example output in the terminal window running the application:
```
---------------------------------
| Turbine | Expected production |
---------------------------------
| A       | 0                   |
| B       | 2                   |
| C       | 0                   |
| D       | 0                   |
| E       | 5                   |
---------------------------------
Sum production: 7MWh. Target production: 10MWh. Price limit: 6€.
```

Example response in terminal window sending the requests:
```
[{"identifier":"A","expectedProduction":0},{"identifier":"B","expectedProduction":2},{"identifier":"C","expectedProduction":0},{"identifier":"D","expectedProduction":0},{"identifier":"E","expectedProduction":5}]%
``` 


## Run tests

To run all unit and integration tests, execute the following command:

```bash
mvn test
```