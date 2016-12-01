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
import ca.qc.ircm.proview.web.HomeWebContext;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ClientConnector.AttachEvent;
import com.vaadin.server.ClientConnector.AttachListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
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
  @Captor
  private ArgumentCaptor<Collection<User>> usersCaptor;
  @Captor
  private ArgumentCaptor<UserFilter> userFilterCaptor;
  @Captor
  private ArgumentCaptor<HomeWebContext> homeWebContextCaptor;
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
    view.usersGrid = new Grid();
    view.activateButton = new Button();
    view.deactivateButton = new Button();
    view.clearButton = new Button();
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

    assertEquals(SELECT, columns.get(0).getPropertyId());
    assertTrue(columns.get(0).getRenderer() instanceof ComponentRenderer);
    assertEquals(EMAIL, columns.get(1).getPropertyId());
    assertEquals(NAME, columns.get(2).getPropertyId());
    assertEquals(LABORATORY_NAME, columns.get(3).getPropertyId());
    assertEquals(ORGANIZATION, columns.get(4).getPropertyId());
    assertEquals(ACTIVE, columns.get(5).getPropertyId());
    assertTrue(columns.get(5).getRenderer() instanceof ComponentRenderer);
    assertEquals(VIEW, columns.get(6).getPropertyId());
    assertTrue(columns.get(6).getRenderer() instanceof ComponentRenderer);
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
  public void emailFilter() {
    HeaderRow filterRow = view.usersGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EMAIL);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    TextChangeListener listener =
        (TextChangeListener) textField.getListeners(TextChangeEvent.class).iterator().next();
    TextChangeEvent event = mock(TextChangeEvent.class);
    when(event.getText()).thenReturn(filterValue);

    listener.textChange(event);

    GeneratedPropertyContainer container =
        (GeneratedPropertyContainer) view.usersGrid.getContainerDataSource();
    Collection<Filter> filters = container.getContainerFilters();
    assertEquals(1, filters.size());
    Filter filter = filters.iterator().next();
    assertTrue(filter instanceof SimpleStringFilter);
    SimpleStringFilter stringFilter = (SimpleStringFilter) filter;
    assertEquals(filterValue, stringFilter.getFilterString());
    assertEquals(EMAIL, stringFilter.getPropertyId());
  }

  @Test
  public void nameFilter() {
    HeaderRow filterRow = view.usersGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(NAME);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    TextChangeListener listener =
        (TextChangeListener) textField.getListeners(TextChangeEvent.class).iterator().next();
    TextChangeEvent event = mock(TextChangeEvent.class);
    when(event.getText()).thenReturn(filterValue);

    listener.textChange(event);

    GeneratedPropertyContainer container =
        (GeneratedPropertyContainer) view.usersGrid.getContainerDataSource();
    Collection<Filter> filters = container.getContainerFilters();
    assertEquals(1, filters.size());
    Filter filter = filters.iterator().next();
    assertTrue(filter instanceof SimpleStringFilter);
    SimpleStringFilter stringFilter = (SimpleStringFilter) filter;
    assertEquals(filterValue, stringFilter.getFilterString());
    assertEquals(NAME, stringFilter.getPropertyId());
  }

  @Test
  public void laboratoryNameFilter() {
    HeaderRow filterRow = view.usersGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(LABORATORY_NAME);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    TextChangeListener listener =
        (TextChangeListener) textField.getListeners(TextChangeEvent.class).iterator().next();
    TextChangeEvent event = mock(TextChangeEvent.class);
    when(event.getText()).thenReturn(filterValue);

    listener.textChange(event);

    GeneratedPropertyContainer container =
        (GeneratedPropertyContainer) view.usersGrid.getContainerDataSource();
    Collection<Filter> filters = container.getContainerFilters();
    assertEquals(1, filters.size());
    Filter filter = filters.iterator().next();
    assertTrue(filter instanceof SimpleStringFilter);
    SimpleStringFilter stringFilter = (SimpleStringFilter) filter;
    assertEquals(filterValue, stringFilter.getFilterString());
    assertEquals(LABORATORY_NAME, stringFilter.getPropertyId());
  }

  @Test
  public void organizationFilter() {
    HeaderRow filterRow = view.usersGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(ORGANIZATION);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    TextChangeListener listener =
        (TextChangeListener) textField.getListeners(TextChangeEvent.class).iterator().next();
    TextChangeEvent event = mock(TextChangeEvent.class);
    when(event.getText()).thenReturn(filterValue);

    listener.textChange(event);

    GeneratedPropertyContainer container =
        (GeneratedPropertyContainer) view.usersGrid.getContainerDataSource();
    Collection<Filter> filters = container.getContainerFilters();
    assertEquals(1, filters.size());
    Filter filter = filters.iterator().next();
    assertTrue(filter instanceof SimpleStringFilter);
    SimpleStringFilter stringFilter = (SimpleStringFilter) filter;
    assertEquals(filterValue, stringFilter.getFilterString());
    assertEquals(ORGANIZATION, stringFilter.getPropertyId());
  }

  @Test
  public void activeFilter() {
    HeaderRow filterRow = view.usersGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(ACTIVE);
    ComboBox booleanField = (ComboBox) cell.getComponent();
    boolean filterValue = true;

    booleanField.setValue(filterValue);

    GeneratedPropertyContainer container =
        (GeneratedPropertyContainer) view.usersGrid.getContainerDataSource();
    Collection<Filter> filters = container.getContainerFilters();
    assertEquals(1, filters.size());
    Filter filter = filters.iterator().next();
    assertTrue(filter instanceof Compare.Equal);
    Compare.Equal booleanFilter = (Compare.Equal) filter;
    assertEquals(filterValue, booleanFilter.getValue());
    assertEquals(ACTIVE, booleanFilter.getPropertyId());
  }

  @Test
  public void styles() {
    assertTrue(view.headerLabel.getStyleName().contains(HEADER));
    assertTrue(view.headerLabel.getStyleName().contains("h1"));
    assertTrue(view.usersGrid.getStyleName().contains(USERS_GRID));
    assertTrue(view.activateButton.getStyleName().contains(ACTIVATE));
    assertTrue(view.deactivateButton.getStyleName().contains(DEACTIVATE));
    assertTrue(view.clearButton.getStyleName().contains(CLEAR));
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
    assertEquals(resources.message(ACTIVE), view.usersGrid.getColumn(ACTIVE).getHeaderCaption());
    assertEquals(resources.message(VIEW), view.usersGrid.getColumn(VIEW).getHeaderCaption());
    assertEquals(resources.message(ACTIVATE), view.activateButton.getCaption());
    assertEquals(resources.message(DEACTIVATE), view.deactivateButton.getCaption());
    assertEquals(resources.message(CLEAR), view.clearButton.getCaption());
  }

  @Test
  public void users_Admin() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    verify(userService, times(2)).all(userFilterCaptor.capture());

    UserFilter userFilter = userFilterCaptor.getValue();
    assertTrue(userFilter.isValid());
    assertNull(userFilter.getLaboratory());
  }

  @Test
  public void users_LaboratoryManager() {
    when(authorizationService.hasAdminRole()).thenReturn(false);
    presenter.init(view);

    verify(userService, times(2)).all(userFilterCaptor.capture());

    UserFilter userFilter = userFilterCaptor.getValue();
    assertTrue(userFilter.isValid());
    assertEquals(signedUser.getLaboratory(), userFilter.getLaboratory());
  }

  @Test
  public void selectUser() {
    final User user = users.get(0);
    Container.Indexed container = view.usersGrid.getContainerDataSource();
    CheckBox checkBox = (CheckBox) container.getItem(user).getItemProperty(SELECT).getValue();
    checkBox.getListeners(AttachEvent.class)
        .forEach(l -> ((AttachListener) l).attach(mock(AttachEvent.class)));
    assertEquals(false, checkBox.getValue());

    checkBox.setValue(true);

    Collection<Object> selection = view.usersGrid.getSelectedRows();
    assertEquals(1, selection.size());
    assertTrue(selection.contains(user));
  }

  @Test
  public void deselectUser() {
    final User user = users.get(0);
    Container.Indexed container = view.usersGrid.getContainerDataSource();
    CheckBox checkBox = (CheckBox) container.getItem(user).getItemProperty(SELECT).getValue();
    checkBox.getListeners(AttachEvent.class)
        .forEach(l -> ((AttachListener) l).attach(mock(AttachEvent.class)));
    view.usersGrid.select(user);
    assertEquals(true, checkBox.getValue());

    checkBox.setValue(false);

    Collection<Object> selection = view.usersGrid.getSelectedRows();
    assertEquals(0, selection.size());
  }

  @Test
  public void activeLabel_True() {
    final User user = users.get(0);
    user.setActive(true);
    Container.Indexed container = view.usersGrid.getContainerDataSource();
    Label label = (Label) container.getItem(user).getItemProperty(ACTIVE).getValue();

    assertEquals(ContentMode.HTML, label.getContentMode());
    assertEquals(FontAwesome.CHECK.getHtml() + " " + resources.message(ACTIVE + ".true"),
        label.getValue());
  }

  @Test
  public void activeLabel_False() {
    final User user = users.get(0);
    user.setActive(false);
    Container.Indexed container = view.usersGrid.getContainerDataSource();
    Label label = (Label) container.getItem(user).getItemProperty(ACTIVE).getValue();

    assertEquals(ContentMode.HTML, label.getContentMode());
    assertEquals(FontAwesome.CLOSE.getHtml() + " " + resources.message(ACTIVE + ".false"),
        label.getValue());
  }

  @Test
  public void viewUser() {
    final User user = users.get(0);
    Container.Indexed container = view.usersGrid.getContainerDataSource();
    Button button = (Button) container.getItem(user).getItemProperty(VIEW).getValue();

    button.click();

    verify(userWindowProvider).get();
    verify(userWindow).setUser(user);
    verify(userWindow).center();
    verify(view).addWindow(userWindow);
  }

  @Test
  public void activate() {
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
    assertEquals(0, view.usersGrid.getSelectedRows().size());
    assertEquals(0, view.usersGrid.getContainerDataSource().getItemIds().size());
  }

  @Test
  public void activate_NoSelection() {
    view.activateButton.click();

    verify(userService, never()).validate(any(), any());
    verify(view).showError(any());
  }

  @Test
  public void deactivate() {
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
    assertEquals(0, view.usersGrid.getSelectedRows().size());
    assertEquals(0, view.usersGrid.getContainerDataSource().getItemIds().size());
  }

  @Test
  public void deactivate_NoSelection() {
    view.activateButton.click();

    verify(userService, never()).validate(any(), any());
    verify(view).showError(any());
  }

  @Test
  public void clear() {
    view.activateButton.click();

    verify(userService, never()).validate(any(), any());
    verify(view).showError(any());
  }
}
