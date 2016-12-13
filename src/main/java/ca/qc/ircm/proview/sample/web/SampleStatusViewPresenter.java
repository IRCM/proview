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
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.GENERAL_MESSAGES;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleService;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.SelectionMode;
import de.datenhahn.vaadin.componentrenderer.ComponentCellKeyExtension;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
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
public class SampleStatusViewPresenter {
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
  public static final String COMPONENTS = "components";
  public static final String INVALID_SUBMISSIONS = "submissions.invalid";
  private static final Object[] SAMPLES_COLUMNS =
      new Object[] { NAME, EXPERIENCE, STATUS, NEW_STATUS, DOWN };
  public static final String SPLIT_SAMPLES_PARAMETERS = ",";
  private static final Logger logger = LoggerFactory.getLogger(SampleStatusViewPresenter.class);
  private SampleStatusView view;
  private BeanItemContainer<SubmissionSample> samplesContainer =
      new BeanItemContainer<>(SubmissionSample.class);
  private GeneratedPropertyContainer samplesGridContainer =
      new GeneratedPropertyContainer(samplesContainer);
  private Map<Object, BeanFieldGroup<SubmissionSample>> samplesFieldGroup = new HashMap<>();
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
    prepareComponents();
    addListeners();
  }

  @SuppressWarnings("serial")
  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.setValue(resources.message(HEADER));
    samplesContainer.addNestedContainerProperty(EXPERIENCE);
    samplesGridContainer.addGeneratedProperty(STATUS,
        new SampleStatusGenerator(() -> view.getLocale()));
    samplesGridContainer.addGeneratedProperty(NEW_STATUS, new PropertyValueGenerator<ComboBox>() {
      @Override
      public ComboBox getValue(Item item, Object itemId, Object propertyId) {
        BeanFieldGroup<SubmissionSample> fieldGroup = samplesFieldGroup.get(itemId);
        fieldGroup = new BeanFieldGroup<>(SubmissionSample.class);
        fieldGroup.setItemDataSource(item);
        samplesFieldGroup.put(itemId, fieldGroup);
        ComboBox statuses = statusesComboBox();
        fieldGroup.bind(statuses, STATUS);
        return statuses;
      }

      @Override
      public Class<ComboBox> getType() {
        return ComboBox.class;
      }
    });
    samplesGridContainer.addGeneratedProperty(DOWN, new PropertyValueGenerator<Button>() {
      @Override
      public Button getValue(Item item, Object itemId, Object propertyId) {
        Button down = new Button();
        down.addStyleName(DOWN);
        down.setCaption(resources.message(DOWN));
        down.addClickListener(e -> copyStatusDown(itemId));
        return down;
      }

      @Override
      public Class<Button> getType() {
        return Button.class;
      }
    });
    view.samplesGrid.addStyleName(SAMPLES);
    view.samplesGrid.addStyleName(COMPONENTS);
    ComponentCellKeyExtension.extend(view.samplesGrid);
    view.samplesGrid.setContainerDataSource(samplesGridContainer);
    view.samplesGrid.setSelectionMode(SelectionMode.MULTI);
    view.samplesGrid.setColumns(SAMPLES_COLUMNS);
    for (Object propertyId : SAMPLES_COLUMNS) {
      view.samplesGrid.getColumn(propertyId)
          .setHeaderCaption(resources.message((String) propertyId));
    }
    view.samplesGrid.getColumn(NEW_STATUS).setRenderer(new ComponentRenderer());
    view.samplesGrid.getColumn(DOWN).setRenderer(new ComponentRenderer());
    view.saveButton.addStyleName(SAVE);
    view.saveButton.setCaption(resources.message(SAVE));
  }

  private void addListeners() {
    view.saveButton.addClickListener(e -> save());
  }

  private ComboBox statusesComboBox() {
    final MessageResource generalResources =
        new MessageResource(GENERAL_MESSAGES, view.getLocale());
    Locale locale = view.getLocale();
    ComboBox statuses = new ComboBox();
    statuses.addStyleName(NEW_STATUS);
    statuses.setNullSelectionAllowed(false);
    statuses.setNewItemsAllowed(false);
    for (SampleStatus status : SampleStatus.values()) {
      statuses.addItem(status);
      statuses.setItemCaption(status, status.getLabel(locale));
    }
    statuses.setRequired(true);
    statuses.setRequiredError(generalResources.message(REQUIRED));
    return statuses;
  }

  private void copyStatusDown(Object itemId) {
    boolean copy = false;
    Object value = samplesFieldGroup.get(itemId).getField(STATUS).getValue();
    for (int i = 0; i < samplesGridContainer.size(); i++) {
      Object iterateItemId = samplesContainer.getIdByIndex(i);
      if (itemId.equals(iterateItemId)) {
        copy = true;
      }
      if (copy) {
        ((ComboBox) samplesFieldGroup.get(iterateItemId).getField(STATUS)).setValue(value);
      }
    }
  }

  private boolean validate() {
    boolean valid = true;
    Collection<Object> itemIds = view.samplesGrid.getSelectedRows();
    try {
      for (Object itemId : itemIds) {
        samplesFieldGroup.get(itemId).commit();
      }
    } catch (CommitException e) {
      final MessageResource generalResources =
          new MessageResource(GENERAL_MESSAGES, view.getLocale());
      logger.trace("Validation commit failed with message {}", e.getMessage(), e);
      view.showError(generalResources.message(FIELD_NOTIFICATION));
      valid = false;
    }
    return valid;
  }

  private boolean statusRegress() {
    boolean regress = false;
    List<SubmissionSample> samples = view.samplesGrid.getSelectedRows().stream()
        .map(id -> (SubmissionSample) id).collect(Collectors.toList());
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
    MessageResource resources = view.getResources();
    List<SubmissionSample> samples = view.samplesGrid.getSelectedRows().stream()
        .map(id -> (SubmissionSample) id).collect(Collectors.toList());
    submissionSampleService.updateStatus(samples);
    refresh();
    view.showTrayNotification(resources.message(SAVE + ".done", samples.size()));
  }

  private void refresh() {
    view.samplesGrid.deselectAll();
    Collection<?> itemIds = samplesGridContainer.getItemIds();
    List<SubmissionSample> samples = itemIds.stream().map(itemId -> (SubmissionSample) itemId)
        .map(sample -> submissionSampleService.get(sample.getId())).collect(Collectors.toList());
    samplesContainer.removeAllItems();
    samples.stream().forEach(sample -> samplesContainer.addItem(sample));
    view.samplesGrid.setSortOrder(new ArrayList<>(view.samplesGrid.getSortOrder()));
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
      logger.trace("Parsing submissions from parameters");
      samplesParameters = new ArrayList<>();
      if (validateParameters(parameters)) {
        String[] rawIds = parameters.split(SPLIT_SAMPLES_PARAMETERS, -1);
        for (String rawId : rawIds) {
          Long id = Long.valueOf(rawId);
          samplesParameters.add(sampleService.get(id));
        }
      } else {
        view.showWarning(view.getResources().message(INVALID_SUBMISSIONS));
      }
    }

    List<SubmissionSample> samples =
        samplesParameters.stream().filter(s -> s instanceof SubmissionSample)
            .map(s -> (SubmissionSample) s).collect(Collectors.toList());
    samplesContainer.removeAllItems();
    samplesContainer.addAll(samples);
    samples.forEach(s -> view.samplesGrid.select(s));
  }

  public static Object[] getSamplesColumns() {
    return SAMPLES_COLUMNS.clone();
  }
}
