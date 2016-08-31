package ca.qc.ircm.proview.fractionation;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.transfer.DestinationUsedInTreatmentException;

import java.util.List;

/**
 * Service for fractionation.
 */
public interface FractionationService {
  /**
   * Selects fractionation from database.
   *
   * @param id
   *          database identifier of fractionation
   * @return fractionation
   */
  public Fractionation get(Long id);

  /**
   * Selects fractionated sample corresponding to specified container. Null is returned if container
   * is not linked to a fractionated sample. If fractionated sample was transfered, search will find
   * it in any destination.
   *
   * @param container
   *          sample's container
   * @return fractionated sample corresponding to specified container
   */
  public FractionationDetail find(SampleContainer container);

  /**
   * Selects all fractionations involving sample.
   *
   * @param sample
   *          sample
   * @return all fractionations involving sample
   */
  public List<Fractionation> all(Sample sample);

  /**
   * Add fractionation to database.
   *
   * @param fractionation
   *          fractionation
   */
  public void insert(Fractionation fractionation);

  /**
   * Undo erroneous fractionation that never actually occurred. This method is usually called
   * shortly after action was inserted into the database. The user realises that the samples checked
   * for fractionation are not the right ones. So, in practice, the fractionation never actually
   * occurred.
   *
   * @param fractionation
   *          erroneous fractionation to undo
   * @param justification
   *          explanation of what was incorrect with the fractionation
   * @throws DestinationUsedInTreatmentException
   *           destination container(s) is used in another treatment and sample cannot be remove
   */
  public void undoErroneous(Fractionation fractionation, String justification)
      throws DestinationUsedInTreatmentException;

  /**
   * Report that a problem occurred during fractionation causing it to fail. Problems usually occur
   * because of an experimental error. In this case, the fractionation was done but the incorrect
   * fractionation could only be detected later in the sample processing. Thus the fractionation is
   * not undone but flagged as having failed.
   *
   * @param fractionation
   *          fractionation to flag as having failed
   * @param failedDescription
   *          description of the problem that occurred
   * @param banContainers
   *          true if containers used in fractionation should be banned, this will also ban any
   *          container were samples were transfered / fractionated after fractionation
   */
  public void undoFailed(Fractionation fractionation, String failedDescription,
      boolean banContainers);
}
