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

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.LaboratoryProperties;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilter;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.HomeWebContext;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Validate users presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ValidateViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String USERS = "users";
  public static final String LABORATORY_NAME = property(LABORATORY, LaboratoryProperties.NAME);
  public static final String ORGANIZATION = property(LABORATORY, LaboratoryProperties.ORGANIZATION);
  public static final String VALIDATE = "validate";
  public static final String REMOVE = "remove";
  public static final String VALIDATE_SELECTED = "validateSelected";
  public static final String REMOVE_SELECTED = "removeSelected";
  public static final String NO_SELECTION = "selected.none";
  public static final String VALIDATED = "validated";
  public static final String REMOVED = "removed";
  public static final String USER_SEPARATOR = "userSeparator";
  private static final Logger logger = LoggerFactory.getLogger(ValidateViewPresenter.class);
  private ValidateView view;
  private ValidateViewDesign design;
  @Inject
  private UserService userService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private UserWindow userWindow;
  @Value("${spring.application.name}")
  private String applicationName;

  protected ValidateViewPresenter() {
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
    design = view.design;
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
    design.users.addStyleName(USERS);
    prepareUsersGrid();
    design.validateSelected.addStyleName(VALIDATE_SELECTED);
    design.validateSelected.setCaption(resources.message(VALIDATE_SELECTED));
    design.validateSelected.addClickListener(event -> validateSelected());
    design.removeSelected.addStyleName(REMOVE_SELECTED);
    design.removeSelected.setCaption(resources.message(REMOVE_SELECTED));
    design.removeSelected.addClickListener(event -> removeSelected());
  }

  private void prepareUsersGrid() {
    MessageResource resources = view.getResources();
    final Collator collator = Collator.getInstance(view.getLocale());
    design.users.setItems(searchUsers());
    design.users.addColumn(user -> viewButton(user), new ComponentRenderer()).setId(EMAIL)
        .setCaption(resources.message(EMAIL))
        .setComparator((u1, u2) -> collator.compare(u1.getEmail(), u2.getEmail()));
    design.users.addColumn(User::getName).setId(NAME).setCaption(resources.message(NAME));
    design.users.addColumn(user -> user.getLaboratory().getName()).setId(LABORATORY_NAME)
        .setCaption(resources.message(LABORATORY_NAME));
    design.users.addColumn(user -> user.getLaboratory().getOrganization()).setId(ORGANIZATION)
        .setCaption(resources.message(ORGANIZATION));
    design.users.setFrozenColumnCount(2);
    design.users.addColumn(user -> validateButton(user), new ComponentRenderer()).setId(VALIDATE)
        .setCaption(resources.message(VALIDATE)).setSortable(false);
    design.users.addColumn(user -> removeButton(user), new ComponentRenderer()).setId(REMOVE)
        .setCaption(resources.message(REMOVE)).setSortable(false);
    design.users.setSelectionMode(SelectionMode.MULTI);
    design.users.sort(EMAIL, SortDirection.ASCENDING);
    design.users.addStyleName(COMPONENTS);
  }

  private List<User> searchUsers() {
    UserFilter filter = new UserFilter();
    filter.valid = false;
    if (!authorizationService.hasAdminRole()) {
      filter.laboratory = authorizationService.getCurrentUser().getLaboratory();
    }
    return userService.all(filter);
  }

  private Button viewButton(User user) {
    Button button = new Button();
    button.addStyleName(EMAIL);
    button.setCaption(user.getEmail());
    button.addClickListener(event -> view(user));
    return button;
  }

  private Button validateButton(User user) {
    MessageResource resources = view.getResources();
    Button button = new Button();
    button.addStyleName(VALIDATE);
    button.setCaption(resources.message(VALIDATE));
    button.addClickListener(event -> validate(user));
    return button;
  }

  private Button removeButton(User user) {
    MessageResource resources = view.getResources();
    Button button = new Button();
    button.addStyleName(REMOVE);
    button.setCaption(resources.message(REMOVE));
    button.addClickListener(event -> remove(user));
    return button;
  }

  private void refresh() {
    design.users.getSelectionModel().deselectAll();
    design.users.setItems(searchUsers());
    design.users.setSortOrder(new ArrayList<>(design.users.getSortOrder()));
  }

  private void view(User user) {
    userWindow.center();
    if (!userWindow.isAttached()) {
      userWindow.setValue(user);
      view.addWindow(userWindow);
    }
  }

  private void validate(User user) {
    logger.debug("Validate user {}", user);
    userService.validate(Collections.nCopies(1, user), homeWebContext());
    refresh();
    final MessageResource resources = view.getResources();
    view.showTrayNotification(resources.message(VALIDATED, 1, user.getEmail()));
  }

  private void remove(User user) {
    logger.debug("Remove user {}", user);
    userService.delete(Collections.nCopies(1, user));
    refresh();
    final MessageResource resources = view.getResources();
    view.showTrayNotification(resources.message(REMOVED, 1, user.getEmail()));
  }

  private boolean validateSelection() {
    List<User> users = new ArrayList<>(design.users.getSelectedItems());
    if (users.isEmpty()) {
      final MessageResource resources = view.getResources();
      String message = resources.message(NO_SELECTION);
      logger.trace("Validation failed {}", message);
      view.showError(message);
      return false;
    }
    return true;
  }

  private void validateSelected() {
    if (validateSelection()) {
      List<User> users = new ArrayList<>(design.users.getSelectedItems());
      logger.debug("Validate users {}", users);
      userService.validate(users, homeWebContext());
      final MessageResource resources = view.getResources();
      StringBuilder emails = new StringBuilder();
      for (int i = 0; i < users.size(); i++) {
        User user = users.get(i);
        emails.append(user.getEmail());
        if (i == users.size() - 2) {
          emails.append(resources.message(USER_SEPARATOR, 1));
        } else if (i < users.size() - 2) {
          emails.append(resources.message(USER_SEPARATOR, 0));
        }
      }
      refresh();
      view.showTrayNotification(resources.message(VALIDATED, users.size(), emails));
    }
  }

  private void removeSelected() {
    if (validateSelection()) {
      List<User> users = new ArrayList<>(design.users.getSelectedItems());
      logger.debug("Remove users {}", users);
      userService.delete(users);
      final MessageResource resources = view.getResources();
      StringBuilder emails = new StringBuilder();
      for (int i = 0; i < users.size(); i++) {
        User user = users.get(i);
        emails.append(user.getEmail());
        if (i == users.size() - 2) {
          emails.append(resources.message(USER_SEPARATOR, 1));
        } else if (i < users.size() - 2) {
          emails.append(resources.message(USER_SEPARATOR, 0));
        }
      }
      refresh();
      view.showTrayNotification(resources.message(REMOVED, users.size(), emails));
    }
  }

  public HomeWebContext homeWebContext() {
    return locale -> view.getUrl(MainView.VIEW_NAME);
  }
}
