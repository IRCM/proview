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

import static ca.qc.ircm.proview.web.CloseWindowOnViewChange.closeWindowOnViewChange;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.utils.MessageResource;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Windows that shows submission.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionWindowPresenter {
  public static final String WINDOW_STYLE = "submission-window";
  public static final String TITLE = "title";
  private static final Logger logger = LoggerFactory.getLogger(SubmissionWindowPresenter.class);
  private SubmissionWindow window = new SubmissionWindow();
  @Inject
  private AuthorizationService authorizationService;

  protected SubmissionWindowPresenter() {
  }

  /**
   * Initializes presenter.
   *
   * @param window
   *          window
   */
  public void init(SubmissionWindow window) {
    this.window = window;
    prepareComponents();
  }

  private void prepareComponents() {
    closeWindowOnViewChange(window);
    window.addStyleName(WINDOW_STYLE);
    window.setHeight("700px");
    window.setWidth("1200px");
  }

  void setValue(Submission submission) {
    logger.debug("Submission window for submission {}", submission);
    MessageResource resources = window.getResources();
    window.setCaption(resources.message(TITLE, submission.getExperiment()));
    window.submissionForm.setValue(submission);
    window.submissionForm
        .setReadOnly(!authorizationService.hasSubmissionWritePermission(submission));
  }
}
