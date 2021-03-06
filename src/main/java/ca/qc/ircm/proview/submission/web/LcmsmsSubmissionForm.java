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

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.PLACEHOLDER;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.sample.SampleProperties.QUANTITY;
import static ca.qc.ircm.proview.sample.SampleProperties.VOLUME;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.CONTAMINANTS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DECOLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DEVELOPMENT_TIME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.IDENTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.IDENTIFICATION_LINK;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_CONTENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_QUANTITY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION_COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SEPARATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STANDARDS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.THICKNESS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USED_DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submission form for {@link Service#LC_MS_MS}.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LcmsmsSubmissionForm extends FormLayout implements LocaleChangeObserver {
  public static final String ID = "lcmsms-submission-form";
  public static final String SAMPLES_TYPE = SAMPLES + "Type";
  public static final String SAMPLES_COUNT = SAMPLES + "Count";
  public static final String SAMPLES_NAMES = SAMPLES + "Names";
  public static final String SAMPLES_NAMES_PLACEHOLDER = property(SAMPLES_NAMES, PLACEHOLDER);
  public static final String SAMPLES_NAMES_TITLE = property(SAMPLES_NAMES, TITLE);
  public static final String SAMPLES_NAMES_DUPLICATES = property(SAMPLES + "Names", "duplicate");
  public static final String SAMPLES_NAMES_EXISTS = property(SAMPLES + "Names", "exists");
  public static final String SAMPLES_NAMES_WRONG_COUNT = property(SAMPLES + "Names", "wrongCount");
  public static final String QUANTITY_PLACEHOLDER = property(QUANTITY, PLACEHOLDER);
  public static final String VOLUME_PLACEHOLDER = property(VOLUME, PLACEHOLDER);
  public static final String CONTAMINANTS_PLACEHOLDER = property(CONTAMINANTS, PLACEHOLDER);
  public static final String STANDARDS_PLACEHOLDER = property(STANDARDS, PLACEHOLDER);
  public static final String DEVELOPMENT_TIME_PLACEHOLDER = property(DEVELOPMENT_TIME, PLACEHOLDER);
  public static final String WEIGHT_MARKER_QUANTITY_PLACEHOLDER =
      property(WEIGHT_MARKER_QUANTITY, PLACEHOLDER);
  public static final String PROTEIN_QUANTITY_PLACEHOLDER = property(PROTEIN_QUANTITY, PLACEHOLDER);
  public static final String QUANTIFICATION_COMMENT_PLACEHOLDER =
      property(QUANTIFICATION_COMMENT, PLACEHOLDER);
  public static final String QUANTIFICATION_COMMENT_PLACEHOLDER_TMT =
      property(QUANTIFICATION_COMMENT, PLACEHOLDER, Quantification.TMT.name());
  private static final long serialVersionUID = 1460183864073097086L;
  protected TextField experiment = new TextField();
  protected TextField goal = new TextField();
  protected TextField taxonomy = new TextField();
  protected TextField protein = new TextField();
  protected TextField molecularWeight = new TextField();
  protected TextField postTranslationModification = new TextField();
  protected RadioButtonGroup<SampleType> sampleType = new RadioButtonGroup<>();
  protected TextField samplesCount = new TextField();
  protected TextArea samplesNames = new TextArea();
  protected TextField quantity = new TextField();
  protected TextField volume = new TextField();
  protected TextArea contaminants = new TextArea();
  protected TextArea standards = new TextArea();
  protected ComboBox<GelSeparation> separation = new ComboBox<>();
  protected ComboBox<GelThickness> thickness = new ComboBox<>();
  protected ComboBox<GelColoration> coloration = new ComboBox<>();
  protected TextField otherColoration = new TextField();
  protected TextField developmentTime = new TextField();
  protected Checkbox destained = new Checkbox();
  protected TextField weightMarkerQuantity = new TextField();
  protected TextField proteinQuantity = new TextField();
  protected ComboBox<ProteolyticDigestion> digestion = new ComboBox<>();
  protected TextField usedDigestion = new TextField();
  protected TextField otherDigestion = new TextField();
  protected RadioButtonGroup<ProteinContent> proteinContent = new RadioButtonGroup<>();
  protected ComboBox<MassDetectionInstrument> instrument = new ComboBox<>();
  protected RadioButtonGroup<ProteinIdentification> identification = new RadioButtonGroup<>();
  protected TextField identificationLink = new TextField();
  protected ComboBox<Quantification> quantification = new ComboBox<>();
  protected TextArea quantificationComment = new TextArea();
  private transient LcmsmsSubmissionFormPresenter presenter;

  @Autowired
  protected LcmsmsSubmissionForm(LcmsmsSubmissionFormPresenter presenter) {
    this.presenter = presenter;
  }

  public static String id(String baseId) {
    return styleName(ID, baseId);
  }

  @PostConstruct
  void init() {
    setId(ID);
    setMaxWidth("80em");
    setResponsiveSteps(new ResponsiveStep("15em", 1), new ResponsiveStep("15em", 2),
        new ResponsiveStep("15em", 3));
    add(new FormLayout(experiment, goal, taxonomy, protein, molecularWeight,
        postTranslationModification),
        new FormLayout(sampleType, samplesCount, samplesNames, quantity, volume, separation,
            thickness, coloration, otherColoration, developmentTime, destained,
            weightMarkerQuantity, proteinQuantity),
        new FormLayout(digestion, usedDigestion, otherDigestion, proteinContent, instrument,
            identification, identificationLink, quantification, quantificationComment));
    experiment.setId(id(EXPERIMENT));
    goal.setId(id(GOAL));
    taxonomy.setId(id(TAXONOMY));
    protein.setId(id(PROTEIN));
    molecularWeight.setId(id(MOLECULAR_WEIGHT));
    postTranslationModification.setId(id(POST_TRANSLATION_MODIFICATION));
    quantity.setId(id(QUANTITY));
    volume.setId(id(VOLUME));
    contaminants.setId(id(CONTAMINANTS));
    standards.setId(id(STANDARDS));
    sampleType.setId(id(SAMPLES_TYPE));
    sampleType.setItems(SampleType.values());
    sampleType.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    sampleType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    samplesCount.setId(id(SAMPLES_COUNT));
    samplesNames.setId(id(SAMPLES_NAMES));
    samplesNames.setMinHeight("10em");
    separation.setId(id(SEPARATION));
    separation.setItems(GelSeparation.values());
    separation.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    thickness.setId(id(THICKNESS));
    thickness.setItems(GelThickness.values());
    thickness.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    coloration.setId(id(COLORATION));
    coloration.setItems(GelColoration.values());
    coloration.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    otherColoration.setId(id(OTHER_COLORATION));
    developmentTime.setId(id(DEVELOPMENT_TIME));
    destained.setId(id(DECOLORATION));
    weightMarkerQuantity.setId(id(WEIGHT_MARKER_QUANTITY));
    proteinQuantity.setId(id(PROTEIN_QUANTITY));
    digestion.setId(id(DIGESTION));
    digestion.setItems(ProteolyticDigestion.values());
    digestion.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    usedDigestion.setId(id(USED_DIGESTION));
    otherDigestion.setId(id(OTHER_DIGESTION));
    proteinContent.setId(id(PROTEIN_CONTENT));
    proteinContent.setItems(ProteinContent.values());
    proteinContent.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    proteinContent.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    instrument.setId(id(INSTRUMENT));
    instrument.setItems(MassDetectionInstrument.userChoices());
    instrument.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    identification.setId(id(IDENTIFICATION));
    identification.setItems(ProteinIdentification.availables());
    identification.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    identification.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    identificationLink.setId(id(IDENTIFICATION_LINK));
    quantification.setId(id(QUANTIFICATION));
    quantification.setItems(Quantification.values());
    quantification.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    quantification.addValueChangeListener(e -> updateQuantificationComment());
    quantificationComment.setId(id(QUANTIFICATION_COMMENT));
    identification.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    presenter.init(this);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final AppResources resources = new AppResources(LcmsmsSubmissionForm.class, getLocale());
    final AppResources submissionResources = new AppResources(Submission.class, getLocale());
    final AppResources sampleResources = new AppResources(Sample.class, getLocale());
    final AppResources submissionSampleResources =
        new AppResources(SubmissionSample.class, getLocale());
    experiment.setLabel(submissionResources.message(EXPERIMENT));
    goal.setLabel(submissionResources.message(GOAL));
    taxonomy.setLabel(submissionResources.message(TAXONOMY));
    protein.setLabel(submissionResources.message(PROTEIN));
    molecularWeight.setLabel(submissionSampleResources.message(MOLECULAR_WEIGHT));
    postTranslationModification
        .setLabel(submissionResources.message(POST_TRANSLATION_MODIFICATION));
    quantity.setLabel(sampleResources.message(QUANTITY));
    quantity.setPlaceholder(resources.message(QUANTITY_PLACEHOLDER));
    volume.setLabel(sampleResources.message(VOLUME));
    volume.setPlaceholder(resources.message(VOLUME_PLACEHOLDER));
    contaminants.setLabel(submissionResources.message(CONTAMINANTS));
    contaminants.setPlaceholder(resources.message(CONTAMINANTS_PLACEHOLDER));
    standards.setLabel(submissionResources.message(STANDARDS));
    standards.setPlaceholder(resources.message(STANDARDS_PLACEHOLDER));
    sampleType.setLabel(resources.message(SAMPLES_TYPE));
    samplesCount.setLabel(resources.message(SAMPLES_COUNT));
    samplesNames.setLabel(resources.message(SAMPLES_NAMES));
    samplesNames.setPlaceholder(resources.message(SAMPLES_NAMES_PLACEHOLDER));
    samplesNames.getElement().setAttribute(TITLE, resources.message(SAMPLES_NAMES_TITLE));
    separation.setLabel(submissionResources.message(SEPARATION));
    thickness.setLabel(submissionResources.message(THICKNESS));
    coloration.setLabel(submissionResources.message(COLORATION));
    otherColoration.setLabel(submissionResources.message(OTHER_COLORATION));
    developmentTime.setLabel(submissionResources.message(DEVELOPMENT_TIME));
    developmentTime.setPlaceholder(resources.message(DEVELOPMENT_TIME_PLACEHOLDER));
    destained.setLabel(submissionResources.message(DECOLORATION));
    weightMarkerQuantity.setLabel(submissionResources.message(WEIGHT_MARKER_QUANTITY));
    weightMarkerQuantity.setPlaceholder(resources.message(WEIGHT_MARKER_QUANTITY_PLACEHOLDER));
    proteinQuantity.setLabel(submissionResources.message(PROTEIN_QUANTITY));
    proteinQuantity.setPlaceholder(resources.message(PROTEIN_QUANTITY_PLACEHOLDER));
    digestion.setLabel(submissionResources.message(DIGESTION));
    usedDigestion.setLabel(submissionResources.message(USED_DIGESTION));
    otherDigestion.setLabel(submissionResources.message(OTHER_DIGESTION));
    proteinContent.setLabel(submissionResources.message(PROTEIN_CONTENT));
    instrument.setLabel(submissionResources.message(INSTRUMENT));
    identification.setLabel(submissionResources.message(IDENTIFICATION));
    identificationLink.setLabel(submissionResources.message(IDENTIFICATION_LINK));
    quantification.setLabel(submissionResources.message(QUANTIFICATION));
    quantificationComment.setLabel(submissionResources.message(QUANTIFICATION_COMMENT));
    quantificationComment.setPlaceholder(resources.message(QUANTIFICATION_COMMENT_PLACEHOLDER));
    presenter.localeChange(getLocale());
  }

  private void updateQuantificationComment() {
    final AppResources resources = new AppResources(LcmsmsSubmissionForm.class, getLocale());
    if (quantification.getValue() == Quantification.TMT) {
      quantificationComment
          .setPlaceholder(resources.message(QUANTIFICATION_COMMENT_PLACEHOLDER_TMT));
    } else {
      quantificationComment.setPlaceholder(resources.message(QUANTIFICATION_COMMENT_PLACEHOLDER));
    }
  }

  public boolean isValid() {
    return presenter.isValid();
  }

  public Submission getSubmission() {
    return presenter.getSubmission();
  }

  public void setSubmission(Submission submission) {
    presenter.setSubmission(submission);
  }
}
