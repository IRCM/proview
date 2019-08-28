package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.treatment.Solvent.ACETONITRILE;
import static ca.qc.ircm.proview.treatment.Solvent.CHCL3;
import static ca.qc.ircm.proview.treatment.Solvent.METHANOL;
import static ca.qc.ircm.proview.treatment.Solvent.OTHER;

import ca.qc.ircm.proview.treatment.Solvent;
import com.vaadin.flow.component.checkbox.testbench.CheckboxElement;
import com.vaadin.flow.component.formlayout.testbench.FormLayoutElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-custom-field")
public class SolventsFieldElement extends FormLayoutElement {
  public CheckboxElement solvent(Solvent solvent) {
    return $(CheckboxElement.class).attribute("class", solvent.name()).first();
  }

  public CheckboxElement methanol() {
    return $(CheckboxElement.class).attribute("class", METHANOL.name()).first();
  }

  public CheckboxElement acetonitrile() {
    return $(CheckboxElement.class).attribute("class", ACETONITRILE.name()).first();
  }

  public CheckboxElement chcl3() {
    return $(CheckboxElement.class).attribute("class", CHCL3.name()).first();
  }

  public CheckboxElement other() {
    return $(CheckboxElement.class).attribute("class", OTHER.name()).first();
  }
}
