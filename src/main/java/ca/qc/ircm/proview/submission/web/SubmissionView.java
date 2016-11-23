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

package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import ca.qc.ircm.proview.web.Menu;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Submission view.
 */
@SpringView(name = SubmissionView.VIEW_NAME)
public class SubmissionView extends SubmissionViewDesign implements MessageResourcesView {
  public static final String VIEW_NAME = "submission";
  private static final long serialVersionUID = -6009778227571187664L;
  @Inject
  private SubmissionViewPresenter presenter;
  @Inject
  protected SubmissionFormPresenter submissionFormPresenter;
  protected Menu menu = new Menu();
  protected SubmissionForm submissionForm = new SubmissionForm();

  /**
   * Initializes view.
   */
  @PostConstruct
  public void init() {
    menuLayout.addComponent(menu);
    submissionForm.setPresenter(submissionFormPresenter);
    submissionFormLayout.addComponent(submissionForm);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  @Override
  public void enter(ViewChangeEvent event) {
    presenter.enter(event.getParameters());
  }
}
