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
import static ca.qc.ircm.proview.sample.web.ControlViewPresenter.INVALID_SAMPLE;
import static ca.qc.ircm.proview.sample.web.ControlViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlRepository;
import ca.qc.ircm.proview.sample.ControlService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Locale;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ControlViewPresenterTest {
  @Inject
  private ControlViewPresenter presenter;
  @Inject
  private ControlRepository repository;
  @MockBean
  private ControlService controlService;
  @MockBean
  private AuthorizationService authorizationService;
  @Mock
  private ControlView view;
  @Mock
  private ControlForm form;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  @Captor
  private ArgumentCaptor<Control> controlCaptor;
  @Captor
  private ArgumentCaptor<SaveListener<Control>> listenerCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private ControlViewDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(ControlView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    design = new ControlViewDesign();
    view.design = design;
    view.form = form;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.enter("");

    assertTrue(design.headerLabel.getStyleName().contains(HEADER));
    assertTrue(design.headerLabel.getStyleName().contains(ValoTheme.LABEL_H1));
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.enter("");

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.headerLabel.getValue());
  }

  @Test
  public void enter_Empty_ReadOnly() {
    presenter.init(view);
    presenter.enter("");

    verify(form, never()).setValue(any());
    verify(form).setReadOnly(true);
  }

  @Test
  public void enter_Empty() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.enter("");

    verify(form, never()).setValue(any());
    verify(form).setReadOnly(false);
  }

  @Test
  public void enter_Control_ReadOnly() {
    final Control control = repository.findOne(444L);
    when(controlService.get(444L)).thenReturn(control);

    presenter.init(view);
    presenter.enter("444");

    verify(controlService, atLeastOnce()).get(444L);
    verify(form).setValue(control);
    verify(form).setReadOnly(true);
  }

  @Test
  public void enter_Control() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    final Control control = repository.findOne(444L);
    when(controlService.get(444L)).thenReturn(control);

    presenter.init(view);
    presenter.enter("444");

    verify(controlService, atLeastOnce()).get(444L);
    verify(form).setValue(control);
    verify(form).setReadOnly(false);
  }

  @Test
  public void enter_InvalidNumber() {
    presenter.init(view);

    presenter.enter("a");

    verify(view).showWarning(resources.message(INVALID_SAMPLE));
  }

  @Test
  public void enter_InvalidSample() {
    presenter.init(view);

    presenter.enter("3");

    verify(controlService, atLeastOnce()).get(3L);
    verify(view).showWarning(resources.message(INVALID_SAMPLE));
  }

  @Test
  public void saveEvent() {
    presenter.init(view);
    presenter.enter("");
    verify(form).addSaveListener(listenerCaptor.capture());
    SaveListener<Control> listener = listenerCaptor.getValue();
    Control control = new Control();
    control.setId(235L);

    listener.saved(new SaveEvent<>(form, control));

    verify(view).navigateTo(ControlView.VIEW_NAME + "/235");
  }
}
