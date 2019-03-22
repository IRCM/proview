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
import static ca.qc.ircm.proview.user.UserProperties.ACTIVE;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.NAME;
import static ca.qc.ircm.proview.user.web.UsersViewPresenter.ADD;
import static ca.qc.ircm.proview.user.web.UsersViewPresenter.EMPTY;
import static ca.qc.ircm.proview.user.web.UsersViewPresenter.HEADER;
import static ca.qc.ircm.proview.user.web.UsersViewPresenter.LABORATORY_NAME;
import static ca.qc.ircm.proview.user.web.UsersViewPresenter.ORGANIZATION;
import static ca.qc.ircm.proview.user.web.UsersViewPresenter.SWITCHED;
import static ca.qc.ircm.proview.user.web.UsersViewPresenter.SWITCH_USER;
import static ca.qc.ircm.proview.user.web.UsersViewPresenter.TITLE;
import static ca.qc.ircm.proview.user.web.UsersViewPresenter.USERS;
import static ca.qc.ircm.proview.user.web.UsersViewPresenter.VALIDATION;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.text.NormalizedComparator;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilter;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.SelectionModel;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.components.grid.ItemClickListener;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
public class UsersViewPresenterTest {
  @Inject
  private UsersViewPresenter presenter;
  @Inject
  private UserRepository repository;
  @MockBean
  private UserService userService;
  @MockBean
  private AuthorizationService authorizationService;
  @MockBean
  private AuthenticationService authenticationService;
  @MockBean
  private UserWindow userWindow;
  @Mock
  private UsersView view;
  @Mock
  private DataProvider<User, Void> usersProvider;
  @Mock
  private Grid.ItemClick<User> clickItemEvent;
  @Mock
  private MouseEventDetails mouseEventDetails;
  @Captor
  private ArgumentCaptor<Collection<User>> usersCaptor;
  @Captor
  private ArgumentCaptor<UserFilter> userFilterCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private UsersViewDesign design;
  private User signedUser;
  private List<User> users;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(UsersView.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    signedUser = repository.findOne(1L);
    when(authorizationService.getCurrentUser()).thenReturn(signedUser);
    users = new ArrayList<>();
    users.add(repository.findOne(4L));
    users.add(repository.findOne(5L));
    users.add(repository.findOne(10L));
    users.add(repository.findOne(11L));
    when(userService.all(any())).thenReturn(users);
    design = new UsersViewDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(clickItemEvent.getMouseEventDetails()).thenReturn(mouseEventDetails);
  }

  @Test
  public void usersGrid() {
    presenter.init(view);

    assertEquals(5, design.users.getColumns().size());
    assertEquals(EMAIL, design.users.getColumns().get(0).getId());
    assertEquals(resources.message(EMAIL), design.users.getColumn(EMAIL).getCaption());
    NormalizedComparator comparator = new NormalizedComparator();
    List<User> expectedSortedUsers = new ArrayList<>(users);
    List<User> sortedUsers = new ArrayList<>(users);
    expectedSortedUsers.sort((u1, u2) -> comparator.compare(u1.getEmail(), u2.getEmail()));
    sortedUsers.sort(design.users.getColumn(EMAIL).getComparator(SortDirection.ASCENDING));
    assertEquals(expectedSortedUsers, sortedUsers);
    expectedSortedUsers.sort((u1, u2) -> -comparator.compare(u1.getEmail(), u2.getEmail()));
    sortedUsers.sort(design.users.getColumn(EMAIL).getComparator(SortDirection.DESCENDING));
    assertEquals(expectedSortedUsers, sortedUsers);
    for (User user : users) {
      assertEquals(user.getEmail(), design.users.getColumn(EMAIL).getValueProvider().apply(user));
    }
    assertEquals(NAME, design.users.getColumns().get(1).getId());
    assertEquals(resources.message(NAME), design.users.getColumn(NAME).getCaption());
    for (User user : users) {
      assertEquals(user.getName(), design.users.getColumn(NAME).getValueProvider().apply(user));
    }
    assertEquals(LABORATORY_NAME, design.users.getColumns().get(2).getId());
    assertEquals(resources.message(LABORATORY_NAME),
        design.users.getColumn(LABORATORY_NAME).getCaption());
    for (User user : users) {
      assertEquals(user.getLaboratory().getName(),
          design.users.getColumn(LABORATORY_NAME).getValueProvider().apply(user));
    }
    assertEquals(ORGANIZATION, design.users.getColumns().get(3).getId());
    assertEquals(resources.message(ORGANIZATION),
        design.users.getColumn(ORGANIZATION).getCaption());
    for (User user : users) {
      assertEquals(user.getLaboratory().getOrganization(),
          design.users.getColumn(ORGANIZATION).getValueProvider().apply(user));
    }
    assertEquals(ACTIVE, design.users.getColumns().get(4).getId());
    assertEquals(resources.message(ACTIVE), design.users.getColumn(ACTIVE).getCaption());
    assertTrue(containsInstanceOf(design.users.getColumn(ACTIVE).getExtensions(),
        ComponentRenderer.class));
    expectedSortedUsers.sort((u1, u2) -> Boolean.compare(u1.isActive(), u2.isActive()));
    sortedUsers.sort(design.users.getColumn(ACTIVE).getComparator(SortDirection.ASCENDING));
    assertEquals(expectedSortedUsers, sortedUsers);
    expectedSortedUsers.sort((u1, u2) -> -Boolean.compare(u1.isActive(), u2.isActive()));
    sortedUsers.sort(design.users.getColumn(ACTIVE).getComparator(SortDirection.DESCENDING));
    assertEquals(expectedSortedUsers, sortedUsers);
    for (User user : users) {
      Button button = (Button) design.users.getColumn(ACTIVE).getValueProvider().apply(user);
      assertTrue(button.getStyleName().contains(ACTIVE));
      assertTrue(button.getStyleName()
          .contains(user.isActive() ? ValoTheme.BUTTON_FRIENDLY : ValoTheme.BUTTON_DANGER));
      assertEquals(user.isActive() ? VaadinIcons.EYE : VaadinIcons.EYE_SLASH, button.getIcon());
      assertEquals(resources.message(property(ACTIVE, user.isActive())), button.getCaption());
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
  @SuppressWarnings("unchecked")
  public void emailFilter() {
    presenter.init(view);
    design.users.setDataProvider(usersProvider);
    HeaderRow filterRow = design.users.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EMAIL);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    verify(usersProvider).refreshAll();
    UserWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.emailContains);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void nameFilter() {
    presenter.init(view);
    design.users.setDataProvider(usersProvider);
    HeaderRow filterRow = design.users.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(NAME);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    verify(usersProvider).refreshAll();
    UserWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.nameContains);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void laboratoryNameFilter() {
    presenter.init(view);
    design.users.setDataProvider(usersProvider);
    HeaderRow filterRow = design.users.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(LABORATORY_NAME);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    verify(usersProvider).refreshAll();
    UserWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.laboratoryNameContains);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void organizationFilter() {
    presenter.init(view);
    design.users.setDataProvider(usersProvider);
    HeaderRow filterRow = design.users.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(ORGANIZATION);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    verify(usersProvider).refreshAll();
    UserWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.organizationContains);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void activeFilter_True() {
    presenter.init(view);
    design.users.setDataProvider(usersProvider);
    HeaderRow filterRow = design.users.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(ACTIVE);
    ComboBox<Boolean> booleanField = (ComboBox<Boolean>) cell.getComponent();
    boolean filterValue = true;

    booleanField.setValue(filterValue);

    verify(usersProvider).refreshAll();
    UserWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.active);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void activeFilter_False() {
    presenter.init(view);
    design.users.setDataProvider(usersProvider);
    HeaderRow filterRow = design.users.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(ACTIVE);
    ComboBox<Boolean> booleanField = (ComboBox<Boolean>) cell.getComponent();
    boolean filterValue = false;

    booleanField.setValue(filterValue);

    verify(usersProvider).refreshAll();
    UserWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.active);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void activeFilter_Clear() {
    presenter.init(view);
    design.users.setDataProvider(usersProvider);
    HeaderRow filterRow = design.users.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(ACTIVE);
    ComboBox<Boolean> booleanField = (ComboBox<Boolean>) cell.getComponent();
    booleanField.setValue(true);

    booleanField.setValue(null);

    verify(usersProvider, times(2)).refreshAll();
    UserWebFilter filter = presenter.getFilter();
    assertNull(filter.active);
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.header.getStyleName().contains(HEADER));
    assertTrue(design.header.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.validation.getStyleName().contains(VALIDATION));
    assertTrue(design.validation.getStyleName().contains(ValoTheme.BUTTON_FRIENDLY));
    assertTrue(design.users.getStyleName().contains(USERS));
    assertTrue(design.add.getStyleName().contains(ADD));
    assertTrue(design.switchUser.getStyleName().contains(SWITCH_USER));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
    assertEquals(resources.message(VALIDATION), design.validation.getCaption());
    assertEquals(resources.message(ADD), design.add.getCaption());
    assertEquals(resources.message(SWITCH_USER), design.switchUser.getCaption());
  }

  @Test
  public void init_Admin() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    verify(userService).all(userFilterCaptor.capture());
    UserFilter userFilter = userFilterCaptor.getValue();
    assertTrue(userFilter.valid);
    assertNull(userFilter.laboratory);
    assertNull(userFilter.active);
    assertNull(userFilter.admin);
    assertTrue(design.add.isVisible());
    assertTrue(design.switchUser.isVisible());
    verify(userService).hasInvalid(null);
  }

  @Test
  public void init_LaboratoryManager() {
    when(authorizationService.hasAdminRole()).thenReturn(false);
    presenter.init(view);

    verify(userService).all(userFilterCaptor.capture());
    UserFilter userFilter = userFilterCaptor.getValue();
    assertTrue(userFilter.valid);
    assertEquals(signedUser.getLaboratory(), userFilter.laboratory);
    assertNull(userFilter.active);
    assertNull(userFilter.admin);
    assertFalse(design.add.isVisible());
    assertFalse(design.switchUser.isVisible());
    verify(userService).hasInvalid(signedUser.getLaboratory());
  }

  @Test
  public void validation_Visible() {
    when(userService.hasInvalid(any())).thenReturn(true);
    presenter.init(view);

    assertTrue(design.validation.isVisible());
  }

  @Test
  public void validation_NotVisible() {
    presenter.init(view);

    assertFalse(design.validation.isVisible());
  }

  @Test
  public void selectUser() {
    presenter.init(view);
    final User user = users.get(0);

    design.users.select(user);

    Set<User> selection = design.users.getSelectedItems();
    assertEquals(1, selection.size());
    assertTrue(selection.contains(user));
  }

  @Test
  public void activeButton_True() {
    presenter.init(view);
    final User user = users.get(0);
    user.setActive(true);
    Button button = (Button) design.users.getColumn(ACTIVE).getValueProvider().apply(user);

    assertTrue(button.getStyleName().contains(ACTIVE));
    assertTrue(button.getStyleName().contains(ValoTheme.BUTTON_FRIENDLY));
    assertEquals(VaadinIcons.EYE, button.getIcon());
    assertEquals(resources.message(property(ACTIVE, true)), button.getCaption());
  }

  @Test
  public void activeLabel_False() {
    presenter.init(view);
    final User user = users.get(0);
    user.setActive(false);
    Button button = (Button) design.users.getColumn(ACTIVE).getValueProvider().apply(user);

    assertTrue(button.getStyleName().contains(ACTIVE));
    assertTrue(button.getStyleName().contains(ValoTheme.BUTTON_DANGER));
    assertEquals(VaadinIcons.EYE_SLASH, button.getIcon());
    assertEquals(resources.message(property(ACTIVE, false)), button.getCaption());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void viewUser() {
    presenter.init(view);
    final User user = users.get(0);
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
  public void activate() {
    presenter.init(view);
    design.users.setDataProvider(usersProvider);
    final User user = users.get(0);
    user.setActive(false);
    Button button = (Button) design.users.getColumn(ACTIVE).getValueProvider().apply(user);

    button.click();

    verify(userService).activate(user);
    assertTrue(user.isActive());
    verify(usersProvider).refreshItem(user);
  }

  @Test
  public void deactivate() {
    presenter.init(view);
    design.users.setDataProvider(usersProvider);
    final User user = users.get(0);
    user.setActive(true);
    Button button = (Button) design.users.getColumn(ACTIVE).getValueProvider().apply(user);

    button.click();

    verify(userService).deactivate(user);
    assertFalse(user.isActive());
    verify(usersProvider).refreshItem(user);
  }

  @Test
  public void validate() {
    presenter.init(view);
    design.users.setDataProvider(usersProvider);
    final User user = users.get(0);
    user.setValid(false);
    user.setActive(false);
    Button button = (Button) design.users.getColumn(ACTIVE).getValueProvider().apply(user);

    button.click();

    verify(userService).activate(user);
    assertTrue(user.isActive());
    verify(usersProvider).refreshItem(user);
  }

  @Test
  public void add() {
    presenter.init(view);

    design.add.click();

    verify(view).navigateTo(RegisterView.VIEW_NAME);
  }

  @Test
  public void switchUser() {
    presenter.init(view);
    final User user = users.get(0);
    design.users.select(user);

    design.switchUser.click();

    verify(authenticationService).runAs(user);
    verify(view).showTrayNotification(resources.message(SWITCHED, user.getEmail()));
    verify(view).navigateTo(MainView.VIEW_NAME);
  }

  @Test
  public void switchUser_NoSelection() {
    presenter.init(view);

    design.switchUser.click();

    verifyZeroInteractions(authenticationService);
    verify(view).showError(resources.message(EMPTY));
  }
}
