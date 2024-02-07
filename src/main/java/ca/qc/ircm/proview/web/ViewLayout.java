/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.ADD;
import static ca.qc.ircm.proview.Constants.EDIT;
import static ca.qc.ircm.proview.Constants.PRINT;
import static ca.qc.ircm.proview.text.Strings.styleName;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.SwitchUserService;
import ca.qc.ircm.proview.submission.web.HistoryView;
import ca.qc.ircm.proview.submission.web.PrintSubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.user.web.ProfileView;
import ca.qc.ircm.proview.user.web.UsersView;
import ca.qc.ircm.proview.web.component.UrlComponent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

/**
 * Main layout.
 */
@JsModule("./styles/shared-styles.js")
public class ViewLayout extends VerticalLayout
    implements RouterLayout, LocaleChangeObserver, AfterNavigationObserver, UrlComponent {
  public static final String ID = "view-layout";
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
  private static final long serialVersionUID = 710800815636494374L;
  private static final Logger logger = LoggerFactory.getLogger(ViewLayout.class);
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
  protected Tab print = new Tab();
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
    setHeightFull();
    UI.getCurrent().getPage()
        .retrieveExtendedClientDetails(details -> setWidth(details.getScreenWidth(), Unit.PIXELS));
    setPadding(false);
    setSpacing(false);
    add(tabs);
    tabs.setId(TABS);
    tabs.add(submissions, profile, users, exitSwitchUser, signout, changeLanguage, contact,
        guidelines, add, edit, print, history);
    tabs.setWidthFull();
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
    print.setId(styleName(PRINT, TAB));
    print.setVisible(false);
    history.setId(styleName(HISTORY, TAB));
    history.setVisible(false);
    tabsHref.put(submissions, SubmissionsView.VIEW_NAME);
    tabsHref.put(profile, ProfileView.VIEW_NAME);
    tabsHref.put(users, UsersView.VIEW_NAME);
    tabsHref.put(contact, ContactView.VIEW_NAME);
    tabsHref.put(guidelines, GuidelinesView.VIEW_NAME);
    tabsHref.put(add, SubmissionView.VIEW_NAME);
    tabsHref.put(edit, SubmissionView.VIEW_NAME + "/\\d+");
    tabsHref.put(print, PrintSubmissionView.VIEW_NAME + "/\\d+");
    tabsHref.put(history, HistoryView.VIEW_NAME + "/\\d+");
    tabs.addSelectedChangeListener(e -> selectTab(e.getPreviousTab()));
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    AppResources resources = new AppResources(ViewLayout.class, getLocale());
    submissions.setLabel(resources.message(SUBMISSIONS));
    profile.setLabel(resources.message(PROFILE));
    users.setLabel(resources.message(USERS));
    exitSwitchUser.setLabel(resources.message(EXIT_SWITCH_USER));
    signout.setLabel(resources.message(SIGNOUT));
    changeLanguage.setLabel(resources.message(CHANGE_LANGUAGE));
    contact.setLabel(resources.message(CONTACT));
    guidelines.setLabel(resources.message(GUIDELINES));
    add.setLabel(resources.message(ADD));
    edit.setLabel(resources.message(EDIT));
    print.setLabel(resources.message(PRINT));
    history.setLabel(resources.message(HISTORY));
  }

  private void selectTab(Tab previous) {
    if (tabs.getSelectedTab() == signout) {
      logger.debug("Sign out user {}", authenticatedUser);
      UI.getCurrent().getPage().setLocation(getUrl(MainView.VIEW_NAME));
      CompositeLogoutHandler logoutHandler = new CompositeLogoutHandler(
          new CookieClearingLogoutHandler("remember-me"), new SecurityContextLogoutHandler());
      logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(),
          VaadinServletResponse.getCurrent().getHttpServletResponse(), null);
    } else if (tabs.getSelectedTab() == exitSwitchUser) {
      logger.debug("Exit switch user");
      switchUserService.exitSwitchUser();
      UI.getCurrent().navigate(MainView.class);
    } else if (tabs.getSelectedTab() == changeLanguage) {
      Locale locale = UI.getCurrent().getLocale();
      Locale newLocale = Constants.getLocales().stream().filter(lo -> !lo.equals(locale))
          .findFirst().orElse(Constants.DEFAULT_LOCALE);
      logger.debug("Change locale to {}", newLocale);
      UI.getCurrent().setLocale(newLocale);
      tabs.setSelectedTab(previous);
    } else if (add == tabs.getSelectedTab() || edit == tabs.getSelectedTab()
        || print == tabs.getSelectedTab() || history == tabs.getSelectedTab()) {
      // Do nothing.
    } else {
      if (!currentHref.equals(tabsHref.get(tabs.getSelectedTab()))) {
        logger.debug("Navigate to {}", tabsHref.get(tabs.getSelectedTab()));
        UI.getCurrent().navigate(tabsHref.get(tabs.getSelectedTab()));
      }
    }
  }

  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    add.setVisible(false);
    edit.setVisible(false);
    print.setVisible(false);
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
    });
  }
}
