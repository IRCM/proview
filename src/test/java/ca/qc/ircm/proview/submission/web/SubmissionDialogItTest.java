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

import static ca.qc.ircm.proview.submission.web.SubmissionsView.VIEW_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import java.time.LocalDate;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration tests for {@link SubmissionDialog}.
 */
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
  private MassDetectionInstrument instrument = MassDetectionInstrument.Q_EXACTIVE;
  private LocalDate dataAvailableDate = LocalDate.now().minusDays(1);

  private SubmissionDialogElement openDialog(int row) {
    openView(VIEW_NAME);
    SubmissionsViewElement view = $(SubmissionsViewElement.class).id(SubmissionsView.ID);
    view.doubleClickSubmission(row);
    return view.dialog();
  }

  private void setFields(SubmissionDialogElement dialog) {
    Locale locale = this.currentLocale();
    dialog.instrument().selectByText(instrument.getLabel(locale));
    dialog.dataAvailableDate().setDate(dataAvailableDate);
  }

  @Test
  public void fieldsExistence_User() throws Throwable {
    SubmissionDialogElement dialog = openDialog(0);
    assertTrue(optional(() -> dialog.header()).isPresent());
    assertFalse(optional(() -> dialog.instrument()).isPresent());
    assertFalse(optional(() -> dialog.dataAvailableDate()).isPresent());
    assertFalse(optional(() -> dialog.save()).isPresent());
    assertTrue(optional(() -> dialog.print()).isPresent());
    assertTrue(optional(() -> dialog.edit()).isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void fieldsExistence_Admin() throws Throwable {
    SubmissionDialogElement dialog = openDialog(0);
    assertTrue(optional(() -> dialog.header()).isPresent());
    assertTrue(optional(() -> dialog.instrument()).isPresent());
    assertTrue(optional(() -> dialog.dataAvailableDate()).isPresent());
    assertTrue(optional(() -> dialog.save()).isPresent());
    assertTrue(optional(() -> dialog.print()).isPresent());
    assertTrue(optional(() -> dialog.edit()).isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void update() throws Throwable {
    SubmissionDialogElement dialog = openDialog(0);

    setFields(dialog);

    dialog.clickSave();
    assertFalse(dialog.isOpen());
    Submission submission = repository.findById(164L).get();
    assertEquals(instrument, submission.getInstrument());
    assertEquals(dataAvailableDate, submission.getDataAvailableDate());
  }

  @Test
  public void print() throws Throwable {
    SubmissionDialogElement dialog = openDialog(0);

    dialog.clickPrint();

    assertEquals(viewUrl(PrintSubmissionView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
  }

  @Test
  public void edit() throws Throwable {
    SubmissionDialogElement dialog = openDialog(0);

    dialog.clickEdit();

    assertEquals(viewUrl(SubmissionView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
  }
}
