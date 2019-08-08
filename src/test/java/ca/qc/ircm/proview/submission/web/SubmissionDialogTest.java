package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.SubmissionProperties.ANALYSIS_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLE_DELIVERY_DATE;
import static ca.qc.ircm.proview.submission.web.SubmissionDialog.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionDialog.ID;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findChild;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateEquals;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.web.WebConstants.EDIT;
import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.FRENCH;
import static ca.qc.ircm.proview.web.WebConstants.PRIMARY;
import static ca.qc.ircm.proview.web.WebConstants.PRINT;
import static ca.qc.ircm.proview.web.WebConstants.SAVE;
import static ca.qc.ircm.proview.web.WebConstants.SUCCESS;
import static ca.qc.ircm.proview.web.WebConstants.englishDatePickerI18n;
import static ca.qc.ircm.proview.web.WebConstants.frenchDatePickerI18n;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SavedEvent;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionDialogTest extends AbstractViewTestCase {
  private SubmissionDialog dialog;
  @Mock
  private SubmissionDialogPresenter presenter;
  @Autowired
  private PrintSubmission printContent;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Submission submission;
  @Mock
  private ComponentEventListener<SavedEvent<SubmissionDialog>> savedListener;
  private Locale locale = ENGLISH;
  private MessageResource resources = new MessageResource(SubmissionDialog.class, locale);
  private MessageResource webResources = new MessageResource(WebConstants.class, locale);
  private MessageResource submissionResources = new MessageResource(Submission.class, locale);

  /**
   * Before tests.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    dialog = new SubmissionDialog(presenter, printContent, authorizationService);
    dialog.init();
  }

  @Test
  public void init_User() {
    verify(presenter).init(dialog);
    verify(authorizationService, atLeastOnce()).hasRole(ADMIN);
    assertFalse(dialog.datesForm.isVisible());
  }

  @Test
  public void init_Admin() {
    when(authorizationService.hasRole(ADMIN)).thenReturn(true);
    dialog.init();

    verify(presenter, times(2)).init(dialog);
    verify(authorizationService, atLeastOnce()).hasRole(ADMIN);
    assertTrue(dialog.datesForm.isVisible());
  }

  @Test
  public void printContent() {
    assertSame(printContent, findChild(dialog, PrintSubmission.class).orElse(null));
  }

  @Test
  public void styles() {
    assertEquals(ID, dialog.getId().orElse(""));
    assertTrue(dialog.header.getClassName().contains(HEADER));
    assertTrue(dialog.sampleDeliveryDate.getClassName().contains(SAMPLE_DELIVERY_DATE));
    assertTrue(dialog.digestionDate.getClassName().contains(DIGESTION_DATE));
    assertTrue(dialog.analysisDate.getClassName().contains(ANALYSIS_DATE));
    assertTrue(dialog.dataAvailableDate.getClassName().contains(DATA_AVAILABLE_DATE));
    assertTrue(dialog.save.getClassName().contains(SAVE));
    assertTrue(dialog.save.getThemeName().contains(SUCCESS));
    assertTrue(dialog.edit.getClassName().contains(EDIT));
    assertTrue(dialog.edit.getThemeName().contains(PRIMARY));
    validateIcon(VaadinIcon.EDIT.create(), dialog.edit.getIcon());
    assertTrue(dialog.print.getClassName().contains(PRINT));
    validateIcon(VaadinIcon.PRINT.create(), dialog.print.getIcon());
  }

  @Test
  public void labels() {
    dialog.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), dialog.header.getText());
    assertEquals(submissionResources.message(SAMPLE_DELIVERY_DATE),
        dialog.sampleDeliveryDate.getLabel());
    assertEquals(ENGLISH, dialog.sampleDeliveryDate.getLocale());
    validateEquals(englishDatePickerI18n(), dialog.sampleDeliveryDate.getI18n());
    assertEquals(submissionResources.message(DIGESTION_DATE), dialog.digestionDate.getLabel());
    assertEquals(ENGLISH, dialog.digestionDate.getLocale());
    validateEquals(englishDatePickerI18n(), dialog.digestionDate.getI18n());
    assertEquals(submissionResources.message(ANALYSIS_DATE), dialog.analysisDate.getLabel());
    assertEquals(ENGLISH, dialog.analysisDate.getLocale());
    validateEquals(englishDatePickerI18n(), dialog.analysisDate.getI18n());
    assertEquals(submissionResources.message(DATA_AVAILABLE_DATE),
        dialog.dataAvailableDate.getLabel());
    assertEquals(ENGLISH, dialog.dataAvailableDate.getLocale());
    validateEquals(englishDatePickerI18n(), dialog.dataAvailableDate.getI18n());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    assertEquals(webResources.message(EDIT), dialog.edit.getText());
    assertEquals(webResources.message(PRINT), dialog.print.getText());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void localeChange() {
    dialog.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final MessageResource resources = new MessageResource(SubmissionDialog.class, locale);
    final MessageResource webResources = new MessageResource(WebConstants.class, locale);
    final MessageResource submissionResources = new MessageResource(Submission.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    dialog.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), dialog.header.getText());
    assertEquals(submissionResources.message(SAMPLE_DELIVERY_DATE),
        dialog.sampleDeliveryDate.getLabel());
    assertEquals(ENGLISH, dialog.sampleDeliveryDate.getLocale());
    validateEquals(frenchDatePickerI18n(), dialog.sampleDeliveryDate.getI18n());
    assertEquals(submissionResources.message(DIGESTION_DATE), dialog.digestionDate.getLabel());
    assertEquals(ENGLISH, dialog.digestionDate.getLocale());
    validateEquals(frenchDatePickerI18n(), dialog.digestionDate.getI18n());
    assertEquals(submissionResources.message(ANALYSIS_DATE), dialog.analysisDate.getLabel());
    assertEquals(ENGLISH, dialog.analysisDate.getLocale());
    validateEquals(frenchDatePickerI18n(), dialog.analysisDate.getI18n());
    assertEquals(submissionResources.message(DATA_AVAILABLE_DATE),
        dialog.dataAvailableDate.getLabel());
    assertEquals(ENGLISH, dialog.dataAvailableDate.getLocale());
    validateEquals(frenchDatePickerI18n(), dialog.dataAvailableDate.getI18n());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    assertEquals(webResources.message(EDIT), dialog.edit.getText());
    assertEquals(webResources.message(PRINT), dialog.print.getText());
    verify(presenter).localeChange(locale);
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
    dialog.save.click();
    verify(presenter).save();
  }

  @Test
  public void edit() {
    dialog.edit.click();
    verify(presenter).edit();
  }

  @Test
  public void print() {
    dialog.print.click();
    verify(presenter).print();
  }

  @Test
  public void getSubmission() {
    when(presenter.getSubmission()).thenReturn(submission);
    Submission submission = dialog.getSubmission();
    verify(presenter).getSubmission();
    assertEquals(this.submission, submission);
  }

  @Test
  public void setSubmission() {
    Submission submission = new Submission(1L);
    String experiment = "test submission";
    submission.setExperiment(experiment);
    dialog.localeChange(mock(LocaleChangeEvent.class));
    when(presenter.getSubmission()).thenReturn(submission);

    dialog.setSubmission(submission);

    verify(presenter).setSubmission(submission);
    assertEquals(experiment, dialog.header.getText());
  }

  @Test
  public void setSubmission_BeforeLocalChange() {
    Submission submission = new Submission(1L);
    String experiment = "test submission";
    submission.setExperiment(experiment);
    when(presenter.getSubmission()).thenReturn(submission);

    dialog.setSubmission(submission);
    dialog.localeChange(mock(LocaleChangeEvent.class));

    verify(presenter).setSubmission(submission);
    assertEquals(experiment, dialog.header.getText());
  }

  @Test
  public void setSubmission_NoId() {
    Submission submission = new Submission();
    dialog.localeChange(mock(LocaleChangeEvent.class));
    when(presenter.getSubmission()).thenReturn(submission);
    dialog.setSubmission(submission);

    verify(presenter).setSubmission(submission);
    assertEquals(resources.message(HEADER), dialog.header.getText());
  }

  @Test
  public void setSubmission_IdThenNoId() {
    dialog.localeChange(mock(LocaleChangeEvent.class));
    Submission submission = new Submission(1L);
    String experiment = "test submission";
    submission.setExperiment(experiment);
    when(presenter.getSubmission()).thenReturn(submission);
    dialog.setSubmission(submission);
    submission = new Submission();
    when(presenter.getSubmission()).thenReturn(submission);

    dialog.setSubmission(submission);

    verify(presenter).setSubmission(submission);
    assertEquals(resources.message(HEADER), dialog.header.getText());
  }
}
