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

import static ca.qc.ircm.proview.FindbugsJustifications.DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD;

import com.vaadin.ui.themes.ValoTheme;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * View base layout.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MainLayout extends MainLayoutDesign {
  private static final long serialVersionUID = -3818536803897529844L;
  @Inject
  protected Menu menu;

  /**
   * Initializes layout.
   */
  @PostConstruct
  @SuppressFBWarnings(
      value = "NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD)
  public void init() {
    menuLayout.addComponent(menu);
    menuLayout.addStyleName(ValoTheme.LAYOUT_CARD);
    content.addStyleName(ValoTheme.PANEL_BORDERLESS);
  }

  @Override
  public void attach() {
    super.attach();
    getUI().getNavigator().addViewChangeListener(menu);
  }
}
