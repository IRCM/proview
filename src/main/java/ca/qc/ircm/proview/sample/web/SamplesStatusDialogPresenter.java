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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.sample.web.SamplesStatusDialog.SAVED;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.WebConstants;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Samples status dialog.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SamplesStatusDialogPresenter {
  private static final Logger logger = LoggerFactory.getLogger(SamplesStatusDialogPresenter.class);
  private SamplesStatusDialog dialog;
  private Submission submission;
  private Locale locale;
  private Map<SubmissionSample, Binder<SubmissionSample>> binders = new HashMap<>();
  private SubmissionSampleService service;

  @Autowired
  private SamplesStatusDialogPresenter(SubmissionSampleService service) {
    this.service = service;
  }

  void init(SamplesStatusDialog dialog) {
    this.dialog = dialog;
    dialog.addOpenedChangeListener(e -> dialog.allStatus.clear());
  }

  void bindFields() {
    if (submission != null && locale != null) {
      final AppResources webResources = new AppResources(WebConstants.class, locale);
      for (SubmissionSample sample : submission.getSamples()) {
        Binder<SubmissionSample> binder =
            new BeanValidationBinder<SubmissionSample>(SubmissionSample.class);
        binder.forField(dialog.status(sample)).asRequired(webResources.message(REQUIRED))
            .bind(STATUS);
        binder.setBean(sample);
        binders.put(sample, binder);
      }
    }
  }

  void localeChange(Locale locale) {
    this.locale = locale;
    bindFields();
  }

  void setAllStatus(SampleStatus status) {
    if (status != null) {
      submission.getSamples().stream().forEach(sample -> dialog.status(sample).setValue(status));
    }
  }

  List<BinderValidationStatus<SubmissionSample>> validateSamples() {
    return submission.getSamples().stream().map(sample -> binders.get(sample).validate())
        .collect(Collectors.toList());
  }

  private boolean validate() {
    return submission != null
        && !validateSamples().stream().filter(status -> !status.isOk()).findAny().isPresent();
  }

  void save() {
    if (validate()) {
      logger.debug("update samples' status of submission {}", submission);
      AppResources resources = new AppResources(SamplesStatusDialog.class, locale);
      service.updateStatus(submission.getSamples());
      dialog.showNotification(resources.message(SAVED, submission.getExperiment()));
      dialog.close();
      dialog.fireSavedEvent();
    }
  }

  void cancel() {
    dialog.close();
  }

  Submission getSubmission() {
    return submission;
  }

  void setSubmission(Submission submission) {
    this.submission = submission;
    dialog.samples.setItems(submission.getSamples());
    bindFields();
  }
}
