package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.text.Strings.styleName;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.SwitchUserService;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.user.web.ExitSwitchUserView;
import ca.qc.ircm.proview.user.web.ProfileView;
import ca.qc.ircm.proview.user.web.UsersView;
import ca.qc.ircm.proview.web.component.UrlComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.RouterLayout;
import jakarta.annotation.PostConstruct;
import java.io.Serial;
import java.util.Locale;
import java.util.Optional;
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
  public static final String SIDE_NAV = styleName(ID, "sidenav");
  public static final String SUBMISSIONS = "submissions";
  public static final String PROFILE = "profile";
  public static final String USERS = "users";
  public static final String EXIT_SWITCH_USER = "exitSwitchUser";
  public static final String EXIT_SWITCH_USER_FORM = "exitSwitchUserform";
  public static final String SIGNOUT = "signout";
  public static final String CHANGE_LANGUAGE = "changeLanguage";
  public static final String CONTACT = "contact";
  public static final String GUIDELINES = "guidelines";
  public static final String NAV = "nav";
  private static final String MESSAGES_PREFIX = messagePrefix(ViewLayout.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @Serial
  private static final long serialVersionUID = 710800815636494374L;
  private static final Logger logger = LoggerFactory.getLogger(ViewLayout.class);
  protected H1 applicationName = new H1();
  protected H2 header = new H2();
  protected DrawerToggle drawerToggle = new DrawerToggle();
  protected SideNav sideNav = new SideNav();
  protected SideNavItem submissions;
  protected SideNavItem profile;
  protected SideNavItem users;
  protected SideNavItem exitSwitchUser;
  protected SideNavItem signout;
  protected SideNavItem contact;
  protected SideNavItem guidelines;
  protected Button changeLanguage = new Button();
  private transient SwitchUserService switchUserService;
  private transient AuthenticatedUser authenticatedUser;

  @Autowired
  protected ViewLayout(SwitchUserService switchUserService, AuthenticatedUser authenticatedUser) {
    this.switchUserService = switchUserService;
    this.authenticatedUser = authenticatedUser;
  }

  @PostConstruct
  void init() {
    setId(ID);
    addToDrawer(applicationName, sideNav);
    addToNavbar(drawerToggle, header, changeLanguage);
    setPrimarySection(Section.DRAWER);
    applicationName.setId(styleName(APPLICATION_NAME));
    applicationName.getStyle().set("font-size", "var(--lumo-font-size-l)")
        .set("line-height", "var(--lumo-size-l)")
        .set("margin", "var(--lumo-space-s) var(--lumo-space-m)");
    header.setId(styleName(ID, HEADER));
    header.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");
    header.setWidthFull();
    drawerToggle.setId(DRAWER_TOGGLE);
    sideNav.setId(SIDE_NAV);
    submissions = new SideNavItem("Submissions", SubmissionsView.class, VaadinIcon.FLASK.create());
    submissions.setId(styleName(SUBMISSIONS, NAV));
    profile = new SideNavItem("Profile", ProfileView.class, VaadinIcon.USER.create());
    profile.setId(styleName(PROFILE, NAV));
    users = new SideNavItem("Users", UsersView.class, VaadinIcon.GROUP.create());
    users.setId(styleName(USERS, NAV));
    users.setVisible(false);
    exitSwitchUser = new SideNavItem("Exit switch user", ExitSwitchUserView.class,
        VaadinIcon.LEVEL_LEFT.create());
    exitSwitchUser.setId(styleName(EXIT_SWITCH_USER, NAV));
    exitSwitchUser.setVisible(false);
    signout = new SideNavItem("Signout", SignoutView.class, VaadinIcon.SIGN_OUT.create());
    signout.setId(styleName(SIGNOUT, NAV));
    contact = new SideNavItem("Contact", ContactView.class, VaadinIcon.ENVELOPE.create());
    contact.setId(styleName(CONTACT, NAV));
    guidelines = new SideNavItem("Guidelines", GuidelinesView.class, VaadinIcon.BOOK.create());
    guidelines.setId(styleName(GUIDELINES, NAV));
    sideNav.addItem(submissions, profile, users, exitSwitchUser, signout, contact, guidelines);
    changeLanguage.setId(CHANGE_LANGUAGE);
    changeLanguage.getStyle().set("margin", "0 var(--lumo-space-m)");
    changeLanguage.setIcon(VaadinIcon.GLOBE.create());
    changeLanguage.addClickListener(e -> changeLanguage());
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
    contact.setLabel(getTranslation(MESSAGES_PREFIX + CONTACT));
    guidelines.setLabel(getTranslation(MESSAGES_PREFIX + GUIDELINES));
    changeLanguage.setText(getTranslation(MESSAGES_PREFIX + CHANGE_LANGUAGE));
  }

  private void changeLanguage() {
    Locale locale = UI.getCurrent().getLocale();
    Locale newLocale = Constants.getLocales().stream().filter(lo -> !lo.equals(locale)).findFirst()
        .orElse(Constants.DEFAULT_LOCALE);
    logger.debug("Change locale to {}", newLocale);
    UI.getCurrent().setLocale(newLocale);
  }

  @Override
  public void beforeLeave(BeforeLeaveEvent event) {
    header.setText("");
  }

  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    users.setVisible(authenticatedUser.isAuthorized(UsersView.class));
    exitSwitchUser
        .setVisible(authenticatedUser.hasRole(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR));
    Optional<SideNavItem> currentNav = selectedSideNavItem();
    currentNav.ifPresent(item -> {
      if (header.getText().isEmpty()) {
        header.setText(item.getLabel());
      }
    });
  }

  Optional<SideNavItem> selectedSideNavItem() {
    Component view = UI.getCurrent().getCurrentView();
    if (view instanceof SubmissionsView) {
      return Optional.of(submissions);
    } else if (view instanceof ProfileView) {
      return Optional.of(profile);
    } else if (view instanceof UsersView) {
      return Optional.of(users);
    } else if (view instanceof ExitSwitchUserView) {
      return Optional.of(exitSwitchUser);
    } else if (view instanceof SignoutView) {
      return Optional.of(signout);
    } else if (view instanceof ContactView) {
      return Optional.of(contact);
    } else if (view instanceof GuidelinesView) {
      return Optional.of(guidelines);
    } else {
      return Optional.empty();
    }
  }

  public String getHeaderText() {
    return header.getText();
  }

  public void setHeaderText(String text) {
    header.setText(text);
  }
}
