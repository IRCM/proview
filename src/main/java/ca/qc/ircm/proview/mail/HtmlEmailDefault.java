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

import java.util.Collection;
import java.util.LinkedList;

public class HtmlEmailDefault implements HtmlEmail {
  private Collection<String> receivers;
  private String subject;
  private String textMessage;
  private String htmlMessage;

  @Override
  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  @Override
  public String getTextMessage() {
    return textMessage;
  }

  public void setTextMessage(String textMessage) {
    this.textMessage = textMessage;
  }

  @Override
  public String getHtmlMessage() {
    return htmlMessage;
  }

  public void setHtmlMessage(String htmlMessage) {
    this.htmlMessage = htmlMessage;
  }

  @Override
  public Collection<String> getReceivers() {
    return receivers;
  }

  /**
   * Adds a receiver to the list of receivers.
   *
   * @param receiver
   *          receiver
   */
  public void addReceiver(String receiver) {
    if (receivers == null) {
      receivers = new LinkedList<String>();
    }
    receivers.add(receiver);
  }

  public void setReceivers(Collection<String> receivers) {
    this.receivers = receivers;
  }
}