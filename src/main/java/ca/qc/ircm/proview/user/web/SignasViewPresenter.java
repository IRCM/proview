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

package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;

import ca.qc.ircm.proview.laboratory.QLaboratory;
import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.user.QUser;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilterBuilder;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Sign as another user view.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SignasViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String USERS_GRID = "users";
  public static final String EMAIL = QUser.user.email.getMetadata().getName();
  public static final String NAME = QUser.user.name.getMetadata().getName();
  public static final String LABORATORY_PREFIX =
      QUser.user.laboratory.getMetadata().getName() + ".";
  public static final String LABORATORY_NAME =
      LABORATORY_PREFIX + QLaboratory.laboratory.name.getMetadata().getName();
  public static final String ORGANIZATION =
      LABORATORY_PREFIX + QLaboratory.laboratory.organization.getMetadata().getName();
  public static final String VIEW = "view";
  public static final String SIGN_AS = "signas";
  public static final String ALL = "all";
  private static final Logger logger = LoggerFactory.getLogger(SignasViewPresenter.class);
  private SignasView view;
  private ListDataProvider<User> usersProvider;
  private UserWebFilter filter;
  @Inject
  private UserService userService;
  @Inject
  private AuthenticationService authenticationService;
  @Inject
  private Provider<UserWindow> userWindowProvider;
  @Value("${spring.application.name}")
  private String applicationName;

  public SignasViewPresenter() {
  }

  protected SignasViewPresenter(UserService userService,
      AuthenticationService authenticationService, Provider<UserWindow> userWindowProvider,
      String applicationName) {
    this.userService = userService;
    this.authenticationService = authenticationService;
    this.userWindowProvider = userWindowProvider;
    this.applicationName = applicationName;
  }

  /**
   * Called by view when view is attached.
   *
   * @param view
   *          view
   */
  public void init(SignasView view) {
    this.view = view;
    logger.debug("Users access view");
    filter = new UserWebFilter(view.getLocale());
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.addStyleName("h1");
    view.headerLabel.setValue(resources.message(HEADER));
    view.usersGrid.addStyleName(USERS_GRID);
    prepareUsersGrid();
  }

  private void prepareUsersGrid() {
    MessageResource resources = view.getResources();
    view.usersGrid.setDataProvider(searchUsers());
    view.usersGrid.addColumn(User::getEmail).setId(EMAIL).setCaption(resources.message(EMAIL));
    view.usersGrid.addColumn(User::getName).setId(NAME).setCaption(resources.message(NAME));
    view.usersGrid.addColumn(user -> user.getLaboratory().getName()).setId(LABORATORY_NAME)
        .setCaption(resources.message(LABORATORY_NAME));
    view.usersGrid.addColumn(user -> user.getLaboratory().getOrganization()).setId(ORGANIZATION)
        .setCaption(resources.message(ORGANIZATION));
    view.usersGrid.setFrozenColumnCount(2);
    view.usersGrid.addColumn(user -> viewButton(user), new ComponentRenderer()).setId(VIEW)
        .setCaption(resources.message(VIEW));
    view.usersGrid.addColumn(user -> signasButton(user), new ComponentRenderer()).setId(SIGN_AS)
        .setCaption(resources.message(SIGN_AS));
    view.usersGrid.setFrozenColumnCount(2);
    view.usersGrid.addStyleName(COMPONENTS);
    view.usersGrid.sort(EMAIL, SortDirection.ASCENDING);
    HeaderRow filterRow = view.usersGrid.appendHeaderRow();
    filterRow.getCell(EMAIL).setComponent(textFilter(e -> {
      filter.setEmailContains(e.getValue());
      view.usersGrid.getDataProvider().refreshAll();
    }, resources));
    filterRow.getCell(NAME).setComponent(textFilter(e -> {
      filter.setNameContains(e.getValue());
      view.usersGrid.getDataProvider().refreshAll();
    }, resources));
    filterRow.getCell(LABORATORY_NAME).setComponent(textFilter(e -> {
      filter.setLaboratoryNameContains(e.getValue());
      view.usersGrid.getDataProvider().refreshAll();
    }, resources));
    filterRow.getCell(ORGANIZATION).setComponent(textFilter(e -> {
      filter.setOrganizationContains(e.getValue());
      view.usersGrid.getDataProvider().refreshAll();
    }, resources));
  }

  private TextField textFilter(ValueChangeListener<String> listener, MessageResource resources) {
    TextField filter = new TextField();
    filter.addValueChangeListener(listener);
    filter.setWidth("100%");
    filter.addStyleName("tiny");
    filter.setPlaceholder(resources.message(ALL));
    return filter;
  }

  private ListDataProvider<User> searchUsers() {
    UserFilterBuilder parameters = new UserFilterBuilder();
    parameters.onlyNonAdmin();
    parameters.onlyActive();
    List<User> users = userService.all(parameters);
    usersProvider = DataProvider.ofCollection(users);
    usersProvider.setFilter(filter);
    return usersProvider;
  }

  private Button viewButton(User user) {
    MessageResource resources = view.getResources();
    Button button = new Button();
    button.addStyleName(VIEW);
    button.setCaption(resources.message(VIEW));
    button.addClickListener(event -> viewUser(user));
    return button;
  }

  private Button signasButton(User user) {
    MessageResource resources = view.getResources();
    Button button = new Button();
    button.addStyleName(SIGN_AS);
    button.setCaption(resources.message(SIGN_AS));
    button.addClickListener(event -> signasUser(user));
    return button;
  }

  private void viewUser(User user) {
    UserWindow userWindow = userWindowProvider.get();
    userWindow.center();
    userWindow.setUser(user);
    view.addWindow(userWindow);
  }

  private void signasUser(User user) {
    MessageResource resources = view.getResources();
    authenticationService.runAs(user);
    view.showTrayNotification(resources.message(SIGN_AS + ".done", user.getEmail()));
    view.navigateTo(MainView.VIEW_NAME);
  }

  UserWebFilter getFilter() {
    return filter;
  }
}
