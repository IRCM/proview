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

import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submission dialog presenter.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionDialogPresenter {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SubmissionDialogPresenter.class);
  private SubmissionDialog dialog;
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private SubmissionService service;

  @Autowired
  protected SubmissionDialogPresenter(SubmissionService service) {
    this.service = service;
  }

  /**
   * Initializes presenter.
   *
   * @param dialog
   *          dialog
   */
  void init(SubmissionDialog dialog) {
    this.dialog = dialog;
    setSubmission(null);
  }

  void localeChange(Locale locale) {
    binder.forField(dialog.instrument).withNullRepresentation(MassDetectionInstrument.NULL)
        .bind(INSTRUMENT);
    binder.forField(dialog.dataAvailableDate).bind(DATA_AVAILABLE_DATE);
  }

  private boolean validate() {
    return validateSubmission().isOk();
  }

  BinderValidationStatus<Submission> validateSubmission() {
    return binder.validate();
  }

  void save() {
    if (validate()) {
      service.update(binder.getBean(), null);
      dialog.close();
      dialog.fireSavedEvent();
    }
  }

  void edit() {
    UI.getCurrent().navigate(SubmissionView.class, binder.getBean().getId());
    dialog.close();
  }

  void print() {
    UI.getCurrent().navigate(PrintSubmissionView.class, binder.getBean().getId());
    dialog.close();
  }

  Submission getSubmission() {
    return binder.getBean();
  }

  void setSubmission(Submission submission) {
    if (submission == null) {
      submission = new Submission();
    }
    binder.setBean(submission);
    dialog.printContent.setSubmission(submission);
  }
}
