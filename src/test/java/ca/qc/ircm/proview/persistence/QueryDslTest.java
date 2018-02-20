package ca.qc.ircm.proview.persistence;

import static ca.qc.ircm.proview.persistence.QueryDsl.direction;
import static ca.qc.ircm.proview.persistence.QueryDsl.qname;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QueryDslTest {
  @Test
  public void qname_Test() {
    assertEquals("submission", qname(submission));
    assertEquals("user", qname(submission.user));
    assertEquals("name", qname(submission.user.name));
  }

  @Test
  public void direction_Test() {
    assertEquals(submission.experience.asc(), direction(submission.experience, false));
    assertEquals(submission.experience.desc(), direction(submission.experience, true));
    assertEquals(submission.user.name.asc(), direction(submission.user.name, false));
    assertEquals(submission.user.name.desc(), direction(submission.user.name, true));
  }
}
