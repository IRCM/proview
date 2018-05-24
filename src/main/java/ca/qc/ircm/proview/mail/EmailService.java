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

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.vaadin.easyuploads.Streams;

/**
 * Service class for sending emails.
 */
@Component
public class EmailService {
  private final Logger logger = LoggerFactory.getLogger(EmailService.class);
  @Inject
  private MailConfiguration mailConfiguration;
  @Inject
  private JavaMailSender mailSender;
  @Inject
  private MimeMessage templateMessage;
  @Inject
  private AuthorizationService authorizationService;

  protected EmailService() {
  }

  protected EmailService(MailConfiguration mailConfiguration, JavaMailSender mailSender,
      MimeMessage templateMessage, AuthorizationService authorizationService) {
    this.mailConfiguration = mailConfiguration;
    this.mailSender = mailSender;
    this.templateMessage = templateMessage;
    this.authorizationService = authorizationService;
  }

  /**
   * Creates a plain text email.
   *
   * @return plain text email
   * @throws MessagingException
   *           could not create email
   */
  public MimeMessageHelper textEmail() throws MessagingException {
    MimeMessage message = new MimeMessage(templateMessage);
    return new MimeMessageHelper(message);
  }

  /**
   * Creates a multipart email.
   *
   * @return multipart email
   * @throws MessagingException
   *           could not create email
   */
  public MimeMessageHelper htmlEmail() throws MessagingException {
    MimeMessage message = new MimeMessage(templateMessage);
    return new MimeMessageHelper(message, true);
  }

  /**
   * Sends an email.
   *
   * @param email
   *          email with text content only
   * @throws MessagingException
   *           could not send email
   */
  public void send(MimeMessageHelper email) throws MessagingException {
    if (!mailConfiguration.isEnabled()) {
      return;
    }

    try {
      mailSender.send(email.getMimeMessage());
    } catch (MailException e) {
      logger.error("Could not send error email with content {}", message(email), e);
    }
  }

  private String message(MimeMessageHelper email) {
    try {
      if (email.isMultipart()) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Streams.copy(email.getMimeMultipart().getBodyPart(0).getInputStream(), output);
        return new String(output.toByteArray(), StandardCharsets.UTF_8);
      } else {
        return String.valueOf(email.getMimeMessage().getContent());
      }
    } catch (IOException | IllegalStateException | MessagingException e) {
      return null;
    }
  }

  /**
   * Sends an email to the system administrator containing error.
   *
   * @param error
   *          error to send
   */
  public void sendError(Throwable error) {
    if (!mailConfiguration.isEnabled()) {
      return;
    }

    StringBuilder message = new StringBuilder();
    message.append("User:");
    User user = authorizationService.getCurrentUser();
    if (user != null) {
      message.append(user.getEmail());
    } else {
      message.append("null");
    }
    message.append("\n");
    message.append(error.getMessage());
    message.append("\n");
    StringWriter stringWriter = new StringWriter();
    error.printStackTrace(new PrintWriter(stringWriter));
    message.append(stringWriter.toString());
    try {
      MimeMessageHelper email = textEmail();
      email.setTo(mailConfiguration.getTo());
      email.setText(message.toString());
      send(email);
    } catch (MessagingException e) {
      logger.error("Could not send error email with content {}", message.toString(), e);
    }
  }
}
