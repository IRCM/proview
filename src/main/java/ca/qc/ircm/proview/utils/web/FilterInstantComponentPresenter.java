package ca.qc.ircm.proview.utils.web;

import com.google.common.collect.Range;

import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.util.ObjectProperty;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Presenter of FilterInstantComponent.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FilterInstantComponentPresenter {
  public static final String BASE_STYLE = "filter-instant-";
  public static final String FILTER = "filter";
  public static final String ALL = "all";
  public static final String INTERVAL = "interval";
  public static final String NULL = "null";
  public static final String FROM = "from";
  public static final String TO = "to";
  public static final String SET = "set";
  public static final String CLEAR = "clear";
  public static final Instant MINIMAL_DATE =
      LocalDateTime.of(0, 1, 1, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant();
  private final ObjectProperty<Range<Instant>> rangeProperty = new ObjectProperty<>(Range.all());
  private FilterInstantComponent view;

  public void init(FilterInstantComponent view) {
    this.view = view;
    view.setPresenter(this);
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setStyles();
    setCaption();
    setListeners();
  }

  private LocalDateTime toLocalDateTime(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
  }

  private void setStyles() {
    view.filterButton.addStyleName(BASE_STYLE + FILTER);
    view.startDateField.addStyleName(BASE_STYLE + FROM);
    view.endDateField.addStyleName(BASE_STYLE + TO);
    view.setButton.addStyleName(BASE_STYLE + SET);
    view.clearButton.addStyleName(BASE_STYLE + CLEAR);
  }

  private void setCaption() {
    MessageResource resources = view.getResources();
    updateFilterButtonCaption();
    view.filterButton.setCaption(resources.message(ALL));
    view.startDateField.setCaption(resources.message(FROM));
    view.endDateField.setCaption(resources.message(TO));
    view.setButton.setCaption(resources.message(SET));
    view.clearButton.setCaption(resources.message(CLEAR));
  }

  private void setListeners() {
    view.setButton.addClickListener(e -> setInterval());
    view.clearButton.addClickListener(e -> clearInterval());
  }

  private void updateFilterButtonCaption() {
    MessageResource resources = view.getResources();
    if (Range.all().equals(rangeProperty.getValue())) {
      view.filterButton.setCaption(resources.message("all"));
    } else {
      Range<Instant> range = rangeProperty.getValue();
      Instant from = range.hasLowerBound() ? range.lowerEndpoint() : null;
      Instant to = range.hasUpperBound() ? range.upperEndpoint() : null;
      DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
      String caption = resources.message(INTERVAL,
          from != null ? formatter.format(toLocalDateTime(from)) : resources.message(NULL),
          formatter.format(toLocalDateTime(to)));
      view.filterButton.setCaption(caption);
    }
  }

  private void setInterval() {
    Instant from = view.startDateField.getValue().toInstant();
    Instant to = view.endDateField.getValue().toInstant();
    Range<Instant> range;
    if (from.isAfter(to)) {
      range = Range.atMost(to);
    } else {
      range = Range.open(from, to);
    }
    rangeProperty.setValue(range);
    view.filterButton.setPopupVisible(false);
    updateFilterButtonCaption();
  }

  private void clearInterval() {
    rangeProperty.setValue(Range.all());
    view.startDateField.setValue(new Date());
    view.endDateField.setValue(new Date());
    view.filterButton.setPopupVisible(false);
    updateFilterButtonCaption();
  }

  public Range<Instant> getRange() {
    return rangeProperty.getValue();
  }

  public ObjectProperty<Range<Instant>> getRangeProperty() {
    return rangeProperty;
  }
}
