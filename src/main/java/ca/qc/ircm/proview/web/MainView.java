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

import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Main view.
 */
@SpringView(name = MainView.VIEW_NAME)
public class MainView extends CustomComponent implements BaseView {
  private static final long serialVersionUID = -2537732272999926530L;
  public static final String VIEW_NAME = "";
  private static final Logger logger = LoggerFactory.getLogger(MainView.class);
  protected MainViewDesign design = new MainViewDesign();
  protected CustomLoginForm signForm = new CustomLoginForm();
  @Inject
  private transient MainViewPresenter presenter;

  /**
   * Creates main view.
   */
  public MainView() {
    setSizeFull();
    setCompositionRoot(design);
    design.signLayout.addComponent(signForm);
  }

  @Override
  public void attach() {
    logger.debug("Main view");
    super.attach();
    signForm.getLayout().setMargin(false);
    presenter.init(this);
  }

  @Override
  public void enter(ViewChangeEvent event) {
    presenter.enter(event.getParameters());
  }
}
