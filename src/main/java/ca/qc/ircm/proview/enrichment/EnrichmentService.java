package ca.qc.ircm.proview.enrichment;

import ca.qc.ircm.proview.sample.Sample;

import java.util.List;

/**
 * Service for enrichment.
 */
public interface EnrichmentService {
  /**
   * Selects enrichment from database.
   *
   * @param id
   *          database identifier of enrichment
   * @return enrichment
   */
  public Enrichment get(Long id);

  /**
   * Returns all enrichments where sample was enriched.
   *
   * @param sample
   *          sample
   * @return all enrichments where sample was enriched
   */
  public List<Enrichment> all(Sample sample);

  /**
   * Inserts an enrichment into the database.
   *
   * @param enrichment
   *          enrichment to insert
   */
  public void insert(Enrichment enrichment);

  /**
   * Undo erroneous enrichment that never actually occurred. This method is usually called shortly
   * after action was inserted into the database. The user realises that the samples checked for
   * enrichment are not the right ones. So, in practice, the enrichment never actually occurred.
   *
   * @param enrichment
   *          erroneous enrichment to undo
   * @param justification
   *          explanation of what was incorrect with the enrichment
   */
  public void undoErroneous(Enrichment enrichment, String justification);

  /**
   * Report that a problem occurred during enrichment causing it to fail. Problems usually occur
   * because of an experimental error. In this case, the enrichment was done but the incorrect
   * enrichment could only be detected later in the sample processing. Thus the enrichment is not
   * undone but flagged as having failed.
   *
   * @param enrichment
   *          enrichment to flag as having failed
   * @param failedDescription
   *          description of the problem that occurred
   * @param banContainers
   *          true if containers used in enrichment should be banned, this will also ban any
   *          container were samples were transfered after enrichment
   */
  public void undoFailed(Enrichment enrichment, String failedDescription, boolean banContainers);
}
