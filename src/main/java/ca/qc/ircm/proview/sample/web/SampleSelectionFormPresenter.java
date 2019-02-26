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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.ControlProperties.CONTROL_TYPE;
import static ca.qc.ircm.proview.sample.SampleProperties.NAME;
import static ca.qc.ircm.proview.sample.SampleProperties.ORIGINAL_CONTAINER;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.SUBMISSION;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.BANNED;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlService;
import ca.qc.ircm.proview.sample.ControlType;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.SubmissionProperties;
import ca.qc.ircm.proview.tube.TubeProperties;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.ComponentRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

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
  public static final String SAMPLES_LAST_CONTAINER = "lastContainer";
  public static final String ORIGINAL_CONTAINER_NAME =
      property(ORIGINAL_CONTAINER, TubeProperties.NAME);
  public static final String UPDATE = "update";
  public static final String EXPERIMENT = property(SUBMISSION, SubmissionProperties.EXPERIMENT);
  public static final String SELECT = "select";
  public static final String CLEAR = "clear";
  private SampleSelectionForm view;
  private SampleSelectionFormDesign design;
  private List<Sample> selectedSamples = new ArrayList<>();
  private ListDataProvider<Control> controlsDataProvider = DataProvider.ofItems();
  @Inject
  private SampleContainerService sampleContainerService;
  @Inject
  private ControlService controlService;

  protected SampleSelectionFormPresenter() {
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(SampleSelectionForm view) {
    this.view = view;
    design = view.design;
    prepareComponents();
    addListeners();
    updateSamples();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    final Locale locale = view.getLocale();
    design.samplesPanel.addStyleName(SAMPLES_PANEL);
    design.samplesPanel.setCaption(resources.message(SAMPLES_PANEL));
    design.samplesGrid.addStyleName(SAMPLES);
    design.samplesGrid.addColumn(Sample::getName).setId(NAME).setCaption(resources.message(NAME));
    design.samplesGrid.addColumn(sample -> sample.getSubmission().getExperiment()).setId(EXPERIMENT)
        .setCaption(resources.message(EXPERIMENT));
    design.samplesGrid.addColumn(sample -> sample.getStatus().getLabel(locale)).setId(STATUS)
        .setCaption(resources.message(STATUS));
    design.samplesGrid.addColumn(sample -> sampleContainerService.last(sample).getFullName())
        .setId(SAMPLES_LAST_CONTAINER).setCaption(resources.message(SAMPLES_LAST_CONTAINER))
        .setStyleGenerator(sample -> sampleContainerService.last(sample).isBanned() ? BANNED : "");
    design.samplesGrid.setSelectionMode(SelectionMode.MULTI);
    design.samplesGrid.setFrozenColumnCount(1);
    design.samplesGrid.setSortOrder(GridSortOrder.asc(design.samplesGrid.getColumn(EXPERIMENT))
        .thenAsc(design.samplesGrid.getColumn(NAME)));
    design.controlsPanel.addStyleName(CONTROLS_PANEL);
    design.controlsPanel.setCaption(resources.message(CONTROLS_PANEL));
    design.controlsGrid.addStyleName(CONTROLS);
    design.controlsGrid.setDataProvider(controlsDataProvider);
    controlsDataProvider.getItems().addAll(controlService.all());
    controlsDataProvider.refreshAll();
    design.controlsGrid.addColumn(Sample::getName).setId(NAME).setCaption(resources.message(NAME));
    design.controlsGrid
        .addColumn(
            control -> control.getControlType() != null ? control.getControlType().getLabel(locale)
                : ControlType.getNullLabel(locale))
        .setId(CONTROL_TYPE).setCaption(resources.message(CONTROL_TYPE));
    design.controlsGrid.addColumn(control -> control.getOriginalContainer().getName())
        .setId(ORIGINAL_CONTAINER_NAME).setCaption(resources.message(ORIGINAL_CONTAINER_NAME));
    design.controlsGrid.addColumn(control -> updateButton(control), new ComponentRenderer())
        .setId(UPDATE).setSortable(false);
    design.controlsGrid.setSelectionMode(SelectionMode.MULTI);
    design.controlsGrid.setFrozenColumnCount(1);
    design.controlsGrid.sort(NAME);
    design.selectButton.addStyleName(SELECT);
    design.selectButton.setCaption(resources.message(SELECT));
    design.clearButton.addStyleName(CLEAR);
    design.clearButton.setCaption(resources.message(CLEAR));
  }

  private void addListeners() {
    design.selectButton.addClickListener(e -> selectSamples());
    design.clearButton.addClickListener(e -> clearSamples());
  }

  private void updateSamples() {
    design.samplesGrid.deselectAll();
    design.samplesGrid
        .setItems(selectedSamples.stream().filter(sample -> sample instanceof SubmissionSample)
            .map(sample -> (SubmissionSample) sample)
            .flatMap(sample -> sample.getSubmission().getSamples().stream()).distinct());
    design.samplesGrid.setSortOrder(design.samplesGrid.getSortOrder());
    selectedSamples.stream().filter(sample -> sample instanceof SubmissionSample)
        .map(sample -> (SubmissionSample) sample)
        .forEach(sample -> design.samplesGrid.select(sample));
    design.controlsGrid.deselectAll();
    Map<Long, Control> controlsMap = controlsDataProvider.getItems().stream()
        .collect(Collectors.toMap(sample -> sample.getId(), sample -> sample));
    selectedSamples.stream().filter(sample -> controlsMap.containsKey(sample.getId()))
        .forEach(sample -> design.controlsGrid.select(controlsMap.get(sample.getId())));
  }

  private Button updateButton(Control control) {
    final MessageResource resources = view.getResources();
    Button button = new Button();
    button.setCaption(resources.message(UPDATE));
    button.addClickListener(e -> {
      view.navigateTo(ControlView.VIEW_NAME, String.valueOf(control.getId()));
    });
    return button;
  }

  private void selectSamples() {
    selectedSamples.clear();
    selectedSamples.addAll(design.samplesGrid.getSelectedItems());
    selectedSamples.addAll(design.controlsGrid.getSelectedItems());
    updateSamples();
    view.fireSaveEvent(selectedSamples);
  }

  private void clearSamples() {
    selectedSamples.clear();
    updateSamples();
    view.fireSaveEvent(selectedSamples);
  }

  List<Sample> getItems() {
    return selectedSamples;
  }

  void setItems(List<Sample> samples) {
    selectedSamples = new ArrayList<>(samples);
    if (view != null) {
      updateSamples();
    }
  }
}
