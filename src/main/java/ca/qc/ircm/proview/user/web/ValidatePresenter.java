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
import ca.qc.ircm.proview.user.SearchUserParametersBuilder;
import ca.qc.ircm.proview.user.Signed;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.ButtonRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Validate users presenter.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ValidatePresenter {
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
  private static final String[] COLUMNS =
      { EMAIL, NAME, LABORATORY_NAME, ORGANIZATION, VIEW, VALIDATE };
  private static final Logger logger = LoggerFactory.getLogger(ValidatePresenter.class);
  private ValidateView view;
  private BeanItemContainer<User> container;
  private GeneratedPropertyContainer gridContainer;
  private Label headerLabel;
  private Grid usersGrid;
  private Button validateSelectedButton;
  @Inject
  private UserService userService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private Signed signed;

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(ValidateView view) {
    this.view = view;
    setFields();
    initializeUsersGridContainer();
    initializeUsersGrid();
    addFieldListeners();
    setCaptions();
  }

  private void setFields() {
    headerLabel = view.getHeaderLabel();
    usersGrid = view.getUsersGrid();
    validateSelectedButton = view.getValidateSelectedButton();
  }

  @SuppressWarnings("serial")
  private void initializeUsersGridContainer() {
    container = new BeanItemContainer<User>(User.class, searchUsers());
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
    usersGrid.setContainerDataSource(gridContainer);
    usersGrid.setColumns((Object[]) COLUMNS);
    usersGrid.setSelectionMode(SelectionMode.MULTI);
    usersGrid.sort(EMAIL, SortDirection.ASCENDING);
    Grid.Column viewColumn = usersGrid.getColumn(VIEW);
    viewColumn.setRenderer(new ButtonRenderer(event -> {
      User user = (User) event.getItemId();
      viewUser(user);
    }));
    Grid.Column validateColumn = usersGrid.getColumn(VALIDATE);
    validateColumn.setRenderer(new ButtonRenderer(event -> {
      User user = (User) event.getItemId();
      validateUser(user);
    }));
  }

  private void addFieldListeners() {
    validateSelectedButton.addClickListener(event -> {
      validateMany();
    });
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message("title"));
    headerLabel.setValue(resources.message("header"));
    validateSelectedButton.setCaption(resources.message("validateSelected"));
  }

  private List<User> searchUsers() {
    SearchUserParametersBuilder parameters = new SearchUserParametersBuilder();
    parameters = parameters.onlyInvalid();
    if (!authorizationService.hasProteomicRole()) {
      parameters = parameters.inLaboratory(signed.getUser().getLaboratory());
    }
    return userService.all(parameters);
  }

  private void refresh() {
    usersGrid.getSelectionModel().reset();
    container.removeAllItems();
    container.addAll(searchUsers());
    usersGrid.setSortOrder(new ArrayList<>(usersGrid.getSortOrder()));
  }

  private void viewUser(User user) {
    view.viewUser(user);
  }

  private void validateUser(User user) {
    logger.debug("Validate user {}", user);
    userService.validate(Collections.nCopies(1, user));
    refresh();
    final MessageResource resources = view.getResources();
    view.afterSuccessfulValidate(resources.message("done", 1, user.getEmail()));
  }

  private void validateMany() {
    Collection<Object> ids = usersGrid.getSelectedRows();
    List<User> selected = new ArrayList<>();
    for (Object id : ids) {
      selected.add((User) id);
    }
    if (selected.isEmpty()) {
      final MessageResource resources = view.getResources();
      view.showError(resources.message("validateSelected.none"));
    } else {
      logger.debug("Validate users {}", selected);
      userService.validate(selected);
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

  public static String[] getColumns() {
    return COLUMNS.clone();
  }
}
