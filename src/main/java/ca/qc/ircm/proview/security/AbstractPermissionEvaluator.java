package ca.qc.ircm.proview.security;

import static ca.qc.ircm.proview.security.Permission.READ;
import static ca.qc.ircm.proview.security.Permission.WRITE;

import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import java.util.Optional;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implements common methods to use for {@link PermissionEvaluator} implementations.
 */
public abstract class AbstractPermissionEvaluator implements PermissionEvaluator {
  private UserRepository userRepository;

  protected AbstractPermissionEvaluator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  protected Optional<UserDetails> getUserDetails(Authentication authentication) {
    return Optional.of(authentication).filter(au -> au.getPrincipal() instanceof UserDetails)
        .map(au -> (UserDetails) au.getPrincipal());
  }

  protected Optional<User> getUser(Authentication authentication) {
    return getUserDetails(authentication).filter(ud -> ud instanceof UserDetailsWithId)
        .map(ud -> (UserDetailsWithId) ud).map(au -> au.getId())
        .map(id -> userRepository.findById(id).orElseThrow());
  }

  protected Permission resolvePermission(Object permission) {
    if (permission instanceof Permission) {
      return (Permission) permission;
    }
    switch (permission.toString().toUpperCase()) {
      case "READ":
        return READ;
      case "WRITE":
        return WRITE;
      default:
        throw new IllegalArgumentException(
            "Permission " + permission + " does not exists in " + Permission.class.getSimpleName());
    }
  }
}
