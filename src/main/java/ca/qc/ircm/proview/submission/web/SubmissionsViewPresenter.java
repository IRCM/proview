package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;

import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFilterBuilder;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.submission.SubmissionService.Report;
import ca.qc.ircm.proview.utils.web.StringToInstantConverter;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.themes.ValoTheme;
import de.datenhahn.vaadin.componentrenderer.ComponentCellKeyExtension;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

/**
 * Submissions view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionsViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER_ID = "header";
  public static final String SUBMISSIONS_PROPERTY = "submissions";
  public static final String SUBMISSIONS_PROPERTY_G = "submissionsG";
  public static final String SAMPLE_COUNT_PROPERTY = "sampleCount";
  public static final String SELECT_PROPERTY = "select";
  public static final String EXPERIMENT_PROPERTY =
      submissionSample.experience.getMetadata().getName();
  public static final String EXPERIMENT_GOAL_PROPERTY =
      submissionSample.goal.getMetadata().getName();
  public static final String SAMPLE_NAME_PROPERTY = submissionSample.name.getMetadata().getName();
  public static final String SAMPLE_STATUS_PROPERTY =
      submissionSample.status.getMetadata().getName();
  public static final String SUBMISSION_PROPERTY = submission.getMetadata().getName();
  public static final String DATE_PROPERTY =
      SUBMISSION_PROPERTY + "." + submission.submissionDate.getMetadata().getName();
  public static final String LINKED_TO_RESULTS_PROPERTY = "results";
  public static final Object[] columns = new Object[] { SELECT_PROPERTY, EXPERIMENT_PROPERTY,
      SAMPLE_COUNT_PROPERTY, SAMPLE_NAME_PROPERTY, EXPERIMENT_GOAL_PROPERTY, SAMPLE_STATUS_PROPERTY,
      DATE_PROPERTY, LINKED_TO_RESULTS_PROPERTY };
  private static final Logger logger = LoggerFactory.getLogger(SubmissionsViewPresenter.class);
  private SubmissionsView view;
  private BeanItemContainer<SubmissionSample> submissionsContainer =
      new BeanItemContainer<>(SubmissionSample.class);
  private GeneratedPropertyContainer submissionsGeneratedContainer =
      new GeneratedPropertyContainer(submissionsContainer);
  private Map<Object, CheckBox> selectionCheckboxes = new HashMap<>();
  @Inject
  private SubmissionService submissionService;
  private Report report;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(SubmissionsView view) {
    this.view = view;
    setIds();
    prepareSumissionsContainer();
    prepareSubmissionsGrid();
    setCaptions();
    setDefaults();
  }

  private void setIds() {
    view.headerLabel.setId(HEADER_ID);
    view.submissionsGrid.setId(SUBMISSIONS_PROPERTY);
  }

  @SuppressWarnings("serial")
  private void prepareSumissionsContainer() {
    submissionsContainer.addNestedContainerProperty(DATE_PROPERTY);
    submissionsGeneratedContainer.addGeneratedProperty(SELECT_PROPERTY,
        new PropertyValueGenerator<CheckBox>() {
          @Override
          public CheckBox getValue(Item item, Object itemId, Object propertyId) {
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
            return checkbox;
          }

          @Override
          public Class<CheckBox> getType() {
            return CheckBox.class;
          }
        });
    submissionsGeneratedContainer.addGeneratedProperty(EXPERIMENT_PROPERTY,
        new PropertyValueGenerator<Button>() {
          @Override
          public Button getValue(Item item, Object itemId, Object propertyId) {
            SubmissionSample sample = (SubmissionSample) itemId;
            Button button = new Button();
            button.setCaption(sample.getExperience());
            button.addClickListener(e -> openSubmission(sample.getSubmission()));
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
        });
    submissionsGeneratedContainer.addGeneratedProperty(SAMPLE_COUNT_PROPERTY,
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
    submissionsGeneratedContainer.addGeneratedProperty(LINKED_TO_RESULTS_PROPERTY,
        new PropertyValueGenerator<Button>() {
          @Override
          public Button getValue(Item item, Object itemId, Object propertyId) {
            SubmissionSample sample = (SubmissionSample) itemId;
            MessageResource resources = view.getResources();
            boolean value = report.getLinkedToResults().get(sample.getSubmission());
            Button button = new Button();
            button.setCaption(resources.message(LINKED_TO_RESULTS_PROPERTY + "." + value));
            if (value) {
              button.addClickListener(e -> openSubmissionResults(sample.getSubmission()));
            } else {
              button.setStyleName(ValoTheme.BUTTON_BORDERLESS);
              button.addStyleName("condition-false");
            }
            return button;
          }

          @Override
          public Class<Button> getType() {
            return Button.class;
          }
        });
  }

  private void prepareSubmissionsGrid() {
    ComponentCellKeyExtension.extend(view.submissionsGrid);
    view.submissionsGrid.setContainerDataSource(submissionsGeneratedContainer);
    view.submissionsGrid.setColumns(columns);
    view.submissionsGrid.getColumn(SELECT_PROPERTY).setWidth(56);
    view.submissionsGrid.getColumn(SELECT_PROPERTY).setRenderer(new ComponentRenderer());
    view.submissionsGrid.getColumn(EXPERIMENT_PROPERTY).setRenderer(new ComponentRenderer());
    view.submissionsGrid.getColumn(DATE_PROPERTY)
        .setConverter(new StringToInstantConverter(DateTimeFormatter.ISO_LOCAL_DATE));
    view.submissionsGrid.getColumn(LINKED_TO_RESULTS_PROPERTY).setRenderer(new ComponentRenderer());
    view.submissionsGrid.setSelectionMode(SelectionMode.MULTI);
    view.submissionsGrid.addStyleName("hide-selection");
    view.submissionsGrid.addStyleName("components");
    view.submissionsGrid.addSelectionListener(e -> {
      Set<Object> itemIds = e.getSelected();
      for (Map.Entry<Object, CheckBox> checkboxEntry : selectionCheckboxes.entrySet()) {
        CheckBox checkbox = checkboxEntry.getValue();
        checkbox.setValue(itemIds.contains(checkboxEntry.getKey()));
      }
    });
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE));
    view.headerLabel.setValue(resources.message(HEADER_ID));
    for (Column column : view.submissionsGrid.getColumns()) {
      column.setHeaderCaption(resources.message((String) column.getPropertyId()));
    }
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
    view.submissionsGrid.sort(DATE_PROPERTY, SortDirection.DESCENDING);
  }

  private void openSubmission(Submission submission) {
    logger.debug("openSubmission {}", submission);
  }

  private void openSubmissionResults(Submission submission) {
    logger.debug("openSubmissionResults {}", submission);
  }
}
