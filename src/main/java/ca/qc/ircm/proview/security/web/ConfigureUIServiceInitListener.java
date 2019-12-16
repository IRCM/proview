package ca.qc.ircm.proview.security.web;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

/**
 * Adds before enter listener to check access to views. Adds the Offline banner.
 */
@SpringComponent
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {
  private static final Logger logger =
      LoggerFactory.getLogger(ConfigureUIServiceInitListener.class);
  private static final long serialVersionUID = -5535854753812022664L;
  private AuthorizationService authorizationService;

  @Autowired
  protected ConfigureUIServiceInitListener(AuthorizationService authorizationService) {
    this.authorizationService = authorizationService;
  }

  @Override
  public void serviceInit(ServiceInitEvent event) {
    event.getSource().addUIInitListener(uiEvent -> {
      final UI ui = uiEvent.getUI();
      //ui.add(new OfflineBanner());
      ui.addBeforeEnterListener(this::beforeEnter);
    });
  }

  /**
   * Reroutes the user if she is not authorized to access the view.
   *
   * @param event
   *          before navigation event with event details
   */
  private void beforeEnter(BeforeEnterEvent event) {
    final boolean accessGranted = authorizationService.isAuthorized(event.getNavigationTarget());
    if (!accessGranted) {
      User user = authorizationService.getCurrentUser();
      logger.debug("Access denied for user {} when accessing view {}",
          user != null ? user.getId() : "anonymous", event.getNavigationTarget());
      if (user != null) {
        event.rerouteToError(new AccessDeniedException("Access denied"), "Access denied");
      } else {
        event.rerouteTo(SigninView.class);
      }
    }
  }
}