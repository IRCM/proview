package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FILES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.web.SubmissionView.HEADER;
import static ca.qc.ircm.proview.web.WebConstants.SAVE;
import static ca.qc.ircm.proview.web.WebConstants.UPLOAD;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.flow.component.tabs.testbench.TabElement;
import com.vaadin.flow.component.tabs.testbench.TabsElement;
import com.vaadin.flow.component.textfield.testbench.TextAreaElement;
import com.vaadin.flow.component.upload.testbench.UploadElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-vertical-layout")
public class SubmissionViewElement extends VerticalLayoutElement {
  protected LcmsmsSubmissionForm lcmsmsSubmissionForm;
  protected SmallMoleculeSubmissionForm smallMoleculeSubmissionForm;
  protected IntactProteinSubmissionForm intactProteinSubmissionForm;

  public H2Element header() {
    return $(H2Element.class).id(HEADER);
  }

  public TabsElement service() {
    return $(TabsElement.class).id(SERVICE);
  }

  public TabElement lcmsms() {
    return $(TabElement.class).id(LC_MS_MS.name());
  }

  public LcmsmsSubmissionFormElement lcmsmsSubmissionForm() {
    return $(LcmsmsSubmissionFormElement.class).attribute("class", LcmsmsSubmissionForm.CLASS_NAME)
        .first();
  }

  public TabElement smallMolecule() {
    return $(TabElement.class).id(SMALL_MOLECULE.name());
  }

  public SmallMoleculeSubmissionFormElement smallMoleculeSubmissionForm() {
    return $(SmallMoleculeSubmissionFormElement.class)
        .attribute("class", SmallMoleculeSubmissionForm.CLASS_NAME).first();
  }

  public TabElement intactProtein() {
    return $(TabElement.class).id(INTACT_PROTEIN.name());
  }

  public IntactProteinSubmissionFormElement intactProteinSubmissionForm() {
    return $(IntactProteinSubmissionFormElement.class)
        .attribute("class", IntactProteinSubmissionForm.CLASS_NAME).first();
  }

  public TextAreaElement comment() {
    return $(TextAreaElement.class).id(COMMENT);
  }

  public UploadElement upload() {
    return $(UploadElement.class).id(UPLOAD);
  }

  public GridElement files() {
    return $(GridElement.class).id(FILES);
  }

  public ButtonElement save() {
    return $(ButtonElement.class).id(SAVE);
  }
}
