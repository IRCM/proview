package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.web.SubmissionsView.SUBMISSIONS;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.grid.testbench.GridTHTDElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link SubmissionsView#submissions SubmissionsView submissions} element.
 */
@Element("vaadin-grid")
@Attribute(name = "id", value = SUBMISSIONS)
public class SubmissionsViewSubmissionsElement extends GridElement {

  private static final int EXPERIMENT_COLUMN = 0;
  private static final int VISIBLE_COLUMN = 10;

  public GridTHTDElement experimentCell(int row) {
    return getCell(row, EXPERIMENT_COLUMN);
  }

  public ButtonElement visible(int row) {
    return getCell(row, VISIBLE_COLUMN).$(ButtonElement.class).first();
  }
}
