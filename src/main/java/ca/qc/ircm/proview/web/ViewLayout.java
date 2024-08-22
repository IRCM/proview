package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.ADD;
import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.EDIT;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.text.Strings.styleName;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.SwitchUserService;
import ca.qc.ircm.proview.submission.web.HistoryView;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.user.web.ExitSwitchUserView;
import ca.qc.ircm.proview.user.web.ProfileView;
import ca.qc.ircm.proview.user.web.UsersView;
import ca.qc.ircm.proview.web.component.UrlComponent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.RouterLayout;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

/**
 * Main layout.
 */
@JsModule("./styles/shared-styles.js")
public class ViewLayout extends AppLayout implements RouterLayout, LocaleChangeObserver,
    BeforeLeaveObserver, AfterNavigationObserver, UrlComponent {
  public static final String ID = "view-layout";
  public static final String HEADER = "header";
  public static final String DRAWER_TOGGLE = "drawerToggle";
  public static final String TABS = styleName(ID, "tabs");
  public static final String SUBMISSIONS = "submissions";
  public static final String PROFILE = "profile";
  public static final String USERS = "users";
  public static final String EXIT_SWITCH_USER = "exitSwitchUser";
  public static final String EXIT_SWITCH_USER_FORM = "exitSwitchUserform";
  public static final String SIGNOUT = "signout";
  public static final String CHANGE_LANGUAGE = "changeLanguage";
  public static final String CONTACT = "contact";
  public static final String GUIDELINES = "guidelines";
  public static final String HISTORY = "history";
  public static final String TAB = "tab";
  private static final String MESSAGES_PREFIX = messagePrefix(ViewLayout.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final long serialVersionUID = 710800815636494374L;
  private static final Logger logger = LoggerFactory.getLogger(ViewLayout.class);
  protected H1 applicationName = new H1();
  protected H2 header = new H2();
  protected DrawerToggle drawerToggle = new DrawerToggle();
  protected Tabs tabs = new Tabs();
  protected Tab submissions = new Tab();
  protected Tab profile = new Tab();
  protected Tab users = new Tab();
  protected Tab exitSwitchUser = new Tab();
  protected Tab signout = new Tab();
  protected Tab changeLanguage = new Tab();
  protected Tab contact = new Tab();
  protected Tab guidelines = new Tab();
  protected Tab add = new Tab();
  protected Tab edit = new Tab();
  protected Tab history = new Tab();
  private Map<Tab, String> tabsHref = new HashMap<>();
  private String currentHref;
  @Autowired
  private transient SwitchUserService switchUserService;
  @Autowired
  private transient AuthenticatedUser authenticatedUser;

  protected ViewLayout() {
  }

  protected ViewLayout(SwitchUserService switchUserService, AuthenticatedUser authenticatedUser) {
    this.switchUserService = switchUserService;
    this.authenticatedUser = authenticatedUser;
  }

  @PostConstruct
  void init() {
    setId(ID);
    addToDrawer(applicationName, tabs);
    addToNavbar(drawerToggle, header);
    setPrimarySection(Section.DRAWER);
    applicationName.setId(styleName(APPLICATION_NAME));
    applicationName.getStyle().set("font-size", "var(--lumo-font-size-l)")
        .set("line-height", "var(--lumo-size-l)")
        .set("margin", "var(--lumo-space-s) var(--lumo-space-m)");
    header.setId(styleName(ID, HEADER));
    header.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");
    drawerToggle.setId(DRAWER_TOGGLE);
    tabs.setId(TABS);
    tabs.add(submissions, profile, users, exitSwitchUser, signout, changeLanguage, contact,
        guidelines, add, edit, history);
    tabs.setOrientation(Tabs.Orientation.VERTICAL);
    submissions.setId(styleName(SUBMISSIONS, TAB));
    profile.setId(styleName(PROFILE, TAB));
    users.setId(styleName(USERS, TAB));
    users.setVisible(false);
    exitSwitchUser.setId(styleName(EXIT_SWITCH_USER, TAB));
    exitSwitchUser.setVisible(false);
    signout.setId(styleName(SIGNOUT, TAB));
    changeLanguage.setId(styleName(CHANGE_LANGUAGE, TAB));
    contact.setId(styleName(CONTACT, TAB));
    guidelines.setId(styleName(GUIDELINES, TAB));
    add.setId(styleName(ADD, TAB));
    add.setVisible(false);
    edit.setId(styleName(EDIT, TAB));
    edit.setVisible(false);
    history.setId(styleName(HISTORY, TAB));
    history.setVisible(false);
    tabsHref.put(submissions, SubmissionsView.VIEW_NAME);
    tabsHref.put(profile, ProfileView.VIEW_NAME);
    tabsHref.put(users, UsersView.VIEW_NAME);
    tabsHref.put(exitSwitchUser, ExitSwitchUserView.VIEW_NAME);
    tabsHref.put(signout, SignoutView.VIEW_NAME);
    tabsHref.put(contact, ContactView.VIEW_NAME);
    tabsHref.put(guidelines, GuidelinesView.VIEW_NAME);
    tabsHref.put(add, SubmissionView.VIEW_NAME);
    tabsHref.put(edit, SubmissionView.VIEW_NAME + "/\\d+");
    tabsHref.put(history, HistoryView.VIEW_NAME + "/\\d+");
    tabs.addSelectedChangeListener(e -> selectTab(e.getPreviousTab()));
    setDrawerOpened(false);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    applicationName.setText(getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME));
    submissions.setLabel(getTranslation(MESSAGES_PREFIX + SUBMISSIONS));
    profile.setLabel(getTranslation(MESSAGES_PREFIX + PROFILE));
    users.setLabel(getTranslation(MESSAGES_PREFIX + USERS));
    exitSwitchUser.setLabel(getTranslation(MESSAGES_PREFIX + EXIT_SWITCH_USER));
    signout.setLabel(getTranslation(MESSAGES_PREFIX + SIGNOUT));
    changeLanguage.setLabel(getTranslation(MESSAGES_PREFIX + CHANGE_LANGUAGE));
    contact.setLabel(getTranslation(MESSAGES_PREFIX + CONTACT));
    guidelines.setLabel(getTranslation(MESSAGES_PREFIX + GUIDELINES));
    add.setLabel(getTranslation(MESSAGES_PREFIX + ADD));
    edit.setLabel(getTranslation(MESSAGES_PREFIX + EDIT));
    history.setLabel(getTranslation(MESSAGES_PREFIX + HISTORY));
  }

  private void selectTab(Tab previous) {
    if (tabs.getSelectedTab() == changeLanguage) {
      Locale locale = UI.getCurrent().getLocale();
      Locale newLocale = Constants.getLocales().stream().filter(lo -> !lo.equals(locale))
          .findFirst().orElse(Constants.DEFAULT_LOCALE);
      logger.debug("Change locale to {}", newLocale);
      UI.getCurrent().setLocale(newLocale);
      tabs.setSelectedTab(previous);
    } else if (add == tabs.getSelectedTab() || edit == tabs.getSelectedTab()
        || history == tabs.getSelectedTab()) {
      // Do nothing.
    } else {
      if (!currentHref.equals(tabsHref.get(tabs.getSelectedTab()))) {
        logger.debug("Navigate to {}", tabsHref.get(tabs.getSelectedTab()));
        UI.getCurrent().navigate(tabsHref.get(tabs.getSelectedTab()));
      }
    }
  }

  @Override
  public void beforeLeave(BeforeLeaveEvent event) {
    header.setText("");
  }

  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    add.setVisible(false);
    edit.setVisible(false);
    history.setVisible(false);
    users.setVisible(authenticatedUser.isAuthorized(UsersView.class));
    exitSwitchUser
        .setVisible(authenticatedUser.hasRole(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR));
    currentHref = event.getLocation().getPath();
    Optional<Tab> currentTab = tabsHref.entrySet().stream()
        .filter(e -> Pattern.matches(e.getValue(), currentHref)).map(e -> e.getKey()).findFirst();
    currentTab.ifPresent(tab -> {
      tabs.setSelectedTab(tab);
      tab.setVisible(true);
      if (header.getText().isEmpty()) {
        header.setText(tab.getLabel());
      }
    });
  }

  public String getHeaderText() {
    return header.getText();
  }

  public void setHeaderText(String text) {
    header.setText(text);
  }
}
