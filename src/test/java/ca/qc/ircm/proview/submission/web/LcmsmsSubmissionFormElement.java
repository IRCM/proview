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
import static ca.qc.ircm.proview.submission.SubmissionProperties.SEPARATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STANDARDS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.THICKNESS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USED_DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_COUNT;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_TYPE;

import com.vaadin.flow.component.checkbox.testbench.CheckboxElement;
import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.component.formlayout.testbench.FormLayoutElement;
import com.vaadin.flow.component.radiobutton.testbench.RadioButtonGroupElement;
import com.vaadin.flow.component.textfield.testbench.TextAreaElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-form-layout")
public class LcmsmsSubmissionFormElement extends FormLayoutElement {
  public TextFieldElement experiment() {
    return $(TextFieldElement.class).attribute("class", EXPERIMENT).first();
  }

  public TextFieldElement goal() {
    return $(TextFieldElement.class).attribute("class", GOAL).first();
  }

  public TextFieldElement taxonomy() {
    return $(TextFieldElement.class).attribute("class", TAXONOMY).first();
  }

  public TextFieldElement protein() {
    return $(TextFieldElement.class).attribute("class", PROTEIN).first();
  }

  public TextFieldElement molecularWeight() {
    return $(TextFieldElement.class).attribute("class", MOLECULAR_WEIGHT).first();
  }

  public TextFieldElement postTranslationModification() {
    return $(TextFieldElement.class).attribute("class", POST_TRANSLATION_MODIFICATION).first();
  }

  public RadioButtonGroupElement sampleType() {
    return $(RadioButtonGroupElement.class).attribute("class", SAMPLES_TYPE).first();
  }

  public TextFieldElement samplesCount() {
    return $(TextFieldElement.class).attribute("class", SAMPLES_COUNT).first();
  }

  public TextAreaElement samplesNames() {
    return $(TextAreaElement.class).attribute("class", SAMPLES_NAMES).first();
  }

  public TextFieldElement quantity() {
    return $(TextFieldElement.class).attribute("class", QUANTITY).first();
  }

  public TextFieldElement volume() {
    return $(TextFieldElement.class).attribute("class", VOLUME).first();
  }

  public TextAreaElement contaminants() {
    return $(TextAreaElement.class).attribute("class", CONTAMINANTS).first();
  }

  public TextAreaElement standards() {
    return $(TextAreaElement.class).attribute("class", STANDARDS).first();
  }

  public ComboBoxElement separation() {
    return $(ComboBoxElement.class).attribute("class", SEPARATION).first();
  }

  public ComboBoxElement thickness() {
    return $(ComboBoxElement.class).attribute("class", THICKNESS).first();
  }

  public ComboBoxElement coloration() {
    return $(ComboBoxElement.class).attribute("class", COLORATION).first();
  }

  public TextFieldElement otherColoration() {
    return $(TextFieldElement.class).attribute("class", OTHER_COLORATION).first();
  }

  public TextFieldElement developmentTime() {
    return $(TextFieldElement.class).attribute("class", DEVELOPMENT_TIME).first();
  }

  public CheckboxElement destained() {
    return $(CheckboxElement.class).attribute("class", DECOLORATION).first();
  }

  public TextFieldElement weightMarkerQuantity() {
    return $(TextFieldElement.class).attribute("class", WEIGHT_MARKER_QUANTITY).first();
  }

  public TextFieldElement proteinQuantity() {
    return $(TextFieldElement.class).attribute("class", PROTEIN_QUANTITY).first();
  }

  public ComboBoxElement digestion() {
    return $(ComboBoxElement.class).attribute("class", DIGESTION).first();
  }

  public TextFieldElement usedDigestion() {
    return $(TextFieldElement.class).attribute("class", USED_DIGESTION).first();
  }

  public TextFieldElement otherDigestion() {
    return $(TextFieldElement.class).attribute("class", OTHER_DIGESTION).first();
  }

  public RadioButtonGroupElement proteinContent() {
    return $(RadioButtonGroupElement.class).attribute("class", PROTEIN_CONTENT).first();
  }

  public ComboBoxElement instrument() {
    return $(ComboBoxElement.class).attribute("class", INSTRUMENT).first();
  }

  public RadioButtonGroupElement identification() {
    return $(RadioButtonGroupElement.class).attribute("class", IDENTIFICATION).first();
  }

  public TextFieldElement identificationLink() {
    return $(TextFieldElement.class).attribute("class", IDENTIFICATION_LINK).first();
  }

  public ComboBoxElement quantification() {
    return $(ComboBoxElement.class).attribute("class", QUANTIFICATION).first();
  }

  public TextAreaElement quantificationComment() {
    return $(TextAreaElement.class).attribute("class", QUANTIFICATION_COMMENT).first();
  }
}
