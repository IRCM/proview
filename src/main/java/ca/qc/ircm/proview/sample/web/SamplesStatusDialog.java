package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.Constants.ALL;
import static ca.qc.ircm.proview.Constants.CANCEL;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.sample.SampleProperties.NAME;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.text.Strings.normalizedCollator;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
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
import java.io.Serial;
import java.util.Comparator;
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
public class SamplesStatusDialog extends Dialog implements LocaleChangeObserver,
    NotificationComponent {

  public static final String ID = "samples-status-dialog";
  public static final String HEADER = "header";
  public static final String SAVED = "saved";
  @Serial
  private static final long serialVersionUID = -5878136560444849327L;
  private static final String MESSAGES_PREFIX = messagePrefix(SamplesStatusDialog.class);
  private static final String SAMPLE_PREFIX = messagePrefix(Sample.class);
  private static final String SUBMISSION_SAMPLE_PREFIX = messagePrefix(SubmissionSample.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String SAMPLE_STATUS_PREFIX = messagePrefix(SampleStatus.class);
  private static final Logger logger = LoggerFactory.getLogger(SamplesStatusDialog.class);
  protected Grid<SubmissionSample> samples = new Grid<>();
  protected Column<SubmissionSample> name;
  protected Column<SubmissionSample> status;
  protected ComboBox<SampleStatus> allStatus = new ComboBox<>();
  protected Button save = new Button();
  protected Button cancel = new Button();
  private final Map<SubmissionSample, ComboBox<SampleStatus>> statusFields = new HashMap<>();
  private Submission submission;
  private final Map<SubmissionSample, Binder<SubmissionSample>> binders = new HashMap<>();
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
    ValueProvider<SubmissionSample, String> sampleName = sample -> Objects.toString(
        sample.getName(), "");
    name = samples.addColumn(sampleName, NAME).setKey(NAME)
        .setComparator(Comparator.comparing(Sample::getName, normalizedCollator())).setFlexGrow(2);
    status = samples.addColumn(new ComponentRenderer<>(this::status)).setKey(STATUS)
        .setSortable(false);
    samples.appendHeaderRow(); // Headers.
    HeaderRow allRow = samples.appendHeaderRow();
    allRow.getCell(status).setComponent(allStatus);
    allStatus.setId(id(styleName(STATUS, ALL)));
    allStatus.setClearButtonVisible(true);
    allStatus.setItems(SampleStatus.values());
    allStatus.setItemLabelGenerator(value -> getTranslation(SAMPLE_STATUS_PREFIX + value.name()));
    allStatus.addValueChangeListener(e -> Optional.ofNullable(e.getValue()).ifPresent(
        status -> submission.getSamples().forEach(sample -> status(sample).setValue(status))));
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
    status.setItemLabelGenerator(value -> getTranslation(SAMPLE_STATUS_PREFIX + value.name()));
    statusFields.put(sample, status);
    return status;
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    localeChanged();
  }

  private void localeChanged() {
    String nameHeader = getTranslation(SAMPLE_PREFIX + NAME);
    name.setHeader(nameHeader).setFooter(nameHeader);
    String statusHeader = getTranslation(SUBMISSION_SAMPLE_PREFIX + STATUS);
    status.setHeader(statusHeader).setFooter(statusHeader);
    allStatus.setLabel(getTranslation(MESSAGES_PREFIX + property(STATUS, ALL)));
    save.setText(getTranslation(CONSTANTS_PREFIX + SAVE));
    cancel.setText(getTranslation(CONSTANTS_PREFIX + CANCEL));
    setHeaderTitle(getTranslation(MESSAGES_PREFIX + HEADER));
    if (submission != null) {
      for (SubmissionSample sample : submission.getSamples()) {
        Binder<SubmissionSample> binder = new BeanValidationBinder<>(SubmissionSample.class);
        binder.forField(status(sample)).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
            .bind(STATUS);
        binder.setBean(sample);
        binders.put(sample, binder);
      }
      if (submission.getId() != 0) {
        setHeaderTitle(getTranslation(MESSAGES_PREFIX + HEADER, submission.getExperiment()));
      }
    }
  }

  /**
   * Adds listener to be informed when a submission was saved.
   *
   * @param listener listener
   * @return listener registration
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Registration addSavedListener(
      ComponentEventListener<SavedEvent<SamplesStatusDialog>> listener) {
    return addListener((Class) SavedEvent.class, listener);
  }

  void fireSavedEvent() {
    fireEvent(new SavedEvent<>(this, true));
  }

  public long getSubmissionId() {
    return submission.getId();
  }

  public void setSubmissionId(long id) {
    submission = service.get(id).orElseThrow();
    samples.setItems(submission.getSamples());
    localeChanged();
  }

  List<BinderValidationStatus<SubmissionSample>> validateSamples() {
    return submission.getSamples().stream().map(sample -> binders.get(sample).validate())
        .collect(Collectors.toList());
  }

  private boolean validate() {
    return submission != null && !submission.getSamples().isEmpty() && validateSamples().stream()
        .allMatch(BinderValidationStatus::isOk);
  }

  private void save() {
    if (validate()) {
      logger.debug("update samples' status of submission {}", submission);
      sampleService.updateStatus(submission.getSamples());
      showNotification(getTranslation(MESSAGES_PREFIX + SAVED, submission.getExperiment()));
      close();
      fireSavedEvent();
    }
  }
}
