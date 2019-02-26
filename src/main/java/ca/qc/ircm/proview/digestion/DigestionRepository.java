package ca.qc.ircm.proview.digestion;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Digestion repository.
 */
public interface DigestionRepository extends JpaRepository<Digestion, Long> {
}
