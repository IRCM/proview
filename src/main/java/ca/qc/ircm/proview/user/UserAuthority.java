package ca.qc.ircm.proview.user;

/**
 * User authorities.
 */
public interface UserAuthority {
  /**
   * Forces user to change his password.
   */
  public static final String FORCE_CHANGE_PASSWORD = "CHANGE_PASSWORD";

  public static String laboratoryMember(Laboratory laboratory) {
    return Laboratory.class.getSimpleName() + "_" + laboratory.getId();
  }
}
