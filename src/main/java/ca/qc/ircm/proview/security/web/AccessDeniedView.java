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

import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.proview.web.view.BaseView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * View to show in case of an access denied.
 */
@UIScope
@SpringView(name = AccessDeniedView.VIEW_NAME)
public class AccessDeniedView extends VerticalLayout implements BaseView {
  private static final long serialVersionUID = 2998062811797958331L;
  private static final Logger logger = LoggerFactory.getLogger(AccessDeniedView.class);
  public static final String VIEW_NAME = "accessDenied";
  public static final String TITLE = "title";
  public static final String LABEL = "label";
  public static final String BUTTON = "button";
  private Label label = new Label();
  private Button button = new Button();
  @Value("${spring.application.name}")
  private String applicationName;

  /**
   * Creates error view.
   */
  @SuppressWarnings("serial")
  public AccessDeniedView() {
    setSizeFull();
    setMargin(true);
    setSpacing(true);

    addComponent(label);
    button.addClickListener(new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event) {
        navigateTo(MainView.VIEW_NAME);
      }
    });
    addComponent(button);
  }

  @Override
  public void attach() {
    super.attach();
    logger.debug("Access denied view");
    MessageResource resources = getResources();
    setTitle(resources.message(TITLE, applicationName));
    label.setValue(resources.message(LABEL));
    button.setCaption(resources.message(BUTTON));
  }
}
