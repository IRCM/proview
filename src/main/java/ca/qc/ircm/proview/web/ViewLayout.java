package ca.qc.ircm.proview.web;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.security.web.WebSecurityConfiguration;
import ca.qc.ircm.proview.user.web.UsersView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

/**
 * Main layout.
 */
public class ViewLayout extends VerticalLayout
    implements RouterLayout, LocaleChangeObserver, AfterNavigationObserver {
  public static final String HOME = "home";
  public static final String USERS = "users";
  public static final String EXIT_SWITCH_USER = "exitSwitchUser";
  public static final String SIGNOUT = "signout";
  private static final long serialVersionUID = 710800815636494374L;
  private static final Logger logger = LoggerFactory.getLogger(ViewLayout.class);
  protected Tabs tabs = new Tabs();
  protected Tab home = new Tab();
  protected Tab users = new Tab();
  protected Tab exitSwitchUser = new Tab();
  protected Tab signout = new Tab();
  private Map<Tab, String> tabsHref = new HashMap<>();
  private String currentHref;
  @Inject
  private transient AuthorizationService authorizationService;

  protected ViewLayout() {
  }

  protected ViewLayout(AuthorizationService authorizationService) {
    this.authorizationService = authorizationService;
  }

  @PostConstruct
  void init() {
    add(tabs);
    tabs.add(home, users, exitSwitchUser, signout);
    exitSwitchUser
        .setVisible(authorizationService.hasRole(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR));
    tabsHref.put(home, MainView.VIEW_NAME);
    tabsHref.put(users, UsersView.VIEW_NAME);
    tabs.addSelectedChangeListener(e -> selectTab());
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    MessageResource resources = new MessageResource(ViewLayout.class, getLocale());
    home.setLabel(resources.message(HOME));
    users.setLabel(resources.message(USERS));
    exitSwitchUser.setLabel(resources.message(EXIT_SWITCH_USER));
    signout.setLabel(resources.message(SIGNOUT));
  }

  private void selectTab() {
    if (tabs.getSelectedTab() == signout) {
      // Sign-out requires a request to be made outside of Vaadin.
      logger.debug("Redirect to sign out");
      UI.getCurrent().getPage()
          .executeJs("location.assign('" + WebSecurityConfiguration.SIGNOUT_URL + "')");
    } else if (tabs.getSelectedTab() == exitSwitchUser) {
      // Exit switch user requires a request to be made outside of Vaadin.
      logger.debug("Redirect to exit switch user");
      UI.getCurrent().getPage()
          .executeJs("location.assign('" + WebSecurityConfiguration.SWITCH_USER_EXIT_URL + "')");
    } else {
      if (!currentHref.equals(tabsHref.get(tabs.getSelectedTab()))) {
        logger.debug("Navigate to {}", tabsHref.get(tabs.getSelectedTab()));
        UI.getCurrent().navigate(tabsHref.get(tabs.getSelectedTab()));
      }
    }
  }

  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    currentHref = event.getLocation().getFirstSegment();
    Optional<Tab> currentTab = tabsHref.entrySet().stream()
        .filter(e -> e.getValue().equals(currentHref)).map(e -> e.getKey()).findFirst();
    currentTab.ifPresent(tab -> tabs.setSelectedTab(tab));
  }
}
