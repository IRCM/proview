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

package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;
import static ca.qc.ircm.proview.user.UserRole.USER;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.flow.router.BeforeEnterEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Tests for {@link MainView}.
 */
@NonTransactionalTestAnnotations
public class MainViewTest {
  private MainView view;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private BeforeEnterEvent event;

  @BeforeEach
  public void beforeTest() {
    view = new MainView();
  }

  @Test
  public void beforeEnter_User() {
    when(authorizationService.hasRole(USER)).thenReturn(true);

    view.beforeEnter(event);

    verify(event).forwardTo(SubmissionsView.class);
  }

  @Test
  public void beforeEnter_Admin() {
    when(authorizationService.hasRole(USER)).thenReturn(true);
    when(authorizationService.hasRole(ADMIN)).thenReturn(true);

    view.beforeEnter(event);

    verify(event).forwardTo(SubmissionsView.class);
  }

  @Test
  public void beforeEnter_Manager() {
    when(authorizationService.hasRole(USER)).thenReturn(true);
    when(authorizationService.hasRole(MANAGER)).thenReturn(true);

    view.beforeEnter(event);

    verify(event).forwardTo(SubmissionsView.class);
  }

  @Test
  public void beforeEnter_NoRole() {
    view.beforeEnter(event);
  }
}
