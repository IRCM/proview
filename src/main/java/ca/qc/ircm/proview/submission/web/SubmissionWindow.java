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

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.component.BaseComponent;
import ca.qc.ircm.utils.MessageResource;
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
 * Windows that shows submission.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressFBWarnings(
    value = "NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
    justification = DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD)
public class SubmissionWindow extends Window implements BaseComponent {
  public static final String WINDOW_STYLE = "submission-window";
  public static final String TITLE = "title";
  public static final String UPDATE = "update";
  private static final long serialVersionUID = 4789125002422549258L;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionWindow.class);
  private SubmissionWindowDesign view = new SubmissionWindowDesign();
  @Inject
  private SubmissionForm submissionForm;
  @Inject
  private AuthorizationService authorizationService;

  @PostConstruct
  protected void init() {
    addStyleName(WINDOW_STYLE);
    setContent(view);
    view.submissionLayout.addComponent(submissionForm);
    view.update.addStyleName(UPDATE);
    view.update.setVisible(false);
    setHeight("700px");
    setWidth("1200px");
  }

  @Override
  public void attach() {
    super.attach();
    MessageResource resources = getResources();
    view.update.setCaption(resources.message(UPDATE));
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
    logger.debug("Submission window for submission {}", submission);
    setCaption(getResources().message(TITLE, submission.getExperience()));
    view.update.setVisible(authorizationService.hasSubmissionWritePermission(submission));
    view.update.addClickListener(e -> {
      navigateTo(SubmissionView.VIEW_NAME, String.valueOf(submission.getId()));
      close();
    });
    submissionForm.setValue(submission);
    submissionForm.setReadOnly(true);
  }
}
