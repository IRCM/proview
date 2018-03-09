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
  private static final long serialVersionUID = 4789125002422549258L;
  protected SubmissionWindowDesign design = new SubmissionWindowDesign();
  private Submission submission;
  @Inject
  protected SubmissionForm submissionForm;
  @Inject
  protected SubmissionWindowPresenter presenter;

  @PostConstruct
  protected void init() {
    setContent(design);
    design.submissionLayout.addComponent(submissionForm);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
    presenter.setValue(submission);
  }

  /**
   * Sets submission.
   *
   * @param submission
   *          submission
   */
  public void setValue(Submission submission) {
    this.submission = submission;
    if (isAttached()) {
      presenter.setValue(submission);
    }
  }
}
