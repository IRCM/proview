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
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.VIEW;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIDDEN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SUBMISSION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USER;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.user.LaboratoryProperties.DIRECTOR;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.web.SamplesStatusDialog;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.text.NormalizedComparator;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.DateRangeField;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LitRenderer;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Submissions view.
 */
@Route(value = SubmissionsView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ UserRole.USER })
public class SubmissionsView extends VerticalLayout
    implements HasDynamicTitle, LocaleChangeObserver, NotificationComponent {
  public static final String VIEW_NAME = "submissions";
  public static final String ID = styleName(VIEW_NAME, "view");
  public static final String HEADER = "header";
  public static final String SUBMISSIONS = "submissions";
  public static final String SAMPLES_COUNT = SAMPLES + "Count";
  public static final String SAMPLES_VALUE = property(SAMPLES, "value");
  public static final String STATUS_VALUE = property(STATUS, "value");
  public static final String EDIT_STATUS = "editStatus";
  public static final String HISTORY = "history";
  public static final String ADD = "add";
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
  protected SubmissionDialog dialog;
  protected SamplesStatusDialog statusDialog;
  private transient AuthenticatedUser authenticatedUser;
  private transient SubmissionsViewPresenter presenter;

  @Autowired
  protected SubmissionsView(SubmissionsViewPresenter presenter, SubmissionDialog dialog,
      SamplesStatusDialog statusDialog, AuthenticatedUser authenticatedUser) {
    this.presenter = presenter;
    this.dialog = dialog;
    this.statusDialog = statusDialog;
    this.authenticatedUser = authenticatedUser;
  }

  @PostConstruct
  void init() {
    logger.debug("submissions view");
    setId(ID);
    setSizeFull();
    HorizontalLayout buttonsLayout = new HorizontalLayout();
    buttonsLayout.add(add, editStatus, history);
    add(header, submissions, buttonsLayout);
    expand(submissions);
    header.setId(HEADER);
    submissions.setId(SUBMISSIONS);
    submissions.setMinHeight("500px");
    submissions.setWidth(authenticatedUser.hasRole(ADMIN) ? "2500px" : "100%");
    submissions.addItemDoubleClickListener(e -> presenter.view(e.getItem()));
    submissions.addItemClickListener(e -> {
      if (e.isShiftKey() || e.isCtrlKey() || e.isMetaKey()) {
        presenter.editStatus(e.getItem());
      } else if (e.isAltKey()) {
        presenter.history(e.getItem());
      }
    });
    view = submissions
        .addColumn(LitRenderer.<Submission>of(VIEW_BUTTON).withFunction("view",
            submission -> presenter.view(submission)))
        .setKey(VIEW).setSortable(false).setFlexGrow(0);
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
    ValueProvider<Submission, String> submissionDirector =
        submission -> Objects.toString(submission.getLaboratory().getDirector(), "");
    director = submissions.addColumn(submissionDirector, DIRECTOR).setKey(DIRECTOR)
        .setComparator(NormalizedComparator.of(s -> s.getLaboratory().getDirector()))
        .setFlexGrow(3);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    dataAvailableDate =
        submissions.addColumn(submission -> submission.getDataAvailableDate() != null
            ? dateFormatter.format(submission.getDataAvailableDate())
            : "", DATA_AVAILABLE_DATE).setKey(DATA_AVAILABLE_DATE).setFlexGrow(2);
    date = submissions.addColumn(submission -> submission.getSubmissionDate().toLocalDate() != null
        ? dateFormatter.format(submission.getSubmissionDate().toLocalDate())
        : "", SUBMISSION_DATE).setKey(SUBMISSION_DATE).setFlexGrow(2);
    instrument = submissions
        .addColumn(submission -> submission.getInstrument() != null
            ? submission.getInstrument().getLabel(getLocale())
            : MassDetectionInstrument.getNullLabel(getLocale()), INSTRUMENT)
        .setKey(INSTRUMENT).setFlexGrow(2);
    service = submissions.addColumn(submission -> submission.getService() != null
        ? submission.getService().getLabel(getLocale())
        : Service.getNullLabel(getLocale()), SERVICE).setKey(SERVICE).setFlexGrow(2);
    samplesCount =
        submissions.addColumn(submission -> submission.getSamples().size(), SAMPLES_COUNT)
            .setKey(SAMPLES_COUNT).setFlexGrow(0);
    samples = submissions
        .addColumn(LitRenderer.<Submission>of(SAMPLES_SPAN)
            .withProperty("samplesValue", submission -> sampleNamesValue(submission))
            .withProperty("samplesTitle", submission -> sampleNamesTitle(submission)))
        .setKey(SAMPLES).setSortable(false).setFlexGrow(3);
    status = submissions
        .addColumn(LitRenderer.<Submission>of(STATUS_SPAN)
            .withProperty("statusValue", submission -> statusesValue(submission))
            .withProperty("statusTitle", submission -> statusesTitle(submission)))
        .setKey(STATUS).setSortable(false).setFlexGrow(2);
    hidden = submissions.addColumn(LitRenderer.<Submission>of(HIDDEN_BUTTON)
        .withProperty("hiddenTheme", submission -> hiddenTheme(submission))
        .withProperty("hiddenValue", submission -> hiddenValue(submission))
        .withProperty("hiddenIcon", submission -> hiddenIcon(submission))
        .withFunction("toggleHidden", submission -> {
          presenter.toggleHidden(submission);
          submissions.getDataProvider().refreshItem(submission);
        })).setKey(HIDDEN).setSortProperty(HIDDEN)
        .setComparator((s1, s2) -> Boolean.compare(s1.isHidden(), s2.isHidden()));
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
    filtersRow.getCell(dataAvailableDate).setComponent(dataAvailableDateFilter);
    dataAvailableDateFilter
        .addValueChangeListener(e -> presenter.filterDataAvailableDate(e.getValue()));
    dataAvailableDateFilter.setSizeFull();
    filtersRow.getCell(date).setComponent(dateFilter);
    dateFilter.addValueChangeListener(e -> presenter.filterDate(e.getValue()));
    dateFilter.setSizeFull();
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
    hiddenFilter.setItemLabelGenerator(
        value -> new AppResources(Submission.class, getLocale()).message(property(HIDDEN, value)));
    hiddenFilter.setClearButtonVisible(true);
    hiddenFilter.addValueChangeListener(e -> presenter.filterHidden(e.getValue()));
    hiddenFilter.setSizeFull();
    add.setId(ADD);
    add.setIcon(VaadinIcon.PLUS.create());
    add.addClickListener(e -> presenter.add());
    editStatus.setId(EDIT_STATUS);
    editStatus.setIcon(VaadinIcon.EDIT.create());
    editStatus.addClickListener(e -> presenter.editSelectedStatus(getLocale()));
    history.setId(HISTORY);
    history.setIcon(VaadinIcon.ARCHIVE.create());
    history.addClickListener(e -> presenter.historySelected(getLocale()));
    presenter.init(this);
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
    serviceFilter.setPlaceholder(resources.message(ALL));
    samplesFilter.setPlaceholder(resources.message(ALL));
    statusFilter.setPlaceholder(resources.message(ALL));
    hiddenFilter.setPlaceholder(resources.message(ALL));
    add.setText(resources.message(ADD));
    editStatus.setText(resources.message(EDIT_STATUS));
    history.setText(resources.message(HISTORY));
    submissions.getDataProvider().refreshAll();
    instrumentFilter.getDataProvider().refreshAll();
    serviceFilter.getDataProvider().refreshAll();
    statusFilter.getDataProvider().refreshAll();
    hiddenFilter.getDataProvider().refreshAll();
  }

  @Override
  public String getPageTitle() {
    final AppResources resources = new AppResources(getClass(), getLocale());
    final AppResources generalResources = new AppResources(Constants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }
}
