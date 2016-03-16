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

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.inject.Inject;

/**
 * Service class for sending emails.
 */
@Component
public class EmailServiceDefault implements EmailService {
  private final Logger logger = LoggerFactory.getLogger(EmailServiceDefault.class);
  @Inject
  private ApplicationConfiguration applicationConfiguration;
  @Inject
  private AuthorizationService authorizationService;

  protected EmailServiceDefault() {
  }

  protected EmailServiceDefault(ApplicationConfiguration applicationConfiguration,
      AuthorizationService authorizationService) {
    this.applicationConfiguration = applicationConfiguration;
    this.authorizationService = authorizationService;
  }

  @Override
  public void sendTextEmail(TextEmail email) throws EmailException {
    org.apache.commons.mail.SimpleEmail commonsSimpleEmail =
        new org.apache.commons.mail.SimpleEmail();
    commonsSimpleEmail.setHostName(applicationConfiguration.getEmailServer());
    commonsSimpleEmail.setFrom(applicationConfiguration.getEmailSender());
    for (String receiver : email.getReceivers()) {
      commonsSimpleEmail.addTo(receiver);
    }
    commonsSimpleEmail.setSubject(email.getSubject());
    commonsSimpleEmail.setMsg(email.getTextMessage());
    try {
      if (getSendEmail()) {
        // Send email.
        commonsSimpleEmail.send();
      }
    } finally {
      // Always log email content for development.
      logger.trace("Email receivers: {}", email.getReceivers());
      logger.trace("Email subject: {}", email.getSubject());
      logger.trace("Text email content: {}", email.getTextMessage());
    }
  }

  @Override
  public void sendHtmlEmail(final HtmlEmail email) throws EmailException {
    org.apache.commons.mail.HtmlEmail commonsHtmlEmail = new org.apache.commons.mail.HtmlEmail();
    commonsHtmlEmail.setHostName(applicationConfiguration.getEmailServer());
    commonsHtmlEmail.setFrom(applicationConfiguration.getEmailSender());
    for (String receiver : email.getReceivers()) {
      commonsHtmlEmail.addTo(receiver);
    }
    commonsHtmlEmail.setSubject(email.getSubject());
    commonsHtmlEmail.setHtmlMsg(email.getHtmlMessage());
    commonsHtmlEmail.setTextMsg(email.getTextMessage());
    try {
      if (getSendEmail()) {
        // Send email.
        commonsHtmlEmail.send();
      }
    } finally {
      // Always log email content for development.
      logger.trace("Email receivers: {}", email.getReceivers());
      logger.trace("Email subject: {}", email.getSubject());
      logger.trace("HTML email content: {}", email.getHtmlMessage());
      logger.trace("Text email content: {}", email.getTextMessage());
    }
  }

  @Override
  public void sendError(Throwable error) {
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
      org.apache.commons.mail.SimpleEmail commonsSimpleEmail =
          new org.apache.commons.mail.SimpleEmail();
      commonsSimpleEmail.setHostName(applicationConfiguration.getEmailServer());
      commonsSimpleEmail.setFrom(applicationConfiguration.getEmailSender());
      commonsSimpleEmail.addTo(applicationConfiguration.getEmailErrorReceiver());
      commonsSimpleEmail.setSubject("ProView - Error");
      commonsSimpleEmail.setMsg(message.toString());
      try {
        if (getSendEmail()) {
          // Send email.
          commonsSimpleEmail.send();
        }
      } finally {
        // Always log email content for development.
        logger.trace("Email receivers: {}", applicationConfiguration.getEmailErrorReceiver());
        logger.trace("Email subject: {}", "ProView - Error");
        logger.trace("Text email content: {}", message.toString());
      }
    } catch (EmailException e) {
      logger.error("Could not send error email with content {}", message.toString());
    }
  }

  private boolean getSendEmail() {
    return Boolean.valueOf(applicationConfiguration.isEmailEnabled());
  }
}
