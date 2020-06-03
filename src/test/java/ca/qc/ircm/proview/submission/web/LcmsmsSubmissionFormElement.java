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
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.id;

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
    return $(TextFieldElement.class).id(id(EXPERIMENT));
  }

  public TextFieldElement goal() {
    return $(TextFieldElement.class).id(id(GOAL));
  }

  public TextFieldElement taxonomy() {
    return $(TextFieldElement.class).id(id(TAXONOMY));
  }

  public TextFieldElement protein() {
    return $(TextFieldElement.class).id(id(PROTEIN));
  }

  public TextFieldElement molecularWeight() {
    return $(TextFieldElement.class).id(id(MOLECULAR_WEIGHT));
  }

  public TextFieldElement postTranslationModification() {
    return $(TextFieldElement.class).id(id(POST_TRANSLATION_MODIFICATION));
  }

  public RadioButtonGroupElement sampleType() {
    return $(RadioButtonGroupElement.class).id(id(SAMPLES_TYPE));
  }

  public TextFieldElement samplesCount() {
    return $(TextFieldElement.class).id(id(SAMPLES_COUNT));
  }

  public TextAreaElement samplesNames() {
    return $(TextAreaElement.class).id(id(SAMPLES_NAMES));
  }

  public TextFieldElement quantity() {
    return $(TextFieldElement.class).id(id(QUANTITY));
  }

  public TextFieldElement volume() {
    return $(TextFieldElement.class).id(id(VOLUME));
  }

  public TextAreaElement contaminants() {
    return $(TextAreaElement.class).id(id(CONTAMINANTS));
  }

  public TextAreaElement standards() {
    return $(TextAreaElement.class).id(id(STANDARDS));
  }

  public ComboBoxElement separation() {
    return $(ComboBoxElement.class).id(id(SEPARATION));
  }

  public ComboBoxElement thickness() {
    return $(ComboBoxElement.class).id(id(THICKNESS));
  }

  public ComboBoxElement coloration() {
    return $(ComboBoxElement.class).id(id(COLORATION));
  }

  public TextFieldElement otherColoration() {
    return $(TextFieldElement.class).id(id(OTHER_COLORATION));
  }

  public TextFieldElement developmentTime() {
    return $(TextFieldElement.class).id(id(DEVELOPMENT_TIME));
  }

  public CheckboxElement destained() {
    return $(CheckboxElement.class).id(id(DECOLORATION));
  }

  public TextFieldElement weightMarkerQuantity() {
    return $(TextFieldElement.class).id(id(WEIGHT_MARKER_QUANTITY));
  }

  public TextFieldElement proteinQuantity() {
    return $(TextFieldElement.class).id(id(PROTEIN_QUANTITY));
  }

  public ComboBoxElement digestion() {
    return $(ComboBoxElement.class).id(id(DIGESTION));
  }

  public TextFieldElement usedDigestion() {
    return $(TextFieldElement.class).id(id(USED_DIGESTION));
  }

  public TextFieldElement otherDigestion() {
    return $(TextFieldElement.class).id(id(OTHER_DIGESTION));
  }

  public RadioButtonGroupElement proteinContent() {
    return $(RadioButtonGroupElement.class).id(id(PROTEIN_CONTENT));
  }

  public ComboBoxElement instrument() {
    return $(ComboBoxElement.class).id(id(INSTRUMENT));
  }

  public RadioButtonGroupElement identification() {
    return $(RadioButtonGroupElement.class).id(id(IDENTIFICATION));
  }

  public TextFieldElement identificationLink() {
    return $(TextFieldElement.class).id(id(IDENTIFICATION_LINK));
  }

  public ComboBoxElement quantification() {
    return $(ComboBoxElement.class).id(id(QUANTIFICATION));
  }

  public TextAreaElement quantificationComment() {
    return $(TextAreaElement.class).id(id(QUANTIFICATION_COMMENT));
  }
}
