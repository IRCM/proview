package ca.qc.ircm.proview.security;

import ca.qc.ircm.proview.Data;
import java.io.Serial;
import java.util.Collection;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * An authenticated user.
 */
public class UserDetailsWithId extends User implements Data {

  @Serial
  private static final long serialVersionUID = -5167464958438112402L;
  private final long id;

  /**
   * Construct the <code>AuthenticatedUser</code> with the details required by authentication.
   *
   * @param user                  user
   * @param accountNonExpired     set to <code>true</code> if the account has not expired
   * @param credentialsNonExpired set to <code>true</code> if the credentials have not expired
   * @param accountNonLocked      set to <code>true</code> if the account is not locked
   * @param authorities           the authorities that should be granted to the caller if they
   *                              presented the correct username and password and the user is
   *                              enabled. Not null.
   * @throws IllegalArgumentException if a <code>null</code> value was passed either as a parameter
   *                                  or as an element in the
   *                                  <code>GrantedAuthority</code> collection
   */
  public UserDetailsWithId(ca.qc.ircm.proview.user.User user, boolean accountNonExpired,
      boolean credentialsNonExpired, boolean accountNonLocked,
      Collection<? extends GrantedAuthority> authorities) {
    super(user.getEmail(), user.getHashedPassword(), user.isActive(), accountNonExpired,
        credentialsNonExpired, accountNonLocked, authorities);
    id = user.getId();
  }

  /**
   * Construct the <code>AuthenticatedUser</code> with the details required by authentication.
   *
   * @param user        user
   * @param authorities the authorities that should be granted to the caller if they presented the
   *                    correct username and password and the user is enabled. Not null.
   * @throws IllegalArgumentException if a <code>null</code> value was passed either as a parameter
   *                                  or as an element in the
   *                                  <code>GrantedAuthority</code> collection
   */
  public UserDetailsWithId(ca.qc.ircm.proview.user.User user,
      Collection<? extends GrantedAuthority> authorities) {
    super(user.getEmail(), user.getHashedPassword(), authorities);
    id = user.getId();
  }

  /**
   * Construct the <code>AuthenticatedUser</code> with the details required by authentication.
   *
   * @param user        user
   * @param password    hashed password
   * @param authorities the authorities that should be granted to the caller if they presented the
   *                    correct username and password and the user is enabled. Not null.
   * @throws IllegalArgumentException if a <code>null</code> value was passed either as a parameter
   *                                  or as an element in the
   *                                  <code>GrantedAuthority</code> collection
   */
  public UserDetailsWithId(ca.qc.ircm.proview.user.User user, String password,
      Collection<? extends GrantedAuthority> authorities) {
    super(user.getEmail(), password, authorities);
    id = user.getId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDetailsWithId that = (UserDetailsWithId) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public long getId() {
    return id;
  }
}
