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

package ca.qc.ircm.proview.msanalysis.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.msanalysis.AcquisitionProperties.ACQUISITION_FILE;
import static ca.qc.ircm.proview.msanalysis.AcquisitionProperties.COMMENT;
import static ca.qc.ircm.proview.msanalysis.AcquisitionProperties.CONTAINER;
import static ca.qc.ircm.proview.msanalysis.AcquisitionProperties.NUMBER_OF_ACQUISITION;
import static ca.qc.ircm.proview.msanalysis.AcquisitionProperties.POSITION;
import static ca.qc.ircm.proview.msanalysis.AcquisitionProperties.SAMPLE;
import static ca.qc.ircm.proview.msanalysis.AcquisitionProperties.SAMPLE_LIST_NAME;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.ACQUISITIONS;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.DELETED;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.INSERT_TIME;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.MASS_DETECTION_INSTRUMENT;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.SOURCE;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog.HEADER;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog.ID;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.AcquisitionRepository;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisRepository;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
public class MsAnalysisDialogTest extends AbstractViewTestCase {
  private MsAnalysisDialog dialog;
  @Mock
  private MsAnalysis msAnalysis;
  @Captor
  private ArgumentCaptor<ValueProvider<Acquisition, String>> valueProviderCaptor;
  @Autowired
  private MsAnalysisRepository repository;
  @Autowired
  private AcquisitionRepository acquisitionRepository;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(MsAnalysisDialog.class, locale);
  private AppResources msAnalysisResources = new AppResources(MsAnalysis.class, locale);
  private AppResources acquisitionResources = new AppResources(Acquisition.class, locale);
  private List<Acquisition> acquisitions;

  /**
   * Before tests.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    dialog = new MsAnalysisDialog();
    dialog.init();
    acquisitions = acquisitionRepository.findAll();
  }

  @SuppressWarnings("unchecked")
  private void mockColumns() {
    Element gridElement = dialog.acquisitions.getElement();
    dialog.acquisitions = mock(Grid.class);
    when(dialog.acquisitions.getElement()).thenReturn(gridElement);
    dialog.sample = mock(Column.class);
    when(dialog.acquisitions.addColumn(any(ValueProvider.class), eq(SAMPLE)))
        .thenReturn(dialog.sample);
    when(dialog.sample.setKey(any())).thenReturn(dialog.sample);
    when(dialog.sample.setComparator(any(Comparator.class))).thenReturn(dialog.sample);
    when(dialog.sample.setHeader(any(String.class))).thenReturn(dialog.sample);
    dialog.container = mock(Column.class);
    when(dialog.acquisitions.addColumn(any(ValueProvider.class), eq(CONTAINER)))
        .thenReturn(dialog.container);
    when(dialog.container.setKey(any())).thenReturn(dialog.container);
    when(dialog.container.setComparator(any(Comparator.class))).thenReturn(dialog.container);
    when(dialog.container.setHeader(any(String.class))).thenReturn(dialog.container);
    dialog.numberOfAcquisition = mock(Column.class);
    when(dialog.acquisitions.addColumn(any(ValueProvider.class), eq(NUMBER_OF_ACQUISITION)))
        .thenReturn(dialog.numberOfAcquisition);
    when(dialog.numberOfAcquisition.setKey(any())).thenReturn(dialog.numberOfAcquisition);
    when(dialog.numberOfAcquisition.setComparator(any(Comparator.class)))
        .thenReturn(dialog.numberOfAcquisition);
    when(dialog.numberOfAcquisition.setHeader(any(String.class)))
        .thenReturn(dialog.numberOfAcquisition);
    dialog.sampleListName = mock(Column.class);
    when(dialog.acquisitions.addColumn(any(ValueProvider.class), eq(SAMPLE_LIST_NAME)))
        .thenReturn(dialog.sampleListName);
    when(dialog.sampleListName.setKey(any())).thenReturn(dialog.sampleListName);
    when(dialog.sampleListName.setComparator(any(Comparator.class)))
        .thenReturn(dialog.sampleListName);
    when(dialog.sampleListName.setHeader(any(String.class))).thenReturn(dialog.sampleListName);
    dialog.acquisitionFile = mock(Column.class);
    when(dialog.acquisitions.addColumn(any(ValueProvider.class), eq(ACQUISITION_FILE)))
        .thenReturn(dialog.acquisitionFile);
    when(dialog.acquisitionFile.setKey(any())).thenReturn(dialog.acquisitionFile);
    when(dialog.acquisitionFile.setComparator(any(Comparator.class)))
        .thenReturn(dialog.acquisitionFile);
    when(dialog.acquisitionFile.setHeader(any(String.class))).thenReturn(dialog.acquisitionFile);
    dialog.position = mock(Column.class);
    when(dialog.acquisitions.addColumn(any(ValueProvider.class), eq(POSITION)))
        .thenReturn(dialog.position);
    when(dialog.position.setKey(any())).thenReturn(dialog.position);
    when(dialog.position.setComparator(any(Comparator.class))).thenReturn(dialog.position);
    when(dialog.position.setHeader(any(String.class))).thenReturn(dialog.position);
    dialog.comment = mock(Column.class);
    when(dialog.acquisitions.addColumn(any(ValueProvider.class), eq(COMMENT)))
        .thenReturn(dialog.comment);
    when(dialog.comment.setKey(any())).thenReturn(dialog.comment);
    when(dialog.comment.setComparator(any(Comparator.class))).thenReturn(dialog.comment);
    when(dialog.comment.setHeader(any(String.class))).thenReturn(dialog.comment);
  }

  @Test
  public void styles() {
    assertEquals(ID, dialog.getId().orElse(""));
    assertTrue(dialog.header.hasClassName(HEADER));
    assertTrue(dialog.deleted.hasClassName(DELETED));
    assertTrue(dialog.instrument.hasClassName(MASS_DETECTION_INSTRUMENT));
    assertTrue(dialog.source.hasClassName(SOURCE));
    assertTrue(dialog.date.hasClassName(INSERT_TIME));
    assertTrue(dialog.acquisitionsHeader.hasClassName(styleName(ACQUISITIONS, HEADER)));
    assertTrue(dialog.acquisitions.hasClassName(ACQUISITIONS));
  }

  @Test
  public void labels() {
    mockColumns();
    dialog.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), dialog.header.getText());
    assertEquals(msAnalysisResources.message(DELETED), dialog.deleted.getText());
    assertEquals(msAnalysisResources.message(ACQUISITIONS), dialog.acquisitionsHeader.getText());
    verify(dialog.sample).setHeader(acquisitionResources.message(SAMPLE));
    verify(dialog.container).setHeader(acquisitionResources.message(CONTAINER));
    verify(dialog.numberOfAcquisition)
        .setHeader(acquisitionResources.message(NUMBER_OF_ACQUISITION));
    verify(dialog.sampleListName).setHeader(acquisitionResources.message(SAMPLE_LIST_NAME));
    verify(dialog.acquisitionFile).setHeader(acquisitionResources.message(ACQUISITION_FILE));
    verify(dialog.position).setHeader(acquisitionResources.message(POSITION));
    verify(dialog.comment).setHeader(acquisitionResources.message(COMMENT));
  }

  @Test
  public void localeChange() {
    mockColumns();
    dialog.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(MsAnalysisDialog.class, locale);
    final AppResources msAnalysisResources = new AppResources(MsAnalysis.class, locale);
    final AppResources acquisitionResources = new AppResources(Acquisition.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    dialog.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), dialog.header.getText());
    assertEquals(msAnalysisResources.message(DELETED), dialog.deleted.getText());
    assertEquals(msAnalysisResources.message(ACQUISITIONS), dialog.acquisitionsHeader.getText());
    verify(dialog.sample).setHeader(acquisitionResources.message(SAMPLE));
    verify(dialog.container).setHeader(acquisitionResources.message(CONTAINER));
    verify(dialog.numberOfAcquisition)
        .setHeader(acquisitionResources.message(NUMBER_OF_ACQUISITION));
    verify(dialog.sampleListName, atLeastOnce())
        .setHeader(acquisitionResources.message(SAMPLE_LIST_NAME));
    verify(dialog.acquisitionFile).setHeader(acquisitionResources.message(ACQUISITION_FILE));
    verify(dialog.position, atLeastOnce()).setHeader(acquisitionResources.message(POSITION));
    verify(dialog.comment).setHeader(acquisitionResources.message(COMMENT));
  }

  @Test
  public void samples_Columns() {
    assertEquals(7, dialog.acquisitions.getColumns().size());
    assertNotNull(dialog.acquisitions.getColumnByKey(SAMPLE));
    assertEquals(dialog.sample, dialog.acquisitions.getColumnByKey(SAMPLE));
    assertTrue(dialog.sample.isSortable());
    assertNotNull(dialog.acquisitions.getColumnByKey(CONTAINER));
    assertEquals(dialog.container, dialog.acquisitions.getColumnByKey(CONTAINER));
    assertTrue(dialog.container.isSortable());
    assertNotNull(dialog.acquisitions.getColumnByKey(NUMBER_OF_ACQUISITION));
    assertEquals(dialog.numberOfAcquisition,
        dialog.acquisitions.getColumnByKey(NUMBER_OF_ACQUISITION));
    assertTrue(dialog.numberOfAcquisition.isSortable());
    assertNotNull(dialog.acquisitions.getColumnByKey(SAMPLE_LIST_NAME));
    assertEquals(dialog.sampleListName, dialog.acquisitions.getColumnByKey(SAMPLE_LIST_NAME));
    assertTrue(dialog.sampleListName.isSortable());
    assertNotNull(dialog.acquisitions.getColumnByKey(ACQUISITION_FILE));
    assertEquals(dialog.acquisitionFile, dialog.acquisitions.getColumnByKey(ACQUISITION_FILE));
    assertTrue(dialog.acquisitionFile.isSortable());
    assertNotNull(dialog.acquisitions.getColumnByKey(POSITION));
    assertEquals(dialog.position, dialog.acquisitions.getColumnByKey(POSITION));
    assertTrue(dialog.position.isSortable());
    assertNotNull(dialog.acquisitions.getColumnByKey(COMMENT));
    assertEquals(dialog.comment, dialog.acquisitions.getColumnByKey(COMMENT));
    assertFalse(dialog.comment.isSortable());
  }

  @Test
  public void samples_ColumnsValueProvider() {
    dialog = new MsAnalysisDialog();
    mockColumns();
    dialog.init();
    verify(dialog.acquisitions).addColumn(valueProviderCaptor.capture(), eq(SAMPLE));
    ValueProvider<Acquisition, String> valueProvider = valueProviderCaptor.getValue();
    for (Acquisition ac : acquisitions) {
      assertEquals(ac.getSample().getName(), valueProvider.apply(ac));
    }
    verify(dialog.acquisitions).addColumn(valueProviderCaptor.capture(), eq(CONTAINER));
    valueProvider = valueProviderCaptor.getValue();
    for (Acquisition ac : acquisitions) {
      assertEquals(ac.getContainer().getFullName(), valueProvider.apply(ac));
    }
    verify(dialog.acquisitions).addColumn(valueProviderCaptor.capture(), eq(NUMBER_OF_ACQUISITION));
    valueProvider = valueProviderCaptor.getValue();
    for (Acquisition ac : acquisitions) {
      assertEquals(ac.getNumberOfAcquisition(), valueProvider.apply(ac));
    }
    verify(dialog.acquisitions).addColumn(valueProviderCaptor.capture(), eq(SAMPLE_LIST_NAME));
    valueProvider = valueProviderCaptor.getValue();
    for (Acquisition ac : acquisitions) {
      assertEquals(ac.getSampleListName(), valueProvider.apply(ac));
    }
    verify(dialog.acquisitions).addColumn(valueProviderCaptor.capture(), eq(ACQUISITION_FILE));
    valueProvider = valueProviderCaptor.getValue();
    for (Acquisition ac : acquisitions) {
      assertEquals(ac.getAcquisitionFile(), valueProvider.apply(ac));
    }
    verify(dialog.acquisitions).addColumn(valueProviderCaptor.capture(), eq(POSITION));
    valueProvider = valueProviderCaptor.getValue();
    for (Acquisition ac : acquisitions) {
      assertEquals(ac.getPosition(), valueProvider.apply(ac));
    }
    verify(dialog.acquisitions).addColumn(valueProviderCaptor.capture(), eq(COMMENT));
    valueProvider = valueProviderCaptor.getValue();
    for (Acquisition ac : acquisitions) {
      assertEquals(ac.getComment(), valueProvider.apply(ac));
    }
  }

  @Test
  public void getMsAnalysis() {
    MsAnalysis msAnalysis = repository.findById(1L).get();
    dialog.setMsAnalysis(msAnalysis);
    assertEquals(msAnalysis, dialog.getMsAnalysis());
  }

  @Test
  public void setMsAnalysis() {
    MsAnalysis msAnalysis = repository.findById(1L).get();
    dialog.localeChange(mock(LocaleChangeEvent.class));

    dialog.setMsAnalysis(msAnalysis);

    assertFalse(dialog.deleted.isVisible());
    assertEquals(resources.message(MASS_DETECTION_INSTRUMENT,
        msAnalysis.getMassDetectionInstrument().getLabel(locale)), dialog.instrument.getText());
    assertEquals(resources.message(SOURCE, msAnalysis.getSource().getLabel(locale)),
        dialog.source.getText());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(resources.message(INSERT_TIME, dateFormatter.format(msAnalysis.getInsertTime())),
        dialog.date.getText());
  }

  @Test
  public void setMsAnalysis_Deleted() {
    MsAnalysis msAnalysis = repository.findById(12L).get();
    msAnalysis.setDeleted(true);
    msAnalysis.setDeletionExplanation("Test deletion\nexplanation");
    dialog.localeChange(mock(LocaleChangeEvent.class));

    dialog.setMsAnalysis(msAnalysis);

    assertTrue(dialog.deleted.isVisible());
    assertEquals(resources.message(MASS_DETECTION_INSTRUMENT,
        msAnalysis.getMassDetectionInstrument().getLabel(locale)), dialog.instrument.getText());
    assertEquals(resources.message(SOURCE, msAnalysis.getSource().getLabel(locale)),
        dialog.source.getText());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(resources.message(INSERT_TIME, dateFormatter.format(msAnalysis.getInsertTime())),
        dialog.date.getText());
  }

  @Test
  public void setMsAnalysis_BeforeLocalChange() {
    MsAnalysis msAnalysis = repository.findById(1L).get();

    dialog.setMsAnalysis(msAnalysis);
    dialog.localeChange(mock(LocaleChangeEvent.class));

    assertFalse(dialog.deleted.isVisible());
    assertEquals(resources.message(MASS_DETECTION_INSTRUMENT,
        msAnalysis.getMassDetectionInstrument().getLabel(locale)), dialog.instrument.getText());
    assertEquals(resources.message(SOURCE, msAnalysis.getSource().getLabel(locale)),
        dialog.source.getText());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(resources.message(INSERT_TIME, dateFormatter.format(msAnalysis.getInsertTime())),
        dialog.date.getText());
  }

  @Test(expected = NullPointerException.class)
  public void setMsAnalysis_Null() {
    dialog.setMsAnalysis(null);
  }
}
