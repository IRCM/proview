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

/**
 * Presenter of FilterInstantComponent.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FilterInstantComponentPresenter {
  public static final String BASE_STYLE = "filter-instant";
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
    view.addStyleName(BASE_STYLE);
    view.filterButton.addStyleName(BASE_STYLE + "-" + FILTER);
    view.fromDateField.addStyleName(BASE_STYLE + "-" + FROM);
    view.toDateField.addStyleName(BASE_STYLE + "-" + TO);
    view.setButton.addStyleName(BASE_STYLE + "-" + SET);
    view.clearButton.addStyleName(BASE_STYLE + "-" + CLEAR);
  }

  private void setCaption() {
    MessageResource resources = view.getResources();
    updateFilterButtonCaption();
    view.filterButton.setCaption(resources.message(ALL));
    view.fromDateField.setCaption(resources.message(FROM));
    view.toDateField.setCaption(resources.message(TO));
    view.setButton.setCaption(resources.message(SET));
    view.clearButton.setCaption(resources.message(CLEAR));
  }

  private void setListeners() {
    view.setButton.addClickListener(e -> setInterval());
    view.clearButton.addClickListener(e -> clearInterval());
    rangeProperty.addValueChangeListener(e -> rangeChanged());
  }

  private void rangeChanged() {
    Range<Instant> range = rangeProperty.getValue();
    if (!range.hasLowerBound()) {
      view.fromDateField.setValue(null);
    } else {
      view.fromDateField.setValue(java.util.Date.from(range.lowerEndpoint()));
    }
    if (!range.hasUpperBound()) {
      view.toDateField.setValue(null);
    } else {
      view.toDateField.setValue(java.util.Date.from(range.upperEndpoint()));
    }
    updateFilterButtonCaption();
  }

  private void updateFilterButtonCaption() {
    MessageResource resources = view.getResources();
    if (Range.all().equals(rangeProperty.getValue())) {
      view.filterButton.setCaption(resources.message("all"));
    } else {
      DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
      Range<Instant> range = rangeProperty.getValue();
      Instant from = range.hasLowerBound() ? range.lowerEndpoint() : null;
      Instant to = range.hasUpperBound() ? range.upperEndpoint() : null;
      String caption;
      if (!range.hasUpperBound()) {
        caption = resources.message(RANGE_ONLY_FROM, formatter.format(toLocalDateTime(from)));
      } else if (!range.hasLowerBound()) {
        caption = resources.message(RANGE_ONLY_TO, formatter.format(toLocalDateTime(to)));
      } else {
        caption = resources.message(RANGE, formatter.format(toLocalDateTime(from)),
            formatter.format(toLocalDateTime(to)));
      }
      view.filterButton.setCaption(caption);
    }
  }

  private void setInterval() {
    Instant from =
        view.fromDateField.getValue() != null ? view.fromDateField.getValue().toInstant() : null;
    Instant to =
        view.toDateField.getValue() != null ? view.toDateField.getValue().toInstant() : null;
    Range<Instant> range;
    if (from != null && to != null) {
      if (from.isAfter(to)) {
        range = Range.atMost(to);
      } else if (from.equals(to)) {
        range = Range.singleton(from);
      } else {
        range = Range.open(from, to);
      }
    } else if (from != null) {
      range = Range.atLeast(from);
    } else if (to != null) {
      range = Range.atMost(to);
    } else {
      range = Range.all();
    }
    rangeProperty.setValue(range);
    view.filterButton.setPopupVisible(false);
  }

  private void clearInterval() {
    rangeProperty.setValue(Range.all());
    view.filterButton.setPopupVisible(false);
  }

  public ObjectProperty<Range<Instant>> getRangeProperty() {
    return rangeProperty;
  }

  public Range<Instant> getRange() {
    return rangeProperty.getValue();
  }

  public void setRange(Range<Instant> range) {
    rangeProperty.setValue(range);
  }
}
