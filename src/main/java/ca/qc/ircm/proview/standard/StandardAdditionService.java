package ca.qc.ircm.proview.standard;

import ca.qc.ircm.proview.sample.Sample;

import java.util.List;

/**
 * Services for addition of standards.
 */
public interface StandardAdditionService {

  /**
   * Selects standard addition from database.
   *
   * @param id
   *          standard addition's database identifier
   * @return standard addition
   */
  public StandardAddition get(Long id);

  /**
   * Returns all sample's standard addition.
   *
   * @param sample
   *          sample
   * @return all sample's standard addition
   */
  public List<StandardAddition> all(Sample sample);

  /**
   * Inserts standard addition into database.
   *
   * @param standardAddition
   *          standard addition
   */
  public void insert(StandardAddition standardAddition);

  /**
   * Undo erroneous standard addition that never actually occurred. This method is usually called
   * shortly after action was inserted into the database. The user realises that the samples checked
   * for standard addition are not the right ones. So, in practice, the standard addition never
   * actually occurred.
   *
   * @param standardAddition
   *          erroneous standard addition to undo
   * @param justification
   *          explanation of what was incorrect with the standard addition
   */
  public void undoErroneous(StandardAddition standardAddition, String justification);

  /**
   * Report that a problem occurred during standard addition causing it to fail. Problems usually
   * occur because of an experimental error. In this case, the standard addition was done but the
   * incorrect standard addition could only be detected later in the sample processing. Thus the
   * standard addition is not undone but flagged as having failed.
   *
   * @param standardAddition
   *          standard addition to flag as having failed
   * @param failedDescription
   *          description of the problem that occurred
   * @param banContainers
   *          true if containers used in standard addition should be banned, this will also ban any
   *          container were samples were transfered after standard addition
   */
  public void undoFailed(StandardAddition standardAddition, String failedDescription,
      boolean banContainers);
}
