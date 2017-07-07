package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Control view.
 */
@SpringView(name = ControlView.VIEW_NAME)
@RolesAllowed({ "USER" })
public class ControlView extends ControlViewDesign implements BaseView {
  public static final String VIEW_NAME = "sample/control";
  private static final long serialVersionUID = 996822613187620022L;
  @Inject
  protected ControlForm form;
  @Inject
  private transient ControlViewPresenter presenter;

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
