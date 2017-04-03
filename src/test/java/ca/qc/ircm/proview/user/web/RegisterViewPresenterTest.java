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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.Label;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class RegisterViewPresenterTest {
  private RegisterViewPresenter presenter;
  @Mock
  private RegisterView view;
  @Captor
  private ArgumentCaptor<SaveListener<User>> saveListenerCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(RegisterView.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new RegisterViewPresenter(applicationName);
    view.headerLabel = new Label();
    view.userForm = mock(UserForm.class);
    view.userFormPresenter = mock(UserFormPresenter.class);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    presenter.init(view);
  }

  @Test
  public void styles() {
    assertTrue(view.headerLabel.getStyleName().contains(HEADER));
    assertTrue(view.headerLabel.getStyleName().contains("h1"));
  }

  @Test
  public void captions() {
    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), view.headerLabel.getValue());
  }

  @Test
  public void editable() {
    verify(view.userFormPresenter).setEditable(true);
  }

  @Test
  public void defaultPhoneNumber() {
    verify(view.userFormPresenter).addPhoneNumber();
  }

  @Test
  public void save() {
    verify(view.userFormPresenter).addSaveListener(saveListenerCaptor.capture());
    saveListenerCaptor.getValue()
        .saved(new SaveEvent<>(view.userForm, new User(1L, "test@ircm.qc.ca")));
    verify(view).navigateTo(MainView.VIEW_NAME);
  }
}
