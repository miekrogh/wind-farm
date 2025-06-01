package app.windfarm.repository;

import app.windfarm.entities.WindTurbine;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository for persisting {@link WindTurbine} objects in a database. */
@Repository
public interface WindTurbineRepository extends JpaRepository<WindTurbine, String> {}
