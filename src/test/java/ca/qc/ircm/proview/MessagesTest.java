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

package ca.qc.ircm.proview;

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for {@link MessagesFactory}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class MessagesTest {
  @Autowired
  private MessageSource messageSource;

  @Test
  public void messageSource_getMessage() {
    assertEquals("ProView",
        messageSource.getMessage("Constants.application.name", null, Locale.ENGLISH));
  }

  @Test
  public void message() {
    Messages messages = new Messages("Constants", Locale.ENGLISH, messageSource);
    assertEquals("Save", messages.message("save"));
  }

  @Test
  public void message_French() {
    Messages messages = new Messages("Constants", Locale.FRENCH, messageSource);
    assertEquals("Sauvegarder", messages.message("save"));
  }

  @Test
  public void message_Replacements() {
    Messages messages = new Messages("Constants", Locale.ENGLISH, messageSource);
    assertEquals("out of the range 2 to 5", messages.message("outOfRange", 2, 5));
  }

  @Test
  public void message_ReplacementsFrench() {
    Messages messages = new Messages("Constants", Locale.FRENCH, messageSource);
    assertEquals("doit être compris entre 2 et 5", messages.message("outOfRange", 2, 5));
  }

  @Test
  public void message_Missing() {
    Messages messages = new Messages("Constants", Locale.ENGLISH, messageSource);
    assertEquals("!{en:Constants.missing_name}!", messages.message("missing_name"));
  }

  @Test
  public void message_MissingFrench() {
    Messages messages = new Messages("Constants", Locale.FRENCH, messageSource);
    assertEquals("!{fr:Constants.missing_name}!", messages.message("missing_name"));
  }

  @Test
  public void message_UserEmail() {
    Messages messages = new Messages("user.User", Locale.ENGLISH, messageSource);
    assertEquals("Email", messages.message("email"));
  }

  @Test
  public void message_UserEmailFrench() {
    Messages messages = new Messages("user.User", Locale.FRENCH, messageSource);
    assertEquals("Courriel", messages.message("email"));
  }
}
