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

import static ca.qc.ircm.proview.web.filter.InstantFilterComponentPresenter.ALL;
import static ca.qc.ircm.proview.web.filter.InstantFilterComponentPresenter.BASE_STYLE;
import static ca.qc.ircm.proview.web.filter.InstantFilterComponentPresenter.CLEAR;
import static ca.qc.ircm.proview.web.filter.InstantFilterComponentPresenter.FILTER;
import static ca.qc.ircm.proview.web.filter.InstantFilterComponentPresenter.FROM;
import static ca.qc.ircm.proview.web.filter.InstantFilterComponentPresenter.RANGE;
import static ca.qc.ircm.proview.web.filter.InstantFilterComponentPresenter.RANGE_ONLY_FROM;
import static ca.qc.ircm.proview.web.filter.InstantFilterComponentPresenter.RANGE_ONLY_TO;
import static ca.qc.ircm.proview.web.filter.InstantFilterComponentPresenter.SET;
import static ca.qc.ircm.proview.web.filter.InstantFilterComponentPresenter.TO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Button;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.VerticalLayout;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.vaadin.hene.popupbutton.PopupButton;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class LocalDateFilterComponentPresenterTest {
  private LocalDateFilterComponentPresenter presenter;
  @Mock
  private LocalDateFilterComponent view;
  @Mock
  private SaveListener saveListener;
  @Mock
  private Registration registration;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(InstantFilterComponent.class, locale);
  private LocalDate fromDate = LocalDate.of(2016, 5, 11);
  private LocalDate toDate = LocalDate.of(2016, 9, 20);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new LocalDateFilterComponentPresenter();
    view.filter = new PopupButton();
    view.popup = new VerticalLayout();
    view.from = new InlineDateField();
    view.to = new InlineDateField();
    view.set = new Button();
    view.clear = new Button();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    presenter.init(view);
  }

  private String intervalCaption() {
    return intervalCaption(fromDate, toDate);
  }

  private String intervalCaption(LocalDate fromDate, LocalDate toDate) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    if (toDate == null) {
      return resources.message(RANGE_ONLY_FROM, formatter.format(fromDate));
    } else if (fromDate == null) {
      return resources.message(RANGE_ONLY_TO, formatter.format(toDate));
    } else {
      return resources.message(RANGE, formatter.format(fromDate), formatter.format(toDate));
    }
  }

  private void setFields() {
    view.from.setValue(fromDate);
    view.to.setValue(toDate);
  }

  @Test
  public void popup() {
    assertEquals(view.popup, view.filter.getContent());
  }

  @Test
  public void styles() {
    verify(view).addStyleName(BASE_STYLE);
    assertTrue(view.filter.getStyleName().contains(FILTER));
    assertTrue(view.from.getStyleName().contains(FROM));
    assertTrue(view.to.getStyleName().contains(TO));
    assertTrue(view.set.getStyleName().contains(SET));
    assertTrue(view.clear.getStyleName().contains(CLEAR));
  }

  @Test
  public void captions() {
    assertEquals(resources.message(ALL), view.filter.getCaption());
    assertEquals(resources.message(FROM), view.from.getCaption());
    assertEquals(resources.message(TO), view.to.getCaption());
    assertEquals(resources.message(SET), view.set.getCaption());
    assertEquals(resources.message(CLEAR), view.clear.getCaption());
  }

  @Test
  public void defaultValues() {
    assertEquals(Range.all(), presenter.getRange());
    assertNull(view.from.getValue());
    assertNull(view.to.getValue());
  }

  @Test
  public void set() {
    setFields();

    view.set.click();

    assertNotNull(presenter.getRange());
    assertTrue(presenter.getRange().hasLowerBound());
    assertEquals(fromDate, presenter.getRange().lowerEndpoint());
    assertTrue(presenter.getRange().hasUpperBound());
    assertEquals(toDate, presenter.getRange().upperEndpoint());
    assertEquals(intervalCaption(), view.filter.getCaption());
    verify(view).fireSaveEvent(Range.closed(fromDate, toDate));
  }

  @Test
  public void set_NullFrom() {
    view.to.setValue(toDate);

    view.set.click();

    assertNotNull(presenter.getRange());
    assertFalse(presenter.getRange().hasLowerBound());
    assertTrue(presenter.getRange().hasUpperBound());
    assertEquals(toDate, presenter.getRange().upperEndpoint());
    assertEquals(intervalCaption(null, toDate), view.filter.getCaption());
    verify(view).fireSaveEvent(Range.atMost(toDate));
  }

  @Test
  public void set_NullTo() {
    view.from.setValue(fromDate);

    view.set.click();

    assertNotNull(presenter.getRange());
    assertTrue(presenter.getRange().hasLowerBound());
    assertEquals(fromDate, presenter.getRange().lowerEndpoint());
    assertFalse(presenter.getRange().hasUpperBound());
    assertEquals(intervalCaption(fromDate, null), view.filter.getCaption());
    verify(view).fireSaveEvent(Range.atLeast(fromDate));
  }

  @Test
  public void set_NullFromTo() {
    view.set.click();

    assertNotNull(presenter.getRange());
    assertFalse(presenter.getRange().hasLowerBound());
    assertFalse(presenter.getRange().hasUpperBound());
    assertEquals(resources.message(ALL), view.filter.getCaption());
    verify(view).fireSaveEvent(Range.all());
  }

  @Test
  public void set_FromEqualsTo() {
    view.from.setValue(fromDate);
    view.to.setValue(fromDate);

    view.set.click();

    assertNotNull(presenter.getRange());
    assertTrue(presenter.getRange().hasLowerBound());
    assertEquals(fromDate, presenter.getRange().lowerEndpoint());
    assertTrue(presenter.getRange().hasUpperBound());
    assertEquals(fromDate, presenter.getRange().upperEndpoint());
    assertEquals(intervalCaption(fromDate, fromDate), view.filter.getCaption());
    verify(view).fireSaveEvent(Range.singleton(fromDate));
  }

  @Test
  public void set_ToBeforeFrom() {
    LocalDate toDate = LocalDate.now();
    view.from.setValue(toDate.plusDays(10));
    view.to.setValue(toDate);

    view.set.click();

    assertNotNull(presenter.getRange());
    assertFalse(presenter.getRange().hasLowerBound());
    assertTrue(presenter.getRange().hasUpperBound());
    assertEquals(toDate, presenter.getRange().upperEndpoint());
    assertEquals(intervalCaption(null, toDate), view.filter.getCaption());
    verify(view).fireSaveEvent(Range.atMost(toDate));
  }

  @Test
  public void clear() {
    setFields();

    view.clear.click();

    assertEquals(Range.all(), presenter.getRange());
    assertEquals(resources.message(ALL), view.filter.getCaption());
    assertNull(view.from.getValue());
    assertNull(view.to.getValue());
    verify(view).fireSaveEvent(Range.all());
  }

  @Test
  public void setThanClear() {
    setFields();

    view.set.click();

    assertNotNull(presenter.getRange());
    assertTrue(presenter.getRange().hasLowerBound());
    assertEquals(fromDate, presenter.getRange().lowerEndpoint());
    assertTrue(presenter.getRange().hasUpperBound());
    assertEquals(toDate, presenter.getRange().upperEndpoint());
    assertEquals(intervalCaption(), view.filter.getCaption());
    verify(view).fireSaveEvent(Range.closed(fromDate, toDate));

    view.clear.click();

    assertEquals(Range.all(), presenter.getRange());
    assertEquals(resources.message(ALL), view.filter.getCaption());
    assertNull(view.from.getValue());
    assertNull(view.to.getValue());
    verify(view).fireSaveEvent(Range.all());
  }

  @Test
  public void addSaveListener() {
    when(view.addListener(any(), any(), any(Method.class))).thenReturn(registration);

    Registration registration = presenter.addSaveListener(saveListener);

    verify(view).addListener(SaveEvent.class, saveListener, SaveListener.SAVED_METHOD);
    assertEquals(this.registration, registration);
  }

  @Test
  public void getRange() {
    setFields();

    view.set.click();

    assertNotNull(presenter.getRange());
    assertTrue(presenter.getRange().hasLowerBound());
    assertEquals(fromDate, presenter.getRange().lowerEndpoint());
    assertTrue(presenter.getRange().hasUpperBound());
    assertEquals(toDate, presenter.getRange().upperEndpoint());
  }

  @Test
  public void setRange() {
    presenter.setRange(Range.closed(fromDate, toDate));

    assertNotNull(presenter.getRange());
    assertTrue(presenter.getRange().hasLowerBound());
    assertEquals(fromDate, presenter.getRange().lowerEndpoint());
    assertTrue(presenter.getRange().hasUpperBound());
    assertEquals(toDate, presenter.getRange().upperEndpoint());
    assertEquals(fromDate, view.from.getValue());
    assertEquals(toDate, view.to.getValue());
    assertEquals(intervalCaption(), view.filter.getCaption());
    verify(view, never()).fireSaveEvent(any());
  }

  @Test
  public void setRange_All() {
    presenter.setRange(Range.all());

    assertNotNull(presenter.getRange());
    assertFalse(presenter.getRange().hasLowerBound());
    assertFalse(presenter.getRange().hasUpperBound());
    assertNull(view.from.getValue());
    assertNull(view.to.getValue());
    assertEquals(resources.message(ALL), view.filter.getCaption());
    verify(view, never()).fireSaveEvent(any());
  }
}
