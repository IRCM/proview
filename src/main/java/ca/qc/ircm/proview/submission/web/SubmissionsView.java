package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.ANALYSIS_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIDDEN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MASS_DETECTION_INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLE_DELIVERY_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SUBMISSION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USER;
import static ca.qc.ircm.proview.text.Strings.normalize;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.user.LaboratoryProperties.DIRECTOR;
import static ca.qc.ircm.proview.web.WebConstants.ALL;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Submissions view.
 */
@Tag("submissions-view")
@Route(value = SubmissionsView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ UserRole.USER })
public class SubmissionsView extends VerticalLayout
    implements HasDynamicTitle, LocaleChangeObserver {
  public static final String VIEW_NAME = "submissions";
  public static final String HEADER = "header";
  public static final String SUBMISSIONS = "submissions";
  public static final String SAMPLES_COUNT = SAMPLES + "Count";
  public static final String SAMPLES_VALUE = property(SAMPLES, "value");
  public static final String STATUS_VALUE = property(STATUS, "value");
  public static final String ADD = "add";
  private static final long serialVersionUID = 4399000178746918928L;
  protected H2 header = new H2();
  protected Grid<Submission> submissions = new Grid<>();
  protected Column<Submission> experiment;
  protected Column<Submission> user;
  protected Column<Submission> director;
  protected Column<Submission> sampleDeliveryDate;
  protected Column<Submission> digestionDate;
  protected Column<Submission> analysisDate;
  protected Column<Submission> dataAvailableDate;
  protected Column<Submission> date;
  protected Column<Submission> instrument;
  protected Column<Submission> service;
  protected Column<Submission> samplesCount;
  protected Column<Submission> samples;
  protected Column<Submission> status;
  protected Column<Submission> hidden;
  protected TextField experimentFilter = new TextField();
  protected TextField userFilter = new TextField();
  protected TextField directorFilter = new TextField();
  protected ComboBox<MassDetectionInstrument> instrumentFilter = new ComboBox<>();
  protected ComboBox<Service> serviceFilter = new ComboBox<>();
  protected TextField samplesFilter = new TextField();
  protected ComboBox<SampleStatus> statusFilter = new ComboBox<>();
  protected ComboBox<Boolean> hiddenFilter = new ComboBox<>();
  protected Button add = new Button();
  private SubmissionsViewPresenter presenter;

  @Autowired
  protected SubmissionsView(SubmissionsViewPresenter presenter) {
    this.presenter = presenter;
  }

  @PostConstruct
  void init() {
    setId(VIEW_NAME);
    add(header, submissions);
    header.setId(HEADER);
    submissions.setId(SUBMISSIONS);
    submissions.addItemDoubleClickListener(e -> presenter.view(e.getItem()));
    ValueProvider<Submission, String> submissionExperiment =
        submission -> Objects.toString(submission.getExperiment(), "");
    experiment = submissions.addColumn(submissionExperiment, EXPERIMENT).setKey(EXPERIMENT)
        .setComparator((s1, s2) -> normalize(submissionExperiment.apply(s1))
            .compareToIgnoreCase(normalize(submissionExperiment.apply(s2))));
    ValueProvider<Submission, String> submissionUser = submission -> submission.getUser() != null
        ? Objects.toString(submission.getUser().getName())
        : "";
    user = submissions.addColumn(submissionUser, USER).setKey(USER)
        .setComparator((s1, s2) -> normalize(submissionUser.apply(s1))
            .compareToIgnoreCase(normalize(submissionUser.apply(s2))));
    ValueProvider<Submission, String> submissionDirector =
        submission -> Objects.toString(submission.getLaboratory().getDirector(), "");
    director = submissions.addColumn(submissionDirector, DIRECTOR).setKey(DIRECTOR)
        .setComparator((s1, s2) -> normalize(submissionDirector.apply(s1))
            .compareToIgnoreCase(normalize(submissionDirector.apply(s2))));
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    sampleDeliveryDate =
        submissions.addColumn(submission -> submission.getSampleDeliveryDate() != null
            ? dateFormatter.format(submission.getSampleDeliveryDate())
            : "", SAMPLE_DELIVERY_DATE).setKey(SAMPLE_DELIVERY_DATE);
    digestionDate = submissions.addColumn(submission -> submission.getDigestionDate() != null
        ? dateFormatter.format(submission.getDigestionDate())
        : "", DIGESTION_DATE).setKey(DIGESTION_DATE);
    analysisDate = submissions.addColumn(submission -> submission.getAnalysisDate() != null
        ? dateFormatter.format(submission.getAnalysisDate())
        : "", ANALYSIS_DATE).setKey(ANALYSIS_DATE);
    dataAvailableDate =
        submissions.addColumn(submission -> submission.getDataAvailableDate() != null
            ? dateFormatter.format(submission.getDataAvailableDate())
            : "", DATA_AVAILABLE_DATE).setKey(DATA_AVAILABLE_DATE);
    date = submissions.addColumn(submission -> submission.getSubmissionDate().toLocalDate() != null
        ? dateFormatter.format(submission.getSubmissionDate().toLocalDate())
        : "", SUBMISSION_DATE).setKey(SUBMISSION_DATE);
    instrument = submissions
        .addColumn(submission -> submission.getMassDetectionInstrument() != null
            ? submission.getMassDetectionInstrument().getLabel(getLocale())
            : MassDetectionInstrument.getNullLabel(getLocale()), MASS_DETECTION_INSTRUMENT)
        .setKey(MASS_DETECTION_INSTRUMENT);
    service = submissions.addColumn(submission -> submission.getService() != null
        ? submission.getService().getLabel(getLocale())
        : Service.getNullLabel(getLocale()), SERVICE).setKey(SERVICE);
    samplesCount =
        submissions.addColumn(submission -> submission.getSamples().size(), SAMPLES_COUNT)
            .setKey(SAMPLES_COUNT);
    samples =
        submissions.addColumn(new ComponentRenderer<>(submission -> samples(submission)), SAMPLES)
            .setKey(SAMPLES).setSortable(false);
    status =
        submissions.addColumn(new ComponentRenderer<>(submission -> status(submission)), STATUS)
            .setKey(STATUS).setSortable(false);
    hidden =
        submissions.addColumn(new ComponentRenderer<>(submission -> hidden(submission)), HIDDEN)
            .setKey(HIDDEN);
    submissions.appendHeaderRow(); // Headers.
    HeaderRow filtersRow = submissions.appendHeaderRow();
    filtersRow.getCell(experiment).setComponent(experimentFilter);
    experimentFilter.addValueChangeListener(e -> presenter.filterExperiment(e.getValue()));
    experimentFilter.setValueChangeMode(ValueChangeMode.EAGER);
    experimentFilter.setSizeFull();
    filtersRow.getCell(user).setComponent(userFilter);
    userFilter.addValueChangeListener(e -> presenter.filterUser(e.getValue()));
    userFilter.setValueChangeMode(ValueChangeMode.EAGER);
    userFilter.setSizeFull();
    filtersRow.getCell(director).setComponent(directorFilter);
    directorFilter.addValueChangeListener(e -> presenter.filterDirector(e.getValue()));
    directorFilter.setValueChangeMode(ValueChangeMode.EAGER);
    directorFilter.setSizeFull();
    filtersRow.getCell(instrument).setComponent(instrumentFilter);
    instrumentFilter.setItems(MassDetectionInstrument.values());
    instrumentFilter.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    instrumentFilter.setClearButtonVisible(true);
    instrumentFilter.addValueChangeListener(e -> presenter.filterInstrument(e.getValue()));
    instrumentFilter.setSizeFull();
    filtersRow.getCell(service).setComponent(serviceFilter);
    serviceFilter.setItems(Service.values());
    serviceFilter.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    serviceFilter.setClearButtonVisible(true);
    serviceFilter.addValueChangeListener(e -> presenter.filterService(e.getValue()));
    serviceFilter.setSizeFull();
    filtersRow.getCell(samples).setComponent(samplesFilter);
    samplesFilter.addValueChangeListener(e -> presenter.filterSamples(e.getValue()));
    samplesFilter.setValueChangeMode(ValueChangeMode.EAGER);
    samplesFilter.setSizeFull();
    filtersRow.getCell(status).setComponent(statusFilter);
    statusFilter.setItems(SampleStatus.values());
    statusFilter.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    statusFilter.setClearButtonVisible(true);
    statusFilter.addValueChangeListener(e -> presenter.filterStatus(e.getValue()));
    statusFilter.setSizeFull();
    filtersRow.getCell(hidden).setComponent(hiddenFilter);
    hiddenFilter.setItems(false, true);
    hiddenFilter.setItemLabelGenerator(value -> new MessageResource(Submission.class, getLocale())
        .message(property(HIDDEN, value)));
    hiddenFilter.setClearButtonVisible(true);
    hiddenFilter.addValueChangeListener(e -> presenter.filterHidden(e.getValue()));
    hiddenFilter.setSizeFull();
    add.setId(ADD);
    add.setIcon(VaadinIcon.PLUS.create());
    add.addClickListener(e -> presenter.add());
    presenter.init(this);
  }

  private Span samples(Submission submission) {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    Span span = new Span();
    span.setText(resources.message(SAMPLES_VALUE, submission.getSamples().get(0).getName(),
        submission.getSamples().size()));
    span.setTitle(submission.getSamples().stream().map(SubmissionSample::getName)
        .collect(Collectors.joining("\n")));
    return span;
  }

  private Span status(Submission submission) {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    List<SampleStatus> statuses = submission.getSamples().stream().map(sample -> sample.getStatus())
        .distinct().collect(Collectors.toList());
    Span span = new Span();
    span.setText(
        resources.message(STATUS_VALUE, statuses.get(0).getLabel(getLocale()), statuses.size()));
    span.setTitle(statuses.stream().map(status -> status.getLabel(getLocale()))
        .collect(Collectors.joining("\n")));
    return span;
  }

  private Button hidden(Submission submission) {
    Button button = new Button();
    button.addClassName(HIDDEN);
    updateHiddenButton(button, submission);
    button.addClickListener(e -> {
      presenter.toggleHidden(submission);
      updateHiddenButton(button, submission);
    });
    return button;
  }

  private void updateHiddenButton(Button button, Submission submission) {
    final MessageResource resources = new MessageResource(Submission.class, getLocale());
    button.setText(resources.message(property(HIDDEN, submission.isHidden())));
    button.setIcon(submission.isHidden() ? VaadinIcon.EYE_SLASH.create() : VaadinIcon.EYE.create());
    button.addThemeName(submission.isHidden() ? WebConstants.ERROR : WebConstants.SUCCESS);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    final MessageResource submissionResources = new MessageResource(Submission.class, getLocale());
    final MessageResource laboratoryResources = new MessageResource(Laboratory.class, getLocale());
    final MessageResource submissionSampleResources =
        new MessageResource(SubmissionSample.class, getLocale());
    header.setText(resources.message(HEADER));
    String experimentHeader = submissionResources.message(EXPERIMENT);
    experiment.setHeader(experimentHeader).setFooter(experimentHeader);
    String userHeader = submissionResources.message(USER);
    user.setHeader(userHeader).setFooter(userHeader);
    String directorHeader = laboratoryResources.message(DIRECTOR);
    director.setHeader(directorHeader).setFooter(directorHeader);
    String sampleDeliveryDateHeader = submissionResources.message(SAMPLE_DELIVERY_DATE);
    sampleDeliveryDate.setHeader(sampleDeliveryDateHeader).setFooter(sampleDeliveryDateHeader);
    String digestionDateHeader = submissionResources.message(DIGESTION_DATE);
    digestionDate.setHeader(digestionDateHeader).setFooter(digestionDateHeader);
    String analysisDateHeader = submissionResources.message(ANALYSIS_DATE);
    analysisDate.setHeader(analysisDateHeader).setFooter(analysisDateHeader);
    String dataAvailableDateHeader = submissionResources.message(DATA_AVAILABLE_DATE);
    dataAvailableDate.setHeader(dataAvailableDateHeader).setFooter(dataAvailableDateHeader);
    String dateHeader = submissionResources.message(SUBMISSION_DATE);
    date.setHeader(dateHeader).setFooter(dateHeader);
    String instrumentHeader = submissionResources.message(MASS_DETECTION_INSTRUMENT);
    instrument.setHeader(instrumentHeader).setFooter(instrumentHeader);
    String serviceHeader = submissionResources.message(SERVICE);
    service.setHeader(serviceHeader).setFooter(serviceHeader);
    String samplesCountHeader = resources.message(SAMPLES_COUNT);
    samplesCount.setHeader(samplesCountHeader).setFooter(samplesCountHeader);
    String samplesHeader = submissionResources.message(SAMPLES);
    samples.setHeader(samplesHeader).setFooter(samplesHeader);
    String statusHeader = submissionSampleResources.message(STATUS);
    status.setHeader(statusHeader).setFooter(statusHeader);
    String hiddenHeader = submissionResources.message(HIDDEN);
    hidden.setHeader(hiddenHeader).setFooter(hiddenHeader);
    experimentFilter.setPlaceholder(resources.message(ALL));
    userFilter.setPlaceholder(resources.message(ALL));
    directorFilter.setPlaceholder(resources.message(ALL));
    instrumentFilter.setPlaceholder(resources.message(ALL));
    samplesFilter.setPlaceholder(resources.message(ALL));
    statusFilter.setPlaceholder(resources.message(ALL));
    hiddenFilter.setPlaceholder(resources.message(ALL));
    add.setText(resources.message(ADD));
    submissions.getDataProvider().refreshAll();
    instrumentFilter.getDataProvider().refreshAll();
    statusFilter.getDataProvider().refreshAll();
    hiddenFilter.getDataProvider().refreshAll();
  }

  @Override
  public String getPageTitle() {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    final MessageResource generalResources = new MessageResource(WebConstants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }
}
