package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialogElement;
import ca.qc.ircm.proview.treatment.web.TreatmentDialog;
import ca.qc.ircm.proview.treatment.web.TreatmentDialogElement;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;
import org.openqa.selenium.By;

/**
 * {@link HistoryView} element.
 */
@Element("vaadin-vertical-layout")
@Attribute(name = "id", value = HistoryView.ID)
public class HistoryViewElement extends VerticalLayoutElement {
  public HistoryViewActivitiesElement activities() {
    return $(HistoryViewActivitiesElement.class).first();
  }

  public SubmissionDialogElement dialog() {
    return ((TestBenchElement) getDriver().findElement(By.id(SubmissionDialog.ID)))
        .wrap(SubmissionDialogElement.class);
  }

  public MsAnalysisDialogElement msAnalysisDialog() {
    return ((TestBenchElement) getDriver().findElement(By.id(MsAnalysisDialog.ID)))
        .wrap(MsAnalysisDialogElement.class);
  }

  public TreatmentDialogElement treatmentDialog() {
    return ((TestBenchElement) getDriver().findElement(By.id(TreatmentDialog.ID)))
        .wrap(TreatmentDialogElement.class);
  }
}
