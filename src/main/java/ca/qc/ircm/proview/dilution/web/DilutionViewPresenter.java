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

package ca.qc.ircm.proview.dilution.web;

import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.COMMENT;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.CONTAINER;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.SAMPLE;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.SOLVENT;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.SOLVENT_VOLUME;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.SOURCE_VOLUME;
import static ca.qc.ircm.proview.web.WebConstants.BANNED;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static ca.qc.ircm.proview.web.WebConstants.SAVED_SAMPLE_FROM_MULTIPLE_USERS;

import ca.qc.ircm.proview.dilution.Dilution;
import ca.qc.ircm.proview.dilution.DilutionService;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.vaadin.VaadinUtils;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ComponentRenderer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Dilution view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DilutionViewPresenter implements BinderValidator {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String DELETED = "deleted";
  public static final String DILUTIONS_PANEL = "dilutionsPanel";
  public static final String DILUTIONS = "dilutions";
  public static final String DOWN = "down";
  public static final String EXPLANATION = "explanation";
  public static final String EXPLANATION_PANEL = EXPLANATION + "Panel";
  public static final String SAVE = "save";
  public static final String SAVED = "saved";
  public static final String REMOVE = "remove";
  public static final String REMOVED = "removed";
  public static final String BAN_CONTAINERS = "banContainers";
  public static final String NO_CONTAINERS = "containers.empty";
  public static final String INVALID_CONTAINERS = "containers.invalid";
  public static final String SPLIT_CONTAINER_PARAMETERS = ",";
  public static final String INVALID_DILUTION = "dilution.invalid";
  private static final Logger logger = LoggerFactory.getLogger(DilutionViewPresenter.class);
  private DilutionView view;
  private DilutionViewDesign design;
  private Binder<Dilution> binder = new BeanValidationBinder<>(Dilution.class);
  private List<TreatedSample> dilutions = new ArrayList<>();
  private ListDataProvider<TreatedSample> dilutionsDataProvider = DataProvider.ofItems();
  private Map<TreatedSample, Binder<TreatedSample>> dilutionBinders = new HashMap<>();
  private Map<TreatedSample, TextField> sourceVolumeFields = new HashMap<>();
  private Map<TreatedSample, TextField> solventFields = new HashMap<>();
  private Map<TreatedSample, TextField> solventVolumeFields = new HashMap<>();
  private Map<TreatedSample, TextField> commentFields = new HashMap<>();
  private Map<TreatedSample, Button> downButtons = new HashMap<>();
  @Inject
  private DilutionService dilutionService;
  @Inject
  private SampleContainerService sampleContainerService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected DilutionViewPresenter() {
  }

  protected DilutionViewPresenter(DilutionService dilutionService,
      SampleContainerService sampleContainerService, String applicationName) {
    this.dilutionService = dilutionService;
    this.sampleContainerService = sampleContainerService;
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(DilutionView view) {
    logger.debug("Dilution view");
    this.view = view;
    design = view.design;
    binder.setBean(new Dilution());
    prepareComponents();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
    design.deleted.addStyleName(DELETED);
    design.deleted.setValue(resources.message(DELETED));
    design.deleted.setVisible(false);
    design.dilutionsPanel.addStyleName(DILUTIONS_PANEL);
    design.dilutionsPanel.setCaption(resources.message(DILUTIONS_PANEL));
    design.dilutions.addStyleName(DILUTIONS);
    design.dilutions.addStyleName(COMPONENTS);
    design.dilutions.setDataProvider(dilutionsDataProvider);
    design.dilutions.addColumn(ts -> ts.getSample().getName()).setId(SAMPLE)
        .setCaption(resources.message(SAMPLE));
    design.dilutions.addColumn(ts -> ts.getContainer().getFullName()).setId(CONTAINER)
        .setCaption(resources.message(CONTAINER))
        .setStyleGenerator(ts -> ts.getContainer().isBanned() ? BANNED : "");
    design.dilutions.addColumn(ts -> sourceVolumeField(ts), new ComponentRenderer())
        .setId(SOURCE_VOLUME).setCaption(resources.message(SOURCE_VOLUME)).setSortable(false);
    design.dilutions.addColumn(ts -> solventField(ts), new ComponentRenderer()).setId(SOLVENT)
        .setCaption(resources.message(SOLVENT)).setSortable(false);
    design.dilutions.addColumn(ts -> solventVolumeField(ts), new ComponentRenderer())
        .setId(SOLVENT_VOLUME).setCaption(resources.message(SOLVENT_VOLUME)).setSortable(false);
    design.dilutions.addColumn(ts -> commentField(ts), new ComponentRenderer()).setId(COMMENT)
        .setCaption(resources.message(COMMENT)).setSortable(false);
    design.dilutions.addColumn(ts -> downButton(ts), new ComponentRenderer()).setId(DOWN)
        .setCaption(resources.message(DOWN)).setSortable(false);
    design.explanationPanel.addStyleName(EXPLANATION_PANEL);
    design.explanationPanel.setCaption(resources.message(EXPLANATION_PANEL));
    design.explanationPanel.setVisible(false);
    design.explanation.addStyleName(EXPLANATION);
    design.save.addStyleName(SAVE);
    design.save.setCaption(resources.message(SAVE));
    design.save.addClickListener(e -> save());
    design.removeLayout.setVisible(false);
    design.remove.addStyleName(REMOVE);
    design.remove.setCaption(resources.message(REMOVE));
    design.remove.addClickListener(e -> remove());
    design.banContainers.addStyleName(BAN_CONTAINERS);
    design.banContainers.setCaption(resources.message(BAN_CONTAINERS));
  }

  private Binder<TreatedSample> binder(TreatedSample ts) {
    final MessageResource generalResources = view.getGeneralResources();
    Binder<TreatedSample> binder = new BeanValidationBinder<>(TreatedSample.class);
    binder.setBean(ts);
    dilutionBinders.put(ts, binder);
    binder.forField(sourceVolumeField(ts)).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(SOURCE_VOLUME);
    binder.forField(solventField(ts)).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("").bind(SOLVENT);
    binder.forField(solventVolumeField(ts)).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(SOLVENT_VOLUME);
    binder.forField(commentField(ts)).withNullRepresentation("").bind(COMMENT);
    return binder;
  }

  private TextField sourceVolumeField(TreatedSample ts) {
    if (sourceVolumeFields.get(ts) != null) {
      return sourceVolumeFields.get(ts);
    } else {
      TextField field = new TextField();
      field.addStyleName(SOURCE_VOLUME);
      field.setRequiredIndicatorVisible(true);
      field.setReadOnly(binder.getBean().isDeleted());
      sourceVolumeFields.put(ts, field);
      return field;
    }
  }

  private TextField solventField(TreatedSample ts) {
    if (solventFields.get(ts) != null) {
      return solventFields.get(ts);
    } else {
      TextField field = new TextField();
      field.addStyleName(SOLVENT);
      field.setRequiredIndicatorVisible(true);
      field.setReadOnly(binder.getBean().isDeleted());
      solventFields.put(ts, field);
      return field;
    }
  }

  private TextField solventVolumeField(TreatedSample ts) {
    if (solventVolumeFields.get(ts) != null) {
      return solventVolumeFields.get(ts);
    } else {
      TextField field = new TextField();
      field.addStyleName(SOLVENT_VOLUME);
      field.setRequiredIndicatorVisible(true);
      field.setReadOnly(binder.getBean().isDeleted());
      solventVolumeFields.put(ts, field);
      return field;
    }
  }

  private TextField commentField(TreatedSample ts) {
    if (commentFields.get(ts) != null) {
      return commentFields.get(ts);
    } else {
      TextField field = new TextField();
      field.addStyleName(COMMENT);
      field.setReadOnly(binder.getBean().isDeleted());
      commentFields.put(ts, field);
      return field;
    }
  }

  private Button downButton(TreatedSample ts) {
    if (downButtons.get(ts) != null) {
      return downButtons.get(ts);
    } else {
      final MessageResource resources = view.getResources();
      Button button = new Button();
      button.addStyleName(DOWN);
      button.setIcon(VaadinIcons.ARROW_DOWN);
      button.setIconAlternateText(resources.message(DOWN));
      button.addClickListener(e -> down(ts));
      downButtons.put(ts, button);
      return button;
    }
  }

  private void down(TreatedSample ts) {
    boolean copy = false;
    String sourceVolume = sourceVolumeFields.get(ts).getValue();
    String solvent = solventFields.get(ts).getValue();
    String solventVolume = solventVolumeFields.get(ts).getValue();
    String comment = commentFields.get(ts).getValue();
    for (TreatedSample other : VaadinUtils.gridItems(design.dilutions)
        .collect(Collectors.toList())) {
      if (ts.equals(other)) {
        copy = true;
      }
      if (copy) {
        sourceVolumeFields.get(other).setValue(sourceVolume);
        solventFields.get(other).setValue(solvent);
        solventVolumeFields.get(other).setValue(solventVolume);
        commentFields.get(other).setValue(comment);
      }
    }
  }

  private boolean validate() {
    logger.trace("Validate dilution");
    final MessageResource resources = view.getResources();
    if (dilutions.isEmpty()) {
      String message = resources.message(NO_CONTAINERS);
      logger.debug("Validation error: {}", message);
      view.showError(message);
      return false;
    }
    boolean valid = true;
    valid &= validate(binder);
    for (Binder<TreatedSample> binder : dilutionBinders.values()) {
      valid &= validate(binder);
    }
    if (!valid) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("Dilution validation failed");
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return valid;
  }

  private void save() {
    if (validate()) {
      logger.debug("Saving new dilution");
      final MessageResource resources = view.getResources();
      final MessageResource generalResources = view.getGeneralResources();
      Dilution dilution = binder.getBean();
      dilution.setTreatedSamples(dilutions);
      if (dilution.getId() != null) {
        dilutionService.update(dilution, design.explanation.getValue());
      } else {
        try {
          dilutionService.insert(dilution);
        } catch (IllegalArgumentException e) {
          view.showError(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
          return;
        }
      }
      view.showTrayNotification(resources.message(SAVED,
          dilutions.stream().map(ts -> ts.getSample().getId()).distinct().count()));
      view.navigateTo(DilutionView.VIEW_NAME, String.valueOf(dilution.getId()));
    }
  }

  private boolean validateRemove() {
    logger.trace("Validate remove dilution {}", binder.getBean());
    if (design.explanation.getValue().isEmpty()) {
      final MessageResource generalResources = view.getGeneralResources();
      String message = generalResources.message(REQUIRED);
      logger.debug("Validation error: {}", message);
      design.explanation.setComponentError(new UserError(message));
      view.showError(generalResources.message(FIELD_NOTIFICATION));
      return false;
    }
    return true;
  }

  private void remove() {
    if (validateRemove()) {
      logger.debug("Removing dilution {}", binder.getBean());
      Dilution dilution = binder.getBean();
      dilutionService.undo(dilution, design.explanation.getValue(),
          design.banContainers.getValue());
      MessageResource resources = view.getResources();
      view.showTrayNotification(resources.message(REMOVED,
          dilutions.stream().map(ts -> ts.getSample().getId()).distinct().count()));
      view.navigateTo(DilutionView.VIEW_NAME, String.valueOf(dilution.getId()));
    }
  }

  private boolean validateContainersParameters(String parameters) {
    boolean valid = true;
    String[] rawIds = parameters.split(SPLIT_CONTAINER_PARAMETERS, -1);
    if (rawIds.length < 1) {
      valid = false;
    }
    try {
      for (String rawId : rawIds) {
        Long id = Long.valueOf(rawId);
        if (sampleContainerService.get(id) == null) {
          valid = false;
        }
      }
    } catch (NumberFormatException e) {
      valid = false;
    }
    return valid;
  }

  /**
   * Called by view when entered.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    if (parameters == null || parameters.isEmpty()) {
      logger.trace("Recovering containers from session");
      dilutions = view.savedContainers().stream().map(container -> {
        TreatedSample ts = new TreatedSample();
        ts.setSample(container.getSample());
        ts.setContainer(container);
        return ts;
      }).collect(Collectors.toList());
      if (view.savedContainersFromMultipleUsers()) {
        view.showWarning(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
      }
    } else if (parameters.startsWith("containers/")) {
      parameters = parameters.substring("containers/".length());
      logger.trace("Parsing containers from parameters");
      dilutions = new ArrayList<>();
      if (validateContainersParameters(parameters)) {
        String[] rawIds = parameters.split(SPLIT_CONTAINER_PARAMETERS, -1);
        for (String rawId : rawIds) {
          Long id = Long.valueOf(rawId);
          SampleContainer container = sampleContainerService.get(id);
          TreatedSample ts = new TreatedSample();
          ts.setSample(container.getSample());
          ts.setContainer(container);
          dilutions.add(ts);
        }
      } else {
        view.showWarning(resources.message(INVALID_CONTAINERS));
      }
    } else {
      try {
        Long id = Long.valueOf(parameters);
        logger.debug("Set dilution {}", id);
        Dilution dilution = dilutionService.get(id);
        binder.setBean(dilution);
        if (dilution != null) {
          dilutions = dilution.getTreatedSamples();
          design.deleted.setVisible(dilution.isDeleted());
          design.explanationPanel.setVisible(!dilution.isDeleted());
          design.save.setVisible(!dilution.isDeleted());
          design.removeLayout.setVisible(!dilution.isDeleted());
        } else {
          view.showWarning(resources.message(INVALID_DILUTION));
        }
      } catch (NumberFormatException e) {
        view.showWarning(resources.message(INVALID_DILUTION));
      }
    }

    dilutionsDataProvider.getItems().addAll(dilutions);
    dilutionsDataProvider.refreshAll();
    dilutions.stream().forEach(ts -> {
      binder(ts);
    });
  }
}
