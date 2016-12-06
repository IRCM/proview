package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.utils.web.BaseView;
import ca.qc.ircm.proview.web.Menu;
import ca.qc.ircm.proview.web.SavedSubmissionsComponent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Updates sample statuses.
 */
@SpringView(name = SampleStatusView.VIEW_NAME)
@RolesAllowed({ "ADMIN" })
public class SampleStatusView extends SampleStatusViewDesign
    implements BaseView, SavedSubmissionsComponent {
  private static final long serialVersionUID = -2790503384190960260L;
  public static final String VIEW_NAME = "samples/status";
  protected Menu menu = new Menu();
  @Inject
  private SampleStatusViewPresenter presenter;

  @PostConstruct
  public void ini() {
    menuLayout.addComponent(menu);
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
