package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.submission.web.SubmissionsView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;

/**
 * Main view.
 */
@Route(value = MainView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ USER })
public class MainView extends VerticalLayout implements BeforeEnterObserver {
  public static final String VIEW_NAME = "";
  private static final long serialVersionUID = -4472228116629914718L;

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    event.forwardTo(SubmissionsView.class);
  }
}
