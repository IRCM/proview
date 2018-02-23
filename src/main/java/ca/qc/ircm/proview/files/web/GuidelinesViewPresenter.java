package ca.qc.ircm.proview.files.web;

import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.utils.MessageResource;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Guidelines view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GuidelinesViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  private GuidelinesView view;
  private GuidelinesViewDesign design;
  @Inject
  private Provider<CategoryForm> categoryFormProvider;
  @Inject
  private GuidelinesConfiguration guidelinesConfiguration;
  @Inject
  private String applicationName;

  protected GuidelinesViewPresenter() {
  }

  protected GuidelinesViewPresenter(Provider<CategoryForm> categoryFormProvider,
      GuidelinesConfiguration guidelinesConfiguration, String applicationName) {
    this.categoryFormProvider = categoryFormProvider;
    this.guidelinesConfiguration = guidelinesConfiguration;
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(GuidelinesView view) {
    this.view = view;
    design = view.design;
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
    List<Category> categories = guidelinesConfiguration.categories(view.getLocale());
    categories.forEach(category -> {
      CategoryForm form = categoryFormProvider.get();
      form.setValue(category);
      design.categories.addComponent(form);
    });
  }

  /**
   * Called when view is entered.
   *
   * @param parameters
   *          parameters
   */
  public void enter(String parameters) {
  }
}
