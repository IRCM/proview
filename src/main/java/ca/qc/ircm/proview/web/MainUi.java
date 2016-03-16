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

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Main Vaadin UI.
 */
@Theme("valo")
@SpringUI(path = "")
@Push(value = PushMode.AUTOMATIC, transport = Transport.LONG_POLLING)
public class MainUi extends UI {
  private static final long serialVersionUID = -7400782917472488086L;
  @Inject
  private SpringViewProvider viewProvider;

  /**
   * Initialize navigator.
   */
  @PostConstruct
  public void initialize() {
    Navigator navigator = new Navigator(this, this);
    navigator.addProvider(viewProvider);
    getNavigator().setErrorView(ErrorView.class);
  }

  @Override
  protected void init(VaadinRequest vaadinRequest) {
  }
}
