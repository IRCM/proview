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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.web.ControlViewPresenter.HEADER;
import static ca.qc.ircm.proview.sample.web.ControlViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ControlViewPresenterTest {
  private ControlViewPresenter presenter;
  @Mock
  private ControlView view;
  @Mock
  private ControlForm form;
  @Mock
  private ControlFormPresenter formPresenter;
  @Mock
  private ControlService controlService;
  @Mock
  private AuthorizationService authorizationService;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  @Captor
  private ArgumentCaptor<Control> controlCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(ControlView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new ControlViewPresenter(controlService, authorizationService, applicationName);
    view.headerLabel = new Label();
    view.form = form;
    when(form.getPresenter()).thenReturn(formPresenter);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.enter("");

    assertTrue(view.headerLabel.getStyleName().contains(HEADER));
    assertTrue(view.headerLabel.getStyleName().contains(ValoTheme.LABEL_H1));
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.enter("");

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), view.headerLabel.getValue());
  }

  @Test
  public void enter_Empty() {
    presenter.init(view);
    presenter.enter("");

    verify(formPresenter, never()).setBean(any());
    verify(formPresenter, never()).setEditable(true);
  }

  @Test
  public void enter_Empty_Editable() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.enter("");

    verify(formPresenter, never()).setBean(any());
    verify(formPresenter).setEditable(true);
  }

  @Test
  public void enter_Control() {
    final Control control = entityManager.find(Control.class, 444L);
    when(controlService.get(444L)).thenReturn(control);

    presenter.init(view);
    presenter.enter("444");

    verify(formPresenter).setBean(control);
    verify(formPresenter, never()).setEditable(true);
  }

  @Test
  public void enter_Control_Editable() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    final Control control = entityManager.find(Control.class, 444L);
    when(controlService.get(444L)).thenReturn(control);

    presenter.init(view);
    presenter.enter("444");

    verify(formPresenter).setBean(control);
    verify(formPresenter).setEditable(true);
  }
}
