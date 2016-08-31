package ca.qc.ircm.proview.digestion;

import ca.qc.ircm.proview.sample.Sample;

import java.util.List;

/**
 * Service for Digestion class.
 */
public interface DigestionService {
  /**
   * Selects digestion from database.
   *
   * @param id
   *          database identifier of digestion
   * @return digestion
   */
  public Digestion get(Long id);

  /**
   * Returns all digestions where sample was digested.
   *
   * @param sample
   *          sample
   * @return all digestions where sample was digested
   */
  public List<Digestion> all(Sample sample);

  /**
   * Inserts a digestion into the database.
   *
   * @param digestion
   *          digestion to insert
   */
  public void insert(Digestion digestion);

  /**
   * Undo erroneous digestion that never actually occurred. This method is usually called shortly
   * after action was inserted into the database. The user realises that the samples checked for
   * digestion are not the right ones. So, in practice, the digestion never actually occurred.
   *
   * @param digestion
   *          erroneous digestion to undo
   * @param justification
   *          explanation of what was incorrect with the digestion
   */
  public void undoErroneous(Digestion digestion, String justification);

  /**
   * Report that a problem occurred during digestion causing it to fail. Problems usually occur
   * because of an experimental error. In this case, the digestion was done but the incorrect
   * digestion could only be detected later in the sample processing. Thus the digestion is not
   * undone but flagged as having failed.
   *
   * @param digestion
   *          digestion to flag as having failed
   * @param failedDescription
   *          description of the problem that occurred
   * @param banContainers
   *          true if containers used in digestion should be banned, this will also ban any
   *          container were samples were transfered after digestion
   */
  public void undoFailed(Digestion digestion, String failedDescription, boolean banContainers);
}
