package ca.qc.ircm.proview.treatment;

/**
 * Services for protocols.
 */
public interface ProtocolService {
  /**
   * Selects protocol from database.
   *
   * @param id
   *          database identifier of protocol
   * @return protocol
   */
  public Protocol get(Long id);
}
