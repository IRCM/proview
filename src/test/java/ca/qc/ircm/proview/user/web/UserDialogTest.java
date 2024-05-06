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

import static ca.qc.ircm.proview.Constants.CANCEL;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.user.web.UserDialog.HEADER;
import static ca.qc.ircm.proview.user.web.UserDialog.ID;
import static ca.qc.ircm.proview.user.web.UserDialog.id;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.SavedEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link UserDialog}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UserDialogTest extends SpringUIUnitTest {
  private UserDialog dialog;
  @MockBean
  private UserService service;
  @Mock
  private User user;
  @Mock
  private ComponentEventListener<SavedEvent<UserDialog>> savedListener;
  @Autowired
  private UserRepository repository;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(UserDialog.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    when(service.get(anyLong())).then(
        i -> i.getArgument(0) != null ? repository.findById(i.getArgument(0)) : Optional.empty());
    UI.getCurrent().setLocale(locale);
    UsersView view = navigate(UsersView.class);
    view.users.setItems(repository.findAll());
    test(view.users).doubleClickRow(0);
    dialog = $(UserDialog.class).first();
  }

  @Test
  public void styles() {
    assertEquals(ID, dialog.getId().orElse(""));
    assertEquals(id(SAVE), dialog.save.getId().orElse(""));
    assertTrue(dialog.save.hasThemeName(ButtonVariant.LUMO_PRIMARY.getVariantName()));
    assertEquals(id(CANCEL), dialog.cancel.getId().orElse(""));
  }

  @Test
  public void labels() {
    User user = repository.findById(dialog.getUserId()).get();
    assertEquals(resources.message(HEADER, 1, user.getName()), dialog.getHeaderTitle());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    validateIcon(VaadinIcon.CHECK.create(), dialog.save.getIcon());
    assertEquals(webResources.message(CANCEL), dialog.cancel.getText());
    validateIcon(VaadinIcon.CLOSE.create(), dialog.cancel.getIcon());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(UserDialog.class, locale);
    final AppResources webResources = new AppResources(Constants.class, locale);
    UI.getCurrent().setLocale(locale);
    User user = repository.findById(dialog.getUserId()).get();
    assertEquals(resources.message(HEADER, 1, user.getName()), dialog.getHeaderTitle());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    assertEquals(webResources.message(CANCEL), dialog.cancel.getText());
  }

  @Test
  public void savedListener() {
    dialog.addSavedListener(savedListener);
    dialog.fireSavedEvent();
    verify(savedListener).onComponentEvent(any());
  }

  @Test
  public void savedListener_Remove() {
    dialog.addSavedListener(savedListener).remove();
    dialog.fireSavedEvent();
    verify(savedListener, never()).onComponentEvent(any());
  }

  @Test
  public void getUserId() {
    when(user.getId()).thenReturn(2L);
    dialog.form = mock(UserForm.class);
    when(dialog.form.getUser()).thenReturn(user);
    assertEquals(user.getId(), dialog.getUserId());
    verify(dialog.form, atLeastOnce()).getUser();
  }

  @Test
  public void setUserId_User() {
    User user = repository.findById(2L).get();
    dialog.form = mock(UserForm.class);
    when(dialog.form.getUser()).thenReturn(user);

    dialog.setUserId(user.getId());

    verify(dialog.form).setUser(user);
    assertEquals(resources.message(HEADER, 1, user.getName()), dialog.getHeaderTitle());
  }

  @Test
  public void setUserId_UserBeforeLocaleChange() {
    User user = repository.findById(2L).get();
    dialog.form = mock(UserForm.class);
    when(dialog.form.getUser()).thenReturn(user);

    dialog.setUserId(user.getId());

    verify(dialog.form).setUser(user);
    assertEquals(resources.message(HEADER, 1, user.getName()), dialog.getHeaderTitle());
  }

  @Test
  public void setUserId_Null() {
    dialog.form = mock(UserForm.class);

    dialog.setUserId(null);

    verify(dialog.form).setUser(null);
    assertEquals(resources.message(HEADER, 0), dialog.getHeaderTitle());
  }

  @Test
  public void save_Invalid() {
    dialog.form = mock(UserForm.class);
    dialog.addSavedListener(savedListener);

    test(dialog.save).click();

    verify(dialog.form).isValid();
    verify(service, never()).save(any(), any());
    assertTrue(dialog.isOpened());
    verify(savedListener, never()).onComponentEvent(any());
  }

  @Test
  public void save() {
    String password = "test_password";
    dialog.form = mock(UserForm.class);
    dialog.addSavedListener(savedListener);
    when(dialog.form.isValid()).thenReturn(true);
    when(dialog.form.getPassword()).thenReturn(password);
    when(dialog.form.getUser()).thenReturn(user);

    test(dialog.save).click();

    verify(dialog.form).isValid();
    verify(service).save(eq(user), eq(password));
    assertFalse(dialog.isOpened());
    verify(savedListener).onComponentEvent(any());
  }

  @Test
  public void cancel_Close() {
    dialog.addSavedListener(savedListener);

    test(dialog.cancel).click();

    verify(service, never()).save(any(), any());
    assertFalse(dialog.isOpened());
    verify(savedListener, never()).onComponentEvent(any());
  }
}
