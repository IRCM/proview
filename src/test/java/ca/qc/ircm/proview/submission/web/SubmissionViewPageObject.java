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

import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.AVERAGE_MASS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.COLORATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.COMMENT_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.COMMENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANTS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANT_COUNT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.DECOLORATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.DEVELOPMENT_TIME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.DIGESTION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.ENRICHEMENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXCLUSIONS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPERIENCE_GOAL_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPERIENCE_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPERIENCE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES_GRID;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES_UPLOADER;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILL_CONTAMINANTS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILL_SAMPLES_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILL_STANDARDS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FORMULA_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_IMAGE_FILE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.HIGH_RESOLUTION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.INACTIVE_LABEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.INJECTION_TYPE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.INSTRUMENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.LIGHT_SENSITIVE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.MONOISOTOPIC_MASS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_COLORATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_DIGESTION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_SOLVENT_NOTE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_SOLVENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PLATE_NAME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PLATE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.POST_TRANSLATION_MODIFICATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_CONTENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_IDENTIFICATION_LINK_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_IDENTIFICATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_NAME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_QUANTITY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_WEIGHT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.QUANTIFICATION_LABELS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.QUANTIFICATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_CONTAINER_TYPE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_PLATE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_TABLE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_COUNT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_NAME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_QUANTITY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_SUPPORT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_TYPE_LABEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_VOLUME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAVE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SEPARATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICE_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOLUTION_SOLVENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOLVENTS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOURCE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARDS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARD_COUNT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARD_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STORAGE_TEMPERATURE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STRUCTURE_FILE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.TAXONOMY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.THICKNESS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.TOXICITY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.USED_DIGESTION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.WEIGHT_MARKER_QUANTITY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.HEADER_STYLE;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SampleSupport;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.ProteinContent;
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
import org.openqa.selenium.WebElement;

import java.nio.file.Path;

public abstract class SubmissionViewPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(SubmissionView.VIEW_NAME);
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER_STYLE)));
  }

  protected LabelElement sampleTypeLabel() {
    return wrap(LabelElement.class, findElement(className(SAMPLE_TYPE_LABEL)));
  }

  protected LabelElement inactiveLabel() {
    return wrap(LabelElement.class, findElement(className(INACTIVE_LABEL)));
  }

  protected PanelElement servicePanel() {
    return wrap(PanelElement.class, findElement(className(SERVICE_PANEL)));
  }

  protected RadioButtonGroupElement serviceOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(SERVICE_PROPERTY)));
  }

  protected void setService(Service service) {
    serviceOptions().setValue(service.getLabel(currentLocale()));
  }

  protected PanelElement samplesPanel() {
    return wrap(PanelElement.class, findElement(className(SAMPLES_PANEL)));
  }

  protected RadioButtonGroupElement sampleSupportOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(SAMPLE_SUPPORT_PROPERTY)));
  }

  protected void setSampleSupport(SampleSupport support) {
    sampleSupportOptions().setValue(support.getLabel(currentLocale()));
  }

  protected TextFieldElement solutionSolventField() {
    return wrap(TextFieldElement.class, findElement(className(SOLUTION_SOLVENT_PROPERTY)));
  }

  protected TextFieldElement sampleNameField() {
    return wrap(TextFieldElement.class, findElement(className(SAMPLE_NAME_PROPERTY)));
  }

  protected TextFieldElement formulaField() {
    return wrap(TextFieldElement.class, findElement(className(FORMULA_PROPERTY)));
  }

  protected TextFieldElement monoisotopicMassField() {
    return wrap(TextFieldElement.class, findElement(className(MONOISOTOPIC_MASS_PROPERTY)));
  }

  protected TextFieldElement averageMassField() {
    return wrap(TextFieldElement.class, findElement(className(AVERAGE_MASS_PROPERTY)));
  }

  protected TextFieldElement toxicityField() {
    return wrap(TextFieldElement.class, findElement(className(TOXICITY_PROPERTY)));
  }

  protected CheckBoxElement lightSensitiveField() {
    return wrap(CheckBoxElement.class, findElement(className(LIGHT_SENSITIVE_PROPERTY)));
  }

  protected RadioButtonGroupElement storageTemperatureOptions() {
    return wrap(RadioButtonGroupElement.class,
        findElement(className(STORAGE_TEMPERATURE_PROPERTY)));
  }

  protected TextFieldElement sampleCountField() {
    return wrap(TextFieldElement.class, findElement(className(SAMPLE_COUNT_PROPERTY)));
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
    return wrap(RadioButtonGroupElement.class,
        findElement(className(SAMPLES_CONTAINER_TYPE_PROPERTY)));
  }

  protected void setSampleContainerType(SampleContainerType type) {
    sampleContainerTypeOptions().setValue(type.getLabel(currentLocale()));
  }

  protected TextFieldElement plateNameField() {
    return wrap(TextFieldElement.class,
        findElement(className(PLATE_PROPERTY + "-" + PLATE_NAME_PROPERTY)));
  }

  protected LabelElement samplesLabel() {
    return wrap(LabelElement.class, findElement(className(SAMPLES_PROPERTY)));
  }

  protected GridElement samplesGrid() {
    return wrap(GridElement.class, findElement(className(SAMPLES_TABLE)));
  }

  protected void setSampleNameInGrid(int row, String name) {
    samplesGrid().getRow(row).getCell(0).$(TextFieldElement.class).first().setValue(name);
  }

  protected ButtonElement fillSamplesButton() {
    return wrap(ButtonElement.class, findElement(className(FILL_SAMPLES_PROPERTY)));
  }

  protected WebElement samplesPlate() {
    return findElement(className(SAMPLES_PLATE));
  }

  protected PanelElement experiencePanel() {
    return wrap(PanelElement.class, findElement(className(EXPERIENCE_PANEL)));
  }

  protected TextFieldElement experienceField() {
    return wrap(TextFieldElement.class, findElement(className(EXPERIENCE_PROPERTY)));
  }

  protected String getExperience() {
    return experienceField().getValue();
  }

  protected void setExperience(String experience) {
    experienceField().setValue(experience);
  }

  protected TextFieldElement experienceGoalField() {
    return wrap(TextFieldElement.class, findElement(className(EXPERIENCE_GOAL_PROPERTY)));
  }

  protected String getExperienceGoal() {
    return experienceGoalField().getValue();
  }

  protected void setExperienceGoal(String goal) {
    experienceGoalField().setValue(goal);
  }

  protected TextFieldElement taxonomyField() {
    return wrap(TextFieldElement.class, findElement(className(TAXONOMY_PROPERTY)));
  }

  protected String getTaxonomy() {
    return taxonomyField().getValue();
  }

  protected void setTaxonomy(String taxonomy) {
    taxonomyField().setValue(taxonomy);
  }

  protected TextFieldElement proteinNameField() {
    return wrap(TextFieldElement.class, findElement(className(PROTEIN_NAME_PROPERTY)));
  }

  protected TextFieldElement proteinWeightField() {
    return wrap(TextFieldElement.class, findElement(className(PROTEIN_WEIGHT_PROPERTY)));
  }

  protected TextFieldElement postTranslationModificationField() {
    return wrap(TextFieldElement.class,
        findElement(className(POST_TRANSLATION_MODIFICATION_PROPERTY)));
  }

  protected TextFieldElement quantityField() {
    return wrap(TextFieldElement.class, findElement(className(SAMPLE_QUANTITY_PROPERTY)));
  }

  protected String getQuantity() {
    return quantityField().getValue();
  }

  protected void setQuantity(String quantity) {
    quantityField().setValue(quantity);
  }

  protected TextFieldElement volumeField() {
    return wrap(TextFieldElement.class, findElement(className(SAMPLE_VOLUME_PROPERTY)));
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
    return wrap(PanelElement.class, findElement(className(STANDARDS_PANEL)));
  }

  protected TextFieldElement standardCountField() {
    return wrap(TextFieldElement.class, findElement(className(STANDARD_COUNT_PROPERTY)));
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
    return wrap(GridElement.class, findElement(className(STANDARD_PROPERTY)));
  }

  protected ButtonElement fillStandardsButton() {
    return wrap(ButtonElement.class, findElement(className(FILL_STANDARDS_PROPERTY)));
  }

  protected PanelElement contaminantsPanel() {
    return wrap(PanelElement.class, findElement(className(CONTAMINANTS_PANEL)));
  }

  protected TextFieldElement contaminantCountField() {
    return wrap(TextFieldElement.class, findElement(className(CONTAMINANT_COUNT_PROPERTY)));
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
    return wrap(GridElement.class, findElement(className(CONTAMINANT_PROPERTY)));
  }

  protected ButtonElement fillContaminantsButton() {
    return wrap(ButtonElement.class, findElement(className(FILL_CONTAMINANTS_PROPERTY)));
  }

  protected PanelElement gelPanel() {
    return wrap(PanelElement.class, findElement(className(GEL_PANEL)));
  }

  protected ComboBoxElement separationField() {
    return wrap(ComboBoxElement.class, findElement(className(SEPARATION_PROPERTY)));
  }

  protected ComboBoxElement thicknessField() {
    return wrap(ComboBoxElement.class, findElement(className(THICKNESS_PROPERTY)));
  }

  protected ComboBoxElement colorationField() {
    return wrap(ComboBoxElement.class, findElement(className(COLORATION_PROPERTY)));
  }

  protected void setColoration(GelColoration coloration) {
    colorationField().selectByText(coloration.getLabel(currentLocale()));
  }

  protected TextFieldElement otherColorationField() {
    return wrap(TextFieldElement.class, findElement(className(OTHER_COLORATION_PROPERTY)));
  }

  protected TextFieldElement developmentTimeField() {
    return wrap(TextFieldElement.class, findElement(className(DEVELOPMENT_TIME_PROPERTY)));
  }

  protected CheckBoxElement decolorationField() {
    return wrap(CheckBoxElement.class, findElement(className(DECOLORATION_PROPERTY)));
  }

  protected TextFieldElement weightMarkerQuantityField() {
    return wrap(TextFieldElement.class, findElement(className(WEIGHT_MARKER_QUANTITY_PROPERTY)));
  }

  protected TextFieldElement proteinQuantityField() {
    return wrap(TextFieldElement.class, findElement(className(PROTEIN_QUANTITY_PROPERTY)));
  }

  protected PanelElement servicesPanel() {
    return wrap(PanelElement.class, findElement(className(SERVICES_PANEL)));
  }

  protected RadioButtonGroupElement digestionOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(DIGESTION_PROPERTY)));
  }

  protected void setDigestion(ProteolyticDigestion digestion) {
    digestionOptions().setValue(digestion.getLabel(currentLocale()));
  }

  protected TextFieldElement usedDigestionField() {
    return wrap(TextFieldElement.class, findElement(className(USED_DIGESTION_PROPERTY)));
  }

  protected TextFieldElement otherDigestionField() {
    return wrap(TextFieldElement.class, findElement(className(OTHER_DIGESTION_PROPERTY)));
  }

  protected LabelElement enrichmentLabel() {
    return wrap(LabelElement.class, findElement(className(ENRICHEMENT_PROPERTY)));
  }

  protected LabelElement exclusionsLabel() {
    return wrap(LabelElement.class, findElement(className(EXCLUSIONS_PROPERTY)));
  }

  protected RadioButtonGroupElement injectionTypeOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(INJECTION_TYPE_PROPERTY)));
  }

  protected RadioButtonGroupElement sourceOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(SOURCE_PROPERTY)));
  }

  protected RadioButtonGroupElement proteinContentOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(PROTEIN_CONTENT_PROPERTY)));
  }

  protected void setProteinContent(ProteinContent proteinContent) {
    proteinContentOptions().setValue(proteinContent.getLabel(currentLocale()));
  }

  protected RadioButtonGroupElement instrumentOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(INSTRUMENT_PROPERTY)));
  }

  protected void setInstrument(MassDetectionInstrument instrument) {
    instrumentOptions().setValue(instrument.getLabel(currentLocale()));
  }

  protected RadioButtonGroupElement proteinIdentificationOptions() {
    return wrap(RadioButtonGroupElement.class,
        findElement(className(PROTEIN_IDENTIFICATION_PROPERTY)));
  }

  protected void setProteinIdentification(ProteinIdentification proteinIdentification) {
    proteinIdentificationOptions().setValue(proteinIdentification.getLabel(currentLocale()));
  }

  protected TextFieldElement proteinIdentificationLinkField() {
    return wrap(TextFieldElement.class,
        findElement(className(PROTEIN_IDENTIFICATION_LINK_PROPERTY)));
  }

  protected RadioButtonGroupElement quantificationOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(QUANTIFICATION_PROPERTY)));
  }

  protected TextAreaElement quantificationLabelsField() {
    return wrap(TextAreaElement.class, findElement(className(QUANTIFICATION_LABELS_PROPERTY)));
  }

  protected RadioButtonGroupElement highResolutionOptions() {
    return wrap(RadioButtonGroupElement.class, findElement(className(HIGH_RESOLUTION_PROPERTY)));
  }

  protected CheckBoxElement acetonitrileField() {
    return wrap(CheckBoxElement.class,
        findElement(className(SOLVENTS_PROPERTY + "-" + Solvent.ACETONITRILE.name())));
  }

  protected CheckBoxElement methanolField() {
    return wrap(CheckBoxElement.class,
        findElement(className(SOLVENTS_PROPERTY + "-" + Solvent.METHANOL.name())));
  }

  protected CheckBoxElement chclField() {
    return wrap(CheckBoxElement.class,
        findElement(className(SOLVENTS_PROPERTY + "-" + Solvent.CHCL3.name())));
  }

  protected CheckBoxElement otherSolventsField() {
    return wrap(CheckBoxElement.class,
        findElement(className(SOLVENTS_PROPERTY + "-" + Solvent.OTHER.name())));
  }

  protected void setOtherSolvents(boolean value) {
    setCheckBoxValue(otherSolventsField(), true);
  }

  protected TextFieldElement otherSolventField() {
    return wrap(TextFieldElement.class, findElement(className(OTHER_SOLVENT_PROPERTY)));
  }

  protected LabelElement otherSolventNoteLabel() {
    return wrap(LabelElement.class, findElement(className(OTHER_SOLVENT_NOTE)));
  }

  protected PanelElement commentPanel() {
    return wrap(PanelElement.class, findElement(className(COMMENT_PANEL)));
  }

  protected TextAreaElement commentField() {
    return wrap(TextAreaElement.class, findElement(className(COMMENT_PROPERTY)));
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
    return wrap(PanelElement.class, findElement(className(FILES_PROPERTY)));
  }

  protected WebElement filesUploader() {
    return findElement(className(FILES_UPLOADER));
  }

  protected void uploadFile(Path file) {
    uploadFile(filesUploader(), file);
  }

  protected GridElement filesGrid() {
    return wrap(GridElement.class, findElement(className(FILES_GRID)));
  }

  protected ButtonElement saveButton() {
    return wrap(ButtonElement.class, findElement(className(SAVE)));
  }

  protected void clickSaveButton() {
    saveButton().click();
  }
}
