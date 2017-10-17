package ca.qc.ircm.proview.dilution.web;

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
@SpringView(name = DilutionView.VIEW_NAME)
@RolesAllowed({ "ADMIN" })
public class DilutionView extends CustomComponent implements BaseView, SavedContainersComponent {
  public static final String VIEW_NAME = "dilution";
  private static final long serialVersionUID = 5304010135654041113L;
  protected DilutionViewDesign design = new DilutionViewDesign();
  @Inject
  private DilutionViewPresenter presenter;

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
