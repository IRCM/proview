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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import com.vaadin.shared.Registration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ControlFormTest {
  private ControlForm view;
  @Mock
  private ControlFormPresenter presenter;
  @Mock
  private SaveListener<Control> saveListener;
  @Mock
  private Control control;
  @Captor
  private ArgumentCaptor<SaveEvent<Control>> saveEventCaptor;

  @Before
  public void beforeTest() {
    view = new ControlForm(presenter);
  }

  @Test
  public void addSaveListener() {
    Registration registration = view.addSaveListener(saveListener);

    List<?> listeners = new ArrayList<>(view.getListeners(SaveEvent.class));
    assertEquals(1, listeners.size());
    assertEquals(saveListener, listeners.get(0));
    assertNotNull(registration);
  }

  @Test
  public void fireSaveEvent() {
    view.addSaveListener(saveListener);

    view.fireSaveEvent(control);

    verify(saveListener).saved(saveEventCaptor.capture());
    assertEquals(control, saveEventCaptor.getValue().getSavedObject());
  }

  @Test
  public void getValue() {
    when(presenter.getValue()).thenReturn(control);

    assertEquals(control, view.getValue());

    verify(presenter).getValue();
  }

  @Test
  public void setValue() {
    view.setValue(control);

    verify(presenter).setValue(control);
  }

  @Test
  public void isReadOnly_True() {
    when(presenter.isReadOnly()).thenReturn(true);

    assertTrue(view.isReadOnly());

    verify(presenter).isReadOnly();
  }

  @Test
  public void isReadOnly_False() {
    when(presenter.isReadOnly()).thenReturn(false);

    assertFalse(view.isReadOnly());

    verify(presenter).isReadOnly();
  }

  @Test
  public void setReadOnly_True() {
    view.setReadOnly(true);

    verify(presenter).setReadOnly(true);
  }

  @Test
  public void setReadOnly_False() {
    view.setReadOnly(false);

    verify(presenter).setReadOnly(false);
  }
}
