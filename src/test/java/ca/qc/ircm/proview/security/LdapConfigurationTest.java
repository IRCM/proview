package ca.qc.ircm.proview.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link LdapConfiguration}.
 */
@NonTransactionalTestAnnotations
public class LdapConfigurationTest {
  @Autowired
  private LdapConfiguration ldapConfiguration;

  @Test
  public void defaultProperties() throws Throwable {
    assertTrue(ldapConfiguration.enabled());
    assertEquals("uid", ldapConfiguration.idAttribute());
    assertEquals("mail", ldapConfiguration.mailAttribute());
    assertEquals("person", ldapConfiguration.objectClass());
  }
}
