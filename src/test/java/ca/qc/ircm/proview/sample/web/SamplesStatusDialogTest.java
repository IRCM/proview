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
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
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
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.text.NormalizedComparator;
import ca.qc.ircm.proview.web.SavedEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SamplesStatusDialogTest extends AbstractViewTestCase {
  private SamplesStatusDialog dialog;
  @Mock
  private SamplesStatusDialogPresenter presenter;
  @Mock
  private Submission submission;
  @Captor
  private ArgumentCaptor<ValueProvider<SubmissionSample, String>> valueProviderCaptor;
  @Captor
  private ArgumentCaptor<ComponentRenderer<ComboBox<SampleStatus>, SubmissionSample>> statusRendererCaptor;
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
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    dialog = new SamplesStatusDialog(presenter);
    dialog.init();
    samples = repository.findAll();
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
    dialog.status = mock(Column.class);
    when(dialog.samples.addColumn(any(ComponentRenderer.class), eq(STATUS)))
        .thenReturn(dialog.status);
    when(dialog.status.setKey(any())).thenReturn(dialog.status);
    when(dialog.status.setHeader(any(String.class))).thenReturn(dialog.status);
    when(dialog.status.setSortable(anyBoolean())).thenReturn(dialog.status);
    HeaderRow allRow = mock(HeaderRow.class);
    when(dialog.samples.appendHeaderRow()).thenReturn(allRow);
    HeaderCell allStatusCell = mock(HeaderCell.class);
    when(allRow.getCell(dialog.status)).thenReturn(allStatusCell);
  }

  @Test
  public void styles() {
    assertEquals(ID, dialog.getId().orElse(""));
    assertTrue(dialog.header.getClassName().contains(HEADER));
    assertTrue(dialog.samples.getClassName().contains(SAMPLES));
    assertTrue(dialog.allStatus.getClassName().contains(styleName(STATUS, ALL)));
    assertTrue(dialog.save.getClassName().contains(SAVE));
    assertTrue(dialog.cancel.getClassName().contains(CANCEL));
  }

  @Test
  public void labels() {
    mockColumns();
    dialog.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), dialog.header.getText());
    assertEquals(resources.message(property(STATUS, ALL)), dialog.allStatus.getLabel());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    assertEquals(webResources.message(CANCEL), dialog.cancel.getText());
    verify(dialog.name).setHeader(sampleResources.message(NAME));
    verify(dialog.name).setFooter(sampleResources.message(NAME));
    verify(dialog.status).setHeader(submissionSampleResources.message(STATUS));
    verify(dialog.status).setFooter(submissionSampleResources.message(STATUS));
  }

  @Test
  public void localeChange() {
    mockColumns();
    dialog.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(SamplesStatusDialog.class, locale);
    final AppResources webResources = new AppResources(Constants.class, locale);
    final AppResources sampleResources = new AppResources(Sample.class, locale);
    final AppResources submissionSampleResources = new AppResources(SubmissionSample.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    dialog.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), dialog.header.getText());
    assertEquals(resources.message(property(STATUS, ALL)), dialog.allStatus.getLabel());
    assertEquals(webResources.message(SAVE), dialog.save.getText());
    assertEquals(webResources.message(CANCEL), dialog.cancel.getText());
    verify(dialog.name).setHeader(sampleResources.message(NAME));
    verify(dialog.name).setFooter(sampleResources.message(NAME));
    verify(dialog.status).setHeader(submissionSampleResources.message(STATUS));
    verify(dialog.status).setFooter(submissionSampleResources.message(STATUS));
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
    dialog = new SamplesStatusDialog(presenter);
    mockColumns();
    dialog.init();
    verify(dialog.samples).addColumn(valueProviderCaptor.capture(), eq(NAME));
    ValueProvider<SubmissionSample, String> valueProvider = valueProviderCaptor.getValue();
    for (SubmissionSample sample : samples) {
      assertEquals(sample.getName() != null ? sample.getName() : "", valueProvider.apply(sample));
    }
    verify(dialog.name).setComparator(comparatorCaptor.capture());
    Comparator<SubmissionSample> comparator = comparatorCaptor.getValue();
    assertTrue(comparator instanceof NormalizedComparator);
    for (SubmissionSample sample : samples) {
      assertEquals(sample.getName(),
          ((NormalizedComparator<SubmissionSample>) comparator).getConverter().apply(sample));
    }
    verify(dialog.samples).addColumn(statusRendererCaptor.capture(), eq(STATUS));
    ComponentRenderer<ComboBox<SampleStatus>, SubmissionSample> statusRenderer =
        statusRendererCaptor.getValue();
    for (SubmissionSample sample : samples) {
      ComboBox<SampleStatus> comboBox = statusRenderer.createComponent(sample);
      assertTrue(comboBox.getClassName().contains(STATUS));
      List<SampleStatus> statuses = items(comboBox);
      assertEquals(Arrays.asList(SampleStatus.values()), statuses);
      for (SampleStatus status : statuses) {
        assertEquals(status.getLabel(locale), comboBox.getItemLabelGenerator().apply(status));
      }
      assertSame(comboBox, dialog.status(sample));
    }
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
    assertEquals(resources.message(HEADER, experiment), dialog.header.getText());
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
    assertEquals(resources.message(HEADER, experiment), dialog.header.getText());
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
