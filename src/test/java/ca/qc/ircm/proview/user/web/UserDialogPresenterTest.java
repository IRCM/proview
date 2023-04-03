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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.AbstractKaribuTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import com.vaadin.flow.component.button.Button;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link UserDialogPresenter}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UserDialogPresenterTest extends AbstractKaribuTestCase {
  private UserDialogPresenter presenter;
  @Mock
  private UserDialog dialog;
  @Mock
  private UserService userService;
  @Captor
  private ArgumentCaptor<User> userCaptor;
  @Captor
  private ArgumentCaptor<Boolean> booleanCaptor;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    presenter = new UserDialogPresenter(userService);
    dialog.form = mock(UserForm.class);
    dialog.save = new Button();
    dialog.cancel = new Button();
  }

  @Test
  public void save_Invalid() {
    presenter.init(dialog);

    presenter.save();

    verify(dialog.form).isValid();
    verify(userService, never()).save(any(), any());
    verify(dialog, never()).close();
    verify(dialog, never()).fireSavedEvent();
  }

  @Test
  public void save() {
    String password = "test_password";
    User user = mock(User.class);
    when(dialog.form.isValid()).thenReturn(true);
    when(dialog.form.getPassword()).thenReturn(password);
    when(dialog.form.getUser()).thenReturn(user);
    presenter.init(dialog);

    presenter.save();

    verify(dialog.form).isValid();
    verify(userService).save(eq(user), eq(password));
    verify(dialog).close();
    verify(dialog).fireSavedEvent();
  }

  @Test
  public void cancel_Close() {
    presenter.init(dialog);

    presenter.cancel();

    verify(dialog).close();
    verify(dialog, never()).fireSavedEvent();
  }
}
