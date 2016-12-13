package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.QControl.control;
import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.tube.QTube.tube;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.sort.Sort;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Grid.SelectionMode;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Sample selection form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SampleSelectionFormPresenter {
  public static final String SAMPLES_PANEL = "samplesPanel";
  public static final String SAMPLES = "samples";
  public static final String CONTROLS_PANEL = "controlsPanel";
  public static final String CONTROLS = "controls";
  public static final String NAME = sample.name.getMetadata().getName();
  public static final String STATUS = submissionSample.status.getMetadata().getName();
  public static final String CONTROL_TYPE = control.controlType.getMetadata().getName();
  public static final String ORIGINAL_CONTAINER = sample.originalContainer.getMetadata().getName();
  public static final String ORIGINAL_CONTAINER_NAME =
      ORIGINAL_CONTAINER + "." + tube.name.getMetadata().getName();
  public static final String SUBMISSION = submission.getMetadata().getName();
  public static final String EXPERIENCE =
      SUBMISSION + "." + submission.experience.getMetadata().getName();
  public static final String SELECT = "select";
  public static final String CLEAR = "clear";
  public static final Object[] SAMPLES_COLUMNS = new Object[] { NAME, EXPERIENCE, STATUS };
  public static final Object[] CONTROLS_COLUMNS =
      new Object[] { NAME, CONTROL_TYPE, ORIGINAL_CONTAINER_NAME };
  private SampleSelectionForm view;
  private ObjectProperty<List<Sample>> selectedSamples = new ObjectProperty<>(new ArrayList<>());
  private BeanItemContainer<SubmissionSample> samplesContainer =
      new BeanItemContainer<>(SubmissionSample.class);
  private GeneratedPropertyContainer samplesGridContainer =
      new GeneratedPropertyContainer(samplesContainer);
  private BeanItemContainer<Control> controlsContainer = new BeanItemContainer<>(Control.class);
  private GeneratedPropertyContainer controlsGridContainer =
      new GeneratedPropertyContainer(controlsContainer);
  @Inject
  private ControlService controlService;

  protected SampleSelectionFormPresenter() {
  }

  protected SampleSelectionFormPresenter(ControlService controlService) {
    this.controlService = controlService;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(SampleSelectionForm view) {
    this.view = view;
    prepareComponents();
    addListeners();
    updateSamples();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.samplesPanel.addStyleName(SAMPLES_PANEL);
    view.samplesPanel.setCaption(resources.message(SAMPLES_PANEL));
    samplesContainer.addNestedContainerProperty(EXPERIENCE);
    samplesGridContainer.addGeneratedProperty(STATUS,
        new SampleStatusGenerator(() -> view.getLocale()));
    view.samplesGrid.setSizeFull();
    view.samplesGrid.addStyleName(SAMPLES);
    view.samplesGrid.setSelectionMode(SelectionMode.MULTI);
    view.samplesGrid.setContainerDataSource(samplesGridContainer);
    view.samplesGrid.setColumns(SAMPLES_COLUMNS);
    view.samplesGrid.setFrozenColumnCount(1);
    for (Object propertyId : SAMPLES_COLUMNS) {
      view.samplesGrid.getColumn(propertyId)
          .setHeaderCaption(resources.message((String) propertyId));
    }
    view.samplesGrid.sort(Sort.by(EXPERIENCE).then(NAME));
    view.controlsPanel.addStyleName(CONTROLS_PANEL);
    view.controlsPanel.setCaption(resources.message(CONTROLS_PANEL));
    controlsContainer.addNestedContainerProperty(ORIGINAL_CONTAINER_NAME);
    controlsContainer.addAll(controlService.all());
    controlsGridContainer.addGeneratedProperty(CONTROL_TYPE,
        new ControlTypeGenerator(() -> view.getLocale()));
    view.controlsGrid.setSizeFull();
    view.controlsGrid.addStyleName(CONTROLS);
    view.controlsGrid.setSelectionMode(SelectionMode.MULTI);
    view.controlsGrid.setContainerDataSource(controlsGridContainer);
    view.controlsGrid.setColumns(CONTROLS_COLUMNS);
    view.controlsGrid.setFrozenColumnCount(1);
    for (Object propertyId : CONTROLS_COLUMNS) {
      view.controlsGrid.getColumn(propertyId)
          .setHeaderCaption(resources.message((String) propertyId));
    }
    view.controlsGrid.sort(NAME);
    view.selectButton.addStyleName(SELECT);
    view.selectButton.setCaption(resources.message(SELECT));
    view.clearButton.addStyleName(CLEAR);
    view.clearButton.setCaption(resources.message(CLEAR));
  }

  private void addListeners() {
    selectedSamples.addValueChangeListener(e -> updateSamples());
    view.selectButton.addClickListener(e -> selectSamples());
    view.clearButton.addClickListener(e -> clearSamples());
  }

  private void updateSamples() {
    view.samplesGrid.deselectAll();
    samplesContainer.removeAllItems();
    Set<Long> selectedIds = selectedSamples.getValue().stream().map(sample -> sample.getId())
        .collect(Collectors.toSet());
    List<Submission> submissions = selectedSamples.getValue().stream()
        .filter(sample -> sample instanceof SubmissionSample)
        .map(sample -> ((SubmissionSample) sample).getSubmission()).collect(Collectors.toList());
    samplesContainer.addAll(submissions.stream()
        .flatMap(submission -> submission.getSamples().stream()).collect(Collectors.toList()));
    view.samplesGrid.setSortOrder(view.samplesGrid.getSortOrder());
    samplesContainer.getItemIds().stream().map(o -> (Sample) o)
        .filter(s -> selectedIds.contains(s.getId()))
        .forEach(itemId -> view.samplesGrid.select(itemId));
    controlsContainer.getItemIds().stream().map(o -> (Sample) o)
        .filter(s -> selectedIds.contains(s.getId()))
        .forEach(itemId -> view.controlsGrid.select(itemId));
  }

  private void selectSamples() {
    List<Sample> samples = new ArrayList<>();
    samples.addAll(view.samplesGrid.getSelectedRows().stream().map(o -> (Sample) o)
        .collect(Collectors.toList()));
    samples.addAll(view.controlsGrid.getSelectedRows().stream().map(o -> (Sample) o)
        .collect(Collectors.toList()));
    selectedSamples.setValue(samples);
  }

  private void clearSamples() {
    selectedSamples.setValue(new ArrayList<>());
  }

  public ObjectProperty<List<Sample>> selectedSamplesProperty() {
    return selectedSamples;
  }

  public List<Sample> getSelectedSamples() {
    return selectedSamples.getValue();
  }

  public void setSelectedSamples(List<Sample> samples) {
    selectedSamples.setValue(samples);
  }
}
