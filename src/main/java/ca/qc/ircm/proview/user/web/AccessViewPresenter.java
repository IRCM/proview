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

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.QLaboratory;
import ca.qc.ircm.proview.user.QUser;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilter;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * User access presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccessViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String USERS_GRID = "users";
  public static final String SELECT = "select";
  public static final String EMAIL = QUser.user.email.getMetadata().getName();
  public static final String NAME = QUser.user.name.getMetadata().getName();
  public static final String LABORATORY_PREFIX =
      QUser.user.laboratory.getMetadata().getName() + ".";
  public static final String LABORATORY_NAME =
      LABORATORY_PREFIX + QLaboratory.laboratory.name.getMetadata().getName();
  public static final String ORGANIZATION =
      LABORATORY_PREFIX + QLaboratory.laboratory.organization.getMetadata().getName();
  public static final String ACTIVE = QUser.user.active.getMetadata().getName();
  public static final String ACTIVATE = "activate";
  public static final String DEACTIVATE = "deactivate";
  public static final String CLEAR = "clear";
  public static final String ALL = "all";
  public static final String HIDE_SELECTION = "hide-selection";
  private static final Logger logger = LoggerFactory.getLogger(AccessViewPresenter.class);
  private AccessView view;
  private Map<User, CheckBox> selectionCheckboxes = new HashMap<>();
  private ListDataProvider<User> usersProvider;
  private UserWebFilter filter;
  @Inject
  private UserService userService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private Provider<UserWindow> userWindowProvider;
  @Value("${spring.application.name}")
  private String applicationName;

  public AccessViewPresenter() {
  }

  protected AccessViewPresenter(UserService userService, AuthorizationService authorizationService,
      Provider<UserWindow> userWindowProvider, String applicationName) {
    this.userService = userService;
    this.authorizationService = authorizationService;
    this.userWindowProvider = userWindowProvider;
    this.applicationName = applicationName;
  }

  /**
   * Called by view when view is attached.
   *
   * @param view
   *          view
   */
  public void init(AccessView view) {
    this.view = view;
    logger.debug("Users access view");
    filter = new UserWebFilter(view.getLocale());
    prepareComponents();
    addFieldListeners();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.addStyleName("h1");
    view.headerLabel.setValue(resources.message(HEADER));
    view.usersGrid.addStyleName(USERS_GRID);
    prepareUsersGrid();
    view.activateButton.addStyleName(ACTIVATE);
    view.activateButton.setCaption(resources.message(ACTIVATE));
    view.deactivateButton.addStyleName(DEACTIVATE);
    view.deactivateButton.setCaption(resources.message(DEACTIVATE));
    view.clearButton.addStyleName(CLEAR);
    view.clearButton.setCaption(resources.message(CLEAR));
  }

  private void prepareUsersGrid() {
    MessageResource resources = view.getResources();
    view.usersGrid.setDataProvider(searchUsers());
    view.usersGrid.addColumn(user -> selectCheckBox(user), new ComponentRenderer()).setId(SELECT)
        .setCaption(resources.message(SELECT)).setWidth(56);
    view.usersGrid.addColumn(user -> viewButton(user), new ComponentRenderer()).setId(EMAIL)
        .setCaption(resources.message(EMAIL));
    view.usersGrid.addColumn(User::getName).setId(NAME).setCaption(resources.message(NAME));
    view.usersGrid.addColumn(user -> user.getLaboratory().getName()).setId(LABORATORY_NAME)
        .setCaption(resources.message(LABORATORY_NAME));
    view.usersGrid.addColumn(user -> user.getLaboratory().getOrganization()).setId(ORGANIZATION)
        .setCaption(resources.message(ORGANIZATION));
    view.usersGrid.setFrozenColumnCount(2);
    view.usersGrid.addColumn(user -> activeLabel(user), new ComponentRenderer()).setId(ACTIVE)
        .setCaption(resources.message(ACTIVE));
    view.usersGrid.setSelectionMode(SelectionMode.MULTI);
    view.usersGrid.addStyleName(HIDE_SELECTION);
    view.usersGrid.addStyleName(COMPONENTS);
    view.usersGrid.sort(EMAIL, SortDirection.ASCENDING);
    HeaderRow filterRow = view.usersGrid.appendHeaderRow();
    filterRow.getCell(EMAIL).setComponent(textFilter(e -> {
      filter.emailContains = Optional.ofNullable(e.getValue());
      view.usersGrid.getDataProvider().refreshAll();
    }, resources));
    filterRow.getCell(NAME).setComponent(textFilter(e -> {
      filter.nameContains = Optional.ofNullable(e.getValue());
      view.usersGrid.getDataProvider().refreshAll();
    }, resources));
    filterRow.getCell(LABORATORY_NAME).setComponent(textFilter(e -> {
      filter.laboratoryNameContains = Optional.ofNullable(e.getValue());
      view.usersGrid.getDataProvider().refreshAll();
    }, resources));
    filterRow.getCell(ORGANIZATION).setComponent(textFilter(e -> {
      filter.organizationContains = Optional.ofNullable(e.getValue());
      view.usersGrid.getDataProvider().refreshAll();
    }, resources));
    filterRow.getCell(ACTIVE).setComponent(comboBoxFilter(e -> {
      filter.active = Optional.ofNullable(e.getValue());
      view.usersGrid.getDataProvider().refreshAll();
    }, resources, value -> resources.message(ACTIVE + "." + value), new Boolean[] { true, false }));
  }

  private CheckBox selectCheckBox(User user) {
    CheckBox checkbox = new CheckBox();
    checkbox.addStyleName(SELECT);
    checkbox.addValueChangeListener(e -> {
      if (checkbox.getValue()) {
        view.usersGrid.select(user);
      } else {
        view.usersGrid.deselect(user);
      }
    });
    selectionCheckboxes.put(user, checkbox);
    checkbox.setValue(view.usersGrid.getSelectedItems().contains(user));
    checkbox.setVisible(!authorizationService.hasManagerRole(user));
    return checkbox;
  }

  private Label activeLabel(User user) {
    MessageResource resources = view.getResources();
    boolean active = user.isActive();
    Label label = new Label();
    label.addStyleName(ACTIVE);
    VaadinIcons icon = active ? VaadinIcons.CHECK : VaadinIcons.CLOSE;
    label.setContentMode(ContentMode.HTML);
    label.setValue(icon.getHtml() + " " + resources.message(ACTIVE + "." + active));
    return label;
  }

  private Button viewButton(User user) {
    Button button = new Button();
    button.addStyleName(EMAIL);
    button.setCaption(user.getEmail());
    button.addClickListener(event -> viewUser(user));
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

  private void addFieldListeners() {
    view.usersGrid.addSelectionListener(e -> {
      Set<User> users = e.getAllSelectedItems();
      for (Map.Entry<User, CheckBox> checkboxEntry : selectionCheckboxes.entrySet()) {
        CheckBox checkbox = checkboxEntry.getValue();
        checkbox.setValue(users.contains(checkboxEntry.getKey()));
      }
    });
    view.activateButton.addClickListener(event -> activateUsers());
    view.deactivateButton.addClickListener(event -> deactivateUsers());
    view.clearButton.addClickListener(event -> clear());
  }

  private DataProvider<User, ?> searchUsers() {
    UserFilter filter = new UserFilter();
    filter.valid = Optional.of(true);
    if (!authorizationService.hasAdminRole()) {
      filter.laboratory = Optional.of(authorizationService.getCurrentUser().getLaboratory());
    }
    List<User> users = userService.all(filter);
    usersProvider = DataProvider.ofCollection(users);
    usersProvider.setFilter(this.filter);
    return usersProvider;
  }

  private void refreshUsers() {
    view.usersGrid.getSelectionModel().deselectAll();
    view.usersGrid.setDataProvider(searchUsers());
    view.usersGrid.setSortOrder(new ArrayList<>(view.usersGrid.getSortOrder()));
  }

  private void viewUser(User user) {
    UserWindow userWindow = userWindowProvider.get();
    userWindow.center();
    userWindow.setUser(user);
    view.addWindow(userWindow);
  }

  private List<User> selectedUsers() {
    return new ArrayList<>(view.usersGrid.getSelectedItems());
  }

  private void activateUsers() {
    List<User> users = selectedUsers();
    if (users.isEmpty()) {
      logger.trace("No users selected for activate");
      final MessageResource resources = view.getResources();
      view.showError(resources.message("users.empty"));
    } else {
      logger.debug("Activate users {}", users);
      userService.activate(users);
      final MessageResource resources = view.getResources();
      StringBuilder emails = new StringBuilder();
      for (int i = 0; i < users.size(); i++) {
        User user = users.get(i);
        emails.append(user.getEmail());
        if (i == users.size() - 2) {
          emails.append(resources.message("userSeparator", 1));
        } else if (i < users.size() - 2) {
          emails.append(resources.message("userSeparator", 0));
        }
      }
      refreshUsers();
      view.showTrayNotification(resources.message(ACTIVATE + ".done", users.size(), emails));
    }
  }

  private void deactivateUsers() {
    List<User> users = selectedUsers();
    if (users.isEmpty()) {
      logger.trace("No users selected for deactivate");
      final MessageResource resources = view.getResources();
      view.showError(resources.message("users.empty"));
    } else {
      logger.debug("Deactivate users {}", users);
      userService.deactivate(users);
      final MessageResource resources = view.getResources();
      StringBuilder emails = new StringBuilder();
      for (int i = 0; i < users.size(); i++) {
        User user = users.get(i);
        emails.append(user.getEmail());
        if (i == users.size() - 2) {
          emails.append(resources.message("userSeparator", 1));
        } else if (i < users.size() - 2) {
          emails.append(resources.message("userSeparator", 0));
        }
      }
      refreshUsers();
      view.showTrayNotification(resources.message(DEACTIVATE + ".done", users.size(), emails));
    }
  }

  private void clear() {
    logger.trace("Clear selected users");
    view.usersGrid.deselectAll();
  }

  UserWebFilter getFilter() {
    return filter;
  }
}
