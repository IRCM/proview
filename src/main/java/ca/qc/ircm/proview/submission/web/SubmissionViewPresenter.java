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

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.util.BeanItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
  public static final String HEADER_STYLE = "header";
  public static final String INVALID_SUBMISSION = "submission.invalid";
  private static final Logger logger = LoggerFactory.getLogger(SubmissionViewPresenter.class);
  private SubmissionView view;
  @Inject
  private SubmissionService submissionService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected SubmissionViewPresenter() {
  }

  protected SubmissionViewPresenter(SubmissionService submissionService, String applicationName) {
    this.submissionService = submissionService;
    this.applicationName = applicationName;
  }

  /**
   * Initialize presenter.
   *
   * @param view
   *          view
   */
  public void init(SubmissionView view) {
    logger.debug("Submission view");
    this.view = view;
    prepareComponents();
    view.submissionFormPresenter.setEditable(true);
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    view.headerLabel.addStyleName(HEADER_STYLE);
    view.headerLabel.addStyleName("h1");
    view.headerLabel.setValue(resources.message(HEADER_STYLE));
  }

  /**
   * Called when view is entered.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    if (parameters != null && !parameters.isEmpty()) {
      try {
        Long id = Long.valueOf(parameters);
        logger.debug("Set submission {}", id);
        Submission submission = submissionService.get(id);
        view.submissionFormPresenter.setItemDataSource(new BeanItem<>(submission));
        view.submissionFormPresenter.setEditable(editable(submission));
      } catch (NumberFormatException e) {
        view.showWarning(view.getResources().message(INVALID_SUBMISSION));
      }
    }
  }

  private boolean editable(Submission submission) {
    boolean editable = true;
    for (SubmissionSample sample : submission.getSamples()) {
      editable &= sample.getStatus() == SampleStatus.TO_RECEIVE;
    }
    return editable;
  }
}
