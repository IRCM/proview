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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.submission.web.SubmissionsView.ID;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.VIEW_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class SamplesStatusDialogItTest extends AbstractTestBenchTestCase {
  @Autowired
  private SubmissionSampleRepository repository;

  private void open() {
    openView(VIEW_NAME);
    SubmissionsViewElement view = $(SubmissionsViewElement.class).id(ID);
    view.clickSubmission(1, Keys.SHIFT);
    waitUntil(driver -> $(SamplesStatusDialogElement.class).id(SamplesStatusDialog.ID));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    SamplesStatusDialogElement dialog =
        $(SamplesStatusDialogElement.class).id(SamplesStatusDialog.ID);
    assertTrue(optional(() -> dialog.header()).isPresent());
    assertTrue(optional(() -> dialog.samples()).isPresent());
    assertTrue(optional(() -> dialog.allStatus()).isPresent());
    assertTrue(optional(() -> dialog.save()).isPresent());
    assertTrue(optional(() -> dialog.cancel()).isPresent());
  }

  @Test
  public void save() throws Throwable {
    open();
    SamplesStatusDialogElement dialog =
        $(SamplesStatusDialogElement.class).id(SamplesStatusDialog.ID);
    Locale locale = currentLocale();
    dialog.status(0).selectByText(SampleStatus.ANALYSED.getLabel(locale));
    dialog.status(1).selectByText(SampleStatus.DIGESTED.getLabel(locale));
    dialog.save().click();
    assertFalse(
        optional(() -> $(SamplesStatusDialogElement.class).id(SamplesStatusDialog.ID)).isPresent());
    assertEquals(SampleStatus.ANALYSED, repository.findById(640L).get().getStatus());
    assertEquals(SampleStatus.DIGESTED, repository.findById(641L).get().getStatus());
    assertEquals(SampleStatus.WAITING, repository.findById(642L).get().getStatus());
  }

  @Test
  public void cancel() throws Throwable {
    open();
    SamplesStatusDialogElement dialog =
        $(SamplesStatusDialogElement.class).id(SamplesStatusDialog.ID);
    dialog.cancel().click();
    assertFalse(
        optional(() -> $(SamplesStatusDialogElement.class).id(SamplesStatusDialog.ID)).isPresent());
  }
}
