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

import static ca.qc.ircm.proview.user.web.UserViewPresenter.HEADER;
import static ca.qc.ircm.proview.user.web.UserViewPresenter.INVALID_USER;
import static ca.qc.ircm.proview.user.web.UserViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class UserViewPresenterTest {
  private UserViewPresenter presenter;
  @Mock
  private UserView view;
  @Mock
  private UserService userService;
  @Mock
  private AuthorizationService authorizationService;
  @Captor
  private ArgumentCaptor<User> userCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private UserViewDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(UserView.class, locale);
  private User user;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new UserViewPresenter(userService, authorizationService, applicationName);
    design = new UserViewDesign();
    view.design = design;
    view.userForm = mock(UserForm.class);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    presenter.init(view);
    user = new User(1L, "test@ircm.qc.ca");
  }

  @Test
  public void styles() {
    assertTrue(design.header.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.header.getStyleName().contains(HEADER));
  }

  @Test
  public void captions() {
    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
  }

  @Test
  public void enter_ReadOnly() {
    when(authorizationService.getCurrentUser()).thenReturn(user);

    presenter.enter("");

    verify(view.userForm).setValue(userCaptor.capture());
    User user = userCaptor.getValue();
    assertEquals(this.user, user);
    verify(view.userForm).setReadOnly(true);
    verify(authorizationService).hasPermission(user, BasePermission.WRITE);
  }

  @Test
  public void enter() {
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);

    presenter.enter("");

    verify(view.userForm).setValue(userCaptor.capture());
    User user = userCaptor.getValue();
    assertEquals(this.user, user);
    verify(view.userForm).setReadOnly(false);
    verify(authorizationService).hasPermission(user, BasePermission.WRITE);
  }

  @Test
  public void enter_User_ReadOnly() {
    when(userService.get(any(Long.class))).thenReturn(user);

    presenter.enter("1");

    verify(view.userForm).setValue(userCaptor.capture());
    User user = userCaptor.getValue();
    assertEquals(this.user, user);
    verify(view.userForm).setReadOnly(true);
    verify(authorizationService).hasPermission(user, BasePermission.WRITE);
  }

  @Test
  public void enter_User() {
    when(userService.get(any(Long.class))).thenReturn(user);
    when(authorizationService.hasPermission(any(), any())).thenReturn(true);

    presenter.enter("1");

    verify(view.userForm).setValue(userCaptor.capture());
    User user = userCaptor.getValue();
    assertEquals(this.user, user);
    verify(view.userForm).setReadOnly(false);
    verify(authorizationService).hasPermission(user, BasePermission.WRITE);
  }

  @Test
  public void enter_InvalidId() {
    presenter.enter("a");

    verify(view.userForm, never()).setValue(any());
    verify(view.userForm).setReadOnly(true);
    verify(view).showWarning(resources.message(INVALID_USER));
  }

  @Test
  public void enter_InvalidUser() {
    when(userService.get(any(Long.class))).thenReturn(null);

    presenter.enter("1");

    verify(view.userForm, never()).setValue(any());
    verify(view.userForm).setReadOnly(true);
    verify(view).showWarning(resources.message(INVALID_USER));
  }
}
