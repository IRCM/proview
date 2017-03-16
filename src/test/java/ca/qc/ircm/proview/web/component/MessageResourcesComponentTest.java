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

package ca.qc.ircm.proview.web.component;

import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;

import ca.qc.ircm.utils.MessageResource;
import com.vaadin.v7.ui.TextField;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class MessageResourcesComponentTest {
  private TestMessageResourcesComponent messageResourcesComponent;
  private Locale locale = Locale.getDefault();
  private Locale frenchLocale = Locale.FRENCH;
  private String message = "This is a test";
  private String frenchMessage = "Ceci est un test";

  @Before
  public void beforeTest() {
    messageResourcesComponent = new TestMessageResourcesComponent();
  }

  @Test
  public void getResources() {
    messageResourcesComponent.setLocale(locale);

    MessageResource resources = messageResourcesComponent.getResources();

    assertEquals(message, resources.message("message"));
  }

  @Test
  public void getResources_French() {
    messageResourcesComponent.setLocale(frenchLocale);

    MessageResource resources = messageResourcesComponent.getResources();

    assertEquals(frenchMessage, resources.message("message"));
  }

  @Test
  public void getGeneralResources() {
    messageResourcesComponent.setLocale(locale);

    MessageResource resources = messageResourcesComponent.getGeneralResources();

    assertEquals("may not be empty", resources.message(REQUIRED));
  }

  @Test
  public void getGeneralResources_French() {
    messageResourcesComponent.setLocale(frenchLocale);

    MessageResource resources = messageResourcesComponent.getGeneralResources();

    assertEquals("ne peut pas être vide", resources.message(REQUIRED));
  }

  @SuppressWarnings("serial")
  private static class TestMessageResourcesComponent extends TextField
      implements MessageResourcesComponent {
  }
}
