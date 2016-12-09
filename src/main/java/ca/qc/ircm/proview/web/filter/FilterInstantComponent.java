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

import com.google.common.collect.Range;

import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.data.util.ObjectProperty;

import java.time.Instant;

/**
 * Filters instant based on a day resolution.
 */
public class FilterInstantComponent extends FilterInstantComponentDesign implements BaseComponent {
  private static final long serialVersionUID = -4819761558400463539L;
  private FilterInstantComponentPresenter presenter;

  public FilterInstantComponent() {
    removeComponent(popupLayout);
    filterButton.setContent(popupLayout);
  }

  public void setPresenter(FilterInstantComponentPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.attach();
  }

  @Override
  public void addStyleName(String style) {
    super.addStyleName(style);
    filterButton.addStyleName(style);
  }

  @Override
  public void setStyleName(String style) {
    super.setStyleName(style);
    filterButton.setStyleName(style);
  }

  public ObjectProperty<Range<Instant>> getRangeProperty() {
    return presenter.getRangeProperty();
  }

  public Range<Instant> getRange() {
    return presenter.getRange();
  }

  public void setRange(Range<Instant> range) {
    presenter.setRange(range);
  }
}
