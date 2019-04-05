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
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.text.NormalizedComparator;
import ca.qc.ircm.proview.user.LaboratoryProperties;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilter;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Users presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UsersViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String VALIDATION = "validation";
  public static final String USERS = "users";
  public static final String LABORATORY_NAME = property(LABORATORY, LaboratoryProperties.NAME);
  public static final String ORGANIZATION = property(LABORATORY, LaboratoryProperties.ORGANIZATION);
  public static final String ACTIVE = "active";
  public static final String ADD = "add";
  public static final String SWITCH_USER = "switchUser";
  public static final String SWITCHED = "switched";
  public static final String ALL = "all";
  public static final String EMPTY = "users.empty";
  private static final Logger logger = LoggerFactory.getLogger(UsersViewPresenter.class);
  private UsersView view;
  private UsersViewDesign design;
  private ListDataProvider<User> usersProvider;
  private UserFilter filter;
  @Inject
  private UserService userService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private AuthenticationService authenticationService;
  @Inject
  private Provider<UserWindow> userWindowProvider;
  @Value("${spring.application.name}")
  private String applicationName;

  public UsersViewPresenter() {
  }

  /**
   * Called by view when view is attached.
   *
   * @param view
   *          view
   */
  public void init(UsersView view) {
    this.view = view;
    design = view.design;
    logger.debug("Users view");
    filter = new UserFilter();
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
    design.validation.addStyleName(VALIDATION);
    design.validation.setCaption(resources.message(VALIDATION));
    design.validation.setVisible(showValidation());
    design.validation.addClickListener(e -> validation());
    design.users.addStyleName(USERS);
    prepareUsersGrid();
    design.add.addStyleName(ADD);
    design.add.setCaption(resources.message(ADD));
    design.add.setVisible(authorizationService.hasAdminRole());
    design.add.addClickListener(e -> add());
    design.switchUser.addStyleName(SWITCH_USER);
    design.switchUser.setCaption(resources.message(SWITCH_USER));
    design.switchUser.setVisible(authorizationService.hasAdminRole());
    design.switchUser.addClickListener(e -> switchUser());
  }

  private void prepareUsersGrid() {
    final MessageResource resources = view.getResources();
    design.users.setDataProvider(searchUsers());
    design.users.addItemClickListener(e -> {
      if (e.getMouseEventDetails().isDoubleClick()) {
        viewUser(e.getItem());
      }
    });
    NormalizedComparator comparator = new NormalizedComparator();
    design.users.addColumn(User::getEmail).setId(EMAIL).setCaption(resources.message(EMAIL))
        .setComparator((u1, u2) -> comparator.compare(u1.getEmail(), u2.getEmail()));
    design.users.setFrozenColumnCount(1);
    design.users.addColumn(User::getName).setId(NAME).setCaption(resources.message(NAME));
    design.users.addColumn(user -> user.getLaboratory().getName()).setId(LABORATORY_NAME)
        .setCaption(resources.message(LABORATORY_NAME));
    design.users.addColumn(user -> user.getLaboratory().getOrganization()).setId(ORGANIZATION)
        .setCaption(resources.message(ORGANIZATION));
    design.users.addColumn(user -> activeButton(user), new ComponentRenderer()).setId(ACTIVE)
        .setCaption(resources.message(ACTIVE))
        .setComparator((u1, u2) -> Boolean.compare(u1.isActive(), u2.isActive()));
    design.users.setSelectionMode(SelectionMode.SINGLE);
    design.users.addStyleName(COMPONENTS);
    design.users.sort(EMAIL, SortDirection.ASCENDING);
    HeaderRow filterRow = design.users.appendHeaderRow();
    filterRow.getCell(EMAIL).setComponent(textFilter(e -> {
      filter.emailContains = e.getValue();
      design.users.getDataProvider().refreshAll();
    }, resources));
    filterRow.getCell(NAME).setComponent(textFilter(e -> {
      filter.nameContains = e.getValue();
      design.users.getDataProvider().refreshAll();
    }, resources));
    filterRow.getCell(LABORATORY_NAME).setComponent(textFilter(e -> {
      filter.laboratoryNameContains = e.getValue();
      design.users.getDataProvider().refreshAll();
    }, resources));
    filterRow.getCell(ORGANIZATION).setComponent(textFilter(e -> {
      filter.organizationContains = e.getValue();
      design.users.getDataProvider().refreshAll();
    }, resources));
    filterRow.getCell(ACTIVE).setComponent(comboBoxFilter(e -> {
      filter.active = e.getValue();
      design.users.getDataProvider().refreshAll();
    }, resources, value -> resources.message(property(ACTIVE, value)),
        new Boolean[] { true, false }));
  }

  private Button activeButton(User user) {
    MessageResource resources = view.getResources();
    boolean active = user.isActive();
    Button button = new Button();
    button.addStyleName(ACTIVE);
    button.addStyleName(active ? ValoTheme.BUTTON_FRIENDLY : ValoTheme.BUTTON_DANGER);
    button.setCaption(resources.message(property(ACTIVE, active)));
    button.setIcon(active ? VaadinIcons.EYE : VaadinIcons.EYE_SLASH);
    button.addClickListener(e -> toggleActive(user));
    return button;
  }

  private TextField textFilter(ValueChangeListener<String> listener, MessageResource resources) {
    TextField filter = new TextField();
    filter.addValueChangeListener(listener);
    filter.setWidth("100%");
    filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
    filter.setPlaceholder(resources.message(ALL));
    return filter;
  }

  private <V> ComboBox<V> comboBoxFilter(ValueChangeListener<V> listener, MessageResource resources,
      ItemCaptionGenerator<V> itemCaptionGenerator, V[] values) {
    ComboBox<V> filter = new ComboBox<>();
    filter.setEmptySelectionAllowed(true);
    filter.setEmptySelectionCaption(resources.message(ALL));
    filter.setPlaceholder(resources.message(ALL));
    filter.setTextInputAllowed(false);
    filter.setItems(values);
    filter.setItemCaptionGenerator(itemCaptionGenerator);
    filter.addValueChangeListener(listener);
    filter.setWidth("100%");
    filter.addStyleName("tiny");
    return filter;
  }

  private DataProvider<User, ?> searchUsers() {
    UserFilter filter = new UserFilter();
    filter.valid = true;
    if (!authorizationService.hasAdminRole()) {
      filter.laboratory = authorizationService.getCurrentUser().getLaboratory();
    }
    List<User> users = userService.all(filter);
    usersProvider = DataProvider.ofCollection(users);
    usersProvider.setFilter(p -> this.filter.test(p));
    return usersProvider;
  }

  private boolean showValidation() {
    return userService.hasInvalid(authorizationService.hasAdminRole() ? null
        : authorizationService.getCurrentUser().getLaboratory());
  }

  private void validation() {
    view.navigateTo(ValidateView.VIEW_NAME);
  }

  private void viewUser(User user) {
    UserWindow userWindow = userWindowProvider.get();
    userWindow.center();
    userWindow.setValue(user);
    view.addWindow(userWindow);
  }

  private void toggleActive(User user) {
    if (user.isActive()) {
      userService.deactivate(user);
    } else {
      userService.activate(user);
    }
    user.setActive(!user.isActive());
    design.users.getDataProvider().refreshItem(user);
  }

  private void add() {
    view.navigateTo(RegisterView.VIEW_NAME);
  }

  private void switchUser() {
    MessageResource resources = view.getResources();
    User user = design.users.getSelectedItems().stream().findFirst().orElse(null);
    if (user == null) {
      view.showError(resources.message(EMPTY));
    } else {
      authenticationService.runAs(user);
      view.showTrayNotification(resources.message(SWITCHED, user.getEmail()));
      view.navigateTo(MainView.VIEW_NAME);
    }
  }

  UserFilter getFilter() {
    return filter;
  }
}
