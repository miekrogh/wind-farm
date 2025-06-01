package app.windfarm;

import app.windfarm.entities.WindTurbine;
import app.windfarm.repository.WindTurbineRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/** Run the application. */
@SpringBootApplication
public class ParkApplication {

  public static void main(String[] args) {
    SpringApplication.run(ParkApplication.class, args);
  }

  /**
   * Preload the database with the five wind turbines.
   */
  @Bean
  CommandLineRunner initDatabase(WindTurbineRepository repository) {
    return args -> {
      repository.save(new WindTurbine("A", 2, 15));
      repository.save(new WindTurbine("B", 2, 5));
      repository.save(new WindTurbine("C", 6, 5));
      repository.save(new WindTurbine("D", 6, 5));
      repository.save(new WindTurbine("E", 5, 3));
    };
  }
}