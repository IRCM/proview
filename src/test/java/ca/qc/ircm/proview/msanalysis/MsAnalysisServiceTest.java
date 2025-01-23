package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;
import static ca.qc.ircm.proview.user.UserRole.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Tests for {@link MsAnalysisService}.
 */
@ServiceTestAnnotations
@WithMockUser(authorities = ADMIN)
public class MsAnalysisServiceTest extends AbstractServiceTestCase {
  @Autowired
  private MsAnalysisService service;

  @Test
  public void get() {
    MsAnalysis msAnalysis = service.get(1L).orElseThrow();

    assertNotNull(msAnalysis);
    assertEquals((Long) 1L, msAnalysis.getId());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, msAnalysis.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.NSI, msAnalysis.getSource());
    assertEquals(LocalDateTime.of(2010, 12, 13, 14, 10, 27, 0), msAnalysis.getInsertTime());
    assertFalse(msAnalysis.isDeleted());
    assertNull(msAnalysis.getDeletionExplanation());
  }

  @Test
  @WithMockUser(authorities = { USER, MANAGER })
  public void get_NotAdmin() {
    assertThrows(AccessDeniedException.class, () -> service.get(1L));
  }

  @Test
  public void get_0() {
    assertFalse(service.get(0).isPresent());
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

    assertThrows(AccessDeniedException.class, () -> service.all(submission));
  }
}
