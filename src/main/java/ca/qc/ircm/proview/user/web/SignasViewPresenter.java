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

import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.LABORATORY;
import static ca.qc.ircm.proview.user.UserProperties.NAME;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;

import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.user.LaboratoryProperties;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilter;
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
import java.text.Collator;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Sign as another user view.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SignasViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String USERS_GRID = "users";
  public static final String LABORATORY_NAME = property(LABORATORY, LaboratoryProperties.NAME);
  public static final String ORGANIZATION = property(LABORATORY, LaboratoryProperties.ORGANIZATION);
  public static final String SIGN_AS = "signas";
  public static final String SIGNED_AS = "signedas";
  public static final String ALL = "all";
  private static final Logger logger = LoggerFactory.getLogger(SignasViewPresenter.class);
  private SignasView view;
  private SignasViewDesign design;
  private ListDataProvider<User> usersProvider;
  private UserWebFilter filter;
  @Inject
  private UserService userService;
  @Inject
  private AuthenticationService authenticationService;
  @Inject
  private UserWindow userWindow;
  @Value("${spring.application.name}")
  private String applicationName;

  public SignasViewPresenter() {
  }

  /**
   * Called by view when view is attached.
   *
   * @param view
   *          view
   */
  public void init(SignasView view) {
    this.view = view;
    design = view.design;
    logger.debug("Sign as user view");
    filter = new UserWebFilter(view.getLocale());
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.headerLabel.addStyleName(HEADER);
    design.headerLabel.setValue(resources.message(HEADER));
    design.usersGrid.addStyleName(USERS_GRID);
    prepareUsersGrid();
  }

  private void prepareUsersGrid() {
    MessageResource resources = view.getResources();
    final Collator collator = Collator.getInstance(view.getLocale());
    design.usersGrid.setDataProvider(searchUsers());
    design.usersGrid.addColumn(user -> viewButton(user), new ComponentRenderer()).setId(EMAIL)
        .setCaption(resources.message(EMAIL))
        .setComparator((u1, u2) -> collator.compare(u1.getEmail(), u2.getEmail()));
    design.usersGrid.addColumn(User::getName).setId(NAME).setCaption(resources.message(NAME));
    design.usersGrid.addColumn(user -> user.getLaboratory().getName()).setId(LABORATORY_NAME)
        .setCaption(resources.message(LABORATORY_NAME));
    design.usersGrid.addColumn(user -> user.getLaboratory().getOrganization()).setId(ORGANIZATION)
        .setCaption(resources.message(ORGANIZATION));
    design.usersGrid.setFrozenColumnCount(2);
    design.usersGrid.addColumn(user -> signasButton(user), new ComponentRenderer()).setId(SIGN_AS)
        .setCaption(resources.message(SIGN_AS)).setSortable(false);
    design.usersGrid.setFrozenColumnCount(2);
    design.usersGrid.addStyleName(COMPONENTS);
    design.usersGrid.sort(EMAIL, SortDirection.ASCENDING);
    HeaderRow filterRow = design.usersGrid.appendHeaderRow();
    filterRow.getCell(EMAIL).setComponent(textFilter(e -> {
      filter.emailContains = e.getValue();
      design.usersGrid.getDataProvider().refreshAll();
    }, resources));
    filterRow.getCell(NAME).setComponent(textFilter(e -> {
      filter.nameContains = e.getValue();
      design.usersGrid.getDataProvider().refreshAll();
    }, resources));
    filterRow.getCell(LABORATORY_NAME).setComponent(textFilter(e -> {
      filter.laboratoryNameContains = e.getValue();
      design.usersGrid.getDataProvider().refreshAll();
    }, resources));
    filterRow.getCell(ORGANIZATION).setComponent(textFilter(e -> {
      filter.organizationContains = e.getValue();
      design.usersGrid.getDataProvider().refreshAll();
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
    UserFilter filter = new UserFilter();
    filter.admin = false;
    filter.active = true;
    List<User> users = userService.all(filter);
    usersProvider = DataProvider.ofCollection(users);
    usersProvider.setFilter(this.filter);
    return usersProvider;
  }

  private Button viewButton(User user) {
    Button button = new Button();
    button.addStyleName(EMAIL);
    button.setCaption(user.getEmail());
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
    userWindow.center();
    if (!userWindow.isAttached()) {
      userWindow.setValue(user);
      view.addWindow(userWindow);
    }
  }

  private void signasUser(User user) {
    MessageResource resources = view.getResources();
    authenticationService.runAs(user);
    view.showTrayNotification(resources.message(SIGNED_AS, user.getEmail()));
    view.navigateTo(MainView.VIEW_NAME);
  }

  UserWebFilter getFilter() {
    return filter;
  }
}
