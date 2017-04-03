package ca.qc.ircm.proview.web.filter;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.shared.Registration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Instant filter component presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LocalDateFilterComponentPresenter {
  public static final String BASE_STYLE = "instant-filter";
  public static final String FILTER = "filter";
  public static final String ALL = "all";
  public static final String RANGE = "range";
  public static final String RANGE_ONLY_FROM = "range.from";
  public static final String RANGE_ONLY_TO = "range.to";
  public static final String FROM = "from";
  public static final String TO = "to";
  public static final String SET = "set";
  public static final String CLEAR = "clear";
  public static final Instant MINIMAL_DATE =
      LocalDateTime.of(0, 1, 1, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant();
  private LocalDateFilterComponent view;
  private Range<LocalDate> range;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(LocalDateFilterComponent view) {
    this.view = view;
    range = Range.all();
    prepareComponents();
    setListeners();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.addStyleName(BASE_STYLE);
    view.filter.addStyleName(FILTER);
    view.filter.setCaption(resources.message(ALL));
    view.filter.setContent(view.popup);
    view.from.addStyleName(FROM);
    view.from.setCaption(resources.message(FROM));
    view.to.addStyleName(TO);
    view.to.setCaption(resources.message(TO));
    view.set.addStyleName(SET);
    view.set.setCaption(resources.message(SET));
    view.clear.addStyleName(CLEAR);
    view.clear.setCaption(resources.message(CLEAR));
    updateFilterButtonCaption();
  }

  private void setListeners() {
    view.set.addClickListener(e -> setInterval());
    view.clear.addClickListener(e -> clearInterval());
  }

  private void rangeChanged() {
    if (!range.hasLowerBound()) {
      view.from.setValue(null);
    } else {
      view.from.setValue(range.lowerEndpoint());
    }
    if (!range.hasUpperBound()) {
      view.to.setValue(null);
    } else {
      view.to.setValue(range.upperEndpoint());
    }
    updateFilterButtonCaption();
  }

  private void updateFilterButtonCaption() {
    MessageResource resources = view.getResources();
    if (Range.all().equals(range)) {
      view.filter.setCaption(resources.message("all"));
    } else {
      DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
      LocalDate from = range.hasLowerBound() ? range.lowerEndpoint() : null;
      LocalDate to = range.hasUpperBound() ? range.upperEndpoint() : null;
      String caption;
      if (!range.hasUpperBound()) {
        caption = resources.message(RANGE_ONLY_FROM, formatter.format(from));
      } else if (!range.hasLowerBound()) {
        caption = resources.message(RANGE_ONLY_TO, formatter.format(to));
      } else {
        caption = resources.message(RANGE, formatter.format(from), formatter.format(to));
      }
      view.filter.setCaption(caption);
    }
  }

  private void setInterval() {
    LocalDate from = view.from.getValue();
    LocalDate to = view.to.getValue();
    if (from != null && to != null) {
      if (from.isAfter(to)) {
        range = Range.atMost(to);
      } else if (from.equals(to)) {
        range = Range.singleton(from);
      } else {
        range = Range.closed(from, to);
      }
    } else if (from != null) {
      range = Range.atLeast(from);
    } else if (to != null) {
      range = Range.atMost(to);
    } else {
      range = Range.all();
    }
    view.filter.setPopupVisible(false);
    rangeChanged();
    view.fireSaveEvent(range);
  }

  private void clearInterval() {
    range = Range.all();
    view.filter.setPopupVisible(false);
    rangeChanged();
    view.fireSaveEvent(range);
  }

  public Registration addSaveListener(SaveListener<Range<LocalDate>> listener) {
    return view.addListener(SaveEvent.class, listener, SaveListener.SAVED_METHOD);
  }

  public Range<LocalDate> getRange() {
    return range;
  }

  public void setRange(Range<LocalDate> range) {
    this.range = range;
    rangeChanged();
  }
}
