package ca.qc.ircm.proview.utils.web;

import static ca.qc.ircm.proview.utils.web.FilterInstantComponentPresenter.ALL;
import static ca.qc.ircm.proview.utils.web.FilterInstantComponentPresenter.BASE_STYLE;
import static ca.qc.ircm.proview.utils.web.FilterInstantComponentPresenter.CLEAR;
import static ca.qc.ircm.proview.utils.web.FilterInstantComponentPresenter.FILTER;
import static ca.qc.ircm.proview.utils.web.FilterInstantComponentPresenter.FROM;
import static ca.qc.ircm.proview.utils.web.FilterInstantComponentPresenter.INTERVAL;
import static ca.qc.ircm.proview.utils.web.FilterInstantComponentPresenter.NULL;
import static ca.qc.ircm.proview.utils.web.FilterInstantComponentPresenter.SET;
import static ca.qc.ircm.proview.utils.web.FilterInstantComponentPresenter.TO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Property.ValueChangeListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class FilterInstantComponentPresenterTest {
  private FilterInstantComponentPresenter presenter;
  private FilterInstantComponent view = new FilterInstantComponent();
  @Mock
  private ValueChangeListener rangeListener;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources;
  private Instant fromDate = toInstant(LocalDateTime.of(2016, 5, 11, 0, 0, 0));
  private Instant toDate = toInstant(LocalDateTime.of(2016, 9, 20, 0, 0, 0));

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new FilterInstantComponentPresenter();
    view.setLocale(locale);
    resources = view.getResources();
    presenter.init(view);
    presenter.attach();
    presenter.getRangeProperty().addValueChangeListener(rangeListener);
  }

  private Instant toInstant(LocalDateTime localDateTime) {
    return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
  }

  private LocalDateTime toLocalDateTime(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
  }

  private String intervalCaption() {
    return intervalCaption(fromDate, toDate);
  }

  private String intervalCaption(Instant startDate, Instant endDate) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    return resources.message(INTERVAL,
        startDate != null ? formatter.format(toLocalDateTime(startDate)) : resources.message(NULL),
        formatter.format(toLocalDateTime(endDate)));
  }

  private void setFields() {
    view.startDateField.setValue(Date.from(fromDate));
    view.endDateField.setValue(Date.from(toDate));
  }

  @Test
  public void setPresenterInView() {
    FilterInstantComponent view = mock(FilterInstantComponent.class);
    presenter.init(view);

    verify(view).setPresenter(presenter);
  }

  @Test
  public void styles() {
    assertTrue(view.filterButton.getStyleName().contains(BASE_STYLE + FILTER));
    assertTrue(view.startDateField.getStyleName().contains(BASE_STYLE + FROM));
    assertTrue(view.endDateField.getStyleName().contains(BASE_STYLE + TO));
    assertTrue(view.setButton.getStyleName().contains(BASE_STYLE + SET));
    assertTrue(view.clearButton.getStyleName().contains(BASE_STYLE + CLEAR));
  }

  @Test
  public void captions() {
    assertEquals(resources.message(ALL), view.filterButton.getCaption());
    assertEquals(resources.message(FROM), view.startDateField.getCaption());
    assertEquals(resources.message(TO), view.endDateField.getCaption());
    assertEquals(resources.message(SET), view.setButton.getCaption());
    assertEquals(resources.message(CLEAR), view.clearButton.getCaption());
  }

  @Test
  public void defaultValues() {
    assertEquals(Range.all(), presenter.getRange());
  }

  @Test
  public void set() {
    setFields();

    view.setButton.click();

    assertNotNull(presenter.getRange());
    assertEquals(fromDate, presenter.getRange().lowerEndpoint());
    assertEquals(toDate, presenter.getRange().upperEndpoint());
    assertEquals(intervalCaption(), view.filterButton.getCaption());
    verify(rangeListener).valueChange(any());
  }

  @Test
  public void set_EndBeforeStart() {
    LocalDateTime endDate = LocalDateTime.now();
    view.startDateField.setValue(Date.from(toInstant(endDate.plusDays(10))));
    view.endDateField.setValue(Date.from(toInstant(endDate)));

    view.setButton.click();

    assertNotNull(presenter.getRange());
    assertFalse(presenter.getRange().hasLowerBound());
    assertEquals(toInstant(endDate), presenter.getRange().upperEndpoint());
    assertEquals(intervalCaption(null, toInstant(endDate)), view.filterButton.getCaption());
    verify(rangeListener).valueChange(any());
  }

  @Test
  public void clear() {
    setFields();

    view.clearButton.click();

    assertEquals(Range.all(), presenter.getRange());
    assertEquals(resources.message(ALL), view.filterButton.getCaption());
  }

  @Test
  public void setThanClear() {
    setFields();

    view.setButton.click();

    assertNotNull(presenter.getRange());
    assertEquals(fromDate, presenter.getRange().lowerEndpoint());
    assertEquals(toDate, presenter.getRange().upperEndpoint());
    assertEquals(intervalCaption(), view.filterButton.getCaption());

    view.clearButton.click();

    assertEquals(Range.all(), presenter.getRange());
    assertEquals(resources.message(ALL), view.filterButton.getCaption());
    verify(rangeListener, times(2)).valueChange(any());
  }
}
