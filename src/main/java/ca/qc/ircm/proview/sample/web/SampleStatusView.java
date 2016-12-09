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

import ca.qc.ircm.proview.web.Menu;
import ca.qc.ircm.proview.web.component.SavedSubmissionsComponent;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Updates sample statuses.
 */
@SpringView(name = SampleStatusView.VIEW_NAME)
@RolesAllowed({ "ADMIN" })
public class SampleStatusView extends SampleStatusViewDesign
    implements BaseView, SavedSubmissionsComponent {
  private static final long serialVersionUID = -2790503384190960260L;
  public static final String VIEW_NAME = "samples/status";
  protected Menu menu = new Menu();
  @Inject
  private SampleStatusViewPresenter presenter;

  @PostConstruct
  public void ini() {
    menuLayout.addComponent(menu);
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
