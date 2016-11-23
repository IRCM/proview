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

import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import ca.qc.ircm.proview.web.Menu;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Registers user view.
 */
@SpringView(name = RegisterView.VIEW_NAME)
public class RegisterView extends RegisterViewDesign implements MessageResourcesView {
  public static final String VIEW_NAME = "user/register";
  private static final long serialVersionUID = 7586918222688019429L;
  @Inject
  private RegisterViewPresenter presenter;
  @Inject
  protected UserFormPresenter userFormPresenter;
  protected Menu menu = new Menu();
  protected UserForm userForm = new UserForm();

  /**
   * Initializes view.
   */
  @PostConstruct
  public void init() {
    menuLayout.addComponent(menu);
    userFormLayout.addComponent(userForm);
    userForm.setPresenter(userFormPresenter);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }
}
