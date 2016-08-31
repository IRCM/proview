package ca.qc.ircm.proview.enrichment;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.treatment.TreatmentSample;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * A sample that was enriched.
 */
@Entity
@DiscriminatorValue("ENRICHMENT")
public class EnrichedSample extends TreatmentSample implements Data {
}
