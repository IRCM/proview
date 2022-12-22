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

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.user.web.UserView.SAVED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.test.config.AbstractKaribuTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link UserViewPresenter}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UserViewPresenterTest extends AbstractKaribuTestCase {
  private UserViewPresenter presenter;
  @Mock
  private UserView view;
  @Mock
  private UserService service;
  @Mock
  private User user;
  @Captor
  private ArgumentCaptor<User> userCaptor;
  @Captor
  private ArgumentCaptor<Boolean> booleanCaptor;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(UserView.class, locale);

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    presenter = new UserViewPresenter(service);
    view.header = new H2();
    view.form = mock(UserForm.class);
    view.buttonsLayout = new HorizontalLayout();
    view.save = new Button();
    when(service.get(any(Long.class))).thenReturn(Optional.of(user));
  }

  @Test
  public void save_Invalid() {
    presenter.init(view);

    presenter.save(locale);

    verify(view.form).isValid();
    verify(service, never()).save(any(), any());
  }

  @Test
  public void save() {
    String password = "test_password";
    User user = mock(User.class);
    when(view.form.isValid()).thenReturn(true);
    when(view.form.getPassword()).thenReturn(password);
    when(view.form.getUser()).thenReturn(user);
    ui.navigate(UserView.class);
    presenter.init(view);

    presenter.save(locale);

    verify(view.form).isValid();
    verify(service).save(eq(user), eq(password));
    assertCurrentView(UsersView.class);
    verify(view).showNotification(resources.message(SAVED, user.getName()));
  }

  @Test
  public void setParameter() {
    presenter.init(view);

    presenter.setParameter(12L);

    verify(view.form).setUser(user);
    verify(service).get(12L);
  }

  @Test
  public void setParameter_Null() {
    presenter.init(view);

    presenter.setParameter(null);

    verify(view.form, never()).setUser(any());
    verify(service, never()).get(any(Long.class));
  }
}
