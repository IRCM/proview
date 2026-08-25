package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.sample.web.SamplesStatusDialog;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.browserless.MetaKeys;
import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.ButtonVariant;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link SubmissionsView}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionsViewIT extends SpringBrowserlessTest {

  private static final String SAMPLES_STATUS_DIALOG_PREFIX = messagePrefix(
      SamplesStatusDialog.class);
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SubmissionsViewIT.class);
  @Autowired
  private SubmissionRepository repository;

  @Test
  @WithAnonymousUser
  public void security_Anonymous() {
    navigate(VIEW_NAME, SigninView.class);
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hide() {
    SubmissionsView view = navigate(SubmissionsView.class);

    test(view.submissions).invokeLitRendererFunction(0, view.hidden.getKey(), "toggleHidden");

    assertEquals(ButtonVariant.LUMO_ERROR.getVariantName(),
        test(view.submissions).getLitRendererPropertyValue(0, view.hidden.getKey(), "hiddenTheme",
            String.class));
    Submission submission = repository.findById(164L).orElseThrow();
    assertTrue(submission.isHidden());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void show() {
    SubmissionsView view = navigate(SubmissionsView.class);

    test(view.submissions).invokeLitRendererFunction(0, view.hidden.getKey(), "toggleHidden");
    test(view.submissions).invokeLitRendererFunction(0, view.hidden.getKey(), "toggleHidden");

    assertEquals(ButtonVariant.LUMO_SUCCESS.getVariantName(),
        test(view.submissions).getLitRendererPropertyValue(0, view.hidden.getKey(), "hiddenTheme",
            String.class));
    Submission submission = repository.findById(164L).orElseThrow();
    assertFalse(submission.isHidden());
  }

  @Test
  public void view() {
    SubmissionsView view = navigate(SubmissionsView.class);

    test(view.submissions).select(0);
    test(view.view).click();

    SubmissionDialog dialog = $(SubmissionDialog.class).single();
    assertTrue(dialog.isOpened());
    assertEquals("POLR3B-Flag", dialog.getHeaderTitle());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void statusDialog() {
    SubmissionsView view = navigate(SubmissionsView.class);

    test(view.submissions).clickRow(0, new MetaKeys().shift());

    SamplesStatusDialog dialog = $(SamplesStatusDialog.class).single();
    assertTrue(dialog.isOpened());
    assertEquals(view.getTranslation(SAMPLES_STATUS_DIALOG_PREFIX + SamplesStatusDialog.HEADER,
        "POLR3B-Flag"), dialog.getHeaderTitle());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void history_Grid() {
    SubmissionsView view = navigate(SubmissionsView.class);

    test(view.submissions).clickRow(0, new MetaKeys().alt());

    HistoryView historyView = $(HistoryView.class).single();
    assertEquals(164, historyView.getSubmissionId());
  }

  @Test
  public void add() {
    SubmissionsView view = navigate(SubmissionsView.class);

    test(view.add).click();

    $(SubmissionView.class).single();
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void editStatus() {
    SubmissionsView view = navigate(SubmissionsView.class);

    test(view.submissions).select(0);
    test(view.editStatus).click();

    SamplesStatusDialog dialog = $(SamplesStatusDialog.class).single();
    assertTrue(dialog.isOpened());
    assertEquals(view.getTranslation(SAMPLES_STATUS_DIALOG_PREFIX + SamplesStatusDialog.HEADER,
        "POLR3B-Flag"), dialog.getHeaderTitle());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void history() {
    SubmissionsView view = navigate(SubmissionsView.class);

    test(view.submissions).select(0);
    test(view.history).click();

    HistoryView historyView = $(HistoryView.class).single();
    assertEquals(164, historyView.getSubmissionId());
  }
}
