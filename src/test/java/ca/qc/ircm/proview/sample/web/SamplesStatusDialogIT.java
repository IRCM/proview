package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.testbench.unit.MetaKeys;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

/**
 * Integration tests for {@link SamplesStatusDialog}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class SamplesStatusDialogIT extends SpringUIUnitTest {

  private static final String SAMPLE_STATUS_PREFIX = messagePrefix(SampleStatus.class);
  @MockitoSpyBean
  private SubmissionService submissionService;
  @Autowired
  private SubmissionSampleRepository repository;
  @Autowired
  private EntityManager entityManager;

  private void detachOnServiceGet() {
    when(submissionService.get(anyLong())).then(a -> {
      @SuppressWarnings("unchecked") Optional<Submission> optionalSubmission = (Optional<Submission>) a.callRealMethod();
      optionalSubmission.ifPresent(d -> entityManager.detach(d));
      return optionalSubmission;
    });
  }

  @Test
  public void save() {
    detachOnServiceGet();
    SubmissionsView view = navigate(SubmissionsView.class);
    @SuppressWarnings("unchecked") Grid<Submission> submissions = test(view).find(Grid.class)
        .id(SubmissionsView.SUBMISSIONS);
    test(submissions).clickRow(1, new MetaKeys().shift());
    SamplesStatusDialog dialog = $(SamplesStatusDialog.class).single();
    @SuppressWarnings("unchecked") ComboBox<SampleStatus> status1 = (ComboBox<SampleStatus>) test(
        dialog.samples).getCellComponent(0, STATUS);
    test(status1).selectItem(
        dialog.getTranslation(SAMPLE_STATUS_PREFIX + SampleStatus.ANALYSED.name()));
    @SuppressWarnings("unchecked") ComboBox<SampleStatus> status2 = (ComboBox<SampleStatus>) test(
        dialog.samples).getCellComponent(1, STATUS);
    test(status2).selectItem(
        dialog.getTranslation(SAMPLE_STATUS_PREFIX + SampleStatus.DIGESTED.name()));

    test(dialog.save).click();

    assertFalse(dialog.isOpened());
    Assertions.assertEquals(SampleStatus.ANALYSED,
        repository.findById(640L).orElseThrow().getStatus());
    Assertions.assertEquals(SampleStatus.DIGESTED,
        repository.findById(641L).orElseThrow().getStatus());
    Assertions.assertEquals(SampleStatus.WAITING,
        repository.findById(642L).orElseThrow().getStatus());
  }

  @Test
  public void cancel() {
    SubmissionsView view = navigate(SubmissionsView.class);
    @SuppressWarnings("unchecked") Grid<Submission> submissions = test(view).find(Grid.class)
        .id(SubmissionsView.SUBMISSIONS);
    test(submissions).clickRow(1, new MetaKeys().shift());
    SamplesStatusDialog dialog = $(SamplesStatusDialog.class).single();
    test(dialog.cancel).click();
    assertFalse(dialog.isOpened());
  }
}
