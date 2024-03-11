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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.files.Guideline;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link CategoryComponent}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class CategoryComponentTest extends SpringUIUnitTest {
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
  @BeforeEach
  public void beforeTest() {
    when(category.getName()).thenReturn("test category");
    when(category.getGuidelines()).thenReturn(guidelines);
    guidelines.add(guideline1);
    when(guideline1.getName()).thenReturn("test guideline 1");
    when(guideline1.getPath())
        .thenReturn(Paths.get(System.getProperty("user.home"), "guideline1.pdf"));
    guidelines.add(guideline2);
    when(guideline2.getName()).thenReturn("test guideline 2");
    when(guideline2.getPath())
        .thenReturn(Paths.get(System.getProperty("user.home"), "guideline2.pdf"));
    component = new CategoryComponent(category);
  }

  @Test
  public void styles() {
    assertTrue(component.hasClassName(CATEGORY));
  }

  @Test
  public void labels() {
    assertEquals(category.getName(), component.header.getText());
    List<Anchor> anchors = findChildren(component, Anchor.class);
    for (int i = 0; i < category.getGuidelines().size(); i++) {
      Guideline guideline = category.getGuidelines().get(i);
      Anchor anchor = anchors.get(i);
      assertEquals(guideline.getName(), anchor.getText());
    }
  }

  @Test
  public void hrefs() {
    List<Anchor> anchors = findChildren(component, Anchor.class);
    for (int i = 0; i < category.getGuidelines().size(); i++) {
      Guideline guideline = category.getGuidelines().get(i);
      Anchor anchor = anchors.get(i);
      assertEquals(guideline.getPath().getFileName().toString(),
          Paths.get(anchor.getHref()).getFileName().toString());
    }
  }
}
