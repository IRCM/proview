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

import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.web.SampleSelectionWindow;
import ca.qc.ircm.proview.sample.web.SampleStatusGenerator;
import ca.qc.ircm.proview.sample.web.SampleStatusView;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.submission.SubmissionService.Report;
import ca.qc.ircm.proview.web.converter.StringToInstantConverter;
import ca.qc.ircm.proview.web.filter.CutomNullPropertyFilterValueChangeListener;
import ca.qc.ircm.proview.web.filter.FilterEqualsChangeListener;
import ca.qc.ircm.proview.web.filter.FilterInstantComponent;
import ca.qc.ircm.proview.web.filter.FilterInstantComponentPresenter;
import ca.qc.ircm.proview.web.filter.FilterRangeChangeListener;
import ca.qc.ircm.proview.web.filter.FilterTextChangeListener;
import ca.qc.ircm.proview.web.filter.FunctionFilter;
import ca.qc.ircm.proview.web.filter.GeneratedPropertyContainerFilter;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.UnsupportedFilterException;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import de.datenhahn.vaadin.componentrenderer.ComponentCellKeyExtension;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

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
  public static final String EXPERIENCE =
      SUBMISSION + "." + submission.experience.getMetadata().getName();
  public static final String EXPERIENCE_GOAL =
      SUBMISSION + "." + submission.goal.getMetadata().getName();
  public static final String SAMPLE_NAME = submissionSample.name.getMetadata().getName();
  public static final String SAMPLE_STATUS = submissionSample.status.getMetadata().getName();
  public static final String DATE =
      SUBMISSION + "." + submission.submissionDate.getMetadata().getName();
  public static final String LINKED_TO_RESULTS = "results";
  public static final String ALL = "all";
  public static final String SELECT_SAMPLES = "selectSamples";
  public static final String SELECT_SAMPLES_LABEL = "selectSamplesLabel";
  public static final String UPDATE_STATUS = "updateStatus";
  public static final Object[] columns = new Object[] { EXPERIENCE, SAMPLE_COUNT, SAMPLE_NAME,
      EXPERIENCE_GOAL, SAMPLE_STATUS, DATE, LINKED_TO_RESULTS };
  public static final String COMPONENTS = "components";
  public static final String CONDITION_FALSE = "condition-false";
  private static final Logger logger = LoggerFactory.getLogger(SubmissionsViewPresenter.class);
  private SubmissionsView view;
  private BeanItemContainer<SubmissionSample> submissionsContainer =
      new BeanItemContainer<>(SubmissionSample.class);
  private GeneratedPropertyContainer submissionsGeneratedContainer =
      new GeneratedPropertyContainer(submissionsContainer);
  private Object nullId = -1;
  private Report report;
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
        .setCaption(resources.message(SELECT_SAMPLES_LABEL, view.savedSamples().size()));
    view.updateStatusButton.addStyleName(UPDATE_STATUS);
    view.updateStatusButton.setCaption(resources.message(UPDATE_STATUS));
    view.updateStatusButton.setVisible(authorizationService.hasAdminRole());
  }

  @SuppressWarnings("serial")
  private void prepareSumissionsGrid() {
    MessageResource resources = view.getResources();
    Locale locale = view.getLocale();
    submissionsContainer.addNestedContainerProperty(EXPERIENCE);
    submissionsContainer.addNestedContainerProperty(EXPERIENCE_GOAL);
    submissionsContainer.addNestedContainerProperty(DATE);
    submissionsGeneratedContainer.addGeneratedProperty(EXPERIENCE,
        new PropertyValueGenerator<Button>() {
          @Override
          public Button getValue(Item item, Object itemId, Object propertyId) {
            SubmissionSample sample = (SubmissionSample) itemId;
            Button button = new Button();
            button.setCaption(sample.getSubmission().getExperience());
            button.addClickListener(e -> viewSubmission(sample.getSubmission()));
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
    submissionsGeneratedContainer.addGeneratedProperty(SAMPLE_COUNT,
        new PropertyValueGenerator<Integer>() {
          @Override
          public Integer getValue(Item item, Object itemId, Object propertyId) {
            SubmissionSample sample = (SubmissionSample) itemId;
            return sample.getSubmission().getSamples().size();
          }

          @Override
          public Class<Integer> getType() {
            return Integer.class;
          }
        });
    submissionsGeneratedContainer.addGeneratedProperty(SAMPLE_STATUS,
        new SampleStatusGenerator(() -> view.getLocale()));
    submissionsGeneratedContainer.addGeneratedProperty(LINKED_TO_RESULTS,
        new PropertyValueGenerator<Button>() {
          @Override
          public Button getValue(Item item, Object itemId, Object propertyId) {
            SubmissionSample sample = (SubmissionSample) itemId;
            MessageResource resources = view.getResources();
            boolean value = report.getLinkedToResults().get(sample.getSubmission());
            Button button = new Button();
            button.setCaption(resources.message(LINKED_TO_RESULTS + "." + value));
            if (value) {
              button.addClickListener(e -> viewSubmissionResults(sample.getSubmission()));
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
          public Filter modifyFilter(Filter filter) throws UnsupportedFilterException {
            return new GeneratedPropertyContainerFilter(filter, submissionsGeneratedContainer);
          }
        });
    view.submissionsGrid.addStyleName(SUBMISSIONS);
    ComponentCellKeyExtension.extend(view.submissionsGrid);
    view.submissionsGrid.setContainerDataSource(submissionsGeneratedContainer);
    view.submissionsGrid.setColumns(columns);
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
      } else if (propertyId.equals(SAMPLE_STATUS)) {
        ComboBox filter = createFilterComboBox(propertyId, resources, SampleStatus.values());
        filter.addValueChangeListener(
            new FilterEqualsChangeListener(submissionsGeneratedContainer, propertyId, nullId));
        for (SampleStatus value : SampleStatus.values()) {
          filter.setItemCaption(value, value.getLabel(locale));
        }
        cell.setComponent(filter);
      } else if (propertyId.equals(DATE)) {
        cell.setComponent(createFilterInstantComponent(propertyId));
      } else if (propertyId.equals(LINKED_TO_RESULTS)) {
        Boolean[] values = new Boolean[] { true, false };
        ComboBox filter = createFilterComboBox(propertyId, resources, values);
        filter
            .addValueChangeListener(new ResultsFilterChangeListener(submissionsGeneratedContainer));
        for (Boolean value : values) {
          filter.setItemCaption(value, resources.message(LINKED_TO_RESULTS + "." + value));
        }
        cell.setComponent(filter);
      }
    }
  }

  private TextField createFilterTextField(Object propertyId, MessageResource resources) {
    TextField filter = new TextField();
    filter.addTextChangeListener(
        new FilterTextChangeListener(submissionsGeneratedContainer, propertyId, true, false));
    filter.setWidth("100%");
    filter.addStyleName("tiny");
    filter.setInputPrompt(resources.message(ALL));
    return filter;
  }

  private ComboBox createFilterComboBox(Object propertyId, MessageResource resources,
      Object[] values) {
    ComboBox filter = new ComboBox();
    filter.setNullSelectionAllowed(false);
    filter.setTextInputAllowed(false);
    filter.addItem(nullId);
    filter.setItemCaption(nullId, resources.message(ALL));
    for (Object value : values) {
      filter.addItem(value);
    }
    filter.select(nullId);
    filter.setWidth("100%");
    filter.addStyleName("tiny");
    return filter;
  }

  private FilterInstantComponent createFilterInstantComponent(Object propertyId) {
    FilterInstantComponentPresenter presenter = filterInstantComponentPresenterProvider.get();
    FilterInstantComponent filter = new FilterInstantComponent();
    presenter.init(filter);
    presenter.getRangeProperty().addValueChangeListener(
        new FilterRangeChangeListener(submissionsGeneratedContainer, propertyId));
    filter.setWidth("100%");
    filter.addStyleName("tiny");
    return filter;
  }

  private void addListeners() {
    view.updateStatusButton.addClickListener(e -> updateStatus());
    view.selectSamplesButton.addClickListener(e -> selectSamples());
  }

  private void searchSubmissions() {
    report = submissionService.report();
    submissionsContainer.removeAllItems();
    report.getSubmissions().stream().map(s -> s.getSamples().get(0))
        .forEach(s -> submissionsContainer.addItem(s));
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
    List<Sample> samples;
    if (!view.submissionsGrid.getSelectedRows().isEmpty()) {
      samples = view.submissionsGrid.getSelectedRows().stream()
          .map(o -> ((SubmissionSample) o).getSubmission()).flatMap(s -> s.getSamples().stream())
          .collect(Collectors.toList());
    } else {
      samples = view.savedSamples();
    }
    window.setSelectedSamples(samples);
    window.center();
    window.selectedSamplesProperty().addValueChangeListener(e -> {
      window.close();
      MessageResource resources = view.getResources();
      @SuppressWarnings("unchecked")
      List<Sample> selectedSamples = (List<Sample>) e.getProperty().getValue();
      view.submissionsGrid.deselectAll();
      view.saveSamples(selectedSamples);
      view.selectedSamplesLabel
          .setCaption(resources.message(SELECT_SAMPLES_LABEL, selectedSamples.size()));
      logger.debug("Selected samples {}", selectedSamples);
    });
    view.addWindow(window);
  }

  private void updateStatus() {
    if (!view.submissionsGrid.getSelectedRows().isEmpty()) {
      List<Submission> submissions = view.submissionsGrid.getSelectedRows().stream()
          .map(sample -> ((SubmissionSample) sample).getSubmission()).collect(Collectors.toList());
      view.saveSamples(
          submissions.stream().flatMap(s -> s.getSamples().stream()).collect(Collectors.toList()));
    }
    view.navigateTo(SampleStatusView.VIEW_NAME);
  }

  private class ResultsFilterChangeListener extends CutomNullPropertyFilterValueChangeListener {
    private static final long serialVersionUID = 2301952986512937369L;

    private ResultsFilterChangeListener(Container.Filterable container) {
      super(container, LINKED_TO_RESULTS, nullId);
    }

    @Override
    public void addNonNullFilter(Object propertyValue) {
      container.addContainerFilter(
          new FunctionFilter(LINKED_TO_RESULTS, propertyValue, (itemId, item) -> {
            SubmissionSample sample = (SubmissionSample) itemId;
            return report.getLinkedToResults().get(sample.getSubmission());
          }));
    }
  }
}
