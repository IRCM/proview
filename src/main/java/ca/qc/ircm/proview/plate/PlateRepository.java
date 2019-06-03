package ca.qc.ircm.proview.plate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Plate repository.
 */
public interface PlateRepository
    extends JpaRepository<Plate, Long>, QuerydslPredicateExecutor<Plate> {
  long countByName(String name);
}
