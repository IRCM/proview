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

import static ca.qc.ircm.proview.user.web.SignasViewPresenter.EMAIL;
import static ca.qc.ircm.proview.user.web.SignasViewPresenter.HEADER;
import static ca.qc.ircm.proview.user.web.SignasViewPresenter.LABORATORY_NAME;
import static ca.qc.ircm.proview.user.web.SignasViewPresenter.NAME;
import static ca.qc.ircm.proview.user.web.SignasViewPresenter.ORGANIZATION;
import static ca.qc.ircm.proview.user.web.SignasViewPresenter.SIGN_AS;
import static ca.qc.ircm.proview.user.web.SignasViewPresenter.TITLE;
import static ca.qc.ircm.proview.user.web.SignasViewPresenter.USERS_GRID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilter;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.HomeWebContext;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
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

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SignasViewPresenterTest {
  private SignasViewPresenter presenter;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private SignasView view;
  @Mock
  private UserService userService;
  @Mock
  private AuthenticationService authenticationService;
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
  @Captor
  private ArgumentCaptor<HomeWebContext> homeWebContextCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private List<User> users;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SignasView.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SignasViewPresenter(userService, authenticationService, userWindowProvider,
        applicationName);
    users = new ArrayList<>();
    users.add(entityManager.find(User.class, 4L));
    users.add(entityManager.find(User.class, 5L));
    users.add(entityManager.find(User.class, 10L));
    users.add(entityManager.find(User.class, 11L));
    when(userService.all(any())).thenReturn(users);
    view.headerLabel = new Label();
    view.usersGrid = new Grid<>();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(userWindowProvider.get()).thenReturn(userWindow);
    presenter.init(view);
  }

  private <V> boolean containsInstanceOf(Collection<V> extensions, Class<? extends V> clazz) {
    return extensions.stream().filter(extension -> clazz.isInstance(extension)).findAny()
        .isPresent();
  }

  @Test
  public void usersGridColumns() {
    List<Column<User, ?>> columns = view.usersGrid.getColumns();

    assertEquals(EMAIL, columns.get(0).getId());
    assertTrue(containsInstanceOf(columns.get(0).getExtensions(), ComponentRenderer.class));
    assertEquals(NAME, columns.get(1).getId());
    assertEquals(LABORATORY_NAME, columns.get(2).getId());
    assertEquals(ORGANIZATION, columns.get(3).getId());
    assertEquals(SIGN_AS, columns.get(4).getId());
    assertTrue(containsInstanceOf(columns.get(4).getExtensions(), ComponentRenderer.class));
  }

  @Test
  public void usersGridOrder() {
    List<GridSortOrder<User>> sortOrders = view.usersGrid.getSortOrder();

    assertFalse(sortOrders.isEmpty());
    GridSortOrder<User> sortOrder = sortOrders.get(0);
    assertEquals(EMAIL, sortOrder.getSorted().getId());
    assertEquals(SortDirection.ASCENDING, sortOrder.getDirection());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void emailFilter() {
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
  public void styles() {
    assertTrue(view.headerLabel.getStyleName().contains(HEADER));
    assertTrue(view.headerLabel.getStyleName().contains("h1"));
    assertTrue(view.usersGrid.getStyleName().contains(USERS_GRID));
  }

  @Test
  public void captions() {
    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), view.headerLabel.getValue());
    final User user = users.get(0);
    assertEquals(resources.message(EMAIL), view.usersGrid.getColumn(EMAIL).getCaption());
    Button viewButton = (Button) view.usersGrid.getColumn(EMAIL).getValueProvider().apply(user);
    assertTrue(viewButton.getStyleName().contains(EMAIL));
    assertEquals(user.getEmail(), viewButton.getCaption());
    assertEquals(resources.message(NAME), view.usersGrid.getColumn(NAME).getCaption());
    assertEquals(resources.message(LABORATORY_NAME),
        view.usersGrid.getColumn(LABORATORY_NAME).getCaption());
    assertEquals(resources.message(ORGANIZATION),
        view.usersGrid.getColumn(ORGANIZATION).getCaption());
    assertEquals(resources.message(SIGN_AS), view.usersGrid.getColumn(SIGN_AS).getCaption());
    Button signasButton = (Button) view.usersGrid.getColumn(SIGN_AS).getValueProvider().apply(user);
    assertTrue(signasButton.getStyleName().contains(SIGN_AS));
    assertEquals(resources.message(SIGN_AS), signasButton.getCaption());
  }

  @Test
  public void users() {
    verify(userService).all(userFilterCaptor.capture());

    UserFilter userFilter = userFilterCaptor.getValue();
    assertTrue(userFilter.isNonAdmin());
    assertTrue(userFilter.isActive());
  }

  @Test
  public void viewUser() {
    final User user = users.get(0);
    Button button = (Button) view.usersGrid.getColumn(EMAIL).getValueProvider().apply(user);

    button.click();

    verify(userWindowProvider).get();
    verify(userWindow).setUser(user);
    verify(userWindow).center();
    verify(view).addWindow(userWindow);
  }

  @Test
  public void signasUser() {
    final User user = users.get(0);
    Button button = (Button) view.usersGrid.getColumn(SIGN_AS).getValueProvider().apply(user);

    button.click();

    verify(authenticationService).runAs(user);
    verify(view).showTrayNotification(resources.message(SIGN_AS + ".done", user.getEmail()));
    verify(view).navigateTo(MainView.VIEW_NAME);
  }
}
