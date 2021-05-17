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

package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;
import static ca.qc.ircm.proview.user.UserRole.USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for {@link MsAnalysisService}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
@WithMockUser(authorities = ADMIN)
public class MsAnalysisServiceTest extends AbstractServiceTestCase {
  @Autowired
  private MsAnalysisService service;

  @Test
  public void get() {
    MsAnalysis msAnalysis = service.get(1L).orElse(null);

    assertNotNull(msAnalysis);
    assertEquals((Long) 1L, msAnalysis.getId());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, msAnalysis.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.NSI, msAnalysis.getSource());
    assertEquals(LocalDateTime.of(2010, 12, 13, 14, 10, 27, 0), msAnalysis.getInsertTime());
    assertEquals(false, msAnalysis.isDeleted());
    assertEquals(null, msAnalysis.getDeletionExplanation());
  }

  @Test
  @WithMockUser(authorities = { USER, MANAGER })
  public void get_NotAdmin() {
    assertThrows(AccessDeniedException.class, () -> {
      service.get(1L);
    });
  }

  @Test
  public void get_Null() {
    assertFalse(service.get((Long) null).isPresent());
  }

  @Test
  public void all_Submission() {
    Submission submission = new Submission(155L);

    List<MsAnalysis> msAnalyses = service.all(submission);

    assertEquals(1, msAnalyses.size());
    assertTrue(find(msAnalyses, 21).isPresent());
  }

  @Test
  @WithMockUser(authorities = { USER, MANAGER })
  public void all_NotAdmin() {
    Submission submission = new Submission(155L);

    assertThrows(AccessDeniedException.class, () -> {
      service.all(submission);
    });
  }

  @Test
  public void all_SubmissionNull() {
    List<MsAnalysis> msAnalyses = service.all((Submission) null);

    assertEquals(0, msAnalyses.size());
  }
}
