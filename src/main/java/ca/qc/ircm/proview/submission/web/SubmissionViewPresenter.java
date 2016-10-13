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

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.util.BeanItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * Submission view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionViewPresenter {
  public static final String TITLE = "title";
  public static final String SUBMIT_ID = "submit";
  private static final Logger logger = LoggerFactory.getLogger(SubmissionViewPresenter.class);
  private SubmissionView view;
  @Inject
  protected SubmissionFormPresenter submissionFormPresenter;
  @Inject
  private SubmissionService submissionService;

  /**
   * Initialize presenter.
   *
   * @param view
   *          view
   */
  public void init(SubmissionView view) {
    logger.debug("Submission view");
    this.view = view;
    view.submissionForm.setPresenter(submissionFormPresenter);
    submissionFormPresenter.setEditable(true);
    setCaptions();
    view.editableCheckbox.addValueChangeListener(
        e -> submissionFormPresenter.setEditable(view.editableCheckbox.getValue()));
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE));
  }

  void setSubmissionById(Long id) {
    Submission submission = submissionService.get(id);
    submissionFormPresenter.setItemDataSource(new BeanItem<>(submission));
  }
}
