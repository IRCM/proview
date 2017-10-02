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

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.shared.Registration;
import com.vaadin.ui.CustomComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Sample selection form.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SampleSelectionForm extends CustomComponent implements BaseComponent {
  private static final long serialVersionUID = -2890553778973734044L;
  protected SampleSelectionFormDesign design = new SampleSelectionFormDesign();
  @Inject
  private transient SampleSelectionFormPresenter presenter;

  protected SampleSelectionForm() {
  }

  protected SampleSelectionForm(SampleSelectionFormPresenter presenter) {
    this.presenter = presenter;
  }

  @PostConstruct
  public void init() {
    setSizeFull();
    setCompositionRoot(design);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public Registration addSaveListener(SaveListener<List<Sample>> listener) {
    return addListener(SaveEvent.class, listener, SaveListener.SAVED_METHOD);
  }

  protected void fireSaveEvent(List<Sample> selectedSamples) {
    fireEvent(new SaveEvent<>(this, selectedSamples));
  }

  public List<Sample> getItems() {
    return presenter.getItems();
  }

  public void setItems(List<Sample> samples) {
    presenter.setItems(samples);
  }
}
