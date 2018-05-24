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

import static ca.qc.ircm.proview.sample.web.ContaminantsFormPresenter.CONTAMINANTS;
import static ca.qc.ircm.proview.sample.web.StandardsFormPresenter.STANDARDS;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.COLORATION;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.DECOLORATION;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.DEVELOPMENT_TIME;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.OTHER_COLORATION;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.PROTEIN_QUANTITY;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.SEPARATION;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.THICKNESS;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.AVERAGE_MASS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.COMMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.COMMENT_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANTS_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.DIGESTION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.ENRICHEMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXCLUSIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPERIMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPERIMENT_GOAL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPERIMENT_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES_UPLOADER;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FORMULA;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_IMAGE_FILE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GUIDELINES;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.INACTIVE_WARNING;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.INJECTION_TYPE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.INSTRUMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.LIGHT_SENSITIVE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.MONOISOTOPIC_MASS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_DIGESTION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_SOLVENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_SOLVENT_NOTE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PLATE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PLATE_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_CONTENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_IDENTIFICATION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_IDENTIFICATION_LINK;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_WEIGHT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.QUANTIFICATION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.QUANTIFICATION_COMMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_CONTAINER_TYPE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_LABEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_PLATE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_QUANTITY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_TYPE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_TYPE_WARNING;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_VOLUME;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAVE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICE_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOLUTION_SOLVENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOLVENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOURCE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARDS_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STORAGE_TEMPERATURE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STRUCTURE_FILE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STYLE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.TAXONOMY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.TOXICITY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.USED_DIGESTION;
import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.HEADER_STYLE;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.web.ContaminantsFormPresenter;
import ca.qc.ircm.proview.sample.web.StandardsFormPresenter;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.treatment.Solvent;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import java.nio.file.Path;
import org.openqa.selenium.WebElement;

public abstract class SubmissionViewPageObject extends AbstractTestBenchTestCase {
  private static final String STANDARD_COUNT = StandardsFormPresenter.COUNT;
  private static final String FILL_STANDARDS = StandardsFormPresenter.DOWN;
  private static final String CONTAMINANT_COUNT = ContaminantsFormPresenter.COUNT;
  private static final String FILL_CONTAMINANTS = ContaminantsFormPresenter.DOWN;

  protected void open() {
    openView(SubmissionView.VIEW_NAME);
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER_STYLE)));
  }

  protected LabelElement sampleTypeLabel() {
    return wrap(LabelElement.class, findElement(className(SAMPLE_TYPE_WARNING)));
  }

  protected LabelElement inactiveLabel() {
    return wrap(LabelElement.class, findElement(className(INACTIVE_WARNING)));
  }

  protected ButtonElement guidelines() {
    return wrap(ButtonElement.class,
        findElement(className(STYLE)).findElement(className(GUIDELINES)));
  }

  protected void clickGuidelines() {
    guidelines().click();
  }

  protected PanelElement servicePanel() {
    return wrap(PanelElement.class, findElement(className(SERVICE_PANEL)));
  }

  protected RadioButtonGroupElement serviceOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(SERVICE)));
  }

  protected void setService(Service service) {
    serviceOptions().setValue(service.getLabel(currentLocale()));
  }

  protected PanelElement samplesPanel() {
    return wrap(PanelElement.class, findElement(className(SAMPLES_PANEL)));
  }

  protected RadioButtonGroupElement sampleTypeOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(SAMPLE_TYPE)));
  }

  protected void setSampleType(SampleType type) {
    sampleTypeOptions().setValue(type.getLabel(currentLocale()));
  }

  protected TextFieldElement solutionSolventField() {
    return wrap(TextFieldElement.class, findElement(className(SOLUTION_SOLVENT)));
  }

  protected TextFieldElement sampleNameField() {
    return wrap(TextFieldElement.class, findElement(className(SAMPLE_NAME)));
  }

  protected TextFieldElement formulaField() {
    return wrap(TextFieldElement.class, findElement(className(FORMULA)));
  }

  protected TextFieldElement monoisotopicMassField() {
    return wrap(TextFieldElement.class, findElement(className(MONOISOTOPIC_MASS)));
  }

  protected TextFieldElement averageMassField() {
    return wrap(TextFieldElement.class, findElement(className(AVERAGE_MASS)));
  }

  protected TextFieldElement toxicityField() {
    return wrap(TextFieldElement.class, findElement(className(TOXICITY)));
  }

  protected CheckBoxElement lightSensitiveField() {
    return wrap(CheckBoxElement.class, findElement(className(LIGHT_SENSITIVE)));
  }

  protected RadioButtonGroupElement storageTemperatureOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(STORAGE_TEMPERATURE)));
  }

  protected TextFieldElement sampleCountField() {
    return wrap(TextFieldElement.class, findElement(className(SAMPLE_COUNT)));
  }

  protected Integer getSampleCount() {
    if (sampleCountField().getValue().isEmpty()) {
      return null;
    } else {
      return Integer.valueOf(sampleCountField().getValue());
    }
  }

  protected void setSampleCount(Integer count) {
    sampleCountField().setValue(count != null ? count.toString() : "");
  }

  protected RadioButtonGroupElement sampleContainerTypeOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(SAMPLES_CONTAINER_TYPE)));
  }

  protected void setSampleContainerType(SampleContainerType type) {
    sampleContainerTypeOptions().setValue(type.getLabel(currentLocale()));
  }

  protected TextFieldElement plateNameField() {
    return wrap(TextFieldElement.class, findElement(className(PLATE + "-" + PLATE_NAME)));
  }

  protected LabelElement samplesLabel() {
    return wrap(LabelElement.class, findElement(className(SAMPLES_LABEL)));
  }

  protected GridElement samplesGrid() {
    return wrap(GridElement.class, findElement(className(SAMPLES)));
  }

  protected void setSampleNameInGrid(int row, String name) {
    samplesGrid().getRow(row).getCell(0).$(TextFieldElement.class).first().setValue(name);
  }

  protected WebElement samplesPlate() {
    return findElement(className(SAMPLES_PLATE));
  }

  protected PanelElement experimentPanel() {
    return wrap(PanelElement.class, findElement(className(EXPERIMENT_PANEL)));
  }

  protected TextFieldElement experimentField() {
    return wrap(TextFieldElement.class, findElement(className(EXPERIMENT)));
  }

  protected String getExperiment() {
    return experimentField().getValue();
  }

  protected void setExperiment(String experiment) {
    experimentField().setValue(experiment);
  }

  protected TextFieldElement experimentGoalField() {
    return wrap(TextFieldElement.class, findElement(className(EXPERIMENT_GOAL)));
  }

  protected String getExperimentGoal() {
    return experimentGoalField().getValue();
  }

  protected void setExperimentGoal(String goal) {
    experimentGoalField().setValue(goal);
  }

  protected TextFieldElement taxonomyField() {
    return wrap(TextFieldElement.class, findElement(className(TAXONOMY)));
  }

  protected String getTaxonomy() {
    return taxonomyField().getValue();
  }

  protected void setTaxonomy(String taxonomy) {
    taxonomyField().setValue(taxonomy);
  }

  protected TextFieldElement proteinNameField() {
    return wrap(TextFieldElement.class, findElement(className(PROTEIN_NAME)));
  }

  protected TextFieldElement proteinWeightField() {
    return wrap(TextFieldElement.class, findElement(className(PROTEIN_WEIGHT)));
  }

  protected TextFieldElement postTranslationModificationField() {
    return wrap(TextFieldElement.class, findElement(className(POST_TRANSLATION_MODIFICATION)));
  }

  protected TextFieldElement quantityField() {
    return wrap(TextFieldElement.class, findElement(className(SAMPLE_QUANTITY)));
  }

  protected String getQuantity() {
    return quantityField().getValue();
  }

  protected void setQuantity(String quantity) {
    quantityField().setValue(quantity);
  }

  protected TextFieldElement volumeField() {
    return wrap(TextFieldElement.class, findElement(className(SAMPLE_VOLUME)));
  }

  protected Double getVolume() {
    if (volumeField().getValue().isEmpty()) {
      return null;
    } else {
      return Double.valueOf(volumeField().getValue());
    }
  }

  protected void setVolume(Double volume) {
    volumeField().setValue(volume != null ? volume.toString() : "");
  }

  protected PanelElement standardsPanel() {
    return wrap(PanelElement.class, findElement(className(STANDARDS_CONTAINER)));
  }

  protected TextFieldElement standardCountField() {
    return wrap(TextFieldElement.class, standardsPanel().findElement(className(STANDARD_COUNT)));
  }

  protected Integer getStandardCount() {
    if (standardCountField().getValue().isEmpty()) {
      return null;
    } else {
      return Integer.valueOf(standardCountField().getValue());
    }
  }

  protected void setStandardCount(Integer count) {
    standardCountField().setValue(count != null ? count.toString() : "");
  }

  protected GridElement standardsGrid() {
    return wrap(GridElement.class, standardsPanel().findElement(className(STANDARDS)));
  }

  protected ButtonElement fillStandardsButton() {
    return wrap(ButtonElement.class, standardsPanel().findElement(className(FILL_STANDARDS)));
  }

  protected PanelElement contaminantsContainer() {
    return wrap(PanelElement.class, findElement(className(CONTAMINANTS_CONTAINER)));
  }

  protected TextFieldElement contaminantCountField() {
    return wrap(TextFieldElement.class,
        contaminantsContainer().findElement(className(CONTAMINANT_COUNT)));
  }

  protected Integer getContaminantCount() {
    if (contaminantCountField().getValue().isEmpty()) {
      return null;
    } else {
      return Integer.valueOf(contaminantCountField().getValue());
    }
  }

  protected void setContaminantCount(Integer count) {
    contaminantCountField().setValue(count != null ? count.toString() : "");
  }

  protected GridElement contaminantsGrid() {
    return wrap(GridElement.class, contaminantsContainer().findElement(className(CONTAMINANTS)));
  }

  protected ButtonElement fillContaminantsButton() {
    return wrap(ButtonElement.class,
        contaminantsContainer().findElement(className(FILL_CONTAMINANTS)));
  }

  protected PanelElement gelPanel() {
    return wrap(PanelElement.class, findElement(className(GEL_PANEL)));
  }

  protected ComboBoxElement separationField() {
    return wrap(ComboBoxElement.class, findElement(className(SEPARATION)));
  }

  protected ComboBoxElement thicknessField() {
    return wrap(ComboBoxElement.class, findElement(className(THICKNESS)));
  }

  protected ComboBoxElement colorationField() {
    return wrap(ComboBoxElement.class, findElement(className(COLORATION)));
  }

  protected void setColoration(GelColoration coloration) {
    colorationField().selectByText(coloration.getLabel(currentLocale()));
  }

  protected TextFieldElement otherColorationField() {
    return wrap(TextFieldElement.class, findElement(className(OTHER_COLORATION)));
  }

  protected TextFieldElement developmentTimeField() {
    return wrap(TextFieldElement.class, findElement(className(DEVELOPMENT_TIME)));
  }

  protected CheckBoxElement decolorationField() {
    return wrap(CheckBoxElement.class, findElement(className(DECOLORATION)));
  }

  protected TextFieldElement weightMarkerQuantityField() {
    return wrap(TextFieldElement.class, findElement(className(WEIGHT_MARKER_QUANTITY)));
  }

  protected TextFieldElement proteinQuantityField() {
    return wrap(TextFieldElement.class, findElement(className(PROTEIN_QUANTITY)));
  }

  protected PanelElement servicesPanel() {
    return wrap(PanelElement.class, findElement(className(SERVICES_PANEL)));
  }

  protected RadioButtonGroupElement digestionOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(DIGESTION)));
  }

  protected void setDigestion(ProteolyticDigestion digestion) {
    digestionOptions().setValue(digestion.getLabel(currentLocale()));
  }

  protected TextFieldElement usedDigestionField() {
    return wrap(TextFieldElement.class, findElement(className(USED_DIGESTION)));
  }

  protected TextFieldElement otherDigestionField() {
    return wrap(TextFieldElement.class, findElement(className(OTHER_DIGESTION)));
  }

  protected LabelElement enrichmentLabel() {
    return wrap(LabelElement.class, findElement(className(ENRICHEMENT)));
  }

  protected LabelElement exclusionsLabel() {
    return wrap(LabelElement.class, findElement(className(EXCLUSIONS)));
  }

  protected RadioButtonGroupElement injectionTypeOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(INJECTION_TYPE)));
  }

  protected RadioButtonGroupElement sourceOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(SOURCE)));
  }

  protected RadioButtonGroupElement proteinContentOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(PROTEIN_CONTENT)));
  }

  protected void setProteinContent(ProteinContent proteinContent) {
    proteinContentOptions().setValue(proteinContent.getLabel(currentLocale()));
  }

  protected RadioButtonGroupElement instrumentOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(INSTRUMENT)));
  }

  protected void setInstrument(MassDetectionInstrument instrument) {
    instrumentOptions().setValue(instrument.getLabel(currentLocale()));
  }

  protected RadioButtonGroupElement proteinIdentificationOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(PROTEIN_IDENTIFICATION)));
  }

  protected void setProteinIdentification(ProteinIdentification proteinIdentification) {
    proteinIdentificationOptions().setValue(proteinIdentification.getLabel(currentLocale()));
  }

  protected TextFieldElement proteinIdentificationLinkField() {
    return wrap(TextFieldElement.class, findElement(className(PROTEIN_IDENTIFICATION_LINK)));
  }

  protected RadioButtonGroupElement quantificationOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(QUANTIFICATION)));
  }

  protected void setQuantification(Quantification quantification) {
    quantificationOptions().selectByText(quantification.getLabel(currentLocale()));
  }

  protected TextAreaElement quantificationCommentField() {
    return wrap(TextAreaElement.class, findElement(className(QUANTIFICATION_COMMENT)));
  }

  protected RadioButtonGroupElement highResolutionOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(HIGH_RESOLUTION)));
  }

  protected CheckBoxElement acetonitrileField() {
    return wrap(CheckBoxElement.class,
        findElement(className(SOLVENTS + "-" + Solvent.ACETONITRILE.name())));
  }

  protected CheckBoxElement methanolField() {
    return wrap(CheckBoxElement.class,
        findElement(className(SOLVENTS + "-" + Solvent.METHANOL.name())));
  }

  protected CheckBoxElement chclField() {
    return wrap(CheckBoxElement.class,
        findElement(className(SOLVENTS + "-" + Solvent.CHCL3.name())));
  }

  protected CheckBoxElement otherSolventsField() {
    return wrap(CheckBoxElement.class,
        findElement(className(SOLVENTS + "-" + Solvent.OTHER.name())));
  }

  protected void setOtherSolvents(boolean value) {
    setCheckBoxValue(otherSolventsField(), true);
  }

  protected TextFieldElement otherSolventField() {
    return wrap(TextFieldElement.class, findElement(className(OTHER_SOLVENT)));
  }

  protected LabelElement otherSolventNoteLabel() {
    return wrap(LabelElement.class, findElement(className(OTHER_SOLVENT_NOTE)));
  }

  protected PanelElement commentPanel() {
    return wrap(PanelElement.class, findElement(className(COMMENT_PANEL)));
  }

  protected TextAreaElement commentField() {
    return wrap(TextAreaElement.class, findElement(className(COMMENT)));
  }

  protected String getComment() {
    return commentField().getValue();
  }

  protected void setComment(String comment) {
    commentField().setValue(comment);
  }

  protected LabelElement structureFile() {
    return wrap(LabelElement.class, findElement(className(STRUCTURE_FILE)));
  }

  protected LabelElement gelImageFile() {
    return wrap(LabelElement.class, findElement(className(GEL_IMAGE_FILE)));
  }

  protected PanelElement filesPanel() {
    return wrap(PanelElement.class, findElement(className(FILES_PANEL)));
  }

  protected WebElement filesUploader() {
    return findElement(className(FILES_UPLOADER));
  }

  protected void uploadFile(Path file) {
    uploadFile(filesUploader(), file);
  }

  protected GridElement filesGrid() {
    return wrap(GridElement.class, findElement(className(FILES)));
  }

  protected ButtonElement saveButton() {
    return wrap(ButtonElement.class, findElement(className(SAVE)));
  }

  protected void clickSaveButton() {
    saveButton().click();
  }
}
