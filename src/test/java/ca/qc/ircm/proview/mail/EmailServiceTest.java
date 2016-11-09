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

package ca.qc.ircm.proview.mail;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class EmailServiceTest {
  private EmailService emailService;
  @Inject
  private MailConfiguration mailConfiguration;
  @Inject
  private JavaMailSenderImpl mailSender;
  @Inject
  private MimeMessage templateMessage;
  @Mock
  private AuthorizationService authorizationService;
  private GreenMail testSmtp;
  private String originalHost;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    emailService =
        new EmailService(mailConfiguration, mailSender, templateMessage, authorizationService);
    testSmtp = new GreenMail(ServerSetupTest.SMTP);
    testSmtp.start();
    originalHost = mailSender.getHost();
    mailSender.setPort(testSmtp.getSmtp().getPort());
    mailSender.setHost("localhost");
  }

  @After
  public void cleanup() {
    testSmtp.stop();
    mailSender.setHost(originalHost);
  }

  @Test
  public void textEmail() throws Throwable {
    MimeMessageHelper email = emailService.textEmail();

    assertNotSame(templateMessage, email.getMimeMessage());
    assertEquals(templateMessage.getSubject(), email.getMimeMessage().getSubject());
    assertArrayEquals(templateMessage.getFrom(), email.getMimeMessage().getFrom());
    assertEquals(templateMessage.getContent(), email.getMimeMessage().getContent());
  }

  @Test
  public void htmlEmail() throws Throwable {
    MimeMessageHelper email = emailService.htmlEmail();

    assertNotSame(templateMessage, email.getMimeMessage());
    assertEquals(templateMessage.getSubject(), email.getMimeMessage().getSubject());
    assertArrayEquals(templateMessage.getFrom(), email.getMimeMessage().getFrom());
    assertTrue(email.getMimeMessage().getContent() instanceof Multipart);
    final MimeMessageParser mimeMessageParser =
        new MimeMessageParser(email.getMimeMessage()).parse();
    assertNull(mimeMessageParser.getHtmlContent());
    assertNull(mimeMessageParser.getPlainContent());
  }

  @Test
  public void sendEmail_Text() throws Throwable {
    String receiver = "liam.li@ircm.qc.ca";
    String subject = "test subject";
    String content = "text message";
    MimeMessageHelper email = new MimeMessageHelper(new MimeMessage(templateMessage));
    email.addTo(receiver);
    email.setSubject(subject);
    email.setText(content);

    emailService.send(email);

    MimeMessage[] messages = testSmtp.getReceivedMessages();
    assertEquals(1, messages.length);
    MimeMessage message = messages[0];
    assertEquals(subject, message.getSubject());
    assertNotNull(message.getFrom());
    assertEquals(1, message.getFrom().length);
    assertEquals(new InternetAddress(mailConfiguration.getFrom()), message.getFrom()[0]);
    assertNotNull(message.getRecipients(RecipientType.TO));
    assertEquals(1, message.getRecipients(RecipientType.TO).length);
    assertEquals(new InternetAddress(receiver), message.getRecipients(RecipientType.TO)[0]);
    assertTrue(message.getRecipients(RecipientType.CC) == null
        || message.getRecipients(RecipientType.CC).length == 0);
    assertTrue(message.getRecipients(RecipientType.BCC) == null
        || message.getRecipients(RecipientType.BCC).length == 0);
    String body = GreenMailUtil.getBody(message);
    assertEquals(content, body);
  }

  @Test
  public void sendEmail_HtmlAndText() throws Throwable {
    String receiver = "liam.li@ircm.qc.ca";
    String subject = "test subject";
    String textContent = "text message";
    String htmlContent = "<html><body>html message</body></html>";
    MimeMessageHelper email = new MimeMessageHelper(new MimeMessage(templateMessage), true);
    email.addTo(receiver);
    email.setSubject(subject);
    email.setText(textContent, htmlContent);

    emailService.send(email);

    MimeMessage[] messages = testSmtp.getReceivedMessages();
    assertEquals(1, messages.length);
    MimeMessage message = messages[0];
    assertEquals(subject, message.getSubject());
    assertNotNull(message.getFrom());
    assertEquals(1, message.getFrom().length);
    assertEquals(new InternetAddress(mailConfiguration.getFrom()), message.getFrom()[0]);
    assertNotNull(message.getRecipients(RecipientType.TO));
    assertEquals(1, message.getRecipients(RecipientType.TO).length);
    assertEquals(new InternetAddress(receiver), message.getRecipients(RecipientType.TO)[0]);
    assertTrue(message.getRecipients(RecipientType.CC) == null
        || message.getRecipients(RecipientType.CC).length == 0);
    assertTrue(message.getRecipients(RecipientType.BCC) == null
        || message.getRecipients(RecipientType.BCC).length == 0);
    final MimeMessageParser mimeMessageParser = new MimeMessageParser(message).parse();
    assertEquals(htmlContent, mimeMessageParser.getHtmlContent());
    assertEquals(textContent, mimeMessageParser.getPlainContent());
  }

  @Test
  public void sendEmail_ErrorText() throws Throwable {
    String receiver = "liam.li@ircm.qc.ca";
    String subject = "test subject";
    String content = "text message";
    MimeMessageHelper email = new MimeMessageHelper(new MimeMessage(templateMessage));
    email.addTo(receiver);
    email.setSubject(subject);
    email.setText(content);
    testSmtp.stop();

    emailService.send(email);
  }

  @Test
  public void sendEmail_ErrorHtmlAndText() throws Throwable {
    String receiver = "liam.li@ircm.qc.ca";
    String subject = "test subject";
    String textContent = "text message";
    String htmlContent = "<html><body>html message</body></html>";
    MimeMessageHelper email = new MimeMessageHelper(new MimeMessage(templateMessage), true);
    email.addTo(receiver);
    email.setSubject(subject);
    email.setText(textContent, htmlContent);
    testSmtp.stop();

    emailService.send(email);
  }

  @Test
  public void sendError() throws Throwable {
    Exception error = new IllegalStateException("test");
    StringWriter writer = new StringWriter();
    try (PrintWriter printWriter = new PrintWriter(writer)) {
      error.printStackTrace(printWriter);
    }
    User user = new User(1L, "christian.poitras@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);

    emailService.sendError(error);

    MimeMessage[] messages = testSmtp.getReceivedMessages();
    assertEquals(1, messages.length);
    MimeMessage message = messages[0];
    assertEquals(mailConfiguration.getSubject(), message.getSubject());
    assertNotNull(message.getFrom());
    assertEquals(1, message.getFrom().length);
    assertEquals(new InternetAddress(mailConfiguration.getFrom()), message.getFrom()[0]);
    assertNotNull(message.getRecipients(RecipientType.TO));
    assertEquals(1, message.getRecipients(RecipientType.TO).length);
    assertEquals(new InternetAddress(mailConfiguration.getTo()),
        message.getRecipients(RecipientType.TO)[0]);
    assertTrue(message.getRecipients(RecipientType.CC) == null
        || message.getRecipients(RecipientType.CC).length == 0);
    assertTrue(message.getRecipients(RecipientType.BCC) == null
        || message.getRecipients(RecipientType.BCC).length == 0);
    String body = GreenMailUtil.getBody(message).replaceAll("\r?\n", "");
    String expectedBody = "User:" + user.getEmail()
        + (error.getMessage() + "\n" + writer.toString()).replaceAll("\r?\n", "");
    assertEquals(expectedBody, body);
  }

  @Test
  public void sendError_NoCurrentUser() throws Throwable {
    Exception error = new IllegalStateException("test");
    StringWriter writer = new StringWriter();
    try (PrintWriter printWriter = new PrintWriter(writer)) {
      error.printStackTrace(printWriter);
    }

    emailService.sendError(error);

    MimeMessage[] messages = testSmtp.getReceivedMessages();
    assertEquals(1, messages.length);
    MimeMessage message = messages[0];
    assertEquals(mailConfiguration.getSubject(), message.getSubject());
    assertNotNull(message.getFrom());
    assertEquals(1, message.getFrom().length);
    assertEquals(new InternetAddress(mailConfiguration.getFrom()), message.getFrom()[0]);
    assertNotNull(message.getRecipients(RecipientType.TO));
    assertEquals(1, message.getRecipients(RecipientType.TO).length);
    assertEquals(new InternetAddress(mailConfiguration.getTo()),
        message.getRecipients(RecipientType.TO)[0]);
    assertTrue(message.getRecipients(RecipientType.CC) == null
        || message.getRecipients(RecipientType.CC).length == 0);
    assertTrue(message.getRecipients(RecipientType.BCC) == null
        || message.getRecipients(RecipientType.BCC).length == 0);
    String body = GreenMailUtil.getBody(message).replaceAll("\r?\n", "");
    String expectedBody =
        "User:null" + (error.getMessage() + "\n" + writer.toString()).replaceAll("\r?\n", "");
    assertEquals(expectedBody, body);
  }
}