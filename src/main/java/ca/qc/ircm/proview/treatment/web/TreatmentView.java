package ca.qc.ircm.proview.treatment.web;

import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.component.SavedContainersComponent;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Treatment view.
 */
@SpringView(name = TreatmentView.VIEW_NAME)
@RolesAllowed({ UserRole.ADMIN })
public class TreatmentView extends CustomComponent implements BaseView, SavedContainersComponent {
  public static final String VIEW_NAME = "treatment";
  private static final long serialVersionUID = -1530151615569179054L;
  protected TreatmentViewDesign design = new TreatmentViewDesign();
  @Inject
  private transient TreatmentViewPresenter presenter;

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
