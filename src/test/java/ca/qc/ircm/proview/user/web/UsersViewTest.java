package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.ALL;
import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.EDIT;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.SwitchUserService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.LaboratoryService;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilter;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.ErrorNotification;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.provider.ListDataProvider;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link UsersView}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UsersViewTest extends SpringUIUnitTest {
  private static final String MESSAGES_PREFIX = messagePrefix(UsersView.class);
  private static final String USER_PREFIX = messagePrefix(User.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private UsersView view;
  @MockitoBean
  private UserService service;
  @MockitoBean
  private LaboratoryService laboratoryService;
  @MockitoBean
  private SwitchUserService switchUserService;
  @Autowired
  private UserRepository repository;
  @Autowired
  private LaboratoryRepository laboratoryRepository;
  @Autowired
  private AuthenticatedUser authenticatedUser;
  @Mock
  private ListDataProvider<User> dataProvider;
  @Captor
  private ArgumentCaptor<UserFilter> userFilterCaptor;
  private Locale locale = ENGLISH;
  private List<User> users;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    when(service.get(anyLong())).then(i -> repository.findById(i.getArgument(0)));
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
    assertEquals(USERS, view.users.getId().orElse(""));
    assertEquals(ADD, view.add.getId().orElse(""));
    validateIcon(VaadinIcon.PLUS.create(), view.add.getIcon());
    assertEquals(EDIT, view.edit.getId().orElse(""));
    validateIcon(VaadinIcon.EDIT.create(), view.edit.getIcon());
    assertEquals(SWITCH_USER, view.switchUser.getId().orElse(""));
    validateIcon(VaadinIcon.BUG.create(), view.switchUser.getIcon());
    assertEquals(VIEW_LABORATORY, view.viewLaboratory.getId().orElse(""));
    validateIcon(VaadinIcon.EDIT.create(), view.viewLaboratory.getIcon());
  }

  @Test
  public void labels() {
    HeaderRow headerRow = view.users.getHeaderRows().get(0);
    FooterRow footerRow = view.users.getFooterRows().get(0);
    assertEquals(view.getTranslation(USER_PREFIX + EMAIL), headerRow.getCell(view.email).getText());
    assertEquals(view.getTranslation(USER_PREFIX + EMAIL), footerRow.getCell(view.email).getText());
    assertEquals(view.getTranslation(USER_PREFIX + NAME), headerRow.getCell(view.name).getText());
    assertEquals(view.getTranslation(USER_PREFIX + NAME), footerRow.getCell(view.name).getText());
    assertEquals(view.getTranslation(USER_PREFIX + LABORATORY),
        headerRow.getCell(view.laboratory).getText());
    assertEquals(view.getTranslation(USER_PREFIX + LABORATORY),
        footerRow.getCell(view.laboratory).getText());
    assertEquals(view.getTranslation(USER_PREFIX + ACTIVE),
        headerRow.getCell(view.active).getText());
    assertEquals(view.getTranslation(USER_PREFIX + ACTIVE),
        footerRow.getCell(view.active).getText());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + ALL), view.emailFilter.getPlaceholder());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + ALL), view.nameFilter.getPlaceholder());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + ALL),
        view.laboratoryFilter.getPlaceholder());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + ALL),
        view.activeFilter.getItemLabelGenerator().apply(Optional.empty()));
    assertEquals(view.getTranslation(USER_PREFIX + property(ACTIVE, false)),
        view.activeFilter.getItemLabelGenerator().apply(Optional.of(false)));
    assertEquals(view.getTranslation(USER_PREFIX + property(ACTIVE, true)),
        view.activeFilter.getItemLabelGenerator().apply(Optional.of(true)));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ADD), view.add.getText());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + EDIT), view.edit.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SWITCH_USER), view.switchUser.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + VIEW_LABORATORY),
        view.viewLaboratory.getText());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    HeaderRow headerRow = view.users.getHeaderRows().get(0);
    FooterRow footerRow = view.users.getFooterRows().get(0);
    assertEquals(view.getTranslation(USER_PREFIX + EMAIL), headerRow.getCell(view.email).getText());
    assertEquals(view.getTranslation(USER_PREFIX + EMAIL), footerRow.getCell(view.email).getText());
    assertEquals(view.getTranslation(USER_PREFIX + NAME), headerRow.getCell(view.name).getText());
    assertEquals(view.getTranslation(USER_PREFIX + NAME), footerRow.getCell(view.name).getText());
    assertEquals(view.getTranslation(USER_PREFIX + LABORATORY),
        headerRow.getCell(view.laboratory).getText());
    assertEquals(view.getTranslation(USER_PREFIX + LABORATORY),
        footerRow.getCell(view.laboratory).getText());
    assertEquals(view.getTranslation(USER_PREFIX + ACTIVE),
        headerRow.getCell(view.active).getText());
    assertEquals(view.getTranslation(USER_PREFIX + ACTIVE),
        footerRow.getCell(view.active).getText());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + ALL), view.emailFilter.getPlaceholder());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + ALL), view.nameFilter.getPlaceholder());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + ALL),
        view.laboratoryFilter.getPlaceholder());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + ALL),
        view.activeFilter.getItemLabelGenerator().apply(Optional.empty()));
    assertEquals(view.getTranslation(USER_PREFIX + property(ACTIVE, false)),
        view.activeFilter.getItemLabelGenerator().apply(Optional.of(false)));
    assertEquals(view.getTranslation(USER_PREFIX + property(ACTIVE, true)),
        view.activeFilter.getItemLabelGenerator().apply(Optional.of(true)));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ADD), view.add.getText());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + EDIT), view.edit.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SWITCH_USER), view.switchUser.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + VIEW_LABORATORY),
        view.viewLaboratory.getText());
  }

  @Test
  public void getPageTitle() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + TITLE,
        view.getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME)), view.getPageTitle());
  }

  @Test
  public void users_SelectionMode() {
    assertTrue(view.users.getSelectionModel() instanceof SelectionModel.Single);
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void users_Manager() {
    verify(service).all(userFilterCaptor.capture(),
        eq(authenticatedUser.getUser().orElseThrow().getLaboratory()));
    assertNull(userFilterCaptor.getValue().emailContains);
    assertNull(userFilterCaptor.getValue().nameContains);
    assertNull(userFilterCaptor.getValue().active);
    assertNull(userFilterCaptor.getValue().laboratoryNameContains);
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
    verify(service).all(userFilterCaptor.capture());
    assertNull(userFilterCaptor.getValue().emailContains);
    assertNull(userFilterCaptor.getValue().nameContains);
    assertNull(userFilterCaptor.getValue().active);
    assertNull(userFilterCaptor.getValue().laboratoryNameContains);
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
    assertEquals(4, view.users.getColumns().size());
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
      assertEquals(user.getEmail(), test(view.users).getCellText(i, indexOfColumn(EMAIL)));
      assertEquals(user.getName(), test(view.users).getCellText(i, indexOfColumn(NAME)));
      assertEquals(user.getLaboratory().getName(),
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
      assertEquals(view.getTranslation(USER_PREFIX + property(ACTIVE, user.isActive())),
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
    assertEquals(user.getId(), dialog.getUserId());
    verify(service).get(1L);
  }

  @Test
  public void users_DoubleClickLaboratory() {
    when(laboratoryService.get(any(Long.class)))
        .thenAnswer(i -> laboratoryRepository.findById(i.getArgument(0)));
    User user = users.get(0);
    doubleClickItem(view.users, user, view.laboratory);

    LaboratoryDialog dialog = $(LaboratoryDialog.class).first();
    assertEquals(user.getLaboratory().getId(), dialog.getLaboratoryId());
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
    view.users.setItems(dataProvider);
    view.emailFilter.setValue("test");

    assertEquals("test", view.filter().emailContains);
    verify(view.users.getDataProvider()).refreshAll();
  }

  @Test
  public void filterEmail_value() {
    assertEquals(13, view.users.getListDataView().getItems().count());

    view.emailFilter.setValue("an");

    assertEquals(3, view.users.getListDataView().getItems().count());
  }

  @Test
  public void filterEmail_Empty() {
    view.users.setItems(dataProvider);
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
    view.users.setItems(dataProvider);
    view.nameFilter.setValue("test");

    assertEquals("test", view.filter().nameContains);
    verify(view.users.getDataProvider()).refreshAll();
  }

  @Test
  public void filterName_Empty() {
    view.users.setItems(dataProvider);
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
    view.users.setItems(dataProvider);
    view.laboratoryFilter.setValue("test");

    assertEquals("test", view.filter().laboratoryNameContains);
    verify(view.users.getDataProvider()).refreshAll();
  }

  @Test
  public void filterLaboratory_Empty() {
    view.users.setItems(dataProvider);
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
    assertTrue(values.contains(Optional.<Boolean>empty()));
    assertTrue(values.contains(Optional.of(false)));
    assertTrue(values.contains(Optional.of(true)));
  }

  @Test
  public void filterActive_False() {
    view.users.setItems(dataProvider);
    view.activeFilter.setValue(Optional.of(false));

    assertFalse(view.filter().active);
    verify(view.users.getDataProvider()).refreshAll();
  }

  @Test
  public void filterActive_True() {
    view.users.setItems(dataProvider);
    view.activeFilter.setValue(Optional.of(true));

    assertTrue(view.filter().active);
    verify(view.users.getDataProvider()).refreshAll();
  }

  @Test
  public void filterActive_Empty() {
    view.users.setItems(dataProvider);
    view.activeFilter.setValue(Optional.of(true));
    view.activeFilter.setValue(Optional.empty());

    assertNull(view.filter().active);
    verify(view.users.getDataProvider(), times(2)).refreshAll();
  }

  @Test
  public void add() {
    test(view.add).click();

    UserDialog dialog = $(UserDialog.class).first();
    assertEquals(0, dialog.getUserId());
  }

  @Test
  public void edit_Enabled() {
    assertFalse(view.edit.isEnabled());
    view.users.select(repository.findById(10L).orElseThrow());
    assertTrue(view.edit.isEnabled());
    view.users.deselectAll();
    assertFalse(view.edit.isEnabled());
  }

  @Test
  public void edit_NoSelection() {
    view.edit();

    verify(service, never()).get(anyLong());
    assertTrue($(UsersView.class).exists());
    Notification error = $(Notification.class).first();
    assertTrue(error instanceof ErrorNotification);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + USERS_REQUIRED),
        ((ErrorNotification) error).getText());
  }

  @Test
  public void edit() {
    User user = repository.findById(10L).orElseThrow();
    view.users.select(user);

    test(view.edit).click();
    UserDialog dialog = $(UserDialog.class).last();
    assertEquals(user.getId(), dialog.getUserId());
    verify(service).get(user.getId());
  }

  @Test
  public void switchUser_Enabled() {
    assertFalse(view.switchUser.isEnabled());
    view.users.select(repository.findById(10L).orElseThrow());
    assertTrue(view.switchUser.isEnabled());
    view.users.deselectAll();
    assertFalse(view.switchUser.isEnabled());
  }

  @Test
  public void switchUser_NoSelection() {
    view.switchUser();

    verify(switchUserService, never()).switchUser(any(), any());
    assertTrue($(UsersView.class).exists());
    Notification error = $(Notification.class).first();
    assertTrue(error instanceof ErrorNotification);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + USERS_REQUIRED),
        ((ErrorNotification) error).getText());
  }

  @Test
  public void switchUser() {
    User user = repository.findById(10L).orElseThrow();
    view.users.select(user);

    test(view.switchUser).click();
    verify(switchUserService).switchUser(user, VaadinServletRequest.getCurrent());
    assertTrue(UI.getCurrent().getInternals().dumpPendingJavaScriptInvocations().stream()
        .anyMatch(i -> i.getInvocation().getExpression().contains("window.open($0, $1)")
            && !i.getInvocation().getParameters().isEmpty()
            && i.getInvocation().getParameters().get(0).equals("/")));
    assertFalse($(Notification.class).exists());
  }

  @Test
  public void viewLaboratory_Enabled() {
    assertFalse(view.viewLaboratory.isEnabled());
    view.users.select(repository.findById(10L).orElseThrow());
    assertTrue(view.viewLaboratory.isEnabled());
    view.users.deselectAll();
    assertFalse(view.viewLaboratory.isEnabled());
  }

  @Test
  public void viewLaboratory_NoSelection() {
    view.viewLaboratory();

    verify(laboratoryService, never()).get(anyLong());
    assertFalse($(LaboratoryDialog.class).exists());
    Notification error = $(Notification.class).first();
    assertTrue(error instanceof ErrorNotification);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + USERS_REQUIRED),
        ((ErrorNotification) error).getText());
  }

  @Test
  public void viewLaboratory() {
    when(laboratoryService.get(any(Long.class)))
        .thenAnswer(i -> laboratoryRepository.findById(i.getArgument(0)));
    User user = repository.findById(10L).orElseThrow();
    view.users.select(user);

    test(view.viewLaboratory).click();

    verify(laboratoryService).get(user.getLaboratory().getId());
    LaboratoryDialog dialog = $(LaboratoryDialog.class).first();
    assertEquals(user.getLaboratory().getId(), dialog.getLaboratoryId());
    assertFalse($(Notification.class).exists());
  }

  @Test
  public void toggleActive_Inactive() {
    view.users.setItems(dataProvider);
    User user = repository.findById(3L).orElseThrow();
    view.toggleActive(user);

    verify(service).save(user, null);
    assertFalse(user.isActive());
    verify(view.users.getDataProvider()).refreshItem(user);
  }

  @Test
  public void toggleActive() {
    view.users.setItems(dataProvider);
    User user = repository.findById(11L).orElseThrow();
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
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SWITCH_FAILED),
        test(notification).getText());
  }
}
