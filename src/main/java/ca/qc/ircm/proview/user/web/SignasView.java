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

import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Sign as another user view.
 */
@SpringView(name = SignasView.VIEW_NAME)
@RolesAllowed("ADMIN")
public class SignasView extends SignasViewDesign implements BaseView {
  public static final String VIEW_NAME = "user/signas";
  private static final long serialVersionUID = 2149245363614885816L;
  @Inject
  private transient SignasViewPresenter presenter;

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }
}
