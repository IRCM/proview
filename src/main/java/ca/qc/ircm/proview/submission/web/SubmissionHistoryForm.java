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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * Submission history form.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionHistoryForm extends SubmissionHistoryFormDesign implements BaseComponent {
  private static final long serialVersionUID = 4814629523385144606L;
  @Inject
  private transient SubmissionHistoryFormPresenter presenter;

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public Submission getBean() {
    return presenter.getBean();
  }

  public void setBean(Submission submission) {
    presenter.setBean(submission);
  }
}
