package ca.qc.ircm.proview.enrichment;

import java.util.List;

/**
 * Services for enrichment protocols.
 */
public interface EnrichmentProtocolService {
  /**
   * Selects enrichment protocol from database.
   *
   * @param id
   *          enrichment protocol's object identifier
   * @return enrichment protocol
   */
  public EnrichmentProtocol get(Long id);

  /**
   * Returns all enrichment protocols.
   *
   * @return All enrichment protocols.
   */
  public List<EnrichmentProtocol> all();

  /**
   * Returns true if enrichment protocol's name is available for insertion.
   *
   * @param name
   *          enrichment protocol's name
   * @return true if enrichment protocol's name is available for insertion
   */
  public boolean availableName(String name);

  /**
   * Inserts enrichment protocol into database.
   *
   * @param protocol
   *          protocol
   */
  public void insert(EnrichmentProtocol protocol);
}
