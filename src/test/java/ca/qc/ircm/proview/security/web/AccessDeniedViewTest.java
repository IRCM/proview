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

package ca.qc.ircm.proview.security.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.security.web.AccessDeniedView.HEADER;
import static ca.qc.ircm.proview.security.web.AccessDeniedView.HOME;
import static ca.qc.ircm.proview.security.web.AccessDeniedView.MESSAGE;
import static ca.qc.ircm.proview.security.web.AccessDeniedView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.AbstractKaribuTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link AccessDeniedView}.
 */
@NonTransactionalTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class AccessDeniedViewTest extends AbstractKaribuTestCase {
  private AccessDeniedView view;
  private Locale locale = Locale.ENGLISH;
  private AppResources resources = new AppResources(AccessDeniedView.class, locale);
  private AppResources generalResources = new AppResources(Constants.class, locale);

  @BeforeEach
  public void beforeTest() {
    ui.setLocale(locale);
    view = new AccessDeniedView();
  }

  @Test
  public void styles() {
    assertTrue(view.getContent().getId().orElse("").equals(VIEW_NAME));
    assertTrue(view.header.hasClassName(HEADER));
    assertTrue(view.message.hasClassName(MESSAGE));
    assertTrue(view.home.hasClassName(HOME));
  }

  @Test
  public void labels() {
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    assertEquals(resources.message(MESSAGE), view.message.getText());
    assertEquals(resources.message(HOME), view.home.getText());
  }

  @Test
  public void localeChange() {
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = Locale.FRENCH;
    final AppResources resources = new AppResources(AccessDeniedView.class, locale);
    ui.setLocale(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    assertEquals(resources.message(MESSAGE), view.message.getText());
    assertEquals(resources.message(HOME), view.home.getText());
  }

  @Test
  public void getPageTitle() {
    assertEquals(resources.message(TITLE, generalResources.message(APPLICATION_NAME)),
        view.getPageTitle());
  }
}
