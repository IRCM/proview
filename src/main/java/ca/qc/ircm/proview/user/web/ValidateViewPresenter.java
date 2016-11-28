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

import ca.qc.ircm.proview.laboratory.QLaboratory;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.QUser;
import ca.qc.ircm.proview.user.Signed;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilterBuilder;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.HomeWebContext;
import ca.qc.ircm.proview.web.MainUi;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ButtonRenderer;
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

/**
 * Validate users presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ValidateViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER_LABEL_ID = "header";
  public static final String USERS_GRID_ID = "usersGrid";
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
  public static final String VALIDATE_SELECTED_BUTTON_ID = "validateSelected";
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
  private MainUi ui;
  @Inject
  private Signed signed;
  @Value("${spring.application.name}")
  private String applicationName;

  public ValidateViewPresenter() {
  }

  protected ValidateViewPresenter(UserService userService,
      AuthorizationService authorizationService, MainUi ui, Signed signed, String applicationName) {
    this.userService = userService;
    this.authorizationService = authorizationService;
    this.ui = ui;
    this.signed = signed;
    this.applicationName = applicationName;
  }

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(ValidateView view) {
    this.view = view;
  }

  /**
   * Called by view when view is attached.
   */
  public void attach() {
    logger.debug("Validate users view");
    setIds();
    initializeUsersGridContainer();
    initializeUsersGrid();
    addFieldListeners();
    setCaptions();
  }

  private void setIds() {
    view.headerLabel.setId(HEADER_LABEL_ID);
    view.usersGrid.setId(USERS_GRID_ID);
    view.validateSelectedButton.setId(VALIDATE_SELECTED_BUTTON_ID);
  }

  @SuppressWarnings("serial")
  private void initializeUsersGridContainer() {
    container = new BeanItemContainer<>(User.class, searchUsers());
    container.addNestedContainerProperty(LABORATORY_NAME);
    container.addNestedContainerProperty(ORGANIZATION);
    gridContainer = new GeneratedPropertyContainer(container);
    gridContainer.addGeneratedProperty(VIEW, new PropertyValueGenerator<String>() {
      @Override
      public String getValue(Item item, Object itemId, Object propertyId) {
        MessageResource resources = view.getResources();
        return resources.message(VIEW);
      }

      @Override
      public Class<String> getType() {
        return String.class;
      }
    });
    gridContainer.addGeneratedProperty(VALIDATE, new PropertyValueGenerator<String>() {
      @Override
      public String getValue(Item item, Object itemId, Object propertyId) {
        MessageResource resources = view.getResources();
        return resources.message(VALIDATE);
      }

      @Override
      public Class<String> getType() {
        return String.class;
      }
    });
  }

  private void initializeUsersGrid() {
    view.usersGrid.setContainerDataSource(gridContainer);
    view.usersGrid.setColumns((Object[]) COLUMNS);
    view.usersGrid.setSelectionMode(SelectionMode.MULTI);
    view.usersGrid.sort(EMAIL, SortDirection.ASCENDING);
    Grid.Column viewColumn = view.usersGrid.getColumn(VIEW);
    viewColumn.setRenderer(new ButtonRenderer(event -> {
      User user = (User) event.getItemId();
      viewUser(user);
    }));
    Grid.Column validateColumn = view.usersGrid.getColumn(VALIDATE);
    validateColumn.setRenderer(new ButtonRenderer(event -> {
      User user = (User) event.getItemId();
      validateUser(user);
    }));
  }

  private void addFieldListeners() {
    view.validateSelectedButton.addClickListener(event -> {
      validateMany();
    });
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    view.headerLabel.setValue(resources.message(HEADER_LABEL_ID));
    view.validateSelectedButton.setCaption(resources.message(VALIDATE_SELECTED_BUTTON_ID));
  }

  private List<User> searchUsers() {
    UserFilterBuilder parameters = new UserFilterBuilder();
    parameters = parameters.onlyInvalid();
    if (!authorizationService.hasAdminRole()) {
      parameters = parameters.inLaboratory(signed.getUser().getLaboratory());
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
    view.viewUser(user);
  }

  private void validateUser(User user) {
    logger.debug("Validate user {}", user);
    userService.validate(Collections.nCopies(1, user), homeWebContext());
    refresh();
    final MessageResource resources = view.getResources();
    view.afterSuccessfulValidate(resources.message("done", 1, user.getEmail()));
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
      view.afterSuccessfulValidate(resources.message("done", selected.size(), emails));
    }
  }

  public HomeWebContext homeWebContext() {
    return locale -> ui.getUrl(MainView.VIEW_NAME);
  }

  public static String[] getColumns() {
    return COLUMNS.clone();
  }
}
