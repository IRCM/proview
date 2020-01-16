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

import static ca.qc.ircm.proview.submission.web.HistoryView.ID;
import static ca.qc.ircm.proview.submission.web.HistoryView.VIEW_NAME;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialogElement;
import ca.qc.ircm.proview.security.web.AccessDeniedError;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.treatment.TreatmentType;
import ca.qc.ircm.proview.treatment.web.TreatmentDialog;
import ca.qc.ircm.proview.treatment.web.TreatmentDialogElement;
import ca.qc.ircm.proview.web.SigninView;
import ca.qc.ircm.proview.web.WebConstants;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class HistoryViewItTest extends AbstractTestBenchTestCase {
  private void open() {
    openView(VIEW_NAME, "1");
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() throws Throwable {
    open();

    Locale locale = currentLocale();
    assertEquals(
        new AppResources(SigninView.class, locale).message(TITLE,
            new AppResources(WebConstants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void security_User() throws Throwable {
    open();

    Locale locale = currentLocale();
    assertEquals(
        new AppResources(AccessDeniedError.class, locale).message(TITLE,
            new AppResources(WebConstants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void security_Manager() throws Throwable {
    open();

    Locale locale = currentLocale();
    assertEquals(
        new AppResources(AccessDeniedError.class, locale).message(TITLE,
            new AppResources(WebConstants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(HistoryView.class).message(TITLE,
        resources(WebConstants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).id(ID);
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.activities()).isPresent());
  }

  @Test
  public void dialog() throws Throwable {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).id(ID);
    view.doubleClickActivity(6);
    waitUntil(driver -> $(SubmissionDialogElement.class).id(SubmissionDialog.ID));
    SubmissionDialogElement dialog = $(SubmissionDialogElement.class).id(SubmissionDialog.ID);
    assertEquals("G100429", dialog.header().getText());
  }

  @Test
  public void msAnalysisDialog() throws Throwable {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).id(ID);
    view.doubleClickActivity(5);
    waitUntil(driver -> $(MsAnalysisDialogElement.class).id(MsAnalysisDialog.ID));
    MsAnalysisDialogElement dialog = $(MsAnalysisDialogElement.class).id(MsAnalysisDialog.ID);
    assertEquals(resources(MsAnalysisDialog.class).message(MsAnalysisDialog.HEADER),
        dialog.header().getText());
  }

  @Test
  public void treatmentDialog() throws Throwable {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).id(ID);
    view.doubleClickActivity(0);
    waitUntil(driver -> $(TreatmentDialogElement.class).id(TreatmentDialog.ID));
    TreatmentDialogElement dialog = $(TreatmentDialogElement.class).id(TreatmentDialog.ID);
    assertEquals(TreatmentType.TRANSFER.getLabel(currentLocale()), dialog.header().getText());
  }
}
