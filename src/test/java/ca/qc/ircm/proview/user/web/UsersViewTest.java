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
import static ca.qc.ircm.proview.user.web.UsersView.USERS_REQUIRED;
import static ca.qc.ircm.proview.user.web.UsersView.VIEW_LABORATORY;
import static ca.qc.ircm.proview.user.web.UsersView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.SwitchUserService;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.LaboratoryService;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.UserService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.selection.SelectionModel;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link UsersView}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UsersViewTest extends SpringUIUnitTest {
  private UsersView view;
  @MockBean
  private UserService service;
  @MockBean
  private LaboratoryService laboratoryService;
  @MockBean
  private SwitchUserService switchUserService;
  @Autowired
  private UserRepository repository;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @Autowired
  private AuthenticatedUser authenticatedUser;
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
    UI.getCurrent().setLocale(locale);
    users = repository.findAll();
    when(service.all(any())).thenReturn(users);
    when(service.all(any(), any())).thenReturn(users);
    view = navigate(UsersView.class);
  }

  private int indexOfColumn(String property) {
    return test(view.users).getColumnPosition(property);
  }

  private User email(String email) {
    User user = new User();
    user.setEmail(email);
    return user;
  }

  private User name(String name) {
    User user = new User();
    user.setName(name);
    return user;
  }

  private User laboratory(String name) {
    User user = new User();
    user.setLaboratory(new Laboratory());
    user.getLaboratory().setName(name);
    return user;
  }

  private User active(boolean active) {
    User user = new User();
    user.setActive(active);
    return user;
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
    assertEquals(resources.message(HEADER), view.header.getText());
    HeaderRow headerRow = view.users.getHeaderRows().get(0);
    FooterRow footerRow = view.users.getFooterRows().get(0);
    assertEquals(webResources.message(EDIT), headerRow.getCell(view.edit).getText());
    assertEquals(webResources.message(EDIT), footerRow.getCell(view.edit).getText());
    assertEquals(userResources.message(EMAIL), headerRow.getCell(view.email).getText());
    assertEquals(userResources.message(EMAIL), footerRow.getCell(view.email).getText());
    assertEquals(userResources.message(NAME), headerRow.getCell(view.name).getText());
    assertEquals(userResources.message(NAME), footerRow.getCell(view.name).getText());
    assertEquals(userResources.message(LABORATORY), headerRow.getCell(view.laboratory).getText());
    assertEquals(userResources.message(LABORATORY), footerRow.getCell(view.laboratory).getText());
    assertEquals(userResources.message(ACTIVE), headerRow.getCell(view.active).getText());
    assertEquals(userResources.message(ACTIVE), footerRow.getCell(view.active).getText());
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
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(UsersView.class, locale);
    final AppResources userResources = new AppResources(User.class, locale);
    final AppResources webResources = new AppResources(Constants.class, locale);
    UI.getCurrent().setLocale(locale);
    assertEquals(resources.message(HEADER), view.header.getText());
    HeaderRow headerRow = view.users.getHeaderRows().get(0);
    FooterRow footerRow = view.users.getFooterRows().get(0);
    assertEquals(webResources.message(EDIT), headerRow.getCell(view.edit).getText());
    assertEquals(webResources.message(EDIT), footerRow.getCell(view.edit).getText());
    assertEquals(userResources.message(EMAIL), headerRow.getCell(view.email).getText());
    assertEquals(userResources.message(EMAIL), footerRow.getCell(view.email).getText());
    assertEquals(userResources.message(NAME), headerRow.getCell(view.name).getText());
    assertEquals(userResources.message(NAME), footerRow.getCell(view.name).getText());
    assertEquals(userResources.message(LABORATORY), headerRow.getCell(view.laboratory).getText());
    assertEquals(userResources.message(LABORATORY), footerRow.getCell(view.laboratory).getText());
    assertEquals(userResources.message(ACTIVE), headerRow.getCell(view.active).getText());
    assertEquals(userResources.message(ACTIVE), footerRow.getCell(view.active).getText());
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
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void users_Manager() {
    verify(service).all(null, authenticatedUser.getUser().get().getLaboratory());
    List<User> users = items(view.users);
    assertEquals(this.users.size(), users.size());
    for (User user : this.users) {
      assertTrue(users.contains(user), user.toString());
    }
    assertEquals(0, view.users.getSelectedItems().size());
    users.forEach(user -> view.users.select(user));
    assertEquals(1, view.users.getSelectedItems().size());
    assertTrue(view.active.isVisible());
    assertTrue(view.add.isVisible());
    assertFalse(view.switchUser.isVisible());
    assertTrue(view.viewLaboratory.isVisible());
  }

  @Test
  public void users_Admin() {
    verify(service).all(null);
    List<User> users = items(view.users);
    assertEquals(this.users.size(), users.size());
    for (User user : this.users) {
      assertTrue(users.contains(user), user.toString());
    }
    assertEquals(0, view.users.getSelectedItems().size());
    users.forEach(user -> view.users.select(user));
    assertEquals(1, view.users.getSelectedItems().size());
    assertTrue(view.active.isVisible());
    assertTrue(view.add.isVisible());
    assertTrue(view.switchUser.isVisible());
    assertTrue(view.viewLaboratory.isVisible());
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
  public void users_ColumnsValueProvider() {
    when(service.get(any(Long.class))).thenAnswer(i -> repository.findById(i.getArgument(0)));
    for (int i = 0; i < users.size(); i++) {
      User user = users.get(i);
      Renderer<User> editRawRenderer = view.users.getColumnByKey(EDIT).getRenderer();
      assertTrue(editRawRenderer instanceof LitRenderer<User>);
      LitRenderer<User> editRenderer = (LitRenderer<User>) editRawRenderer;
      assertEquals(EDIT_BUTTON, rendererTemplate(editRenderer));
      assertTrue(functions(editRenderer).containsKey("edit"));
      functions(editRenderer).get("edit").accept(user, null);
      UserDialog dialog = $(UserDialog.class).last();
      assertEquals(user, dialog.getUser());
      verify(service).get(user.getId());
      assertEquals(user.getEmail() != null ? user.getEmail() : "",
          test(view.users).getCellText(i, indexOfColumn(EMAIL)));
      assertEquals(user.getName() != null ? user.getName() : "",
          test(view.users).getCellText(i, indexOfColumn(NAME)));
      assertEquals(user.getLaboratory().getName() != null ? user.getLaboratory().getName() : "",
          test(view.users).getCellText(i, indexOfColumn(LABORATORY)));
      Renderer<User> activeRawRenderer = view.users.getColumnByKey(ACTIVE).getRenderer();
      assertTrue(activeRawRenderer instanceof LitRenderer<User>);
      LitRenderer<User> activeRenderer = (LitRenderer<User>) activeRawRenderer;
      assertEquals(ACTIVE_BUTTON, rendererTemplate(activeRenderer));
      assertTrue(activeRenderer.getValueProviders().containsKey("activeTheme"));
      assertEquals(
          user.isActive() ? ButtonVariant.LUMO_SUCCESS.getVariantName()
              : ButtonVariant.LUMO_ERROR.getVariantName(),
          activeRenderer.getValueProviders().get("activeTheme").apply(user));
      assertTrue(activeRenderer.getValueProviders().containsKey("activeValue"));
      assertEquals(userResources.message(property(ACTIVE, user.isActive())),
          activeRenderer.getValueProviders().get("activeValue").apply(user));
      assertTrue(activeRenderer.getValueProviders().containsKey("activeIcon"));
      assertEquals(user.isActive() ? "vaadin:eye" : "vaadin:eye-slash",
          activeRenderer.getValueProviders().get("activeIcon").apply(user));
      assertTrue(functions(activeRenderer).containsKey("toggleActive"));
      boolean active = user.isActive();
      functions(activeRenderer).get("toggleActive").accept(user, null);
      verify(service, atLeastOnce()).save(user, null);
      assertEquals(!active, user.isActive());
    }
  }

  @Test
  public void users_EmailColumnComparator() {
    Comparator<User> comparator =
        test(view.users).getColumn(EMAIL).getComparator(SortDirection.ASCENDING);
    assertEquals(0, comparator.compare(email("éê"), email("ee")));
    assertTrue(comparator.compare(email("a"), email("e")) < 0);
    assertTrue(comparator.compare(email("a"), email("é")) < 0);
    assertTrue(comparator.compare(email("e"), email("a")) > 0);
    assertTrue(comparator.compare(email("é"), email("a")) > 0);
  }

  @Test
  public void users_NameColumnComparator() {
    Comparator<User> comparator =
        test(view.users).getColumn(NAME).getComparator(SortDirection.ASCENDING);
    assertEquals(0, comparator.compare(name("éê"), name("ee")));
    assertTrue(comparator.compare(name("a"), name("e")) < 0);
    assertTrue(comparator.compare(name("a"), name("é")) < 0);
    assertTrue(comparator.compare(name("e"), name("a")) > 0);
    assertTrue(comparator.compare(name("é"), name("a")) > 0);
  }

  @Test
  public void users_LaboratoryColumnComparator() {
    Comparator<User> comparator =
        test(view.users).getColumn(LABORATORY).getComparator(SortDirection.ASCENDING);
    assertEquals(0, comparator.compare(laboratory("éê"), laboratory("ee")));
    assertTrue(comparator.compare(laboratory("a"), laboratory("e")) < 0);
    assertTrue(comparator.compare(laboratory("a"), laboratory("é")) < 0);
    assertTrue(comparator.compare(laboratory("e"), laboratory("a")) > 0);
    assertTrue(comparator.compare(laboratory("é"), laboratory("a")) > 0);
  }

  @Test
  public void users_ActiveColumnComparator() {
    Comparator<User> comparator =
        test(view.users).getColumn(ACTIVE).getComparator(SortDirection.ASCENDING);
    assertTrue(comparator.compare(active(false), active(true)) < 0);
    assertTrue(comparator.compare(active(false), active(false)) == 0);
    assertTrue(comparator.compare(active(true), active(true)) == 0);
    assertTrue(comparator.compare(active(true), active(false)) > 0);
  }

  @Test
  public void users_DoubleClick() {
    when(service.get(any(Long.class))).thenAnswer(i -> repository.findById(i.getArgument(0)));
    User user = users.get(0);
    test(view.users).doubleClickRow(0);

    UserDialog dialog = $(UserDialog.class).first();
    assertEquals(user, dialog.getUser());
    verify(service).get(1L);
  }

  @Test
  public void users_DoubleClick_Empty() {
    when(service.get(any(Long.class))).thenReturn(Optional.empty());
    test(view.users).doubleClickRow(0);

    UserDialog dialog = $(UserDialog.class).first();
    assertNull(dialog.getUser().getId());
    verify(service).get(1L);
  }

  @Test
  public void users_DoubleClickLaboratory() {
    when(laboratoryService.get(any(Long.class)))
        .thenAnswer(i -> laboratoryRepository.findById(i.getArgument(0)));
    User user = users.get(0);
    doubleClickItem(view.users, user, view.laboratory);

    LaboratoryDialog dialog = $(LaboratoryDialog.class).first();
    assertEquals(user.getLaboratory(), dialog.getLaboratory());
    verify(laboratoryService).get(user.getLaboratory().getId());
  }

  @Test
  public void users_DoubleClickLaboratory_Empty() {
    when(laboratoryService.get(any(Long.class))).thenReturn(Optional.empty());
    User user = users.get(0);
    doubleClickItem(view.users, user, view.laboratory);

    LaboratoryDialog dialog = $(LaboratoryDialog.class).first();
    assertNull(dialog.getLaboratory().getId());
    verify(laboratoryService).get(user.getLaboratory().getId());
  }

  @Test
  public void dialog_RefreshOnSave() {
    when(service.get(any(Long.class))).thenAnswer(i -> repository.findById(i.getArgument(0)));
    test(view.users).doubleClickRow(1);

    UserDialog dialog = $(UserDialog.class).first();
    test(dialog.save).click();
    verify(service, times(2)).all(any());
  }

  @Test
  public void laboratoryDialog_RefreshOnSave() {
    when(laboratoryService.get(any(Long.class)))
        .thenAnswer(i -> laboratoryRepository.findById(i.getArgument(0)));
    User user = users.get(1);
    doubleClickItem(view.users, user, view.laboratory);

    LaboratoryDialog dialog = $(LaboratoryDialog.class).first();
    test(dialog.save).click();
    verify(service, times(2)).all(any());
  }

  @Test
  public void emailFilter() {
    assertEquals("", view.emailFilter.getValue());
    assertEquals(ValueChangeMode.EAGER, view.emailFilter.getValueChangeMode());
  }

  @Test
  public void filterEmail() {
    view.users.setItems(mock(DataProvider.class));
    view.emailFilter.setValue("test");

    assertEquals("test", view.filter().emailContains);
    verify(view.users.getDataProvider()).refreshAll();
  }

  @Test
  public void filterEmail_Empty() {
    view.users.setItems(mock(DataProvider.class));
    view.emailFilter.setValue("test");
    view.emailFilter.setValue("");

    assertEquals(null, view.filter().emailContains);
    verify(view.users.getDataProvider(), times(2)).refreshAll();
  }

  @Test
  public void nameFilter() {
    assertEquals("", view.nameFilter.getValue());
    assertEquals(ValueChangeMode.EAGER, view.nameFilter.getValueChangeMode());
  }

  @Test
  public void filterName() {
    view.users.setItems(mock(DataProvider.class));
    view.nameFilter.setValue("test");

    assertEquals("test", view.filter().nameContains);
    verify(view.users.getDataProvider()).refreshAll();
  }

  @Test
  public void filterName_Empty() {
    view.users.setItems(mock(DataProvider.class));
    view.nameFilter.setValue("test");
    view.nameFilter.setValue("");

    assertEquals(null, view.filter().nameContains);
    verify(view.users.getDataProvider(), times(2)).refreshAll();
  }

  @Test
  public void laboratoryFilter() {
    assertEquals("", view.laboratoryFilter.getValue());
    assertEquals(ValueChangeMode.EAGER, view.laboratoryFilter.getValueChangeMode());
  }

  @Test
  public void filterLaboratory() {
    view.users.setItems(mock(DataProvider.class));
    view.laboratoryFilter.setValue("test");

    assertEquals("test", view.filter().laboratoryNameContains);
    verify(view.users.getDataProvider()).refreshAll();
  }

  @Test
  public void filterLaboratory_Empty() {
    view.users.setItems(mock(DataProvider.class));
    view.laboratoryFilter.setValue("test");
    view.laboratoryFilter.setValue("");

    assertEquals(null, view.filter().laboratoryNameContains);
    verify(view.users.getDataProvider(), times(2)).refreshAll();
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
    view.users.setItems(mock(DataProvider.class));
    view.activeFilter.setValue(Optional.of(false));

    assertFalse(view.filter().active);
    verify(view.users.getDataProvider()).refreshAll();
  }

  @Test
  public void filterActive_True() {
    view.users.setItems(mock(DataProvider.class));
    view.activeFilter.setValue(Optional.of(true));

    assertTrue(view.filter().active);
    verify(view.users.getDataProvider()).refreshAll();
  }

  @Test
  public void filterActive_Empty() {
    view.users.setItems(mock(DataProvider.class));
    view.activeFilter.setValue(Optional.of(true));
    view.activeFilter.setValue(Optional.empty());

    assertNull(view.filter().active);
    verify(view.users.getDataProvider(), times(2)).refreshAll();
  }

  @Test
  public void error() {
    assertFalse(view.error.isVisible());
  }

  @Test
  public void error_VisibileThanHidden() {
    test(view.switchUser).click();
    assertTrue(view.error.isVisible());
    test(view.users).doubleClickRow(0);
    assertFalse(view.error.isVisible());
  }

  @Test
  public void add() {
    test(view.add).click();

    UserDialog dialog = $(UserDialog.class).first();
    assertNull(dialog.getUser().getId());
  }

  @Test
  public void switchUser_NoSelection() {
    test(view.switchUser).click();

    verify(switchUserService, never()).switchUser(any(), any());
    assertTrue($(UsersView.class).exists());
    assertTrue(view.error.isVisible());
    assertEquals(resources.message(USERS_REQUIRED), view.error.getText());
  }

  @Test
  public void switchUser() {
    User user = repository.findById(10L).get();
    view.users.select(user);

    test(view.switchUser).click();
    verify(switchUserService).switchUser(user, VaadinServletRequest.getCurrent());
    assertTrue($(SubmissionsView.class).exists());
    assertFalse(view.error.isVisible());
  }

  @Test
  public void viewLaboratory_NoSelection() {
    test(view.viewLaboratory).click();

    verify(laboratoryService, never()).get(any());
    assertFalse($(LaboratoryDialog.class).exists());
    assertTrue(view.error.isVisible());
    assertEquals(resources.message(USERS_REQUIRED), view.error.getText());
  }

  @Test
  public void viewLaboratory() {
    when(laboratoryService.get(any(Long.class)))
        .thenAnswer(i -> laboratoryRepository.findById(i.getArgument(0)));
    User user = repository.findById(10L).get();
    view.users.select(user);

    test(view.viewLaboratory).click();

    verify(laboratoryService).get(user.getLaboratory().getId());
    LaboratoryDialog dialog = $(LaboratoryDialog.class).first();
    assertEquals(user.getLaboratory(), dialog.getLaboratory());
    assertFalse(view.error.isVisible());
  }

  @Test
  public void toggleActive_Inactive() {
    view.users.setItems(mock(DataProvider.class));
    User user = repository.findById(3L).get();
    view.toggleActive(user);

    verify(service).save(user, null);
    assertFalse(user.isActive());
    verify(view.users.getDataProvider()).refreshItem(user);
  }

  @Test
  public void toggleActive() {
    view.users.setItems(mock(DataProvider.class));
    User user = repository.findById(11L).get();
    view.toggleActive(user);

    verify(service).save(user, null);
    assertTrue(user.isActive());
    verify(view.users.getDataProvider()).refreshItem(user);
  }

  @Test
  public void afterNavigation() {
    AfterNavigationEvent event = mock(AfterNavigationEvent.class);
    Location location = new Location(VIEW_NAME);
    when(event.getLocation()).thenReturn(location);

    view.afterNavigation(event);

    assertFalse($(Notification.class).exists());
  }

  @Test
  public void afterNavigation_SwitchFailed() {
    AfterNavigationEvent event = mock(AfterNavigationEvent.class);
    Location location = new Location(VIEW_NAME + "?" + SWITCH_FAILED);
    when(event.getLocation()).thenReturn(location);

    view.afterNavigation(event);

    Notification notification = $(Notification.class).first();
    assertEquals(resources.message(SWITCH_FAILED), test(notification).getText());
  }
}
