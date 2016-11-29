package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.utils.web.BaseView;
import ca.qc.ircm.proview.web.Menu;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * User access view.
 */
@SpringView(name = AccessView.VIEW_NAME)
@RolesAllowed({ "ADMIN", "MANAGER" })
public class AccessView extends AccessViewDesign implements BaseView {
  public static final String VIEW_NAME = "user/access";
  private static final long serialVersionUID = -1897739429426168438L;
  @Inject
  private AccessViewPresenter presenter;
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
}
