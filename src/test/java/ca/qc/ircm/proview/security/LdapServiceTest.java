package ca.qc.ircm.proview.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class LdapServiceTest {
  private LdapService ldapService;
  @Inject
  private LdapConfiguration ldapConfiguration;

  @Before
  public void beforeTest() {
    ldapService = new LdapService(ldapConfiguration);
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
    assertEquals("christian.poitras@ircm.qc.ca", ldapService.getEmail("poitrasc", "secret"));
  }

  @Test
  public void getEmail_InvalidUser() {
    assertEquals(null, ldapService.getEmail("invalid", "secret"));
  }

  @Test
  public void getEmail_InvalidPassword() {
    assertEquals(null, ldapService.getEmail("poitrasc", "secret2"));
  }
}
