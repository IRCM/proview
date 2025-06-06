package ca.qc.ircm.proview.msanalysis.web;

import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.ACQUISITIONS;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.DELETED;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.INSERT_TIME;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.MASS_DETECTION_INSTRUMENT;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.SOURCE;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog.HEADER;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog.id;
import static ca.qc.ircm.proview.text.Strings.styleName;

import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.html.testbench.H4Element;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link MsAnalysisDialog} element.
 */
@Element("vaadin-dialog")
@Attribute(name = "id", value = MsAnalysisDialog.ID)
public class MsAnalysisDialogElement extends DialogElement {

  public H2Element header() {
    return $(H2Element.class).first();
  }

  public DivElement deleted() {
    return $(DivElement.class).id(id(DELETED));
  }

  public DivElement instrument() {
    return $(DivElement.class).id(id(MASS_DETECTION_INSTRUMENT));
  }

  public DivElement source() {
    return $(DivElement.class).id(id(SOURCE));
  }

  public DivElement date() {
    return $(DivElement.class).id(id(INSERT_TIME));
  }

  public H4Element acquisitionsHeader() {
    return $(H4Element.class).id(id(styleName(ACQUISITIONS, HEADER)));
  }

  public GridElement acquisitions() {
    return $(GridElement.class).id(id(ACQUISITIONS));
  }
}
