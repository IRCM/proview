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

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  private AccessViewDesign design;
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
    design = view.design;
    logger.debug("Users access view");
    filter = new UserWebFilter(view.getLocale());
    prepareComponents();
    addFieldListeners();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.headerLabel.addStyleName(HEADER);
    design.headerLabel.setValue(resources.message(HEADER));
    design.usersGrid.addStyleName(USERS_GRID);
    prepareUsersGrid();
    design.activateButton.addStyleName(ACTIVATE);
    design.activateButton.setCaption(resources.message(ACTIVATE));
    design.deactivateButton.addStyleName(DEACTIVATE);
    design.deactivateButton.setCaption(resources.message(DEACTIVATE));
    design.clearButton.addStyleName(CLEAR);
    design.clearButton.setCaption(resources.message(CLEAR));
  }

  private void prepareUsersGrid() {
    MessageResource resources = view.getResources();
    final Collator collator = Collator.getInstance(view.getLocale());
    design.usersGrid.setDataProvider(searchUsers());
    design.usersGrid.addColumn(user -> selectCheckBox(user), new ComponentRenderer()).setId(SELECT)
        .setCaption(resources.message(SELECT)).setWidth(56).setSortable(false);
    design.usersGrid.addColumn(user -> viewButton(user), new ComponentRenderer()).setId(EMAIL)
        .setCaption(resources.message(EMAIL))
        .setComparator((u1, u2) -> collator.compare(u1.getEmail(), u2.getEmail()));
    design.usersGrid.addColumn(User::getName).setId(NAME).setCaption(resources.message(NAME));
    design.usersGrid.addColumn(user -> user.getLaboratory().getName()).setId(LABORATORY_NAME)
        .setCaption(resources.message(LABORATORY_NAME));
    design.usersGrid.addColumn(user -> user.getLaboratory().getOrganization()).setId(ORGANIZATION)
        .setCaption(resources.message(ORGANIZATION));
    design.usersGrid.setFrozenColumnCount(2);
    design.usersGrid.addColumn(user -> activeLabel(user), new ComponentRenderer()).setId(ACTIVE)
        .setCaption(resources.message(ACTIVE))
        .setComparator((u1, u2) -> Boolean.compare(u1.isActive(), u2.isActive()));
    design.usersGrid.setSelectionMode(SelectionMode.MULTI);
    design.usersGrid.addStyleName(HIDE_SELECTION);
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
    filterRow.getCell(ACTIVE).setComponent(comboBoxFilter(e -> {
      filter.active = e.getValue();
      design.usersGrid.getDataProvider().refreshAll();
    }, resources, value -> resources.message(ACTIVE + "." + value), new Boolean[] { true, false }));
  }

  private CheckBox selectCheckBox(User user) {
    CheckBox checkbox = new CheckBox();
    checkbox.addStyleName(SELECT);
    checkbox.addValueChangeListener(e -> {
      if (checkbox.getValue()) {
        design.usersGrid.select(user);
      } else {
        design.usersGrid.deselect(user);
      }
    });
    selectionCheckboxes.put(user, checkbox);
    checkbox.setValue(design.usersGrid.getSelectedItems().contains(user));
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
    design.usersGrid.addSelectionListener(e -> {
      Set<User> users = e.getAllSelectedItems();
      for (Map.Entry<User, CheckBox> checkboxEntry : selectionCheckboxes.entrySet()) {
        CheckBox checkbox = checkboxEntry.getValue();
        checkbox.setValue(users.contains(checkboxEntry.getKey()));
      }
    });
    design.activateButton.addClickListener(event -> activateUsers());
    design.deactivateButton.addClickListener(event -> deactivateUsers());
    design.clearButton.addClickListener(event -> clear());
  }

  private DataProvider<User, ?> searchUsers() {
    UserFilter filter = new UserFilter();
    filter.valid = true;
    if (!authorizationService.hasAdminRole()) {
      filter.laboratory = authorizationService.getCurrentUser().getLaboratory();
    }
    List<User> users = userService.all(filter);
    usersProvider = DataProvider.ofCollection(users);
    usersProvider.setFilter(this.filter);
    return usersProvider;
  }

  private void refreshUsers() {
    design.usersGrid.getSelectionModel().deselectAll();
    design.usersGrid.setDataProvider(searchUsers());
    design.usersGrid.setSortOrder(new ArrayList<>(design.usersGrid.getSortOrder()));
  }

  private void viewUser(User user) {
    UserWindow userWindow = userWindowProvider.get();
    userWindow.center();
    userWindow.setValue(user);
    view.addWindow(userWindow);
  }

  private List<User> selectedUsers() {
    return new ArrayList<>(design.usersGrid.getSelectedItems());
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
    design.usersGrid.deselectAll();
  }

  UserWebFilter getFilter() {
    return filter;
  }
}
