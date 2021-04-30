/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.EDIT;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.PRINT;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionDialog.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionDialog.ID;
import static ca.qc.ircm.proview.submission.web.SubmissionDialog.id;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findChild;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateEquals;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.web.DatePickerInternationalization.englishDatePickerI18n;
import static ca.qc.ircm.proview.web.DatePickerInternationalization.frenchDatePickerI18n;
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

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SavedEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.List;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for {@link SubmissionDialog}.
 */
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
  private AppResources resources = new AppResources(SubmissionDialog.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);
  private AppResources submissionResources = new AppResources(Submission.class, locale);

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
    assertFalse(dialog.submissionForm.isVisible());
  }

  @Test
  public void init_Admin() {
    when(authorizationService.hasRole(ADMIN)).thenReturn(true);
    dialog.init();

    verify(presenter, times(2)).init(dialog);
    verify(authorizationService, atLeastOnce()).hasRole(ADMIN);
    assertTrue(dialog.submissionForm.isVisible());
  }

  @Test
  public void printContent() {
    assertSame(printContent, findChild(dialog, PrintSubmission.class).orElse(null));
  }

  @Test
  public void styles() {
    assertEquals(ID, dialog.getId().orElse(""));
    assertEquals(id(HEADER), dialog.header.getId().orElse(""));
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
    dialog.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), dialog.header.getText());
    assertEquals(submissionResources.message(INSTRUMENT), dialog.instrument.getLabel());
    for (MassDetectionInstrument instrument : MassDetectionInstrument.userChoices()) {
      assertEquals(instrument.getLabel(locale),
          dialog.instrument.getItemLabelGenerator().apply(instrument));
    }
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
    final AppResources resources = new AppResources(SubmissionDialog.class, locale);
    final AppResources webResources = new AppResources(Constants.class, locale);
    final AppResources submissionResources = new AppResources(Submission.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    dialog.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), dialog.header.getText());
    assertEquals(submissionResources.message(INSTRUMENT), dialog.instrument.getLabel());
    for (MassDetectionInstrument instrument : MassDetectionInstrument.userChoices()) {
      assertEquals(instrument.getLabel(locale),
          dialog.instrument.getItemLabelGenerator().apply(instrument));
    }
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
