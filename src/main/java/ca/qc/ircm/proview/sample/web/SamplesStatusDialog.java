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

import static ca.qc.ircm.proview.Constants.ALL;
import static ca.qc.ircm.proview.Constants.CANCEL;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.sample.SampleProperties.NAME;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.text.NormalizedComparator;
import ca.qc.ircm.proview.web.SavedEvent;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
public class SamplesStatusDialog extends Dialog
    implements LocaleChangeObserver, NotificationComponent {
  private static final long serialVersionUID = -5878136560444849327L;
  public static final String ID = "samples-status-dialog";
  public static final String HEADER = "header";
  public static final String SAVED = "saved";
  private static final Logger logger = LoggerFactory.getLogger(SamplesStatusDialog.class);
  protected Grid<SubmissionSample> samples = new Grid<>();
  protected Column<SubmissionSample> name;
  protected Column<SubmissionSample> status;
  protected ComboBox<SampleStatus> allStatus = new ComboBox<>();
  protected Button save = new Button();
  protected Button cancel = new Button();
  private Map<SubmissionSample, ComboBox<SampleStatus>> statusFields = new HashMap<>();
  private Submission submission;
  private Map<SubmissionSample, Binder<SubmissionSample>> binders = new HashMap<>();
  private transient SubmissionService service;
  private transient SubmissionSampleService sampleService;

  public SamplesStatusDialog() {
  }

  @Autowired
  SamplesStatusDialog(SubmissionService service, SubmissionSampleService sampleService) {
    this.service = service;
    this.sampleService = sampleService;
  }

  public static String id(String baseId) {
    return styleName(ID, baseId);
  }

  @PostConstruct
  void init() {
    logger.debug("samples status dialog");
    setId(ID);
    setWidth("700px");
    setResizable(true);
    VerticalLayout layout = new VerticalLayout();
    add(layout);
    layout.add(samples);
    layout.setSizeFull();
    layout.expand(samples);
    getFooter().add(cancel, save);
    samples.setId(id(SAMPLES));
    ValueProvider<SubmissionSample, String> sampleName =
        sample -> Objects.toString(sample.getName(), "");
    name = samples.addColumn(sampleName, NAME).setKey(NAME)
        .setComparator(NormalizedComparator.of(Sample::getName)).setFlexGrow(2);
    status = samples.addColumn(new ComponentRenderer<>(sample -> status(sample))).setKey(STATUS)
        .setSortable(false);
    samples.appendHeaderRow(); // Headers.
    HeaderRow allRow = samples.appendHeaderRow();
    allRow.getCell(status).setComponent(allStatus);
    allStatus.setId(id(styleName(STATUS, ALL)));
    allStatus.setClearButtonVisible(true);
    allStatus.setItems(SampleStatus.values());
    allStatus.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    allStatus.addValueChangeListener(
        e -> Optional.ofNullable(e.getValue()).ifPresent(status -> submission.getSamples().stream()
            .forEach(sample -> status(sample).setValue(status))));
    addOpenedChangeListener(e -> allStatus.clear());
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    save.setId(id(SAVE));
    save.setIcon(VaadinIcon.CHECK.create());
    save.addClickListener(e -> save());
    cancel.setId(id(CANCEL));
    cancel.setIcon(VaadinIcon.CLOSE.create());
    cancel.addClickListener(e -> close());
  }

  ComboBox<SampleStatus> status(SubmissionSample sample) {
    if (statusFields.containsKey(sample)) {
      return statusFields.get(sample);
    }
    ComboBox<SampleStatus> status = new ComboBox<>();
    status.addClassName(STATUS);
    status.setItems(SampleStatus.values());
    status.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    statusFields.put(sample, status);
    return status;
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    localeChanged();
  }

  private void localeChanged() {
    final AppResources resources = new AppResources(SamplesStatusDialog.class, getLocale());
    final AppResources webResources = new AppResources(Constants.class, getLocale());
    final AppResources sampleResources = new AppResources(Sample.class, getLocale());
    final AppResources submissionSampleResources =
        new AppResources(SubmissionSample.class, getLocale());
    String nameHeader = sampleResources.message(NAME);
    name.setHeader(nameHeader).setFooter(nameHeader);
    String statusHeader = submissionSampleResources.message(STATUS);
    status.setHeader(statusHeader).setFooter(statusHeader);
    allStatus.setLabel(resources.message(property(STATUS, ALL)));
    save.setText(webResources.message(SAVE));
    cancel.setText(webResources.message(CANCEL));
    setHeaderTitle(resources.message(HEADER));
    if (submission != null) {
      for (SubmissionSample sample : submission.getSamples()) {
        Binder<SubmissionSample> binder = new BeanValidationBinder<>(SubmissionSample.class);
        binder.forField(status(sample)).asRequired(webResources.message(REQUIRED)).bind(STATUS);
        binder.setBean(sample);
        binders.put(sample, binder);
      }
      if (submission.getId() != null) {
        setHeaderTitle(resources.message(HEADER, submission.getExperiment()));
      }
    }
  }

  /**
   * Adds listener to be informed when a submission was saved.
   *
   * @param listener
   *          listener
   * @return listener registration
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Registration
      addSavedListener(ComponentEventListener<SavedEvent<SamplesStatusDialog>> listener) {
    return addListener((Class) SavedEvent.class, listener);
  }

  void fireSavedEvent() {
    fireEvent(new SavedEvent<>(this, true));
  }

  public Long getSubmissionId() {
    return submission.getId();
  }

  public void setSubmissionId(Long id) {
    submission = service.get(id).orElseThrow();
    samples.setItems(submission.getSamples());
    localeChanged();
  }

  List<BinderValidationStatus<SubmissionSample>> validateSamples() {
    return submission.getSamples().stream().map(sample -> binders.get(sample).validate())
        .collect(Collectors.toList());
  }

  private boolean validate() {
    return submission != null && !submission.getSamples().isEmpty()
        && !validateSamples().stream().filter(status -> !status.isOk()).findAny().isPresent();
  }

  private void save() {
    if (validate()) {
      logger.debug("update samples' status of submission {}", submission);
      AppResources resources = new AppResources(SamplesStatusDialog.class, getLocale());
      sampleService.updateStatus(submission.getSamples());
      showNotification(resources.message(SAVED, submission.getExperiment()));
      close();
      fireSavedEvent();
    }
  }
}
