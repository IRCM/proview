package ca.qc.ircm.proview.digestion.web;

import ca.qc.ircm.proview.web.component.SavedContainersComponent;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Digestion view.
 */
@SpringView(name = DigestionView.VIEW_NAME)
@RolesAllowed({ "ADMIN" })
public class DigestionView extends CustomComponent implements BaseView, SavedContainersComponent {
  public static final String VIEW_NAME = "digestion";
  private static final long serialVersionUID = -9198529755000983897L;
  protected DigestionViewDesign design = new DigestionViewDesign();
  @Inject
  private DigestionViewPresenter presenter;

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

  @Override
  public void enter(ViewChangeEvent event) {
    presenter.enter(event.getParameters());
  }
}
