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

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_FAILED;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_USERNAME;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_USER_FORM;
import static ca.qc.ircm.proview.user.web.UsersView.USERS_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.test.config.AbstractKaribuTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.LaboratoryService;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.SavedEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.data.provider.DataProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link UsersViewPresenter}.
 */
@ServiceTestAnnotations
public class UsersViewPresenterTest extends AbstractKaribuTestCase {
  @Autowired
  private UsersViewPresenter presenter;
  @Mock
  private UsersView view;
  @MockBean
  private UserService service;
  @MockBean
  private LaboratoryService laboratoryService;
  @MockBean
  private AuthenticatedUser authenticatedUser;
  @Mock
  private DataProvider<User, Void> dataProvider;
  @Captor
  private ArgumentCaptor<User> userCaptor;
  @Captor
  private ArgumentCaptor<ComponentEventListener<SavedEvent<UserDialog>>> userSavedListenerCaptor;
  @Captor
  private ArgumentCaptor<
      ComponentEventListener<SavedEvent<LaboratoryDialog>>> laboratorySavedListenerCaptor;
  @Autowired
  private UserRepository repository;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  private List<User> users;
  private User currentUser;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(UsersView.class, locale);

  /**
   * Before test.
   */
  @BeforeEach
  @SuppressWarnings("unchecked")
  public void beforeTest() {
    view.header = new H2();
    view.users = new Grid<>();
    view.users.setSelectionMode(SelectionMode.MULTI);
    view.active = mock(Column.class);
    view.error = new Div();
    view.add = new Button();
    view.switchUser = new Button();
    view.viewLaboratory = new Button();
    view.switchUserForm = new Html("<form></form>");
    view.dialog = mock(UserDialog.class);
    view.laboratoryDialog = mock(LaboratoryDialog.class);
    users = repository.findAll();
    when(service.all(any(), any(Laboratory.class))).thenReturn(users);
    when(service.all(any())).thenReturn(users);
    currentUser = repository.findById(2L).orElse(null);
    when(authenticatedUser.getUser()).thenReturn(Optional.of(currentUser));
  }

  @Test
  public void users_User() {
    presenter.init(view);
    verify(service).all(null, currentUser.getLaboratory());
    List<User> users = items(view.users);
    assertEquals(this.users.size(), users.size());
    for (User user : this.users) {
      assertTrue(users.contains(user), user.toString());
    }
    assertEquals(0, view.users.getSelectedItems().size());
    users.forEach(user -> view.users.select(user));
    assertEquals(users.size(), view.users.getSelectedItems().size());
    verify(view.active).setVisible(false);
    assertFalse(view.add.isVisible());
    assertFalse(view.switchUser.isVisible());
    assertFalse(view.switchUserForm.isVisible());
    assertFalse(view.viewLaboratory.isVisible());
  }

  @Test
  public void users_Manager() {
    when(authenticatedUser.hasAnyRole(UserRole.ADMIN, UserRole.MANAGER)).thenReturn(true);
    presenter.init(view);
    verify(service).all(null, currentUser.getLaboratory());
    List<User> users = items(view.users);
    assertEquals(this.users.size(), users.size());
    for (User user : this.users) {
      assertTrue(users.contains(user), user.toString());
    }
    assertEquals(0, view.users.getSelectedItems().size());
    users.forEach(user -> view.users.select(user));
    assertEquals(users.size(), view.users.getSelectedItems().size());
    verify(view.active).setVisible(true);
    assertTrue(view.add.isVisible());
    assertFalse(view.switchUser.isVisible());
    assertFalse(view.switchUserForm.isVisible());
    assertTrue(view.viewLaboratory.isVisible());
  }

  @Test
  public void users_Admin() {
    when(authenticatedUser.hasAnyRole(UserRole.ADMIN, UserRole.MANAGER)).thenReturn(true);
    when(authenticatedUser.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.init(view);
    verify(service).all(null);
    List<User> users = items(view.users);
    assertEquals(this.users.size(), users.size());
    for (User user : this.users) {
      assertTrue(users.contains(user), user.toString());
    }
    assertEquals(0, view.users.getSelectedItems().size());
    users.forEach(user -> view.users.select(user));
    assertEquals(users.size(), view.users.getSelectedItems().size());
    verify(view.active).setVisible(true);
    assertTrue(view.add.isVisible());
    assertTrue(view.switchUser.isVisible());
    assertTrue(view.switchUserForm.isVisible());
    assertTrue(view.viewLaboratory.isVisible());
  }

  @Test
  public void filterEmail() {
    presenter.init(view);
    view.users.setItems(dataProvider);

    presenter.filterEmail("test");

    assertEquals("test", presenter.filter().emailContains);
    verify(dataProvider).refreshAll();
  }

  @Test
  public void filterEmail_Empty() {
    presenter.init(view);
    view.users.setItems(dataProvider);

    presenter.filterEmail("");

    assertEquals(null, presenter.filter().emailContains);
    verify(dataProvider).refreshAll();
  }

  @Test
  public void filterName() {
    presenter.init(view);
    view.users.setItems(dataProvider);

    presenter.filterName("test");

    assertEquals("test", presenter.filter().nameContains);
    verify(dataProvider).refreshAll();
  }

  @Test
  public void filterName_Empty() {
    presenter.init(view);
    view.users.setItems(dataProvider);

    presenter.filterName("");

    assertEquals(null, presenter.filter().nameContains);
    verify(dataProvider).refreshAll();
  }

  @Test
  public void filterLaboratory() {
    presenter.init(view);
    view.users.setItems(dataProvider);

    presenter.filterLaboratory("test");

    assertEquals("test", presenter.filter().laboratoryNameContains);
    verify(dataProvider).refreshAll();
  }

  @Test
  public void filterLaboratory_Empty() {
    presenter.init(view);
    view.users.setItems(dataProvider);

    presenter.filterLaboratory("");

    assertEquals(null, presenter.filter().laboratoryNameContains);
    verify(dataProvider).refreshAll();
  }

  @Test
  public void filterActive_False() {
    presenter.init(view);
    view.users.setItems(dataProvider);

    presenter.filterActive(false);

    assertEquals(false, presenter.filter().active);
    verify(dataProvider).refreshAll();
  }

  @Test
  public void filterActive_True() {
    presenter.init(view);
    view.users.setItems(dataProvider);

    presenter.filterActive(true);

    assertEquals(true, presenter.filter().active);
    verify(dataProvider).refreshAll();
  }

  @Test
  public void filterActive_Null() {
    presenter.init(view);
    view.users.setItems(dataProvider);

    presenter.filterActive(null);

    assertEquals(null, presenter.filter().active);
    verify(dataProvider).refreshAll();
  }

  @Test
  public void error() {
    presenter.init(view);
    presenter.localeChange(locale);
    assertFalse(view.error.isVisible());
  }

  @Test
  public void view() {
    presenter.init(view);
    User user = mock(User.class);
    when(user.getId()).thenReturn(2L);
    User databaseUser = repository.findById(2L).orElse(null);
    when(service.get(any(Long.class))).thenReturn(Optional.of(databaseUser));
    presenter.view(user);
    verify(service).get(2L);
    verify(view.dialog).setUser(databaseUser);
    verify(view.dialog).open();
  }

  @Test
  public void view_Empty() {
    presenter.init(view);
    User user = mock(User.class);
    when(user.getId()).thenReturn(2L);
    when(service.get(any(Long.class))).thenReturn(Optional.empty());
    presenter.view(user);
    verify(service).get(2L);
    verify(view.dialog).setUser(null);
    verify(view.dialog).open();
  }

  @Test
  public void viewLaboratory() {
    presenter.init(view);
    presenter.localeChange(locale);
    User user = repository.findById(3L).get();
    view.users.select(user);
    Laboratory databaseLaboratory = laboratoryRepository.findById(2L).orElse(null);
    when(laboratoryService.get(any(Long.class))).thenReturn(Optional.of(databaseLaboratory));
    presenter.viewLaboratory();
    assertFalse(view.error.isVisible());
    verify(laboratoryService).get(2L);
    verify(view.laboratoryDialog).setLaboratory(databaseLaboratory);
    verify(view.laboratoryDialog).open();
  }

  @Test
  public void viewLaboratory_Empty() {
    presenter.init(view);
    presenter.localeChange(locale);
    presenter.viewLaboratory();
    assertEquals(resources.message(USERS_REQUIRED), view.error.getText());
    assertTrue(view.error.isVisible());
    verify(laboratoryService, never()).get(any());
    verify(view.laboratoryDialog, never()).setLaboratory(any());
    verify(view.laboratoryDialog, never()).open();
  }

  @Test
  public void viewLaboratory_Laboratory() {
    presenter.init(view);
    Laboratory laboratory = mock(Laboratory.class);
    when(laboratory.getId()).thenReturn(2L);
    Laboratory databaseLaboratory = laboratoryRepository.findById(2L).orElse(null);
    when(laboratoryService.get(any(Long.class))).thenReturn(Optional.of(databaseLaboratory));
    presenter.viewLaboratory(laboratory);
    verify(laboratoryService).get(2L);
    verify(view.laboratoryDialog).setLaboratory(databaseLaboratory);
    verify(view.laboratoryDialog).open();
  }

  @Test
  public void viewLaboratory_LaboratoryEmpty() {
    presenter.init(view);
    Laboratory laboratory = mock(Laboratory.class);
    when(laboratory.getId()).thenReturn(2L);
    when(laboratoryService.get(any(Long.class))).thenReturn(Optional.empty());
    presenter.viewLaboratory(laboratory);
    verify(laboratoryService).get(2L);
    verify(view.laboratoryDialog).setLaboratory(null);
    verify(view.laboratoryDialog).open();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void refreshOnSaved() {
    presenter.init(view);
    verify(view.dialog).addSavedListener(userSavedListenerCaptor.capture());
    ComponentEventListener<SavedEvent<UserDialog>> savedListener =
        userSavedListenerCaptor.getValue();
    savedListener.onComponentEvent(mock(SavedEvent.class));
    verify(service, times(2)).all(null, currentUser.getLaboratory());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void refreshOnSaved_Laboratory() {
    presenter.init(view);
    verify(view.laboratoryDialog).addSavedListener(laboratorySavedListenerCaptor.capture());
    ComponentEventListener<SavedEvent<LaboratoryDialog>> savedListener =
        laboratorySavedListenerCaptor.getValue();
    savedListener.onComponentEvent(mock(SavedEvent.class));
    verify(service, times(2)).all(null, currentUser.getLaboratory());
  }

  @Test
  public void toggleActive_Active() {
    presenter.init(view);
    User user = repository.findById(3L).orElse(null);
    presenter.toggleActive(user);
    verify(service).save(user, null);
    assertFalse(user.isActive());
  }

  @Test
  public void toggleActive_Inactive() {
    presenter.init(view);
    User user = repository.findById(11L).orElse(null);
    presenter.toggleActive(user);
    verify(service).save(user, null);
    assertTrue(user.isActive());
  }

  @Test
  @WithUserDetails("christian.poitras@ircm.qc.ca")
  public void switchUser() throws Throwable {
    ui.navigate(UsersView.class);
    presenter.init(view);
    User user = repository.findById(3L).orElse(null);
    view.users.select(user);
    presenter.switchUser();
    assertFalse(view.error.isVisible());
    assertTrue(UI.getCurrent().getInternals().dumpPendingJavaScriptInvocations().stream()
        .anyMatch(i -> ("document.getElementById(\"" + SWITCH_USERNAME + "\").value = \""
            + user.getEmail() + "\"; document.getElementById(\"" + SWITCH_USER_FORM
            + "\").submit()").equals(i.getInvocation().getExpression())));
  }

  @Test
  @WithUserDetails("christian.poitras@ircm.qc.ca")
  public void switchUser_EmptySelection() throws Throwable {
    ui.navigate(UsersView.class);
    presenter.init(view);
    presenter.localeChange(locale);
    presenter.switchUser();
    assertEquals(resources.message(USERS_REQUIRED), view.error.getText());
    assertTrue(view.error.isVisible());
    assertFalse(UI.getCurrent().getInternals().dumpPendingJavaScriptInvocations().stream()
        .anyMatch(i -> i.getInvocation().getExpression().contains(SWITCH_USER_FORM)));
  }

  @Test
  public void permissions_ErrorThenView() {
    presenter.init(view);
    presenter.localeChange(locale);
    presenter.switchUser();
    presenter.view(users.get(1));
    assertFalse(view.error.isVisible());
  }

  @Test
  public void add() {
    presenter.init(view);
    presenter.add();
    verify(view.dialog).setUser(userCaptor.capture());
    User user = userCaptor.getValue();
    assertNull(user.getId());
    assertNull(user.getEmail());
    assertNull(user.getName());
    assertNull(user.getLaboratory());
    verify(view.dialog).open();
  }

  @Test
  public void showError_NoError() {
    presenter.init(view);
    Map<String, List<String>> parameters = new HashMap<>();
    presenter.showError(parameters, locale);
    verify(view, never()).showNotification(any());
  }

  @Test
  public void showError_SwitchFailed() {
    presenter.init(view);
    Map<String, List<String>> parameters = new HashMap<>();
    parameters.put(SWITCH_FAILED, Collections.emptyList());
    presenter.showError(parameters, locale);
    verify(view).showNotification(resources.message(SWITCH_FAILED));
  }
}
