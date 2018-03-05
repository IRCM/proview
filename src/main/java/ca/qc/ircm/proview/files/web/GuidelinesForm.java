package ca.qc.ircm.proview.files.web;

import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.ui.CustomComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Guidelines form.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GuidelinesForm extends CustomComponent implements BaseComponent {
  private static final long serialVersionUID = -2957789867158175076L;
  protected GuidelinesFormDesign design = new GuidelinesFormDesign();
  @Inject
  private transient GuidelinesFormPresenter presenter;

  /**
   * Initializes form.
   */
  @PostConstruct
  public void init() {
    setCompositionRoot(design);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }
}
