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

package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.FindbugsExplanations.DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.web.component.BaseComponent;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.Window;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * User window.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressFBWarnings(
    value = "NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
    justification = DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD)
public class UserWindow extends Window implements BaseComponent {
  public static final String WINDOW_STYLE = "user-window";
  public static final String TITLE = "title";
  public static final String UPDATE = "update";
  private static final long serialVersionUID = 9032686080431923743L;
  private static final Logger logger = LoggerFactory.getLogger(UserWindow.class);
  private UserWindowDesign design = new UserWindowDesign();
  @Inject
  private UserForm userForm;
  @Inject
  private AuthorizationService authorizationService;

  @PostConstruct
  protected void init() {
    addStyleName(WINDOW_STYLE);
    setContent(design);
    design.userLayout.addComponent(userForm);
    design.update.setVisible(false);
    design.update.addStyleName(UPDATE);
    setHeight("650px");
    setWidth("500px");
  }

  @Override
  public void attach() {
    super.attach();
    final MessageResource resources = getResources();
    userForm.addSaveListener(e -> close());
    design.update.setCaption(resources.message(UPDATE));
  }

  /**
   * Sets user.
   *
   * @param user
   *          user
   */
  public void setValue(User user) {
    if (isAttached()) {
      updateUser(user);
    } else {
      addAttachListener(e -> updateUser(user));
    }
  }

  private void updateUser(User user) {
    logger.debug("User window for user {}", user);
    setCaption(getResources().message(TITLE, user != null ? user.getName() : ""));
    design.update.setVisible(authorizationService.hasUserWritePermission(user));
    design.update.addClickListener(e -> {
      navigateTo(UserView.VIEW_NAME, String.valueOf(user.getId()));
      close();
    });
    userForm.setValue(user);
    userForm.setReadOnly(true);
  }
}
