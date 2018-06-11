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

package ca.qc.ircm.proview.plate.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.test.config.AbstractComponentTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class PlateSelectionWindowTest extends AbstractComponentTestCase {
  private PlateSelectionWindow window;
  @Mock
  private PlateSelectionWindowPresenter presenter;
  @Mock
  private PlatesSelectionComponent platesSelection;
  @Mock
  private SaveListener<Plate> listener;
  @Mock
  private Plate plate;
  @Captor
  private ArgumentCaptor<SaveEvent<Plate>> eventCaptor;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    window = new PlateSelectionWindow(presenter, platesSelection);
    window.init();
  }

  @Test
  public void attach() {
    window.setParent(ui);

    verify(presenter).init(window);
  }

  @Test
  public void addSaveListener() {
    window.addSaveListener(listener);

    Collection<?> listeners = window.getListeners(SaveEvent.class);
    assertEquals(1, listeners.size());
    assertTrue(listeners.contains(listener));
  }

  @Test
  public void addSaveListener_Remove() {
    window.addSaveListener(listener).remove();

    Collection<?> listeners = window.getListeners(SaveEvent.class);
    assertEquals(0, listeners.size());
  }

  @Test
  public void fireSaveEvent() {
    window.addSaveListener(listener);
    window.fireSaveEvent(plate);

    verify(listener).saved(eventCaptor.capture());
    SaveEvent<Plate> event = eventCaptor.getValue();
    assertEquals(window, event.getSource());
    assertEquals(plate, event.getSavedObject());
  }

  @Test
  public void fireSaveEvent_Null() {
    window.addSaveListener(listener);
    window.fireSaveEvent(null);

    verify(listener).saved(eventCaptor.capture());
    SaveEvent<Plate> event = eventCaptor.getValue();
    assertEquals(window, event.getSource());
    assertNull(event.getSavedObject());
  }

  @Test
  public void setValue() {
    window.setValue(plate);
    window.setParent(ui);

    verify(presenter).setValue(plate);
  }

  @Test
  public void setValue_AfterAttach() {
    window.setParent(ui);
    window.setValue(plate);

    verify(presenter).setValue(plate);
  }
}
