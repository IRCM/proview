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
