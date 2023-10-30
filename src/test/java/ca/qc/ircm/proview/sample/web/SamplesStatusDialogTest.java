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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.Constants.ALL;
import static ca.qc.ircm.proview.Constants.CANCEL;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.SAVE;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.submission.Submission;
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
import java.util.Optional;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link SamplesStatusDialog}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class SamplesStatusDialogTest extends SpringUIUnitTest {
  private SamplesStatusDialog dialog;
  @MockBean
  private SubmissionSampleService service;
  @Mock
  private ComponentEventListener<SavedEvent<SamplesStatusDialog>> savedListener;
  @Autowired
  private SubmissionSampleRepository repository;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(SamplesStatusDialog.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);
  private AppResources sampleResources = new AppResources(Sample.class, locale);
  private AppResources submissionSampleResources = new AppResources(SubmissionSample.class, locale);
  private List<SubmissionSample> samples;
  private SampleStatus status1 = SampleStatus.ANALYSED;
  private SampleStatus status2 = SampleStatus.DIGESTED;

  /**
   * Before tests.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    SubmissionsView view = navigate(SubmissionsView.class);
    Grid<Submission> submissions = test(view).find(Grid.class).id(SubmissionsView.SUBMISSIONS);
    test(submissions).clickRow(1, new MetaKeys().shift());
    dialog = $(SamplesStatusDialog.class).id(ID);
    samples = repository.findAll();
  }

  private int indexOfColumn(String property) {
    return test(dialog.samples).getColumnPosition(property);
  }

  private void setFields() {
    dialog.status(dialog.getSubmission().getSamples().get(0)).setValue(status1);
    dialog.getSubmission().getSamples().stream().skip(1)
        .forEach(sample -> dialog.status(sample).setValue(status2));
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
    assertEquals(resources.message(HEADER, dialog.getSubmission().getExperiment()),
        dialog.getHeaderTitle());
    assertEquals(resources.message(property(STATUS, ALL)), dialog.allStatus.getLabel());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    assertEquals(webResources.message(CANCEL), dialog.cancel.getText());
    HeaderRow header = dialog.samples.getHeaderRows().get(0);
    FooterRow footer = dialog.samples.getFooterRows().get(0);
    assertEquals(sampleResources.message(NAME), header.getCell(dialog.name).getText());
    assertEquals(sampleResources.message(NAME), footer.getCell(dialog.name).getText());
    assertEquals(submissionSampleResources.message(STATUS),
        header.getCell(dialog.status).getText());
    assertEquals(submissionSampleResources.message(STATUS),
        footer.getCell(dialog.status).getText());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(SamplesStatusDialog.class, locale);
    final AppResources webResources = new AppResources(Constants.class, locale);
    final AppResources sampleResources = new AppResources(Sample.class, locale);
    final AppResources submissionSampleResources = new AppResources(SubmissionSample.class, locale);
    UI.getCurrent().setLocale(locale);
    assertEquals(resources.message(HEADER, dialog.getSubmission().getExperiment()),
        dialog.getHeaderTitle());
    assertEquals(resources.message(property(STATUS, ALL)), dialog.allStatus.getLabel());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    assertEquals(webResources.message(CANCEL), dialog.cancel.getText());
    HeaderRow header = dialog.samples.getHeaderRows().get(0);
    FooterRow footer = dialog.samples.getFooterRows().get(0);
    assertEquals(sampleResources.message(NAME), header.getCell(dialog.name).getText());
    assertEquals(sampleResources.message(NAME), footer.getCell(dialog.name).getText());
    assertEquals(submissionSampleResources.message(STATUS),
        header.getCell(dialog.status).getText());
    assertEquals(submissionSampleResources.message(STATUS),
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
    Submission submission = new Submission();
    submission.setSamples(samples);
    dialog.setSubmission(submission);
    for (int i = 0; i < samples.size(); i++) {
      SubmissionSample sample = samples.get(i);
      assertEquals(sample.getName(), test(dialog.samples).getCellText(i, indexOfColumn(NAME)));
      ComboBox<SampleStatus> statusBox =
          test(test(dialog.samples).getCellComponent(i, STATUS)).find(ComboBox.class).first();
      assertEquals(sample.getStatus(), statusBox.getValue(), i + ", " + sample);
      assertTrue(statusBox.hasClassName(STATUS));
      assertTrue(statusBox.isRequiredIndicatorVisible());
      List<SampleStatus> statuses = items(statusBox);
      assertEquals(Arrays.asList(SampleStatus.values()), statuses);
      for (SampleStatus status : statuses) {
        assertEquals(status.getLabel(locale), statusBox.getItemLabelGenerator().apply(status));
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
    assertTrue(dialog.getSubmission().getSamples().stream()
        .filter(sa -> sa.getStatus() != SampleStatus.ANALYSED).findFirst().isPresent());
    dialog.allStatus.setValue(SampleStatus.ANALYSED);
    for (SubmissionSample sample : dialog.getSubmission().getSamples()) {
      assertEquals(SampleStatus.ANALYSED, dialog.status(sample).getValue());
    }
  }

  @Test
  public void allStatus_Clear() {
    dialog.allStatus.setValue(SampleStatus.ANALYSED);
    dialog.allStatus.clear();
    for (SubmissionSample sample : dialog.getSubmission().getSamples()) {
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
    Submission submission = new Submission();
    submission.setSamples(new ArrayList<>());
    dialog.setSubmission(submission);
    dialog.save.click();
    verify(service, never()).updateStatus(any());
  }

  @Test
  public void save_EmptyStatus_First() {
    setFields();
    SubmissionSample sample = dialog.getSubmission().getSamples().get(0);
    dialog.status(sample).setValue(null);
    dialog.save.click();
    verify(service, never()).updateStatus(any());
    List<BinderValidationStatus<SubmissionSample>> statuses = dialog.validateSamples();
    BinderValidationStatus<SubmissionSample> status = statuses.get(0);
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, dialog.status(sample));
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void save_EmptyStatus_Second() {
    setFields();
    SubmissionSample sample = dialog.getSubmission().getSamples().get(1);
    dialog.status(sample).setValue(null);
    dialog.save.click();
    verify(service, never()).updateStatus(any());
    List<BinderValidationStatus<SubmissionSample>> statuses = dialog.validateSamples();
    BinderValidationStatus<SubmissionSample> status = statuses.get(1);
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, dialog.status(sample));
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void save() {
    dialog.addSavedListener(savedListener);
    setFields();
    dialog.save.click();
    verify(service).updateStatus(dialog.getSubmission().getSamples());
    Notification notification = $(Notification.class).first();
    assertEquals(resources.message(SAVED, dialog.getSubmission().getExperiment()),
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
  public void getSubmission() {
    assertEquals(163L, dialog.getSubmission().getId());
  }

  @Test
  public void setSubmission_New() {
    Submission submission = new Submission(1L);
    String experiment = "test submission";
    submission.setExperiment(experiment);
    submission.setSamples(new ArrayList<>());

    dialog.setSubmission(submission);

    assertEquals(submission, dialog.getSubmission());
    assertEquals(resources.message(HEADER, experiment), dialog.getHeaderTitle());
  }

  @Test
  public void setSubmission_NewLocalChange() {
    Submission submission = new Submission(1L);
    String experiment = "test submission";
    submission.setExperiment(experiment);
    submission.setSamples(new ArrayList<>());
    dialog.setSubmission(submission);

    locale = Locale.FRENCH;
    UI.getCurrent().setLocale(locale);

    final AppResources resources = new AppResources(SamplesStatusDialog.class, locale);
    assertEquals(resources.message(HEADER, experiment), dialog.getHeaderTitle());
  }

  @Test
  public void setSubmission_NoId() {
    Submission submission = new Submission();
    submission.setSamples(new ArrayList<>());

    dialog.setSubmission(submission);

    assertEquals(resources.message(HEADER), dialog.getHeaderTitle());
  }

  @Test
  public void setSubmission_IdThenNoId() {
    Submission submission = new Submission(1L);
    String experiment = "test submission";
    submission.setExperiment(experiment);
    submission.setSamples(new ArrayList<>());
    dialog.setSubmission(submission);
    submission = new Submission();
    submission.setSamples(new ArrayList<>());

    dialog.setSubmission(submission);

    assertEquals(resources.message(HEADER), dialog.getHeaderTitle());
  }
}
