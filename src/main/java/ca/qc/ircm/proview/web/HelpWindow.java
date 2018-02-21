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

import static ca.qc.ircm.proview.FindbugsExplanations.DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD;
import static ca.qc.ircm.proview.web.CloseWindowOnViewChange.closeWindowOnViewChange;

import ca.qc.ircm.proview.web.component.BaseComponent;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Window;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

/**
 * Windows that shows submission.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressFBWarnings(
    value = "NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
    justification = DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD)
public class HelpWindow extends Window implements BaseComponent {
  public static final String WINDOW_STYLE = "help-window";
  public static final String TITLE = "title";
  public static final String HELP = "help";
  private static final long serialVersionUID = 4789125002422549258L;
  private static final Logger logger = LoggerFactory.getLogger(HelpWindow.class);
  private HelpWindowDesign design = new HelpWindowDesign();
  private boolean titleUpdated = false;

  @PostConstruct
  protected void init() {
    addStyleName(WINDOW_STYLE);
    setContent(design);
    setHeight("700px");
    setWidth("1200px");
  }

  @Override
  public void attach() {
    super.attach();
    logger.debug("Show help window");
    if (!titleUpdated) {
      MessageResource resources = getResources();
      design.panel.setCaption(resources.message(TITLE));
    }
    closeWindowOnViewChange(this);
  }

  /**
   * Sets window title.
   *
   * @param title
   *          window title
   */
  public void setTitle(String title) {
    titleUpdated = true;
    if (isAttached()) {
      updateTitle(title);
    } else {
      addAttachListener(e -> updateTitle(title));
    }
  }

  /**
   * Sets help message.
   *
   * @param help
   *          help message
   */
  public void setHelp(String help) {
    setHelp(help, ContentMode.TEXT);
  }

  /**
   * Sets help message.
   *
   * @param help
   *          help message
   * @param contentMode
   *          content mode
   */
  public void setHelp(String help, ContentMode contentMode) {
    if (isAttached()) {
      updateHelp(help, contentMode);
    } else {
      addAttachListener(e -> updateHelp(help, contentMode));
    }
  }

  private void updateTitle(String title) {
    design.panel.setCaption(title);
  }

  private void updateHelp(String help, ContentMode contentMode) {
    design.help.setValue(help);
    design.help.setContentMode(contentMode);
  }
}
