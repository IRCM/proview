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
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.dataProvider;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.NAME;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.HEADER;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.LABORATORY_NAME;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.ORGANIZATION;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.REMOVE;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.REMOVED;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.TITLE;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.USERS;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.VALIDATE;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.VALIDATED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.text.NormalizedComparator;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilter;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.HomeWebContext;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.SelectionModel;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.ItemClickListener;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ValidateViewPresenterTest {
  @Inject
  private ValidateViewPresenter presenter;
  @Inject
  private UserRepository repository;
  @MockBean
  private UserService userService;
  @MockBean
  private AuthorizationService authorizationService;
  @MockBean
  private UserWindow userWindow;
  @Mock
  private ValidateView view;
  @Mock
  private Grid.ItemClick<User> clickItemEvent;
  @Mock
  private MouseEventDetails mouseEventDetails;
  @Captor
  private ArgumentCaptor<User> userCaptor;
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
    signedUser = repository.findOne(1L);
    when(authorizationService.getCurrentUser()).thenReturn(signedUser);
    usersToValidate = new ArrayList<>();
    usersToValidate.add(repository.findOne(4L));
    usersToValidate.add(repository.findOne(5L));
    usersToValidate.add(repository.findOne(10L));
    when(userService.all(any())).thenReturn(usersToValidate);
    design = new ValidateViewDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(clickItemEvent.getMouseEventDetails()).thenReturn(mouseEventDetails);
  }

  @Test
  public void users() {
    presenter.init(view);

    assertEquals(6, design.users.getColumns().size());
    assertEquals(EMAIL, design.users.getColumns().get(0).getId());
    assertEquals(resources.message(EMAIL), design.users.getColumn(EMAIL).getCaption());
    NormalizedComparator comparator = new NormalizedComparator();
    List<User> expectedSortedUsers = new ArrayList<>(usersToValidate);
    List<User> sortedUsers = new ArrayList<>(usersToValidate);
    expectedSortedUsers.sort((u1, u2) -> comparator.compare(u1.getEmail(), u2.getEmail()));
    sortedUsers.sort(design.users.getColumn(EMAIL).getComparator(SortDirection.ASCENDING));
    assertEquals(expectedSortedUsers, sortedUsers);
    expectedSortedUsers.sort((u1, u2) -> -comparator.compare(u1.getEmail(), u2.getEmail()));
    sortedUsers.sort(design.users.getColumn(EMAIL).getComparator(SortDirection.DESCENDING));
    assertEquals(expectedSortedUsers, sortedUsers);
    for (User user : usersToValidate) {
      assertEquals(user.getEmail(), design.users.getColumn(EMAIL).getValueProvider().apply(user));
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
      assertTrue(user.getEmail(), button.getStyleName().contains(ValoTheme.BUTTON_FRIENDLY));
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
      assertTrue(user.getEmail(), button.getStyleName().contains(ValoTheme.BUTTON_DANGER));
      assertTrue(user.getEmail(), button.getStyleName().contains(REMOVE));
    }
    SelectionModel<User> selectionModel = design.users.getSelectionModel();
    assertTrue(selectionModel instanceof SelectionModel.Single);
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
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
  }

  @Test
  public void usersToValidate_Admin() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.init(view);

    verify(userService).all(userFilterCaptor.capture());

    UserFilter userFilter = userFilterCaptor.getValue();
    assertFalse(userFilter.valid);
    assertNull(userFilter.laboratory);
    assertNull(userFilter.active);
  }

  @Test
  public void usersToValidate_LaboratoryManager() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(false);
    presenter.init(view);

    verify(userService).all(userFilterCaptor.capture());

    UserFilter userFilter = userFilterCaptor.getValue();
    assertFalse(userFilter.valid);
    assertEquals(signedUser.getLaboratory(), userFilter.laboratory);
    assertNull(userFilter.active);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void viewUser() {
    presenter.init(view);
    final User user = usersToValidate.get(0);
    ItemClickListener<User> listener =
        (ItemClickListener<User>) design.users.getListeners(Grid.ItemClick.class).iterator().next();
    when(clickItemEvent.getItem()).thenReturn(user);
    when(mouseEventDetails.isDoubleClick()).thenReturn(true);

    listener.itemClick(clickItemEvent);

    verify(userWindow).setValue(user);
    verify(userWindow).center();
    verify(view).addWindow(userWindow);
  }

  @Test
  public void validate() {
    presenter.init(view);
    final User user = usersToValidate.get(0);
    List<User> usersToValidateAfter = new ArrayList<>(usersToValidate);
    usersToValidateAfter.remove(0);
    when(userService.all(any())).thenReturn(usersToValidateAfter);
    String homeUrl = "homeUrl";
    when(view.getUrl(any())).thenReturn(homeUrl);
    Button button = (Button) design.users.getColumn(VALIDATE).getValueProvider().apply(user);

    button.click();

    verify(userService).validate(eq(user), homeWebContextCaptor.capture());
    verify(userService, never()).delete(any());
    verify(view).showTrayNotification(resources.message(VALIDATED, user.getEmail()));
    verify(userService, times(2)).all(any());
    assertEquals(usersToValidateAfter.size(), dataProvider(design.users).getItems().size());
    HomeWebContext homeWebContext = homeWebContextCaptor.getValue();
    assertEquals(homeUrl, homeWebContext.getHomeUrl(locale));
  }

  @Test
  public void remove() {
    presenter.init(view);
    final User user = usersToValidate.get(0);
    List<User> usersToValidateAfter = new ArrayList<>(usersToValidate);
    usersToValidateAfter.remove(0);
    when(userService.all(any())).thenReturn(usersToValidateAfter);
    String homeUrl = "homeUrl";
    when(view.getUrl(any())).thenReturn(homeUrl);
    Button button = (Button) design.users.getColumn(REMOVE).getValueProvider().apply(user);

    button.click();

    verify(userService).delete(user);
    verify(userService, never()).validate(any(), any());
    verify(view).showTrayNotification(resources.message(REMOVED, user.getEmail()));
    verify(userService, times(2)).all(any());
    assertEquals(usersToValidateAfter.size(), dataProvider(design.users).getItems().size());
  }
}
