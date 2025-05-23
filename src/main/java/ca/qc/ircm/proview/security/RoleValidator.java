package ca.qc.ircm.proview.security;

import static ca.qc.ircm.proview.UsedBy.SPRING;

import ca.qc.ircm.proview.UsedBy;
import java.util.Collection;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Services for authorization.
 */
@Service
public class RoleValidator {

  @UsedBy(SPRING)
  protected RoleValidator() {
  }

  private Optional<Authentication> getAuthentication() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
  }

  /**
   * Returns true if current user has specified role, false otherwise.
   *
   * @param role role
   * @return true if current user has specified role, false otherwise
   */
  public boolean hasRole(String role) {
    Optional<Authentication> optionalAuthentication = getAuthentication();
    if (optionalAuthentication.isEmpty()) {
      return false;
    }
    Authentication authentication = optionalAuthentication.orElseThrow();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    boolean hasRole = false;
    for (GrantedAuthority authority : authorities) {
      hasRole |= authority.getAuthority().equals(role);
    }
    return hasRole;
  }

  /**
   * Returns true if current user has any of the specified roles, false otherwise.
   *
   * @param roles roles
   * @return true if current user has any of the specified roles, false otherwise
   */
  public boolean hasAnyRole(String... roles) {
    boolean hasAnyRole = false;
    for (String role : roles) {
      hasAnyRole |= hasRole(role);
    }
    return hasAnyRole;
  }

  /**
   * Returns true if current user has all of the specified roles, false otherwise.
   *
   * @param roles roles
   * @return true if current user has all of the specified roles, false otherwise
   */
  public boolean hasAllRoles(String... roles) {
    boolean hasAllRole = true;
    for (String role : roles) {
      hasAllRole &= hasRole(role);
    }
    return hasAllRole;
  }
}
