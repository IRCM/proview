package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.ALL;
import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.VIEW;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.SpotbugsJustifications.INNER_CLASS_EI_EXPOSE_REP;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIDDEN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SUBMISSION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USER;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.user.LaboratoryProperties.DIRECTOR;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.persistence.QueryDsl;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.web.SamplesStatusDialog;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFilter;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.text.NormalizedComparator;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.UserPreferenceService;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.DateRangeField;
import ca.qc.ircm.proview.web.ErrorNotification;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.google.common.collect.Range;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.customfield.CustomFieldVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Submissions view.
 */
@Route(value = SubmissionsView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ UserRole.USER })
public class SubmissionsView extends VerticalLayout
    implements HasDynamicTitle, LocaleChangeObserver, NotificationComponent {
  public static final String VIEW_NAME = "submissions";
  public static final String ID = "submissions-view";
  public static final String SUBMISSIONS = "submissions";
  public static final String SAMPLES_COUNT = SAMPLES + "Count";
  public static final String SAMPLES_VALUE = property(SAMPLES, "value");
  public static final String STATUS_VALUE = property(STATUS, "value");
  public static final String EDIT_STATUS = "editStatus";
  public static final String HISTORY = "history";
  public static final String ADD = "add";
  public static final String HIDE_COLUMNS = "hideColumns";
  public static final String SAMPLES_SPAN =
      "<span .title='${item.samplesTitle}'>${item.samplesValue}</span>";
  public static final String STATUS_SPAN =
      "<span .title='${item.statusTitle}'>${item.statusValue}</span>";
  public static final String HIDDEN_BUTTON =
      "<vaadin-button class='" + HIDDEN + "' .theme='${item.hiddenTheme}' @click='${toggleHidden}'>"
          + "<vaadin-icon .icon='${item.hiddenIcon}' slot='prefix'></vaadin-icon>"
          + "${item.hiddenValue}" + "</vaadin-button>";
  private static final String MESSAGES_PREFIX = messagePrefix(SubmissionsView.class);
  private static final String SUBMISSION_PREFIX = messagePrefix(Submission.class);
  private static final String SUBMISSION_SAMPLE_PREFIX = messagePrefix(SubmissionSample.class);
  private static final String LABORATORY_PREFIX = messagePrefix(Laboratory.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String MASS_DETECTION_INSTRUMENT_PREFIX =
      messagePrefix(MassDetectionInstrument.class);
  private static final String SAMPLE_STATUS_PREFIX = messagePrefix(SampleStatus.class);
  private static final String SERVICE_PREFIX = messagePrefix(Service.class);
  private static final long serialVersionUID = 4399000178746918928L;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionsView.class);
  protected Grid<Submission> submissions = new Grid<>();
  protected Column<Submission> experiment;
  protected Column<Submission> user;
  protected Column<Submission> director;
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
  protected DateRangeField dataAvailableDateFilter = new DateRangeField();
  protected DateRangeField dateFilter = new DateRangeField();
  protected ComboBox<MassDetectionInstrument> instrumentFilter = new ComboBox<>();
  protected ComboBox<Service> serviceFilter = new ComboBox<>();
  protected TextField samplesFilter = new TextField();
  protected ComboBox<SampleStatus> statusFilter = new ComboBox<>();
  protected ComboBox<Boolean> hiddenFilter = new ComboBox<>();
  protected Button add = new Button();
  protected Button view = new Button();
  protected Button editStatus = new Button();
  protected Button history = new Button();
  protected Button hideColumns = new Button();
  protected ColumnToggleContextMenu hideColumnsContextMenu;
  private Map<String, ComparableExpressionBase<?>> columnProperties = new HashMap<>();
  private List<Grid.Column> hidableColumns = new ArrayList<>();
  private transient ObjectFactory<SubmissionDialog> dialogFactory;
  private transient ObjectFactory<SamplesStatusDialog> statusDialogFactory;
  private transient SubmissionFilter filter = new SubmissionFilter();
  private transient SubmissionService submissionService;
  private transient AuthenticatedUser authenticatedUser;
  private transient UserPreferenceService userPreferenceService;

  @Autowired
  protected SubmissionsView(ObjectFactory<SubmissionDialog> dialogFactory,
      ObjectFactory<SamplesStatusDialog> statusDialogFactory, SubmissionService submissionService,
      AuthenticatedUser authenticatedUser, UserPreferenceService userPreferenceService) {
    this.dialogFactory = dialogFactory;
    this.statusDialogFactory = statusDialogFactory;
    this.submissionService = submissionService;
    this.authenticatedUser = authenticatedUser;
    this.userPreferenceService = userPreferenceService;
  }

  @PostConstruct
  void init() {
    logger.debug("submissions view");
    setId(ID);
    setHeightFull();
    VerticalLayout submissionsLayout = new VerticalLayout();
    submissionsLayout.setWidthFull();
    submissionsLayout.setPadding(false);
    submissionsLayout.setSpacing(false);
    submissionsLayout.add(new HorizontalLayout(add, hideColumns), submissions);
    submissionsLayout.expand(submissions);
    HorizontalLayout buttonsLayout = new HorizontalLayout();
    buttonsLayout.add(view, editStatus, history);
    add(submissionsLayout, buttonsLayout);
    expand(submissionsLayout);

    columnProperties.put(EXPERIMENT, submission.experiment);
    columnProperties.put(USER, submission.user.name);
    columnProperties.put(DIRECTOR, submission.laboratory.director);
    columnProperties.put(DATA_AVAILABLE_DATE, submission.dataAvailableDate);
    columnProperties.put(SERVICE, submission.service);
    columnProperties.put(INSTRUMENT, submission.instrument);
    columnProperties.put(SAMPLES_COUNT, submission.samples.size());
    columnProperties.put(SUBMISSION_DATE, submission.submissionDate);
    columnProperties.put(HIDDEN, submission.hidden);

    submissions.setId(SUBMISSIONS);
    submissions.setMinHeight("500px");
    submissions.addItemDoubleClickListener(e -> view(e.getItem()));
    submissions.addItemClickListener(e -> {
      if (e.isShiftKey() || e.isCtrlKey() || e.isMetaKey()) {
        editStatus(e.getItem());
      } else if (e.isAltKey()) {
        history(e.getItem());
      }
    });
    submissions.addSelectionListener(e -> {
      view.setEnabled(e.getAllSelectedItems().size() == 1);
      editStatus.setEnabled(e.getAllSelectedItems().size() == 1);
      history.setEnabled(e.getAllSelectedItems().size() == 1);
    });
    Function<Column<Submission>, Boolean> columnVisibility = column -> {
      Optional<Boolean> value = userPreferenceService.get(this, column.getKey());
      return value.orElse(true);
    };
    ValueProvider<Submission, String> submissionExperiment =
        submission -> Objects.toString(submission.getExperiment(), "");
    experiment = submissions.addColumn(submissionExperiment, EXPERIMENT).setKey(EXPERIMENT)
        .setComparator(NormalizedComparator.of(Submission::getExperiment)).setFlexGrow(3);
    ValueProvider<Submission, String> submissionUser = submission -> submission.getUser().getName();
    user = submissions.addColumn(submissionUser, USER).setKey(USER)
        .setComparator(NormalizedComparator.of(s -> s.getUser().getName())).setFlexGrow(3);
    user.setVisible(authenticatedUser.hasAnyRole(MANAGER, ADMIN) && columnVisibility.apply(user));
    ValueProvider<Submission, String> submissionDirector =
        submission -> Objects.toString(submission.getLaboratory().getDirector(), "");
    director = submissions.addColumn(submissionDirector, DIRECTOR).setKey(DIRECTOR)
        .setComparator(NormalizedComparator.of(s -> s.getLaboratory().getDirector()))
        .setFlexGrow(3);
    director.setVisible(authenticatedUser.hasRole(ADMIN) && columnVisibility.apply(director));
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    dataAvailableDate =
        submissions.addColumn(submission -> submission.getDataAvailableDate() != null
            ? dateFormatter.format(submission.getDataAvailableDate())
            : "", DATA_AVAILABLE_DATE).setKey(DATA_AVAILABLE_DATE).setFlexGrow(2);
    dataAvailableDate.setVisible(columnVisibility.apply(dataAvailableDate));
    date = submissions.addColumn(submission -> submission.getSubmissionDate().toLocalDate() != null
        ? dateFormatter.format(submission.getSubmissionDate().toLocalDate())
        : "", SUBMISSION_DATE).setKey(SUBMISSION_DATE).setFlexGrow(2);
    date.setVisible(columnVisibility.apply(date));
    instrument = submissions.addColumn(
        submission -> getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + Optional
            .ofNullable(submission.getInstrument()).orElse(MassDetectionInstrument.NULL).name()),
        INSTRUMENT).setKey(INSTRUMENT).setFlexGrow(2);
    instrument.setVisible(authenticatedUser.hasRole(ADMIN) && columnVisibility.apply(instrument));
    service = submissions
        .addColumn(submission -> getTranslation(SERVICE_PREFIX + submission.getService().name()),
            SERVICE)
        .setKey(SERVICE).setFlexGrow(2);
    service.setVisible(authenticatedUser.hasRole(ADMIN) && columnVisibility.apply(service));
    samplesCount =
        submissions.addColumn(submission -> submission.getSamples().size(), SAMPLES_COUNT)
            .setKey(SAMPLES_COUNT).setFlexGrow(0);
    samplesCount.setVisible(columnVisibility.apply(samplesCount));
    samples = submissions
        .addColumn(LitRenderer.<Submission>of(SAMPLES_SPAN)
            .withProperty("samplesValue", submission -> sampleNamesValue(submission))
            .withProperty("samplesTitle", submission -> sampleNamesTitle(submission)))
        .setKey(SAMPLES).setSortable(false).setFlexGrow(3);
    samples.setVisible(columnVisibility.apply(samples));
    status = submissions
        .addColumn(LitRenderer.<Submission>of(STATUS_SPAN)
            .withProperty("statusValue", submission -> statusesValue(submission))
            .withProperty("statusTitle", submission -> statusesTitle(submission)))
        .setKey(STATUS).setSortable(false).setFlexGrow(2);
    status.setVisible(columnVisibility.apply(status));
    hidden = submissions.addColumn(LitRenderer.<Submission>of(HIDDEN_BUTTON)
        .withProperty("hiddenTheme", submission -> hiddenTheme(submission))
        .withProperty("hiddenValue", submission -> hiddenValue(submission))
        .withProperty("hiddenIcon", submission -> hiddenIcon(submission))
        .withFunction("toggleHidden", submission -> {
          toggleHidden(submission);
          submissions.getDataProvider().refreshItem(submission);
        })).setKey(HIDDEN).setSortProperty(HIDDEN)
        .setComparator((s1, s2) -> Boolean.compare(s1.isHidden(), s2.isHidden()));
    hidden.setVisible(authenticatedUser.hasRole(ADMIN) && columnVisibility.apply(hidden));
    submissions.appendHeaderRow(); // Headers.
    HeaderRow filtersRow = submissions.appendHeaderRow();
    filtersRow.getCell(experiment).setComponent(experimentFilter);
    experimentFilter.addValueChangeListener(e -> filterExperiment(e.getValue()));
    experimentFilter.setValueChangeMode(ValueChangeMode.EAGER);
    experimentFilter.setSizeFull();
    filtersRow.getCell(user).setComponent(userFilter);
    userFilter.addValueChangeListener(e -> filterUser(e.getValue()));
    userFilter.setValueChangeMode(ValueChangeMode.EAGER);
    userFilter.setSizeFull();
    filtersRow.getCell(director).setComponent(directorFilter);
    directorFilter.addValueChangeListener(e -> filterDirector(e.getValue()));
    directorFilter.setValueChangeMode(ValueChangeMode.EAGER);
    directorFilter.setSizeFull();
    filtersRow.getCell(dataAvailableDate).setComponent(dataAvailableDateFilter);
    dataAvailableDateFilter.addValueChangeListener(e -> filterDataAvailableDate(e.getValue()));
    dataAvailableDateFilter.setSizeFull();
    dataAvailableDateFilter.addThemeVariants(CustomFieldVariant.LUMO_SMALL);
    filtersRow.getCell(date).setComponent(dateFilter);
    dateFilter.addValueChangeListener(e -> filterDate(e.getValue()));
    dateFilter.setSizeFull();
    dateFilter.addThemeVariants(CustomFieldVariant.LUMO_SMALL);
    filtersRow.getCell(instrument).setComponent(instrumentFilter);
    instrumentFilter.setItems(MassDetectionInstrument.values());
    instrumentFilter.setClearButtonVisible(true);
    instrumentFilter.addValueChangeListener(e -> filterInstrument(e.getValue()));
    instrumentFilter.setSizeFull();
    filtersRow.getCell(service).setComponent(serviceFilter);
    serviceFilter.setItems(Service.values());
    serviceFilter.setClearButtonVisible(true);
    serviceFilter.addValueChangeListener(e -> filterService(e.getValue()));
    serviceFilter.setSizeFull();
    filtersRow.getCell(samples).setComponent(samplesFilter);
    samplesFilter.addValueChangeListener(e -> filterSamples(e.getValue()));
    samplesFilter.setValueChangeMode(ValueChangeMode.EAGER);
    samplesFilter.setSizeFull();
    filtersRow.getCell(status).setComponent(statusFilter);
    statusFilter.setItems(SampleStatus.values());
    statusFilter.setClearButtonVisible(true);
    statusFilter.addValueChangeListener(e -> filterStatus(e.getValue()));
    statusFilter.setSizeFull();
    filtersRow.getCell(hidden).setComponent(hiddenFilter);
    hiddenFilter.setItems(false, true);
    hiddenFilter.setClearButtonVisible(true);
    hiddenFilter.addValueChangeListener(e -> filterHidden(e.getValue()));
    hiddenFilter.setSizeFull();

    add.setId(ADD);
    add.setIcon(VaadinIcon.PLUS.create());
    add.addClickListener(e -> add());
    view.setId(VIEW);
    view.setIcon(VaadinIcon.EYE.create());
    view.addClickListener(e -> view());
    view.setEnabled(false);
    editStatus.setId(EDIT_STATUS);
    editStatus.setIcon(VaadinIcon.EDIT.create());
    editStatus.addClickListener(e -> editStatus());
    editStatus.setVisible(authenticatedUser.hasRole(ADMIN));
    editStatus.setEnabled(false);
    history.setId(HISTORY);
    history.setIcon(VaadinIcon.ARCHIVE.create());
    history.addClickListener(e -> history());
    history.setVisible(authenticatedUser.hasRole(ADMIN));
    history.setEnabled(false);
    hideColumns.setId(HIDE_COLUMNS);
    hideColumns.setIcon(VaadinIcon.COG.create());
    hideColumnsContextMenu = new ColumnToggleContextMenu(hideColumns);

    hidableColumns.add(dataAvailableDate);
    hidableColumns.add(date);
    hidableColumns.add(samplesCount);
    hidableColumns.add(samples);
    hidableColumns.add(status);
    if (authenticatedUser.hasAnyRole(MANAGER, ADMIN)) {
      hidableColumns.add(user);
    }
    if (authenticatedUser.hasAnyRole(ADMIN)) {
      hidableColumns.add(director);
      hidableColumns.add(instrument);
      hidableColumns.add(service);
      hidableColumns.add(hidden);
    }
    hidableColumns.sort(
        (c1, c2) -> submissions.getColumns().indexOf(c1) - submissions.getColumns().indexOf(c2));
    loadSubmissions();
  }

  private void loadSubmissions() {
    Function<Query<Submission, Void>, List<OrderSpecifier<?>>> filterSortOrders =
        query -> query.getSortOrders() != null && !query.getSortOrders().isEmpty()
            ? query.getSortOrders().stream()
                .filter(order -> columnProperties.containsKey(order.getSorted()))
                .map(order -> QueryDsl.direction(columnProperties.get(order.getSorted()),
                    order.getDirection() == SortDirection.DESCENDING))
                .collect(Collectors.toList())
            : Arrays.asList(submission.id.desc());
    submissions.setItems(query -> {
      filter.sortOrders = filterSortOrders.apply(query);
      filter.offset = query.getOffset();
      filter.limit = query.getLimit();
      return submissionService.all(filter).stream();
    });
  }

  private String sampleNamesValue(Submission submission) {
    return getTranslation(MESSAGES_PREFIX + SAMPLES_VALUE, submission.getSamples().get(0).getName(),
        submission.getSamples().size());
  }

  private String sampleNamesTitle(Submission submission) {
    return submission.getSamples().stream().map(SubmissionSample::getName)
        .collect(Collectors.joining("\n"));
  }

  private String statusesValue(Submission submission) {
    List<SampleStatus> statuses = submission.getSamples().stream().map(sample -> sample.getStatus())
        .distinct().collect(Collectors.toList());
    return getTranslation(MESSAGES_PREFIX + STATUS_VALUE,
        getTranslation(SAMPLE_STATUS_PREFIX + statuses.get(0).name()), statuses.size());
  }

  private String statusesTitle(Submission submission) {
    List<SampleStatus> statuses = submission.getSamples().stream().map(sample -> sample.getStatus())
        .distinct().collect(Collectors.toList());
    return statuses.stream().map(status -> getTranslation(SAMPLE_STATUS_PREFIX + status.name()))
        .collect(Collectors.joining("\n"));
  }

  private String hiddenTheme(Submission submission) {
    return submission.isHidden() ? ButtonVariant.LUMO_ERROR.getVariantName()
        : ButtonVariant.LUMO_SUCCESS.getVariantName();
  }

  private String hiddenValue(Submission submission) {
    return getTranslation(SUBMISSION_PREFIX + property(HIDDEN, submission.isHidden()));
  }

  private String hiddenIcon(Submission submission) {
    return submission.isHidden() ? "vaadin:eye-slash" : "vaadin:eye";
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    String experimentHeader = getTranslation(SUBMISSION_PREFIX + EXPERIMENT);
    experiment.setHeader(experimentHeader).setFooter(experimentHeader);
    String userHeader = getTranslation(SUBMISSION_PREFIX + USER);
    user.setHeader(userHeader).setFooter(userHeader);
    String directorHeader = getTranslation(LABORATORY_PREFIX + DIRECTOR);
    director.setHeader(directorHeader).setFooter(directorHeader);
    String dataAvailableDateHeader = getTranslation(SUBMISSION_PREFIX + DATA_AVAILABLE_DATE);
    dataAvailableDate.setHeader(dataAvailableDateHeader).setFooter(dataAvailableDateHeader);
    String dateHeader = getTranslation(SUBMISSION_PREFIX + SUBMISSION_DATE);
    date.setHeader(dateHeader).setFooter(dateHeader);
    String instrumentHeader = getTranslation(SUBMISSION_PREFIX + INSTRUMENT);
    instrument.setHeader(instrumentHeader).setFooter(instrumentHeader);
    String serviceHeader = getTranslation(SUBMISSION_PREFIX + SERVICE);
    service.setHeader(serviceHeader).setFooter(serviceHeader);
    String samplesCountHeader = getTranslation(MESSAGES_PREFIX + SAMPLES_COUNT);
    samplesCount.setHeader(samplesCountHeader).setFooter(samplesCountHeader);
    String samplesHeader = getTranslation(SUBMISSION_PREFIX + SAMPLES);
    samples.setHeader(samplesHeader).setFooter(samplesHeader);
    String statusHeader = getTranslation(SUBMISSION_SAMPLE_PREFIX + STATUS);
    status.setHeader(statusHeader).setFooter(statusHeader);
    String hiddenHeader = getTranslation(SUBMISSION_PREFIX + HIDDEN);
    hidden.setHeader(hiddenHeader).setFooter(hiddenHeader);
    experimentFilter.setPlaceholder(getTranslation(MESSAGES_PREFIX + ALL));
    userFilter.setPlaceholder(getTranslation(MESSAGES_PREFIX + ALL));
    directorFilter.setPlaceholder(getTranslation(MESSAGES_PREFIX + ALL));
    instrumentFilter.setPlaceholder(getTranslation(MESSAGES_PREFIX + ALL));
    instrumentFilter.setItemLabelGenerator(
        value -> getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + value.name()));
    serviceFilter.setPlaceholder(getTranslation(MESSAGES_PREFIX + ALL));
    serviceFilter.setItemLabelGenerator(value -> getTranslation(SERVICE_PREFIX + value.name()));
    samplesFilter.setPlaceholder(getTranslation(MESSAGES_PREFIX + ALL));
    statusFilter.setPlaceholder(getTranslation(MESSAGES_PREFIX + ALL));
    statusFilter
        .setItemLabelGenerator(value -> getTranslation(SAMPLE_STATUS_PREFIX + value.name()));
    hiddenFilter.setPlaceholder(getTranslation(MESSAGES_PREFIX + ALL));
    hiddenFilter.setItemLabelGenerator(
        value -> getTranslation(SUBMISSION_PREFIX + property(HIDDEN, value)));
    add.setText(getTranslation(MESSAGES_PREFIX + ADD));
    view.setText(getTranslation(CONSTANTS_PREFIX + VIEW));
    editStatus.setText(getTranslation(MESSAGES_PREFIX + EDIT_STATUS));
    history.setText(getTranslation(MESSAGES_PREFIX + HISTORY));
    hideColumns.setText(getTranslation(MESSAGES_PREFIX + HIDE_COLUMNS));
    hideColumnsContextMenu.removeAll();
    hidableColumns.forEach(
        column -> hideColumnsContextMenu.addColumnToggleItem(getHeaderText(column), column));
    submissions.getDataProvider().refreshAll();
  }

  protected String getHeaderText(Column<Submission> column) {
    return submissions.getHeaderRows().get(0).getCell(column).getText();
  }

  @Override
  public String getPageTitle() {
    return getTranslation(MESSAGES_PREFIX + TITLE,
        getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME));
  }

  void filterExperiment(String value) {
    filter.experimentContains = value;
    submissions.getDataProvider().refreshAll();
  }

  void filterUser(String value) {
    filter.userContains = value;
    submissions.getDataProvider().refreshAll();
  }

  void filterDirector(String value) {
    filter.directorContains = value;
    submissions.getDataProvider().refreshAll();
  }

  void filterDataAvailableDate(Range<LocalDate> range) {
    filter.dataAvailableDateRange = range;
    submissions.getDataProvider().refreshAll();
  }

  void filterDate(Range<LocalDate> range) {
    filter.dateRange = range;
    submissions.getDataProvider().refreshAll();
  }

  void filterService(Service value) {
    filter.service = value;
    submissions.getDataProvider().refreshAll();
  }

  void filterInstrument(MassDetectionInstrument value) {
    filter.instrument = value;
    submissions.getDataProvider().refreshAll();
  }

  void filterSamples(String value) {
    filter.anySampleNameContains = value;
    submissions.getDataProvider().refreshAll();
  }

  void filterStatus(SampleStatus value) {
    filter.anySampleStatus = value;
    submissions.getDataProvider().refreshAll();
  }

  void filterHidden(Boolean value) {
    filter.hidden = value;
    submissions.getDataProvider().refreshAll();
  }

  void add() {
    UI.getCurrent().navigate(SubmissionView.class);
  }

  void view() {
    Optional<Submission> os = submissions.getSelectedItems().stream().findFirst();
    if (os.isPresent()) {
      view(os.get());
    } else {
      new ErrorNotification(getTranslation(MESSAGES_PREFIX + property(SUBMISSIONS, REQUIRED)))
          .open();
    }
  }

  void view(Submission submission) {
    SubmissionDialog dialog = dialogFactory.getObject();
    dialog.setSubmissionId(submission.getId());
    dialog.open();
    dialog.addSavedListener(e -> loadSubmissions());
  }

  void editStatus() {
    Optional<Submission> os = submissions.getSelectedItems().stream().findFirst();
    if (os.isPresent()) {
      editStatus(os.get());
    } else {
      new ErrorNotification(getTranslation(MESSAGES_PREFIX + property(SUBMISSIONS, REQUIRED)))
          .open();
    }
  }

  void editStatus(Submission submission) {
    if (authenticatedUser.hasRole(ADMIN)) {
      SamplesStatusDialog statusDialog = statusDialogFactory.getObject();
      statusDialog.setSubmissionId(submission.getId());
      statusDialog.open();
      statusDialog.addSavedListener(e -> loadSubmissions());
    }
  }

  void history() {
    Optional<Submission> os = submissions.getSelectedItems().stream().findFirst();
    if (os.isPresent()) {
      history(os.get());
    } else {
      new ErrorNotification(getTranslation(MESSAGES_PREFIX + property(SUBMISSIONS, REQUIRED)))
          .open();
    }
  }

  void history(Submission submission) {
    if (authenticatedUser.hasRole(ADMIN)) {
      UI.getCurrent().navigate(HistoryView.class, submission.getId());
    }
  }

  void toggleHidden(Submission submission) {
    submission.setHidden(!submission.isHidden());
    logger.debug("Change submission {} hidden to {}", submission, submission.isHidden());
    submissionService.update(submission, null);
    submissions.getDataProvider().refreshAll();
  }

  void toggleHideColumn(Grid.Column<Submission> column) {
    userPreferenceService.save(this, column.getKey(), column.isVisible());
  }

  SubmissionFilter filter() {
    return filter;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = INNER_CLASS_EI_EXPOSE_REP)
  protected class ColumnToggleContextMenu extends ContextMenu {
    public ColumnToggleContextMenu(Component target) {
      super(target);
      setOpenOnClick(true);
    }

    protected MenuItem addColumnToggleItem(String label, Grid.Column<Submission> column) {
      MenuItem menuItem = this.addItem(label, e -> {
        column.setVisible(e.getSource().isChecked());
        toggleHideColumn(column);
      });
      menuItem.setCheckable(true);
      menuItem.setChecked(column.isVisible());
      return menuItem;
    }
  }
}
