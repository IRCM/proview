package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.SubmissionProperties.ANALYSIS_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLE_DELIVERY_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SUBMISSION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USER;
import static ca.qc.ircm.proview.text.Strings.normalize;
import static ca.qc.ircm.proview.user.LaboratoryProperties.DIRECTOR;
import static ca.qc.ircm.proview.web.WebConstants.ALL;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Submissions view.
 */
@Tag("submissions-view")
@Route(value = MainView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ UserRole.USER })
public class SubmissionsView extends VerticalLayout
    implements HasDynamicTitle, LocaleChangeObserver {
  public static final String VIEW_NAME = "submissions";
  public static final String HEADER = "header";
  public static final String SUBMISSIONS = "submissions";
  public static final String ADD = "add";
  private static final long serialVersionUID = 4399000178746918928L;
  protected H2 header = new H2();
  protected Grid<Submission> submissions = new Grid<>();
  protected Column<Submission> experiment;
  protected Column<Submission> user;
  protected Column<Submission> director;
  protected Column<Submission> service;
  protected Column<Submission> sample;
  protected Column<Submission> sampleDeliveryDate;
  protected Column<Submission> digestionDate;
  protected Column<Submission> analysisDate;
  protected Column<Submission> dataAvailableDate;
  protected Column<Submission> date;
  protected TextField experimentFilter = new TextField();
  protected TextField userFilter = new TextField();
  protected TextField directorFilter = new TextField();
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
    sampleDeliveryDate = submissions
        .addColumn(new LocalDateRenderer<>(Submission::getSampleDeliveryDate, dateFormatter),
            SAMPLE_DELIVERY_DATE)
        .setKey(SAMPLE_DELIVERY_DATE);
    digestionDate =
        submissions.addColumn(new LocalDateRenderer<>(Submission::getDigestionDate, dateFormatter),
            DIGESTION_DATE).setKey(DIGESTION_DATE);
    analysisDate =
        submissions.addColumn(new LocalDateRenderer<>(Submission::getAnalysisDate, dateFormatter),
            ANALYSIS_DATE).setKey(ANALYSIS_DATE);
    dataAvailableDate = submissions
        .addColumn(new LocalDateRenderer<>(Submission::getDataAvailableDate, dateFormatter),
            DATA_AVAILABLE_DATE)
        .setKey(DATA_AVAILABLE_DATE);
    date = submissions.addColumn(new LocalDateRenderer<>(
        submission -> submission.getSubmissionDate().toLocalDate(), dateFormatter), SUBMISSION_DATE)
        .setKey(SUBMISSION_DATE);
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
    add.setId(ADD);
    add.setIcon(VaadinIcon.PLUS.create());
    add.addClickListener(e -> presenter.add());
    presenter.init(this);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    final MessageResource submissionResources = new MessageResource(Submission.class, getLocale());
    final MessageResource laboratoryResources = new MessageResource(Laboratory.class, getLocale());
    final MessageResource webResources = new MessageResource(WebConstants.class, getLocale());
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
    experimentFilter.setPlaceholder(resources.message(ALL));
    add.setText(resources.message(ADD));
  }

  @Override
  public String getPageTitle() {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    final MessageResource generalResources = new MessageResource(WebConstants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }
}
