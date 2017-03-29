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

import static ca.qc.ircm.proview.user.web.AccessViewPresenter.ACTIVATE;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.ACTIVE;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.CLEAR;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.DEACTIVATE;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.EMAIL;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.HEADER;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.LABORATORY_NAME;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.NAME;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.ORGANIZATION;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.SELECT;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.TITLE;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.USERS_GRID;
import static ca.qc.ircm.proview.user.web.AccessViewPresenter.VIEW;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
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
import java.util.Set;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    view.headerLabel = new Label();
    view.usersGrid = new Grid<>();
    view.activateButton = new Button();
    view.deactivateButton = new Button();
    view.clearButton = new Button();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(userWindowProvider.get()).thenReturn(userWindow);
  }

  private User find(Collection<User> users, long id) {
    for (User user : users) {
      if (id == user.getId()) {
        return user;
      }
    }
    return null;
  }

  private <V> boolean containsInstanceOf(Collection<V> extensions, Class<? extends V> clazz) {
    return extensions.stream().filter(extension -> clazz.isInstance(extension)).findAny()
        .isPresent();
  }

  @SuppressWarnings("unchecked")
  private ListDataProvider<User> dataProvider() {
    return (ListDataProvider<User>) view.usersGrid.getDataProvider();
  }

  @Test
  public void usersGridColumns() {
    presenter.init(view);

    List<Column<User, ?>> columns = view.usersGrid.getColumns();

    assertEquals(SELECT, columns.get(0).getId());
    assertTrue(containsInstanceOf(columns.get(0).getExtensions(), ComponentRenderer.class));
    assertEquals(EMAIL, columns.get(1).getId());
    assertEquals(NAME, columns.get(2).getId());
    assertEquals(LABORATORY_NAME, columns.get(3).getId());
    assertEquals(ORGANIZATION, columns.get(4).getId());
    assertEquals(ACTIVE, columns.get(5).getId());
    assertTrue(containsInstanceOf(columns.get(5).getExtensions(), ComponentRenderer.class));
    assertEquals(VIEW, columns.get(6).getId());
    assertTrue(containsInstanceOf(columns.get(6).getExtensions(), ComponentRenderer.class));
  }

  @Test
  public void usersGridSelection() {
    presenter.init(view);

    SelectionModel<User> selectionModel = view.usersGrid.getSelectionModel();

    assertTrue(selectionModel instanceof SelectionModel.Multi);
  }

  @Test
  public void usersGridOrder() {
    presenter.init(view);

    List<GridSortOrder<User>> sortOrders = view.usersGrid.getSortOrder();

    assertFalse(sortOrders.isEmpty());
    GridSortOrder<User> sortOrder = sortOrders.get(0);
    assertEquals(EMAIL, sortOrder.getSorted().getId());
    assertEquals(SortDirection.ASCENDING, sortOrder.getDirection());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void emailFilter() {
    presenter.init(view);
    view.usersGrid.setDataProvider(usersProvider);
    HeaderRow filterRow = view.usersGrid.getHeaderRow(1);
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
    assertEquals(filterValue, filter.getEmailContains());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void nameFilter() {
    presenter.init(view);
    view.usersGrid.setDataProvider(usersProvider);
    HeaderRow filterRow = view.usersGrid.getHeaderRow(1);
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
    assertEquals(filterValue, filter.getNameContains());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void laboratoryNameFilter() {
    presenter.init(view);
    view.usersGrid.setDataProvider(usersProvider);
    HeaderRow filterRow = view.usersGrid.getHeaderRow(1);
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
    assertEquals(filterValue, filter.getLaboratoryNameContains());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void organizationFilter() {
    presenter.init(view);
    view.usersGrid.setDataProvider(usersProvider);
    HeaderRow filterRow = view.usersGrid.getHeaderRow(1);
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
    assertEquals(filterValue, filter.getOrganizationContains());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void activeFilter_True() {
    presenter.init(view);
    view.usersGrid.setDataProvider(usersProvider);
    HeaderRow filterRow = view.usersGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(ACTIVE);
    ComboBox<Boolean> booleanField = (ComboBox<Boolean>) cell.getComponent();
    boolean filterValue = true;

    booleanField.setValue(filterValue);

    verify(usersProvider).refreshAll();
    UserWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.getActive());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void activeFilter_False() {
    presenter.init(view);
    view.usersGrid.setDataProvider(usersProvider);
    HeaderRow filterRow = view.usersGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(ACTIVE);
    ComboBox<Boolean> booleanField = (ComboBox<Boolean>) cell.getComponent();
    boolean filterValue = false;

    booleanField.setValue(filterValue);

    verify(usersProvider).refreshAll();
    UserWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.getActive());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void activeFilter_Clear() {
    presenter.init(view);
    view.usersGrid.setDataProvider(usersProvider);
    HeaderRow filterRow = view.usersGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(ACTIVE);
    ComboBox<Boolean> booleanField = (ComboBox<Boolean>) cell.getComponent();
    booleanField.setValue(true);

    booleanField.setValue(null);

    verify(usersProvider, times(2)).refreshAll();
    UserWebFilter filter = presenter.getFilter();
    assertEquals(null, filter.getActive());
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(view.headerLabel.getStyleName().contains(HEADER));
    assertTrue(view.headerLabel.getStyleName().contains("h1"));
    assertTrue(view.usersGrid.getStyleName().contains(USERS_GRID));
    assertTrue(view.activateButton.getStyleName().contains(ACTIVATE));
    assertTrue(view.deactivateButton.getStyleName().contains(DEACTIVATE));
    assertTrue(view.clearButton.getStyleName().contains(CLEAR));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), view.headerLabel.getValue());
    assertEquals(resources.message(EMAIL), view.usersGrid.getColumn(EMAIL).getCaption());
    assertEquals(resources.message(NAME), view.usersGrid.getColumn(NAME).getCaption());
    assertEquals(resources.message(LABORATORY_NAME),
        view.usersGrid.getColumn(LABORATORY_NAME).getCaption());
    assertEquals(resources.message(ORGANIZATION),
        view.usersGrid.getColumn(ORGANIZATION).getCaption());
    assertEquals(resources.message(ACTIVE), view.usersGrid.getColumn(ACTIVE).getCaption());
    assertEquals(resources.message(VIEW), view.usersGrid.getColumn(VIEW).getCaption());
    assertEquals(resources.message(ACTIVATE), view.activateButton.getCaption());
    assertEquals(resources.message(DEACTIVATE), view.deactivateButton.getCaption());
    assertEquals(resources.message(CLEAR), view.clearButton.getCaption());
  }

  @Test
  public void users_Admin() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    verify(userService).all(userFilterCaptor.capture());

    UserFilter userFilter = userFilterCaptor.getValue();
    assertTrue(userFilter.isValid());
    assertNull(userFilter.getLaboratory());
  }

  @Test
  public void users_LaboratoryManager() {
    when(authorizationService.hasAdminRole()).thenReturn(false);
    presenter.init(view);

    verify(userService).all(userFilterCaptor.capture());

    UserFilter userFilter = userFilterCaptor.getValue();
    assertTrue(userFilter.isValid());
    assertEquals(signedUser.getLaboratory(), userFilter.getLaboratory());
  }

  @Test
  public void selectUser() {
    presenter.init(view);
    final User user = users.get(0);
    CheckBox checkBox = (CheckBox) view.usersGrid.getColumn(SELECT).getValueProvider().apply(user);
    assertEquals(false, checkBox.getValue());

    checkBox.setValue(true);

    Set<User> selection = view.usersGrid.getSelectedItems();
    assertEquals(1, selection.size());
    assertTrue(selection.contains(user));
  }

  @Test
  public void deselectUser() {
    presenter.init(view);
    final User user = users.get(0);
    CheckBox checkBox = (CheckBox) view.usersGrid.getColumn(SELECT).getValueProvider().apply(user);
    view.usersGrid.select(user);
    assertEquals(true, checkBox.getValue());

    checkBox.setValue(false);

    Set<User> selection = view.usersGrid.getSelectedItems();
    assertEquals(0, selection.size());
  }

  @Test
  public void activeLabel_True() {
    presenter.init(view);
    final User user = users.get(0);
    user.setActive(true);
    Label label = (Label) view.usersGrid.getColumn(ACTIVE).getValueProvider().apply(user);

    assertEquals(ContentMode.HTML, label.getContentMode());
    assertEquals(VaadinIcons.CHECK.getHtml() + " " + resources.message(ACTIVE + ".true"),
        label.getValue());
  }

  @Test
  public void activeLabel_False() {
    presenter.init(view);
    final User user = users.get(0);
    user.setActive(false);
    Label label = (Label) view.usersGrid.getColumn(ACTIVE).getValueProvider().apply(user);

    assertEquals(ContentMode.HTML, label.getContentMode());
    assertEquals(VaadinIcons.CLOSE.getHtml() + " " + resources.message(ACTIVE + ".false"),
        label.getValue());
  }

  @Test
  public void viewUser() {
    presenter.init(view);
    final User user = users.get(0);
    Button button = (Button) view.usersGrid.getColumn(VIEW).getValueProvider().apply(user);

    button.click();

    verify(userWindowProvider).get();
    verify(userWindow).setUser(user);
    verify(userWindow).center();
    verify(view).addWindow(userWindow);
  }

  @Test
  public void activate() {
    presenter.init(view);
    final User user1 = users.get(0);
    final User user2 = users.get(1);
    final User user3 = users.get(2);
    view.usersGrid.select(user1);
    view.usersGrid.select(user2);
    view.usersGrid.select(user3);
    when(userService.all(any())).thenReturn(new ArrayList<>());

    view.activateButton.click();

    verify(userService).activate(usersCaptor.capture());
    Collection<User> users = usersCaptor.getValue();
    assertEquals(3, users.size());
    assertNotNull(find(users, user1.getId()));
    assertNotNull(find(users, user2.getId()));
    assertNotNull(find(users, user3.getId()));
    verify(view).showTrayNotification(resources.message(ACTIVATE + ".done", 3,
        user1.getEmail() + resources.message("userSeparator", 0) + user2.getEmail()
            + resources.message("userSeparator", 1) + user3.getEmail()));
    verify(userService, times(2)).all(any());
    assertEquals(0, view.usersGrid.getSelectedItems().size());
    assertEquals(0, dataProvider().getItems().size());
  }

  @Test
  public void activate_NoSelection() {
    presenter.init(view);

    view.activateButton.click();

    verify(userService, never()).validate(any(), any());
    verify(view).showError(any());
  }

  @Test
  public void deactivate() {
    presenter.init(view);
    final User user1 = users.get(0);
    final User user2 = users.get(1);
    final User user3 = users.get(2);
    view.usersGrid.select(user1);
    view.usersGrid.select(user2);
    view.usersGrid.select(user3);
    when(userService.all(any())).thenReturn(new ArrayList<>());

    view.deactivateButton.click();

    verify(userService).deactivate(usersCaptor.capture());
    Collection<User> users = usersCaptor.getValue();
    assertEquals(3, users.size());
    assertNotNull(find(users, user1.getId()));
    assertNotNull(find(users, user2.getId()));
    assertNotNull(find(users, user3.getId()));
    verify(view).showTrayNotification(resources.message(DEACTIVATE + ".done", 3,
        user1.getEmail() + resources.message("userSeparator", 0) + user2.getEmail()
            + resources.message("userSeparator", 1) + user3.getEmail()));
    verify(userService, times(2)).all(any());
    assertEquals(0, view.usersGrid.getSelectedItems().size());
    assertEquals(0, dataProvider().getItems().size());
  }

  @Test
  public void deactivate_NoSelection() {
    presenter.init(view);

    view.activateButton.click();

    verify(userService, never()).validate(any(), any());
    verify(view).showError(any());
  }

  @Test
  public void clear() {
    presenter.init(view);

    view.activateButton.click();

    verify(userService, never()).validate(any(), any());
    verify(view).showError(any());
  }
}
