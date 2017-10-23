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

import ca.qc.ircm.proview.digestion.web.DigestionView;
import ca.qc.ircm.proview.dilution.web.DilutionView;
import ca.qc.ircm.proview.enrichment.web.EnrichmentView;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.web.ContainerSelectionWindow;
import ca.qc.ircm.proview.sample.web.SampleSelectionWindow;
import ca.qc.ircm.proview.sample.web.SampleStatusView;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.transfer.web.TransferView;
import ca.qc.ircm.proview.user.UserPreferenceService;
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
  public static final String USER = SUBMISSION + "." + submission.user.getMetadata().getName();
  public static final String EXPERIENCE_GOAL =
      SUBMISSION + "." + submission.goal.getMetadata().getName();
  public static final String SAMPLE_NAME =
      SAMPLE + "." + submissionSample.name.getMetadata().getName();
  public static final String SAMPLE_STATUSES = "statuses";
  public static final String SAMPLE_STATUSES_SEPARATOR = SAMPLE_STATUSES + ".separator";
  public static final String DATE =
      SUBMISSION + "." + submission.submissionDate.getMetadata().getName();
  public static final String LINKED_TO_RESULTS = "results";
  public static final String TREATMENTS = "treatments";
  public static final String HISTORY = "history";
  public static final String ALL = "all";
  public static final String SELECT_SAMPLES = "selectSamples";
  public static final String SELECT_SAMPLES_LABEL = "selectSamplesLabel";
  public static final String SELECT_CONTAINERS = "selectContainers";
  public static final String SELECT_CONTAINERS_NO_SAMPLES = "selectContainers.noSamples";
  public static final String SELECT_CONTAINERS_LABEL = "selectContainersLabel";
  public static final String UPDATE_STATUS = "updateStatus";
  public static final String TRANSFER = "transfer";
  public static final String DIGESTION = "digestion";
  public static final String ENRICHMENT = "enrichment";
  public static final String DILUTION = "dilution";
  public static final String NO_CONTAINERS = "noContainers";
  public static final String CONDITION_FALSE = "condition-false";
  public static final String COLUMN_ORDER = "columnOrder";
  private static final Logger logger = LoggerFactory.getLogger(SubmissionsViewPresenter.class);
  private SubmissionsView view;
  private SubmissionsViewDesign design;
  private SubmissionWebFilter filter;
  @Inject
  private SubmissionService submissionService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private UserPreferenceService userPreferenceService;
  @Inject
  private Provider<LocalDateFilterComponent> localDateFilterComponentProvider;
  @Inject
  private Provider<SubmissionWindow> submissionWindowProvider;
  @Inject
  private Provider<SubmissionAnalysesWindow> submissionAnalysesWindowProvider;
  @Inject
  private Provider<SubmissionTreatmentsWindow> submissionTreatmentsWindowProvider;
  @Inject
  private Provider<SubmissionHistoryWindow> submissionHistoryWindowProvider;
  @Inject
  private Provider<SampleSelectionWindow> sampleSelectionWindowProvider;
  @Inject
  private Provider<ContainerSelectionWindow> containerSelectionWindowProvider;
  @Value("${spring.application.name}")
  private String applicationName;

  protected SubmissionsViewPresenter() {
  }

  protected SubmissionsViewPresenter(SubmissionService submissionService,
      AuthorizationService authorizationService, UserPreferenceService userPreferenceService,
      Provider<LocalDateFilterComponent> localDateFilterComponentProvider,
      Provider<SubmissionWindow> submissionWindowProvider,
      Provider<SubmissionAnalysesWindow> submissionAnalysesWindowProvider,
      Provider<SubmissionTreatmentsWindow> submissionTreatmentsWindowProvider,
      Provider<SubmissionHistoryWindow> submissionHistoryWindowProvider,
      Provider<SampleSelectionWindow> sampleSelectionWindowProvider,
      Provider<ContainerSelectionWindow> containerSelectionWindowProvider, String applicationName) {
    this.submissionService = submissionService;
    this.authorizationService = authorizationService;
    this.userPreferenceService = userPreferenceService;
    this.localDateFilterComponentProvider = localDateFilterComponentProvider;
    this.submissionWindowProvider = submissionWindowProvider;
    this.submissionAnalysesWindowProvider = submissionAnalysesWindowProvider;
    this.submissionTreatmentsWindowProvider = submissionTreatmentsWindowProvider;
    this.submissionHistoryWindowProvider = submissionHistoryWindowProvider;
    this.sampleSelectionWindowProvider = sampleSelectionWindowProvider;
    this.containerSelectionWindowProvider = containerSelectionWindowProvider;
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
    design = view.design;
    filter = new SubmissionWebFilter(view.getLocale());
    prepareComponents();
    addListeners();
    searchSubmissions();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.headerLabel.addStyleName(HEADER);
    design.headerLabel.setValue(resources.message(HEADER));
    prepareSumissionsGrid();
    design.sampleSelectionLayout.setVisible(authorizationService.hasAdminRole());
    design.selectSamplesButton.addStyleName(SELECT_SAMPLES);
    design.selectSamplesButton.setCaption(resources.message(SELECT_SAMPLES));
    design.selectedSamplesLabel.addStyleName(SELECT_SAMPLES_LABEL);
    design.selectedSamplesLabel
        .setValue(resources.message(SELECT_SAMPLES_LABEL, view.savedSamples().size()));
    design.containerSelectionLayout.setVisible(authorizationService.hasAdminRole());
    design.selectContainers.addStyleName(SELECT_CONTAINERS);
    design.selectContainers.setCaption(resources.message(SELECT_CONTAINERS));
    design.selectContainers.addClickListener(e -> selectContainers());
    design.selectedContainersLabel.addStyleName(SELECT_CONTAINERS_LABEL);
    design.selectedContainersLabel
        .setValue(resources.message(SELECT_CONTAINERS_LABEL, view.savedSamples().size()));
    design.updateStatusButton.addStyleName(UPDATE_STATUS);
    design.updateStatusButton.setCaption(resources.message(UPDATE_STATUS));
    design.updateStatusButton.setVisible(authorizationService.hasAdminRole());
    design.transfer.addStyleName(TRANSFER);
    design.transfer.setCaption(resources.message(TRANSFER));
    design.transfer.setVisible(authorizationService.hasAdminRole());
    design.transfer.addClickListener(e -> transfer());
    design.digestion.addStyleName(DIGESTION);
    design.digestion.setCaption(resources.message(DIGESTION));
    design.digestion.setVisible(authorizationService.hasAdminRole());
    design.digestion.addClickListener(e -> digestion());
    design.enrichment.addStyleName(ENRICHMENT);
    design.enrichment.setCaption(resources.message(ENRICHMENT));
    design.enrichment.setVisible(authorizationService.hasAdminRole());
    design.enrichment.addClickListener(e -> enrichment());
    design.dilution.addStyleName(DILUTION);
    design.dilution.setCaption(resources.message(DILUTION));
    design.dilution.setVisible(authorizationService.hasAdminRole());
    design.dilution.addClickListener(e -> dilution());
  }

  private void prepareSumissionsGrid() {
    final MessageResource resources = view.getResources();
    final Locale locale = view.getLocale();
    final DateTimeFormatter dateFormatter =
        DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault());
    design.submissionsGrid.addStyleName(SUBMISSIONS);
    design.submissionsGrid.addStyleName(COMPONENTS);
    design.submissionsGrid.setDataProvider(searchSubmissions());
    design.submissionsGrid.addColumn(submission -> viewButton(submission), new ComponentRenderer())
        .setId(EXPERIENCE).setCaption(resources.message(EXPERIENCE));
    design.submissionsGrid.addColumn(submission -> submission.getUser().getName()).setId(USER)
        .setCaption(resources.message(USER))
        .setDescriptionGenerator(submission -> submission.getUser().getEmail());
    design.submissionsGrid.addColumn(submission -> submission.getSamples().size())
        .setId(SAMPLE_COUNT).setCaption(resources.message(SAMPLE_COUNT));
    design.submissionsGrid.addColumn(submission -> submission.getSamples().get(0).getName())
        .setId(SAMPLE_NAME).setCaption(resources.message(SAMPLE_NAME));
    design.submissionsGrid.addColumn(Submission::getGoal).setId(EXPERIENCE_GOAL)
        .setCaption(resources.message(EXPERIENCE_GOAL));
    design.submissionsGrid.addColumn(submission -> statusesLabel(submission)).setId(SAMPLE_STATUSES)
        .setCaption(resources.message(SAMPLE_STATUSES));
    design.submissionsGrid
        .addColumn(submission -> dateFormatter.format(submission.getSubmissionDate())).setId(DATE)
        .setCaption(resources.message(DATE));
    design.submissionsGrid
        .addColumn(submission -> viewResultsButton(submission), new ComponentRenderer())
        .setId(LINKED_TO_RESULTS).setCaption(resources.message(LINKED_TO_RESULTS))
        .setComparator((s1, s2) -> Boolean.compare(linkedToResults(s1), linkedToResults(s2)));
    design.submissionsGrid
        .addColumn(submission -> viewTreatmentsButton(submission), new ComponentRenderer())
        .setId(TREATMENTS).setCaption(resources.message(TREATMENTS)).setSortable(false);
    design.submissionsGrid
        .addColumn(submission -> viewHistoryButton(submission), new ComponentRenderer())
        .setId(HISTORY).setCaption(resources.message(HISTORY)).setSortable(false);
    if (authorizationService.hasManagerRole() || authorizationService.hasAdminRole()) {
      design.submissionsGrid.getColumn(USER).setHidable(true);
      design.submissionsGrid.getColumn(USER)
          .setHidden(userPreferenceService.get(this, USER, false));
    } else {
      design.submissionsGrid.getColumn(USER).setHidden(true);
    }
    design.submissionsGrid.getColumn(SAMPLE_COUNT).setHidable(true);
    design.submissionsGrid.getColumn(SAMPLE_COUNT)
        .setHidden(userPreferenceService.get(this, SAMPLE_COUNT, false));
    design.submissionsGrid.getColumn(SAMPLE_NAME).setHidable(true);
    design.submissionsGrid.getColumn(SAMPLE_NAME)
        .setHidden(userPreferenceService.get(this, SAMPLE_NAME, false));
    design.submissionsGrid.getColumn(EXPERIENCE_GOAL).setHidable(true);
    design.submissionsGrid.getColumn(EXPERIENCE_GOAL)
        .setHidden(userPreferenceService.get(this, EXPERIENCE_GOAL, false));
    design.submissionsGrid.getColumn(SAMPLE_STATUSES).setHidable(true);
    design.submissionsGrid.getColumn(SAMPLE_STATUSES)
        .setHidden(userPreferenceService.get(this, SAMPLE_STATUSES, false));
    design.submissionsGrid.getColumn(DATE).setHidable(true);
    design.submissionsGrid.getColumn(DATE).setHidden(userPreferenceService.get(this, DATE, false));
    design.submissionsGrid.getColumn(LINKED_TO_RESULTS).setHidable(true);
    design.submissionsGrid.getColumn(LINKED_TO_RESULTS)
        .setHidden(userPreferenceService.get(this, LINKED_TO_RESULTS, false));
    if (authorizationService.hasAdminRole()) {
      design.submissionsGrid.getColumn(TREATMENTS).setHidable(true);
      design.submissionsGrid.getColumn(TREATMENTS)
          .setHidden(userPreferenceService.get(this, TREATMENTS, false));
      design.submissionsGrid.getColumn(HISTORY).setHidable(true);
      design.submissionsGrid.getColumn(HISTORY)
          .setHidden(userPreferenceService.get(this, HISTORY, false));
    } else {
      design.submissionsGrid.getColumn(TREATMENTS).setHidden(true);
      design.submissionsGrid.getColumn(HISTORY).setHidden(true);
    }
    design.submissionsGrid.addColumnVisibilityChangeListener(e -> {
      userPreferenceService.save(SubmissionsViewPresenter.this, e.getColumn().getId(),
          e.isHidden());
    });
    String[] defaultColumnOrder =
        design.submissionsGrid.getColumns().stream().map(col -> col.getId()).toArray(String[]::new);
    design.submissionsGrid
        .setColumnOrder(userPreferenceService.get(this, COLUMN_ORDER, defaultColumnOrder));
    design.submissionsGrid.setColumnReorderingAllowed(true);
    design.submissionsGrid.addColumnReorderListener(e -> {
      userPreferenceService.save(SubmissionsViewPresenter.this, COLUMN_ORDER, design.submissionsGrid
          .getColumns().stream().map(col -> col.getId()).toArray(String[]::new));
    });
    design.submissionsGrid.setFrozenColumnCount(1);
    if (authorizationService.hasAdminRole()) {
      design.submissionsGrid.setSelectionMode(SelectionMode.MULTI);
    }
    HeaderRow filterRow = design.submissionsGrid.appendHeaderRow();
    filterRow.getCell(EXPERIENCE).setComponent(textFilter(e -> {
      filter.experienceContains = e.getValue();
      design.submissionsGrid.getDataProvider().refreshAll();
    }));
    filterRow.getCell(USER).setComponent(textFilter(e -> {
      filter.userContains = e.getValue();
      design.submissionsGrid.getDataProvider().refreshAll();
    }));
    filterRow.getCell(SAMPLE_NAME).setComponent(textFilter(e -> {
      filter.anySampleNameContains = e.getValue();
      design.submissionsGrid.getDataProvider().refreshAll();
    }));
    filterRow.getCell(EXPERIENCE_GOAL).setComponent(textFilter(e -> {
      filter.goalContains = e.getValue();
      design.submissionsGrid.getDataProvider().refreshAll();
    }));
    filterRow.getCell(SAMPLE_STATUSES).setComponent(comboBoxFilter(e -> {
      filter.anySampleStatus = e.getValue();
      design.submissionsGrid.getDataProvider().refreshAll();
    }, SampleStatus.values(), status -> status.getLabel(locale)));
    filterRow.getCell(DATE).setComponent(instantFilter(e -> {
      filter.dateRange = e.getSavedObject();
      design.submissionsGrid.getDataProvider().refreshAll();
    }));
    filterRow.getCell(LINKED_TO_RESULTS).setComponent(comboBoxFilter(e -> {
      filter.results = e.getValue();
      design.submissionsGrid.getDataProvider().refreshAll();
    }, new Boolean[] { true, false }, value -> resources.message(LINKED_TO_RESULTS + "." + value)));
    design.submissionsGrid.sort(DATE, SortDirection.DESCENDING);
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
    boolean results = linkedToResults(submission);
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

  private boolean linkedToResults(Submission submission) {
    return submission.getSamples().stream().filter(sample -> sample.getStatus() != null)
        .filter(sample -> SampleStatus.ANALYSED.equals(sample.getStatus())
            || SampleStatus.DATA_ANALYSIS.equals(sample.getStatus()))
        .count() > 0;
  }

  private Button viewTreatmentsButton(Submission submission) {
    MessageResource resources = view.getResources();
    Button button = new Button();
    button.addStyleName(TREATMENTS);
    button.setCaption(resources.message(TREATMENTS));
    button.addClickListener(e -> viewSubmissionTreatments(submission));
    return button;
  }

  private Button viewHistoryButton(Submission submission) {
    MessageResource resources = view.getResources();
    Button button = new Button();
    button.addStyleName(HISTORY);
    button.setCaption(resources.message(HISTORY));
    button.addClickListener(e -> viewSubmissionHistory(submission));
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
    design.updateStatusButton.addClickListener(e -> updateStatus());
    design.selectSamplesButton.addClickListener(e -> selectSamples());
  }

  private ListDataProvider<Submission> searchSubmissions() {
    List<Submission> submissions = submissionService.all();
    ListDataProvider<Submission> dataProvider = DataProvider.ofCollection(submissions);
    dataProvider.setFilter(filter);
    return dataProvider;
  }

  private void viewSubmission(Submission submission) {
    SubmissionWindow window = submissionWindowProvider.get();
    window.setValue(submission);
    window.center();
    view.addWindow(window);
  }

  private void viewSubmissionResults(Submission submission) {
    SubmissionAnalysesWindow window = submissionAnalysesWindowProvider.get();
    window.setValue(submission);
    window.center();
    view.addWindow(window);
  }

  private void viewSubmissionTreatments(Submission submission) {
    SubmissionTreatmentsWindow window = submissionTreatmentsWindowProvider.get();
    window.setValue(submission);
    window.center();
    view.addWindow(window);
  }

  private void viewSubmissionHistory(Submission submission) {
    SubmissionHistoryWindow window = submissionHistoryWindowProvider.get();
    window.setValue(submission);
    window.center();
    view.addWindow(window);
  }

  private void selectSamples() {
    SampleSelectionWindow window = sampleSelectionWindowProvider.get();
    view.addWindow(window);
    List<Sample> samples;
    if (!design.submissionsGrid.getSelectedItems().isEmpty()) {
      samples = design.submissionsGrid.getSelectedItems().stream()
          .flatMap(submission -> submission.getSamples().stream()).collect(Collectors.toList());
    } else {
      samples = view.savedSamples();
    }
    window.setItems(samples);
    window.center();
    window.addSaveListener(e -> {
      MessageResource resources = view.getResources();
      List<Sample> selectedSamples = e.getSavedObject();
      design.submissionsGrid.deselectAll();
      view.saveSamples(selectedSamples);
      design.selectedSamplesLabel
          .setValue(resources.message(SELECT_SAMPLES_LABEL, selectedSamples.size()));
      logger.debug("Selected samples {}", selectedSamples);
    });
  }

  private void saveSelectedSamples() {
    if (!design.submissionsGrid.getSelectedItems().isEmpty()) {
      MessageResource resources = view.getResources();
      List<Sample> samples = design.submissionsGrid.getSelectedItems().stream()
          .flatMap(submission -> submission.getSamples().stream()).collect(Collectors.toList());
      view.saveSamples(samples);
      design.selectedSamplesLabel.setValue(resources.message(SELECT_SAMPLES_LABEL, samples.size()));
    }
  }

  private void selectContainers() {
    saveSelectedSamples();
    if (view.savedSamples().isEmpty()) {
      MessageResource resources = view.getResources();
      view.showError(resources.message(SELECT_CONTAINERS_NO_SAMPLES));
    } else {
      ContainerSelectionWindow window = containerSelectionWindowProvider.get();
      view.addWindow(window);
      List<Sample> samples = view.savedSamples();
      window.setSamples(samples);
      window.center();
      window.addSaveListener(e -> {
        MessageResource resources = view.getResources();
        List<SampleContainer> selectedContainers = e.getSavedObject();
        view.saveContainers(selectedContainers);
        design.selectedContainersLabel
            .setValue(resources.message(SELECT_CONTAINERS_LABEL, selectedContainers.size()));
        logger.debug("Selected containers {}", selectedContainers);
      });
    }
  }

  private void updateStatus() {
    saveSelectedSamples();
    view.navigateTo(SampleStatusView.VIEW_NAME);
  }

  private void transfer() {
    if (!view.savedContainers().isEmpty()) {
      view.navigateTo(TransferView.VIEW_NAME);
    } else {
      MessageResource resources = view.getResources();
      view.showError(resources.message(NO_CONTAINERS));
    }
  }

  private void digestion() {
    if (!view.savedContainers().isEmpty()) {
      view.navigateTo(DigestionView.VIEW_NAME);
    } else {
      MessageResource resources = view.getResources();
      view.showError(resources.message(NO_CONTAINERS));
    }
  }

  private void enrichment() {
    if (!view.savedContainers().isEmpty()) {
      view.navigateTo(EnrichmentView.VIEW_NAME);
    } else {
      MessageResource resources = view.getResources();
      view.showError(resources.message(NO_CONTAINERS));
    }
  }

  private void dilution() {
    if (!view.savedContainers().isEmpty()) {
      view.navigateTo(DilutionView.VIEW_NAME);
    } else {
      MessageResource resources = view.getResources();
      view.showError(resources.message(NO_CONTAINERS));
    }
  }

  SubmissionWebFilter getFilter() {
    return filter;
  }
}
