package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;
import static ca.qc.ircm.proview.web.WebConstants.PRINT;

import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.security.web.WebSecurityConfiguration;
import ca.qc.ircm.proview.submission.web.PrintSubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.user.web.UsersView;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import java.util.HashMap;
import java.util.Locale;
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
@HtmlImport("styles/shared-styles.html")
public class ViewLayout extends VerticalLayout
    implements RouterLayout, LocaleChangeObserver, AfterNavigationObserver {
  public static final String ID = "view-layout";
  public static final String SUBMISSIONS = "submissions";
  public static final String USERS = "users";
  public static final String EXIT_SWITCH_USER = "exitSwitchUser";
  public static final String SIGNOUT = "signout";
  public static final String CHANGE_LANGUAGE = "changeLanguage";
  public static final String CONTACT = "contact";
  public static final String GUIDELINES = "guidelines";
  public static final String TAB = "tab";
  private static final long serialVersionUID = 710800815636494374L;
  private static final Logger logger = LoggerFactory.getLogger(ViewLayout.class);
  protected Tabs tabs = new Tabs();
  protected Tab submissions = new Tab();
  protected Tab users = new Tab();
  protected Tab exitSwitchUser = new Tab();
  protected Tab signout = new Tab();
  protected Tab changeLanguage = new Tab();
  protected Tab contact = new Tab();
  protected Tab guidelines = new Tab();
  protected Tab print = new Tab();
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
    setId(ID);
    setSizeFull();
    setPadding(false);
    setSpacing(false);
    add(tabs);
    tabs.add(submissions, users, exitSwitchUser, signout, changeLanguage, contact, guidelines,
        print);
    submissions.setId(styleName(SUBMISSIONS, TAB));
    users.setId(styleName(USERS, TAB));
    users.setVisible(authorizationService.hasAnyRole(MANAGER, ADMIN));
    exitSwitchUser.setId(styleName(EXIT_SWITCH_USER, TAB));
    exitSwitchUser
        .setVisible(authorizationService.hasRole(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR));
    signout.setId(styleName(SIGNOUT, TAB));
    changeLanguage.setId(styleName(CHANGE_LANGUAGE, TAB));
    contact.setId(styleName(CONTACT, TAB));
    guidelines.setId(styleName(GUIDELINES, TAB));
    print.setId(styleName(PRINT, TAB));
    print.setVisible(false);
    tabsHref.put(submissions, SubmissionsView.VIEW_NAME);
    tabsHref.put(users, UsersView.VIEW_NAME);
    tabsHref.put(contact, ContactView.VIEW_NAME);
    tabsHref.put(guidelines, GuidelinesView.VIEW_NAME);
    tabsHref.put(print, PrintSubmissionView.VIEW_NAME);
    tabs.addSelectedChangeListener(e -> selectTab(e.getPreviousTab()));
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    MessageResource resources = new MessageResource(ViewLayout.class, getLocale());
    submissions.setLabel(resources.message(SUBMISSIONS));
    users.setLabel(resources.message(USERS));
    exitSwitchUser.setLabel(resources.message(EXIT_SWITCH_USER));
    signout.setLabel(resources.message(SIGNOUT));
    changeLanguage.setLabel(resources.message(CHANGE_LANGUAGE));
    contact.setLabel(resources.message(CONTACT));
    guidelines.setLabel(resources.message(GUIDELINES));
    print.setLabel(resources.message(PRINT));
  }

  private void selectTab(Tab previous) {
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
    } else if (tabs.getSelectedTab() == changeLanguage) {
      Locale locale = UI.getCurrent().getLocale();
      Locale newLocale = WebConstants.getLocales().stream().filter(lo -> !lo.equals(locale))
          .findFirst().orElse(WebConstants.DEFAULT_LOCALE);
      logger.debug("Change locale to {}", newLocale);
      UI.getCurrent().setLocale(newLocale);
      tabs.setSelectedTab(previous);
    } else {
      if (print != tabs.getSelectedTab()
          && !currentHref.equals(tabsHref.get(tabs.getSelectedTab()))) {
        logger.debug("Navigate to {}", tabsHref.get(tabs.getSelectedTab()));
        UI.getCurrent().navigate(tabsHref.get(tabs.getSelectedTab()));
      }
    }
  }

  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    print.setVisible(false);
    currentHref = event.getLocation().getPath();
    Optional<Tab> currentTab = tabsHref.entrySet().stream()
        .filter(e -> e.getValue().equals(currentHref)).map(e -> e.getKey()).findFirst();
    if (!currentTab.isPresent()) {
      if (tabsHref.get(print).equals(currentHref.replaceFirst("/[^/]*$", ""))) {
        currentTab = Optional.of(print);
      }
    }
    currentTab.ifPresent(tab -> {
      tabs.setSelectedTab(tab);
      tab.setVisible(true);
    });
  }
}
