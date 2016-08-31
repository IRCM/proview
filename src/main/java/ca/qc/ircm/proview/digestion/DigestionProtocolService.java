package ca.qc.ircm.proview.digestion;

import java.util.List;

/**
 * Services for digestion protocol.
 */
public interface DigestionProtocolService {
  /**
   * Selects digestion protocol from database.
   *
   * @param id
   *          digestion protocol's object identifier
   * @return digestion protocol
   */
  public DigestionProtocol get(Long id);

  /**
   * Returns all digestion protocols.
   *
   * @return All digestion protocols.
   */
  public List<DigestionProtocol> all();

  /**
   * Returns true if digestion protocol's name is available for insertion.
   *
   * @param name
   *          digestion protocol's name
   * @return true if digestion protocol's name is available for insertion
   */
  public boolean availableName(String name);

  /**
   * Inserts digestion protocol into database.
   *
   * @param protocol
   *          digestion protocol
   */
  public void insert(DigestionProtocol protocol);
}
