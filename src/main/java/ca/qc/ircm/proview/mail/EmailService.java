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

import org.apache.commons.mail.EmailException;

/**
 * Service class for sending emails.
 */
public interface EmailService {
  /**
   * Sends an email with text content only.
   *
   * @param email
   *          email with text content only
   * @throws EmailException
   *           could not send email
   */
  public void sendTextEmail(TextEmail email) throws EmailException;

  /**
   * Sends an email with HTML content.
   *
   * @param email
   *          email with HTML content
   * @throws EmailException
   *           could not send email
   */
  public void sendHtmlEmail(HtmlEmail email) throws EmailException;

  /**
   * Sends an email to the system administrator containing error.
   *
   * @param error
   *          error to send
   */
  public void sendError(Throwable error);
}
