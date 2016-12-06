package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.web.WebConstants.GENERAL_MESSAGES;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.SelectionMode;
import de.datenhahn.vaadin.componentrenderer.ComponentCellKeyExtension;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Updates sample statuses presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SampleStatusViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String SAMPLES = "samples";
  public static final String SAVE = "save";
  public static final String NAME = submissionSample.name.getMetadata().getName();
  public static final String STATUS = submissionSample.status.getMetadata().getName();
  public static final String SUBMISSION = submission.getMetadata().getName();
  public static final String EXPERIENCE =
      SUBMISSION + "." + submission.experience.getMetadata().getName();
  public static final String NEW_STATUS = "newStatus";
  public static final String COMPONENTS = "components";
  public static final Object[] samplesColumns =
      new Object[] { NAME, EXPERIENCE, STATUS, NEW_STATUS };
  private static final Logger logger = LoggerFactory.getLogger(SampleStatusViewPresenter.class);
  private SampleStatusView view;
  private BeanItemContainer<SubmissionSample> samplesContainer =
      new BeanItemContainer<>(SubmissionSample.class);
  private GeneratedPropertyContainer samplesGridContainer =
      new GeneratedPropertyContainer(samplesContainer);
  @Value("${spring.application.name}")
  private String applicationName;

  protected SampleStatusViewPresenter() {
  }

  protected SampleStatusViewPresenter(String applicationName) {
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(SampleStatusView view) {
    logger.debug("Update sample status view");
    this.view = view;
    prepareComponents();
    addListeners();
  }

  @SuppressWarnings("serial")
  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.setValue(resources.message(HEADER));
    samplesContainer.addNestedContainerProperty(EXPERIENCE);
    samplesGridContainer.addGeneratedProperty(NEW_STATUS, new PropertyValueGenerator<ComboBox>() {
      @Override
      public ComboBox getValue(Item item, Object itemId, Object propertyId) {
        SubmissionSample sample = (SubmissionSample) itemId;
        ComboBox statuses = statusesComboBox();
        statuses.setValue(sample.getStatus());
        return statuses;
      }

      @Override
      public Class<ComboBox> getType() {
        return ComboBox.class;
      }
    });
    view.samplesGrid.addStyleName(SAMPLES);
    view.samplesGrid.addStyleName(COMPONENTS);
    ComponentCellKeyExtension.extend(view.samplesGrid);
    view.samplesGrid.setContainerDataSource(samplesGridContainer);
    view.samplesGrid.setSelectionMode(SelectionMode.MULTI);
    view.samplesGrid.setColumns(samplesColumns);
    for (Object propertyId : samplesColumns) {
      view.samplesGrid.getColumn(propertyId)
          .setHeaderCaption(resources.message((String) propertyId));
    }
    view.samplesGrid.getColumn(NEW_STATUS).setRenderer(new ComponentRenderer());
    view.saveButton.addStyleName(SAVE);
    view.saveButton.setCaption(resources.message(SAVE));
  }

  private void addListeners() {
    view.saveButton.addClickListener(e -> save());
  }

  private ComboBox statusesComboBox() {
    final MessageResource generalResources =
        new MessageResource(GENERAL_MESSAGES, view.getLocale());
    Locale locale = view.getLocale();
    ComboBox statuses = new ComboBox();
    statuses.setNewItemsAllowed(false);
    for (SampleStatus status : SampleStatus.values()) {
      statuses.addItem(status);
      statuses.setItemCaption(status, status.getLabel(locale));
    }
    statuses.setRequired(true);
    statuses.setRequiredError(generalResources.message(REQUIRED));
    return statuses;
  }

  private void save() {
    logger.debug("Save button clicked");
  }

  /**
   * Called by view when entered.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    logger.trace("Recovering samples from session");
    Collection<Submission> submissions = view.savedSubmissions();
    List<SubmissionSample> samples = submissions.stream()
        .flatMap(submission -> submission.getSamples().stream()).collect(Collectors.toList());
    samplesContainer.removeAllItems();
    samplesContainer.addAll(samples);
  }
}
