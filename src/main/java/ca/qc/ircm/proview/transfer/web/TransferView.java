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

package ca.qc.ircm.proview.transfer.web;

import ca.qc.ircm.proview.plate.web.PlateComponent;
import ca.qc.ircm.proview.web.component.SavedSamplesComponent;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Sample transfer view.
 */
@SpringView(name = TransferView.VIEW_NAME)
@RolesAllowed({ "ADMIN" })
public class TransferView extends TransferViewDesign implements BaseView, SavedSamplesComponent {
  public static final String VIEW_NAME = "transfer";
  private static final long serialVersionUID = -4719228370965227442L;
  @Inject
  private transient TransferViewPresenter presenter;
  @Inject
  protected PlateComponent sourcePlateForm;
  @Inject
  protected PlateComponent destinationPlateForm;

  /**
   * Initializes view.
   */
  @PostConstruct
  public void init() {
    sourcePlateForm.setWidth("100%");
    sourcePlateFormLayout.addComponent(sourcePlateForm);
    destinationPlateForm.setWidth("100%");
    destinationPlateFormLayout.addComponent(destinationPlateForm);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  @Override
  public void enter(ViewChangeEvent event) {
    presenter.enter(event.getParameters());
  }
}
