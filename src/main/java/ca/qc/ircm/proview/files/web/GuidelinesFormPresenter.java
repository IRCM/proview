package ca.qc.ircm.proview.files.web;

import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Guidelines form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GuidelinesFormPresenter {
  private GuidelinesForm view;
  private GuidelinesFormDesign design;
  @Inject
  private Provider<CategoryForm> categoryFormProvider;
  @Inject
  private GuidelinesConfiguration guidelinesConfiguration;

  protected GuidelinesFormPresenter() {
  }

  protected GuidelinesFormPresenter(Provider<CategoryForm> categoryFormProvider,
      GuidelinesConfiguration guidelinesConfiguration) {
    this.categoryFormProvider = categoryFormProvider;
    this.guidelinesConfiguration = guidelinesConfiguration;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(GuidelinesForm view) {
    this.view = view;
    design = view.design;
    prepareComponents();
  }

  private void prepareComponents() {
    List<Category> categories = guidelinesConfiguration.categories(view.getLocale());
    categories.forEach(category -> {
      CategoryForm form = categoryFormProvider.get();
      form.setValue(category);
      design.categories.addComponent(form);
    });
  }
}
