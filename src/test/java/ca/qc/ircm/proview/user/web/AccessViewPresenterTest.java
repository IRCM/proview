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
import static ca.qc.ircm.proview.user.UserProperties.ACTIVE;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.NAME;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.ACTIVATE;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.ACTIVATED;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.CLEAR;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.DEACTIVATE;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.DEACTIVATED;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.HEADER;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.LABORATORY_NAME;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.ORGANIZATION;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.SELECT;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.TITLE;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.USERS_GRID;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilter;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.SelectionModel;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class AccessViewPresenterTest {
  private AccessViewPresenter presenter;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AccessView view;
  @Mock
  private UserService userService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Provider<UserWindow> userWindowProvider;
  @Mock
  private UserWindow userWindow;
  @Mock
  private DataProvider<User, Void> usersProvider;
  @Captor
  private ArgumentCaptor<Collection<User>> usersCaptor;
  @Captor
  private ArgumentCaptor<UserFilter> userFilterCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private AccessViewDesign design;
  private User signedUser;
  private List<User> users;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(AccessView.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new AccessViewPresenter(userService, authorizationService, userWindowProvider,
        applicationName);
    signedUser = entityManager.find(User.class, 1L);
    when(authorizationService.getCurrentUser()).thenReturn(signedUser);
    users = new ArrayList<>();
    users.add(entityManager.find(User.class, 4L));
    users.add(entityManager.find(User.class, 5L));
    users.add(entityManager.find(User.class, 10L));
    users.add(entityManager.find(User.class, 11L));
    when(userService.all(any())).thenReturn(users);
    design = new AccessViewDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(userWindowProvider.get()).thenReturn(userWindow);
  }

  @Test
  public void usersGrid() {
    presenter.init(view);

    assertEquals(6, design.usersGrid.getColumns().size());
    assertEquals(SELECT, design.usersGrid.getColumns().get(0).getId());
    assertEquals(resources.message(SELECT), design.usersGrid.getColumn(SELECT).getCaption());
    assertTrue(containsInstanceOf(design.usersGrid.getColumn(SELECT).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.usersGrid.getColumn(SELECT).isSortable());
    for (User user : users) {
      CheckBox field = (CheckBox) design.usersGrid.getColumn(SELECT).getValueProvider().apply(user);
      assertTrue(field.getStyleName().contains(SELECT));
    }
    assertEquals(EMAIL, design.usersGrid.getColumns().get(1).getId());
    assertEquals(resources.message(EMAIL), design.usersGrid.getColumn(EMAIL).getCaption());
    assertTrue(containsInstanceOf(design.usersGrid.getColumn(EMAIL).getExtensions(),
        ComponentRenderer.class));
    Collator collator = Collator.getInstance(locale);
    List<User> expectedSortedUsers = new ArrayList<>(users);
    List<User> sortedUsers = new ArrayList<>(users);
    expectedSortedUsers.sort((u1, u2) -> collator.compare(u1.getEmail(), u2.getEmail()));
    sortedUsers.sort(design.usersGrid.getColumn(EMAIL).getComparator(SortDirection.ASCENDING));
    assertEquals(expectedSortedUsers, sortedUsers);
    expectedSortedUsers.sort((u1, u2) -> -collator.compare(u1.getEmail(), u2.getEmail()));
    sortedUsers.sort(design.usersGrid.getColumn(EMAIL).getComparator(SortDirection.DESCENDING));
    assertEquals(expectedSortedUsers, sortedUsers);
    for (User user : users) {
      Button button = (Button) design.usersGrid.getColumn(EMAIL).getValueProvider().apply(user);
      assertEquals(user.getEmail(), button.getCaption());
      assertTrue(button.getStyleName().contains(EMAIL));
    }
    assertEquals(NAME, design.usersGrid.getColumns().get(2).getId());
    assertEquals(resources.message(NAME), design.usersGrid.getColumn(NAME).getCaption());
    for (User user : users) {
      assertEquals(user.getName(), design.usersGrid.getColumn(NAME).getValueProvider().apply(user));
    }
    assertEquals(LABORATORY_NAME, design.usersGrid.getColumns().get(3).getId());
    assertEquals(resources.message(LABORATORY_NAME),
        design.usersGrid.getColumn(LABORATORY_NAME).getCaption());
    for (User user : users) {
      assertEquals(user.getLaboratory().getName(),
          design.usersGrid.getColumn(LABORATORY_NAME).getValueProvider().apply(user));
    }
    assertEquals(ORGANIZATION, design.usersGrid.getColumns().get(4).getId());
    assertEquals(resources.message(ORGANIZATION),
        design.usersGrid.getColumn(ORGANIZATION).getCaption());
    for (User user : users) {
      assertEquals(user.getLaboratory().getOrganization(),
          design.usersGrid.getColumn(ORGANIZATION).getValueProvider().apply(user));
    }
    assertEquals(ACTIVE, design.usersGrid.getColumns().get(5).getId());
    assertEquals(resources.message(ACTIVE), design.usersGrid.getColumn(ACTIVE).getCaption());
    assertTrue(containsInstanceOf(design.usersGrid.getColumn(ACTIVE).getExtensions(),
        ComponentRenderer.class));
    expectedSortedUsers.sort((u1, u2) -> Boolean.compare(u1.isActive(), u2.isActive()));
    sortedUsers.sort(design.usersGrid.getColumn(ACTIVE).getComparator(SortDirection.ASCENDING));
    assertEquals(expectedSortedUsers, sortedUsers);
    expectedSortedUsers.sort((u1, u2) -> -Boolean.compare(u1.isActive(), u2.isActive()));
    sortedUsers.sort(design.usersGrid.getColumn(ACTIVE).getComparator(SortDirection.DESCENDING));
    assertEquals(expectedSortedUsers, sortedUsers);
    for (User user : users) {
      Label label = (Label) design.usersGrid.getColumn(ACTIVE).getValueProvider().apply(user);
      assertTrue(label.getStyleName().contains(ACTIVE));
      assertEquals(ContentMode.HTML, label.getContentMode());
      VaadinIcons activeIcon = user.isActive() ? VaadinIcons.CHECK : VaadinIcons.CLOSE;
      String activeValue =
          activeIcon.getHtml() + " " + resources.message(property(ACTIVE, user.isActive()));
      assertEquals(activeValue, label.getValue());
    }
    SelectionModel<User> selectionModel = design.usersGrid.getSelectionModel();
    assertTrue(selectionModel instanceof SelectionModel.Multi);
    List<GridSortOrder<User>> sortOrders = design.usersGrid.getSortOrder();
    assertFalse(sortOrders.isEmpty());
    GridSortOrder<User> sortOrder = sortOrders.get(0);
    assertEquals(EMAIL, sortOrder.getSorted().getId());
    assertEquals(SortDirection.ASCENDING, sortOrder.getDirection());
  }

  @Test
  public void users_SelectVisiblity() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    when(authorizationService.getCurrentUser()).thenReturn(users.get(0));
    presenter.init(view);

    for (User user : users) {
      assertEquals(user.getId() != users.get(0).getId(),
          ((CheckBox) (design.usersGrid.getColumn(SELECT).getValueProvider().apply(user)))
              .isVisible());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void emailFilter() {
    presenter.init(view);
    design.usersGrid.setDataProvider(usersProvider);
    HeaderRow filterRow = design.usersGrid.getHeaderRow(1);
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
    design.usersGrid.setDataProvider(usersProvider);
    HeaderRow filterRow = design.usersGrid.getHeaderRow(1);
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
    design.usersGrid.setDataProvider(usersProvider);
    HeaderRow filterRow = design.usersGrid.getHeaderRow(1);
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
    design.usersGrid.setDataProvider(usersProvider);
    HeaderRow filterRow = design.usersGrid.getHeaderRow(1);
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
    design.usersGrid.setDataProvider(usersProvider);
    HeaderRow filterRow = design.usersGrid.getHeaderRow(1);
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
    design.usersGrid.setDataProvider(usersProvider);
    HeaderRow filterRow = design.usersGrid.getHeaderRow(1);
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
    design.usersGrid.setDataProvider(usersProvider);
    HeaderRow filterRow = design.usersGrid.getHeaderRow(1);
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

    assertTrue(design.headerLabel.getStyleName().contains(HEADER));
    assertTrue(design.headerLabel.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.usersGrid.getStyleName().contains(USERS_GRID));
    assertTrue(design.activateButton.getStyleName().contains(ACTIVATE));
    assertTrue(design.deactivateButton.getStyleName().contains(DEACTIVATE));
    assertTrue(design.clearButton.getStyleName().contains(CLEAR));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.headerLabel.getValue());
    assertEquals(resources.message(ACTIVATE), design.activateButton.getCaption());
    assertEquals(resources.message(DEACTIVATE), design.deactivateButton.getCaption());
    assertEquals(resources.message(CLEAR), design.clearButton.getCaption());
  }

  @Test
  public void users_Admin() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    verify(userService).all(userFilterCaptor.capture());

    UserFilter userFilter = userFilterCaptor.getValue();
    assertTrue(userFilter.valid);
    assertNull(userFilter.laboratory);
    assertNull(userFilter.active);
    assertNull(userFilter.admin);
  }

  @Test
  public void users_LaboratoryManager() {
    when(authorizationService.hasAdminRole()).thenReturn(false);
    presenter.init(view);

    verify(userService).all(userFilterCaptor.capture());

    UserFilter userFilter = userFilterCaptor.getValue();
    assertTrue(userFilter.valid);
    assertEquals(signedUser.getLaboratory(), userFilter.laboratory);
    assertNull(userFilter.active);
    assertNull(userFilter.admin);
  }

  @Test
  public void selectUser() {
    presenter.init(view);
    final User user = users.get(0);
    CheckBox checkBox =
        (CheckBox) design.usersGrid.getColumn(SELECT).getValueProvider().apply(user);
    assertEquals(false, checkBox.getValue());

    checkBox.setValue(true);

    Set<User> selection = design.usersGrid.getSelectedItems();
    assertEquals(1, selection.size());
    assertTrue(selection.contains(user));
  }

  @Test
  public void deselectUser() {
    presenter.init(view);
    final User user = users.get(0);
    CheckBox checkBox =
        (CheckBox) design.usersGrid.getColumn(SELECT).getValueProvider().apply(user);
    design.usersGrid.select(user);
    assertEquals(true, checkBox.getValue());

    checkBox.setValue(false);

    Set<User> selection = design.usersGrid.getSelectedItems();
    assertEquals(0, selection.size());
  }

  @Test
  public void activeLabel_True() {
    presenter.init(view);
    final User user = users.get(0);
    user.setActive(true);
    Label label = (Label) design.usersGrid.getColumn(ACTIVE).getValueProvider().apply(user);

    assertEquals(ContentMode.HTML, label.getContentMode());
    assertEquals(VaadinIcons.CHECK.getHtml() + " " + resources.message(property(ACTIVE, true)),
        label.getValue());
  }

  @Test
  public void activeLabel_False() {
    presenter.init(view);
    final User user = users.get(0);
    user.setActive(false);
    Label label = (Label) design.usersGrid.getColumn(ACTIVE).getValueProvider().apply(user);

    assertEquals(ContentMode.HTML, label.getContentMode());
    assertEquals(VaadinIcons.CLOSE.getHtml() + " " + resources.message(property(ACTIVE, false)),
        label.getValue());
  }

  @Test
  public void viewUser() {
    presenter.init(view);
    final User user = users.get(0);
    Button button = (Button) design.usersGrid.getColumn(EMAIL).getValueProvider().apply(user);

    button.click();

    verify(userWindowProvider).get();
    verify(userWindow).setValue(user);
    verify(userWindow).center();
    verify(view).addWindow(userWindow);
  }

  @Test
  public void activate() {
    presenter.init(view);
    final User user1 = users.get(0);
    final User user2 = users.get(1);
    final User user3 = users.get(2);
    design.usersGrid.select(user1);
    design.usersGrid.select(user2);
    design.usersGrid.select(user3);
    when(userService.all(any())).thenReturn(new ArrayList<>());

    design.activateButton.click();

    verify(userService).activate(usersCaptor.capture());
    Collection<User> users = usersCaptor.getValue();
    assertEquals(3, users.size());
    assertTrue(find(users, user1.getId()).isPresent());
    assertTrue(find(users, user2.getId()).isPresent());
    assertTrue(find(users, user3.getId()).isPresent());
    verify(view).showTrayNotification(
        resources.message(ACTIVATED, 3, user1.getEmail() + resources.message("userSeparator", 0)
            + user2.getEmail() + resources.message("userSeparator", 1) + user3.getEmail()));
    verify(userService, times(2)).all(any());
    assertEquals(0, design.usersGrid.getSelectedItems().size());
    assertEquals(0, dataProvider(design.usersGrid).getItems().size());
  }

  @Test
  public void activate_NoSelection() {
    presenter.init(view);

    design.activateButton.click();

    verify(userService, never()).validate(any(), any());
    verify(view).showError(any());
  }

  @Test
  public void deactivate() {
    presenter.init(view);
    final User user1 = users.get(0);
    final User user2 = users.get(1);
    final User user3 = users.get(2);
    design.usersGrid.select(user1);
    design.usersGrid.select(user2);
    design.usersGrid.select(user3);
    when(userService.all(any())).thenReturn(new ArrayList<>());

    design.deactivateButton.click();

    verify(userService).deactivate(usersCaptor.capture());
    Collection<User> users = usersCaptor.getValue();
    assertEquals(3, users.size());
    assertTrue(find(users, user1.getId()).isPresent());
    assertTrue(find(users, user2.getId()).isPresent());
    assertTrue(find(users, user3.getId()).isPresent());
    verify(view).showTrayNotification(
        resources.message(DEACTIVATED, 3, user1.getEmail() + resources.message("userSeparator", 0)
            + user2.getEmail() + resources.message("userSeparator", 1) + user3.getEmail()));
    verify(userService, times(2)).all(any());
    assertEquals(0, design.usersGrid.getSelectedItems().size());
    assertEquals(0, dataProvider(design.usersGrid).getItems().size());
  }

  @Test
  public void deactivate_NoSelection() {
    presenter.init(view);

    design.activateButton.click();

    verify(userService, never()).validate(any(), any());
    verify(view).showError(any());
  }

  @Test
  public void clear() {
    presenter.init(view);

    design.activateButton.click();

    verify(userService, never()).validate(any(), any());
    verify(view).showError(any());
  }
}
