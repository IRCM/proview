package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.EDIT;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.PRINT;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionDialog.ID;
import static ca.qc.ircm.proview.submission.web.SubmissionDialog.id;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateEquals;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.web.DatePickerInternationalization.englishDatePickerI18n;
import static ca.qc.ircm.proview.web.DatePickerInternationalization.frenchDatePickerI18n;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SavedEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link SubmissionDialog}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionDialogTest extends SpringUIUnitTest {
  private static final String MASS_DETECTION_INSTRUMENT_PREFIX =
      messagePrefix(MassDetectionInstrument.class);
  private SubmissionDialog dialog;
  @MockBean
  private SubmissionService service;
  @Autowired
  private SubmissionRepository repository;
  @Mock
  private ComponentEventListener<SavedEvent<SubmissionDialog>> savedListener;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(SubmissionDialog.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);
  private AppResources submissionResources = new AppResources(Submission.class, locale);
  private MassDetectionInstrument instrument = MassDetectionInstrument.Q_EXACTIVE;
  private LocalDate dataAvailableDate = LocalDate.of(2019, 8, 2);

  /**
   * Before tests.
   */
  @BeforeEach
  public void beforeTest() {
    when(service.get(any())).thenAnswer(
        i -> i.getArgument(0) != null ? repository.findById(i.getArgument(0)) : Optional.empty());
    UI.getCurrent().setLocale(locale);
    SubmissionsView view = navigate(SubmissionsView.class);
    view.submissions.setItems(repository.findAll());
    test(view.submissions).doubleClickRow(1);
    dialog = $(SubmissionDialog.class).first();
  }

  private void setFields() {
    dialog.instrument.setValue(instrument);
    dialog.dataAvailableDate.setValue(dataAvailableDate);
  }

  @Test
  public void init_User() {
    assertFalse(dialog.submissionForm.isVisible());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void init_Admin() {
    assertTrue(dialog.submissionForm.isVisible());
  }

  @Test
  public void styles() {
    assertEquals(ID, dialog.getId().orElse(""));
    assertEquals(id(INSTRUMENT), dialog.instrument.getId().orElse(""));
    assertEquals(id(DATA_AVAILABLE_DATE), dialog.dataAvailableDate.getId().orElse(""));
    assertEquals(id(SAVE), dialog.save.getId().orElse(""));
    assertTrue(dialog.save.hasThemeName(ButtonVariant.LUMO_SUCCESS.getVariantName()));
    assertEquals(id(EDIT), dialog.edit.getId().orElse(""));
    assertTrue(dialog.edit.hasThemeName(ButtonVariant.LUMO_PRIMARY.getVariantName()));
    validateIcon(VaadinIcon.EDIT.create(), dialog.edit.getIcon());
    assertEquals(id(PRINT), dialog.print.getId().orElse(""));
    validateIcon(VaadinIcon.PRINT.create(), dialog.print.getIcon());
  }

  @Test
  public void labels() {
    Submission submission = repository.findById(32L).get();
    assertEquals(submission.getExperiment(), dialog.getHeaderTitle());
    assertEquals(submissionResources.message(INSTRUMENT), dialog.instrument.getLabel());
    for (MassDetectionInstrument instrument : MassDetectionInstrument.userChoices()) {
      assertEquals(dialog.getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + instrument.name()),
          dialog.instrument.getItemLabelGenerator().apply(instrument));
    }
    assertEquals(submissionResources.message(DATA_AVAILABLE_DATE),
        dialog.dataAvailableDate.getLabel());
    assertEquals(ENGLISH, dialog.dataAvailableDate.getLocale());
    validateEquals(englishDatePickerI18n(), dialog.dataAvailableDate.getI18n());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    assertEquals(webResources.message(EDIT), dialog.edit.getText());
    assertEquals(webResources.message(PRINT), dialog.print.getText());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    final AppResources webResources = new AppResources(Constants.class, locale);
    final AppResources submissionResources = new AppResources(Submission.class, locale);
    UI.getCurrent().setLocale(locale);
    Submission submission = repository.findById(32L).get();
    assertEquals(submission.getExperiment(), dialog.getHeaderTitle());
    assertEquals(submissionResources.message(INSTRUMENT), dialog.instrument.getLabel());
    for (MassDetectionInstrument instrument : MassDetectionInstrument.userChoices()) {
      assertEquals(dialog.getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + instrument.name()),
          dialog.instrument.getItemLabelGenerator().apply(instrument));
    }
    assertEquals(submissionResources.message(DATA_AVAILABLE_DATE),
        dialog.dataAvailableDate.getLabel());
    assertEquals(ENGLISH, dialog.dataAvailableDate.getLocale());
    validateEquals(frenchDatePickerI18n(), dialog.dataAvailableDate.getI18n());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    assertEquals(webResources.message(EDIT), dialog.edit.getText());
    assertEquals(webResources.message(PRINT), dialog.print.getText());
  }

  @Test
  public void instrument() {
    List<MassDetectionInstrument> instruments = items(dialog.instrument);
    assertEquals(MassDetectionInstrument.userChoices(), instruments);
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
  public void save() {
    Submission submission = repository.findById(32L).get();
    setFields();
    dialog.addSavedListener(savedListener);

    dialog.save();

    verify(service).update(submission, null);
    assertEquals(instrument, submission.getInstrument());
    assertEquals(dataAvailableDate, submission.getDataAvailableDate());
    assertFalse(dialog.isOpened());
    verify(savedListener).onComponentEvent(any());
  }

  @Test
  public void save_NullInstrument() {
    Submission submission = repository.findById(32L).get();
    setFields();
    dialog.instrument.setValue(MassDetectionInstrument.NULL);
    dialog.addSavedListener(savedListener);

    dialog.save();

    verify(service).update(submission, null);
    assertNull(submission.getInstrument());
    assertEquals(dataAvailableDate, submission.getDataAvailableDate());
    assertFalse(dialog.isOpened());
    verify(savedListener).onComponentEvent(any());
  }

  @Test
  public void edit() {
    Submission submission = repository.findById(164L).get();
    when(service.get(any())).thenReturn(Optional.of(submission));
    dialog.setSubmissionId(164L);
    dialog.edit();
    SubmissionView submissionView = $(SubmissionView.class).first();
    assertNotNull(submissionView.getSubmission());
    assertEquals(164L, submissionView.getSubmission().getId());
    assertFalse(dialog.isOpened());
  }

  @Test
  public void edit_New() {
    dialog.edit();
    $(SubmissionView.class).first();
    assertFalse(dialog.isOpened());
  }

  @Test
  public void print() {
    Submission submission = repository.findById(164L).get();
    when(service.get(any())).thenReturn(Optional.of(submission));
    dialog.setSubmissionId(164L);
    dialog.print();
    PrintSubmissionView printView = $(PrintSubmissionView.class).first();
    assertNotNull(printView.printContent.getSubmission());
    assertEquals(164L, printView.printContent.getSubmission().getId());
    assertFalse(dialog.isOpened());
  }

  @Test
  public void getSubmissionId() {
    assertEquals(32L, dialog.getSubmissionId());
  }

  @Test
  public void setSubmissionId() {
    dialog.setSubmissionId(164L);

    verify(service).get(164L);
    assertEquals("POLR3B-Flag", dialog.getHeaderTitle());
    assertEquals(MassDetectionInstrument.VELOS, dialog.instrument.getValue());
    assertNull(dialog.dataAvailableDate.getValue());
    assertTrue(dialog.edit.isEnabled());
  }

  @Test
  public void setSubmissionId_ReadOnly() {
    dialog.setSubmissionId(35L);

    verify(service).get(35L);
    assertEquals("cap_experiment", dialog.getHeaderTitle());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, dialog.instrument.getValue());
    assertEquals(LocalDate.of(2011, 11, 10), dialog.dataAvailableDate.getValue());
    assertFalse(dialog.edit.isEnabled());
  }

  @Test
  public void setSubmissionId_Null() {
    assertThrows(NoSuchElementException.class, () -> dialog.setSubmissionId(null));
  }
}
