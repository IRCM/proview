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

package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.files.web.GuidelinesView.HEADER;
import static ca.qc.ircm.proview.files.web.GuidelinesView.ID;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findChildren;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.files.Guideline;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.proview.test.config.AbstractKaribuTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.google.common.net.UrlEscapers;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link GuidelinesView}.
 */
@NonTransactionalTestAnnotations
public class GuidelinesViewTest extends AbstractKaribuTestCase {
  private GuidelinesView view;
  @Autowired
  private GuidelinesConfiguration guidelinesConfiguration;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(GuidelinesView.class, locale);
  private AppResources generalResources = new AppResources(Constants.class, locale);

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    ui.setLocale(locale);
    view = new GuidelinesView(guidelinesConfiguration);
    view.init();
  }

  @Test
  public void ids() {
    assertEquals(ID, view.getId().orElse(null));
    assertEquals(HEADER, view.header.getId().orElse(null));
  }

  @Test
  public void labels() {
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    List<Category> categories = guidelinesConfiguration.categories(locale);
    List<CategoryComponent> categoryComponents = findChildren(view, CategoryComponent.class);
    for (int i = 0; i < categories.size(); i++) {
      Category category = categories.get(i);
      CategoryComponent categoryComponent = categoryComponents.get(i);
      assertEquals(category.getName(), categoryComponent.header.getText());
      List<Anchor> anchors = findChildren(categoryComponent, Anchor.class);
      for (int j = 0; j < category.getGuidelines().size(); j++) {
        Guideline guideline = category.getGuidelines().get(j);
        Anchor anchor = anchors.get(j);
        assertEquals(guideline.getName(), anchor.getText());
      }
    }
  }

  @Test
  public void localeChange() {
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(GuidelinesView.class, locale);
    ui.setLocale(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    List<Category> categories = guidelinesConfiguration.categories(locale);
    List<CategoryComponent> categoryComponents = findChildren(view, CategoryComponent.class);
    for (int i = 0; i < categories.size(); i++) {
      Category category = categories.get(i);
      CategoryComponent categoryComponent = categoryComponents.get(i);
      assertEquals(category.getName(), categoryComponent.header.getText());
      List<Anchor> anchors = findChildren(categoryComponent, Anchor.class);
      for (int j = 0; j < category.getGuidelines().size(); j++) {
        Guideline guideline = category.getGuidelines().get(j);
        Anchor anchor = anchors.get(j);
        assertEquals(guideline.getName(), anchor.getText());
      }
    }
  }

  @Test
  public void hrefs() throws Throwable {
    view.localeChange(mock(LocaleChangeEvent.class));
    List<Category> categories = guidelinesConfiguration.categories(locale);
    List<CategoryComponent> categoryComponents = findChildren(view, CategoryComponent.class);
    for (int i = 0; i < categories.size(); i++) {
      Category category = categories.get(i);
      CategoryComponent categoryComponent = categoryComponents.get(i);
      List<Anchor> anchors = findChildren(categoryComponent, Anchor.class);
      for (int j = 0; j < category.getGuidelines().size(); j++) {
        Guideline guideline = category.getGuidelines().get(j);
        Anchor anchor = anchors.get(j);
        String guidelinePath =
            UrlEscapers.urlFragmentEscaper().escape(guideline.getPath().getFileName().toString());
        assertEquals(guidelinePath, Paths.get(anchor.getHref()).getFileName().toString());
      }
    }
  }

  @Test
  public void getPageTitle() {
    assertEquals(resources.message(TITLE, generalResources.message(APPLICATION_NAME)),
        view.getPageTitle());
  }
}
