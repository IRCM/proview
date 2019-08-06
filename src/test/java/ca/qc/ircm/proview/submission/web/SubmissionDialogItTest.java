/*
 * Copyright (c) 2018 Institut de recherches cliniques de Montreal (IRCM)
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

import static ca.qc.ircm.proview.submission.web.SubmissionDialog.ID;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.VIEW_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import java.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionDialogItTest extends AbstractTestBenchTestCase {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SubmissionDialogItTest.class);
  @Autowired
  private SubmissionRepository repository;
  @Value("${spring.application.name}")
  private String applicationName;
  private LocalDate sampleDeliveryDate = LocalDate.now().minusDays(8);
  private LocalDate digestionDate = LocalDate.now().minusDays(5);
  private LocalDate analysisDate = LocalDate.now().minusDays(3);
  private LocalDate dataAvailableDate = LocalDate.now().minusDays(1);

  private void openDialog(int row) {
    openView(VIEW_NAME);
    SubmissionsViewElement view = $(SubmissionsViewElement.class).id(SubmissionsView.ID);
    view.doubleClickSubmission(row);
    waitUntil(driver -> $(SubmissionDialogElement.class).id(SubmissionDialog.ID));
  }

  private void setFields(SubmissionDialogElement dialog) {
    dialog.sampleDeliveryDate().setDate(sampleDeliveryDate);
    dialog.digestionDate().setDate(digestionDate);
    dialog.analysisDate().setDate(analysisDate);
    dialog.dataAvailableDate().setDate(dataAvailableDate);
  }

  @Test
  public void fieldsExistence() throws Throwable {
    openDialog(0);
    SubmissionDialogElement dialog = $(SubmissionDialogElement.class).id(ID);
    assertTrue(optional(() -> dialog.header()).isPresent());
    assertTrue(optional(() -> dialog.sampleDeliveryDate()).isPresent());
    assertTrue(optional(() -> dialog.digestionDate()).isPresent());
    assertTrue(optional(() -> dialog.analysisDate()).isPresent());
    assertTrue(optional(() -> dialog.dataAvailableDate()).isPresent());
    assertTrue(optional(() -> dialog.save()).isPresent());
    assertTrue(optional(() -> dialog.print()).isPresent());
    assertTrue(optional(() -> dialog.edit()).isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void update() throws Throwable {
    openDialog(0);
    SubmissionDialogElement dialog = $(SubmissionDialogElement.class).id(ID);

    setFields(dialog);

    dialog.clickSave();
    waitUntil(driver -> $(SubmissionDialogElement.class).all().isEmpty());
    Submission submission = repository.findById(164L).get();
    assertEquals(sampleDeliveryDate, submission.getSampleDeliveryDate());
    assertEquals(digestionDate, submission.getDigestionDate());
    assertEquals(analysisDate, submission.getAnalysisDate());
    assertEquals(dataAvailableDate, submission.getDataAvailableDate());
  }

  @Test
  public void print() throws Throwable {
    openDialog(0);
    SubmissionDialogElement dialog = $(SubmissionDialogElement.class).id(ID);

    dialog.clickPrint();

    assertEquals(viewUrl(PrintSubmissionView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
  }

  @Test
  public void edit() throws Throwable {
    openDialog(0);
    SubmissionDialogElement dialog = $(SubmissionDialogElement.class).id(ID);

    dialog.clickEdit();

    assertEquals(viewUrl(SubmissionView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
  }
}
