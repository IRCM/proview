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

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.web.SampleSelectionWindow;
import ca.qc.ircm.proview.sample.web.SampleStatusView;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.submission.SubmissionService.Report;
import ca.qc.ircm.proview.web.v7.converter.StringToInstantConverter;
import ca.qc.ircm.proview.web.v7.filter.FilterEqualsChangeListener;
import ca.qc.ircm.proview.web.v7.filter.FilterInstantComponent;
import ca.qc.ircm.proview.web.v7.filter.FilterInstantComponentPresenter;
import ca.qc.ircm.proview.web.v7.filter.FilterRangeChangeListener;
import ca.qc.ircm.proview.web.v7.filter.FilterTextChangeListener;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.sort.SortOrder;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.data.util.filter.UnsupportedFilterException;
import com.vaadin.v7.ui.Grid.Column;
import com.vaadin.v7.ui.Grid.HeaderCell;
import com.vaadin.v7.ui.Grid.HeaderRow;
import com.vaadin.v7.ui.Grid.SelectionMode;
import de.datenhahn.vaadin.componentrenderer.ComponentCellKeyExtension;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
  public static final String DATE =
      SUBMISSION + "." + submission.submissionDate.getMetadata().getName();
  public static final String LINKED_TO_RESULTS = "results";
  public static final String ALL = "all";
  public static final String SELECT_SAMPLES = "selectSamples";
  public static final String SELECT_SAMPLES_LABEL = "selectSamplesLabel";
  public static final String UPDATE_STATUS = "updateStatus";
  public static final String CONDITION_FALSE = "condition-false";
  private static final Object[] COLUMNS = new Object[] { EXPERIENCE, SAMPLE_COUNT, SAMPLE_NAME,
      EXPERIENCE_GOAL, SAMPLE_STATUSES, DATE, LINKED_TO_RESULTS };
  private static final Logger logger = LoggerFactory.getLogger(SubmissionsViewPresenter.class);
  private SubmissionsView view;
  private BeanItemContainer<SubmissionFirstSample> submissionsContainer =
      new BeanItemContainer<>(SubmissionFirstSample.class);
  private GeneratedPropertyContainer submissionsGeneratedContainer =
      new GeneratedPropertyContainer(submissionsContainer);
  private Object nullId = -1;
  @Inject
  private SubmissionService submissionService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private Provider<FilterInstantComponentPresenter> filterInstantComponentPresenterProvider;
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
      Provider<FilterInstantComponentPresenter> filterInstantComponentPresenterProvider,
      Provider<SubmissionWindow> submissionWindowProvider,
      Provider<SubmissionAnalysesWindow> submissionAnalysesWindowProvider,
      Provider<SampleSelectionWindow> sampleSelectionWindowProvider, String applicationName) {
    this.submissionService = submissionService;
    this.authorizationService = authorizationService;
    this.filterInstantComponentPresenterProvider = filterInstantComponentPresenterProvider;
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
    view.updateStatusButton.addStyleName(UPDATE_STATUS);
    view.updateStatusButton.setCaption(resources.message(UPDATE_STATUS));
    view.updateStatusButton.setVisible(authorizationService.hasAdminRole());
  }

  @SuppressWarnings("serial")
  private void prepareSumissionsGrid() {
    MessageResource resources = view.getResources();
    Locale locale = view.getLocale();
    submissionsContainer.addNestedContainerBean(SAMPLE);
    submissionsContainer.addNestedContainerBean(SUBMISSION);
    submissionsGeneratedContainer.addGeneratedProperty(EXPERIENCE,
        new PropertyValueGenerator<Button>() {
          @Override
          public Button getValue(Item item, Object itemId, Object propertyId) {
            Submission submission = ((SubmissionFirstSample) itemId).submission;
            Button button = new Button();
            button.setCaption(submission.getExperience());
            button.addClickListener(e -> viewSubmission(submission));
            return button;
          }

          @Override
          public Class<Button> getType() {
            return Button.class;
          }

          @Override
          public SortOrder[] getSortProperties(SortOrder order) {
            return new SortOrder[] { order };
          }

          @Override
          public Filter modifyFilter(Filter filter) throws UnsupportedFilterException {
            return filter;
          }
        });
    submissionsGeneratedContainer.addGeneratedProperty(LINKED_TO_RESULTS,
        new PropertyValueGenerator<Button>() {
          @Override
          public Button getValue(Item item, Object itemId, Object propertyId) {
            Submission submission = ((SubmissionFirstSample) itemId).submission;
            boolean results = ((SubmissionFirstSample) itemId).results;
            Button button = new Button();
            button.setCaption(resources.message(LINKED_TO_RESULTS + "." + results));
            if (results) {
              button.addClickListener(e -> viewSubmissionResults(submission));
            } else {
              button.setStyleName(ValoTheme.BUTTON_BORDERLESS);
              button.addStyleName(CONDITION_FALSE);
            }
            return button;
          }

          @Override
          public Class<Button> getType() {
            return Button.class;
          }

          @Override
          public SortOrder[] getSortProperties(SortOrder order) {
            return new SortOrder[] { order };
          }

          @Override
          public Filter modifyFilter(Filter filter) throws UnsupportedFilterException {
            return filter;
          }
        });
    view.submissionsGrid.addStyleName(SUBMISSIONS);
    ComponentCellKeyExtension.extend(view.submissionsGrid);
    view.submissionsGrid.setContainerDataSource(submissionsGeneratedContainer);
    view.submissionsGrid.setColumns(COLUMNS);
    for (Column column : view.submissionsGrid.getColumns()) {
      column.setHeaderCaption(resources.message((String) column.getPropertyId()));
    }
    view.submissionsGrid.setFrozenColumnCount(1);
    view.submissionsGrid.getColumn(EXPERIENCE).setRenderer(new ComponentRenderer());
    view.submissionsGrid.getColumn(DATE)
        .setConverter(new StringToInstantConverter(DateTimeFormatter.ISO_LOCAL_DATE));
    view.submissionsGrid.getColumn(LINKED_TO_RESULTS).setRenderer(new ComponentRenderer());
    if (authorizationService.hasAdminRole()) {
      view.submissionsGrid.setSelectionMode(SelectionMode.MULTI);
    }
    view.submissionsGrid.addStyleName(COMPONENTS);
    HeaderRow filterRow = view.submissionsGrid.appendHeaderRow();
    for (Column column : view.submissionsGrid.getColumns()) {
      Object propertyId = column.getPropertyId();
      HeaderCell cell = filterRow.getCell(propertyId);
      if (propertyId.equals(EXPERIENCE)) {
        cell.setComponent(createFilterTextField(propertyId, resources));
      } else if (propertyId.equals(SAMPLE_COUNT)) {
        // Don't filter sample count.
      } else if (propertyId.equals(SAMPLE_NAME)) {
        cell.setComponent(createFilterTextField(propertyId, resources));
      } else if (propertyId.equals(EXPERIENCE_GOAL)) {
        cell.setComponent(createFilterTextField(propertyId, resources));
      } else if (propertyId.equals(SAMPLE_STATUSES)) {
        List<String> sampleStatusLabels = new ArrayList<>();
        for (SampleStatus status : SampleStatus.values()) {
          sampleStatusLabels.add(status.getLabel(locale));
        }
        ComboBox<String> filter = createFilterComboBox(propertyId, resources, sampleStatusLabels);
        filter.addValueChangeListener(
            new FilterTextChangeListener(submissionsGeneratedContainer, propertyId, true, false));
        cell.setComponent(filter);
      } else if (propertyId.equals(DATE)) {
        cell.setComponent(createFilterInstantComponent(propertyId));
      } else if (propertyId.equals(LINKED_TO_RESULTS)) {
        List<Boolean> values = Arrays.asList(new Boolean[] { true, false });
        ComboBox<Boolean> filter = createFilterComboBox(propertyId, resources, values);
        filter.addValueChangeListener(
            new FilterEqualsChangeListener(submissionsGeneratedContainer, propertyId));
        filter.setItemCaptionGenerator(value -> resources.message(LINKED_TO_RESULTS + "." + value));
        cell.setComponent(filter);
      }
    }
  }

  private TextField createFilterTextField(Object propertyId, MessageResource resources) {
    TextField filter = new TextField();
    filter.addValueChangeListener(
        new FilterTextChangeListener(submissionsGeneratedContainer, propertyId, true, false));
    filter.setWidth("100%");
    filter.addStyleName("tiny");
    filter.setPlaceholder(resources.message(ALL));
    return filter;
  }

  private <V> ComboBox<V> createFilterComboBox(Object propertyId, MessageResource resources,
      Collection<V> values) {
    ComboBox<V> filter = new ComboBox<>();
    filter.setEmptySelectionAllowed(true);
    filter.setTextInputAllowed(false);
    filter.setEmptySelectionCaption(resources.message(ALL));
    filter.setPlaceholder(resources.message(ALL));
    filter.setItems(values);
    filter.setSelectedItem(null);
    filter.setWidth("100%");
    filter.addStyleName(ValoTheme.COMBOBOX_TINY);
    return filter;
  }

  private FilterInstantComponent createFilterInstantComponent(Object propertyId) {
    FilterInstantComponentPresenter presenter = filterInstantComponentPresenterProvider.get();
    FilterInstantComponent filter = new FilterInstantComponent();
    presenter.init(filter);
    presenter.getRangeProperty().addValueChangeListener(
        new FilterRangeChangeListener(submissionsGeneratedContainer, propertyId));
    filter.setWidth("100%");
    filter.addStyleName(ValoTheme.BUTTON_TINY);
    return filter;
  }

  private void addListeners() {
    view.updateStatusButton.addClickListener(e -> updateStatus());
    view.selectSamplesButton.addClickListener(e -> selectSamples());
  }

  private void searchSubmissions() {
    Report report = submissionService.report();
    submissionsContainer.removeAllItems();
    report.getSubmissions().stream()
        .forEach(s -> submissionsContainer.addItem(new SubmissionFirstSample(s, report)));
    view.submissionsGrid.sort(DATE, SortDirection.DESCENDING);
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
    if (!view.submissionsGrid.getSelectedRows().isEmpty()) {
      samples = view.submissionsGrid.getSelectedRows().stream()
          .flatMap(o -> ((SubmissionFirstSample) o).submission.getSamples().stream())
          .collect(Collectors.toList());
    } else {
      samples = view.savedSamples();
    }
    window.setSelectedSamples(samples);
    window.center();
    window.addCloseListener(e -> {
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
    if (!view.submissionsGrid.getSelectedRows().isEmpty()) {
      List<Sample> samples = view.submissionsGrid.getSelectedRows().stream()
          .flatMap(o -> ((SubmissionFirstSample) o).submission.getSamples().stream())
          .collect(Collectors.toList());
      view.saveSamples(samples);
    }
    view.navigateTo(SampleStatusView.VIEW_NAME);
  }

  public static Object[] getColumns() {
    return COLUMNS.clone();
  }

  public class SubmissionFirstSample {
    private Submission submission;
    private SubmissionSample sample;
    private String statuses;
    private int sampleCount;
    private boolean results;

    private SubmissionFirstSample(Submission submission, Report report) {
      this.submission = submission;
      this.sample = submission.getSamples().get(0);
      this.statuses = statuses(submission);
      this.sampleCount = submission.getSamples().size();
      this.results = report.getLinkedToResults().get(submission);
    }

    private String statuses(Submission submission) {
      MessageResource resources = view.getResources();
      List<SampleStatus> statuses = submission.getSamples().stream().map(s -> s.getStatus())
          .distinct().sorted().collect(Collectors.toList());
      String separator = resources.message(SAMPLE_STATUSES + ".separator");
      return statuses.stream().map(s -> s.getLabel(view.getLocale()))
          .collect(Collectors.joining(separator));
    }

    public Submission getSubmission() {
      return submission;
    }

    public void setSubmission(Submission submission) {
      this.submission = submission;
    }

    public SubmissionSample getSample() {
      return sample;
    }

    public void setSample(SubmissionSample sample) {
      this.sample = sample;
    }

    public int getSampleCount() {
      return sampleCount;
    }

    public void setSampleCount(int sampleCount) {
      this.sampleCount = sampleCount;
    }

    public boolean isResults() {
      return results;
    }

    public void setResults(boolean results) {
      this.results = results;
    }

    public String getStatuses() {
      return statuses;
    }

    public void setStatuses(String statuses) {
      this.statuses = statuses;
    }
  }
}
