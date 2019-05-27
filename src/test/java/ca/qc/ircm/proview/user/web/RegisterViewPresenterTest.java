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

import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.HEADER;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.TITLE;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class RegisterViewPresenterTest {
  private RegisterViewPresenter presenter;
  @Mock
  private RegisterView view;
  @Mock
  private AuthorizationService authorizationService;
  @Captor
  private ArgumentCaptor<SaveListener<User>> saveListenerCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private RegisterViewDesign design;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(RegisterView.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new RegisterViewPresenter(authorizationService, applicationName);
    design = new RegisterViewDesign();
    view.design = design;
    view.userForm = mock(UserForm.class);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.headerLabel.getStyleName().contains(HEADER));
    assertTrue(design.headerLabel.getStyleName().contains(ValoTheme.LABEL_H1));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.headerLabel.getValue());
  }

  @Test
  public void captions_Admin() {
    when(authorizationService.hasRole(UserRole.ADMIN)).thenReturn(true);
    presenter.init(view);

    verify(view).setTitle(resources.message(property(TITLE, "admin"), applicationName));
    assertEquals(resources.message(property(HEADER, "admin")), design.headerLabel.getValue());
  }

  @Test
  public void save() {
    presenter.init(view);

    verify(view.userForm).addSaveListener(saveListenerCaptor.capture());
    saveListenerCaptor.getValue()
        .saved(new SaveEvent<>(view.userForm, new User(1L, "test@ircm.qc.ca")));
    verify(view).navigateTo(MainView.VIEW_NAME);
  }
}
