package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.HEADER;
import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.SECOND_HEADER;
import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.SUBMISSIONS_VIEW;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.html.testbench.H3Element;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link PrintSubmissionView} element.
 */
@Element("vaadin-vertical-layout")
@Attribute(name = "id", value = PrintSubmissionView.ID)
public class PrintSubmissionViewElement extends VerticalLayoutElement {

  public ButtonElement submissionsView() {
    return $(ButtonElement.class).id(SUBMISSIONS_VIEW);
  }

  public H2Element header() {
    return $(H2Element.class).id(HEADER);
  }

  public H3Element secondHeader() {
    return $(H3Element.class).id(SECOND_HEADER);
  }

  public PrintSubmissionElement printSubmission() {
    return $(PrintSubmissionElement.class).first();
  }
}
