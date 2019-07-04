/*
 * Copyright (c) 2018 Institut de recherches cliniques de Montreal (IRCM)
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

import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.clickButton;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.doubleClickItem;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.user.UserProperties.ACTIVE;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.LABORATORY;
import static ca.qc.ircm.proview.user.UserProperties.NAME;
import static ca.qc.ircm.proview.user.web.UsersView.ADD;
import static ca.qc.ircm.proview.user.web.UsersView.HEADER;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_FAILED;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_USER;
import static ca.qc.ircm.proview.user.web.UsersView.USERS;
import static ca.qc.ircm.proview.user.web.UsersView.VIEW_NAME;
import static ca.qc.ircm.proview.web.WebConstants.ALL;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.ERROR;
import static ca.qc.ircm.proview.web.WebConstants.ERROR_TEXT;
import static ca.qc.ircm.proview.web.WebConstants.SUCCESS;
import static ca.qc.ircm.proview.web.WebConstants.THEME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class UsersViewTest extends AbstractViewTestCase {
  private UsersView view;
  @Mock
  private UsersViewPresenter presenter;
  @Mock
  private UserDialog userDialog;
  @Captor
  private ArgumentCaptor<ValueProvider<User, String>> valueProviderCaptor;
  @Captor
  private ArgumentCaptor<ComponentRenderer<Button, User>> buttonRendererCaptor;
  @Captor
  private ArgumentCaptor<Comparator<User>> comparatorCaptor;
  @Autowired
  private UserRepository userRepository;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(UsersView.class, locale);
  private MessageResource userResources = new MessageResource(User.class, locale);
  private MessageResource webResources = new MessageResource(WebConstants.class, locale);
  private List<User> users;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    view = new UsersView(presenter, userDialog);
    view.init();
    users = userRepository.findAll();
  }

  @SuppressWarnings("unchecked")
  private void mockColumns() {
    Element usersElement = view.users.getElement();
    view.users = mock(Grid.class);
    when(view.users.getElement()).thenReturn(usersElement);
    view.email = mock(Column.class);
    when(view.users.addColumn(any(ValueProvider.class), eq(EMAIL))).thenReturn(view.email);
    when(view.email.setKey(any())).thenReturn(view.email);
    when(view.email.setComparator(any(Comparator.class))).thenReturn(view.email);
    when(view.email.setHeader(any(String.class))).thenReturn(view.email);
    view.name = mock(Column.class);
    when(view.users.addColumn(any(ValueProvider.class), eq(NAME))).thenReturn(view.name);
    when(view.name.setKey(any())).thenReturn(view.name);
    when(view.name.setHeader(any(String.class))).thenReturn(view.name);
    view.laboratory = mock(Column.class);
    when(view.users.addColumn(any(ValueProvider.class), eq(LABORATORY)))
        .thenReturn(view.laboratory);
    when(view.laboratory.setKey(any())).thenReturn(view.laboratory);
    when(view.laboratory.setComparator(any(Comparator.class))).thenReturn(view.laboratory);
    when(view.laboratory.setHeader(any(String.class))).thenReturn(view.laboratory);
    view.active = mock(Column.class);
    when(view.users.addColumn(any(ComponentRenderer.class), eq(ACTIVE))).thenReturn(view.active);
    when(view.active.setKey(any())).thenReturn(view.active);
    when(view.active.setComparator(any(Comparator.class))).thenReturn(view.active);
    when(view.active.setHeader(any(String.class))).thenReturn(view.active);
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
    assertTrue(view.getId().orElse("").equals(VIEW_NAME));
    assertTrue(view.header.getId().orElse("").contains(HEADER));
    assertTrue(view.users.getClassNames().contains(USERS));
    assertTrue(view.error.getId().orElse("").contains(ERROR_TEXT));
    assertTrue(view.add.getId().orElse("").contains(ADD));
    assertTrue(view.switchUser.getId().orElse("").contains(SWITCH_USER));
  }

  @Test
  public void labels() {
    mockColumns();
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
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
    verify(presenter).localeChange(locale);
  }

  @Test
  public void localeChange() {
    view = new UsersView(presenter, userDialog);
    mockColumns();
    view.init();
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = Locale.FRENCH;
    final MessageResource resources = new MessageResource(UsersView.class, locale);
    final MessageResource userResources = new MessageResource(User.class, locale);
    final MessageResource webResources = new MessageResource(WebConstants.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
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
    doAnswer(i -> {
      User user = i.getArgument(0);
      user.setActive(!user.isActive());
      return null;
    }).when(presenter).toggleActive(any());
    view = new UsersView(presenter, userDialog);
    mockColumns();
    view.init();
    verify(view.users).addColumn(valueProviderCaptor.capture(), eq(EMAIL));
    ValueProvider<User, String> valueProvider = valueProviderCaptor.getValue();
    for (User user : users) {
      assertEquals(user.getEmail() != null ? user.getEmail() : "", valueProvider.apply(user));
    }
    verify(view.email).setComparator(comparatorCaptor.capture());
    Comparator<User> comparator = comparatorCaptor.getValue();
    assertTrue(comparator.compare(email("abc@site.com"), email("test@site.com")) < 0);
    assertTrue(comparator.compare(email("Abc@site.com"), email("test@site.com")) < 0);
    assertTrue(comparator.compare(email("test@abc.com"), email("test@site.com")) < 0);
    assertTrue(comparator.compare(email("test@site.com"), email("test@site.com")) == 0);
    assertTrue(comparator.compare(email("Test@site.com"), email("test@site.com")) == 0);
    assertTrue(comparator.compare(email("test@site.com"), email("abc@site.com")) > 0);
    assertTrue(comparator.compare(email("Test@site.com"), email("abc@site.com")) > 0);
    assertTrue(comparator.compare(email("test@site.com"), email("test@abc.com")) > 0);
    verify(view.users).addColumn(valueProviderCaptor.capture(), eq(NAME));
    valueProvider = valueProviderCaptor.getValue();
    for (User user : users) {
      assertEquals(user.getName() != null ? user.getName() : "", valueProvider.apply(user));
    }
    verify(view.users).addColumn(valueProviderCaptor.capture(), eq(LABORATORY));
    valueProvider = valueProviderCaptor.getValue();
    for (User user : users) {
      assertEquals(user.getLaboratory().getName() != null ? user.getLaboratory().getName() : "",
          valueProvider.apply(user));
    }
    verify(view.laboratory).setComparator(comparatorCaptor.capture());
    comparator = comparatorCaptor.getValue();
    assertTrue(comparator.compare(lab("abc"), lab("test")) < 0);
    assertTrue(comparator.compare(lab("Abc"), lab("test")) < 0);
    assertTrue(comparator.compare(lab("élement"), lab("facteur")) < 0);
    assertTrue(comparator.compare(lab("test"), lab("test")) == 0);
    assertTrue(comparator.compare(lab("Test"), lab("test")) == 0);
    assertTrue(comparator.compare(lab("Expérienceà"), lab("experiencea")) == 0);
    assertTrue(comparator.compare(lab("experiencea"), lab("Expérienceà")) == 0);
    assertTrue(comparator.compare(lab("test"), lab("abc")) > 0);
    assertTrue(comparator.compare(lab("Test"), lab("abc")) > 0);
    assertTrue(comparator.compare(lab("facteur"), lab("élement")) > 0);
    verify(view.users).addColumn(buttonRendererCaptor.capture(), eq(ACTIVE));
    ComponentRenderer<Button, User> buttonRenderer = buttonRendererCaptor.getValue();
    for (User user : users) {
      Button button = buttonRenderer.createComponent(user);
      assertTrue(button.getClassNames().contains(ACTIVE));
      assertTrue(button.getElement().getAttribute(THEME).equals(user.isActive() ? SUCCESS : ERROR));
      assertEquals(userResources.message(property(ACTIVE, user.isActive())), button.getText());
      validateIcon(user.isActive() ? VaadinIcon.EYE.create() : VaadinIcon.EYE_SLASH.create(),
          button.getIcon());
      boolean previousActive = user.isActive();
      clickButton(button);
      verify(presenter, atLeastOnce()).toggleActive(user);
      assertEquals(!previousActive, user.isActive());
      assertTrue(button.getElement().getAttribute(THEME).equals(user.isActive() ? SUCCESS : ERROR));
      assertEquals(userResources.message(property(ACTIVE, user.isActive())), button.getText());
      validateIcon(user.isActive() ? VaadinIcon.EYE.create() : VaadinIcon.EYE_SLASH.create(),
          button.getIcon());
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
    doubleClickItem(view.users, user);

    verify(presenter).view(user);
  }

  private User email(String email) {
    User user = new User();
    user.setEmail(email);
    return user;
  }

  private User lab(String name) {
    User user = new User();
    Laboratory laboratory = new Laboratory();
    laboratory.setName(name);
    user.setLaboratory(laboratory);
    return user;
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
  public void afterNavigation() {
    AfterNavigationEvent event = mock(AfterNavigationEvent.class);
    Location location = new Location(VIEW_NAME + "?" + SWITCH_FAILED);
    when(event.getLocation()).thenReturn(location);

    view.afterNavigation(event);

    verify(presenter).showError(location.getQueryParameters().getParameters(), locale);
  }
}
