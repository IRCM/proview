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
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.clickButton;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.user.web.UserDialog.HEADER;
import static ca.qc.ircm.proview.user.web.UserDialog.ID;
import static ca.qc.ircm.proview.user.web.UserDialog.id;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.web.SavedEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link UserDialog}.
 */
@ServiceTestAnnotations
public class UserDialogTest extends AbstractViewTestCase {
  private UserDialog dialog;
  private UserForm form;
  @Mock
  private UserDialogPresenter presenter;
  @Mock
  private User user;
  @Mock
  private UserFormPresenter formPresenter;
  @Mock
  private ComponentEventListener<SavedEvent<UserDialog>> savedListener;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private DefaultAddressConfiguration defaultAddressConfiguration;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(UserDialog.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    form = new UserForm(formPresenter, defaultAddressConfiguration);
    dialog = new UserDialog(form, presenter);
    dialog.init();
  }

  @Test
  public void presenter_Init() {
    verify(presenter).init(dialog);
  }

  @Test
  public void styles() {
    assertEquals(ID, dialog.getId().orElse(""));
    assertEquals(id(HEADER), dialog.header.getId().orElse(""));
    assertEquals(id(SAVE), dialog.save.getId().orElse(""));
    assertTrue(dialog.save.hasThemeName(ButtonVariant.LUMO_PRIMARY.getVariantName()));
    assertEquals(id(CANCEL), dialog.cancel.getId().orElse(""));
  }

  @Test
  public void labels() {
    dialog.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER, 0), dialog.header.getText());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    validateIcon(VaadinIcon.CHECK.create(), dialog.save.getIcon());
    assertEquals(webResources.message(CANCEL), dialog.cancel.getText());
    validateIcon(VaadinIcon.CLOSE.create(), dialog.cancel.getIcon());
  }

  @Test
  public void localeChange() {
    dialog.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(UserDialog.class, locale);
    final AppResources webResources = new AppResources(Constants.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    dialog.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER, 0), dialog.header.getText());
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
  public void getUser() {
    when(formPresenter.getUser()).thenReturn(user);
    assertEquals(user, dialog.getUser());
    verify(formPresenter).getUser();
  }

  @Test
  public void setUser_NewUser() {
    User user = new User();
    when(formPresenter.getUser()).thenReturn(user);

    dialog.localeChange(mock(LocaleChangeEvent.class));
    dialog.setUser(user);

    verify(formPresenter).setUser(user);
    assertEquals(resources.message(HEADER, 0), dialog.header.getText());
  }

  @Test
  public void setUser_User() {
    User user = userRepository.findById(2L).get();
    when(formPresenter.getUser()).thenReturn(user);

    dialog.localeChange(mock(LocaleChangeEvent.class));
    dialog.setUser(user);

    verify(formPresenter).setUser(user);
    assertEquals(resources.message(HEADER, 1, user.getName()), dialog.header.getText());
  }

  @Test
  public void setUser_UserBeforeLocaleChange() {
    User user = userRepository.findById(2L).get();
    when(formPresenter.getUser()).thenReturn(user);

    dialog.setUser(user);
    dialog.localeChange(mock(LocaleChangeEvent.class));

    verify(formPresenter).setUser(user);
    assertEquals(resources.message(HEADER, 1, user.getName()), dialog.header.getText());
  }

  @Test
  public void setUser_Null() {
    dialog.localeChange(mock(LocaleChangeEvent.class));
    dialog.setUser(null);

    verify(formPresenter).setUser(null);
    assertEquals(resources.message(HEADER, 0), dialog.header.getText());
  }

  @Test
  public void save() {
    clickButton(dialog.save);

    verify(presenter).save();
  }

  @Test
  public void cancel() {
    clickButton(dialog.cancel);

    verify(presenter).cancel();
  }
}
