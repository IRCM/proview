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

import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.shared.Registration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;

import javax.inject.Inject;

/**
 * Instant filter component.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LocalDateFilterComponent extends LocalDateFilterComponentDesign
    implements BaseComponent {
  private static final long serialVersionUID = -5938290034747610261L;
  @Inject
  private transient LocalDateFilterComponentPresenter presenter;

  protected LocalDateFilterComponent() {
  }

  protected LocalDateFilterComponent(LocalDateFilterComponentPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  @Override
  public void addStyleName(String style) {
    super.addStyleName(style);
    filter.addStyleName(style);
  }

  @Override
  public void setStyleName(String style) {
    super.setStyleName(style);
    filter.setStyleName(style);
  }

  @Override
  public void removeStyleName(String style) {
    super.removeStyleName(style);
    filter.removeStyleName(style);
  }

  public Registration addSaveListener(SaveListener<Range<LocalDate>> listener) {
    return addListener(SaveEvent.class, listener, SaveListener.SAVED_METHOD);
  }

  protected void fireSaveEvent(Range<LocalDate> range) {
    fireEvent(new SaveEvent<>(this, range));
  }

  public Range<LocalDate> getValue() {
    return presenter.getValue();
  }

  public void setValue(Range<LocalDate> range) {
    presenter.setValue(range);
  }
}
