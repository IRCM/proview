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

package ca.qc.ircm.proview.security.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.web.MainView;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

/**
 * Access denied view.
 */
@Route("accessdenied")
@AnonymousAllowed
public class AccessDeniedView extends Composite<VerticalLayout>
    implements HasErrorParameter<AccessDeniedException>, LocaleChangeObserver, HasDynamicTitle {
  public static final String VIEW_NAME = "accessdenied";
  public static final String HEADER = "header";
  public static final String MESSAGE = "message";
  public static final String HOME = "home";
  private static final long serialVersionUID = -5974627607641654031L;
  private static final Logger logger = LoggerFactory.getLogger(AccessDeniedView.class);
  protected H2 header = new H2();
  protected Div message = new Div();
  protected Button home = new Button();

  /**
   * Creates new access denied view.
   */
  public AccessDeniedView() {
    logger.debug("access denied view");
    VerticalLayout root = getContent();
    root.setId(VIEW_NAME);
    root.add(header, header, home);
    header.addClassName(HEADER);
    message.addClassName(MESSAGE);
    home.addClassName(HOME);
    home.addClickListener(e -> UI.getCurrent().navigate(MainView.class));
  }

  @Override
  public int setErrorParameter(BeforeEnterEvent event,
      ErrorParameter<AccessDeniedException> parameter) {
    return HttpServletResponse.SC_FORBIDDEN;
  }

  @Override
  public String getPageTitle() {
    final AppResources resources = new AppResources(getClass(), getLocale());
    final AppResources generalResources = new AppResources(Constants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final AppResources resources = new AppResources(getClass(), getLocale());
    header.setText(resources.message(HEADER));
    message.setText(resources.message(MESSAGE));
    home.setText(resources.message(HOME));
  }
}
