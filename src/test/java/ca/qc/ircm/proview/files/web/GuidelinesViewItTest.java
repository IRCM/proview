/*
 * Copyright (c) 2018 Institut de recherches cliniques de Montreal (IRCM)
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

package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.files.Guideline;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.proview.security.web.AccessDeniedError;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.ContactView;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.html.testbench.AnchorElement;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithMockUser
public class GuidelinesViewItTest extends GuidelinesViewPageObject {
  @Inject
  private GuidelinesConfiguration guidelinesConfiguration;
  @Value("${spring.application.name}")
  private String applicationName;

  @Test
  @WithAnonymousUser
  public void security_Anonymous() throws Throwable {
    openView(ContactView.VIEW_NAME);
    Locale locale = currentLocale();

    open();

    assertEquals(
        new MessageResource(AccessDeniedError.class, locale).message(TITLE,
            new MessageResource(WebConstants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(GuidelinesView.class).message(TITLE,
        resources(WebConstants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertTrue(optional(() -> header()).isPresent());
    List<Category> categories = guidelinesConfiguration.categories(currentLocale());
    assertEquals(categories.size(), categories().size());
    for (int i = 0; i < categories.size(); i++) {
      Category category = categories.get(i);
      VerticalLayoutElement categoryElement = categories().get(i);
      assertTrue(optional(() -> categoryHeader(categoryElement)).isPresent());
      assertEquals(category.guidelines().size(), categoryGuidelines(categoryElement).size());
    }
  }

  @Test
  @Ignore
  public void download() throws Throwable {
    // TODO program test.
    open();

    Guideline guideline =
        guidelinesConfiguration.categories(currentLocale()).get(0).guidelines().get(0);
    AnchorElement guidelineElement = categoryGuidelines(categories().get(0)).get(0);
    guidelineElement.click();
  }
}
