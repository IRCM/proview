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

import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_FAILED;
import static ca.qc.ircm.proview.user.web.UsersView.USERS_REQUIRED;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.SwitchUserService;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryService;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Users view presenter.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UsersViewPresenter {
  private UsersView view;
  private UserService service;
  private LaboratoryService laboratoryService;
  private SwitchUserService switchUserService;
  private AuthenticatedUser authenticatedUser;
  private Locale locale;
  private ListDataProvider<User> usersDataProvider;
  private WebUserFilter filter = new WebUserFilter();

  @Autowired
  protected UsersViewPresenter(UserService service, LaboratoryService laboratoryService,
      SwitchUserService switchUserService, AuthenticatedUser authenticatedUser) {
    this.service = service;
    this.laboratoryService = laboratoryService;
    this.switchUserService = switchUserService;
    this.authenticatedUser = authenticatedUser;
  }

  void init(UsersView view) {
    this.view = view;
    loadUsers();
    view.active.setVisible(authenticatedUser.hasAnyRole(ADMIN, MANAGER));
    view.error.setVisible(false);
    view.add.setVisible(authenticatedUser.hasAnyRole(ADMIN, MANAGER));
    view.switchUser.setVisible(authenticatedUser.hasRole(ADMIN));
    view.viewLaboratory.setVisible(authenticatedUser.hasAnyRole(ADMIN, MANAGER));
  }

  private void loadUsers() {
    List<User> users = authenticatedUser.hasRole(ADMIN) ? service.all(null)
        : service.all(null, authenticatedUser.getUser().get().getLaboratory());
    view.users.setItems(users);
  }

  void localeChange(Locale locale) {
    this.locale = locale;
  }

  void filterEmail(String value) {
    filter.emailContains = value.isEmpty() ? null : value;
    view.users.getDataProvider().refreshAll();
  }

  void filterName(String value) {
    filter.nameContains = value.isEmpty() ? null : value;
    view.users.getDataProvider().refreshAll();
  }

  void filterLaboratory(String value) {
    filter.laboratoryNameContains = value.isEmpty() ? null : value;
    view.users.getDataProvider().refreshAll();
  }

  void filterActive(Boolean value) {
    filter.active = value;
    view.users.getDataProvider().refreshAll();
  }

  private void clearError() {
    view.error.setVisible(false);
  }

  void view(User user) {
    clearError();
    UserDialog dialog = view.dialogFactory.getObject();
    dialog.setUser(service.get(user.getId()).orElse(null));
    dialog.open();
    dialog.addSavedListener(e -> loadUsers());
  }

  void viewLaboratory() {
    clearError();
    User user = view.users.getSelectedItems().stream().findFirst().orElse(null);
    if (user == null) {
      AppResources resources = new AppResources(UsersView.class, locale);
      view.error.setText(resources.message(USERS_REQUIRED));
      view.error.setVisible(true);
    } else {
      viewLaboratory(user.getLaboratory());
    }
  }

  void viewLaboratory(Laboratory laboratory) {
    clearError();
    LaboratoryDialog laboratoryDialog = view.laboratoryDialogFactory.getObject();
    laboratoryDialog.setLaboratory(laboratoryService.get(laboratory.getId()).orElse(null));
    laboratoryDialog.open();
    laboratoryDialog.addSavedListener(e -> loadUsers());
  }

  void toggleActive(User user) {
    clearError();
    user.setActive(!user.isActive());
    service.save(user, null);
  }

  void switchUser() {
    clearError();
    User user = view.users.getSelectedItems().stream().findFirst().orElse(null);
    if (user == null) {
      AppResources resources = new AppResources(UsersView.class, locale);
      view.error.setText(resources.message(USERS_REQUIRED));
      view.error.setVisible(true);
    } else {
      switchUserService.switchUser(user, VaadinServletRequest.getCurrent());
      UI.getCurrent().navigate(MainView.class);
    }
  }

  void add() {
    UserDialog dialog = view.dialogFactory.getObject();
    dialog.setUser(new User());
    dialog.open();
    dialog.addSavedListener(e -> loadUsers());
  }

  void showError(Map<String, List<String>> parameters, Locale locale) {
    AppResources resources = new AppResources(UsersView.class, locale);
    if (parameters.containsKey(SWITCH_FAILED)) {
      view.showNotification(resources.message(SWITCH_FAILED));
    }
  }

  WebUserFilter filter() {
    return filter;
  }
}
