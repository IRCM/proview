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

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.web.SampleStatusView;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFilterBuilder;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.submission.SubmissionService.Report;
import ca.qc.ircm.proview.utils.web.CutomNullPropertyFilterValueChangeListener;
import ca.qc.ircm.proview.utils.web.FilterEqualsChangeListener;
import ca.qc.ircm.proview.utils.web.FilterInstantComponent;
import ca.qc.ircm.proview.utils.web.FilterInstantComponentPresenter;
import ca.qc.ircm.proview.utils.web.FilterRangeChangeListener;
import ca.qc.ircm.proview.utils.web.FilterTextChangeListener;
import ca.qc.ircm.proview.utils.web.FunctionFilter;
import ca.qc.ircm.proview.utils.web.GeneratedPropertyContainerFilter;
import ca.qc.ircm.proview.utils.web.StringToInstantConverter;
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
import com.vaadin.ui.CheckBox;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
  public static final String SELECT = "select";
  public static final String SUBMISSION = submission.getMetadata().getName();
  public static final String EXPERIENCE =
      SUBMISSION + "." + submission.experience.getMetadata().getName();
  public static final String EXPERIENCE_GOAL =
      SUBMISSION + "." + submission.goal.getMetadata().getName();
  public static final String SAMPLE_NAME = submissionSample.name.getMetadata().getName();
  public static final String SAMPLE_STATUS =
      submissionSample.status.getMetadata().getName();
  public static final String DATE =
      SUBMISSION + "." + submission.submissionDate.getMetadata().getName();
  public static final String LINKED_TO_RESULTS = "results";
  public static final String ALL = "all";
  public static final String UPDATE_STATUS = "updateStatus";
  public static final Object[] columns = new Object[] { SELECT, EXPERIENCE,
      SAMPLE_COUNT, SAMPLE_NAME, EXPERIENCE_GOAL, SAMPLE_STATUS,
      DATE, LINKED_TO_RESULTS };
  public static final String HIDE_SELECTION = "hide-selection";
  public static final String COMPONENTS = "components";
  public static final String CONDITION_FALSE = "condition-false";
  private static final Logger logger = LoggerFactory.getLogger(SubmissionsViewPresenter.class);
  private SubmissionsView view;
  private BeanItemContainer<SubmissionSample> submissionsContainer =
      new BeanItemContainer<>(SubmissionSample.class);
  private GeneratedPropertyContainer submissionsGeneratedContainer =
      new GeneratedPropertyContainer(submissionsContainer);
  private Map<Object, CheckBox> selectionCheckboxes = new HashMap<>();
  private Object nullId = -1;
  Report report;
  @Inject
  private SubmissionService submissionService;
  @Inject
  private Provider<FilterInstantComponentPresenter> filterInstantComponentPresenterProvider;
  @Inject
  private Provider<SubmissionWindow> submissionWindowProvider;
  @Inject
  private Provider<SubmissionAnalysesWindow> submissionAnalysesWindowProvider;
  @Value("${spring.application.name}")
  private String applicationName;

  protected SubmissionsViewPresenter() {
  }

  protected SubmissionsViewPresenter(SubmissionService submissionService,
      Provider<FilterInstantComponentPresenter> filterInstantComponentPresenterProvider,
      Provider<SubmissionWindow> submissionWindowProvider,
      Provider<SubmissionAnalysesWindow> submissionAnalysesWindowProvider, String applicationName) {
    this.submissionService = submissionService;
    this.filterInstantComponentPresenterProvider = filterInstantComponentPresenterProvider;
    this.submissionWindowProvider = submissionWindowProvider;
    this.submissionAnalysesWindowProvider = submissionAnalysesWindowProvider;
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
    setDefaults();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.setValue(resources.message(HEADER));
    prepareSumissionsGrid();
    view.updateStatusButton.addStyleName(UPDATE_STATUS);
    view.updateStatusButton.setCaption(resources.message(UPDATE_STATUS));
  }

  @SuppressWarnings("serial")
  private void prepareSumissionsGrid() {
    MessageResource resources = view.getResources();
    submissionsContainer.addNestedContainerProperty(EXPERIENCE);
    submissionsContainer.addNestedContainerProperty(EXPERIENCE_GOAL);
    submissionsContainer.addNestedContainerProperty(DATE);
    submissionsGeneratedContainer.addGeneratedProperty(SELECT,
        new PropertyValueGenerator<CheckBox>() {
          @Override
          public CheckBox getValue(Item item, Object itemId, Object propertyId) {
            SubmissionSample sample = (SubmissionSample) itemId;
            CheckBox checkbox = new CheckBox();
            checkbox.addValueChangeListener(e -> {
              if (checkbox.getValue()) {
                view.submissionsGrid.select(itemId);
              } else {
                view.submissionsGrid.deselect(itemId);
              }
            });
            checkbox.addAttachListener(e -> selectionCheckboxes.put(itemId, checkbox));
            checkbox.setValue(view.submissionsGrid.getSelectedRows().contains(itemId));
            checkbox.setVisible(report.getLinkedToResults().get(sample.getSubmission()));
            return checkbox;
          }

          @Override
          public Class<CheckBox> getType() {
            return CheckBox.class;
          }
        });
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
    view.submissionsGrid.setFrozenColumnCount(2);
    view.submissionsGrid.getColumn(SELECT).setWidth(56);
    view.submissionsGrid.getColumn(SELECT).setRenderer(new ComponentRenderer());
    view.submissionsGrid.getColumn(EXPERIENCE).setRenderer(new ComponentRenderer());
    view.submissionsGrid.getColumn(DATE)
        .setConverter(new StringToInstantConverter(DateTimeFormatter.ISO_LOCAL_DATE));
    view.submissionsGrid.getColumn(LINKED_TO_RESULTS).setRenderer(new ComponentRenderer());
    view.submissionsGrid.setSelectionMode(SelectionMode.MULTI);
    view.submissionsGrid.addStyleName(HIDE_SELECTION);
    view.submissionsGrid.addStyleName(COMPONENTS);
    view.submissionsGrid.addSelectionListener(e -> {
      Set<Object> itemIds = e.getSelected();
      for (Map.Entry<Object, CheckBox> checkboxEntry : selectionCheckboxes.entrySet()) {
        CheckBox checkbox = checkboxEntry.getValue();
        checkbox.setValue(itemIds.contains(checkboxEntry.getKey()));
      }
    });
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
          filter.setItemCaption(value, value.getLabel(view.getLocale()));
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
  }

  private void setDefaults() {
    searchSubmissions();
  }

  private void searchSubmissions() {
    SubmissionFilterBuilder filter = new SubmissionFilterBuilder();
    report = submissionService.report(filter.build());
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

  private void updateStatus() {
    view.saveSubmissions(view.submissionsGrid.getSelectedRows().stream()
        .map(sample -> ((SubmissionSample) sample).getSubmission()).collect(Collectors.toList()));
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
