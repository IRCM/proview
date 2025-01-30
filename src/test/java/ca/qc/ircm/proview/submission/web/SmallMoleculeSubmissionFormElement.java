package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.SubmissionProperties.AVERAGE_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FORMULA;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.LIGHT_SENSITIVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MONOISOTOPIC_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOLUTION_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOLVENTS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STORAGE_TEMPERATURE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TOXICITY;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.SAMPLE_NAME;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.SAMPLE_TYPE;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.id;

import com.vaadin.flow.component.checkbox.testbench.CheckboxElement;
import com.vaadin.flow.component.checkbox.testbench.CheckboxGroupElement;
import com.vaadin.flow.component.customfield.testbench.CustomFieldElement;
import com.vaadin.flow.component.radiobutton.testbench.RadioButtonGroupElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link SmallMoleculeSubmissionForm} element.
 */
@Element("vaadin-form-layout")
@Attribute(name = "id", value = SmallMoleculeSubmissionForm.ID)
public class SmallMoleculeSubmissionFormElement extends CustomFieldElement {

  public RadioButtonGroupElement sampleType() {
    return $(RadioButtonGroupElement.class).id(id(SAMPLE_TYPE));
  }

  public TextFieldElement sampleName() {
    return $(TextFieldElement.class).id(id(SAMPLE_NAME));
  }

  public TextFieldElement solvent() {
    return $(TextFieldElement.class).id(id(SOLUTION_SOLVENT));
  }

  public TextFieldElement formula() {
    return $(TextFieldElement.class).id(id(FORMULA));
  }

  public TextFieldElement monoisotopicMass() {
    return $(TextFieldElement.class).id(id(MONOISOTOPIC_MASS));
  }

  public TextFieldElement averageMass() {
    return $(TextFieldElement.class).id(id(AVERAGE_MASS));
  }

  public TextFieldElement toxicity() {
    return $(TextFieldElement.class).id(id(TOXICITY));
  }

  public CheckboxElement lightSensitive() {
    return $(CheckboxElement.class).id(id(LIGHT_SENSITIVE));
  }

  public RadioButtonGroupElement storageTemperature() {
    return $(RadioButtonGroupElement.class).id(id(STORAGE_TEMPERATURE));
  }

  public RadioButtonGroupElement highResolution() {
    return $(RadioButtonGroupElement.class).id(id(HIGH_RESOLUTION));
  }

  public CheckboxGroupElement solvents() {
    return $(CheckboxGroupElement.class).id(id(SOLVENTS));
  }

  public TextFieldElement otherSolvent() {
    return $(TextFieldElement.class).id(id(OTHER_SOLVENT));
  }
}
