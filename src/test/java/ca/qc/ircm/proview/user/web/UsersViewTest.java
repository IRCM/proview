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

import static ca.qc.ircm.proview.Constants.ALL;
import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.EDIT;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.ERROR_TEXT;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.clickButton;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.doubleClickItem;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.functions;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.rendererTemplate;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.user.UserProperties.ACTIVE;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.LABORATORY;
import static ca.qc.ircm.proview.user.UserProperties.NAME;
import static ca.qc.ircm.proview.user.web.UsersView.ACTIVE_BUTTON;
import static ca.qc.ircm.proview.user.web.UsersView.ADD;
import static ca.qc.ircm.proview.user.web.UsersView.EDIT_BUTTON;
import static ca.qc.ircm.proview.user.web.UsersView.HEADER;
import static ca.qc.ircm.proview.user.web.UsersView.ID;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_FAILED;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_USER;
import static ca.qc.ircm.proview.user.web.UsersView.USERS;
import static ca.qc.ircm.proview.user.web.UsersView.VIEW_LABORATORY;
import static ca.qc.ircm.proview.user.web.UsersView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.AbstractKaribuTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.text.NormalizedComparator;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.selection.SelectionModel;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Location;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link UsersView}.
 */
@ServiceTestAnnotations
public class UsersViewTest extends AbstractKaribuTestCase {
  private UsersView view;
  @Mock
  private UsersViewPresenter presenter;
  @Captor
  private ArgumentCaptor<ValueProvider<User, String>> valueProviderCaptor;
  @Captor
  private ArgumentCaptor<LitRenderer<User>> litRendererCaptor;
  @Captor
  private ArgumentCaptor<Comparator<User>> comparatorCaptor;
  @Autowired
  private UserRepository userRepository;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(UsersView.class, locale);
  private AppResources userResources = new AppResources(User.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);
  private List<User> users;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    ui.setLocale(locale);
    view = new UsersView(presenter, new UserDialog(), new LaboratoryDialog());
    view.init();
    users = userRepository.findAll();
  }

  @SuppressWarnings("unchecked")
  private void mockColumns() {
    Element usersElement = view.users.getElement();
    view.users = mock(Grid.class);
    when(view.users.getElement()).thenReturn(usersElement);
    view.edit = mock(Column.class);
    view.active = mock(Column.class);
    when(view.users.addColumn(any(LitRenderer.class))).thenReturn(view.edit, view.active);
    when(view.edit.setKey(any())).thenReturn(view.edit);
    when(view.edit.setSortable(anyBoolean())).thenReturn(view.edit);
    when(view.edit.setFlexGrow(anyInt())).thenReturn(view.edit);
    when(view.edit.setHeader(any(String.class))).thenReturn(view.edit);
    view.email = mock(Column.class);
    when(view.users.addColumn(any(ValueProvider.class), eq(EMAIL))).thenReturn(view.email);
    when(view.email.setKey(any())).thenReturn(view.email);
    when(view.email.setComparator(any(Comparator.class))).thenReturn(view.email);
    when(view.email.setHeader(any(String.class))).thenReturn(view.email);
    when(view.email.setFlexGrow(anyInt())).thenReturn(view.email);
    view.name = mock(Column.class);
    when(view.users.addColumn(any(ValueProvider.class), eq(NAME))).thenReturn(view.name);
    when(view.name.setKey(any())).thenReturn(view.name);
    when(view.name.setComparator(any(Comparator.class))).thenReturn(view.name);
    when(view.name.setHeader(any(String.class))).thenReturn(view.name);
    when(view.name.setFlexGrow(anyInt())).thenReturn(view.name);
    view.laboratory = mock(Column.class);
    when(view.users.addColumn(any(ValueProvider.class), eq(LABORATORY)))
        .thenReturn(view.laboratory);
    when(view.laboratory.setKey(any())).thenReturn(view.laboratory);
    when(view.laboratory.setComparator(any(Comparator.class))).thenReturn(view.laboratory);
    when(view.laboratory.setHeader(any(String.class))).thenReturn(view.laboratory);
    when(view.laboratory.setFlexGrow(anyInt())).thenReturn(view.laboratory);
    when(view.active.setKey(any())).thenReturn(view.active);
    when(view.active.setComparator(any(Comparator.class))).thenReturn(view.active);
    when(view.active.setHeader(any(String.class))).thenReturn(view.active);
    when(view.active.setFlexGrow(anyInt())).thenReturn(view.active);
    HeaderRow filtersRow = mock(HeaderRow.class);
    when(view.users.appendHeaderRow()).thenReturn(filtersRow);
    HeaderCell emailFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.email)).thenReturn(emailFilterCell);
    HeaderCell nameFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.name)).thenReturn(nameFilterCell);
    HeaderCell laboratoryFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.laboratory)).thenReturn(laboratoryFilterCell);
    HeaderCell activeFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.active)).thenReturn(activeFilterCell);
  }

  @Test
  public void presenter_Init() {
    verify(presenter).init(view);
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(HEADER, view.header.getId().orElse(""));
    assertEquals(USERS, view.users.getId().orElse(""));
    assertEquals(ERROR_TEXT, view.error.getId().orElse(""));
    assertEquals(ADD, view.add.getId().orElse(""));
    assertEquals(SWITCH_USER, view.switchUser.getId().orElse(""));
    assertEquals(VIEW_LABORATORY, view.viewLaboratory.getId().orElse(""));
  }

  @Test
  public void labels() {
    mockColumns();
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    verify(view.edit).setHeader(webResources.message(EDIT));
    verify(view.edit).setFooter(webResources.message(EDIT));
    verify(view.email).setHeader(userResources.message(EMAIL));
    verify(view.email).setFooter(userResources.message(EMAIL));
    verify(view.name).setHeader(userResources.message(NAME));
    verify(view.name).setFooter(userResources.message(NAME));
    verify(view.laboratory).setHeader(userResources.message(LABORATORY));
    verify(view.laboratory).setFooter(userResources.message(LABORATORY));
    verify(view.active).setHeader(userResources.message(ACTIVE));
    verify(view.active).setFooter(userResources.message(ACTIVE));
    assertEquals(webResources.message(ALL), view.emailFilter.getPlaceholder());
    assertEquals(webResources.message(ALL), view.nameFilter.getPlaceholder());
    assertEquals(webResources.message(ALL), view.laboratoryFilter.getPlaceholder());
    assertEquals(webResources.message(ALL),
        view.activeFilter.getItemLabelGenerator().apply(Optional.empty()));
    assertEquals(userResources.message(property(ACTIVE, false)),
        view.activeFilter.getItemLabelGenerator().apply(Optional.of(false)));
    assertEquals(userResources.message(property(ACTIVE, true)),
        view.activeFilter.getItemLabelGenerator().apply(Optional.of(true)));
    assertEquals(resources.message(ADD), view.add.getText());
    validateIcon(VaadinIcon.PLUS.create(), view.add.getIcon());
    assertEquals(resources.message(SWITCH_USER), view.switchUser.getText());
    validateIcon(VaadinIcon.BUG.create(), view.switchUser.getIcon());
    assertEquals(resources.message(VIEW_LABORATORY), view.viewLaboratory.getText());
    validateIcon(VaadinIcon.EDIT.create(), view.viewLaboratory.getIcon());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void localeChange() {
    mockColumns();
    view.init();
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(UsersView.class, locale);
    final AppResources userResources = new AppResources(User.class, locale);
    final AppResources webResources = new AppResources(Constants.class, locale);
    ui.setLocale(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    verify(view.edit).setHeader(webResources.message(EDIT));
    verify(view.edit).setFooter(webResources.message(EDIT));
    verify(view.email).setHeader(userResources.message(EMAIL));
    verify(view.email).setFooter(userResources.message(EMAIL));
    verify(view.name).setHeader(userResources.message(NAME));
    verify(view.name).setFooter(userResources.message(NAME));
    verify(view.laboratory).setHeader(userResources.message(LABORATORY));
    verify(view.laboratory).setFooter(userResources.message(LABORATORY));
    verify(view.active).setHeader(userResources.message(ACTIVE));
    verify(view.active).setFooter(userResources.message(ACTIVE));
    assertEquals(webResources.message(ALL), view.emailFilter.getPlaceholder());
    assertEquals(webResources.message(ALL), view.nameFilter.getPlaceholder());
    assertEquals(webResources.message(ALL), view.laboratoryFilter.getPlaceholder());
    assertEquals(webResources.message(ALL),
        view.activeFilter.getItemLabelGenerator().apply(Optional.empty()));
    assertEquals(userResources.message(property(ACTIVE, false)),
        view.activeFilter.getItemLabelGenerator().apply(Optional.of(false)));
    assertEquals(userResources.message(property(ACTIVE, true)),
        view.activeFilter.getItemLabelGenerator().apply(Optional.of(true)));
    assertEquals(resources.message(ADD), view.add.getText());
    validateIcon(VaadinIcon.PLUS.create(), view.add.getIcon());
    assertEquals(resources.message(SWITCH_USER), view.switchUser.getText());
    validateIcon(VaadinIcon.BUG.create(), view.switchUser.getIcon());
    assertEquals(resources.message(VIEW_LABORATORY), view.viewLaboratory.getText());
    validateIcon(VaadinIcon.EDIT.create(), view.viewLaboratory.getIcon());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void getPageTitle() {
    assertEquals(resources.message(TITLE, webResources.message(APPLICATION_NAME)),
        view.getPageTitle());
  }

  @Test
  public void users_SelectionMode() {
    assertTrue(view.users.getSelectionModel() instanceof SelectionModel.Single);
  }

  @Test
  public void users_Columns() {
    assertEquals(5, view.users.getColumns().size());
    assertNotNull(view.users.getColumnByKey(EDIT));
    assertFalse(view.users.getColumnByKey(EDIT).isSortable());
    assertNotNull(view.users.getColumnByKey(EMAIL));
    assertTrue(view.users.getColumnByKey(EMAIL).isSortable());
    assertNotNull(view.users.getColumnByKey(NAME));
    assertTrue(view.users.getColumnByKey(NAME).isSortable());
    assertNotNull(view.users.getColumnByKey(LABORATORY));
    assertTrue(view.users.getColumnByKey(LABORATORY).isSortable());
    assertNotNull(view.users.getColumnByKey(ACTIVE));
    assertTrue(view.users.getColumnByKey(ACTIVE).isSortable());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void users_ColumnsValueProvider() {
    doAnswer(i -> {
      User user = i.getArgument(0);
      user.setActive(!user.isActive());
      return null;
    }).when(presenter).toggleActive(any());
    mockColumns();
    when(view.users.getDataProvider()).thenReturn(mock(DataProvider.class));
    view.init();
    verify(view.users, times(2)).addColumn(litRendererCaptor.capture());
    LitRenderer<User> litRenderer = litRendererCaptor.getAllValues().get(0);
    for (User user : users) {
      assertEquals(EDIT_BUTTON, rendererTemplate(litRenderer));
      assertTrue(functions(litRenderer).containsKey("edit"));
      functions(litRenderer).get("edit").accept(user, null);
      verify(presenter).view(user);
    }
    verify(view.users).addColumn(valueProviderCaptor.capture(), eq(EMAIL));
    ValueProvider<User, String> valueProvider = valueProviderCaptor.getValue();
    for (User user : users) {
      assertEquals(user.getEmail() != null ? user.getEmail() : "", valueProvider.apply(user));
    }
    verify(view.email).setComparator(comparatorCaptor.capture());
    Comparator<User> comparator = comparatorCaptor.getValue();
    assertTrue(comparator instanceof NormalizedComparator);
    for (User user : users) {
      assertEquals(user.getEmail(),
          ((NormalizedComparator<User>) comparator).getConverter().apply(user));
    }
    verify(view.users).addColumn(valueProviderCaptor.capture(), eq(NAME));
    valueProvider = valueProviderCaptor.getValue();
    for (User user : users) {
      assertEquals(user.getName() != null ? user.getName() : "", valueProvider.apply(user));
    }
    verify(view.name).setComparator(comparatorCaptor.capture());
    comparator = comparatorCaptor.getValue();
    assertTrue(comparator instanceof NormalizedComparator);
    for (User user : users) {
      assertEquals(user.getName(),
          ((NormalizedComparator<User>) comparator).getConverter().apply(user));
    }
    verify(view.users).addColumn(valueProviderCaptor.capture(), eq(LABORATORY));
    valueProvider = valueProviderCaptor.getValue();
    for (User user : users) {
      assertEquals(user.getLaboratory().getName() != null ? user.getLaboratory().getName() : "",
          valueProvider.apply(user));
    }
    verify(view.laboratory).setComparator(comparatorCaptor.capture());
    comparator = comparatorCaptor.getValue();
    assertTrue(comparator instanceof NormalizedComparator);
    for (User user : users) {
      assertEquals(user.getLaboratory().getName(),
          ((NormalizedComparator<User>) comparator).getConverter().apply(user));
    }
    litRenderer = litRendererCaptor.getAllValues().get(1);
    for (User user : users) {
      assertEquals(ACTIVE_BUTTON, rendererTemplate(litRenderer));
      assertTrue(litRenderer.getValueProviders().containsKey("activeTheme"));
      assertEquals(
          user.isActive() ? ButtonVariant.LUMO_SUCCESS.getVariantName()
              : ButtonVariant.LUMO_ERROR.getVariantName(),
          litRenderer.getValueProviders().get("activeTheme").apply(user));
      assertTrue(litRenderer.getValueProviders().containsKey("activeValue"));
      assertEquals(userResources.message(property(ACTIVE, user.isActive())),
          litRenderer.getValueProviders().get("activeValue").apply(user));
      assertTrue(litRenderer.getValueProviders().containsKey("activeIcon"));
      assertEquals(user.isActive() ? "vaadin:eye" : "vaadin:eye-slash",
          litRenderer.getValueProviders().get("activeIcon").apply(user));
      assertTrue(functions(litRenderer).containsKey("toggleActive"));
      functions(litRenderer).get("toggleActive").accept(user, null);
      verify(presenter).toggleActive(user);
      verify(view.users.getDataProvider()).refreshItem(user);
    }
    verify(view.active).setComparator(comparatorCaptor.capture());
    comparator = comparatorCaptor.getValue();
    assertTrue(comparator.compare(active(false), active(true)) < 0);
    assertTrue(comparator.compare(active(false), active(false)) == 0);
    assertTrue(comparator.compare(active(true), active(true)) == 0);
    assertTrue(comparator.compare(active(true), active(false)) > 0);
  }

  @Test
  public void view() {
    User user = users.get(0);
    doubleClickItem(view.users, user, null);

    verify(presenter).view(user);
  }

  @Test
  public void viewLaboratory_Grid() {
    User user = users.get(0);
    doubleClickItem(view.users, user, view.laboratory);

    verify(presenter).viewLaboratory(user.getLaboratory());
  }

  private User active(boolean active) {
    User user = new User();
    user.setActive(active);
    return user;
  }

  @Test
  public void emailFilter() {
    assertEquals("", view.emailFilter.getValue());
    assertEquals(ValueChangeMode.EAGER, view.emailFilter.getValueChangeMode());
  }

  @Test
  public void filterEmail() {
    view.emailFilter.setValue("test");

    verify(presenter).filterEmail("test");
  }

  @Test
  public void nameFilter() {
    assertEquals("", view.nameFilter.getValue());
    assertEquals(ValueChangeMode.EAGER, view.nameFilter.getValueChangeMode());
  }

  @Test
  public void filterName() {
    view.nameFilter.setValue("test");

    verify(presenter).filterName("test");
  }

  @Test
  public void laboratoryFilter() {
    assertEquals("", view.laboratoryFilter.getValue());
    assertEquals(ValueChangeMode.EAGER, view.laboratoryFilter.getValueChangeMode());
  }

  @Test
  public void filterLaboratory() {
    view.laboratoryFilter.setValue("test");

    verify(presenter).filterLaboratory("test");
  }

  @Test
  public void activeFilter() {
    view.onAttach(mock(AttachEvent.class));
    assertEquals(Optional.empty(), view.activeFilter.getValue());
    List<Optional<Boolean>> values = items(view.activeFilter);
    assertEquals(3, values.size());
    assertTrue(values.contains(Optional.empty()));
    assertTrue(values.contains(Optional.of(false)));
    assertTrue(values.contains(Optional.of(true)));
  }

  @Test
  public void filterActive_False() {
    view.activeFilter.setValue(Optional.of(false));

    verify(presenter).filterActive(false);
  }

  @Test
  public void filterActive_True() {
    view.activeFilter.setValue(Optional.of(true));

    verify(presenter).filterActive(true);
  }

  @Test
  public void add() {
    clickButton(view.add);
    verify(presenter).add();
  }

  @Test
  public void switchUser() {
    clickButton(view.switchUser);
    verify(presenter).switchUser();
  }

  @Test
  public void viewLaboratory() {
    clickButton(view.viewLaboratory);
    verify(presenter).viewLaboratory();
  }

  @Test
  public void afterNavigation() {
    AfterNavigationEvent event = mock(AfterNavigationEvent.class);
    Location location = new Location(VIEW_NAME + "?" + SWITCH_FAILED);
    when(event.getLocation()).thenReturn(location);

    view.afterNavigation(event);

    verify(presenter).showError(location.getQueryParameters().getParameters(), locale);
  }
}
