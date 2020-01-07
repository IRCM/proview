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

import static ca.qc.ircm.proview.files.web.CategoryComponent.CATEGORY;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findChildren;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.files.Guideline;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.flow.component.html.Anchor;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class CategoryComponentTest extends AbstractViewTestCase {
  private CategoryComponent component;
  @Mock
  private Category category;
  private List<Guideline> guidelines = new ArrayList<>();
  @Mock
  private Guideline guideline1;
  @Mock
  private Guideline guideline2;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(category.name()).thenReturn("test category");
    when(category.guidelines()).thenReturn(guidelines);
    guidelines.add(guideline1);
    when(guideline1.name()).thenReturn("test guideline 1");
    when(guideline1.path())
        .thenReturn(Paths.get(System.getProperty("user.home"), "guideline1.pdf"));
    guidelines.add(guideline2);
    when(guideline2.name()).thenReturn("test guideline 2");
    when(guideline2.path())
        .thenReturn(Paths.get(System.getProperty("user.home"), "guideline2.pdf"));
    component = new CategoryComponent(category);
  }

  @Test
  public void styles() {
    assertTrue(component.getClassNames().contains(CATEGORY));
  }

  @Test
  public void labels() {
    assertEquals(category.name(), component.header.getText());
    List<Anchor> anchors = findChildren(component, Anchor.class);
    for (int i = 0; i < category.guidelines().size(); i++) {
      Guideline guideline = category.guidelines().get(i);
      Anchor anchor = anchors.get(i);
      assertEquals(guideline.name(), anchor.getText());
    }
  }

  @Test
  public void hrefs() {
    List<Anchor> anchors = findChildren(component, Anchor.class);
    for (int i = 0; i < category.guidelines().size(); i++) {
      Guideline guideline = category.guidelines().get(i);
      Anchor anchor = anchors.get(i);
      assertEquals(guideline.path().getFileName().toString(),
          Paths.get(anchor.getHref()).getFileName().toString());
    }
  }
}
