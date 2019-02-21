package ca.qc.ircm.proview.plate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Plate repository.
 */
public interface PlateRepository
    extends JpaRepository<Plate, Long>, QueryDslPredicateExecutor<Plate> {
  long countByName(String name);
}
