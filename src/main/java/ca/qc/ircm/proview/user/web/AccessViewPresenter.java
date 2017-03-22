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
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.QUser;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilterBuilder;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.v7.filter.FilterEqualsChangeListener;
import ca.qc.ircm.proview.web.v7.filter.FilterTextChangeListener;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.sort.SortOrder;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.data.util.filter.UnsupportedFilterException;
import com.vaadin.v7.ui.Grid.HeaderCell;
import com.vaadin.v7.ui.Grid.HeaderRow;
import com.vaadin.v7.ui.Grid.SelectionMode;
import de.datenhahn.vaadin.componentrenderer.ComponentCellKeyExtension;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
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
  public static final String VIEW = "view";
  public static final String ACTIVATE = "activate";
  public static final String DEACTIVATE = "deactivate";
  public static final String CLEAR = "clear";
  public static final String ALL = "all";
  public static final String HIDE_SELECTION = "hide-selection";
  public static final Object NULL_ID = -1;
  private static final String[] COLUMNS =
      { SELECT, EMAIL, NAME, LABORATORY_NAME, ORGANIZATION, ACTIVE, VIEW };
  private static final Logger logger = LoggerFactory.getLogger(AccessViewPresenter.class);
  private AccessView view;
  private BeanItemContainer<User> container;
  private GeneratedPropertyContainer gridContainer;
  private Map<Object, CheckBox> selectionCheckboxes = new HashMap<>();
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

  @SuppressWarnings("serial")
  private void prepareUsersGrid() {
    MessageResource resources = view.getResources();
    container = new BeanItemContainer<>(User.class, searchUsers());
    container.addNestedContainerProperty(LABORATORY_NAME);
    container.addNestedContainerProperty(ORGANIZATION);
    gridContainer = new GeneratedPropertyContainer(container);
    gridContainer.addGeneratedProperty(SELECT, new PropertyValueGenerator<CheckBox>() {
      @Override
      public CheckBox getValue(Item item, Object itemId, Object propertyId) {
        User user = (User) itemId;
        CheckBox checkbox = new CheckBox();
        checkbox.addValueChangeListener(e -> {
          if (checkbox.getValue()) {
            view.usersGrid.select(itemId);
          } else {
            view.usersGrid.deselect(itemId);
          }
        });
        checkbox.addAttachListener(e -> selectionCheckboxes.put(itemId, checkbox));
        checkbox.setValue(view.usersGrid.getSelectedRows().contains(itemId));
        checkbox.setVisible(!authorizationService.hasManagerRole(user));
        return checkbox;
      }

      @Override
      public Class<CheckBox> getType() {
        return CheckBox.class;
      }
    });
    gridContainer.addGeneratedProperty(ACTIVE, new PropertyValueGenerator<Label>() {
      @Override
      public Label getValue(Item item, Object itemId, Object propertyId) {
        MessageResource resources = view.getResources();
        User user = (User) itemId;
        boolean active = user.isActive();
        Label label = new Label();
        VaadinIcons icon = active ? VaadinIcons.CHECK : VaadinIcons.CLOSE;
        label.setContentMode(ContentMode.HTML);
        label.setValue(icon.getHtml() + " " + resources.message(ACTIVE + "." + active));
        return label;
      }

      @Override
      public Class<Label> getType() {
        return Label.class;
      }

      @Override
      public SortOrder[] getSortProperties(SortOrder order) {
        return new SortOrder[] {
            new SortOrder(order.getPropertyId(), order.getDirection().getOpposite()) };
      }

      @Override
      public Filter modifyFilter(Filter filter) throws UnsupportedFilterException {
        return filter;
      }
    });
    gridContainer.addGeneratedProperty(VIEW, new PropertyValueGenerator<Button>() {
      @Override
      public Button getValue(Item item, Object itemId, Object propertyId) {
        MessageResource resources = view.getResources();
        User user = (User) itemId;
        Button button = new Button();
        button.setCaption(resources.message(VIEW));
        button.addClickListener(event -> viewUser(user));
        return button;
      }

      @Override
      public Class<Button> getType() {
        return Button.class;
      }
    });
    ComponentCellKeyExtension.extend(view.usersGrid);
    view.usersGrid.setContainerDataSource(gridContainer);
    view.usersGrid.setColumns((Object[]) COLUMNS);
    view.usersGrid.setFrozenColumnCount(2);
    view.usersGrid.getColumn(SELECT).setWidth(56);
    view.usersGrid.getColumn(SELECT).setRenderer(new ComponentRenderer());
    view.usersGrid.getColumn(ACTIVE).setRenderer(new ComponentRenderer());
    view.usersGrid.getColumn(VIEW).setRenderer(new ComponentRenderer());
    view.usersGrid.setSelectionMode(SelectionMode.MULTI);
    view.usersGrid.addStyleName(HIDE_SELECTION);
    view.usersGrid.addStyleName(COMPONENTS);
    view.usersGrid.sort(EMAIL, SortDirection.ASCENDING);
    for (String propertyId : COLUMNS) {
      view.usersGrid.getColumn(propertyId).setHeaderCaption(resources.message(propertyId));
    }
    HeaderRow filterRow = view.usersGrid.appendHeaderRow();
    for (String propertyId : COLUMNS) {
      HeaderCell cell = filterRow.getCell(propertyId);
      if (propertyId.equals(EMAIL)) {
        cell.setComponent(createFilterTextField(propertyId, resources));
      } else if (propertyId.equals(NAME)) {
        cell.setComponent(createFilterTextField(propertyId, resources));
      } else if (propertyId.equals(LABORATORY_NAME)) {
        cell.setComponent(createFilterTextField(propertyId, resources));
      } else if (propertyId.equals(ORGANIZATION)) {
        cell.setComponent(createFilterTextField(propertyId, resources));
      } else if (propertyId.equals(ACTIVE)) {
        Boolean[] values = new Boolean[] { true, false };
        ComboBox<Boolean> filter = createFilterComboBox(propertyId, resources, values);
        filter.addValueChangeListener(
            new FilterEqualsChangeListener(gridContainer, propertyId, NULL_ID));
        filter.setItemCaptionGenerator(value -> resources.message(ACTIVE + "." + value));
        cell.setComponent(filter);
      }
    }
  }

  private TextField createFilterTextField(Object propertyId, MessageResource resources) {
    TextField filter = new TextField();
    filter.addValueChangeListener(
        new FilterTextChangeListener(gridContainer, propertyId, true, false));
    filter.setWidth("100%");
    filter.addStyleName("tiny");
    filter.setPlaceholder(resources.message(ALL));
    return filter;
  }

  private <V> ComboBox<V> createFilterComboBox(Object propertyId, MessageResource resources,
      V[] values) {
    ComboBox<V> filter = new ComboBox<>();
    filter.setTextInputAllowed(false);
    filter.setEmptySelectionAllowed(true);
    filter.setEmptySelectionCaption(resources.message(ALL));
    filter.setPlaceholder(resources.message(ALL));
    filter.setItems(values);
    filter.setSelectedItem(null);
    filter.setWidth("100%");
    filter.addStyleName("tiny");
    return filter;
  }

  private void addFieldListeners() {
    view.usersGrid.addSelectionListener(e -> {
      Set<Object> itemIds = e.getSelected();
      for (Map.Entry<Object, CheckBox> checkboxEntry : selectionCheckboxes.entrySet()) {
        CheckBox checkbox = checkboxEntry.getValue();
        checkbox.setValue(itemIds.contains(checkboxEntry.getKey()));
      }
    });
    view.activateButton.addClickListener(event -> activateUsers());
    view.deactivateButton.addClickListener(event -> deactivateUsers());
    view.clearButton.addClickListener(event -> clear());
  }

  private List<User> searchUsers() {
    UserFilterBuilder parameters = new UserFilterBuilder();
    parameters.onlyValid();
    if (!authorizationService.hasAdminRole()) {
      parameters.inLaboratory(authorizationService.getCurrentUser().getLaboratory());
    }
    return userService.all(parameters);
  }

  private void refresh() {
    view.usersGrid.getSelectionModel().reset();
    container.removeAllItems();
    container.addAll(searchUsers());
    view.usersGrid.setSortOrder(new ArrayList<>(view.usersGrid.getSortOrder()));
  }

  private void viewUser(User user) {
    UserWindow userWindow = userWindowProvider.get();
    userWindow.center();
    userWindow.setUser(user);
    view.addWindow(userWindow);
  }

  private List<User> selectedUsers() {
    Collection<Object> ids = view.usersGrid.getSelectedRows();
    List<User> users = new ArrayList<>();
    for (Object id : ids) {
      users.add((User) id);
    }
    return users;
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
      refresh();
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
      refresh();
      view.showTrayNotification(resources.message(DEACTIVATE + ".done", users.size(), emails));
    }
  }

  private void clear() {
    logger.trace("Clear selected users");
    view.usersGrid.deselectAll();
  }

  public static String[] getColumns() {
    return COLUMNS.clone();
  }
}
