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

import static ca.qc.ircm.proview.FindbugsExplanations.DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.component.BaseComponent;
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
 * Windows that shows submission treatments.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressFBWarnings(
    value = "NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
    justification = DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD)
public class SubmissionTreatmentsWindow extends Window implements BaseComponent {
  public static final String WINDOW_STYLE = "submission-treatments-window";
  public static final String TITLE = "title";
  private static final long serialVersionUID = 4061245566728550570L;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionTreatmentsWindow.class);
  private SubmissionTreatmentsWindowDesign design = new SubmissionTreatmentsWindowDesign();
  @Inject
  private SubmissionTreatmentsForm view = new SubmissionTreatmentsForm();

  @PostConstruct
  protected void init() {
    addStyleName(WINDOW_STYLE);
    setContent(design);
    design.treatmentsLayout.addComponent(view);
    setHeight("700px");
    setWidth("1200px");
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
  public void setValue(Submission submission) {
    if (isAttached()) {
      updateSubmission(submission);
    } else {
      addAttachListener(e -> updateSubmission(submission));
    }
  }

  private void updateSubmission(Submission submission) {
    logger.debug("Submission treatments window for submission {}", submission);
    setCaption(getResources().message(TITLE, submission.getExperience()));
    view.setValue(submission);
  }
}
