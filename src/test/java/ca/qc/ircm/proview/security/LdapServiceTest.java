package ca.qc.ircm.proview.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;

/**
 * Tests for {@link LdapService}.
 */
@ServiceTestAnnotations
public class LdapServiceTest {

  private LdapService ldapService;
  @Autowired
  private LdapTemplate ldapTemplate;
  @Autowired
  private LdapConfiguration ldapConfiguration;

  @BeforeEach
  public void beforeTest() {
    ldapService = new LdapService(ldapTemplate, ldapConfiguration);
  }

  @Test
  public void isPasswordValid_True() {
    assertTrue(ldapService.isPasswordValid("poitrasc", "secret"));
  }

  @Test
  public void isPasswordValid_InvalidUser() {
    assertFalse(ldapService.isPasswordValid("invalid", "secret"));
  }

  @Test
  public void isPasswordValid_InvalidPassword() {
    assertFalse(ldapService.isPasswordValid("poitrasc", "secret2"));
  }

  @Test
  public void getEmail() {
    assertEquals("christian.poitras@ircm.qc.ca", ldapService.getEmail("poitrasc").orElseThrow());
  }

  @Test
  public void getEmail_Invalid() {
    assertFalse(ldapService.getEmail("invalid").isPresent());
  }

  @Test
  public void getUsername() {
    assertEquals("poitrasc", ldapService.getUsername("christian.poitras@ircm.qc.ca").orElseThrow());
  }

  @Test
  public void getUsername_Invalid() {
    assertFalse(ldapService.getUsername("not.present@ircm.qc.ca").isPresent());
  }
}
