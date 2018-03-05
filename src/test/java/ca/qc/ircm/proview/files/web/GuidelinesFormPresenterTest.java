package ca.qc.ircm.proview.files.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;

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
