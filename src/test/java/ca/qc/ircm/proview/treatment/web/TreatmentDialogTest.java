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

package ca.qc.ircm.proview.treatment.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.COMMENT;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.CONTAINER;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.DESTINATION_CONTAINER;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.NAME;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.NUMBER;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.PI_INTERVAL;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.QUANTITY;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.SAMPLE;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.SOLVENT;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.SOLVENT_VOLUME;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.SOURCE_VOLUME;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.DELETED;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.FRACTIONATION_TYPE;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.INSERT_TIME;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.PROTOCOL;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.TREATED_SAMPLES;
import static ca.qc.ircm.proview.treatment.web.TreatmentDialog.HEADER;
import static ca.qc.ircm.proview.treatment.web.TreatmentDialog.ID;
import static ca.qc.ircm.proview.treatment.web.TreatmentDialog.id;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.FractionationType;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.treatment.TreatedSampleRepository;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.TreatmentRepository;
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

/**
 * Tests for {@link TreatmentDialog}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class TreatmentDialogTest extends AbstractViewTestCase {
  private TreatmentDialog dialog;
  @Mock
  private Treatment treatment;
  @Captor
  private ArgumentCaptor<ValueProvider<TreatedSample, String>> valueProviderCaptor;
  @Autowired
  private TreatmentRepository repository;
  @Autowired
  private TreatedSampleRepository treatedSampleRepository;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(TreatmentDialog.class, locale);
  private AppResources treatmentResources = new AppResources(Treatment.class, locale);
  private AppResources treatedSampleResources = new AppResources(TreatedSample.class, locale);
  private List<TreatedSample> treatedSamples;

  /**
   * Before tests.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    dialog = new TreatmentDialog();
    dialog.init();
    treatedSamples = treatedSampleRepository.findAll();
  }

  @SuppressWarnings("unchecked")
  private void mockColumns() {
    Element gridElement = dialog.samples.getElement();
    dialog.samples = mock(Grid.class);
    when(dialog.samples.getElement()).thenReturn(gridElement);
    dialog.sample = mock(Column.class);
    when(dialog.samples.addColumn(any(ValueProvider.class), eq(SAMPLE))).thenReturn(dialog.sample);
    when(dialog.sample.setKey(any())).thenReturn(dialog.sample);
    when(dialog.sample.setComparator(any(Comparator.class))).thenReturn(dialog.sample);
    when(dialog.sample.setHeader(any(String.class))).thenReturn(dialog.sample);
    dialog.container = mock(Column.class);
    when(dialog.samples.addColumn(any(ValueProvider.class), eq(CONTAINER)))
        .thenReturn(dialog.container);
    when(dialog.container.setKey(any())).thenReturn(dialog.container);
    when(dialog.container.setComparator(any(Comparator.class))).thenReturn(dialog.container);
    when(dialog.container.setHeader(any(String.class))).thenReturn(dialog.container);
    dialog.sourceVolume = mock(Column.class);
    when(dialog.samples.addColumn(any(ValueProvider.class), eq(SOURCE_VOLUME)))
        .thenReturn(dialog.sourceVolume);
    when(dialog.sourceVolume.setKey(any())).thenReturn(dialog.sourceVolume);
    when(dialog.sourceVolume.setComparator(any(Comparator.class))).thenReturn(dialog.sourceVolume);
    when(dialog.sourceVolume.setHeader(any(String.class))).thenReturn(dialog.sourceVolume);
    dialog.solvent = mock(Column.class);
    when(dialog.samples.addColumn(any(ValueProvider.class), eq(SOLVENT)))
        .thenReturn(dialog.solvent);
    when(dialog.solvent.setKey(any())).thenReturn(dialog.solvent);
    when(dialog.solvent.setComparator(any(Comparator.class))).thenReturn(dialog.solvent);
    when(dialog.solvent.setHeader(any(String.class))).thenReturn(dialog.solvent);
    dialog.solventVolume = mock(Column.class);
    when(dialog.samples.addColumn(any(ValueProvider.class), eq(SOLVENT_VOLUME)))
        .thenReturn(dialog.solventVolume);
    when(dialog.solventVolume.setKey(any())).thenReturn(dialog.solventVolume);
    when(dialog.solventVolume.setComparator(any(Comparator.class)))
        .thenReturn(dialog.solventVolume);
    when(dialog.solventVolume.setHeader(any(String.class))).thenReturn(dialog.solventVolume);
    dialog.name = mock(Column.class);
    when(dialog.samples.addColumn(any(ValueProvider.class), eq(NAME))).thenReturn(dialog.name);
    when(dialog.name.setKey(any())).thenReturn(dialog.name);
    when(dialog.name.setComparator(any(Comparator.class))).thenReturn(dialog.name);
    when(dialog.name.setHeader(any(String.class))).thenReturn(dialog.name);
    dialog.quantity = mock(Column.class);
    when(dialog.samples.addColumn(any(ValueProvider.class), eq(QUANTITY)))
        .thenReturn(dialog.quantity);
    when(dialog.quantity.setKey(any())).thenReturn(dialog.quantity);
    when(dialog.quantity.setComparator(any(Comparator.class))).thenReturn(dialog.quantity);
    when(dialog.quantity.setHeader(any(String.class))).thenReturn(dialog.quantity);
    dialog.destinationContainer = mock(Column.class);
    when(dialog.samples.addColumn(any(ValueProvider.class), eq(DESTINATION_CONTAINER)))
        .thenReturn(dialog.destinationContainer);
    when(dialog.destinationContainer.setKey(any())).thenReturn(dialog.destinationContainer);
    when(dialog.destinationContainer.setComparator(any(Comparator.class)))
        .thenReturn(dialog.destinationContainer);
    when(dialog.destinationContainer.setHeader(any(String.class)))
        .thenReturn(dialog.destinationContainer);
    dialog.number = mock(Column.class);
    when(dialog.samples.addColumn(any(ValueProvider.class), eq(NUMBER))).thenReturn(dialog.number);
    when(dialog.number.setKey(any())).thenReturn(dialog.number);
    when(dialog.number.setComparator(any(Comparator.class))).thenReturn(dialog.number);
    when(dialog.number.setHeader(any(String.class))).thenReturn(dialog.number);
    dialog.piInterval = mock(Column.class);
    when(dialog.samples.addColumn(any(ValueProvider.class), eq(PI_INTERVAL)))
        .thenReturn(dialog.piInterval);
    when(dialog.piInterval.setKey(any())).thenReturn(dialog.piInterval);
    when(dialog.piInterval.setComparator(any(Comparator.class))).thenReturn(dialog.piInterval);
    when(dialog.piInterval.setHeader(any(String.class))).thenReturn(dialog.piInterval);
    dialog.comment = mock(Column.class);
    when(dialog.samples.addColumn(any(ValueProvider.class), eq(COMMENT)))
        .thenReturn(dialog.comment);
    when(dialog.comment.setKey(any())).thenReturn(dialog.comment);
    when(dialog.comment.setComparator(any(Comparator.class))).thenReturn(dialog.comment);
    when(dialog.comment.setHeader(any(String.class))).thenReturn(dialog.comment);
  }

  @Test
  public void styles() {
    assertEquals(ID, dialog.getId().orElse(""));
    assertEquals(id(HEADER), dialog.header.getId().orElse(""));
    assertEquals(id(DELETED), dialog.deleted.getId().orElse(""));
    assertEquals(id(PROTOCOL), dialog.protocol.getId().orElse(""));
    assertEquals(id(FRACTIONATION_TYPE), dialog.fractionationType.getId().orElse(""));
    assertEquals(id(INSERT_TIME), dialog.date.getId().orElse(""));
    assertEquals(id(styleName(TREATED_SAMPLES, HEADER)), dialog.samplesHeader.getId().orElse(""));
    assertEquals(id(TREATED_SAMPLES), dialog.samples.getId().orElse(""));
  }

  @Test
  public void labels() {
    mockColumns();
    dialog.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), dialog.header.getText());
    assertEquals(treatmentResources.message(DELETED), dialog.deleted.getText());
    assertEquals(treatmentResources.message(TREATED_SAMPLES), dialog.samplesHeader.getText());
    verify(dialog.sample).setHeader(treatedSampleResources.message(SAMPLE));
    verify(dialog.container).setHeader(treatedSampleResources.message(CONTAINER));
    verify(dialog.sourceVolume).setHeader(treatedSampleResources.message(SOURCE_VOLUME));
    verify(dialog.solvent).setHeader(treatedSampleResources.message(SOLVENT));
    verify(dialog.solventVolume).setHeader(treatedSampleResources.message(SOLVENT_VOLUME));
    verify(dialog.name).setHeader(treatedSampleResources.message(NAME));
    verify(dialog.quantity).setHeader(treatedSampleResources.message(QUANTITY));
    verify(dialog.destinationContainer)
        .setHeader(treatedSampleResources.message(DESTINATION_CONTAINER));
    verify(dialog.number).setHeader(treatedSampleResources.message(NUMBER));
    verify(dialog.piInterval).setHeader(treatedSampleResources.message(PI_INTERVAL));
    verify(dialog.comment).setHeader(treatedSampleResources.message(COMMENT));
  }

  @Test
  public void localeChange() {
    mockColumns();
    dialog.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(TreatmentDialog.class, locale);
    final AppResources treatmentResources = new AppResources(Treatment.class, locale);
    final AppResources treatedSampleResources = new AppResources(TreatedSample.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    dialog.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), dialog.header.getText());
    assertEquals(treatmentResources.message(DELETED), dialog.deleted.getText());
    assertEquals(treatmentResources.message(TREATED_SAMPLES), dialog.samplesHeader.getText());
    verify(dialog.sample).setHeader(treatedSampleResources.message(SAMPLE));
    verify(dialog.container).setHeader(treatedSampleResources.message(CONTAINER));
    verify(dialog.sourceVolume).setHeader(treatedSampleResources.message(SOURCE_VOLUME));
    verify(dialog.solvent, atLeastOnce()).setHeader(treatedSampleResources.message(SOLVENT));
    verify(dialog.solventVolume).setHeader(treatedSampleResources.message(SOLVENT_VOLUME));
    verify(dialog.name).setHeader(treatedSampleResources.message(NAME));
    verify(dialog.quantity).setHeader(treatedSampleResources.message(QUANTITY));
    verify(dialog.destinationContainer, atLeastOnce())
        .setHeader(treatedSampleResources.message(DESTINATION_CONTAINER));
    verify(dialog.number).setHeader(treatedSampleResources.message(NUMBER));
    verify(dialog.piInterval).setHeader(treatedSampleResources.message(PI_INTERVAL));
    verify(dialog.comment).setHeader(treatedSampleResources.message(COMMENT));
  }

  @Test
  public void samples_Columns() {
    assertEquals(11, dialog.samples.getColumns().size());
    assertNotNull(dialog.samples.getColumnByKey(SAMPLE));
    assertTrue(dialog.samples.getColumnByKey(SAMPLE).isSortable());
    assertNotNull(dialog.samples.getColumnByKey(CONTAINER));
    assertTrue(dialog.samples.getColumnByKey(CONTAINER).isSortable());
    assertNotNull(dialog.samples.getColumnByKey(SOURCE_VOLUME));
    assertTrue(dialog.samples.getColumnByKey(SOURCE_VOLUME).isSortable());
    assertNotNull(dialog.samples.getColumnByKey(SOLVENT));
    assertTrue(dialog.samples.getColumnByKey(SOLVENT).isSortable());
    assertNotNull(dialog.samples.getColumnByKey(SOLVENT_VOLUME));
    assertTrue(dialog.samples.getColumnByKey(SOLVENT_VOLUME).isSortable());
    assertNotNull(dialog.samples.getColumnByKey(NAME));
    assertTrue(dialog.samples.getColumnByKey(NAME).isSortable());
    assertNotNull(dialog.samples.getColumnByKey(QUANTITY));
    assertTrue(dialog.samples.getColumnByKey(QUANTITY).isSortable());
    assertNotNull(dialog.samples.getColumnByKey(DESTINATION_CONTAINER));
    assertTrue(dialog.samples.getColumnByKey(DESTINATION_CONTAINER).isSortable());
    assertNotNull(dialog.samples.getColumnByKey(NUMBER));
    assertTrue(dialog.samples.getColumnByKey(NUMBER).isSortable());
    assertNotNull(dialog.samples.getColumnByKey(PI_INTERVAL));
    assertTrue(dialog.samples.getColumnByKey(PI_INTERVAL).isSortable());
    assertNotNull(dialog.samples.getColumnByKey(COMMENT));
    assertFalse(dialog.samples.getColumnByKey(COMMENT).isSortable());
  }

  @Test
  public void samples_ColumnsValueProvider() {
    dialog = new TreatmentDialog();
    mockColumns();
    dialog.init();
    verify(dialog.samples).addColumn(valueProviderCaptor.capture(), eq(SAMPLE));
    ValueProvider<TreatedSample, String> valueProvider = valueProviderCaptor.getValue();
    for (TreatedSample ts : treatedSamples) {
      assertEquals(ts.getSample().getName(), valueProvider.apply(ts));
    }
    verify(dialog.samples).addColumn(valueProviderCaptor.capture(), eq(CONTAINER));
    valueProvider = valueProviderCaptor.getValue();
    for (TreatedSample ts : treatedSamples) {
      assertEquals(ts.getContainer().getFullName(), valueProvider.apply(ts));
    }
    verify(dialog.samples).addColumn(valueProviderCaptor.capture(), eq(SOURCE_VOLUME));
    valueProvider = valueProviderCaptor.getValue();
    for (TreatedSample ts : treatedSamples) {
      assertEquals(ts.getSourceVolume(), valueProvider.apply(ts));
    }
    verify(dialog.samples).addColumn(valueProviderCaptor.capture(), eq(SOLVENT));
    valueProvider = valueProviderCaptor.getValue();
    for (TreatedSample ts : treatedSamples) {
      assertEquals(ts.getSolvent(), valueProvider.apply(ts));
    }
    verify(dialog.samples).addColumn(valueProviderCaptor.capture(), eq(SOLVENT_VOLUME));
    valueProvider = valueProviderCaptor.getValue();
    for (TreatedSample ts : treatedSamples) {
      assertEquals(ts.getSolventVolume(), valueProvider.apply(ts));
    }
    verify(dialog.samples).addColumn(valueProviderCaptor.capture(), eq(NAME));
    valueProvider = valueProviderCaptor.getValue();
    for (TreatedSample ts : treatedSamples) {
      assertEquals(ts.getName(), valueProvider.apply(ts));
    }
    verify(dialog.samples).addColumn(valueProviderCaptor.capture(), eq(QUANTITY));
    valueProvider = valueProviderCaptor.getValue();
    for (TreatedSample ts : treatedSamples) {
      assertEquals(ts.getQuantity(), valueProvider.apply(ts));
    }
    verify(dialog.samples).addColumn(valueProviderCaptor.capture(), eq(DESTINATION_CONTAINER));
    valueProvider = valueProviderCaptor.getValue();
    for (TreatedSample ts : treatedSamples) {
      assertEquals(
          ts.getDestinationContainer() != null ? ts.getDestinationContainer().getFullName() : "",
          valueProvider.apply(ts));
    }
    verify(dialog.samples).addColumn(valueProviderCaptor.capture(), eq(NUMBER));
    valueProvider = valueProviderCaptor.getValue();
    for (TreatedSample ts : treatedSamples) {
      assertEquals(ts.getNumber(), valueProvider.apply(ts));
    }
    verify(dialog.samples).addColumn(valueProviderCaptor.capture(), eq(PI_INTERVAL));
    valueProvider = valueProviderCaptor.getValue();
    for (TreatedSample ts : treatedSamples) {
      assertEquals(ts.getPiInterval(), valueProvider.apply(ts));
    }
    verify(dialog.samples).addColumn(valueProviderCaptor.capture(), eq(COMMENT));
    valueProvider = valueProviderCaptor.getValue();
    for (TreatedSample ts : treatedSamples) {
      assertEquals(ts.getComment(), valueProvider.apply(ts));
    }
  }

  @Test
  public void getTreatment() {
    Treatment treatment = repository.findById(1L).get();
    dialog.setTreatment(treatment);
    assertEquals(treatment, dialog.getTreatment());
  }

  @Test
  public void setTreatment_Solubilisation() {
    Treatment treatment = repository.findById(1L).get();
    dialog.localeChange(mock(LocaleChangeEvent.class));

    dialog.setTreatment(treatment);

    assertEquals(treatment.getType().getLabel(locale), dialog.header.getText());
    assertFalse(dialog.deleted.isVisible());
    assertFalse(dialog.protocol.isVisible());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(resources.message(INSERT_TIME, dateFormatter.format(treatment.getInsertTime())),
        dialog.date.getText());
    assertFalse(dialog.sourceVolume.isVisible());
    assertTrue(dialog.solvent.isVisible());
    assertTrue(dialog.solventVolume.isVisible());
    assertFalse(dialog.name.isVisible());
    assertFalse(dialog.quantity.isVisible());
    assertFalse(dialog.destinationContainer.isVisible());
    assertFalse(dialog.number.isVisible());
    assertFalse(dialog.piInterval.isVisible());
  }

  @Test
  public void setTreatment_FractionationMudPit() {
    Treatment treatment = repository.findById(2L).get();
    dialog.localeChange(mock(LocaleChangeEvent.class));

    dialog.setTreatment(treatment);

    assertEquals(treatment.getType().getLabel(locale), dialog.header.getText());
    assertFalse(dialog.deleted.isVisible());
    assertFalse(dialog.protocol.isVisible());
    assertTrue(dialog.fractionationType.isVisible());
    assertEquals(
        resources.message(FRACTIONATION_TYPE, treatment.getFractionationType().getLabel(locale)),
        dialog.fractionationType.getText());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(resources.message(INSERT_TIME, dateFormatter.format(treatment.getInsertTime())),
        dialog.date.getText());
    assertFalse(dialog.sourceVolume.isVisible());
    assertFalse(dialog.solvent.isVisible());
    assertFalse(dialog.solventVolume.isVisible());
    assertFalse(dialog.name.isVisible());
    assertFalse(dialog.quantity.isVisible());
    assertTrue(dialog.destinationContainer.isVisible());
    assertTrue(dialog.number.isVisible());
    assertFalse(dialog.piInterval.isVisible());
  }

  @Test
  public void setTreatment_FractionationPi() {
    Treatment treatment = repository.findById(2L).get();
    treatment.setFractionationType(FractionationType.PI);
    dialog.localeChange(mock(LocaleChangeEvent.class));

    dialog.setTreatment(treatment);

    assertEquals(treatment.getType().getLabel(locale), dialog.header.getText());
    assertFalse(dialog.deleted.isVisible());
    assertFalse(dialog.protocol.isVisible());
    assertTrue(dialog.fractionationType.isVisible());
    assertEquals(
        resources.message(FRACTIONATION_TYPE, treatment.getFractionationType().getLabel(locale)),
        dialog.fractionationType.getText());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(resources.message(INSERT_TIME, dateFormatter.format(treatment.getInsertTime())),
        dialog.date.getText());
    assertFalse(dialog.sourceVolume.isVisible());
    assertFalse(dialog.solvent.isVisible());
    assertFalse(dialog.solventVolume.isVisible());
    assertFalse(dialog.name.isVisible());
    assertFalse(dialog.quantity.isVisible());
    assertTrue(dialog.destinationContainer.isVisible());
    assertFalse(dialog.number.isVisible());
    assertTrue(dialog.piInterval.isVisible());
  }

  @Test
  public void setTreatment_Transfer() {
    Treatment treatment = repository.findById(3L).get();
    dialog.localeChange(mock(LocaleChangeEvent.class));

    dialog.setTreatment(treatment);

    assertEquals(treatment.getType().getLabel(locale), dialog.header.getText());
    assertFalse(dialog.deleted.isVisible());
    assertFalse(dialog.protocol.isVisible());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(resources.message(INSERT_TIME, dateFormatter.format(treatment.getInsertTime())),
        dialog.date.getText());
    assertFalse(dialog.sourceVolume.isVisible());
    assertFalse(dialog.solvent.isVisible());
    assertFalse(dialog.solventVolume.isVisible());
    assertFalse(dialog.name.isVisible());
    assertFalse(dialog.quantity.isVisible());
    assertTrue(dialog.destinationContainer.isVisible());
    assertFalse(dialog.number.isVisible());
    assertFalse(dialog.piInterval.isVisible());
  }

  @Test
  public void setTreatment_Dilution() {
    Treatment treatment = repository.findById(4L).get();
    dialog.localeChange(mock(LocaleChangeEvent.class));

    dialog.setTreatment(treatment);

    assertEquals(treatment.getType().getLabel(locale), dialog.header.getText());
    assertFalse(dialog.deleted.isVisible());
    assertFalse(dialog.protocol.isVisible());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(resources.message(INSERT_TIME, dateFormatter.format(treatment.getInsertTime())),
        dialog.date.getText());
    assertTrue(dialog.sourceVolume.isVisible());
    assertTrue(dialog.solvent.isVisible());
    assertTrue(dialog.solventVolume.isVisible());
    assertFalse(dialog.name.isVisible());
    assertFalse(dialog.quantity.isVisible());
    assertFalse(dialog.destinationContainer.isVisible());
    assertFalse(dialog.number.isVisible());
    assertFalse(dialog.piInterval.isVisible());
  }

  @Test
  public void setTreatment_StandardAddition() {
    Treatment treatment = repository.findById(5L).get();
    dialog.localeChange(mock(LocaleChangeEvent.class));

    dialog.setTreatment(treatment);

    assertEquals(treatment.getType().getLabel(locale), dialog.header.getText());
    assertFalse(dialog.deleted.isVisible());
    assertFalse(dialog.protocol.isVisible());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(resources.message(INSERT_TIME, dateFormatter.format(treatment.getInsertTime())),
        dialog.date.getText());
    assertFalse(dialog.sourceVolume.isVisible());
    assertFalse(dialog.solvent.isVisible());
    assertFalse(dialog.solventVolume.isVisible());
    assertTrue(dialog.name.isVisible());
    assertTrue(dialog.quantity.isVisible());
    assertFalse(dialog.destinationContainer.isVisible());
    assertFalse(dialog.number.isVisible());
    assertFalse(dialog.piInterval.isVisible());
  }

  @Test
  public void setTreatment_Digestion() {
    Treatment treatment = repository.findById(6L).get();
    dialog.localeChange(mock(LocaleChangeEvent.class));

    dialog.setTreatment(treatment);

    assertEquals(treatment.getType().getLabel(locale), dialog.header.getText());
    assertFalse(dialog.deleted.isVisible());
    assertTrue(dialog.protocol.isVisible());
    assertEquals(resources.message(PROTOCOL, treatment.getProtocol().getName()),
        dialog.protocol.getText());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(resources.message(INSERT_TIME, dateFormatter.format(treatment.getInsertTime())),
        dialog.date.getText());
    assertFalse(dialog.sourceVolume.isVisible());
    assertFalse(dialog.solvent.isVisible());
    assertFalse(dialog.solventVolume.isVisible());
    assertFalse(dialog.name.isVisible());
    assertFalse(dialog.quantity.isVisible());
    assertFalse(dialog.destinationContainer.isVisible());
    assertFalse(dialog.number.isVisible());
    assertFalse(dialog.piInterval.isVisible());
  }

  @Test
  public void setTreatment_Enrichment() {
    Treatment treatment = repository.findById(7L).get();
    dialog.localeChange(mock(LocaleChangeEvent.class));

    dialog.setTreatment(treatment);

    assertEquals(treatment.getType().getLabel(locale), dialog.header.getText());
    assertFalse(dialog.deleted.isVisible());
    assertTrue(dialog.protocol.isVisible());
    assertEquals(resources.message(PROTOCOL, treatment.getProtocol().getName()),
        dialog.protocol.getText());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(resources.message(INSERT_TIME, dateFormatter.format(treatment.getInsertTime())),
        dialog.date.getText());
    assertFalse(dialog.sourceVolume.isVisible());
    assertFalse(dialog.solvent.isVisible());
    assertFalse(dialog.solventVolume.isVisible());
    assertFalse(dialog.name.isVisible());
    assertFalse(dialog.quantity.isVisible());
    assertFalse(dialog.destinationContainer.isVisible());
    assertFalse(dialog.number.isVisible());
    assertFalse(dialog.piInterval.isVisible());
  }

  @Test
  public void setTreatment_Deleted() {
    Treatment treatment = repository.findById(323L).get();
    dialog.localeChange(mock(LocaleChangeEvent.class));

    dialog.setTreatment(treatment);

    assertEquals(treatment.getType().getLabel(locale), dialog.header.getText());
    assertTrue(dialog.deleted.isVisible());
    assertFalse(dialog.protocol.isVisible());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(resources.message(INSERT_TIME, dateFormatter.format(treatment.getInsertTime())),
        dialog.date.getText());
    assertFalse(dialog.sourceVolume.isVisible());
    assertFalse(dialog.solvent.isVisible());
    assertFalse(dialog.solventVolume.isVisible());
    assertFalse(dialog.name.isVisible());
    assertFalse(dialog.quantity.isVisible());
    assertTrue(dialog.destinationContainer.isVisible());
    assertFalse(dialog.number.isVisible());
    assertFalse(dialog.piInterval.isVisible());
  }

  @Test
  public void setTreatment_BeforeLocalChange() {
    Treatment treatment = repository.findById(6L).get();

    dialog.setTreatment(treatment);
    dialog.localeChange(mock(LocaleChangeEvent.class));

    assertEquals(treatment.getType().getLabel(locale), dialog.header.getText());
    assertFalse(dialog.deleted.isVisible());
    assertTrue(dialog.protocol.isVisible());
    assertEquals(resources.message(PROTOCOL, treatment.getProtocol().getName()),
        dialog.protocol.getText());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(resources.message(INSERT_TIME, dateFormatter.format(treatment.getInsertTime())),
        dialog.date.getText());
    assertFalse(dialog.sourceVolume.isVisible());
    assertFalse(dialog.solvent.isVisible());
    assertFalse(dialog.solventVolume.isVisible());
    assertFalse(dialog.name.isVisible());
    assertFalse(dialog.quantity.isVisible());
    assertFalse(dialog.destinationContainer.isVisible());
    assertFalse(dialog.number.isVisible());
    assertFalse(dialog.piInterval.isVisible());
  }

  @Test
  public void setTreatment_Null() {
    assertThrows(NullPointerException.class, () -> {
      dialog.setTreatment(null);
    });
  }
}
