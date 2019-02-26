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

import static ca.qc.ircm.proview.sample.SampleContainerType.TUBE;
import static ca.qc.ircm.proview.sample.SampleContainerType.WELL;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.NamedComparator;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateFilter;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeComparator;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.renderers.ComponentRenderer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContainerSelectionFormPresenter {
  public static final String TYPE_PANEL = "typePanel";
  public static final String TYPE = "type";
  public static final String TUBES_PANEL = "tubesPanel";
  public static final String TUBES = "tubes";
  public static final String SAMPLE = "sample";
  public static final String CONTAINER_TUBE = "tube";
  public static final String PLATES_PANEL = "platesPanel";
  public static final String PLATES = "plates";
  public static final String PLATE_PANEL = "platePanel";
  public static final String SELECT = "select";
  public static final String CLEAR = "clear";
  public static final String PLATE_SAMPLE_NOT_SELECTED = "plate.sampleNotSelected";
  private static final Logger logger =
      LoggerFactory.getLogger(ContainerSelectionFormPresenter.class);
  private List<Sample> samples;
  private ListDataProvider<Sample> tubesDataProvider = DataProvider.ofItems();
  private ContainerSelectionForm view;
  private ContainerSelectionFormDesign design;
  private Map<Sample, ComboBox<Tube>> tubes = new HashMap<>();
  @Inject
  private TubeService tubeService;
  @Inject
  private PlateService plateService;

  protected ContainerSelectionFormPresenter() {
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(ContainerSelectionForm view) {
    this.view = view;
    design = view.design;
    prepareComponents();
    design.type.setValue(SampleContainerType.WELL);
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    final Locale locale = view.getLocale();
    design.typePanel.addStyleName(TYPE_PANEL);
    design.typePanel.addStyleName(REQUIRED);
    design.typePanel.setCaption(resources.message(TYPE_PANEL));
    design.type.addStyleName(TYPE);
    design.type.setItemCaptionGenerator(type -> type.getLabel(locale));
    design.type.setItems(SampleContainerType.values());
    design.type.addValueChangeListener(e -> updateType());
    design.tubesPanel.addStyleName(TUBES_PANEL);
    design.tubesPanel.setCaption(resources.message(TUBES_PANEL));
    design.tubes.addStyleName(TUBES);
    design.tubes.addStyleName(COMPONENTS);
    design.tubes.setDataProvider(tubesDataProvider);
    design.tubes.addColumn(Sample::getName).setId(SAMPLE).setCaption(resources.message(SAMPLE));
    design.tubes.addColumn(sample -> tubesField(sample), new ComponentRenderer())
        .setId(CONTAINER_TUBE).setCaption(resources.message(CONTAINER_TUBE)).setSortable(false);
    design.platesPanel.addStyleName(PLATES_PANEL);
    design.platesPanel.setCaption(resources.message(PLATES_PANEL));
    design.plates.addStyleName(PLATES);
    design.plates.setCaption(resources.message(PLATES));
    design.plates.setRequiredIndicatorVisible(true);
    design.plates.setEmptySelectionAllowed(false);
    design.plates.setItemCaptionGenerator(Plate::getName);
    design.plates.addValueChangeListener(e -> updatePlate());
    design.platePanel.addStyleName(PLATE_PANEL);
    design.platePanel.setVisible(false);
    view.plateComponent.setMultiSelect(true);
    design.select.addStyleName(SELECT);
    design.select.setCaption(resources.message(SELECT));
    design.select.addClickListener(e -> select());
    design.clear.addStyleName(CLEAR);
    design.clear.setCaption(resources.message(CLEAR));
    design.clear.addClickListener(e -> clear());
  }

  private ComboBox<Tube> tubesField(Sample sample) {
    if (tubes.containsKey(sample)) {
      return tubes.get(sample);
    } else {
      ComboBox<Tube> field = new ComboBox<>();
      field.addStyleName(TUBES);
      field.setItemCaptionGenerator(Tube::getName);
      field.setItemIconGenerator(tube -> tube.isBanned() ? VaadinIcons.BAN : null);
      field.setRequiredIndicatorVisible(true);
      field.setEmptySelectionAllowed(false);
      tubes.put(sample, field);
      return field;
    }
  }

  private void updateType() {
    SampleContainerType type = design.type.getValue();
    design.tubesPanel.setVisible(type == TUBE);
    design.platesPanel.setVisible(type == WELL);
  }

  private void updatePlate() {
    Plate plate = design.plates.getValue();
    design.platePanel.setVisible(plate != null);
    if (plate != null) {
      design.platePanel.setCaption(plate.getName());
      List<Well> wells =
          samples.stream().flatMap(sample -> plate.wellsContainingSample(sample).stream())
              .collect(Collectors.toList());
      view.plateComponent.setValue(plate);
      view.plateComponent.setSelectedWells(wells);
    }
  }

  private void updateSamples() {
    final Locale locale = view.getLocale();
    tubesDataProvider.getItems().clear();
    tubesDataProvider.getItems().addAll(samples);
    tubesDataProvider.refreshAll();
    samples.forEach(sample -> {
      List<Tube> tubes = tubeService.all(sample);
      Collections.sort(tubes,
          new TubeComparator(view.getLocale(), TubeComparator.Compare.TIME_STAMP));
      Collections.reverse(tubes);
      ComboBox<Tube> field = tubesField(sample);
      field.setItems(tubes);
      if (!tubes.isEmpty()) {
        field.setValue(tubes.get(0));
      }
    });
    List<Sample> filterSamples;
    if (samples.stream().filter(sample -> sample instanceof SubmissionSample).findAny()
        .isPresent()) {
      filterSamples = samples.stream().filter(sample -> sample instanceof SubmissionSample)
          .collect(Collectors.toList());
    } else {
      filterSamples = samples;
    }
    PlateFilter filter = new PlateFilter();
    filter.containsAnySamples = filterSamples;
    List<Plate> plates = plateService.all(filter);
    plates.sort(new NamedComparator(locale));
    design.plates.setItems(plates);
    if (!plates.isEmpty()) {
      design.plates.setValue(plates.get(0));
    }
  }

  private boolean validate() {
    boolean valid = true;
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    SampleContainerType type = design.type.getValue();
    if (type == TUBE) {
      tubes.values().forEach(field -> field.setComponentError(null));
      for (Map.Entry<Sample, ComboBox<Tube>> fieldEntry : tubes.entrySet()) {
        Sample sample = fieldEntry.getKey();
        ComboBox<Tube> field = fieldEntry.getValue();
        if (field.getValue() == null) {
          String message = generalResources.message(REQUIRED);
          logger.debug("Validation error {} for sample {} tube", message, sample);
          field.setComponentError(new UserError(message));
          valid = false;
        }
      }
    } else {
      design.plates.setComponentError(null);
      if (design.plates.getValue() == null) {
        String message = generalResources.message(REQUIRED);
        logger.debug("Validation error {} for plate", message);
        design.plates.setComponentError(new UserError(message));
        valid = false;
      } else {
        List<Well> selectedWells = view.plateComponent.getSelectedWells().stream()
            .filter(well -> well.getSample() != null).collect(Collectors.toList());
        Map<Long, Boolean> wellForSamples =
            samples.stream().collect(Collectors.toMap(Sample::getId, sample -> false));
        selectedWells.forEach(well -> wellForSamples.put(well.getSample().getId(), true));
        Optional<Sample> missingSample =
            samples.stream().filter(sample -> !wellForSamples.get(sample.getId())).findAny();
        if (missingSample.isPresent()) {
          Sample sample = missingSample.get();
          String message = resources.message(PLATE_SAMPLE_NOT_SELECTED, sample.getName());
          logger.debug("Validation error {} for sample {} well", message, sample);
          design.plates.setComponentError(new UserError(message));
          valid = false;
        }
      }
    }
    if (!valid) {
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return valid;
  }

  private void select() {
    if (validate()) {
      SampleContainerType type = design.type.getValue();
      if (type == TUBE) {
        List<SampleContainer> containers = samples.stream()
            .map(sample -> tubes.get(sample).getValue()).collect(Collectors.toList());
        view.fireSaveEvent(containers);
      } else {
        Set<Long> sampleIds =
            samples.stream().map(sample -> sample.getId()).collect(Collectors.toSet());
        List<SampleContainer> containers = view.plateComponent.getSelectedWells().stream()
            .filter(
                well -> well.getSample() != null && sampleIds.contains(well.getSample().getId()))
            .collect(Collectors.toList());
        view.fireSaveEvent(containers);
      }
    }
  }

  private void clear() {
    view.fireSaveEvent(new ArrayList<>());
  }

  List<Sample> getSamples() {
    return samples;
  }

  void setSamples(List<Sample> samples) {
    this.samples = samples;
    updateSamples();
  }
}
