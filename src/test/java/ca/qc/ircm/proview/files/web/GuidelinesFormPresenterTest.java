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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Provider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class GuidelinesFormPresenterTest {
  private GuidelinesFormPresenter presenter;
  @Mock
  private GuidelinesForm view;
  @Mock
  private Provider<CategoryForm> categoryFormProvider;
  @Mock
  private CategoryForm categoryForm;
  @Inject
  private GuidelinesConfiguration guidelinesConfiguration;
  private GuidelinesFormDesign design;
  private Locale locale = Locale.FRENCH;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new GuidelinesFormPresenter(categoryFormProvider, guidelinesConfiguration);
    design = new GuidelinesFormDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(categoryFormProvider.get()).thenReturn(categoryForm);
  }

  @Test
  public void categories() {
    presenter.init(view);

    List<Category> categories = guidelinesConfiguration.categories(locale);

    verify(categoryFormProvider, times(categories.size())).get();
    for (Category category : categories) {
      verify(categoryForm).setValue(category);
    }
    assertEquals(categories.size(), design.categories.getComponentCount());
    for (int i = 0; i < categories.size(); i++) {
      assertEquals(categoryForm, design.categories.getComponent(i));
    }
  }
}
