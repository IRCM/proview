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

import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.web.SampleSelectionWindow;
import ca.qc.ircm.proview.sample.web.SampleStatusView;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.submission.SubmissionService.Report;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.filter.LocalDateFilterComponent;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Submissions view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionsViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String SUBMISSIONS = "submissions";
  public static final String SAMPLE_COUNT = "sampleCount";
  public static final String SUBMISSION = submission.getMetadata().getName();
  public static final String SAMPLE = sample.getMetadata().getName();
  public static final String EXPERIENCE =
      SUBMISSION + "." + submission.experience.getMetadata().getName();
  public static final String EXPERIENCE_GOAL =
      SUBMISSION + "." + submission.goal.getMetadata().getName();
  public static final String SAMPLE_NAME =
      SAMPLE + "." + submissionSample.name.getMetadata().getName();
  public static final String SAMPLE_STATUSES = "statuses";
  public static final String SAMPLE_STATUSES_SEPARATOR = SAMPLE_STATUSES + ".separator";
  public static final String DATE =
      SUBMISSION + "." + submission.submissionDate.getMetadata().getName();
  public static final String LINKED_TO_RESULTS = "results";
  public static final String ALL = "all";
  public static final String SELECT_SAMPLES = "selectSamples";
  public static final String SELECT_SAMPLES_LABEL = "selectSamplesLabel";
  public static final String UPDATE_STATUS = "updateStatus";
  public static final String CONDITION_FALSE = "condition-false";
  private static final Logger logger = LoggerFactory.getLogger(SubmissionsViewPresenter.class);
  private SubmissionsView view;
  private SubmissionWebFilter filter;
  @Inject
  private SubmissionService submissionService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private Provider<LocalDateFilterComponent> localDateFilterComponentProvider;
  @Inject
  private Provider<SubmissionWindow> submissionWindowProvider;
  @Inject
  private Provider<SubmissionAnalysesWindow> submissionAnalysesWindowProvider;
  @Inject
  private Provider<SampleSelectionWindow> sampleSelectionWindowProvider;
  @Value("${spring.application.name}")
  private String applicationName;

  protected SubmissionsViewPresenter() {
  }

  protected SubmissionsViewPresenter(SubmissionService submissionService,
      AuthorizationService authorizationService,
      Provider<LocalDateFilterComponent> localDateFilterComponentProvider,
      Provider<SubmissionWindow> submissionWindowProvider,
      Provider<SubmissionAnalysesWindow> submissionAnalysesWindowProvider,
      Provider<SampleSelectionWindow> sampleSelectionWindowProvider, String applicationName) {
    this.submissionService = submissionService;
    this.authorizationService = authorizationService;
    this.localDateFilterComponentProvider = localDateFilterComponentProvider;
    this.submissionWindowProvider = submissionWindowProvider;
    this.submissionAnalysesWindowProvider = submissionAnalysesWindowProvider;
    this.sampleSelectionWindowProvider = sampleSelectionWindowProvider;
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(SubmissionsView view) {
    logger.debug("View submissions");
    this.view = view;
    filter = new SubmissionWebFilter(view.getLocale());
    prepareComponents();
    addListeners();
    searchSubmissions();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.setValue(resources.message(HEADER));
    prepareSumissionsGrid();
    view.selectSamplesButton.addStyleName(SELECT_SAMPLES);
    view.selectSamplesButton.setCaption(resources.message(SELECT_SAMPLES));
    view.selectSamplesButton.setVisible(authorizationService.hasAdminRole());
    view.selectedSamplesLabel.addStyleName(SELECT_SAMPLES_LABEL);
    view.selectedSamplesLabel
        .setValue(resources.message(SELECT_SAMPLES_LABEL, view.savedSamples().size()));
    view.selectedSamplesLabel.setVisible(authorizationService.hasAdminRole());
    view.updateStatusButton.addStyleName(UPDATE_STATUS);
    view.updateStatusButton.setCaption(resources.message(UPDATE_STATUS));
    view.updateStatusButton.setVisible(authorizationService.hasAdminRole());
  }

  private void prepareSumissionsGrid() {
    final MessageResource resources = view.getResources();
    final Locale locale = view.getLocale();
    final DateTimeFormatter dateFormatter =
        DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault());
    view.submissionsGrid.addStyleName(SUBMISSIONS);
    view.submissionsGrid.addStyleName(COMPONENTS);
    view.submissionsGrid.setDataProvider(searchSubmissions());
    view.submissionsGrid.addColumn(submission -> viewButton(submission), new ComponentRenderer())
        .setId(EXPERIENCE).setCaption(resources.message(EXPERIENCE));
    view.submissionsGrid.addColumn(submission -> submission.getSamples().size()).setId(SAMPLE_COUNT)
        .setCaption(resources.message(SAMPLE_COUNT));
    view.submissionsGrid.addColumn(submission -> submission.getSamples().get(0).getName())
        .setId(SAMPLE_NAME).setCaption(resources.message(SAMPLE_NAME));
    view.submissionsGrid.addColumn(Submission::getGoal).setId(EXPERIENCE_GOAL)
        .setCaption(resources.message(EXPERIENCE_GOAL));
    view.submissionsGrid.addColumn(submission -> statusesLabel(submission)).setId(SAMPLE_STATUSES)
        .setCaption(resources.message(SAMPLE_STATUSES));
    view.submissionsGrid
        .addColumn(submission -> dateFormatter.format(submission.getSubmissionDate())).setId(DATE)
        .setCaption(resources.message(DATE));
    view.submissionsGrid
        .addColumn(submission -> viewResultsButton(submission), new ComponentRenderer())
        .setId(LINKED_TO_RESULTS).setCaption(resources.message(LINKED_TO_RESULTS));
    view.submissionsGrid.setFrozenColumnCount(1);
    if (authorizationService.hasAdminRole()) {
      view.submissionsGrid.setSelectionMode(SelectionMode.MULTI);
    }
    HeaderRow filterRow = view.submissionsGrid.appendHeaderRow();
    filterRow.getCell(EXPERIENCE).setComponent(textFilter(e -> {
      filter.setExperienceContains(e.getValue());
      view.submissionsGrid.getDataProvider().refreshAll();
    }));
    filterRow.getCell(SAMPLE_NAME).setComponent(textFilter(e -> {
      filter.setAnySampleNameContains(e.getValue());
      view.submissionsGrid.getDataProvider().refreshAll();
    }));
    filterRow.getCell(EXPERIENCE_GOAL).setComponent(textFilter(e -> {
      filter.setGoalContains(e.getValue());
      view.submissionsGrid.getDataProvider().refreshAll();
    }));
    filterRow.getCell(SAMPLE_STATUSES).setComponent(comboBoxFilter(e -> {
      filter.setAnySampleStatus(e.getValue());
      view.submissionsGrid.getDataProvider().refreshAll();
    }, SampleStatus.values(), status -> status.getLabel(locale)));
    filterRow.getCell(DATE).setComponent(instantFilter(e -> {
      filter.setDateRange(e.getSavedObject());
      view.submissionsGrid.getDataProvider().refreshAll();
    }));
    filterRow.getCell(LINKED_TO_RESULTS).setComponent(comboBoxFilter(e -> {
      filter.setResults(e.getValue());
      view.submissionsGrid.getDataProvider().refreshAll();
    }, new Boolean[] { true, false }, value -> resources.message(LINKED_TO_RESULTS + "." + value)));
    view.submissionsGrid.sort(DATE, SortDirection.DESCENDING);
  }

  private Button viewButton(Submission submission) {
    Button button = new Button();
    button.addStyleName(EXPERIENCE);
    button.setCaption(submission.getExperience());
    button.addClickListener(e -> viewSubmission(submission));
    return button;
  }

  private String statusesLabel(Submission submission) {
    MessageResource resources = view.getResources();
    Locale locale = view.getLocale();
    return submission.getSamples().stream().map(sample -> sample.getStatus())
        .filter(status -> status != null).distinct().sorted().map(status -> status.getLabel(locale))
        .collect(Collectors.joining(resources.message(SAMPLE_STATUSES_SEPARATOR)));
  }

  private Button viewResultsButton(Submission submission) {
    MessageResource resources = view.getResources();
    boolean results = submission.getSamples().stream().filter(sample -> sample.getStatus() != null)
        .filter(sample -> SampleStatus.ANALYSED.compareTo(sample.getStatus()) <= 0).count() > 0;
    Button button = new Button();
    button.addStyleName(LINKED_TO_RESULTS);
    button.setCaption(resources.message(LINKED_TO_RESULTS + "." + results));
    if (results) {
      button.addClickListener(e -> viewSubmissionResults(submission));
    } else {
      button.setStyleName(ValoTheme.BUTTON_BORDERLESS);
      button.addStyleName(CONDITION_FALSE);
    }
    return button;
  }

  private TextField textFilter(ValueChangeListener<String> listener) {
    MessageResource resources = view.getResources();
    TextField filter = new TextField();
    filter.addValueChangeListener(listener);
    filter.setWidth("100%");
    filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
    filter.setPlaceholder(resources.message(ALL));
    return filter;
  }

  private <V> ComboBox<V> comboBoxFilter(ValueChangeListener<V> listener, V[] values,
      ItemCaptionGenerator<V> itemCaptionGenerator) {
    MessageResource resources = view.getResources();
    ComboBox<V> filter = new ComboBox<>();
    filter.setEmptySelectionAllowed(true);
    filter.setTextInputAllowed(false);
    filter.setEmptySelectionCaption(resources.message(ALL));
    filter.setPlaceholder(resources.message(ALL));
    filter.setItems(values);
    filter.setSelectedItem(null);
    filter.addValueChangeListener(listener);
    filter.setWidth("100%");
    filter.addStyleName(ValoTheme.COMBOBOX_TINY);
    filter.setPlaceholder(resources.message(ALL));
    return filter;
  }

  private Component instantFilter(SaveListener<Range<LocalDate>> listener) {
    LocalDateFilterComponent filter = localDateFilterComponentProvider.get();
    filter.addStyleName(ValoTheme.BUTTON_TINY);
    filter.addSaveListener(listener);
    return filter;
  }

  private void addListeners() {
    view.updateStatusButton.addClickListener(e -> updateStatus());
    view.selectSamplesButton.addClickListener(e -> selectSamples());
  }

  private ListDataProvider<Submission> searchSubmissions() {
    Report report = submissionService.report();
    ListDataProvider<Submission> dataProvider = DataProvider.ofCollection(report.getSubmissions());
    dataProvider.setFilter(filter);
    return dataProvider;
  }

  private void viewSubmission(Submission submission) {
    SubmissionWindow window = submissionWindowProvider.get();
    window.setSubmission(submission);
    window.center();
    view.addWindow(window);
  }

  private void viewSubmissionResults(Submission submission) {
    SubmissionAnalysesWindow window = submissionAnalysesWindowProvider.get();
    window.setSubmission(submission);
    window.center();
    view.addWindow(window);
  }

  private void selectSamples() {
    SampleSelectionWindow window = sampleSelectionWindowProvider.get();
    view.addWindow(window);
    List<Sample> samples;
    if (!view.submissionsGrid.getSelectedItems().isEmpty()) {
      samples = view.submissionsGrid.getSelectedItems().stream()
          .flatMap(submission -> submission.getSamples().stream()).collect(Collectors.toList());
    } else {
      samples = view.savedSamples();
    }
    window.setSelectedSamples(samples);
    window.center();
    window.addSaveListener(e -> {
      MessageResource resources = view.getResources();
      List<Sample> selectedSamples = window.getSelectedSamples();
      view.submissionsGrid.deselectAll();
      view.saveSamples(selectedSamples);
      view.selectedSamplesLabel
          .setValue(resources.message(SELECT_SAMPLES_LABEL, selectedSamples.size()));
      logger.debug("Selected samples {}", selectedSamples);
    });
  }

  private void updateStatus() {
    if (!view.submissionsGrid.getSelectedItems().isEmpty()) {
      List<Sample> samples = view.submissionsGrid.getSelectedItems().stream()
          .flatMap(submission -> submission.getSamples().stream()).collect(Collectors.toList());
      view.saveSamples(samples);
    }
    view.navigateTo(SampleStatusView.VIEW_NAME);
  }

  SubmissionWebFilter getFilter() {
    return filter;
  }
}
