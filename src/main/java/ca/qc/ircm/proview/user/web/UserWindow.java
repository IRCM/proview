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

import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
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
public class UserWindow extends Window implements BaseComponent {
  public static final String WINDOW_STYLE = "user-window";
  public static final String TITLE = "title";
  private static final long serialVersionUID = 9032686080431923743L;
  private Panel panel;
  private User user;
  @Inject
  private UserForm view;

  @PostConstruct
  protected void init() {
    addStyleName(WINDOW_STYLE);
    panel = new Panel();
    setContent(panel);
    panel.setContent(view);
    view.setMargin(true);
    setHeight("650px");
    setWidth("500px");
    panel.setSizeFull();
  }

  @Override
  public void attach() {
    super.attach();
    setCaption(getResources().message(TITLE, user.getEmail()));
    view.getPresenter().setBean(user);
    view.getPresenter().addSaveListener(e -> close());
  }

  public void setUser(User user) {
    this.user = user;
  }
}
