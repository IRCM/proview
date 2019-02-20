package ca.qc.ircm.proview.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Laboratory repository.
 */
public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {
}
