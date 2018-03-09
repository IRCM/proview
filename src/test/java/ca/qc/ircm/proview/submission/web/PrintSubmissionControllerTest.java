package ca.qc.ircm.proview.submission.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.Model;

import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PrintSubmissionControllerTest {
  private PrintSubmissionController controller;
  @Mock
  private SubmissionService submissionService;
  @Mock
  private Model model;
  @PersistenceContext
  private EntityManager entityManager;
  private Locale locale = Locale.FRENCH;

  @Before
  public void beforeTest() {
    controller = new PrintSubmissionController(submissionService);
  }

  @Test
  public void printSubmission() {
    Submission submission = entityManager.find(Submission.class, 32L);
    when(submissionService.get(any())).thenReturn(submission);

    String resource = controller.printSubmission(submission.getId(), model, locale);

    assertEquals("submission-print", resource);
    verify(submissionService).get(submission.getId());
    verify(model).addAttribute("locale", locale);
    verify(model).addAttribute("submission", submission);
    verify(model).addAttribute("user", submission.getUser());
    verify(model).addAttribute("laboratory", submission.getLaboratory());
    verify(model).addAttribute("sample", submission.getSamples().get(0));
    verify(model, never()).addAttribute("plate");
  }

  @Test
  public void printSubmission_Plate() {
    Submission submission = entityManager.find(Submission.class, 163L);
    when(submissionService.get(any())).thenReturn(submission);

    String resource = controller.printSubmission(submission.getId(), model, locale);

    assertEquals("submission-print", resource);
    verify(submissionService).get(submission.getId());
    verify(model).addAttribute("locale", locale);
    verify(model).addAttribute("submission", submission);
    verify(model).addAttribute("user", submission.getUser());
    verify(model).addAttribute("laboratory", submission.getLaboratory());
    verify(model).addAttribute("sample", submission.getSamples().get(0));
    verify(model).addAttribute("plate",
        ((Well) submission.getSamples().get(0).getOriginalContainer()).getPlate());
  }

  @Test
  public void printSubmission_NullSubmission() {
    when(submissionService.get(any())).thenReturn(null);

    String resource = controller.printSubmission(null, model, locale);

    assertEquals("submission-print", resource);
    verify(model).addAttribute("locale", locale);
  }

  @Test
  public void printSubmissionUrl() {
    Submission submission = entityManager.find(Submission.class, 32L);

    assertEquals("/submission/print/32?lang=" + locale.getLanguage(),
        PrintSubmissionController.printSubmissionUrl(submission, locale));
  }

  @Test
  public void printSubmissionUrl_NullSubmission() {
    assertEquals("/submission/print?lang=" + locale.getLanguage(),
        PrintSubmissionController.printSubmissionUrl(null, locale));
  }

  @Test
  public void printSubmissionUrl_NullLocale() {
    Submission submission = entityManager.find(Submission.class, 32L);

    assertEquals("/submission/print/32",
        PrintSubmissionController.printSubmissionUrl(submission, null));
  }
}
