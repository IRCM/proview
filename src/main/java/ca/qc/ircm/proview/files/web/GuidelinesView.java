package ca.qc.ircm.proview.files.web;

import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Guidelines view.
 */
@SpringView(name = GuidelinesView.VIEW_NAME)
@RolesAllowed({ "USER" })
public class GuidelinesView extends CustomComponent implements BaseView {
  public static final String VIEW_NAME = "guidelines";
  private static final long serialVersionUID = -2957789867158175076L;
  protected GuidelinesViewDesign design = new GuidelinesViewDesign();
  @Inject
  private transient GuidelinesViewPresenter presenter;

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
  }
}
