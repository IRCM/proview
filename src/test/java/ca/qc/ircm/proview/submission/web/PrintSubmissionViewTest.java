package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.HEADER;
import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.ID;
import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.SECOND_HEADER;
import static ca.qc.ircm.proview.submission.web.PrintSubmissionView.SUBMISSIONS_VIEW;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.ViewLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link PrintSubmissionView}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class PrintSubmissionViewTest extends SpringUIUnitTest {

  private static final String MESSAGES_PREFIX = messagePrefix(PrintSubmissionView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String SERVICE_PREFIX = messagePrefix(Service.class);
  private PrintSubmissionView view;
  @MockitoBean
  private SubmissionService service;
  @Autowired
  private SubmissionRepository repository;
  private Locale locale = ENGLISH;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    when(service.get(anyLong())).thenReturn(repository.findById(164L));
    when(service.print(any(), any())).thenReturn("");
    view = navigate(PrintSubmissionView.class, 164L);
  }

  @Test
  public void styles() {
    assertFalse($(ViewLayout.class).exists());
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(SUBMISSIONS_VIEW, view.submissionsView.getId().orElse(""));
    validateIcon(VaadinIcon.ARROW_BACKWARD.create(), view.submissionsView.getIcon());
    assertEquals(HEADER, view.header.getId().orElse(""));
    assertEquals(SECOND_HEADER, view.secondHeader.getId().orElse(""));
  }

  @Test
  public void labels() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SUBMISSIONS_VIEW),
        view.submissionsView.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER), view.header.getText());
    assertEquals(view.getTranslation(SERVICE_PREFIX + Service.LC_MS_MS.name()),
        view.secondHeader.getText());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SUBMISSIONS_VIEW),
        view.submissionsView.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER), view.header.getText());
    assertEquals(view.getTranslation(SERVICE_PREFIX + Service.LC_MS_MS.name()),
        view.secondHeader.getText());
  }

  @Test
  public void submissionsView() {
    test(view.submissionsView).click();
    assertTrue($(SubmissionsView.class).exists());
  }

  @Test
  public void getPageTitle() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + TITLE,
        view.getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME)), view.getPageTitle());
  }

  @Test
  public void setParameter() {
    Submission submission = repository.findById(33L).orElseThrow();
    when(service.get(anyLong())).thenReturn(Optional.of(submission));

    view.setParameter(mock(BeforeEvent.class), 33L);

    verify(service).get(33L);
    assertEquals(view.getTranslation(SERVICE_PREFIX + Service.SMALL_MOLECULE.name()),
        view.secondHeader.getText());
    assertEquals(submission, view.printContent.getSubmission());
  }

  @Test
  public void setParameter_EmptySubmission() {
    when(service.get(anyLong())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> view.setParameter(mock(BeforeEvent.class), 35L));

    verify(service).get(35L);
  }
}
