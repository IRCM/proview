package ca.qc.ircm.proview.solubilisation.web;

import ca.qc.ircm.proview.web.component.SavedContainersComponent;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Solubilisation view.
 */
@SpringView(name = SolubilisationView.VIEW_NAME)
@RolesAllowed({ "ADMIN" })
public class SolubilisationView extends CustomComponent
    implements BaseView, SavedContainersComponent {
  public static final String VIEW_NAME = "solubilisation";
  private static final long serialVersionUID = -7690840457260181052L;
  protected SolubilisationViewDesign design = new SolubilisationViewDesign();
  @Inject
  private transient SolubilisationViewPresenter presenter;

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
