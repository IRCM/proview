package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionWebFilterTest {
  private SubmissionWebFilter filter;
  private Locale locale = Locale.FRENCH;

  @Before
  public void beforeTest() {
    filter = new SubmissionWebFilter(locale);
  }

  // TODO Program tests.
}
