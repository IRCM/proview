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

import ca.qc.ircm.proview.plate.web.PlateComponent;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
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

@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContainerSelectionForm extends CustomComponent implements BaseComponent {
  private static final long serialVersionUID = 7128874097968930586L;
  protected ContainerSelectionFormDesign design = new ContainerSelectionFormDesign();
  @Inject
  protected PlateComponent plateComponent;
  @Inject
  private ContainerSelectionFormPresenter presenter;

  protected ContainerSelectionForm() {
  }

  protected ContainerSelectionForm(ContainerSelectionFormPresenter presenter,
      PlateComponent plateComponent) {
    this.presenter = presenter;
    this.plateComponent = plateComponent;
  }

  @PostConstruct
  public void init() {
    setCompositionRoot(design);
    design.plateLayout.addComponent(plateComponent);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public Registration addSaveListener(SaveListener<List<SampleContainer>> listener) {
    return addListener(SaveEvent.class, listener, SaveListener.SAVED_METHOD);
  }

  protected void fireSaveEvent(List<SampleContainer> containers) {
    fireEvent(new SaveEvent<>(this, containers));
  }

  public List<Sample> getSamples() {
    return presenter.getSamples();
  }

  public void setSamples(List<Sample> samples) {
    presenter.setSamples(samples);
  }
}
