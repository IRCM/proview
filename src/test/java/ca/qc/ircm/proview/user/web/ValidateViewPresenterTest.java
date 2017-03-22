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

import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.EMAIL;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.HEADER;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.LABORATORY_NAME;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.NAME;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.ORGANIZATION;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.TITLE;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.USERS_GRID;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.VALIDATE;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.VALIDATE_SELECTED_BUTTON;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.VIEW;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.sort.SortOrder;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.Column;
import com.vaadin.v7.ui.Grid.SelectionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    view.headerLabel = new Label();
    view.usersGrid = new Grid();
    view.validateSelectedButton = new Button();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(userWindowProvider.get()).thenReturn(userWindow);
    presenter.init(view);
  }

  private User find(Collection<User> users, long id) {
    for (User user : users) {
      if (id == user.getId()) {
        return user;
      }
    }
    return null;
  }

  @Test
  public void usersGridColumns() {
    List<Column> columns = view.usersGrid.getColumns();

    assertEquals(EMAIL, columns.get(0).getPropertyId());
    assertEquals(NAME, columns.get(1).getPropertyId());
    assertEquals(LABORATORY_NAME, columns.get(2).getPropertyId());
    assertEquals(ORGANIZATION, columns.get(3).getPropertyId());
    assertEquals(VIEW, columns.get(4).getPropertyId());
    assertEquals(VALIDATE, columns.get(5).getPropertyId());
  }

  @Test
  public void usersGridSelection() {
    SelectionModel selectionModel = view.usersGrid.getSelectionModel();

    assertTrue(selectionModel instanceof SelectionModel.Multi);
  }

  @Test
  public void usersGridOrder() {
    List<SortOrder> sortOrders = view.usersGrid.getSortOrder();

    assertFalse(sortOrders.isEmpty());
    SortOrder sortOrder = sortOrders.get(0);
    assertEquals(EMAIL, sortOrder.getPropertyId());
    assertEquals(SortDirection.ASCENDING, sortOrder.getDirection());
  }

  @Test
  public void styles() {
    assertTrue(view.headerLabel.getStyleName().contains(HEADER));
    assertTrue(view.headerLabel.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(view.usersGrid.getStyleName().contains(USERS_GRID));
    assertTrue(view.validateSelectedButton.getStyleName().contains(VALIDATE_SELECTED_BUTTON));
  }

  @Test
  public void captions() {
    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), view.headerLabel.getValue());
    assertEquals(resources.message(EMAIL), view.usersGrid.getColumn(EMAIL).getHeaderCaption());
    assertEquals(resources.message(NAME), view.usersGrid.getColumn(NAME).getHeaderCaption());
    assertEquals(resources.message(LABORATORY_NAME),
        view.usersGrid.getColumn(LABORATORY_NAME).getHeaderCaption());
    assertEquals(resources.message(ORGANIZATION),
        view.usersGrid.getColumn(ORGANIZATION).getHeaderCaption());
    assertEquals(resources.message(VIEW), view.usersGrid.getColumn(VIEW).getHeaderCaption());
    assertEquals(resources.message(VALIDATE),
        view.usersGrid.getColumn(VALIDATE).getHeaderCaption());
    assertEquals(resources.message(VALIDATE_SELECTED_BUTTON),
        view.validateSelectedButton.getCaption());
  }

  @Test
  public void usersToValidate_Admin() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    verify(userService, times(2)).all(userFilterCaptor.capture());

    UserFilter userFilter = userFilterCaptor.getValue();
    assertTrue(userFilter.isInvalid());
    assertNull(userFilter.getLaboratory());
  }

  @Test
  public void usersToValidate_LaboratoryManager() {
    when(authorizationService.hasAdminRole()).thenReturn(false);
    presenter.init(view);

    verify(userService, times(2)).all(userFilterCaptor.capture());

    UserFilter userFilter = userFilterCaptor.getValue();
    assertTrue(userFilter.isInvalid());
    assertEquals(signedUser.getLaboratory(), userFilter.getLaboratory());
  }

  @Test
  public void viewUser() {
    final User user = usersToValidate.get(0);
    Container.Indexed container = view.usersGrid.getContainerDataSource();
    Button button = (Button) container.getItem(user).getItemProperty(VIEW).getValue();

    button.click();

    verify(userWindowProvider).get();
    verify(userWindow).setUser(user);
    verify(userWindow).center();
    verify(view).addWindow(userWindow);
  }

  @Test
  public void validateOne() {
    final User user = usersToValidate.get(0);
    List<User> usersToValidateAfter = new ArrayList<>(usersToValidate);
    usersToValidateAfter.remove(0);
    when(userService.all(any())).thenReturn(usersToValidateAfter);
    String homeUrl = "homeUrl";
    when(view.getUrl(any())).thenReturn(homeUrl);
    Container.Indexed container = view.usersGrid.getContainerDataSource();
    Button button = (Button) container.getItem(user).getItemProperty(VALIDATE).getValue();

    button.click();

    verify(userService).validate(usersCaptor.capture(), homeWebContextCaptor.capture());
    Collection<User> users = usersCaptor.getValue();
    assertEquals(1, users.size());
    assertNotNull(find(users, user.getId()));
    verify(view).showTrayNotification(resources.message("done", 1, user.getEmail()));
    verify(userService, times(2)).all(any());
    assertEquals(usersToValidateAfter.size(),
        view.usersGrid.getContainerDataSource().getItemIds().size());
    HomeWebContext homeWebContext = homeWebContextCaptor.getValue();
    assertEquals(homeUrl, homeWebContext.getHomeUrl(locale));
  }

  @Test
  public void validateMany() {
    final User user1 = usersToValidate.get(0);
    final User user2 = usersToValidate.get(1);
    final User user3 = usersToValidate.get(2);
    view.usersGrid.select(user1);
    view.usersGrid.select(user2);
    view.usersGrid.select(user3);
    when(userService.all(any())).thenReturn(new ArrayList<>());
    String homeUrl = "homeUrl";
    when(view.getUrl(any())).thenReturn(homeUrl);

    view.validateSelectedButton.click();

    verify(userService).validate(usersCaptor.capture(), homeWebContextCaptor.capture());
    Collection<User> users = usersCaptor.getValue();
    assertEquals(3, users.size());
    assertNotNull(find(users, user1.getId()));
    assertNotNull(find(users, user2.getId()));
    assertNotNull(find(users, user3.getId()));
    verify(view).showTrayNotification(
        resources.message("done", 3, user1.getEmail() + resources.message("userSeparator", 0)
            + user2.getEmail() + resources.message("userSeparator", 1) + user3.getEmail()));
    verify(userService, times(2)).all(any());
    assertEquals(0, view.usersGrid.getSelectedRows().size());
    assertEquals(0, view.usersGrid.getContainerDataSource().getItemIds().size());
    HomeWebContext homeWebContext = homeWebContextCaptor.getValue();
    assertEquals(homeUrl, homeWebContext.getHomeUrl(locale));
  }

  @Test
  public void validateMany_NoSelection() {
    when(userService.all(any())).thenReturn(new ArrayList<>());

    view.validateSelectedButton.click();

    verify(userService, never()).validate(any(), any());
    verify(view).showError(any());
  }
}
