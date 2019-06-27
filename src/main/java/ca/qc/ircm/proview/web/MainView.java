package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.user.UserRole.USER;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;

/**
 * Main view.
 */
@Route(value = MainView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ USER })
public class MainView extends VerticalLayout {
  public static final String VIEW_NAME = "";
  private static final long serialVersionUID = -4472228116629914718L;
  private H1 header = new H1("Main view");

  public MainView() {
    add(header);
  }
}
