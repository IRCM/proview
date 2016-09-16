package ca.qc.ircm.proview.plate;

import ca.qc.ircm.proview.plate.PlateSpotService.SpotLocation;

import java.util.List;

/**
 * Services for plates.
 */
public interface PlateService {
  /**
   * Finds plate in database.
   *
   * @param id
   *          plate's database identifier
   * @return plate
   */
  public Plate get(Long id);

  /**
   * Finds plate in database.
   *
   * @param id
   *          plate's database identifier
   * @return plate
   */
  public Plate getWithSpots(Long id);

  /**
   * Selects all plates of specified type.
   *
   * @param type
   *          plate's type
   * @return all plates of specified type
   */
  public List<Plate> choices(Plate.Type type);

  /**
   * Returns true if plate is available, false otherwise. A plate is considered available when all
   * it's samples are analyzed.
   *
   * @param plate
   *          plate
   * @return true if plate is available, false otherwise
   */
  public boolean available(Plate plate);

  /**
   * Returns true if name is available in database, false otherwise.
   *
   * @param name
   *          plate's name
   * @return true if name is available in database, false otherwise
   */
  public boolean nameAvailable(String name);

  /**
   * Insert plate and it's spots into database.
   *
   * @param plate
   *          plate to insert
   */
  public void insert(Plate plate);

  /**
   * Bans multiple spots to prevent them from being used. Spots that will be banned are spots that
   * are located from <code>from parameter</code> up to <code>to parameter</code>. If a spot was
   * already banned, no change is made to that spot.
   *
   * @param plate
   *          plate were spots are located
   * @param from
   *          first spot to ban
   * @param to
   *          last spot to ban
   * @param justification
   *          justification for banning spots
   */
  public void ban(Plate plate, SpotLocation from, SpotLocation to, String justification);

  /**
   * Reactivates multiple spots that were banned. Spots that will be reactivated are spots that are
   * located from <code>from parameter</code> up to <code>to parameter</code>. If a spot was not
   * banned, no change is made to that spot.
   *
   * @param plate
   *          plate were spots are located
   * @param from
   *          first spot to reactivate
   * @param to
   *          last spot to reactivate
   * @param justification
   *          justification for reactivating spots
   */
  public void activate(Plate plate, SpotLocation from, SpotLocation to, String justification);
}
