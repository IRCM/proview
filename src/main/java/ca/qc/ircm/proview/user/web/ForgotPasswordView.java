package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.utils.web.BaseView;
import ca.qc.ircm.proview.web.Menu;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Forgot password view.
 */
@SpringView(name = ForgotPasswordView.VIEW_NAME)
public class ForgotPasswordView extends ForgotPasswordViewDesign implements BaseView {
  public static final String VIEW_NAME = "user/forgotpassword";
  private static final long serialVersionUID = 164307263615957137L;
  @Inject
  private ForgotPasswordViewPresenter presenter;
  protected Menu menu = new Menu();

  @PostConstruct
  public void init() {
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
