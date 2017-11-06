package ca.qc.ircm.proview.dataanalysis.web;

import static ca.qc.ircm.proview.dataanalysis.QDataAnalysis.dataAnalysis;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.dataanalysis.DataAnalysisService;
import ca.qc.ircm.proview.dataanalysis.DataAnalysisType;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ComponentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

/**
 * Data analysis view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataAnalysisViewPresenter implements BinderValidator {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String PROTEIN_ANALYSIS = "proteinAnalysis";
  public static final String PEPTIDE_ANALYSIS = "peptideAnalysis";
  public static final String MULTIPLE_PROTEINS = "multipleProteins";
  public static final String ANALYSES = "analyses";
  public static final String SAMPLE = dataAnalysis.sample.getMetadata().getName();
  public static final String PROTEIN = dataAnalysis.protein.getMetadata().getName();
  public static final String PEPTIDE = dataAnalysis.peptide.getMetadata().getName();
  public static final String TYPE = dataAnalysis.type.getMetadata().getName();
  public static final String MAX_WORK_TIME = dataAnalysis.maxWorkTime.getMetadata().getName();
  public static final String SAVE = "save";
  public static final String SAVED = "saved";
  public static final String INVALID_SAMPLES = "samples.invalid";
  public static final String SPLIT_SAMPLES_PARAMETERS = ",";
  public static final Double[] MAX_WORK_TIME_VALUES = new Double[] { 0.5, 1.0, 2.0, 3.0 };
  private static final Logger logger = LoggerFactory.getLogger(DataAnalysisViewPresenter.class);
  private DataAnalysisView view;
  private DataAnalysisViewDesign design;
  private Map<DataAnalysis, Binder<DataAnalysis>> analysisBinders = new HashMap<>();
  private Map<DataAnalysis, TextField> proteinFields = new HashMap<>();
  private Map<DataAnalysis, TextField> peptideFields = new HashMap<>();
  private Map<DataAnalysis, ComboBox<DataAnalysisType>> typeFields = new HashMap<>();
  private Map<DataAnalysis, ComboBox<Double>> maxWorkTimeFields = new HashMap<>();
  @Inject
  private DataAnalysisService dataAnalysisService;
  @Inject
  private SampleService sampleService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected DataAnalysisViewPresenter() {
  }

  protected DataAnalysisViewPresenter(DataAnalysisService dataAnalysisService,
      SampleService sampleService, String applicationName) {
    this.dataAnalysisService = dataAnalysisService;
    this.sampleService = sampleService;
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   */
  public void init(DataAnalysisView view) {
    logger.debug("DataAnalysis view");
    this.view = view;
    design = view.design;
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
    design.proteinAnalysis.addStyleName(PROTEIN_ANALYSIS);
    design.proteinAnalysis.setValue(resources.message(PROTEIN_ANALYSIS));
    design.peptideAnalysis.addStyleName(PEPTIDE_ANALYSIS);
    design.peptideAnalysis.setValue(resources.message(PEPTIDE_ANALYSIS));
    design.multipleProteins.addStyleName(MULTIPLE_PROTEINS);
    design.multipleProteins.setValue(resources.message(MULTIPLE_PROTEINS));
    design.analyses.addStyleName(ANALYSES);
    design.analyses.addStyleName(COMPONENTS);
    design.analyses.addColumn(da -> da.getSample().getName()).setId(SAMPLE)
        .setCaption(resources.message(SAMPLE));
    design.analyses.addColumn(da -> proteinField(da), new ComponentRenderer()).setId(PROTEIN)
        .setCaption(resources.message(PROTEIN));
    design.analyses.addColumn(da -> peptideField(da), new ComponentRenderer()).setId(PEPTIDE)
        .setCaption(resources.message(PEPTIDE));
    design.analyses.addColumn(da -> typeField(da), new ComponentRenderer()).setId(TYPE)
        .setCaption(resources.message(TYPE));
    design.analyses.addColumn(da -> maxWorkTimeField(da), new ComponentRenderer())
        .setId(MAX_WORK_TIME).setCaption(resources.message(MAX_WORK_TIME));
    design.save.addStyleName(SAVE);
    design.save.setCaption(resources.message(SAVE));
    design.save.addClickListener(e -> save());
  }

  private void binder(DataAnalysis dataAnalysis) {
    final MessageResource generalResources = view.getGeneralResources();
    Binder<DataAnalysis> binder = new BeanValidationBinder<>(DataAnalysis.class);
    binder.setBean(dataAnalysis);
    binder.forField(proteinField(dataAnalysis)).withNullRepresentation("")
        .withValidator((value, context) -> validateProtein(value, dataAnalysis)).bind(PROTEIN);
    binder.forField(peptideField(dataAnalysis)).withNullRepresentation("")
        .withValidator((value, context) -> validatePeptide(value, dataAnalysis)).bind(PEPTIDE);
    binder.forField(typeField(dataAnalysis)).asRequired(generalResources.message(REQUIRED))
        .bind(TYPE);
    binder.forField(maxWorkTimeField(dataAnalysis)).asRequired(generalResources.message(REQUIRED))
        .bind(MAX_WORK_TIME);
    analysisBinders.put(dataAnalysis, binder);
  }

  private ValidationResult validateProtein(String value, DataAnalysis dataAnalysis) {
    if (value == null && dataAnalysis.getPeptide() != null) {
      MessageResource generalResources = view.getGeneralResources();
      return ValidationResult.error(generalResources.message(REQUIRED));
    } else {
      return ValidationResult.ok();
    }
  }

  private ValidationResult validatePeptide(String value, DataAnalysis dataAnalysis) {
    if (value == null && dataAnalysis.getProtein() != null
        && (dataAnalysis.getType() == DataAnalysisType.PEPTIDE
            || dataAnalysis.getType() == DataAnalysisType.PROTEIN_PEPTIDE)) {
      MessageResource generalResources = view.getGeneralResources();
      return ValidationResult.error(generalResources.message(REQUIRED));
    } else {
      return ValidationResult.ok();
    }
  }

  private TextField proteinField(DataAnalysis dataAnalysis) {
    if (proteinFields.containsKey(dataAnalysis)) {
      return proteinFields.get(dataAnalysis);
    } else {
      TextField field = new TextField();
      field.addStyleName(PROTEIN);
      proteinFields.put(dataAnalysis, field);
      return field;
    }
  }

  private TextField peptideField(DataAnalysis dataAnalysis) {
    if (peptideFields.containsKey(dataAnalysis)) {
      return peptideFields.get(dataAnalysis);
    } else {
      TextField field = new TextField();
      field.addStyleName(PEPTIDE);
      peptideFields.put(dataAnalysis, field);
      return field;
    }
  }

  private ComboBox<DataAnalysisType> typeField(DataAnalysis dataAnalysis) {
    if (typeFields.containsKey(dataAnalysis)) {
      return typeFields.get(dataAnalysis);
    } else {
      ComboBox<DataAnalysisType> field = new ComboBox<>();
      field.addStyleName(TYPE);
      field.setEmptySelectionAllowed(false);
      field.setItems(DataAnalysisType.values());
      field.setItemCaptionGenerator(type -> type.getLabel(view.getLocale()));
      typeFields.put(dataAnalysis, field);
      return field;
    }
  }

  private ComboBox<Double> maxWorkTimeField(DataAnalysis dataAnalysis) {
    if (maxWorkTimeFields.containsKey(dataAnalysis)) {
      return maxWorkTimeFields.get(dataAnalysis);
    } else {
      ComboBox<Double> field = new ComboBox<>();
      field.addStyleName(MAX_WORK_TIME);
      field.setEmptySelectionAllowed(false);
      ListDataProvider<Double> dataProvider =
          DataProvider.fromStream(Stream.of(MAX_WORK_TIME_VALUES));
      field.setDataProvider(dataProvider);
      field.setNewItemHandler(workTime -> {
        try {
          Double value = Double.valueOf(workTime);
          dataProvider.getItems().add(value);
          dataProvider.refreshItem(value);
          field.setValue(value);
        } catch (NumberFormatException e) {
          final MessageResource generalResources = view.getGeneralResources();
          field.setComponentError(new UserError(generalResources.message(INVALID_NUMBER)));
        }
      });
      maxWorkTimeFields.put(dataAnalysis, field);
      return field;
    }
  }

  private boolean validate() {
    boolean valid = true;
    for (Binder<DataAnalysis> binder : analysisBinders.values()) {
      valid &= validate(binder);
    }
    if (!valid) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("Validation failed");
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return valid;
  }

  private void save() {
    if (validate()) {
      MessageResource resources = view.getResources();
      Collection<DataAnalysis> dataAnalysis = analysisBinders.keySet().stream()
          .filter(da -> !da.getProtein().isEmpty()).collect(Collectors.toList());
      dataAnalysisService.insert(dataAnalysis);
      view.showTrayNotification(resources.message(SAVED, dataAnalysis.size()));
      view.navigateTo(SubmissionsView.VIEW_NAME);
    }
  }

  private boolean validateParameters(String parameters) {
    boolean valid = true;
    String[] rawIds = parameters.split(SPLIT_SAMPLES_PARAMETERS, -1);
    if (rawIds.length < 1) {
      valid = false;
    }
    try {
      for (String rawId : rawIds) {
        Long id = Long.valueOf(rawId);
        if (sampleService.get(id) == null) {
          valid = false;
        }
      }
    } catch (NumberFormatException e) {
      valid = false;
    }
    return valid;
  }

  /**
   * Called when view is entered.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    List<DataAnalysis> analyses = new ArrayList<>();
    if (parameters == null || parameters.isEmpty()) {
      logger.trace("Recovering samples from session");
      analyses = view.savedSamples().stream().filter(sa -> sa instanceof SubmissionSample)
          .map(sa -> dataAnalysis((SubmissionSample) sa)).collect(Collectors.toList());
    } else {
      logger.trace("Parsing samples from parameters");
      if (validateParameters(parameters)) {
        String[] rawIds = parameters.split(SPLIT_SAMPLES_PARAMETERS, -1);
        for (String rawId : rawIds) {
          Long id = Long.valueOf(rawId);
          Sample sample = sampleService.get(id);
          if (sample instanceof SubmissionSample) {
            analyses.add(dataAnalysis((SubmissionSample) sample));
          }
        }
      } else {
        view.showWarning(view.getResources().message(INVALID_SAMPLES));
      }
    }

    design.analyses.setItems(analyses);
    analyses.stream().forEach(da -> binder(da));
  }

  private DataAnalysis dataAnalysis(SubmissionSample sample) {
    DataAnalysis analysis = new DataAnalysis();
    analysis.setSample(sample);
    analysis.setType(DataAnalysisType.PROTEIN);
    analysis.setMaxWorkTime(MAX_WORK_TIME_VALUES[0]);
    return analysis;
  }
}
