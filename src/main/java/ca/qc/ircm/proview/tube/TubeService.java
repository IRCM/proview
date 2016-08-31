package ca.qc.ircm.proview.tube;

import ca.qc.ircm.proview.sample.Sample;

import java.util.Collection;
import java.util.List;

/**
 * Service for digestion tubes.
 */
public interface TubeService {
  /**
   * Selects tube from database.
   *
   * @param id
   *          database identifier of tube
   * @return tube
   */
  public Tube get(Long id);

  /**
   * Returns digestion tube.
   *
   * @param name
   *          tube name.
   * @return digestion tube.
   */
  public Tube get(String name);

  /**
   * Selects sample's original (first) tube. For submitted samples, this returns the tube in which
   * sample was submitted.
   *
   * @param sample
   *          sample
   * @return sample's original tube
   */
  public Tube original(Sample sample);

  /**
   * Selects last tube in which sample was put.
   *
   * @param sample
   *          sample
   * @return last tube in which sample was put
   */
  public Tube last(Sample sample);

  /**
   * <p>
   * Returns digestion tubes used for sample.
   * </p>
   * <p>
   * Tubes are ordered from most recent to older tubes.
   * </p>
   *
   * @param sample
   *          sample.
   * @return digestion tubes used for sample.
   */
  public List<Tube> all(Sample sample);

  /**
   * Selects all tube names beginning with specified string.
   *
   * @param beginning
   *          beginning of tube's name
   * @return all tube names beginning with specified string
   */
  public List<String> selectNameSuggestion(String beginning);

  /**
   * Generates an available tube name for sample. <br>
   * For speed purposes, excludes' contains operation should be fast. Using a Set is recommended.
   *
   * @param sample
   *          sample
   * @param excludes
   *          names to excludes
   * @return available tube name for sample
   */
  public String generateTubeName(Sample sample, Collection<String> excludes);
}
