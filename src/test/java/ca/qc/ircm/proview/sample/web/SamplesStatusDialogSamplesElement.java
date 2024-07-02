package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.Constants.ALL;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.sample.web.SamplesStatusDialog.id;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.text.Strings.styleName;

import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link SamplesStatusDialog} samples grid element.
 */
@Element("vaadin-grid")
@Attribute(name = "id", value = SamplesStatusDialog.ID + "-" + SAMPLES)
public class SamplesStatusDialogSamplesElement extends GridElement {
  private static final int STATUS_INDEX = 1;

  public ComboBoxElement allStatus() {
    return $(ComboBoxElement.class).id(id(styleName(STATUS, ALL)));
  }

  public ComboBoxElement status(int row) {
    return getCell(row, STATUS_INDEX).$(ComboBoxElement.class).first();
  }
}
