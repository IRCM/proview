package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.VIEW;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.ADD;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.EDIT_STATUS;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.HISTORY;

import ca.qc.ircm.proview.sample.web.SamplesStatusDialog;
import ca.qc.ircm.proview.sample.web.SamplesStatusDialogElement;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;
import org.openqa.selenium.By;

/**
 * {@link SubmissionsView} element.
 */
@Element("vaadin-vertical-layout")
@Attribute(name = "id", value = SubmissionsView.ID)
public class SubmissionsViewElement extends VerticalLayoutElement {

  public SubmissionsViewSubmissionsElement submissions() {
    return $(SubmissionsViewSubmissionsElement.class).first();
  }

  public ButtonElement add() {
    return $(ButtonElement.class).id(ADD);
  }

  public ButtonElement view() {
    return $(ButtonElement.class).id(VIEW);
  }

  public ButtonElement editStatus() {
    return $(ButtonElement.class).id(EDIT_STATUS);
  }

  public ButtonElement history() {
    return $(ButtonElement.class).id(HISTORY);
  }

  public SubmissionDialogElement dialog() {
    return ((TestBenchElement) getDriver().findElement(By.id(SubmissionDialog.ID)))
        .wrap(SubmissionDialogElement.class);
  }

  public SamplesStatusDialogElement statusDialog() {
    return ((TestBenchElement) getDriver().findElement(By.id(SamplesStatusDialog.ID)))
        .wrap(SamplesStatusDialogElement.class);
  }
}
