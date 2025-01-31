package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.Constants.CANCEL;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.sample.web.SamplesStatusDialog.id;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link SamplesStatusDialog} element.
 */
@Element("vaadin-dialog")
@Attribute(name = "id", value = SamplesStatusDialog.ID)
public class SamplesStatusDialogElement extends DialogElement {

  public H2Element header() {
    return $(H2Element.class).first();
  }

  public SamplesStatusDialogSamplesElement samples() {
    return $(SamplesStatusDialogSamplesElement.class).first();
  }

  public ButtonElement save() {
    return $(ButtonElement.class).id(id(SAVE));
  }

  public ButtonElement cancel() {
    return $(ButtonElement.class).id(id(CANCEL));
  }
}
