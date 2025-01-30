package ca.qc.ircm.proview.treatment.web;

import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.DELETED;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.FRACTIONATION_TYPE;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.INSERT_TIME;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.PROTOCOL;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.TREATED_SAMPLES;
import static ca.qc.ircm.proview.treatment.web.TreatmentDialog.HEADER;
import static ca.qc.ircm.proview.treatment.web.TreatmentDialog.id;

import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.html.testbench.H4Element;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link TreatmentDialog} element.
 */
@Element("vaadin-dialog")
@Attribute(name = "id", value = TreatmentDialog.ID)
public class TreatmentDialogElement extends DialogElement {

  public H2Element header() {
    return $(H2Element.class).first();
  }

  public DivElement deleted() {
    return $(DivElement.class).id(id(DELETED));
  }

  public DivElement protocol() {
    return $(DivElement.class).id(id(PROTOCOL));
  }

  public DivElement fractionationType() {
    return $(DivElement.class).id(id(FRACTIONATION_TYPE));
  }

  public DivElement date() {
    return $(DivElement.class).id(id(INSERT_TIME));
  }

  public H4Element samplesHeader() {
    return $(H4Element.class).id(id(styleName(TREATED_SAMPLES, HEADER)));
  }

  public GridElement samples() {
    return $(GridElement.class).id(id(TREATED_SAMPLES));
  }
}
