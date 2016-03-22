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

import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.utils.web.VaadinUtils;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContext;

/**
 * Main Vaadin UI.
 */
@Theme("proview")
@SpringUI
@Push(value = PushMode.AUTOMATIC, transport = Transport.LONG_POLLING)
@Widgetset("ca.qc.ircm.proview.ProviewWidgetset")
public class MainUi extends UI {
  private static final long serialVersionUID = 5623532890650543834L;
  @Inject
  private SpringViewProvider viewProvider;
  @Inject
  private VaadinUtils vaadinUtils;

  /**
   * Initialize navigator.
   */
  @PostConstruct
  public void initialize() {
    viewProvider.setAccessDeniedViewClass(AccessDeniedView.class);
    Navigator navigator = new Navigator(this, this);
    navigator.addProvider(viewProvider);
    getNavigator().setErrorView(ErrorView.class);
  }

  @Override
  protected void init(VaadinRequest vaadinRequest) {
    if (getUI().getLocale() == null) {
      // TODO Use user's locale rather than a default one.
      getUI().getSession().setLocale(Locale.FRENCH);
    }
  }

  public ServletContext getServletContext() {
    return vaadinUtils.getServletContext();
  }
}
