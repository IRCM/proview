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

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.submission.web.HistoryView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialogElement;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.treatment.TreatmentType;
import ca.qc.ircm.proview.treatment.web.TreatmentDialogElement;
import ca.qc.ircm.proview.web.SigninView;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link HistoryView}.
 */
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
            new AppResources(Constants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void security_User() throws Throwable {
    open();

    Locale locale = currentLocale();
    assertEquals(
        new AppResources(AccessDeniedView.class, locale).message(TITLE,
            new AppResources(Constants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void security_Manager() throws Throwable {
    open();

    Locale locale = currentLocale();
    assertEquals(
        new AppResources(AccessDeniedView.class, locale).message(TITLE,
            new AppResources(Constants.class, locale).message(APPLICATION_NAME)),
        getDriver().getTitle());
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(HistoryView.class).message(TITLE,
        resources(Constants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.activities()).isPresent());
  }

  @Test
  public void dialog() throws Throwable {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).waitForFirst();
    view.activities().view(6).click();
    SubmissionDialogElement dialog = view.dialog();
    assertTrue(dialog.isOpen());
    assertEquals("G100429", dialog.header().getText());
  }

  @Test
  public void msAnalysisDialog() throws Throwable {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).waitForFirst();
    view.activities().view(5).click();
    MsAnalysisDialogElement dialog = view.msAnalysisDialog();
    assertTrue(dialog.isOpen());
    assertEquals(resources(MsAnalysisDialog.class).message(MsAnalysisDialog.HEADER),
        dialog.header().getText());
  }

  @Test
  public void treatmentDialog() throws Throwable {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).waitForFirst();
    view.activities().view(0).click();
    TreatmentDialogElement dialog = view.treatmentDialog();
    assertTrue(dialog.isOpen());
    assertEquals(TreatmentType.TRANSFER.getLabel(currentLocale()), dialog.header().getText());
  }
}
