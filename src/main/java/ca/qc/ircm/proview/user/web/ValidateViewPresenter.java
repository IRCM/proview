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
import ca.qc.ircm.proview.web.HomeWebContext;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.ui.Grid;
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
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Validate users presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ValidateViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String USERS_GRID = "usersGrid";
  public static final String EMAIL = QUser.user.email.getMetadata().getName();
  public static final String NAME = QUser.user.name.getMetadata().getName();
  public static final String LABORATORY_PREFIX =
      QUser.user.laboratory.getMetadata().getName() + ".";
  public static final String LABORATORY_NAME =
      LABORATORY_PREFIX + QLaboratory.laboratory.name.getMetadata().getName();
  public static final String ORGANIZATION =
      LABORATORY_PREFIX + QLaboratory.laboratory.organization.getMetadata().getName();
  public static final String VIEW = "viewUser";
  public static final String VALIDATE = "validateUser";
  public static final String VALIDATE_SELECTED_BUTTON = "validateSelected";
  private static final String[] COLUMNS =
      { EMAIL, NAME, LABORATORY_NAME, ORGANIZATION, VIEW, VALIDATE };
  private static final Logger logger = LoggerFactory.getLogger(ValidateViewPresenter.class);
  private ValidateView view;
  private BeanItemContainer<User> container;
  private GeneratedPropertyContainer gridContainer;
  @Inject
  private UserService userService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private Provider<UserWindow> userWindowProvider;
  @Value("${spring.application.name}")
  private String applicationName;

  public ValidateViewPresenter() {
  }

  protected ValidateViewPresenter(UserService userService,
      AuthorizationService authorizationService, Provider<UserWindow> userWindowProvider,
      String applicationName) {
    this.userService = userService;
    this.authorizationService = authorizationService;
    this.userWindowProvider = userWindowProvider;
    this.applicationName = applicationName;
  }

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(ValidateView view) {
    logger.debug("Validate users view");
    this.view = view;
    prepareComponents();
    addFieldListeners();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.addStyleName(ValoTheme.LABEL_H1);
    view.headerLabel.setValue(resources.message(HEADER));
    view.usersGrid.addStyleName(USERS_GRID);
    prepareUsersGrid();
    view.validateSelectedButton.addStyleName(VALIDATE_SELECTED_BUTTON);
    view.validateSelectedButton.setCaption(resources.message(VALIDATE_SELECTED_BUTTON));
  }

  @SuppressWarnings("serial")
  private void prepareUsersGrid() {
    MessageResource resources = view.getResources();
    container = new BeanItemContainer<>(User.class, searchUsers());
    container.addNestedContainerProperty(LABORATORY_NAME);
    container.addNestedContainerProperty(ORGANIZATION);
    gridContainer = new GeneratedPropertyContainer(container);
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
    gridContainer.addGeneratedProperty(VALIDATE, new PropertyValueGenerator<Button>() {
      @Override
      public Button getValue(Item item, Object itemId, Object propertyId) {
        MessageResource resources = view.getResources();
        User user = (User) itemId;
        Button button = new Button();
        button.setCaption(resources.message(VALIDATE));
        button.addClickListener(event -> validateUser(user));
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
    view.usersGrid.setSelectionMode(SelectionMode.MULTI);
    view.usersGrid.sort(EMAIL, SortDirection.ASCENDING);
    view.usersGrid.addStyleName(COMPONENTS);
    Grid.Column viewColumn = view.usersGrid.getColumn(VIEW);
    viewColumn.setRenderer(new ComponentRenderer());
    Grid.Column validateColumn = view.usersGrid.getColumn(VALIDATE);
    validateColumn.setRenderer(new ComponentRenderer());
    for (String propertyId : COLUMNS) {
      view.usersGrid.getColumn(propertyId).setHeaderCaption(resources.message(propertyId));
    }
  }

  private void addFieldListeners() {
    view.validateSelectedButton.addClickListener(event -> {
      validateMany();
    });
  }

  private List<User> searchUsers() {
    UserFilterBuilder parameters = new UserFilterBuilder();
    parameters = parameters.onlyInvalid();
    if (!authorizationService.hasAdminRole()) {
      parameters = parameters.inLaboratory(authorizationService.getCurrentUser().getLaboratory());
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

  private void validateUser(User user) {
    logger.debug("Validate user {}", user);
    userService.validate(Collections.nCopies(1, user), homeWebContext());
    refresh();
    final MessageResource resources = view.getResources();
    view.showTrayNotification(resources.message("done", 1, user.getEmail()));
  }

  private void validateMany() {
    Collection<Object> ids = view.usersGrid.getSelectedRows();
    List<User> selected = new ArrayList<>();
    for (Object id : ids) {
      selected.add((User) id);
    }
    if (selected.isEmpty()) {
      final MessageResource resources = view.getResources();
      view.showError(resources.message("validateSelected.none"));
    } else {
      logger.debug("Validate users {}", selected);
      userService.validate(selected, homeWebContext());
      final MessageResource resources = view.getResources();
      StringBuilder emails = new StringBuilder();
      for (int i = 0; i < selected.size(); i++) {
        User user = selected.get(i);
        emails.append(user.getEmail());
        if (i == selected.size() - 2) {
          emails.append(resources.message("userSeparator", 1));
        } else if (i < selected.size() - 2) {
          emails.append(resources.message("userSeparator", 0));
        }
      }
      refresh();
      view.showTrayNotification(resources.message("done", selected.size(), emails));
    }
  }

  public HomeWebContext homeWebContext() {
    return locale -> view.getUrl(MainView.VIEW_NAME);
  }

  public static String[] getColumns() {
    return COLUMNS.clone();
  }
}
