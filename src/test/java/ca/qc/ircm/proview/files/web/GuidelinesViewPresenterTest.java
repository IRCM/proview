package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.files.web.GuidelinesViewPresenter.HEADER;
import static ca.qc.ircm.proview.files.web.GuidelinesViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class GuidelinesViewPresenterTest {
  private GuidelinesViewPresenter presenter;
  @Mock
  private GuidelinesView view;
  @Mock
  private Provider<CategoryForm> categoryFormProvider;
  @Mock
  private CategoryForm categoryForm;
  @Inject
  private GuidelinesConfiguration guidelinesConfiguration;
  @Value("${spring.application.name}")
  private String applicationName;
  private GuidelinesViewDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(GuidelinesView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter =
        new GuidelinesViewPresenter(categoryFormProvider, guidelinesConfiguration, applicationName);
    design = new GuidelinesViewDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    when(categoryFormProvider.get()).thenReturn(categoryForm);
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.header.getStyleName().contains(HEADER));
    assertTrue(design.header.getStyleName().contains(ValoTheme.LABEL_H1));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
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
