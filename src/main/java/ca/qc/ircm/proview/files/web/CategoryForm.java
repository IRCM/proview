package ca.qc.ircm.proview.files.web;

import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.ui.CustomComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Form containing one category of guidelines.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CategoryForm extends CustomComponent implements BaseComponent {
  private static final long serialVersionUID = -2957789867158175076L;
  protected CategoryFormDesign design = new CategoryFormDesign();
  @Inject
  private transient CategoryFormPresenter presenter;
  private Category category;

  protected CategoryForm() {
  }

  protected CategoryForm(CategoryFormPresenter presenter) {
    this.presenter = presenter;
  }

  /**
   * Initializes view.
   */
  @PostConstruct
  public void init() {
    setCompositionRoot(design);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
    presenter.setValue(category);
  }

  public void setValue(Category category) {
    this.category = category;
    if (isAttached()) {
      presenter.setValue(category);
    }
  }
}
