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

package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.proview.web.ViewLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Guidelines view.
 */
@Route(value = GuidelinesView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ USER })
public class GuidelinesView extends VerticalLayout
    implements LocaleChangeObserver, HasDynamicTitle {
  public static final String VIEW_NAME = "guidelines";
  public static final String ID = styleName(VIEW_NAME, "view");
  public static final String HEADER = "header";
  private static final long serialVersionUID = 1881767150748374598L;
  private static final Logger logger = LoggerFactory.getLogger(GuidelinesView.class);
  protected H2 header = new H2();
  private transient GuidelinesConfiguration guidelinesConfiguration;

  @Autowired
  protected GuidelinesView(GuidelinesConfiguration guidelinesConfiguration) {
    this.guidelinesConfiguration = guidelinesConfiguration;
  }

  @PostConstruct
  void init() {
    logger.debug("guidelines view");
    setId(ID);
    add(header);
    header.setId(HEADER);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final AppResources resources = new AppResources(getClass(), getLocale());
    removeAll();
    add(header);
    header.setText(resources.message(HEADER));
    guidelinesConfiguration.categories(getLocale())
        .forEach(category -> add(new CategoryComponent(category)));
  }

  @Override
  public String getPageTitle() {
    final AppResources resources = new AppResources(getClass(), getLocale());
    final AppResources generalResources = new AppResources(Constants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }
}
