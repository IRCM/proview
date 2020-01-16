package ca.qc.ircm.proview.msanalysis.web;

import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.ACQUISITIONS;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.DELETED;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.INSERT_TIME;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.MASS_DETECTION_INSTRUMENT;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.SOURCE;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog.HEADER;
import static ca.qc.ircm.proview.text.Strings.styleName;

import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.html.testbench.H3Element;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-dialog")
public class MsAnalysisDialogElement extends DialogElement {
  public H2Element header() {
    return $(H2Element.class).attributeContains("class", HEADER).first();
  }

  public DivElement deleted() {
    return $(DivElement.class).attributeContains("class", DELETED).first();
  }

  public DivElement instrument() {
    return $(DivElement.class).attributeContains("class", MASS_DETECTION_INSTRUMENT).first();
  }

  public DivElement source() {
    return $(DivElement.class).attributeContains("class", SOURCE).first();
  }

  public DivElement date() {
    return $(DivElement.class).attributeContains("class", INSERT_TIME).first();
  }

  public H3Element acquisitionsHeader() {
    return $(H3Element.class).attributeContains("class", styleName(ACQUISITIONS, HEADER)).first();
  }

  public GridElement acquisitions() {
    return $(GridElement.class).attributeContains("class", ACQUISITIONS).first();
  }
}
