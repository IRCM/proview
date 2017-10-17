package ca.qc.ircm.proview.enrichment.web;

import static ca.qc.ircm.proview.enrichment.QEnrichedSample.enrichedSample;
import static ca.qc.ircm.proview.enrichment.QEnrichment.enrichment;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.enrichment.EnrichedSample;
import ca.qc.ircm.proview.enrichment.Enrichment;
import ca.qc.ircm.proview.enrichment.EnrichmentProtocol;
import ca.qc.ircm.proview.enrichment.EnrichmentProtocolService;
import ca.qc.ircm.proview.enrichment.EnrichmentService;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Enrichment view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EnrichmentViewPresenter implements BinderValidator {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String PROTOCOL_PANEL = "protocolPanel";
  public static final String PROTOCOL = enrichment.protocol.getMetadata().getName();
  public static final String ENRICHMENTS_PANEL = "enrichmentsPanel";
  public static final String ENRICHMENTS = "enrichments";
  public static final String SAMPLE = enrichedSample.sample.getMetadata().getName();
  public static final String CONTAINER = enrichedSample.container.getMetadata().getName();
  public static final String SAVE = "save";
  public static final String SAVED = "saved";
  public static final String NO_CONTAINERS = "containers.empty";
  public static final String INVALID_CONTAINERS = "containers.invalid";
  public static final String SPLIT_CONTAINER_PARAMETERS = ",";
  public static final String INVALID_ENRICHMENT = "enrichment.invalid";
  private static final Logger logger = LoggerFactory.getLogger(EnrichmentViewPresenter.class);
  private EnrichmentView view;
  private EnrichmentViewDesign design;
  private boolean readOnly;
  private Binder<Enrichment> binder = new BeanValidationBinder<>(Enrichment.class);
  private ListDataProvider<EnrichmentProtocol> protocolsProvider;
  private List<EnrichedSample> enrichments = new ArrayList<>();
  @Inject
  private EnrichmentService enrichmentService;
  @Inject
  private EnrichmentProtocolService enrichmentProtocolService;
  @Inject
  private SampleContainerService sampleContainerService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected EnrichmentViewPresenter() {
  }

  protected EnrichmentViewPresenter(EnrichmentService enrichmentService,
      EnrichmentProtocolService enrichmentProtocolService,
      SampleContainerService sampleContainerService, String applicationName) {
    this.enrichmentService = enrichmentService;
    this.enrichmentProtocolService = enrichmentProtocolService;
    this.sampleContainerService = sampleContainerService;
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(EnrichmentView view) {
    logger.debug("Enrichment view");
    this.view = view;
    design = view.design;
    binder.setBean(new Enrichment());
    prepareComponents();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
    design.protocolPanel.addStyleName(PROTOCOL_PANEL);
    design.protocolPanel.setCaption(resources.message(PROTOCOL_PANEL));
    design.protocol.addStyleName(PROTOCOL);
    design.protocol.setEmptySelectionAllowed(false);
    design.protocol.setItemCaptionGenerator(protocol -> protocol.getName());
    design.protocol.setNewItemHandler(name -> {
      EnrichmentProtocol protocol = new EnrichmentProtocol(null, name);
      protocolsProvider.getItems().add(protocol);
      protocolsProvider.refreshItem(protocol);
      design.protocol.setValue(protocol);
    });
    protocolsProvider = DataProvider.ofCollection(enrichmentProtocolService.all());
    design.protocol.setDataProvider(protocolsProvider);
    if (!protocolsProvider.getItems().isEmpty()) {
      binder.getBean().setProtocol(protocolsProvider.getItems().iterator().next());
    }
    binder.forField(design.protocol).asRequired(generalResources.message(REQUIRED)).bind(PROTOCOL);
    design.enrichmentsPanel.addStyleName(ENRICHMENTS_PANEL);
    design.enrichmentsPanel.setCaption(resources.message(ENRICHMENTS_PANEL));
    design.enrichments.addStyleName(ENRICHMENTS);
    design.enrichments.addColumn(ts -> ts.getSample().getName()).setId(SAMPLE)
        .setCaption(resources.message(SAMPLE));
    design.enrichments.addColumn(ts -> ts.getContainer().getFullName()).setId(CONTAINER)
        .setCaption(resources.message(CONTAINER));
    design.save.addStyleName(SAVE);
    design.save.setCaption(resources.message(SAVE));
    design.save.addClickListener(e -> save());
  }

  private void updateReadOnly() {
    design.protocol.setReadOnly(readOnly);
    design.save.setVisible(!readOnly);
  }

  private boolean validate() {
    logger.trace("Validate enrichment");
    final MessageResource resources = view.getResources();
    if (enrichments.isEmpty()) {
      String message = resources.message(NO_CONTAINERS);
      logger.debug("Validation error: {}", message);
      view.showError(message);
      return false;
    }
    boolean valid = true;
    valid &= validate(binder);
    if (!valid) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("Enrichment validation failed");
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return valid;
  }

  private void save() {
    if (validate()) {
      logger.debug("Saving new enrichment");
      Enrichment enrichment = binder.getBean();
      enrichment.setTreatmentSamples(enrichments);
      enrichmentService.insert(enrichment);
      MessageResource resources = view.getResources();
      view.showTrayNotification(resources.message(SAVED,
          enrichments.stream().map(ts -> ts.getSample().getId()).distinct().count()));
      view.navigateTo(EnrichmentView.VIEW_NAME, String.valueOf(enrichment.getId()));
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
    if (parameters == null || parameters.isEmpty()) {
      logger.trace("Recovering containers from session");
      enrichments = view.savedContainers().stream().map(container -> {
        EnrichedSample ts = new EnrichedSample();
        ts.setSample(container.getSample());
        ts.setContainer(container);
        return ts;
      }).collect(Collectors.toList());
    } else if (parameters.startsWith("containers/")) {
      parameters = parameters.substring("containers/".length());
      logger.trace("Parsing containers from parameters");
      enrichments = new ArrayList<>();
      if (validateContainersParameters(parameters)) {
        String[] rawIds = parameters.split(SPLIT_CONTAINER_PARAMETERS, -1);
        for (String rawId : rawIds) {
          Long id = Long.valueOf(rawId);
          SampleContainer container = sampleContainerService.get(id);
          EnrichedSample ts = new EnrichedSample();
          ts.setSample(container.getSample());
          ts.setContainer(container);
          enrichments.add(ts);
        }
      } else {
        view.showWarning(view.getResources().message(INVALID_CONTAINERS));
      }
    } else {
      try {
        Long id = Long.valueOf(parameters);
        logger.debug("Set enrichment {}", id);
        Enrichment enrichment = enrichmentService.get(id);
        binder.setBean(enrichment);
        if (enrichment != null) {
          enrichments = enrichment.getTreatmentSamples();
          readOnly = true;
        } else {
          view.showWarning(view.getResources().message(INVALID_ENRICHMENT));
        }
      } catch (NumberFormatException e) {
        view.showWarning(view.getResources().message(INVALID_ENRICHMENT));
      }
    }

    design.enrichments.setItems(enrichments);
    updateReadOnly();
  }
}
