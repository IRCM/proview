package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Sample view.
 */
@SpringView(name = SampleView.VIEW_NAME)
@RolesAllowed({ "USER" })
public class SampleView extends SampleViewDesign implements BaseView {
  public static final String VIEW_NAME = "sample";
  private static final long serialVersionUID = -1847741580837872381L;
  @Inject
  protected SampleForm form;
  @Inject
  private transient SampleViewPresenter presenter;

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
