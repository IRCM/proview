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

import static ca.qc.ircm.proview.user.QUser.user;

import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import com.querydsl.jpa.impl.JPAQuery;
import java.time.Instant;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.SimpleByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentifications.
 */
@Service
@Transactional
public class AuthenticationService {
  private static final long ROBOT_ID = 1L;
  private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private SecurityConfiguration securityConfiguration;
  @Inject
  private LdapConfiguration ldapConfiguration;
  @Inject
  private SpringLdapService springLdapService;
  @Inject
  private ShiroLdapService shiroLdapService;
  @Value("${spring.ldap.username:#{null}}")
  private String ldapUsername;
  /**
   * Used to generate salt for passwords.
   */
  private Random random = new Random();

  protected AuthenticationService() {
  }

  public AuthenticationService(EntityManager entityManager,
      SecurityConfiguration securityConfiguration, LdapConfiguration ldapConfiguration,
      SpringLdapService springLdapService, ShiroLdapService shiroLdapService, String ldapUsername) {
    this.entityManager = entityManager;
    this.securityConfiguration = securityConfiguration;
    this.ldapConfiguration = ldapConfiguration;
    this.springLdapService = springLdapService;
    this.shiroLdapService = shiroLdapService;
    this.ldapUsername = ldapUsername;
  }

  private Subject getSubject() {
    return SecurityUtils.getSubject();
  }

  private User getUser(Long id) {
    if (id == null) {
      return null;
    }

    return entityManager.find(User.class, id);
  }

  private User getUser(String email) {
    if (email == null) {
      return null;
    }

    JPAQuery<User> query = new JPAQuery<>(entityManager);
    query.from(user);
    query.where(user.email.eq(email));
    return query.fetchOne();
  }

  /**
   * Sign user.
   *
   * @param email
   *          user's email
   * @param password
   *          user's password
   * @param rememberMe
   *          true if user is to be remembered between sessions
   * @throws AuthenticationException
   *           user cannot be authenticated
   */
  public void sign(String email, String password, boolean rememberMe)
      throws AuthenticationException {
    if (email == null || password == null) {
      throw new AuthenticationException("username and password cannot be null");
    }

    UsernamePasswordToken token = new UsernamePasswordToken(email, password);
    token.setRememberMe(rememberMe);
    getSubject().login(token);
  }

  /**
   * Signout user.
   */
  public void signout() {
    getSubject().logout();
  }

  /**
   * Run application as another user.
   *
   * @param user
   *          other user
   */
  public void runAs(User user) {
    if (user == null) {
      throw new NullPointerException("user cannot be null");
    }
    getSubject().checkRole(UserRole.ADMIN.name());
    user = getUser(user.getId());
    if (user.isAdmin()) {
      throw new UnauthorizedException("Cannot run as a admin user");
    }

    getSubject().runAs(new SimplePrincipalCollection(user.getId(), realmName()));
  }

  /**
   * Stop running application as another user.
   *
   * @return id of assumed identity, if any
   */
  public Long stopRunAs() {
    PrincipalCollection principalCollection = getSubject().releaseRunAs();
    return (Long) principalCollection.getPrimaryPrincipal();
  }

  /**
   * Selects authentication information based on user's name and password.
   *
   * @param token
   *          authentication token
   * @return authentication information
   * @throws UnknownAccountException
   *           user is unknown
   * @throws InvalidAccountException
   *           user has not been validated yet
   * @throws DisabledAccountException
   *           user is disabled
   * @throws IncorrectCredentialsException
   *           user's password is invalid
   */
  public AuthenticationInfo getAuthenticationInfo(UsernamePasswordToken token) {
    if (token == null || token.getPrincipal() == null || token.getCredentials() == null) {
      throw new UnknownAccountException("No account found for user []");
    }
    if (ldapConfiguration.enabled()) {
      try {
        if (ldapUsername == null) {
          return getShiroLdapAuthenticationInfo(token);
        } else {
          return getSpringLdapAuthenticationInfo(token);
        }
      } catch (AuthenticationException ldapAuthenticationException) {
        return getLocalAuthenticationInfo(token);
      }
    } else {
      return getLocalAuthenticationInfo(token);
    }
  }

  private AuthenticationInfo getSpringLdapAuthenticationInfo(UsernamePasswordToken token) {
    String username = token.getUsername();
    String email = null;
    if (username.contains("@")) {
      String ldapUsername = springLdapService.username(username);
      if (springLdapService.passwordValid(ldapUsername, String.valueOf(token.getPassword()))) {
        email = username;
      }
    } else {
      if (springLdapService.passwordValid(username, String.valueOf(token.getPassword()))) {
        email = springLdapService.email(username);
      }
    }
    User user = getUser(email);
    if (user == null) {
      throw new UnknownAccountException("No account found for user [" + username + "]");
    }
    user.setSignAttempts(0);
    user.setLastSignAttempt(Instant.now());
    entityManager.merge(user);
    return new SimpleAuthenticationInfo(user.getId(), user.getHashedPassword(),
        new SimpleByteSource(Hex.decode(user.getSalt())), realmName());
  }

  private AuthenticationInfo getShiroLdapAuthenticationInfo(UsernamePasswordToken token) {
    String username = token.getUsername();
    String email = shiroLdapService.getEmail(username, String.valueOf(token.getPassword()));
    User user = getUser(email);
    if (user == null) {
      throw new UnknownAccountException("No account found for user [" + username + "]");
    }
    user.setSignAttempts(0);
    user.setLastSignAttempt(Instant.now());
    entityManager.merge(user);
    return new SimpleAuthenticationInfo(user.getId(), user.getHashedPassword(),
        new SimpleByteSource(Hex.decode(user.getSalt())), realmName());
  }

  private AuthenticationInfo getLocalAuthenticationInfo(UsernamePasswordToken token) {
    String username = token.getUsername();
    User user = getUser(username);
    if (user == null) {
      throw new UnknownAccountException("No account found for user [" + username + "]");
    }
    if (!user.isValid()) {
      throw new InvalidAccountException("Account for user [" + username + "] is invalid");
    }
    if (!user.isActive()) {
      throw new DisabledAccountException("Account for user [" + username + "] is disabled");
    }
    if (!canAttempt(user)) {
      throw new ExcessiveAttemptsException("To many attemps for user [" + username + "]");
    }
    try {
      if (!credentialMatches(token, user)) {
        user.setSignAttempts(user.getSignAttempts() + 1);
        if (user.getSignAttempts() >= securityConfiguration.disableSignAttemps()) {
          user.setActive(false);
        }
        throw new IncorrectCredentialsException("Submitted credentials for token [" + token
            + "] did not match the expected credentials.");
      }

      user.setSignAttempts(0);
      return new SimpleAuthenticationInfo(user.getId(), user.getHashedPassword(),
          new SimpleByteSource(Hex.decode(user.getSalt())), realmName());
    } finally {
      user.setLastSignAttempt(Instant.now());
      entityManager.merge(user);
    }
  }

  private boolean canAttempt(User user) {
    if (user.getSignAttempts() > 0
        && user.getSignAttempts() % securityConfiguration.maximumSignAttemps() == 0) {
      if (Instant.now().isBefore(
          user.getLastSignAttempt().plusMillis(securityConfiguration.maximumSignAttempsDelay()))) {
        return false;
      }
    }
    return true;
  }

  private boolean credentialMatches(UsernamePasswordToken token, User user) {
    if (user.getPasswordVersion() == null) {
      return false;
    }

    for (PasswordVersion passwordVersion : securityConfiguration.getPasswordVersions()) {
      if (passwordVersion.getVersion() == user.getPasswordVersion()) {
        SimpleHash hash = new SimpleHash(passwordVersion.getAlgorithm(), token.getCredentials(),
            Hex.decode(user.getSalt()), passwordVersion.getIterations());
        if (hash.toHex().equals(user.getHashedPassword())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Returns true if userId matches robot id, false otherwise.
   *
   * @param userId
   *          user's database identifier.
   * @return true if userId matches robot id, false otherwise
   */
  public boolean isRobot(Long userId) {
    if (userId == null) {
      return false;
    }

    return userId == ROBOT_ID;
  }

  /**
   * Returns user's authorization information.
   *
   * @param principals
   *          user's principals
   * @return user's authorization information
   */
  public AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals) {
    if (principals == null) {
      return new SimpleAuthorizationInfo();
    }

    Long userId = (Long) principals.getPrimaryPrincipal();
    User user = getUser(userId);
    SimpleAuthorizationInfo authorization = new SimpleAuthorizationInfo();
    if (!user.isValid() || !user.isActive()) {
      authorization.setRoles(new HashSet<>());
      authorization.setObjectPermissions(new HashSet<>());
      return authorization;
    } else {
      boolean manager = user.getLaboratory().getManagers().contains(user);
      authorization.setRoles(selectRoles(user, manager));
      authorization.setObjectPermissions(selectPermissions(user, manager));
      return authorization;
    }
  }

  private Set<String> selectRoles(User user, boolean manager) {
    Set<String> roles = new HashSet<>();
    roles.add(UserRole.USER.name());
    if (manager) {
      roles.add(UserRole.MANAGER.name());
    }
    if (user.isAdmin()) {
      roles.add(UserRole.ADMIN.name());
      if (user.isApprover()) {
        roles.add(UserRole.APPROVER.name());
      }
    }
    if (user.getId() == ROBOT_ID) {
      roles.add(UserRole.MANAGER.name());
      roles.add(UserRole.ADMIN.name());
    }

    Set<String> lowerUpperRoles = new HashSet<>();
    for (String role : roles) {
      lowerUpperRoles.add(role.toLowerCase());
      lowerUpperRoles.add(role.toUpperCase());
    }
    logger.trace("User {} has roles {}", user, lowerUpperRoles);
    return lowerUpperRoles;
  }

  private Set<Permission> selectPermissions(User user, boolean manager) {
    Set<Permission> permissions = new HashSet<>();

    permissions.add(new WildcardPermission("user:*:" + user.getId()));
    permissions.add(new WildcardPermission("laboratory:read:" + user.getLaboratory().getId()));
    if (manager) {
      permissions.add(new WildcardPermission("laboratory:manager:" + user.getLaboratory().getId()));
    }
    if (user.getId() == ROBOT_ID) {
      permissions.add(new RobotPermission());
    }
    logger.trace("User {} has permissions {}", user, permissions);
    return permissions;
  }

  private String realmName() {
    return securityConfiguration.realmName();
  }

  /**
   * Hashes password. This method should be used before inserting password into database.
   *
   * @param password
   *          password as entered by user
   * @return hashed password
   */
  public HashedPassword hashPassword(String password) {
    if (password == null) {
      return null;
    }
    final byte[] salt = new byte[64];
    random.nextBytes(salt);
    PasswordVersion passwordVersion = securityConfiguration.getPasswordVersion();
    final SimpleHash hash = new SimpleHash(passwordVersion.getAlgorithm(), password, salt,
        passwordVersion.getIterations());
    return new HashedPassword(hash, passwordVersion.getVersion());
  }
}
