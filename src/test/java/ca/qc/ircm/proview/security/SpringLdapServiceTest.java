package ca.qc.ircm.proview.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SpringLdapServiceTest {
  private SpringLdapService ldapService;
  @Inject
  private LdapTemplate ldapTemplate;
  @Inject
  private LdapConfiguration ldapConfiguration;

  @Before
  public void beforeTest() {
    ldapService = new SpringLdapService(ldapTemplate, ldapConfiguration);
  }

  @Test
  public void passwordValid_True() {
    assertTrue(ldapService.passwordValid("poitrasc", "secret"));
  }

  @Test
  public void passwordValid_InvalidUser() {
    assertFalse(ldapService.passwordValid("invalid", "secret"));
  }

  @Test
  public void passwordValid_InvalidPassword() {
    assertFalse(ldapService.passwordValid("poitrasc", "secret2"));
  }

  @Test
  public void email() {
    assertEquals("christian.poitras@ircm.qc.ca", ldapService.email("poitrasc"));
  }

  @Test
  public void username() {
    assertEquals("poitrasc", ldapService.username("christian.poitras@ircm.qc.ca"));
  }
}
