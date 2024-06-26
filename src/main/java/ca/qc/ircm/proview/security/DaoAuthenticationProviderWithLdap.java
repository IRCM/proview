/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.security;

import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * {@link DaoAuthenticationProvider} that also validates password using {@link LdapService}.
 */
public class DaoAuthenticationProviderWithLdap extends DaoAuthenticationProvider {
  private static final Logger logger =
      LoggerFactory.getLogger(DaoAuthenticationProviderWithLdap.class);
  private UserRepository userRepository;
  private LdapService ldapService;
  private SecurityConfiguration securityConfiguration;
  private LdapConfiguration ldapConfiguration;

  @Override
  protected void additionalAuthenticationChecks(UserDetails userDetails,
      UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    String username = authentication.getName();
    logger.trace("user {} tries to authenticate", username);
    User user = getUser(userDetails);
    if (!user.isActive()) {
      logger.debug("user {} account is disabled", username);
      throw new DisabledException("User " + username + " is inactive");
    }
    if (accountLocked(user)) {
      logger.debug("user {} account is locked", username);
      throw new LockedException("User " + username + " account is locked");
    }

    try {
      super.additionalAuthenticationChecks(userDetails, authentication);
      resetSignAttemps(user);
      logger.debug("user {} authenticated successfully", username);
    } catch (BadCredentialsException e) {
      // Try LDAP, if available.
      if (authentication.getCredentials() != null && ldapConfiguration.enabled()
          && isLdapPasswordValid(userDetails, authentication.getCredentials().toString())) {
        // User is valid.
        resetSignAttemps(user);
        logger.debug("user {} authenticated successfully through LDAP", username);
      } else {
        incrementSignAttemps(user);
        logger.debug("user {} supplied wrong password for authentication", username);
        throw e;
      }
    }
  }

  private User getUser(UserDetails userDetails) {
    String email = userDetails.getUsername();
    return userRepository.findByEmail(email).orElse(null);
  }

  private boolean accountLocked(User user) {
    return user.getSignAttempts() > 0
        && user.getSignAttempts() % securityConfiguration.lockAttemps() == 0
        && user.getLastSignAttempt() != null
        && user.getLastSignAttempt().plusMinutes(securityConfiguration.lockDuration().toMinutes())
            .isAfter(LocalDateTime.now());
  }

  private boolean isLdapPasswordValid(UserDetails userDetails, String password) {
    String email = userDetails.getUsername();
    return ldapService.getUsername(email)
        .map(username -> ldapService.isPasswordValid(username, password)).orElse(false);
  }

  private void resetSignAttemps(User user) {
    user.setSignAttempts(0);
    user.setLastSignAttempt(LocalDateTime.now());
    userRepository.save(user);
  }

  private void incrementSignAttemps(User user) {
    user.setSignAttempts(user.getSignAttempts() + 1);
    user.setLastSignAttempt(LocalDateTime.now());
    if (user.getSignAttempts() >= securityConfiguration.disableSignAttemps()) {
      user.setActive(false);
    }
    userRepository.save(user);
  }

  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void setLdapService(LdapService ldapService) {
    this.ldapService = ldapService;
  }

  public void setLdapConfiguration(LdapConfiguration ldapConfiguration) {
    this.ldapConfiguration = ldapConfiguration;
  }

  public void setSecurityConfiguration(SecurityConfiguration securityConfiguration) {
    this.securityConfiguration = securityConfiguration;
  }
}
