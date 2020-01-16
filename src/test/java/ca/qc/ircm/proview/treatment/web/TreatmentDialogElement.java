package ca.qc.ircm.proview.treatment.web;

import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.DELETED;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.FRACTIONATION_TYPE;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.INSERT_TIME;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.PROTOCOL;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.TREATED_SAMPLES;
import static ca.qc.ircm.proview.treatment.web.TreatmentDialog.HEADER;

import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.html.testbench.H3Element;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-dialog")
public class TreatmentDialogElement extends DialogElement {
  public H2Element header() {
    return $(H2Element.class).attributeContains("class", HEADER).first();
  }

  public DivElement deleted() {
    return $(DivElement.class).attributeContains("class", DELETED).first();
  }

  public DivElement protocol() {
    return $(DivElement.class).attributeContains("class", PROTOCOL).first();
  }

  public DivElement fractionationType() {
    return $(DivElement.class).attributeContains("class", FRACTIONATION_TYPE).first();
  }

  public DivElement date() {
    return $(DivElement.class).attributeContains("class", INSERT_TIME).first();
  }

  public H3Element samplesHeader() {
    return $(H3Element.class).attributeContains("class", styleName(TREATED_SAMPLES, HEADER))
        .first();
  }

  public GridElement samples() {
    return $(GridElement.class).attributeContains("class", TREATED_SAMPLES).first();
  }
}
