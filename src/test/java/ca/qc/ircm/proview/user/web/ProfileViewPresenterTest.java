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
import static ca.qc.ircm.proview.user.web.ProfileView.SAVED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.util.Locale;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for {@link ProfileViewPresenter}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ProfileViewPresenterTest extends AbstractViewTestCase {
  private ProfileViewPresenter presenter;
  @Mock
  private ProfileView view;
  @Mock
  private UserService service;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private User user;
  @Captor
  private ArgumentCaptor<User> userCaptor;
  @Captor
  private ArgumentCaptor<Boolean> booleanCaptor;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(ProfileView.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new ProfileViewPresenter(service, authorizationService);
    view.header = new H2();
    view.form = mock(UserForm.class);
    view.buttonsLayout = new HorizontalLayout();
    view.save = new Button();
    when(service.get(any(Long.class))).thenReturn(Optional.of(user));
    when(authorizationService.getCurrentUser()).thenReturn(Optional.of(user));
  }

  @Test
  public void init() {
    presenter.init(view);
    verify(view.form).setUser(user);
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
    presenter.init(view);

    presenter.save(locale);

    verify(view.form).isValid();
    verify(service).save(eq(user), eq(password));
    verify(ui).navigate(MainView.class);
    verify(view).showNotification(resources.message(SAVED));
  }
}
