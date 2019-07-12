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
public class SubmissionViewPresenter {
  private static final Logger logger = LoggerFactory.getLogger(SubmissionViewPresenter.class);
  private SubmissionView view;
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private Binder<SubmissionSample> firstSampleBinder =
      new BeanValidationBinder<>(SubmissionSample.class);

  @Autowired
  protected SubmissionViewPresenter() {
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  void init(SubmissionView view) {
    this.view = view;
    view.service.addValueChangeListener(e -> serviceChanged());
    view.sampleType.addValueChangeListener(e -> sampleTypeChanged());
    view.coloration.addValueChangeListener(e -> colorationChanged());
    view.digestion.addValueChangeListener(e -> digestionChanged());
    view.identification.addValueChangeListener(e -> identificationChanged());
    view.quantification.addValueChangeListener(e -> quantificationChanged());
    setSubmission(null);
  }

  void localeChange(Locale locale) {
    final MessageResource webResources = new MessageResource(WebConstants.class, locale);
    binder.forField(view.service).asRequired(webResources.message(REQUIRED)).bind(SERVICE);
    binder.forField(view.experiment).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(EXPERIMENT);
    binder.forField(view.goal).withNullRepresentation("").bind(GOAL);
    binder.forField(view.taxonomy).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(TAXONOMY);
    binder.forField(view.protein).withNullRepresentation("").bind(PROTEIN);
    firstSampleBinder.forField(view.molecularWeight).withNullRepresentation("")
        .bind(MOLECULAR_WEIGHT);
    binder.forField(view.postTranslationModification).withNullRepresentation("")
        .bind(POST_TRANSLATION_MODIFICATION);
    firstSampleBinder.forField(view.sampleType).bind(TYPE);
    firstSampleBinder.forField(view.quantity).withNullRepresentation("").bind(QUANTITY);
    firstSampleBinder.forField(view.volume).withNullRepresentation("").bind(VOLUME);
    binder.forField(view.separation).asRequired(webResources.message(REQUIRED)).bind(SEPARATION);
    binder.forField(view.thickness).asRequired(webResources.message(REQUIRED)).bind(THICKNESS);
    binder.forField(view.coloration).asRequired(webResources.message(REQUIRED)).bind(COLORATION);
    binder.forField(view.otherColoration).bind(OTHER_COLORATION);
    binder.forField(view.developmentTime).bind(DEVELOPMENT_TIME);
    binder.forField(view.destained).bind(DECOLORATION);
    binder.forField(view.weightMarkerQuantity).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(webResources.message(INVALID_NUMBER)))
        .bind(WEIGHT_MARKER_QUANTITY);
    binder.forField(view.proteinQuantity).bind(PROTEIN_QUANTITY);
    binder.forField(view.digestion).asRequired(webResources.message(REQUIRED))
        .bind(PROTEOLYTIC_DIGESTION_METHOD);
    binder.forField(view.usedDigestion).asRequired(webResources.message(REQUIRED))
        .bind(USED_PROTEOLYTIC_DIGESTION_METHOD);
    binder.forField(view.otherDigestion).asRequired(webResources.message(REQUIRED))
        .bind(OTHER_PROTEOLYTIC_DIGESTION_METHOD);
    binder.forField(view.proteinQuantity).asRequired(webResources.message(REQUIRED))
        .bind(PROTEIN_QUANTITY);
    binder.forField(view.instrument).bind(MASS_DETECTION_INSTRUMENT);
    binder.forField(view.identification).asRequired(webResources.message(REQUIRED))
        .bind(PROTEIN_IDENTIFICATION);
    binder.forField(view.identificationLink).asRequired(webResources.message(REQUIRED))
        .bind(PROTEIN_IDENTIFICATION_LINK);
    binder.forField(view.quantification).bind(QUANTIFICATION);
    serviceChanged();
    sampleTypeChanged();
    digestionChanged();
    identificationChanged();
    quantificationChanged();
  }

  private void serviceChanged() {
    Service service = view.service.getValue();
  }

  private void sampleTypeChanged() {
    SampleType type = view.sampleType.getValue();
    view.quantity.setVisible(type != SampleType.GEL);
    view.volume.setVisible(type != SampleType.GEL && type != SampleType.DRY);
    view.separation.setVisible(type == SampleType.GEL);
    view.thickness.setVisible(type == SampleType.GEL);
    view.coloration.setVisible(type == SampleType.GEL);
    view.developmentTime.setVisible(type == SampleType.GEL);
    view.destained.setVisible(type == SampleType.GEL);
    view.weightMarkerQuantity.setVisible(type == SampleType.GEL);
    view.proteinQuantity.setVisible(type == SampleType.GEL);
    colorationChanged();
  }

  private void colorationChanged() {
    SampleType type = view.sampleType.getValue();
    GelColoration coloration = view.coloration.getValue();
    view.otherColoration.setVisible(type == SampleType.GEL && coloration == GelColoration.OTHER);
  }

  private void digestionChanged() {
    ProteolyticDigestion digestion = view.digestion.getValue();
    view.usedDigestion.setVisible(digestion == ProteolyticDigestion.DIGESTED);
    view.otherDigestion.setVisible(digestion == ProteolyticDigestion.OTHER);
  }

  private void identificationChanged() {
    ProteinIdentification proteinIdentification = view.identification.getValue();
    view.identificationLink.setVisible(proteinIdentification == ProteinIdentification.OTHER);
  }

  private void quantificationChanged() {
    Quantification quantification = view.quantification.getValue();
    view.quantificationComment
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
