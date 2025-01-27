package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.Constants.ALL;
import static ca.qc.ircm.proview.Constants.CANCEL;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.sample.SampleProperties.NAME;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.sample.web.SamplesStatusDialog.HEADER;
import static ca.qc.ircm.proview.sample.web.SamplesStatusDialog.ID;
import static ca.qc.ircm.proview.sample.web.SamplesStatusDialog.SAVED;
import static ca.qc.ircm.proview.sample.web.SamplesStatusDialog.id;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SavedEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.testbench.unit.MetaKeys;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link SamplesStatusDialog}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class SamplesStatusDialogTest extends SpringUIUnitTest {
  private static final String MESSAGES_PREFIX = messagePrefix(SamplesStatusDialog.class);
  private static final String SAMPLE_PREFIX = messagePrefix(Sample.class);
  private static final String SUBMISSION_SAMPLE_PREFIX = messagePrefix(SubmissionSample.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String SAMPLE_STATUS_PREFIX = messagePrefix(SampleStatus.class);
  private SamplesStatusDialog dialog;
  @MockitoBean
  private SubmissionService service;
  @MockitoBean
  private SubmissionSampleService sampleService;
  @Mock
  private ComponentEventListener<SavedEvent<SamplesStatusDialog>> savedListener;
  @Autowired
  private SubmissionRepository repository;
  @Autowired
  private SubmissionSampleRepository sampleRepository;
  private Locale locale = ENGLISH;
  private List<SubmissionSample> samples;
  private SampleStatus status1 = SampleStatus.ANALYSED;
  private SampleStatus status2 = SampleStatus.DIGESTED;

  /**
   * Before tests.
   */
  @BeforeEach
  public void beforeTest() {
    when(service.get(anyLong())).then(
        i -> i.getArgument(0) != null ? repository.findById(i.getArgument(0)) : Optional.empty());
    UI.getCurrent().setLocale(locale);
    SubmissionsView view = navigate(SubmissionsView.class);
    @SuppressWarnings("unchecked")
    Grid<Submission> submissions = test(view).find(Grid.class).id(SubmissionsView.SUBMISSIONS);
    submissions.setItems(repository.findAll());
    test(submissions).clickRow(18, new MetaKeys().shift());
    dialog = $(SamplesStatusDialog.class).id(ID);
    samples = sampleRepository.findAll();
  }

  private int indexOfColumn(String property) {
    return test(dialog.samples).getColumnPosition(property);
  }

  private void setFields() {
    List<SubmissionSample> samples = items(dialog.samples);
    dialog.status(samples.get(0)).setValue(status1);
    samples.stream().skip(1).forEach(sample -> dialog.status(sample).setValue(status2));
  }

  @Test
  public void styles() {
    assertEquals(ID, dialog.getId().orElse(""));
    assertEquals(id(SAMPLES), dialog.samples.getId().orElse(""));
    assertEquals(id(styleName(STATUS, ALL)), dialog.allStatus.getId().orElse(""));
    assertEquals(id(SAVE), dialog.save.getId().orElse(""));
    assertTrue(dialog.save.getThemeNames().contains(ButtonVariant.LUMO_PRIMARY.getVariantName()));
    assertEquals(id(CANCEL), dialog.cancel.getId().orElse(""));
  }

  @Test
  public void labels() {
    Submission submission = repository.findById(163L).orElseThrow();
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + HEADER, submission.getExperiment()),
        dialog.getHeaderTitle());
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + property(STATUS, ALL)),
        dialog.allStatus.getLabel());
    assertEquals(dialog.getTranslation(CONSTANTS_PREFIX + SAVE), dialog.save.getText());
    assertEquals(dialog.getTranslation(CONSTANTS_PREFIX + CANCEL), dialog.cancel.getText());
    HeaderRow header = dialog.samples.getHeaderRows().get(0);
    FooterRow footer = dialog.samples.getFooterRows().get(0);
    assertEquals(dialog.getTranslation(SAMPLE_PREFIX + NAME),
        header.getCell(dialog.name).getText());
    assertEquals(dialog.getTranslation(SAMPLE_PREFIX + NAME),
        footer.getCell(dialog.name).getText());
    assertEquals(dialog.getTranslation(SUBMISSION_SAMPLE_PREFIX + STATUS),
        header.getCell(dialog.status).getText());
    assertEquals(dialog.getTranslation(SUBMISSION_SAMPLE_PREFIX + STATUS),
        footer.getCell(dialog.status).getText());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    Submission submission = repository.findById(163L).orElseThrow();
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + HEADER, submission.getExperiment()),
        dialog.getHeaderTitle());
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + property(STATUS, ALL)),
        dialog.allStatus.getLabel());
    assertEquals(dialog.getTranslation(CONSTANTS_PREFIX + SAVE), dialog.save.getText());
    assertEquals(dialog.getTranslation(CONSTANTS_PREFIX + CANCEL), dialog.cancel.getText());
    HeaderRow header = dialog.samples.getHeaderRows().get(0);
    FooterRow footer = dialog.samples.getFooterRows().get(0);
    assertEquals(dialog.getTranslation(SAMPLE_PREFIX + NAME),
        header.getCell(dialog.name).getText());
    assertEquals(dialog.getTranslation(SAMPLE_PREFIX + NAME),
        footer.getCell(dialog.name).getText());
    assertEquals(dialog.getTranslation(SUBMISSION_SAMPLE_PREFIX + STATUS),
        header.getCell(dialog.status).getText());
    assertEquals(dialog.getTranslation(SUBMISSION_SAMPLE_PREFIX + STATUS),
        footer.getCell(dialog.status).getText());
  }

  @Test
  public void samples_Columns() {
    assertEquals(2, dialog.samples.getColumns().size());
    assertNotNull(dialog.samples.getColumnByKey(NAME));
    assertTrue(dialog.samples.getColumnByKey(NAME).isSortable());
    assertNotNull(dialog.samples.getColumnByKey(STATUS));
    assertFalse(dialog.samples.getColumnByKey(STATUS).isSortable());
  }

  @Test
  public void samples_ColumnsValueProvider() {
    Submission submission = repository.findById(163L).orElseThrow();
    submission.setSamples(samples);
    dialog.setSubmissionId(163L);
    for (int i = 0; i < samples.size(); i++) {
      SubmissionSample sample = samples.get(i);
      assertEquals(sample.getName(), test(dialog.samples).getCellText(i, indexOfColumn(NAME)));
      @SuppressWarnings("unchecked")
      ComboBox<SampleStatus> statusBox =
          test(test(dialog.samples).getCellComponent(i, STATUS)).find(ComboBox.class).first();
      assertEquals(sample.getStatus(), statusBox.getValue(), i + ", " + sample);
      assertTrue(statusBox.hasClassName(STATUS));
      assertTrue(statusBox.isRequiredIndicatorVisible());
      List<SampleStatus> statuses = items(statusBox);
      assertEquals(Arrays.asList(SampleStatus.values()), statuses);
      for (SampleStatus status : statuses) {
        assertEquals(dialog.getTranslation(SAMPLE_STATUS_PREFIX + status.name()),
            statusBox.getItemLabelGenerator().apply(status));
      }
      assertSame(statusBox, dialog.status(sample));
    }
  }

  @Test
  public void samples_NameColumnComparator() {
    Comparator<SubmissionSample> nameComparator =
        test(dialog.samples).getColumn(NAME).getComparator(SortDirection.ASCENDING);
    assertEquals(0, nameComparator.compare(new SubmissionSample("éê"), new SubmissionSample("ee")));
    assertTrue(nameComparator.compare(new SubmissionSample("a"), new SubmissionSample("e")) < 0);
    assertTrue(nameComparator.compare(new SubmissionSample("a"), new SubmissionSample("é")) < 0);
    assertTrue(nameComparator.compare(new SubmissionSample("e"), new SubmissionSample("a")) > 0);
    assertTrue(nameComparator.compare(new SubmissionSample("é"), new SubmissionSample("a")) > 0);
  }

  @Test
  public void allStatus() {
    assertTrue(dialog.allStatus.isClearButtonVisible());
    assertFalse(dialog.allStatus.isRequiredIndicatorVisible());
  }

  @Test
  public void allStatus_Changed() {
    Submission submission = repository.findById(163L).orElseThrow();
    assertTrue(
        submission.getSamples().stream().anyMatch(sa -> sa.getStatus() != SampleStatus.ANALYSED));
    dialog.allStatus.setValue(SampleStatus.ANALYSED);
    for (SubmissionSample sample : submission.getSamples()) {
      assertEquals(SampleStatus.ANALYSED, dialog.status(sample).getValue());
    }
  }

  @Test
  public void allStatus_Clear() {
    Submission submission = repository.findById(163L).orElseThrow();
    dialog.allStatus.setValue(SampleStatus.ANALYSED);
    dialog.allStatus.clear();
    for (SubmissionSample sample : submission.getSamples()) {
      assertEquals(SampleStatus.ANALYSED, dialog.status(sample).getValue());
    }
  }

  @Test
  public void allStatus_ResetOnOpen() {
    dialog.allStatus.setValue(SampleStatus.ANALYSED);
    dialog.close();
    dialog.open();
    assertNull(dialog.allStatus.getValue());
  }

  @Test
  public void savedListener() {
    dialog.addSavedListener(savedListener);
    dialog.fireSavedEvent();
    verify(savedListener).onComponentEvent(any());
  }

  @Test
  public void savedListener_Remove() {
    dialog.addSavedListener(savedListener).remove();
    dialog.fireSavedEvent();
    verify(savedListener, never()).onComponentEvent(any());
  }

  @Test
  public void save_EmptySamples() {
    Submission submission = repository.findById(163L).orElseThrow();
    submission.setSamples(new ArrayList<>());
    dialog.setSubmissionId(163L);
    dialog.save.click();
    verify(sampleService, never()).updateStatus(any());
  }

  @Test
  public void save_EmptyStatus_First() {
    Submission submission = repository.findById(163L).orElseThrow();
    setFields();
    SubmissionSample sample = submission.getSamples().get(0);
    dialog.status(sample).setValue(null);
    dialog.save.click();
    verify(sampleService, never()).updateStatus(any());
    List<BinderValidationStatus<SubmissionSample>> statuses = dialog.validateSamples();
    BinderValidationStatus<SubmissionSample> status = statuses.get(0);
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, dialog.status(sample));
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(dialog.getTranslation(CONSTANTS_PREFIX + REQUIRED)),
        error.getMessage());
  }

  @Test
  public void save_EmptyStatus_Second() {
    Submission submission = repository.findById(163L).orElseThrow();
    setFields();
    SubmissionSample sample = submission.getSamples().get(1);
    dialog.status(sample).setValue(null);
    dialog.save.click();
    verify(sampleService, never()).updateStatus(any());
    List<BinderValidationStatus<SubmissionSample>> statuses = dialog.validateSamples();
    BinderValidationStatus<SubmissionSample> status = statuses.get(1);
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, dialog.status(sample));
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(dialog.getTranslation(CONSTANTS_PREFIX + REQUIRED)),
        error.getMessage());
  }

  @Test
  public void save() {
    Submission submission = repository.findById(163L).orElseThrow();
    dialog.addSavedListener(savedListener);
    setFields();
    dialog.save.click();
    verify(sampleService).updateStatus(submission.getSamples());
    Notification notification = $(Notification.class).first();
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + SAVED, submission.getExperiment()),
        test(notification).getText());
    assertFalse(dialog.isOpened());
    verify(savedListener).onComponentEvent(any());
  }

  @Test
  public void cancel() {
    dialog.cancel.click();
    assertFalse(dialog.isOpened());
  }

  @Test
  public void getSubmissionId() {
    assertEquals(163L, dialog.getSubmissionId());
  }

  @Test
  public void setSubmissionId() {
    Submission submission = repository.findById(163L).orElseThrow();
    String experiment = "test submission";
    submission.setExperiment(experiment);
    submission.setSamples(new ArrayList<>());
    dialog.setSubmissionId(163L);

    locale = Locale.FRENCH;
    UI.getCurrent().setLocale(locale);

    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + HEADER, experiment),
        dialog.getHeaderTitle());
  }

  @Test
  public void setSubmissionId_0() {
    assertThrows(NoSuchElementException.class, () -> dialog.setSubmissionId(0));
  }
}
