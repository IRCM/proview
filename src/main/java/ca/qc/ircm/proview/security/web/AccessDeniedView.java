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

import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * View to show in case of an access denied.
 */
@UIScope
@SpringView(name = AccessDeniedView.VIEW_NAME)
public class AccessDeniedView extends VerticalLayout implements MessageResourcesComponent, View {
  private static final long serialVersionUID = 2998062811797958331L;
  private static final Logger logger = LoggerFactory.getLogger(AccessDeniedView.class);
  public static final String VIEW_NAME = "accessDenied";
  @Inject
  private UI ui;
  private Label label = new Label();
  private Button button = new Button();

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
        ui.getNavigator().navigateTo(MainView.VIEW_NAME);
      }
    });
    addComponent(button);
  }

  @Override
  public void attach() {
    super.attach();
    logger.debug("Access denied view");
    MessageResource messageResource = getResources();
    label.setValue(messageResource.message("label"));
    button.setCaption(messageResource.message("button"));
  }

  @Override
  public void enter(ViewChangeEvent event) {
  }
}
