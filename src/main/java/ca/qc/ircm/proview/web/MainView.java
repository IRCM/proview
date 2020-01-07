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

package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.submission.web.SubmissionsView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;

/**
 * Main view.
 */
@Route(value = MainView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ USER })
public class MainView extends VerticalLayout implements BeforeEnterObserver {
  public static final String VIEW_NAME = "";
  private static final long serialVersionUID = -4472228116629914718L;

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    event.forwardTo(SubmissionsView.class);
  }
}
