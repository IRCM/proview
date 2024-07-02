package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.EDIT;
import static ca.qc.ircm.proview.Constants.PRINT;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionDialog.id;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.component.datepicker.testbench.DatePickerElement;
import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link SubmissionDialog} element.
 */
@Element("vaadin-dialog")
@Attribute(name = "id", value = SubmissionDialog.ID)
public class SubmissionDialogElement extends DialogElement {
  public H2Element header() {
    return $(H2Element.class).first();
  }

  public ComboBoxElement instrument() {
    return $(ComboBoxElement.class).id(id(INSTRUMENT));
  }

  public DatePickerElement dataAvailableDate() {
    return $(DatePickerElement.class).id(id(DATA_AVAILABLE_DATE));
  }

  public ButtonElement save() {
    return $(ButtonElement.class).id(id(SAVE));
  }

  public void clickSave() {
    save().click();
  }

  public ButtonElement print() {
    return $(ButtonElement.class).id(id(PRINT));
  }

  public void clickPrint() {
    print().click();
  }

  public ButtonElement edit() {
    return $(ButtonElement.class).id(id(EDIT));
  }

  public void clickEdit() {
    edit().click();
  }
}
