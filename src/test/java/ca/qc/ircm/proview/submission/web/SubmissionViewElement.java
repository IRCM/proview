package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.UPLOAD;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.flow.component.tabs.testbench.TabElement;
import com.vaadin.flow.component.tabs.testbench.TabsElement;
import com.vaadin.flow.component.textfield.testbench.TextAreaElement;
import com.vaadin.flow.component.upload.testbench.UploadElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link SubmissionView} element.
 */
@Element("vaadin-vertical-layout")
@Attribute(name = "id", value = SubmissionView.ID)
public class SubmissionViewElement extends VerticalLayoutElement {
  public TabsElement service() {
    return $(TabsElement.class).id(SERVICE);
  }

  public TabElement lcmsms() {
    return $(TabElement.class).id(LC_MS_MS.name());
  }

  public LcmsmsSubmissionFormElement lcmsmsSubmissionForm() {
    return $(LcmsmsSubmissionFormElement.class).first();
  }

  public TabElement smallMolecule() {
    return $(TabElement.class).id(SMALL_MOLECULE.name());
  }

  public SmallMoleculeSubmissionFormElement smallMoleculeSubmissionForm() {
    return $(SmallMoleculeSubmissionFormElement.class).first();
  }

  public TabElement intactProtein() {
    return $(TabElement.class).id(INTACT_PROTEIN.name());
  }

  public IntactProteinSubmissionFormElement intactProteinSubmissionForm() {
    return $(IntactProteinSubmissionFormElement.class).first();
  }

  public TextAreaElement comment() {
    return $(TextAreaElement.class).id(COMMENT);
  }

  public UploadElement upload() {
    return $(UploadElement.class).id(UPLOAD);
  }

  public SubmissionViewFilesElement files() {
    return $(SubmissionViewFilesElement.class).first();
  }

  public ButtonElement save() {
    return $(ButtonElement.class).id(SAVE);
  }
}
