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

import static ca.qc.ircm.proview.Constants.ALL;
import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.VIEW;
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

import ca.qc.ircm.proview.AppResources;
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
import com.vaadin.flow.component.html.H2;
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
import java.util.Locale;
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
  public static final String HEADER = "header";
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
  public static final String VIEW_BUTTON =
      "<vaadin-button class='" + VIEW + "' theme='icon' @click='${view}'>"
          + "<vaadin-icon icon='vaadin:eye' slot='prefix'></vaadin-icon>" + "</vaadin-button>";
  private static final long serialVersionUID = 4399000178746918928L;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionsView.class);
  protected H2 header = new H2();
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
  protected Column<Submission> view;
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
    setSizeFull();
    HorizontalLayout buttonsLayout = new HorizontalLayout();
    buttonsLayout.add(add, editStatus, history, hideColumns);
    add(header, submissions, buttonsLayout);
    expand(submissions);

    columnProperties.put(EXPERIMENT, submission.experiment);
    columnProperties.put(USER, submission.user.name);
    columnProperties.put(DIRECTOR, submission.laboratory.director);
    columnProperties.put(DATA_AVAILABLE_DATE, submission.dataAvailableDate);
    columnProperties.put(SERVICE, submission.service);
    columnProperties.put(INSTRUMENT, submission.instrument);
    columnProperties.put(SAMPLES_COUNT, submission.samples.size());
    columnProperties.put(SUBMISSION_DATE, submission.submissionDate);
    columnProperties.put(HIDDEN, submission.hidden);

    header.setId(HEADER);
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
    Function<Column<Submission>, Boolean> columnVisibility = column -> {
      Optional<Boolean> value = userPreferenceService.get(this, column.getKey());
      return value.orElse(true);
    };
    view = submissions.addColumn(LitRenderer.<Submission>of(VIEW_BUTTON).withFunction("view",
        submission -> view(submission))).setKey(VIEW).setSortable(false).setFlexGrow(0);
    ValueProvider<Submission, String> submissionExperiment =
        submission -> Objects.toString(submission.getExperiment(), "");
    experiment = submissions.addColumn(submissionExperiment, EXPERIMENT).setKey(EXPERIMENT)
        .setComparator(NormalizedComparator.of(Submission::getExperiment)).setFlexGrow(3);
    ValueProvider<Submission,
        String> submissionUser = submission -> submission.getUser() != null
            ? Objects.toString(submission.getUser().getName())
            : "";
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
    instrument = submissions
        .addColumn(submission -> submission.getInstrument() != null
            ? submission.getInstrument().getLabel(getLocale())
            : MassDetectionInstrument.getNullLabel(getLocale()), INSTRUMENT)
        .setKey(INSTRUMENT).setFlexGrow(2);
    instrument.setVisible(authenticatedUser.hasRole(ADMIN) && columnVisibility.apply(instrument));
    service = submissions.addColumn(submission -> submission.getService() != null
        ? submission.getService().getLabel(getLocale())
        : Service.getNullLabel(getLocale()), SERVICE).setKey(SERVICE).setFlexGrow(2);
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
    editStatus.setId(EDIT_STATUS);
    editStatus.setIcon(VaadinIcon.EDIT.create());
    editStatus.addClickListener(e -> editSelectedStatus(getLocale()));
    editStatus.setVisible(authenticatedUser.hasRole(ADMIN));
    history.setId(HISTORY);
    history.setIcon(VaadinIcon.ARCHIVE.create());
    history.addClickListener(e -> historySelected(getLocale()));
    history.setVisible(authenticatedUser.hasRole(ADMIN));
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
    final AppResources resources = new AppResources(getClass(), getLocale());
    return resources.message(SAMPLES_VALUE, submission.getSamples().get(0).getName(),
        submission.getSamples().size());
  }

  private String sampleNamesTitle(Submission submission) {
    return submission.getSamples().stream().map(SubmissionSample::getName)
        .collect(Collectors.joining("\n"));
  }

  private String statusesValue(Submission submission) {
    final AppResources resources = new AppResources(getClass(), getLocale());
    List<SampleStatus> statuses = submission.getSamples().stream().map(sample -> sample.getStatus())
        .distinct().collect(Collectors.toList());
    return resources.message(STATUS_VALUE, statuses.get(0).getLabel(getLocale()), statuses.size());
  }

  private String statusesTitle(Submission submission) {
    List<SampleStatus> statuses = submission.getSamples().stream().map(sample -> sample.getStatus())
        .distinct().collect(Collectors.toList());
    return statuses.stream().map(status -> status.getLabel(getLocale()))
        .collect(Collectors.joining("\n"));
  }

  private String hiddenTheme(Submission submission) {
    return submission.isHidden() ? ButtonVariant.LUMO_ERROR.getVariantName()
        : ButtonVariant.LUMO_SUCCESS.getVariantName();
  }

  private String hiddenValue(Submission submission) {
    final AppResources resources = new AppResources(Submission.class, getLocale());
    return resources.message(property(HIDDEN, submission.isHidden()));
  }

  private String hiddenIcon(Submission submission) {
    return submission.isHidden() ? "vaadin:eye-slash" : "vaadin:eye";
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final AppResources resources = new AppResources(getClass(), getLocale());
    final AppResources submissionResources = new AppResources(Submission.class, getLocale());
    final AppResources laboratoryResources = new AppResources(Laboratory.class, getLocale());
    final AppResources submissionSampleResources =
        new AppResources(SubmissionSample.class, getLocale());
    final AppResources webResources = new AppResources(Constants.class, getLocale());
    header.setText(resources.message(HEADER));
    String experimentHeader = submissionResources.message(EXPERIMENT);
    experiment.setHeader(experimentHeader).setFooter(experimentHeader);
    String userHeader = submissionResources.message(USER);
    user.setHeader(userHeader).setFooter(userHeader);
    String directorHeader = laboratoryResources.message(DIRECTOR);
    director.setHeader(directorHeader).setFooter(directorHeader);
    String dataAvailableDateHeader = submissionResources.message(DATA_AVAILABLE_DATE);
    dataAvailableDate.setHeader(dataAvailableDateHeader).setFooter(dataAvailableDateHeader);
    String dateHeader = submissionResources.message(SUBMISSION_DATE);
    date.setHeader(dateHeader).setFooter(dateHeader);
    String instrumentHeader = submissionResources.message(INSTRUMENT);
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
    String viewHeader = webResources.message(VIEW);
    view.setHeader(viewHeader).setFooter(viewHeader);
    experimentFilter.setPlaceholder(resources.message(ALL));
    userFilter.setPlaceholder(resources.message(ALL));
    directorFilter.setPlaceholder(resources.message(ALL));
    instrumentFilter.setPlaceholder(resources.message(ALL));
    instrumentFilter.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    serviceFilter.setPlaceholder(resources.message(ALL));
    serviceFilter.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    samplesFilter.setPlaceholder(resources.message(ALL));
    statusFilter.setPlaceholder(resources.message(ALL));
    statusFilter.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    hiddenFilter.setPlaceholder(resources.message(ALL));
    hiddenFilter.setItemLabelGenerator(
        value -> new AppResources(Submission.class, getLocale()).message(property(HIDDEN, value)));
    add.setText(resources.message(ADD));
    editStatus.setText(resources.message(EDIT_STATUS));
    history.setText(resources.message(HISTORY));
    hideColumns.setText(resources.message(HIDE_COLUMNS));
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
    final AppResources resources = new AppResources(getClass(), getLocale());
    final AppResources generalResources = new AppResources(Constants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
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

  void view(Submission submission) {
    SubmissionDialog dialog = dialogFactory.getObject();
    dialog.setSubmissionId(submission.getId());
    dialog.open();
    dialog.addSavedListener(e -> loadSubmissions());
  }

  void editStatus(Submission submission) {
    if (authenticatedUser.hasRole(ADMIN)) {
      SamplesStatusDialog statusDialog = statusDialogFactory.getObject();
      statusDialog.setSubmissionId(submission.getId());
      statusDialog.open();
      statusDialog.addSavedListener(e -> loadSubmissions());
    }
  }

  void history(Submission submission) {
    if (authenticatedUser.hasRole(ADMIN)) {
      UI.getCurrent().navigate(HistoryView.class, submission.getId());
    }
  }

  void add() {
    UI.getCurrent().navigate(SubmissionView.class);
  }

  void editSelectedStatus(Locale locale) {
    Optional<Submission> os = submissions.getSelectedItems().stream().findFirst();
    if (os.isPresent()) {
      editStatus(os.get());
    } else {
      AppResources resources = new AppResources(SubmissionsView.class, locale);
      showNotification(resources.message(property(SUBMISSIONS, REQUIRED)));
    }
  }

  void historySelected(Locale locale) {
    Optional<Submission> os = submissions.getSelectedItems().stream().findFirst();
    if (os.isPresent()) {
      history(os.get());
    } else {
      AppResources resources = new AppResources(SubmissionsView.class, locale);
      showNotification(resources.message(property(SUBMISSIONS, REQUIRED)));
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
