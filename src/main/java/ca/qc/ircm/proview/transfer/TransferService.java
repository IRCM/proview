package ca.qc.ircm.proview.transfer;

import ca.qc.ircm.proview.sample.Sample;

import java.util.List;

/**
 * Service for transfer.
 */
public interface TransferService {
  /**
   * Selects transfer from database.
   *
   * @param id
   *          database identifier of transfer
   * @return transfer
   */
  public Transfer get(Long id);

  /**
   * Returns all transfers involving sample.
   *
   * @param sample
   *          sample
   * @return all transfers involving sample
   */
  public List<Transfer> all(Sample sample);

  /**
   * Insert transfer into database.
   *
   * @param transfer
   *          transfer
   */
  public void insert(Transfer transfer);

  /**
   * Undo erroneous transfer that never actually occurred. This method is usually called shortly
   * after action was inserted into the database. The user realises that the samples checked for
   * transfer are not the right ones. So, in practice, the transfer never actually occurred.
   *
   * @param transfer
   *          erroneous transfer to undo
   * @param justification
   *          explanation of what was incorrect with the transfer
   * @throws DestinationUsedInTreatmentException
   *           destination container(s) is used in another treatment and sample cannot be remove
   */
  public void undoErroneous(Transfer transfer, String justification)
      throws DestinationUsedInTreatmentException;

  /**
   * Report that a problem occurred during transfer causing it to fail. Problems usually occur
   * because of an experimental error. In this case, the transfer was done but the incorrect
   * transfer could only be detected later in the sample processing. Thus the transfer is not undone
   * but flagged as having failed.
   *
   * @param transfer
   *          transfer to flag as having failed
   * @param failedDescription
   *          description of the problem that occurred
   * @param banContainers
   *          true if containers used in transfer should be banned, this will also ban any container
   *          were samples were transfered / fractionated after transfer
   */
  public void undoFailed(Transfer transfer, String failedDescription, boolean banContainers);
}