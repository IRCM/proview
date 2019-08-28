package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.sample.SampleProperties.QUANTITY;
import static ca.qc.ircm.proview.sample.SampleProperties.VOLUME;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INJECTION_TYPE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOURCE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_COUNT;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_NAMES;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_TYPE;

import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.component.formlayout.testbench.FormLayoutElement;
import com.vaadin.flow.component.radiobutton.testbench.RadioButtonGroupElement;
import com.vaadin.flow.component.textfield.testbench.TextAreaElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-form-layout")
public class IntactProteinSubmissionFormElement extends FormLayoutElement {
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

  public RadioButtonGroupElement injection() {
    return $(RadioButtonGroupElement.class).attribute("class", INJECTION_TYPE).first();
  }

  public RadioButtonGroupElement source() {
    return $(RadioButtonGroupElement.class).attribute("class", SOURCE).first();
  }

  public ComboBoxElement instrument() {
    return $(ComboBoxElement.class).attribute("class", INSTRUMENT).first();
  }
}
