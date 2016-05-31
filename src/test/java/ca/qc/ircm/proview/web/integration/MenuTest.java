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

package ca.qc.ircm.proview.web.integration;

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.proview.user.web.ValidateView;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.testbench.elements.LabelElement;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
public class MenuTest extends MenuPageObject {
  private MessageResource getMainViewResources(Locale locale) {
    return new MessageResource(MainView.class, locale);
  }

  @Test
  public void home() throws Throwable {
    openView(RegisterView.VIEW_NAME);

    clickHome();

    assertEquals(viewUrl(MainView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  public void changeLanguage() throws Throwable {
    open();
    Locale currentLocale = Locale.ENGLISH;
    if ($(LabelElement.class).id("header").getText()
        .equals(getMainViewResources(Locale.FRENCH).message("header"))) {
      currentLocale = Locale.FRENCH;
    }

    clickChangeLanguage();

    assertEquals(viewUrl(MainView.VIEW_NAME), getDriver().getCurrentUrl());
    Locale newLocale = Locale.FRENCH;
    if (currentLocale == Locale.FRENCH) {
      newLocale = Locale.ENGLISH;
    }
    assertEquals(getMainViewResources(newLocale).message("header"),
        $(LabelElement.class).id("header").getText());
  }

  @Test
  @WithSubject
  public void validateUsers() throws Throwable {
    open();

    clickValidateUsers();

    assertEquals(viewUrl(ValidateView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @Ignore("not programmed yet")
  public void help() throws Throwable {
    open();

    clickHelp();

    assertEquals(viewUrl(MainView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}
