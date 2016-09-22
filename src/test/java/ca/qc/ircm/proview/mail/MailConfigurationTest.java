package ca.qc.ircm.proview.mail;

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class MailConfigurationTest {
  @Inject
  private MailConfiguration mailConfiguration;
  @Inject
  private JavaMailSenderImpl mailSender;

  @Test
  public void defaultValues() {
    assertEquals(true, mailConfiguration.isEnabled());
    assertEquals("myemailserver.com", mailConfiguration.getHost());
    assertEquals("proview@ircm.qc.ca", mailConfiguration.getFrom());
    assertEquals("christian.poitras@ircm.qc.ca", mailConfiguration.getTo());
    assertEquals("proview", mailConfiguration.getSubject());
    assertEquals("myemailserver.com", mailSender.getHost());
  }
}
