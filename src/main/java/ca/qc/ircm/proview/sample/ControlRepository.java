package ca.qc.ircm.proview.sample;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Control repository.
 */
public interface ControlRepository extends JpaRepository<Control, Long> {
  Control findByName(String name);

  long countByName(String name);
}
