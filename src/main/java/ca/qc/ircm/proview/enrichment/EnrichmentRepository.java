package ca.qc.ircm.proview.enrichment;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Enrichment repository.
 */
public interface EnrichmentRepository extends JpaRepository<Enrichment, Long> {
}
