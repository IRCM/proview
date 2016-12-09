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

import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Contact view.
 */
@SpringView(name = ContactView.VIEW_NAME)
public class ContactView extends ContactViewDesign implements BaseView {
  private static final long serialVersionUID = -1067651526935267544L;
  public static final String VIEW_NAME = "contact";
  protected Menu menu = new Menu();
  @Inject
  private ContactViewPresenter presenter;

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
