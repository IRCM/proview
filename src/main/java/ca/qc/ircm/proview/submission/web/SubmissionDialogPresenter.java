package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.sample.SampleProperties.QUANTITY;
import static ca.qc.ircm.proview.sample.SampleProperties.TYPE;
import static ca.qc.ircm.proview.sample.SampleProperties.VOLUME;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DECOLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DEVELOPMENT_TIME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MASS_DETECTION_INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_PROTEOLYTIC_DIGESTION_METHOD;
import static ca.qc.ircm.proview.submission.SubmissionProperties.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_IDENTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_IDENTIFICATION_LINK;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_QUANTITY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEOLYTIC_DIGESTION_METHOD;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SEPARATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.THICKNESS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USED_PROTEOLYTIC_DIGESTION_METHOD;
import static ca.qc.ircm.proview.submission.SubmissionProperties.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.ArrayList;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submission dialog presenter.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionDialogPresenter {
  private static final Logger logger = LoggerFactory.getLogger(SubmissionDialogPresenter.class);
  private SubmissionDialog dialog;
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private Binder<SubmissionSample> firstSampleBinder = new BeanValidationBinder<>(
      SubmissionSample.class);

  @Autowired
  protected SubmissionDialogPresenter() {
  }

  /**
   * Initializes presenter.
   *
   * @param dialog
   *          dialog
   */
  void init(SubmissionDialog dialog) {
    this.dialog = dialog;
    dialog.service.addValueChangeListener(e -> serviceChanged());
    dialog.sampleType.addValueChangeListener(e -> sampleTypeChanged());
    dialog.coloration.addValueChangeListener(e -> colorationChanged());
    dialog.digestion.addValueChangeListener(e -> digestionChanged());
    dialog.identification.addValueChangeListener(e -> identificationChanged());
    dialog.quantification.addValueChangeListener(e -> quantificationChanged());
    setSubmission(null);
  }

  void localeChange(Locale locale) {
    final MessageResource webResources = new MessageResource(WebConstants.class, locale);
    binder.forField(dialog.service).asRequired(webResources.message(REQUIRED)).bind(SERVICE);
    binder.forField(dialog.experiment).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(EXPERIMENT);
    binder.forField(dialog.goal).withNullRepresentation("").bind(GOAL);
    binder.forField(dialog.taxonomy).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(TAXONOMY);
    binder.forField(dialog.protein).withNullRepresentation("").bind(PROTEIN);
    firstSampleBinder.forField(dialog.molecularWeight).withNullRepresentation("")
        .bind(MOLECULAR_WEIGHT);
    binder.forField(dialog.postTranslationModification).withNullRepresentation("")
        .bind(POST_TRANSLATION_MODIFICATION);
    firstSampleBinder.forField(dialog.sampleType).bind(TYPE);
    firstSampleBinder.forField(dialog.quantity).withNullRepresentation("").bind(QUANTITY);
    firstSampleBinder.forField(dialog.volume).withNullRepresentation("").bind(VOLUME);
    binder.forField(dialog.separation).asRequired(webResources.message(REQUIRED)).bind(SEPARATION);
    binder.forField(dialog.thickness).asRequired(webResources.message(REQUIRED)).bind(THICKNESS);
    binder.forField(dialog.coloration).asRequired(webResources.message(REQUIRED)).bind(COLORATION);
    binder.forField(dialog.otherColoration).bind(OTHER_COLORATION);
    binder.forField(dialog.developmentTime).bind(DEVELOPMENT_TIME);
    binder.forField(dialog.destained).bind(DECOLORATION);
    binder.forField(dialog.weightMarkerQuantity).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(webResources.message(INVALID_NUMBER)))
        .bind(WEIGHT_MARKER_QUANTITY);
    binder.forField(dialog.proteinQuantity).bind(PROTEIN_QUANTITY);
    binder.forField(dialog.digestion).asRequired(webResources.message(REQUIRED))
        .bind(PROTEOLYTIC_DIGESTION_METHOD);
    binder.forField(dialog.usedDigestion).asRequired(webResources.message(REQUIRED))
        .bind(USED_PROTEOLYTIC_DIGESTION_METHOD);
    binder.forField(dialog.otherDigestion).asRequired(webResources.message(REQUIRED))
        .bind(OTHER_PROTEOLYTIC_DIGESTION_METHOD);
    binder.forField(dialog.proteinQuantity).asRequired(webResources.message(REQUIRED))
        .bind(PROTEIN_QUANTITY);
    binder.forField(dialog.instrument).bind(MASS_DETECTION_INSTRUMENT);
    binder.forField(dialog.identification).asRequired(webResources.message(REQUIRED))
        .bind(PROTEIN_IDENTIFICATION);
    binder.forField(dialog.identificationLink).asRequired(webResources.message(REQUIRED))
        .bind(PROTEIN_IDENTIFICATION_LINK);
    binder.forField(dialog.quantification).bind(QUANTIFICATION);
    serviceChanged();
    sampleTypeChanged();
    digestionChanged();
    identificationChanged();
    quantificationChanged();
  }

  private void serviceChanged() {
    Service service = dialog.service.getValue();
  }

  private void sampleTypeChanged() {
    SampleType type = dialog.sampleType.getValue();
    dialog.quantity.setVisible(type != SampleType.GEL);
    dialog.volume.setVisible(type != SampleType.GEL && type != SampleType.DRY);
    dialog.separation.setVisible(type == SampleType.GEL);
    dialog.thickness.setVisible(type == SampleType.GEL);
    dialog.coloration.setVisible(type == SampleType.GEL);
    dialog.developmentTime.setVisible(type == SampleType.GEL);
    dialog.destained.setVisible(type == SampleType.GEL);
    dialog.weightMarkerQuantity.setVisible(type == SampleType.GEL);
    dialog.proteinQuantity.setVisible(type == SampleType.GEL);
    colorationChanged();
  }

  private void colorationChanged() {
    SampleType type = dialog.sampleType.getValue();
    GelColoration coloration = dialog.coloration.getValue();
    dialog.otherColoration.setVisible(type == SampleType.GEL && coloration == GelColoration.OTHER);
  }

  private void digestionChanged() {
    ProteolyticDigestion digestion = dialog.digestion.getValue();
    dialog.usedDigestion.setVisible(digestion == ProteolyticDigestion.DIGESTED);
    dialog.otherDigestion.setVisible(digestion == ProteolyticDigestion.OTHER);
  }

  private void identificationChanged() {
    ProteinIdentification proteinIdentification = dialog.identification.getValue();
    dialog.identificationLink.setVisible(proteinIdentification == ProteinIdentification.OTHER);
  }

  private void quantificationChanged() {
    Quantification quantification = dialog.quantification.getValue();
    dialog.quantificationComment
        .setVisible(quantification == Quantification.SILAC || quantification == Quantification.TMT);
  }

  Submission getSubmission() {
    return binder.getBean();
  }

  void setSubmission(Submission submission) {
    if (submission == null) {
      submission = new Submission();
      submission.setService(Service.LC_MS_MS);
      submission.setStorageTemperature(StorageTemperature.LOW);
      submission.setSeparation(GelSeparation.ONE_DIMENSION);
      submission.setThickness(GelThickness.ONE);
      submission.setProteolyticDigestionMethod(ProteolyticDigestion.TRYPSIN);
      submission.setProteinContent(ProteinContent.SMALL);
      submission.setInjectionType(InjectionType.LC_MS);
      submission.setSource(MassDetectionInstrumentSource.ESI);
      submission.setMassDetectionInstrument(MassDetectionInstrument.NULL);
      submission.setProteinIdentification(ProteinIdentification.REFSEQ);
      submission.setQuantification(Quantification.NULL);
    }
    if (submission.getSamples() == null) {
      submission.setSamples(new ArrayList<>());
    }
    if (submission.getSamples() == null || submission.getSamples().isEmpty()) {
      SubmissionSample sample = new SubmissionSample();
      sample.setType(SampleType.SOLUTION);
      submission.getSamples().add(sample);
    }
    binder.setBean(submission);
    firstSampleBinder.setBean(submission.getSamples().get(0));
  }
}
