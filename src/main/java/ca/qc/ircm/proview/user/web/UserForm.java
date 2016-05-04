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

import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

/**
 * User form.
 */
public class UserForm extends UserFormDesign implements MessageResourcesComponent {
  private static final long serialVersionUID = -4585597583437283309L;
  private UserFormPresenter presenter;

  public void setPresenter(UserFormPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.attach();
  }

  public TextField getEmailField() {
    return emailField;
  }

  public TextField getNameField() {
    return nameField;
  }

  public PasswordField getPasswordField() {
    return passwordField;
  }

  public PasswordField getConfirmPasswordField() {
    return confirmPasswordField;
  }
}
