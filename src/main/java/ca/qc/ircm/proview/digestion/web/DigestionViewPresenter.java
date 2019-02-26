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

package ca.qc.ircm.proview.digestion.web;

import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.COMMENT;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.CONTAINER;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.SAMPLE;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.PROTOCOL;
import static ca.qc.ircm.proview.web.WebConstants.BANNED;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static ca.qc.ircm.proview.web.WebConstants.SAVED_SAMPLE_FROM_MULTIPLE_USERS;

import ca.qc.ircm.proview.digestion.Digestion;
import ca.qc.ircm.proview.digestion.DigestionService;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.treatment.Protocol;
import ca.qc.ircm.proview.treatment.ProtocolService;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.vaadin.VaadinUtils;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
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
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Digestion view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DigestionViewPresenter implements BinderValidator {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String DELETED = "deleted";
  public static final String PROTOCOL_PANEL = "protocolPanel";
  public static final String DIGESTIONS_PANEL = "digestionsPanel";
  public static final String DIGESTIONS = "digestions";
  public static final String EXPLANATION = "explanation";
  public static final String EXPLANATION_PANEL = EXPLANATION + "Panel";
  public static final String DOWN = "down";
  public static final String SAVE = "save";
  public static final String SAVED = "saved";
  public static final String REMOVE = "remove";
  public static final String REMOVED = "removed";
  public static final String BAN_CONTAINERS = "banContainers";
  public static final String NO_CONTAINERS = "containers.empty";
  public static final String INVALID_CONTAINERS = "containers.invalid";
  public static final String SPLIT_CONTAINER_PARAMETERS = ",";
  public static final String INVALID_DIGESTION = "digestion.invalid";
  private static final Logger logger = LoggerFactory.getLogger(DigestionViewPresenter.class);
  private DigestionView view;
  private DigestionViewDesign design;
  private Binder<Digestion> binder = new BeanValidationBinder<>(Digestion.class);
  private ListDataProvider<Protocol> protocolsProvider;
  private List<TreatedSample> digestions = new ArrayList<>();
  private ListDataProvider<TreatedSample> digestionsDataProvider = DataProvider.ofItems();
  private Map<TreatedSample, Binder<TreatedSample>> digestionBinders = new HashMap<>();
  private Map<TreatedSample, TextField> commentFields = new HashMap<>();
  private Map<TreatedSample, Button> downButtons = new HashMap<>();
  @Inject
  private DigestionService digestionService;
  @Inject
  private ProtocolService protocolService;
  @Inject
  private SampleContainerService sampleContainerService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected DigestionViewPresenter() {
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(DigestionView view) {
    logger.debug("Digestion view");
    this.view = view;
    design = view.design;
    binder.setBean(new Digestion());
    prepareComponents();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
    design.deleted.addStyleName(DELETED);
    design.deleted.setValue(resources.message(DELETED));
    design.deleted.setVisible(false);
    design.protocolPanel.addStyleName(PROTOCOL_PANEL);
    design.protocolPanel.setCaption(resources.message(PROTOCOL_PANEL));
    design.protocol.addStyleName(PROTOCOL);
    design.protocol.setEmptySelectionAllowed(false);
    design.protocol.setItemCaptionGenerator(protocol -> protocol.getName());
    design.protocol.setNewItemProvider(name -> {
      Protocol protocol = new Protocol(null, name);
      protocol.setType(Protocol.Type.DIGESTION);
      protocolsProvider.getItems().add(protocol);
      protocolsProvider.refreshItem(protocol);
      return Optional.of(protocol);
    });
    protocolsProvider = DataProvider.ofCollection(protocolService.all(Protocol.Type.DIGESTION));
    design.protocol.setDataProvider(protocolsProvider);
    if (!protocolsProvider.getItems().isEmpty()) {
      binder.getBean().setProtocol(protocolsProvider.getItems().iterator().next());
    }
    binder.forField(design.protocol).asRequired(generalResources.message(REQUIRED)).bind(PROTOCOL);
    design.digestionsPanel.addStyleName(DIGESTIONS_PANEL);
    design.digestionsPanel.setCaption(resources.message(DIGESTIONS_PANEL));
    design.digestions.addStyleName(DIGESTIONS);
    design.digestions.addStyleName(COMPONENTS);
    design.digestions.setDataProvider(digestionsDataProvider);
    design.digestions.addColumn(ts -> ts.getSample().getName()).setId(SAMPLE)
        .setCaption(resources.message(SAMPLE));
    design.digestions.addColumn(ts -> ts.getContainer().getFullName()).setId(CONTAINER)
        .setCaption(resources.message(CONTAINER))
        .setStyleGenerator(ts -> ts.getContainer().isBanned() ? BANNED : "");
    design.digestions.addColumn(ts -> commentField(ts), new ComponentRenderer()).setId(COMMENT)
        .setCaption(resources.message(COMMENT)).setSortable(false);
    design.digestions.addColumn(ts -> downButton(ts), new ComponentRenderer()).setId(DOWN)
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

  private Binder<TreatedSample> binder(TreatedSample ts) {
    Binder<TreatedSample> binder = new BeanValidationBinder<>(TreatedSample.class);
    binder.setBean(ts);
    digestionBinders.put(ts, binder);
    binder.forField(commentField(ts)).withNullRepresentation("").bind(COMMENT);
    return binder;
  }

  private void down(TreatedSample ts) {
    boolean copy = false;
    String comment = commentFields.get(ts).getValue();
    for (TreatedSample other : VaadinUtils.gridItems(design.digestions)
        .collect(Collectors.toList())) {
      if (ts.equals(other)) {
        copy = true;
      }
      if (copy) {
        commentFields.get(other).setValue(comment);
      }
    }
  }

  private boolean validate() {
    logger.trace("Validate digestion");
    final MessageResource resources = view.getResources();
    if (digestions.isEmpty()) {
      String message = resources.message(NO_CONTAINERS);
      logger.debug("Validation error: {}", message);
      view.showError(message);
      return false;
    }
    boolean valid = true;
    valid &= validate(binder);
    if (!valid) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("Digestion validation failed");
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return valid;
  }

  private void save() {
    if (validate()) {
      logger.debug("Saving digestion");
      final MessageResource resources = view.getResources();
      final MessageResource generalResources = view.getGeneralResources();
      Digestion digestion = binder.getBean();
      digestion.setTreatedSamples(digestions);
      if (digestion.getId() != null) {
        digestionService.update(digestion, design.explanation.getValue());
      } else {
        try {
          digestionService.insert(digestion);
        } catch (IllegalArgumentException e) {
          view.showError(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
          return;
        }
      }
      view.showTrayNotification(resources.message(SAVED,
          digestions.stream().map(ts -> ts.getSample().getId()).distinct().count()));
      view.navigateTo(DigestionView.VIEW_NAME, String.valueOf(digestion.getId()));
    }
  }

  private boolean validateRemove() {
    logger.trace("Validate remove digestion {}", binder.getBean());
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
      logger.debug("Removing digestion {}", binder.getBean());
      Digestion digestion = binder.getBean();
      digestionService.undo(digestion, design.explanation.getValue(),
          design.banContainers.getValue());
      MessageResource resources = view.getResources();
      view.showTrayNotification(resources.message(REMOVED,
          digestions.stream().map(ts -> ts.getSample().getId()).distinct().count()));
      view.navigateTo(DigestionView.VIEW_NAME, String.valueOf(digestion.getId()));
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
      digestions = view.savedContainers().stream().map(container -> {
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
      digestions = new ArrayList<>();
      if (validateContainersParameters(parameters)) {
        String[] rawIds = parameters.split(SPLIT_CONTAINER_PARAMETERS, -1);
        for (String rawId : rawIds) {
          Long id = Long.valueOf(rawId);
          SampleContainer container = sampleContainerService.get(id);
          TreatedSample ts = new TreatedSample();
          ts.setSample(container.getSample());
          ts.setContainer(container);
          digestions.add(ts);
        }
      } else {
        view.showWarning(resources.message(INVALID_CONTAINERS));
      }
    } else {
      try {
        Long id = Long.valueOf(parameters);
        logger.debug("Set digestion {}", id);
        Digestion digestion = digestionService.get(id);
        binder.setBean(digestion);
        if (digestion != null) {
          digestions = digestion.getTreatedSamples();
          design.protocol.setReadOnly(digestion.isDeleted());
          design.deleted.setVisible(digestion.isDeleted());
          design.explanationPanel.setVisible(!digestion.isDeleted());
          design.save.setVisible(!digestion.isDeleted());
          design.removeLayout.setVisible(!digestion.isDeleted());
        } else {
          view.showWarning(resources.message(INVALID_DIGESTION));
        }
      } catch (NumberFormatException e) {
        view.showWarning(resources.message(INVALID_DIGESTION));
      }
    }

    digestionsDataProvider.getItems().addAll(digestions);
    digestionsDataProvider.refreshAll();
    digestions.stream().forEach(ts -> {
      binder(ts);
    });
  }
}
