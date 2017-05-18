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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.realm.Realm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class SecurityConfigurationTest {
  @Inject
  private SecurityConfiguration securityConfiguration;
  @Inject
  private AuthenticationService authenticationService;
  @Value("spring.application.name")
  private String realmName;

  @Test
  public void defaultProperties() throws Throwable {
    assertArrayEquals(Hex.decode("2c9bff536d5bb8ae5550c93cdadace1b"),
        securityConfiguration.getCipherKeyBytes());
    assertEquals(realmName, securityConfiguration.realmName());
    assertEquals(SecurityConfigurationSpringBoot.AUTHORIZATION_CACHE_NAME,
        securityConfiguration.authorizationCacheName());
    assertEquals(3, securityConfiguration.maximumSignAttemps());
    assertEquals(300000, securityConfiguration.maximumSignAttempsDelay());
    assertEquals(15, securityConfiguration.disableSignAttemps());
    PasswordVersion passwordVersion = securityConfiguration.getPasswordVersion();
    assertEquals(1, passwordVersion.getVersion());
    assertEquals("SHA-256", passwordVersion.getAlgorithm());
    assertEquals(1000, passwordVersion.getIterations());
    List<PasswordVersion> passwordVersions = securityConfiguration.getPasswordVersions();
    assertEquals(1, passwordVersions.size());
    passwordVersion = passwordVersions.get(0);
    assertEquals(1, passwordVersion.getVersion());
    assertEquals("SHA-256", passwordVersion.getAlgorithm());
    assertEquals(1000, passwordVersion.getIterations());
  }

  @Test
  public void base64CipherKey() throws Throwable {
    ((SecurityConfigurationSpringBoot) securityConfiguration)
        .setCipherKey("AcEG7RqLxcP6enoSBJKNjA==");
    assertArrayEquals(Base64.decode("AcEG7RqLxcP6enoSBJKNjA=="),
        securityConfiguration.getCipherKeyBytes());
  }

  @Test
  public void shiroRealm() {
    Realm realm = securityConfiguration.shiroRealm();
    assertTrue(realm instanceof ShiroRealm);
    ShiroRealm shiroRealm = (ShiroRealm) realm;
    assertEquals(realmName, shiroRealm.getName());
    assertTrue(shiroRealm.getPermissionResolver() instanceof WildcardPermissionResolver);
    assertFalse(shiroRealm.isAuthenticationCachingEnabled());
    assertTrue(shiroRealm.isAuthorizationCachingEnabled());
    assertEquals(SecurityConfigurationSpringBoot.AUTHORIZATION_CACHE_NAME,
        shiroRealm.getAuthorizationCacheName());
    assertEquals(authenticationService, shiroRealm.getAuthenticationService());
  }

  @Test
  public void multiplePasswordVersions() throws Throwable {
    securityConfiguration = new SecurityConfigurationSpringBoot();
    List<PasswordVersionSpringBoot> passwords = new ArrayList<>();
    passwords.add(new PasswordVersionSpringBoot(1, "SHA-256", 1000));
    passwords.add(new PasswordVersionSpringBoot(4, "SHA-512", 262144));
    passwords.add(new PasswordVersionSpringBoot(3, "SHA-512", 1234));
    ((SecurityConfigurationSpringBoot) securityConfiguration).setPasswords(passwords);

    ((SecurityConfigurationSpringBoot) securityConfiguration).processPasswords();

    PasswordVersion passwordVersion = securityConfiguration.getPasswordVersion();
    assertEquals(4, passwordVersion.getVersion());
    assertEquals("SHA-512", passwordVersion.getAlgorithm());
    assertEquals(262144, passwordVersion.getIterations());
    List<PasswordVersion> passwordVersions = securityConfiguration.getPasswordVersions();
    assertEquals(3, passwordVersions.size());
    passwordVersion = passwordVersions.get(0);
    assertEquals(4, passwordVersion.getVersion());
    assertEquals("SHA-512", passwordVersion.getAlgorithm());
    assertEquals(262144, passwordVersion.getIterations());
    passwordVersion = passwordVersions.get(1);
    assertEquals(3, passwordVersion.getVersion());
    assertEquals("SHA-512", passwordVersion.getAlgorithm());
    assertEquals(1234, passwordVersion.getIterations());
    passwordVersion = passwordVersions.get(2);
    assertEquals(1, passwordVersion.getVersion());
    assertEquals("SHA-256", passwordVersion.getAlgorithm());
    assertEquals(1000, passwordVersion.getIterations());
  }

  @Test(expected = IllegalStateException.class)
  public void noPasswords() throws Throwable {
    securityConfiguration = new SecurityConfigurationSpringBoot();
    List<PasswordVersionSpringBoot> passwords = new ArrayList<>();
    ((SecurityConfigurationSpringBoot) securityConfiguration).setPasswords(passwords);

    ((SecurityConfigurationSpringBoot) securityConfiguration).processPasswords();
  }

  @Test(expected = IllegalStateException.class)
  public void versionMissing() throws Throwable {
    securityConfiguration = new SecurityConfigurationSpringBoot();
    List<PasswordVersionSpringBoot> passwords = new ArrayList<>();
    passwords.add(new PasswordVersionSpringBoot(0, "SHA-256", 1000));
    ((SecurityConfigurationSpringBoot) securityConfiguration).setPasswords(passwords);

    ((SecurityConfigurationSpringBoot) securityConfiguration).processPasswords();
  }

  @Test(expected = IllegalStateException.class)
  public void algorithmMissing() throws Throwable {
    securityConfiguration = new SecurityConfigurationSpringBoot();
    List<PasswordVersionSpringBoot> passwords = new ArrayList<>();
    passwords.add(new PasswordVersionSpringBoot(1, null, 1000));
    ((SecurityConfigurationSpringBoot) securityConfiguration).setPasswords(passwords);

    ((SecurityConfigurationSpringBoot) securityConfiguration).processPasswords();
  }

  @Test(expected = IllegalStateException.class)
  public void algorithmInvalid() throws Throwable {
    securityConfiguration = new SecurityConfigurationSpringBoot();
    List<PasswordVersionSpringBoot> passwords = new ArrayList<>();
    passwords.add(new PasswordVersionSpringBoot(1, "AAA", 1000));
    ((SecurityConfigurationSpringBoot) securityConfiguration).setPasswords(passwords);

    ((SecurityConfigurationSpringBoot) securityConfiguration).processPasswords();
  }

  @Test(expected = IllegalStateException.class)
  public void iterationsMissing() throws Throwable {
    securityConfiguration = new SecurityConfigurationSpringBoot();
    List<PasswordVersionSpringBoot> passwords = new ArrayList<>();
    passwords.add(new PasswordVersionSpringBoot(1, "SHA-256", 0));
    ((SecurityConfigurationSpringBoot) securityConfiguration).setPasswords(passwords);

    ((SecurityConfigurationSpringBoot) securityConfiguration).processPasswords();
  }
}
