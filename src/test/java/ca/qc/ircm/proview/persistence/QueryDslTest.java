package ca.qc.ircm.proview.persistence;

import static ca.qc.ircm.proview.persistence.QueryDsl.direction;
import static ca.qc.ircm.proview.persistence.QueryDsl.qname;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link QueryDsl}.
 */
public class QueryDslTest {

  @Test
  public void qname_Test() {
    assertEquals("submission", qname(submission));
    assertEquals("user", qname(submission.user));
    assertEquals("name", qname(submission.user.name));
  }

  @Test
  public void direction_Test() {
    assertEquals(submission.experiment.asc(), direction(submission.experiment, false));
    assertEquals(submission.experiment.desc(), direction(submission.experiment, true));
    assertEquals(submission.user.name.asc(), direction(submission.user.name, false));
    assertEquals(submission.user.name.desc(), direction(submission.user.name, true));
  }
}
