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

package ca.qc.ircm.proview.msanalysis.web;

import static ca.qc.ircm.proview.submission.web.HistoryView.ID;
import static ca.qc.ircm.proview.submission.web.HistoryView.VIEW_NAME;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.submission.web.HistoryViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class MsAnalysisDialogItTest extends AbstractTestBenchTestCase {
  private void open() {
    openView(VIEW_NAME, "1");
    HistoryViewElement view = $(HistoryViewElement.class).id(ID);
    view.doubleClickActivity(5);
    waitUntil(driver -> $(MsAnalysisDialogElement.class).id(MsAnalysisDialog.ID));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    MsAnalysisDialogElement dialog = $(MsAnalysisDialogElement.class).id(MsAnalysisDialog.ID);
    assertTrue(optional(() -> dialog.header()).isPresent());
    assertFalse(optional(() -> dialog.deleted()).isPresent());
    assertTrue(optional(() -> dialog.instrument()).isPresent());
    assertTrue(optional(() -> dialog.source()).isPresent());
    assertTrue(optional(() -> dialog.date()).isPresent());
    assertTrue(optional(() -> dialog.acquisitionsHeader()).isPresent());
    assertTrue(optional(() -> dialog.acquisitions()).isPresent());
  }
}
