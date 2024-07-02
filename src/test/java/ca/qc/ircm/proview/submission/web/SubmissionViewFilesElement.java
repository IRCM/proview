package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.SubmissionProperties.FILES;

import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.html.testbench.AnchorElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link SubmissionView} files element.
 */
@Element("vaadin-grid")
@Attribute(name = "id", value = FILES)
public class SubmissionViewFilesElement extends GridElement {
  private static final int FILENAME_COLUMN = 0;

  public AnchorElement filename(int row) {
    return getCell(row, FILENAME_COLUMN).$(AnchorElement.class).first();
  }
}
