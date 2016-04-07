package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.user.User;
import com.vaadin.ui.Window;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * View/Update user window.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserWindow extends Window {
  private static final long serialVersionUID = 9032686080431923743L;
  private UpdateUserForm view = new UpdateUserForm();
  @Inject
  private UpdateUserFormPresenter presenter;

  @PostConstruct
  protected void init() {
    presenter.init(view);
    setContent(view);
    setWidth("30em");
  }

  @Override
  public void attach() {
    super.attach();
    setCaption(view.getResources().message("title"));
  }

  public void setUser(User user) {
    presenter.setUser(user);
  }
}
