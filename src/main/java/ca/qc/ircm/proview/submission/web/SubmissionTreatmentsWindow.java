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
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Windows that shows submission treatments.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionTreatmentsWindow extends Window implements BaseComponent {
  public static final String WINDOW_STYLE = "submission-treatments-window";
  public static final String TITLE = "title";
  private static final long serialVersionUID = 4061245566728550570L;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionTreatmentsWindow.class);
  private Panel panel;
  @Inject
  private SubmissionTreatmentsForm view = new SubmissionTreatmentsForm();

  @PostConstruct
  protected void init() {
    addStyleName(WINDOW_STYLE);
    panel = new Panel();
    setContent(panel);
    panel.setContent(view);
    view.setMargin(true);
    setHeight("700px");
    setWidth("1200px");
    panel.setSizeFull();
  }

  @Override
  public void attach() {
    super.attach();
  }

  /**
   * Sets submission.
   *
   * @param submission
   *          submission
   */
  public void setSubmission(Submission submission) {
    if (isAttached()) {
      updateSubmission(submission);
    } else {
      addAttachListener(e -> updateSubmission(submission));
    }
  }

  private void updateSubmission(Submission submission) {
    logger.debug("Submission treatments window for submission {}", submission);
    setCaption(getResources().message(TITLE, submission.getExperience()));
    view.setBean(submission);
  }
}
