package ca.qc.ircm.proview.user;

/**
 * User authorities.
 */
public interface UserAuthority {

  /**
   * Forces user to change his password.
   */
  String FORCE_CHANGE_PASSWORD = "CHANGE_PASSWORD";

  static String laboratoryMember(Laboratory laboratory) {
    return Laboratory.class.getSimpleName() + "_" + laboratory.getId();
  }
}
