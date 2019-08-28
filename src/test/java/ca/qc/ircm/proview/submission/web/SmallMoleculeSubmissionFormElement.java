package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.SubmissionProperties.AVERAGE_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FORMULA;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.LIGHT_SENSITIVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MONOISOTOPIC_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOLUTION_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STORAGE_TEMPERATURE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TOXICITY;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.SAMPLE_NAME;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.SAMPLE_TYPE;

import com.vaadin.flow.component.checkbox.testbench.CheckboxElement;
import com.vaadin.flow.component.customfield.testbench.CustomFieldElement;
import com.vaadin.flow.component.radiobutton.testbench.RadioButtonGroupElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-form-layout")
public class SmallMoleculeSubmissionFormElement extends CustomFieldElement {
  public RadioButtonGroupElement sampleType() {
    return $(RadioButtonGroupElement.class).attribute("class", SAMPLE_TYPE).first();
  }

  public TextFieldElement sampleName() {
    return $(TextFieldElement.class).attribute("class", SAMPLE_NAME).first();
  }

  public TextFieldElement solvent() {
    return $(TextFieldElement.class).attribute("class", SOLUTION_SOLVENT).first();
  }

  public TextFieldElement formula() {
    return $(TextFieldElement.class).attribute("class", FORMULA).first();
  }

  public TextFieldElement monoisotopicMass() {
    return $(TextFieldElement.class).attribute("class", MONOISOTOPIC_MASS).first();
  }

  public TextFieldElement averageMass() {
    return $(TextFieldElement.class).attribute("class", AVERAGE_MASS).first();
  }

  public TextFieldElement toxicity() {
    return $(TextFieldElement.class).attribute("class", TOXICITY).first();
  }

  public CheckboxElement lightSensitive() {
    return $(CheckboxElement.class).attribute("class", LIGHT_SENSITIVE).first();
  }

  public RadioButtonGroupElement storageTemperature() {
    return $(RadioButtonGroupElement.class).attribute("class", STORAGE_TEMPERATURE).first();
  }

  public RadioButtonGroupElement highResolution() {
    return $(RadioButtonGroupElement.class).attribute("class", HIGH_RESOLUTION).first();
  }

  public SolventsFieldElement solvents() {
    return $(SolventsFieldElement.class).first();
  }

  public TextFieldElement otherSolvent() {
    return $(TextFieldElement.class).attribute("class", OTHER_SOLVENT).first();
  }
}
