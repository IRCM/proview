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

import static ca.qc.ircm.proview.submission.web.SubmissionsView.ADD;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.EDIT_STATUS;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.HEADER;

import ca.qc.ircm.proview.sample.web.SamplesStatusDialog;
import ca.qc.ircm.proview.sample.web.SamplesStatusDialogElement;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;
import org.openqa.selenium.By;

/**
 * {@link SubmissionsView} element.
 */
@Element("vaadin-vertical-layout")
public class SubmissionsViewElement extends VerticalLayoutElement {
  public H2Element header() {
    return $(H2Element.class).id(HEADER);
  }

  public SubmissionsViewSubmissionsElement submissions() {
    return $(SubmissionsViewSubmissionsElement.class).first();
  }

  public ButtonElement add() {
    return $(ButtonElement.class).id(ADD);
  }

  public ButtonElement editStatus() {
    return $(ButtonElement.class).id(EDIT_STATUS);
  }

  public SubmissionDialogElement dialog() {
    return ((TestBenchElement) getDriver().findElement(By.id(SubmissionDialog.ID)))
        .wrap(SubmissionDialogElement.class);
  }

  public SamplesStatusDialogElement statusDialog() {
    return ((TestBenchElement) getDriver().findElement(By.id(SamplesStatusDialog.ID)))
        .wrap(SamplesStatusDialogElement.class);
  }
}
