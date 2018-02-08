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

import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.dataProvider;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.EMAIL;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.HEADER;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.LABORATORY_NAME;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.NAME;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.NO_SELECTION;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.ORGANIZATION;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.REMOVE;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.REMOVE_DONE;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.REMOVE_SELECTED;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.TITLE;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.USERS;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.USER_SEPARATOR;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.VALIDATE;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.VALIDATE_DONE;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.VALIDATE_SELECTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilter;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.HomeWebContext;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.SelectionModel;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ValidateViewPresenterTest {
  private ValidateViewPresenter presenter;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private ValidateView view;
  @Mock
  private UserService userService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Provider<UserWindow> userWindowProvider;
  @Mock
  private UserWindow userWindow;
  @Captor
  private ArgumentCaptor<Collection<User>> usersCaptor;
  @Captor
  private ArgumentCaptor<UserFilter> userFilterCaptor;
  @Captor
  private ArgumentCaptor<HomeWebContext> homeWebContextCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private ValidateViewDesign design;
  private User signedUser;
  private List<User> usersToValidate;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(ValidateView.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new ValidateViewPresenter(userService, authorizationService, userWindowProvider,
        applicationName);
    signedUser = entityManager.find(User.class, 1L);
    when(authorizationService.getCurrentUser()).thenReturn(signedUser);
    usersToValidate = new ArrayList<>();
    usersToValidate.add(entityManager.find(User.class, 4L));
    usersToValidate.add(entityManager.find(User.class, 5L));
    usersToValidate.add(entityManager.find(User.class, 10L));
    when(userService.all(any())).thenReturn(usersToValidate);
    design = new ValidateViewDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(userWindowProvider.get()).thenReturn(userWindow);
  }

  @Test
  public void users() {
    presenter.init(view);

    assertEquals(6, design.users.getColumns().size());
    assertEquals(EMAIL, design.users.getColumns().get(0).getId());
    assertTrue(containsInstanceOf(design.users.getColumns().get(0).getExtensions(),
        ComponentRenderer.class));
    assertEquals(resources.message(EMAIL), design.users.getColumn(EMAIL).getCaption());
    assertTrue(
        containsInstanceOf(design.users.getColumn(EMAIL).getExtensions(), ComponentRenderer.class));
    Collator collator = Collator.getInstance(locale);
    List<User> expectedSortedUsers = new ArrayList<>(usersToValidate);
    List<User> sortedUsers = new ArrayList<>(usersToValidate);
    expectedSortedUsers.sort((u1, u2) -> collator.compare(u1.getEmail(), u2.getEmail()));
    sortedUsers.sort(design.users.getColumn(EMAIL).getComparator(SortDirection.ASCENDING));
    assertEquals(expectedSortedUsers, sortedUsers);
    expectedSortedUsers.sort((u1, u2) -> -collator.compare(u1.getEmail(), u2.getEmail()));
    sortedUsers.sort(design.users.getColumn(EMAIL).getComparator(SortDirection.DESCENDING));
    assertEquals(expectedSortedUsers, sortedUsers);
    for (User user : usersToValidate) {
      Button button = (Button) design.users.getColumn(EMAIL).getValueProvider().apply(user);
      assertEquals(user.getEmail(), button.getCaption());
      assertTrue(user.getEmail(), button.getStyleName().contains(EMAIL));
    }
    assertEquals(NAME, design.users.getColumns().get(1).getId());
    assertEquals(resources.message(NAME), design.users.getColumn(NAME).getCaption());
    for (User user : usersToValidate) {
      assertEquals(user.getName(), design.users.getColumn(NAME).getValueProvider().apply(user));
    }
    assertEquals(LABORATORY_NAME, design.users.getColumns().get(2).getId());
    assertEquals(resources.message(LABORATORY_NAME),
        design.users.getColumn(LABORATORY_NAME).getCaption());
    for (User user : usersToValidate) {
      assertEquals(user.getLaboratory().getName(),
          design.users.getColumn(LABORATORY_NAME).getValueProvider().apply(user));
    }
    assertEquals(ORGANIZATION, design.users.getColumns().get(3).getId());
    assertEquals(resources.message(ORGANIZATION),
        design.users.getColumn(ORGANIZATION).getCaption());
    for (User user : usersToValidate) {
      assertEquals(user.getLaboratory().getOrganization(),
          design.users.getColumn(ORGANIZATION).getValueProvider().apply(user));
    }
    assertEquals(VALIDATE, design.users.getColumns().get(4).getId());
    assertTrue(containsInstanceOf(design.users.getColumn(VALIDATE).getExtensions(),
        ComponentRenderer.class));
    assertEquals(resources.message(VALIDATE), design.users.getColumn(VALIDATE).getCaption());
    assertFalse(design.users.getColumn(VALIDATE).isSortable());
    for (User user : usersToValidate) {
      Button button = (Button) design.users.getColumn(VALIDATE).getValueProvider().apply(user);
      assertEquals(resources.message(VALIDATE), button.getCaption());
      assertTrue(user.getEmail(), button.getStyleName().contains(VALIDATE));
    }
    assertEquals(REMOVE, design.users.getColumns().get(5).getId());
    assertTrue(containsInstanceOf(design.users.getColumn(REMOVE).getExtensions(),
        ComponentRenderer.class));
    assertEquals(resources.message(REMOVE), design.users.getColumn(REMOVE).getCaption());
    assertFalse(design.users.getColumn(REMOVE).isSortable());
    for (User user : usersToValidate) {
      Button button = (Button) design.users.getColumn(REMOVE).getValueProvider().apply(user);
      assertEquals(resources.message(REMOVE), button.getCaption());
      assertTrue(user.getEmail(), button.getStyleName().contains(REMOVE));
    }
    SelectionModel<User> selectionModel = design.users.getSelectionModel();
    assertTrue(selectionModel instanceof SelectionModel.Multi);
    List<GridSortOrder<User>> sortOrders = design.users.getSortOrder();
    assertFalse(sortOrders.isEmpty());
    GridSortOrder<User> sortOrder = sortOrders.get(0);
    assertEquals(EMAIL, sortOrder.getSorted().getId());
    assertEquals(SortDirection.ASCENDING, sortOrder.getDirection());
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.header.getStyleName().contains(HEADER));
    assertTrue(design.header.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.users.getStyleName().contains(USERS));
    assertTrue(design.validateSelected.getStyleName().contains(VALIDATE_SELECTED));
    assertTrue(design.removeSelected.getStyleName().contains(REMOVE_SELECTED));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
    assertEquals(resources.message(VALIDATE_SELECTED), design.validateSelected.getCaption());
  }

  @Test
  public void usersToValidate_Admin() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    verify(userService).all(userFilterCaptor.capture());

    UserFilter userFilter = userFilterCaptor.getValue();
    assertFalse(userFilter.valid);
    assertNull(userFilter.laboratory);
    assertNull(userFilter.active);
    assertNull(userFilter.admin);
  }

  @Test
  public void usersToValidate_LaboratoryManager() {
    when(authorizationService.hasAdminRole()).thenReturn(false);
    presenter.init(view);

    verify(userService).all(userFilterCaptor.capture());

    UserFilter userFilter = userFilterCaptor.getValue();
    assertFalse(userFilter.valid);
    assertEquals(signedUser.getLaboratory(), userFilter.laboratory);
    assertNull(userFilter.active);
    assertNull(userFilter.admin);
  }

  @Test
  public void viewUser() {
    presenter.init(view);
    final User user = usersToValidate.get(0);
    Button button = (Button) design.users.getColumn(EMAIL).getValueProvider().apply(user);

    button.click();

    verify(userWindowProvider).get();
    verify(userWindow).setValue(user);
    verify(userWindow).center();
    verify(view).addWindow(userWindow);
  }

  @Test
  public void validateOne() {
    presenter.init(view);
    final User user = usersToValidate.get(0);
    List<User> usersToValidateAfter = new ArrayList<>(usersToValidate);
    usersToValidateAfter.remove(0);
    when(userService.all(any())).thenReturn(usersToValidateAfter);
    String homeUrl = "homeUrl";
    when(view.getUrl(any())).thenReturn(homeUrl);
    Button button = (Button) design.users.getColumn(VALIDATE).getValueProvider().apply(user);

    button.click();

    verify(userService).validate(usersCaptor.capture(), homeWebContextCaptor.capture());
    verify(userService, never()).delete(any());
    Collection<User> users = usersCaptor.getValue();
    assertEquals(1, users.size());
    assertTrue(find(users, user.getId()).isPresent());
    verify(view).showTrayNotification(resources.message(VALIDATE_DONE, 1, user.getEmail()));
    verify(userService, times(2)).all(any());
    assertEquals(usersToValidateAfter.size(), dataProvider(design.users).getItems().size());
    HomeWebContext homeWebContext = homeWebContextCaptor.getValue();
    assertEquals(homeUrl, homeWebContext.getHomeUrl(locale));
  }

  @Test
  public void removeOne() {
    presenter.init(view);
    final User user = usersToValidate.get(0);
    List<User> usersToValidateAfter = new ArrayList<>(usersToValidate);
    usersToValidateAfter.remove(0);
    when(userService.all(any())).thenReturn(usersToValidateAfter);
    String homeUrl = "homeUrl";
    when(view.getUrl(any())).thenReturn(homeUrl);
    Button button = (Button) design.users.getColumn(REMOVE).getValueProvider().apply(user);

    button.click();

    verify(userService).delete(usersCaptor.capture());
    verify(userService, never()).validate(any(), any());
    Collection<User> users = usersCaptor.getValue();
    assertEquals(1, users.size());
    assertTrue(find(users, user.getId()).isPresent());
    verify(view).showTrayNotification(resources.message(REMOVE_DONE, 1, user.getEmail()));
    verify(userService, times(2)).all(any());
    assertEquals(usersToValidateAfter.size(), dataProvider(design.users).getItems().size());
  }

  @Test
  public void validateMany() {
    presenter.init(view);
    final User user1 = usersToValidate.get(0);
    final User user2 = usersToValidate.get(1);
    final User user3 = usersToValidate.get(2);
    design.users.select(user1);
    design.users.select(user2);
    design.users.select(user3);
    when(userService.all(any())).thenReturn(new ArrayList<>());
    String homeUrl = "homeUrl";
    when(view.getUrl(any())).thenReturn(homeUrl);

    design.validateSelected.click();

    verify(userService).validate(usersCaptor.capture(), homeWebContextCaptor.capture());
    verify(userService, never()).delete(any());
    Collection<User> users = usersCaptor.getValue();
    assertEquals(3, users.size());
    assertTrue(find(users, user1.getId()).isPresent());
    assertTrue(find(users, user2.getId()).isPresent());
    assertTrue(find(users, user3.getId()).isPresent());
    verify(view).showTrayNotification(
        resources.message(VALIDATE_DONE, 3, user1.getEmail() + resources.message(USER_SEPARATOR, 0)
            + user2.getEmail() + resources.message(USER_SEPARATOR, 1) + user3.getEmail()));
    verify(userService, times(2)).all(any());
    assertEquals(0, design.users.getSelectedItems().size());
    assertEquals(0, dataProvider(design.users).getItems().size());
    HomeWebContext homeWebContext = homeWebContextCaptor.getValue();
    assertEquals(homeUrl, homeWebContext.getHomeUrl(locale));
  }

  @Test
  public void validateMany_NoSelection() {
    presenter.init(view);
    when(userService.all(any())).thenReturn(new ArrayList<>());

    design.validateSelected.click();

    verify(userService, never()).validate(any(), any());
    verify(userService, never()).delete(any());
    verify(view).showError(resources.message(NO_SELECTION));
  }

  @Test
  public void removeMany() {
    presenter.init(view);
    final User user1 = usersToValidate.get(0);
    final User user2 = usersToValidate.get(1);
    final User user3 = usersToValidate.get(2);
    design.users.select(user1);
    design.users.select(user2);
    design.users.select(user3);
    when(userService.all(any())).thenReturn(new ArrayList<>());
    String homeUrl = "homeUrl";
    when(view.getUrl(any())).thenReturn(homeUrl);

    design.removeSelected.click();

    verify(userService).delete(usersCaptor.capture());
    verify(userService, never()).validate(any(), any());
    Collection<User> users = usersCaptor.getValue();
    assertEquals(3, users.size());
    assertTrue(find(users, user1.getId()).isPresent());
    assertTrue(find(users, user2.getId()).isPresent());
    assertTrue(find(users, user3.getId()).isPresent());
    verify(view).showTrayNotification(
        resources.message(REMOVE_DONE, 3, user1.getEmail() + resources.message(USER_SEPARATOR, 0)
            + user2.getEmail() + resources.message(USER_SEPARATOR, 1) + user3.getEmail()));
    verify(userService, times(2)).all(any());
    assertEquals(0, design.users.getSelectedItems().size());
    assertEquals(0, dataProvider(design.users).getItems().size());
  }

  @Test
  public void removeMany_NoSelection() {
    presenter.init(view);
    when(userService.all(any())).thenReturn(new ArrayList<>());

    design.removeSelected.click();

    verify(userService, never()).validate(any(), any());
    verify(userService, never()).delete(any());
    verify(view).showError(resources.message(NO_SELECTION));
  }
}
