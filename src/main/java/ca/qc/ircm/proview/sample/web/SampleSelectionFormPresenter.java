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

import static ca.qc.ircm.proview.sample.QControl.control;
import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.tube.QTube.tube;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlService;
import ca.qc.ircm.proview.sample.ControlType;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.GridSortOrderBuilder;
import com.vaadin.ui.Grid.SelectionMode;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
  private SampleSelectionForm view;
  private List<Sample> selectedSamples = new ArrayList<>();
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
    final MessageResource resources = view.getResources();
    final Locale locale = view.getLocale();
    view.samplesPanel.addStyleName(SAMPLES_PANEL);
    view.samplesPanel.setCaption(resources.message(SAMPLES_PANEL));
    view.samplesGrid.addStyleName(SAMPLES);
    view.samplesGrid.addColumn(Sample::getName).setId(NAME).setCaption(resources.message(NAME));
    view.samplesGrid.addColumn(sample -> sample.getSubmission().getExperience()).setId(EXPERIENCE)
        .setCaption(resources.message(EXPERIENCE));
    view.samplesGrid.addColumn(sample -> sample.getStatus().getLabel(locale)).setId(STATUS)
        .setCaption(resources.message(STATUS));
    view.samplesGrid.setSelectionMode(SelectionMode.MULTI);
    view.samplesGrid.setFrozenColumnCount(1);
    view.samplesGrid.setSortOrder(new GridSortOrderBuilder<SubmissionSample>()
        .thenAsc(view.samplesGrid.getColumn(EXPERIENCE)).thenAsc(view.samplesGrid.getColumn(NAME)));
    view.controlsPanel.addStyleName(CONTROLS_PANEL);
    view.controlsPanel.setCaption(resources.message(CONTROLS_PANEL));
    view.controlsGrid.addStyleName(CONTROLS);
    view.controlsGrid.setItems(controlService.all());
    view.controlsGrid.addColumn(Sample::getName).setId(NAME).setCaption(resources.message(NAME));
    view.controlsGrid
        .addColumn(control -> control.getControlType() != null
            ? control.getControlType().getLabel(locale) : ControlType.getNullLabel(locale))
        .setId(CONTROL_TYPE).setCaption(resources.message(CONTROL_TYPE));
    view.controlsGrid.addColumn(control -> control.getOriginalContainer().getName())
        .setId(ORIGINAL_CONTAINER_NAME).setCaption(resources.message(ORIGINAL_CONTAINER_NAME));
    view.controlsGrid.setSelectionMode(SelectionMode.MULTI);
    view.controlsGrid.setFrozenColumnCount(1);
    view.controlsGrid.sort(NAME);
    view.selectButton.addStyleName(SELECT);
    view.selectButton.setCaption(resources.message(SELECT));
    view.clearButton.addStyleName(CLEAR);
    view.clearButton.setCaption(resources.message(CLEAR));
  }

  private void addListeners() {
    view.selectButton.addClickListener(e -> selectSamples());
    view.clearButton.addClickListener(e -> clearSamples());
  }

  private void updateSamples() {
    view.samplesGrid.deselectAll();
    view.samplesGrid
        .setItems(selectedSamples.stream().filter(sample -> sample instanceof SubmissionSample)
            .map(sample -> (SubmissionSample) sample)
            .flatMap(sample -> sample.getSubmission().getSamples().stream()).distinct());
    view.samplesGrid.setSortOrder(view.samplesGrid.getSortOrder());
    selectedSamples.stream().filter(sample -> sample instanceof SubmissionSample)
        .map(sample -> (SubmissionSample) sample)
        .forEach(sample -> view.samplesGrid.select(sample));
    selectedSamples.stream().filter(sample -> sample instanceof Control)
        .map(sample -> (Control) sample).forEach(sample -> view.controlsGrid.select(sample));
  }

  private void selectSamples() {
    selectedSamples.clear();
    selectedSamples.addAll(view.samplesGrid.getSelectedItems());
    selectedSamples.addAll(view.controlsGrid.getSelectedItems());
    updateSamples();
    view.fireSaveEvent(selectedSamples);
  }

  private void clearSamples() {
    selectedSamples.clear();
    updateSamples();
    view.fireSaveEvent(selectedSamples);
  }

  public List<Sample> getSelectedSamples() {
    return selectedSamples;
  }

  /**
   * Sets selected samples.
   *
   * @param samples
   *          selected samples
   */
  public void setSelectedSamples(List<Sample> samples) {
    selectedSamples = new ArrayList<>(samples);
    if (view != null) {
      updateSamples();
    }
  }
}
