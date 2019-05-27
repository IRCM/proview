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
import ca.qc.ircm.proview.text.NormalizedComparator;
import ca.qc.ircm.proview.user.LaboratoryProperties;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilter;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.HomeWebContext;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
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
  public static final String VALIDATED = "validated";
  public static final String REMOVED = "removed";
  private static final Logger logger = LoggerFactory.getLogger(ValidateViewPresenter.class);
  private ValidateView view;
  private ValidateViewDesign design;
  @Inject
  private UserService userService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private Provider<UserWindow> userWindowProvider;
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
  }

  private void prepareUsersGrid() {
    MessageResource resources = view.getResources();
    final NormalizedComparator comparator = new NormalizedComparator();
    design.users.setItems(searchUsers());
    design.users.addItemClickListener(e -> {
      if (e.getMouseEventDetails().isDoubleClick()) {
        view(e.getItem());
      }
    });
    design.users.addColumn(User::getEmail).setId(EMAIL).setCaption(resources.message(EMAIL))
        .setComparator((u1, u2) -> comparator.compare(u1.getEmail(), u2.getEmail()));
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
    design.users.sort(EMAIL, SortDirection.ASCENDING);
    design.users.addStyleName(COMPONENTS);
  }

  private List<User> searchUsers() {
    UserFilter filter = new UserFilter();
    filter.valid = false;
    if (!authorizationService.hasRole(UserRole.ADMIN)) {
      filter.laboratory = authorizationService.getCurrentUser().getLaboratory();
    }
    return userService.all(filter);
  }

  private Button validateButton(User user) {
    MessageResource resources = view.getResources();
    Button button = new Button();
    button.addStyleName(ValoTheme.BUTTON_FRIENDLY);
    button.addStyleName(VALIDATE);
    button.setCaption(resources.message(VALIDATE));
    button.addClickListener(event -> validate(user));
    return button;
  }

  private Button removeButton(User user) {
    MessageResource resources = view.getResources();
    Button button = new Button();
    button.addStyleName(ValoTheme.BUTTON_DANGER);
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
    UserWindow userWindow = userWindowProvider.get();
    userWindow.center();
    userWindow.setValue(user);
    view.addWindow(userWindow);
  }

  private void validate(User user) {
    logger.debug("Validate user {}", user);
    userService.validate(user, homeWebContext());
    refresh();
    final MessageResource resources = view.getResources();
    view.showTrayNotification(resources.message(VALIDATED, user.getEmail()));
  }

  private void remove(User user) {
    logger.debug("Remove user {}", user);
    userService.delete(user);
    refresh();
    final MessageResource resources = view.getResources();
    view.showTrayNotification(resources.message(REMOVED, user.getEmail()));
  }

  public HomeWebContext homeWebContext() {
    return locale -> view.getUrl(MainView.VIEW_NAME);
  }
}
