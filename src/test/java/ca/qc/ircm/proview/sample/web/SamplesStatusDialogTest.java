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
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.sample.SampleProperties.NAME;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.sample.web.SamplesStatusDialog.HEADER;
import static ca.qc.ircm.proview.sample.web.SamplesStatusDialog.ID;
import static ca.qc.ircm.proview.sample.web.SamplesStatusDialog.id;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
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
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.testbench.unit.MetaKeys;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
  private SamplesStatusDialogPresenter presenter;
  @Mock
  private Submission submission;
  @Captor
  private ArgumentCaptor<ValueProvider<SubmissionSample, String>> valueProviderCaptor;
  @Captor
  private ArgumentCaptor<
      ComponentRenderer<ComboBox<SampleStatus>, SubmissionSample>> statusRendererCaptor;
  @Captor
  private ArgumentCaptor<Comparator<SubmissionSample>> comparatorCaptor;
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

  @SuppressWarnings("unchecked")
  private void mockColumns() {
    Element gridElement = dialog.samples.getElement();
    dialog.samples = mock(Grid.class);
    when(dialog.samples.getElement()).thenReturn(gridElement);
    dialog.name = mock(Column.class);
    when(dialog.samples.addColumn(any(ValueProvider.class), eq(NAME))).thenReturn(dialog.name);
    when(dialog.name.setKey(any())).thenReturn(dialog.name);
    when(dialog.name.setComparator(any(Comparator.class))).thenReturn(dialog.name);
    when(dialog.name.setHeader(any(String.class))).thenReturn(dialog.name);
    when(dialog.name.setFlexGrow(anyInt())).thenReturn(dialog.name);
    dialog.status = mock(Column.class);
    when(dialog.samples.addColumn(any(ComponentRenderer.class))).thenReturn(dialog.status);
    when(dialog.status.setKey(any())).thenReturn(dialog.status);
    when(dialog.status.setHeader(any(String.class))).thenReturn(dialog.status);
    when(dialog.status.setSortable(anyBoolean())).thenReturn(dialog.status);
    when(dialog.status.setFlexGrow(anyInt())).thenReturn(dialog.status);
    HeaderRow allRow = mock(HeaderRow.class);
    when(dialog.samples.appendHeaderRow()).thenReturn(allRow);
    HeaderCell allStatusCell = mock(HeaderCell.class);
    when(allRow.getCell(dialog.status)).thenReturn(allStatusCell);
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
    assertEquals(resources.message(HEADER), dialog.getHeaderTitle());
    assertEquals(resources.message(property(STATUS, ALL)), dialog.allStatus.getLabel());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    assertEquals(webResources.message(CANCEL), dialog.cancel.getText());
    FooterRow footer = dialog.samples.getFooterRows().get(0);
    assertEquals(sampleResources.message(NAME),
        test(dialog.samples).getHeaderCell(indexOfColumn(NAME)));
    assertEquals(sampleResources.message(NAME), footer.getCell(dialog.name).getText());
    assertEquals(submissionSampleResources.message(STATUS),
        test(dialog.samples).getHeaderCell(indexOfColumn(STATUS)));
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
    assertEquals(resources.message(HEADER), dialog.getHeaderTitle());
    assertEquals(resources.message(property(STATUS, ALL)), dialog.allStatus.getLabel());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    assertEquals(webResources.message(CANCEL), dialog.cancel.getText());
    FooterRow footer = dialog.samples.getFooterRows().get(0);
    assertEquals(sampleResources.message(NAME),
        test(dialog.samples).getHeaderCell(indexOfColumn(NAME)));
    assertEquals(sampleResources.message(NAME), footer.getCell(dialog.name).getText());
    assertEquals(submissionSampleResources.message(STATUS),
        test(dialog.samples).getHeaderCell(indexOfColumn(STATUS)));
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
    dialog.samples.setItems(samples);
    for (int i = 0; i < samples.size(); i++) {
      SubmissionSample sample = samples.get(i);
      assertEquals(sample.getName(), test(dialog.samples).getCellText(i, indexOfColumn(NAME)));
      ComboBox<SampleStatus> statusBox =
          test(test(dialog.samples).getCellComponent(i, STATUS)).find(ComboBox.class).first();
      assertTrue(statusBox.hasClassName(STATUS));
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
    dialog.allStatus.setValue(SampleStatus.ANALYSED);
    verify(presenter).setAllStatus(SampleStatus.ANALYSED);
  }

  @Test
  public void allStatus_Clear() {
    dialog.allStatus.setValue(SampleStatus.ANALYSED);
    dialog.allStatus.clear();
    verify(presenter).setAllStatus(SampleStatus.ANALYSED);
    verify(presenter).setAllStatus(null);
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
  public void cancel() {
    dialog.cancel.click();
    verify(presenter).cancel();
  }

  @Test
  public void getSubmission() {
    when(presenter.getSubmission()).thenReturn(submission);
    Submission submission = dialog.getSubmission();
    verify(presenter, atLeastOnce()).getSubmission();
    assertEquals(this.submission, submission);
  }

  @Test
  public void setSubmission() {
    Submission submission = new Submission(1L);
    String experiment = "test submission";
    submission.setExperiment(experiment);
    when(presenter.getSubmission()).thenReturn(submission);

    dialog.setSubmission(submission);

    verify(presenter).setSubmission(submission);
    assertEquals(resources.message(HEADER, experiment), dialog.getHeaderTitle());
  }

  @Test
  public void setSubmission_LocalChange() {
    Submission submission = new Submission(1L);
    String experiment = "test submission";
    submission.setExperiment(experiment);
    when(presenter.getSubmission()).thenReturn(submission);
    dialog.setSubmission(submission);

    locale = Locale.FRENCH;
    UI.getCurrent().setLocale(locale);

    verify(presenter).setSubmission(submission);
    final AppResources resources = new AppResources(SamplesStatusDialog.class, locale);
    assertEquals(resources.message(HEADER, experiment), dialog.getHeaderTitle());
  }

  @Test
  public void setSubmission_NoId() {
    Submission submission = new Submission();
    when(presenter.getSubmission()).thenReturn(submission);
    dialog.setSubmission(submission);

    verify(presenter).setSubmission(submission);
    assertEquals(resources.message(HEADER), dialog.getHeaderTitle());
  }

  @Test
  public void setSubmission_IdThenNoId() {
    Submission submission = new Submission(1L);
    String experiment = "test submission";
    submission.setExperiment(experiment);
    when(presenter.getSubmission()).thenReturn(submission);
    dialog.setSubmission(submission);
    submission = new Submission();
    when(presenter.getSubmission()).thenReturn(submission);

    dialog.setSubmission(submission);

    verify(presenter).setSubmission(submission);
    assertEquals(resources.message(HEADER), dialog.getHeaderTitle());
  }
}
