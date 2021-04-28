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
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Print submission view.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PrintSubmissionViewPresenter {
  private PrintSubmissionView view;
  private Submission submission;
  private SubmissionService service;

  @Autowired
  protected PrintSubmissionViewPresenter(SubmissionService service) {
    this.service = service;
  }

  void init(PrintSubmissionView view) {
    this.view = view;
  }

  void setParameter(Long parameter) {
    if (parameter != null) {
      submission = service.get(parameter).orElse(null);
    }
    view.printContent.setSubmission(submission);
  }

  Submission getSubmission() {
    return submission;
  }
}
