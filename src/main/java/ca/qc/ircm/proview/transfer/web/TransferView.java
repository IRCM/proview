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
import ca.qc.ircm.proview.web.component.SavedContainersComponent;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Sample transfer view.
 */
@SpringView(name = TransferView.VIEW_NAME)
@RolesAllowed({ "ADMIN" })
public class TransferView extends CustomComponent implements BaseView, SavedContainersComponent {
  public static final String VIEW_NAME = "transfer";
  private static final long serialVersionUID = -4719228370965227442L;
  protected TransferViewDesign design = new TransferViewDesign();
  @Inject
  protected PlateComponent destinationPlateForm;
  @Inject
  private transient TransferViewPresenter presenter;

  /**
   * Initializes view.
   */
  @PostConstruct
  public void init() {
    setCompositionRoot(design);
    destinationPlateForm.setWidth("100%");
    design.destinationPlateFormLayout.addComponent(destinationPlateForm);
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
