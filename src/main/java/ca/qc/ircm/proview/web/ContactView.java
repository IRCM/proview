package ca.qc.ircm.proview.web;

import ca.qc.ircm.proview.utils.web.BaseView;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Contact view.
 */
@SpringView(name = ContactView.VIEW_NAME)
public class ContactView extends ContactViewDesign implements BaseView {
  private static final long serialVersionUID = -1067651526935267544L;
  public static final String VIEW_NAME = "contact";
  protected Menu menu = new Menu();
  @Inject
  private ContactViewPresenter presenter;

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
