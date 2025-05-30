package ca.qc.ircm.proview.user;

/**
 * Roles for user.
 */
public class UserRole {

  public static final String USER = "ROLE_USER";
  public static final String MANAGER = "ROLE_MANAGER";
  public static final String ADMIN = "ROLE_ADMIN";

  /**
   * Returns all user roles.
   *
   * @return all user roles
   */
  public static String[] roles() {
    return new String[]{USER, MANAGER, ADMIN};
  }
}
