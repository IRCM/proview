/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.UPLOAD;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FILES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.web.SubmissionView.HEADER;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.flow.component.tabs.testbench.TabElement;
import com.vaadin.flow.component.tabs.testbench.TabsElement;
import com.vaadin.flow.component.textfield.testbench.TextAreaElement;
import com.vaadin.flow.component.upload.testbench.UploadElement;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link SubmissionView} element.
 */
@Element("vaadin-vertical-layout")
public class SubmissionViewElement extends VerticalLayoutElement {
  private static final int FILENAME_COLUMN = 0;

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
    return $(LcmsmsSubmissionFormElement.class).id(LcmsmsSubmissionForm.ID);
  }

  public TabElement smallMolecule() {
    return $(TabElement.class).id(SMALL_MOLECULE.name());
  }

  public SmallMoleculeSubmissionFormElement smallMoleculeSubmissionForm() {
    return $(SmallMoleculeSubmissionFormElement.class).id(SmallMoleculeSubmissionForm.ID);
  }

  public TabElement intactProtein() {
    return $(TabElement.class).id(INTACT_PROTEIN.name());
  }

  public IntactProteinSubmissionFormElement intactProteinSubmissionForm() {
    return $(IntactProteinSubmissionFormElement.class).id(IntactProteinSubmissionForm.ID);
  }

  public TextAreaElement comment() {
    return $(TextAreaElement.class).id(COMMENT);
  }

  public UploadElement upload() {
    return $(UploadElement.class).id(UPLOAD);
  }

  public SubmissionViewFilesElement files() {
    return $(SubmissionViewFilesElement.class).id(FILES);
  }

  public ButtonElement save() {
    return $(ButtonElement.class).id(SAVE);
  }
}
