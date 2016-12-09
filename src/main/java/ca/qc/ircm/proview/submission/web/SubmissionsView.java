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

package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.web.Menu;
import ca.qc.ircm.proview.web.component.SavedSubmissionsComponent;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Submissions view.
 */
@SpringView(name = SubmissionsView.VIEW_NAME)
@RolesAllowed({ "USER" })
public class SubmissionsView extends SubmissionsViewDesign
    implements BaseView, SavedSubmissionsComponent {
  public static final String VIEW_NAME = "submissions";
  private static final long serialVersionUID = -7912663074202035516L;
  @Inject
  private SubmissionsViewPresenter presenter;
  protected Menu menu = new Menu();

  @PostConstruct
  public void init() {
    menuLayout.addComponent(menu);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }
}
