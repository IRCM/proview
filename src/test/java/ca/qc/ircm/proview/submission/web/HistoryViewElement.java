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

import static ca.qc.ircm.proview.submission.web.HistoryView.HEADER;

import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialogElement;
import ca.qc.ircm.proview.treatment.web.TreatmentDialog;
import ca.qc.ircm.proview.treatment.web.TreatmentDialogElement;
import com.vaadin.flow.component.html.testbench.H2Element;
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
  public H2Element header() {
    return $(H2Element.class).id(HEADER);
  }

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
