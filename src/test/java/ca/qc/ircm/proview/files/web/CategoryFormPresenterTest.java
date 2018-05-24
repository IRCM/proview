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

import static ca.qc.ircm.proview.files.web.CategoryFormPresenter.CATEGORY;
import static ca.qc.ircm.proview.files.web.CategoryFormPresenter.GUIDELINE;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.SearchUtils.findInstanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.files.Guideline;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Button;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class CategoryFormPresenterTest {
  private CategoryFormPresenter presenter;
  @Mock
  private CategoryForm view;
  @Mock
  private Category category;
  @Mock
  private Guideline guideline1;
  @Mock
  private Guideline guideline2;
  private CategoryFormDesign design = new CategoryFormDesign();
  private Locale locale = Locale.FRENCH;
  private String categoryName = "Protocoles";
  private String guidelineName1 = "Immunoprecipitation";
  private Path guidelinePath1 = Paths.get("Immunoprecipitation.pdf");
  private String guidelineName2 = "Lyse des cellules";
  private Path guidelinePath2 = Paths.get("lyse_cellules.pdf");

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new CategoryFormPresenter();
    design = new CategoryFormDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(category.name()).thenReturn(categoryName);
    List<Guideline> guidelines = Arrays.asList(guideline1, guideline2);
    when(category.guidelines()).thenReturn(guidelines);
    when(guideline1.name()).thenReturn(guidelineName1);
    when(guideline1.path()).thenReturn(guidelinePath1);
    when(guideline2.name()).thenReturn(guidelineName2);
    when(guideline2.path()).thenReturn(guidelinePath2);
  }

  private List<Button> guidelineButtons() {
    return IntStream.range(0, design.guidelines.getComponentCount())
        .mapToObj(i -> (Button) design.guidelines.getComponent(i)).collect(Collectors.toList());
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.setValue(category);

    assertTrue(design.category.getStyleName().contains(CATEGORY));
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.setValue(category);

    assertEquals(categoryName, design.category.getCaption());
  }

  @Test
  public void guidelines() {
    presenter.init(view);
    presenter.setValue(category);

    assertEquals(2, design.guidelines.getComponentCount());
    List<Button> guidelines = guidelineButtons();
    Button guideline = guidelines.get(0);
    assertTrue(guideline.getStyleName().contains(GUIDELINE));
    assertEquals(guidelineName1, guideline.getCaption());
    assertTrue(containsInstanceOf(guideline.getExtensions(), FileDownloader.class));
    FileDownloader fileDownloader =
        findInstanceOf(guideline.getExtensions(), FileDownloader.class).get();
    assertTrue(fileDownloader.getFileDownloadResource() instanceof FileResource);
    FileResource fileResource = (FileResource) fileDownloader.getFileDownloadResource();
    assertEquals(guidelinePath1.toFile(), fileResource.getSourceFile());
    guideline = guidelines.get(1);
    assertTrue(guideline.getStyleName().contains(GUIDELINE));
    assertEquals(guidelineName2, guideline.getCaption());
    assertTrue(containsInstanceOf(guideline.getExtensions(), FileDownloader.class));
    fileDownloader = findInstanceOf(guideline.getExtensions(), FileDownloader.class).get();
    assertTrue(fileDownloader.getFileDownloadResource() instanceof FileResource);
    fileResource = (FileResource) fileDownloader.getFileDownloadResource();
    assertEquals(guidelinePath2.toFile(), fileResource.getSourceFile());
  }

  @Test
  public void guidelines_NullCategory() {
    presenter.init(view);
    presenter.setValue(null);

    verify(view).setVisible(false);
  }
}
