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
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * User view.
 */
@SpringView(name = UserView.VIEW_NAME)
@RolesAllowed("USER")
public class UserView extends UserViewDesign implements MessageResourcesView {
  public static final String VIEW_NAME = "user/user";
  private static final long serialVersionUID = -3508418095993360485L;
  @Inject
  private UserViewPresenter presenter;
  @Inject
  protected UserFormPresenter userFormPresenter;
  protected Menu menu = new Menu();
  protected UserForm userForm = new UserForm();

  @PostConstruct
  protected void init() {
    menuLayout.addComponent(menu);
    userForm.setPresenter(userFormPresenter);
    userFormLayout.addComponent(userForm);
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
