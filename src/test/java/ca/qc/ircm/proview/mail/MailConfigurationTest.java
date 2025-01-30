package ca.qc.ircm.proview.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Tests for {@link MailConfiguration}.
 */
@NonTransactionalTestAnnotations
public class MailConfigurationTest {

  @Autowired
  private MailConfiguration mailConfiguration;
  @Autowired
  private JavaMailSenderImpl mailSender;

  @Test
  public void defaultValues() {
    assertTrue(mailConfiguration.enabled());
    assertEquals("proview@ircm.qc.ca", mailConfiguration.from());
    assertEquals("christian.poitras@ircm.qc.ca", mailConfiguration.to());
    assertEquals("proview", mailConfiguration.subject());
    assertEquals("localhost", mailSender.getHost());
  }
}
