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
import com.vaadin.ui.CustomComponent;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Submission results form.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionAnalysesForm extends CustomComponent implements BaseComponent {
  private static final long serialVersionUID = 3301905236780067587L;
  protected SubmissionAnalysesFormDesign design = new SubmissionAnalysesFormDesign();
  @Inject
  private transient SubmissionAnalysesFormPresenter presenter;

  @PostConstruct
  public void init() {
    setSizeFull();
    setCompositionRoot(design);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public Submission getValue() {
    return presenter.getValue();
  }

  public void setValue(Submission submission) {
    presenter.setValue(submission);
  }
}
