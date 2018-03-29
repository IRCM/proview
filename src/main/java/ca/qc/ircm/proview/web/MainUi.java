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

import static ca.qc.ircm.proview.web.WebConstants.DEFAULT_LOCALE;

import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.user.web.ForgotPasswordView;
import ca.qc.ircm.proview.user.web.ValidateView;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletContext;

/**
 * Main Vaadin UI.
 */
@Theme("proview")
@SpringUI
@PushStateNavigation
@Push(value = PushMode.AUTOMATIC, transport = Transport.LONG_POLLING)
@Widgetset("ca.qc.ircm.proview.ProviewWidgetset")
public class MainUi extends UI {
  private static final String SKIP_ABOUT = "SKIP_ABOUT";
  private static final long serialVersionUID = 5623532890650543834L;
  @Inject
  private MainLayout layout;
  @Inject
  private Provider<SpringNavigator> springNavigatorProvider;
  @Inject
  private SpringViewProvider viewProvider;

  /**
   * Initialize navigator.
   */
  @PostConstruct
  public void initialize() {
    viewProvider.setAccessDeniedViewClass(AccessDeniedView.class);
    SpringNavigator navigator = springNavigatorProvider.get();
    navigator.init(this, layout.design.content);
    getNavigator().setErrorView(ErrorView.class);
    getNavigator().addViewChangeListener(layout.menu);
    setContent(layout);
  }

  @Override
  protected void init(VaadinRequest vaadinRequest) {
    if (getUI().getLocale() == null) {
      // TODO Use user's locale rather than a default one.
      getUI().getSession().setLocale(DEFAULT_LOCALE);
    }
    if (getUI().getSession().getAttribute(SKIP_ABOUT) == null) {
      getUI().getSession().setAttribute(SKIP_ABOUT, true);
      String url = getPage().getLocation().toString();
      if (!url.contains(ForgotPasswordView.VIEW_NAME) && !url.contains(ValidateView.VIEW_NAME)) {
        getNavigator().navigateTo(AboutView.VIEW_NAME);
      }
    }
    checkForLegacyHashBangUrl();
  }

  /**
   * This method supports old style #!foo/bar URLs that people might still link if the app has been
   * in use before HTML5 History era.
   */
  protected void checkForLegacyHashBangUrl() {
    String uriFragment = getPage().getUriFragment();
    if (uriFragment != null) {
      if (uriFragment.startsWith("!")) {
        // Assume this is a legacy hashbang URL, assign directly to Navigator and clear
        getNavigator().navigateTo(uriFragment.substring(1));
        getPage().setUriFragment(null);
      } else {
        // Some other fragment, just ignore or do what you have done before
      }
    }
  }

  public ServletContext getServletContext() {
    return VaadinServlet.getCurrent().getServletContext();
  }

  public String getUrl(String viewName) {
    return getServletContext().getContextPath() + "/#!" + viewName;
  }
}
