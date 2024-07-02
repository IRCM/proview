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
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.id;

import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.component.formlayout.testbench.FormLayoutElement;
import com.vaadin.flow.component.radiobutton.testbench.RadioButtonGroupElement;
import com.vaadin.flow.component.textfield.testbench.TextAreaElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link IntactProteinSubmissionForm} element.
 */
@Element("vaadin-form-layout")
@Attribute(name = "id", value = IntactProteinSubmissionForm.ID)
public class IntactProteinSubmissionFormElement extends FormLayoutElement {
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

  public RadioButtonGroupElement injection() {
    return $(RadioButtonGroupElement.class).id(id(INJECTION_TYPE));
  }

  public RadioButtonGroupElement source() {
    return $(RadioButtonGroupElement.class).id(id(SOURCE));
  }

  public ComboBoxElement instrument() {
    return $(ComboBoxElement.class).id(id(INSTRUMENT));
  }
}
