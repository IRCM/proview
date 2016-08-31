package ca.qc.ircm.proview.plate;

import ca.qc.ircm.proview.history.Activity;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link Plate} that can be recorded.
 */
public interface PlateActivityService {
  /**
   * Creates an activity about insertion of plate.
   *
   * @param plate
   *          inserted plate
   * @return activity about insertion of plate
   */
  @CheckReturnValue
  public Activity insert(Plate plate);

  /**
   * Creates an activity about spots being marked as banned.
   *
   * @param spots
   *          spots that were banned
   * @param justification
   *          justification for banning spots
   * @return activity about spots being marked as banned
   */
  @CheckReturnValue
  public Activity ban(Collection<PlateSpot> spots, String justification);

  /**
   * Creates an activity about spots being marked as reactivated.
   *
   * @param spots
   *          spots that were reactivated
   * @param justification
   *          justification for reactivating spots
   * @return activity about spots being marked as reactivated
   */
  @CheckReturnValue
  public Activity activate(Collection<PlateSpot> spots, String justification);
}
