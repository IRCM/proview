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

package ca.qc.ircm.proview.utils.web;

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.TextField;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class MessageResourcesComponentTest {
  private TestMessageResourcesComponent messageResourcesComponent;
  private Locale locale = Locale.getDefault();
  private Locale frenchLocale = Locale.FRENCH;
  private String message = "This is a test";
  private String frenchMessage = "Ceci est un test";
  private String classMessage = "Class - This is a test";
  private String frenchClassMessage = "Class - Ceci est un test";

  @Before
  public void beforeTest() {
    messageResourcesComponent = new TestMessageResourcesComponent();
  }

  @Test
  public void getResources() {
    messageResourcesComponent.setLocale(locale);

    MessageResource messageResource = messageResourcesComponent.getResources();

    assertEquals(message, messageResource.message("message"));
  }

  @Test
  public void getResources_French() {
    messageResourcesComponent.setLocale(frenchLocale);

    MessageResource messageResource = messageResourcesComponent.getResources();

    assertEquals(frenchMessage, messageResource.message("message"));
  }

  @Test
  public void getResources_Locale() {
    MessageResource messageResource = messageResourcesComponent.getResources(locale);

    assertEquals(message, messageResource.message("message"));
  }

  @Test
  public void getResources_Locale_French() {
    MessageResource messageResource = messageResourcesComponent.getResources(frenchLocale);

    assertEquals(frenchMessage, messageResource.message("message"));
  }

  @Test
  public void getResources_Class() {
    messageResourcesComponent.setLocale(locale);

    MessageResource messageResource =
        messageResourcesComponent.getResources(MessageResourcesComponentTest.class);

    assertEquals(classMessage, messageResource.message("message"));
  }

  @Test
  public void getResources_Class_French() {
    messageResourcesComponent.setLocale(frenchLocale);

    MessageResource messageResource =
        messageResourcesComponent.getResources(MessageResourcesComponentTest.class);

    assertEquals(frenchClassMessage, messageResource.message("message"));
  }

  @Test
  public void getResources_Class_Locale() {
    MessageResource messageResource =
        messageResourcesComponent.getResources(MessageResourcesComponentTest.class, locale);

    assertEquals(classMessage, messageResource.message("message"));
  }

  @Test
  public void getResources_Class_Locale_French() {
    MessageResource messageResource =
        messageResourcesComponent.getResources(MessageResourcesComponentTest.class, frenchLocale);

    assertEquals(frenchClassMessage, messageResource.message("message"));
  }

  private static class TestMessageResourcesComponent extends TextField
      implements MessageResourcesComponent {
    private static final long serialVersionUID = 1640742296797655653L;
  }
}
