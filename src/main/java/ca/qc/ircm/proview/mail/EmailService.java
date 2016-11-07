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
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
   * Sends an email with text content only.
   *
   * @param email
   *          email with text content only
   * @throws EmailException
   *           could not send email
   */
  public void sendTextEmail(TextEmail email) throws EmailException {
    if (!mailConfiguration.isEnabled()) {
      return;
    }

    try {
      MimeMessage springEmail = new MimeMessage(templateMessage);
      MimeMessageHelper helper = new MimeMessageHelper(springEmail);
      helper.setTo(email.getReceivers().toArray(new String[0]));
      helper.setSubject(email.getSubject());
      helper.setText(email.getTextMessage());
      // Send email.
      mailSender.send(springEmail);
    } catch (MessagingException e) {
      logger.error("Could not send error email with content {}", email.getTextMessage(), e);
    }
  }

  /**
   * Sends an email with HTML content.
   *
   * @param email
   *          email with HTML content
   * @throws EmailException
   *           could not send email
   */
  public void sendHtmlEmail(final HtmlEmail email) throws EmailException {
    if (!mailConfiguration.isEnabled()) {
      return;
    }

    try {
      MimeMessage springEmail = new MimeMessage(templateMessage);
      MimeMessageHelper helper = new MimeMessageHelper(springEmail, true);
      helper.setTo(email.getReceivers().toArray(new String[0]));
      helper.setSubject(email.getSubject());
      helper.setText(email.getTextMessage(), email.getHtmlMessage());
      // Send email.
      mailSender.send(springEmail);
    } catch (MessagingException e) {
      logger.error("Could not send error email with content {}", email.getTextMessage(), e);
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
      MimeMessage email = new MimeMessage(templateMessage);
      MimeMessageHelper helper = new MimeMessageHelper(email);
      helper.setText(message.toString());
      // Send email.
      mailSender.send(email);
    } catch (MessagingException e) {
      logger.error("Could not send error email with content {}", message.toString(), e);
    }
  }
}
