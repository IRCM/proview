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

import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleService;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Updates sample statuses presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SampleStatusViewPresenter implements BinderValidator {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String SAMPLES = "samples";
  public static final String SAVE = "save";
  public static final String NAME = submissionSample.name.getMetadata().getName();
  public static final String STATUS = submissionSample.status.getMetadata().getName();
  public static final String SUBMISSION = submission.getMetadata().getName();
  public static final String EXPERIENCE =
      SUBMISSION + "." + submission.experience.getMetadata().getName();
  public static final String NEW_STATUS = "newStatus";
  public static final String DOWN = "down";
  public static final String REGRESS = "regress";
  public static final String REGRESS_MESSAGE = "regress.message";
  public static final String OK = "ok";
  public static final String CANCEL = "cancel";
  public static final String INVALID_SAMPLES = "samples.invalid";
  public static final String SPLIT_SAMPLES_PARAMETERS = ",";
  private static final Logger logger = LoggerFactory.getLogger(SampleStatusViewPresenter.class);
  private SampleStatusView view;
  private SampleStatusViewDesign design;
  private ListDataProvider<SubmissionSample> dataProvider;
  private Map<Object, Binder<SubmissionSample>> sampleBinders = new HashMap<>();
  private Map<Object, ComboBox<SampleStatus>> sampleStatusFields = new HashMap<>();
  @Inject
  private SampleService sampleService;
  @Inject
  private SubmissionSampleService submissionSampleService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected SampleStatusViewPresenter() {
  }

  protected SampleStatusViewPresenter(SampleService sampleService,
      SubmissionSampleService submissionSampleService, String applicationName) {
    this.sampleService = sampleService;
    this.submissionSampleService = submissionSampleService;
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(SampleStatusView view) {
    logger.debug("Update sample status view");
    this.view = view;
    design = view.design;
    prepareComponents();
    addListeners();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    final Locale locale = view.getLocale();
    view.setTitle(resources.message(TITLE, applicationName));
    design.headerLabel.addStyleName(HEADER);
    design.headerLabel.setValue(resources.message(HEADER));
    design.samplesGrid.addStyleName(SAMPLES);
    design.samplesGrid.addStyleName(COMPONENTS);
    design.samplesGrid.addColumn(Sample::getName).setId(NAME).setCaption(resources.message(NAME));
    design.samplesGrid.addColumn(sample -> sample.getSubmission().getExperience()).setId(EXPERIENCE)
        .setCaption(resources.message(EXPERIENCE));
    design.samplesGrid.addColumn(sample -> sample.getStatus().getLabel(locale)).setId(STATUS)
        .setCaption(resources.message(STATUS));
    design.samplesGrid.addColumn(sample -> statusesComboBox(sample), new ComponentRenderer())
        .setId(NEW_STATUS).setCaption(resources.message(NEW_STATUS));
    design.samplesGrid.addColumn(sample -> downButton(sample), new ComponentRenderer()).setId(DOWN)
        .setCaption(resources.message(DOWN));
    design.samplesGrid.setSelectionMode(SelectionMode.NONE);
    design.saveButton.addStyleName(SAVE);
    design.saveButton.setCaption(resources.message(SAVE));
  }

  private void addListeners() {
    design.saveButton.addClickListener(e -> save());
  }

  private ComboBox<SampleStatus> statusesComboBox(SubmissionSample sample) {
    if (sampleStatusFields.containsKey(sample)) {
      return sampleStatusFields.get(sample);
    } else {
      final Locale locale = view.getLocale();
      final MessageResource generalResources = view.getGeneralResources();
      Binder<SubmissionSample> binder = new BeanValidationBinder<>(SubmissionSample.class);
      binder.setBean(sample);
      ComboBox<SampleStatus> statuses = new ComboBox<>();
      statuses.addStyleName(NEW_STATUS);
      statuses.setEmptySelectionAllowed(false);
      statuses.setItems(SampleStatus.values());
      statuses.setItemCaptionGenerator(status -> status.getLabel(locale));
      binder.forField(statuses).asRequired(generalResources.message(REQUIRED))
          .bind(SubmissionSample::getStatus, SubmissionSample::setStatus);
      sampleBinders.put(sample, binder);
      sampleStatusFields.put(sample, statuses);
      return statuses;
    }
  }

  private Button downButton(SubmissionSample sample) {
    MessageResource resources = view.getResources();
    Button down = new Button();
    down.addStyleName(DOWN);
    down.setCaption(resources.message(DOWN));
    down.addClickListener(e -> copyStatusDown(sample));
    return down;
  }

  private void copyStatusDown(SubmissionSample sample) {
    boolean copy = false;
    SampleStatus value = sampleStatusFields.get(sample).getValue();
    for (SubmissionSample other : dataProvider.getItems()) {
      if (sample.equals(other)) {
        copy = true;
      }
      if (copy) {
        sampleStatusFields.get(other).setValue(value);
      }
    }
  }

  private boolean validate() {
    boolean valid = true;
    for (Binder<SubmissionSample> binder : sampleBinders.values()) {
      valid &= validate(binder);
    }
    if (!valid) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("Validation failed");
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return valid;
  }

  private boolean statusRegress() {
    boolean regress = false;
    List<SubmissionSample> samples = sampleBinders.values().stream().map(binder -> binder.getBean())
        .collect(Collectors.toList());
    for (SubmissionSample sample : samples) {
      SampleStatus currentStatus = submissionSampleService.get(sample.getId()).getStatus();
      SampleStatus newStatus = sample.getStatus();
      regress |= newStatus.ordinal() < currentStatus.ordinal();
    }
    return regress;
  }

  private void showConfirmDialog() {
    MessageResource resources = view.getResources();
    String windowCaption = resources.message(REGRESS);
    String message = resources.message(REGRESS_MESSAGE);
    String okCaption = resources.message(OK);
    String cancelCaption = resources.message(CANCEL);
    view.showConfirmDialog(windowCaption, message, okCaption, cancelCaption, dialog -> {
      if (dialog.isConfirmed()) {
        saveToDatabase();
      }
    });
  }

  private void save() {
    if (validate()) {
      if (statusRegress()) {
        showConfirmDialog();
      } else {
        saveToDatabase();
      }
    }
  }

  private void saveToDatabase() {
    logger.debug("Save statuses to database");
    final MessageResource resources = view.getResources();
    List<SubmissionSample> samples = sampleBinders.values().stream().map(binder -> binder.getBean())
        .collect(Collectors.toList());
    submissionSampleService.updateStatus(new ArrayList<>(samples));
    refresh();
    view.showTrayNotification(resources.message(SAVE + ".done", samples.size()));
  }

  private void refresh() {
    List<SubmissionSample> samples = dataProvider.getItems().stream()
        .map(sample -> submissionSampleService.get(sample.getId())).collect(Collectors.toList());
    dataProvider = DataProvider.ofCollection(samples);
    design.samplesGrid.setDataProvider(dataProvider);
    design.samplesGrid.setSortOrder(new ArrayList<>(design.samplesGrid.getSortOrder()));
  }

  private boolean validateParameters(String parameters) {
    boolean valid = true;
    String[] rawIds = parameters.split(SPLIT_SAMPLES_PARAMETERS, -1);
    if (rawIds.length < 1) {
      valid = false;
    }
    try {
      for (String rawId : rawIds) {
        Long id = Long.valueOf(rawId);
        if (sampleService.get(id) == null) {
          valid = false;
        }
      }
    } catch (NumberFormatException e) {
      valid = false;
    }
    return valid;
  }

  /**
   * Called by view when entered.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    Collection<Sample> samplesParameters;
    if (parameters == null || parameters.isEmpty()) {
      logger.trace("Recovering samples from session");
      samplesParameters = view.savedSamples();
    } else {
      logger.trace("Parsing samples from parameters");
      samplesParameters = new ArrayList<>();
      if (validateParameters(parameters)) {
        String[] rawIds = parameters.split(SPLIT_SAMPLES_PARAMETERS, -1);
        for (String rawId : rawIds) {
          Long id = Long.valueOf(rawId);
          samplesParameters.add(sampleService.get(id));
        }
      } else {
        view.showWarning(view.getResources().message(INVALID_SAMPLES));
      }
    }

    List<SubmissionSample> samples =
        samplesParameters.stream().filter(s -> s instanceof SubmissionSample)
            .map(s -> (SubmissionSample) s).collect(Collectors.toList());
    dataProvider = DataProvider.ofCollection(samples);
    design.samplesGrid.setDataProvider(dataProvider);
  }
}
