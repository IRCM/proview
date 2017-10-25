package ca.qc.ircm.proview.standard.web;

import ca.qc.ircm.proview.web.component.SavedContainersComponent;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Standard addition view.
 */
@SpringView(name = StandardAdditionView.VIEW_NAME)
@RolesAllowed({ "ADMIN" })
public class StandardAdditionView extends CustomComponent
    implements BaseView, SavedContainersComponent {
  public static final String VIEW_NAME = "standardaddition";
  private static final long serialVersionUID = -7132037846809810772L;
  protected StandardAdditionViewDesign design = new StandardAdditionViewDesign();
  @Inject
  private StandardAdditionViewPresenter presenter;

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
