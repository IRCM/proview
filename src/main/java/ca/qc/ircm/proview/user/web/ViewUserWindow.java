package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.user.User;
import com.vaadin.ui.Panel;
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
public class ViewUserWindow extends Window {
  private static final long serialVersionUID = 9032686080431923743L;
  private ViewUserForm view = new ViewUserForm();
  private Panel panel;
  @Inject
  private ViewUserFormPresenter presenter;

  @PostConstruct
  protected void init() {
    presenter.init(view);
    panel = new Panel();
    setContent(panel);
    panel.setContent(view);
    view.setMargin(true);
    panel.setWidth("30em");
    this.setHeight("41em");
    panel.setHeight("40em");
    presenter.addCancelClickListener(e -> close());
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
