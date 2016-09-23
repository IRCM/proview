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

package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.laboratory.web.LaboratoryForm;
import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

/**
 * View user form.
 */
public class ViewUserForm extends ViewUserFormDesign implements MessageResourcesComponent {
  private static final long serialVersionUID = -4585597583437283309L;
  private ViewUserFormPresenter presenter;
  protected UserForm userForm = new UserForm();
  protected LaboratoryForm laboratoryForm = new LaboratoryForm();
  protected AddressForm addressForm = new AddressForm();

  /**
   * Creates view user form.
   */
  public ViewUserForm() {
    userFormLayout.addComponent(userForm);
    laboratoryFormLayout.addComponent(laboratoryForm);
    addressFormLayout.addComponent(addressForm);
  }

  public void setPresenter(ViewUserFormPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.attach();
    addressForm.setMargin(false);
  }

  public void showError(String message) {
    Notification.show(message, Notification.Type.ERROR_MESSAGE);
  }

  public void showWindow(Window window) {
    getUI().addWindow(window);
  }

  public void afterSuccessfulUpdate(String message) {
    Notification.show(message, Notification.Type.TRAY_NOTIFICATION);
  }
}
