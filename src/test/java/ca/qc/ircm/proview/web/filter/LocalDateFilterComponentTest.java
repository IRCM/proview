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

package ca.qc.ircm.proview.web.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class LocalDateFilterComponentTest {
  private LocalDateFilterComponent view;
  @Mock
  private LocalDateFilterComponentPresenter presenter;
  @Mock
  private SaveListener<Range<LocalDate>> saveListener;
  @Captor
  private ArgumentCaptor<SaveEvent<Range<LocalDate>>> saveEventCaptor;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    view = new LocalDateFilterComponent(presenter);
  }

  @Test
  public void addStyleName() {
    view.addStyleName("test");

    assertTrue(view.getStyleName().contains("test"));
    assertTrue(view.filter.getStyleName().contains("test"));
  }

  @Test
  public void setStyleName() {
    view.setStyleName("test");

    assertTrue(view.getStyleName().contains("test"));
    assertTrue(view.filter.getStyleName().contains("test"));
  }

  @Test
  public void removeStyleName() {
    view.addStyleName("test");
    view.removeStyleName("test");

    assertFalse(view.getStyleName().contains("test"));
    assertFalse(view.filter.getStyleName().contains("test"));
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
    Range<LocalDate> range = Range.open(LocalDate.now().minusDays(1), LocalDate.now());

    view.fireSaveEvent(range);

    verify(saveListener).saved(saveEventCaptor.capture());
    assertEquals(range, saveEventCaptor.getValue().getSavedObject());
  }

  @Test
  public void getValue() {
    Range<LocalDate> range = Range.atLeast(LocalDate.now());
    when(presenter.getValue()).thenReturn(range);

    Range<LocalDate> viewRange = view.getValue();

    verify(presenter).getValue();
    assertSame(range, viewRange);
  }

  @Test
  public void setValue() {
    Range<LocalDate> range = Range.atLeast(LocalDate.now());

    view.setValue(range);

    verify(presenter).setValue(range);
  }
}
