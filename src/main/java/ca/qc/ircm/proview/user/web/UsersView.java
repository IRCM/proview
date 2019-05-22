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

import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Users view.
 */
@SpringView(name = UsersView.VIEW_NAME)
@RolesAllowed({ UserRole.ADMIN, UserRole.MANAGER })
public class UsersView extends CustomComponent implements BaseView {
  public static final String VIEW_NAME = "users";
  public static final String SWITCH_USER = "switchUser";
  public static final String SWITCH_FAILED = "switchFailed";
  private static final long serialVersionUID = -1897739429426168438L;
  protected UsersViewDesign design = new UsersViewDesign();
  @Inject
  private transient UsersViewPresenter presenter;

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
}
