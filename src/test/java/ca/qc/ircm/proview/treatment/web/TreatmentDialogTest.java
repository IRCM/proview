package ca.qc.ircm.proview.treatment.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.messagePrefix;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.submission.web.HistoryView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.FractionationType;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.treatment.TreatedSampleRepository;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.TreatmentRepository;
import ca.qc.ircm.proview.treatment.TreatmentService;
import ca.qc.ircm.proview.treatment.TreatmentType;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link TreatmentDialog}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class TreatmentDialogTest extends SpringUIUnitTest {

  private static final String MESSAGES_PREFIX = messagePrefix(TreatmentDialog.class);
  private static final String TREATMENT_PREFIX = messagePrefix(Treatment.class);
  private static final String TREATED_SAMPLE_PREFIX = messagePrefix(TreatedSample.class);
  private static final String FRACTIONATION_TYPE_PREFIX = messagePrefix(FractionationType.class);
  private static final String TREATMENT_TYPE_PREFIX = messagePrefix(TreatmentType.class);
  private TreatmentDialog dialog;
  @MockitoBean
  private TreatmentService service;
  @Autowired
  private TreatmentRepository repository;
  @Autowired
  private TreatedSampleRepository treatedSampleRepository;
  private Locale locale = ENGLISH;
  private List<TreatedSample> treatedSamples;

  /**
   * Before tests.
   */
  @BeforeEach
  public void beforeTest() {
    when(service.get(anyLong())).then(
        i -> i.getArgument(0) != null ? repository.findById(i.getArgument(0)) : Optional.empty());
    UI.getCurrent().setLocale(locale);
    treatedSamples = treatedSampleRepository.findAll();
    HistoryView view = navigate(HistoryView.class, 147L);
    @SuppressWarnings("unchecked")
    Grid<Activity> activities = test(view).find(Grid.class).id(HistoryView.ACTIVITIES);
    test(activities).doubleClickRow(3);
    dialog = $(TreatmentDialog.class).first();
  }

  private int indexOfColumn(String property) {
    return test(dialog.samples).getColumnPosition(property);
  }

  @Test
  public void styles() {
    assertEquals(ID, dialog.getId().orElse(""));
    assertEquals(id(DELETED), dialog.deleted.getId().orElse(""));
    assertEquals(id(PROTOCOL), dialog.protocol.getId().orElse(""));
    assertEquals(id(FRACTIONATION_TYPE), dialog.fractionationType.getId().orElse(""));
    assertEquals(id(INSERT_TIME), dialog.date.getId().orElse(""));
    assertEquals(id(styleName(TREATED_SAMPLES, HEADER)), dialog.samplesHeader.getId().orElse(""));
    assertEquals(id(TREATED_SAMPLES), dialog.samples.getId().orElse(""));
  }

  @Test
  public void labels() {
    assertEquals(dialog.getTranslation(TREATMENT_TYPE_PREFIX + TreatmentType.TRANSFER.name()),
        dialog.getHeaderTitle());
    assertEquals(dialog.getTranslation(TREATMENT_PREFIX + DELETED), dialog.deleted.getText());
    assertEquals(dialog.getTranslation(TREATMENT_PREFIX + TREATED_SAMPLES),
        dialog.samplesHeader.getText());
    HeaderRow headerRow = dialog.samples.getHeaderRows().get(0);
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + SAMPLE),
        headerRow.getCell(dialog.sample).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + CONTAINER),
        headerRow.getCell(dialog.container).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + SOURCE_VOLUME),
        headerRow.getCell(dialog.sourceVolume).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + SOLVENT),
        headerRow.getCell(dialog.solvent).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + SOLVENT_VOLUME),
        headerRow.getCell(dialog.solventVolume).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + NAME),
        headerRow.getCell(dialog.name).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + QUANTITY),
        headerRow.getCell(dialog.quantity).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + DESTINATION_CONTAINER),
        headerRow.getCell(dialog.destinationContainer).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + NUMBER),
        headerRow.getCell(dialog.number).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + PI_INTERVAL),
        headerRow.getCell(dialog.piInterval).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + COMMENT),
        headerRow.getCell(dialog.comment).getText());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    assertEquals(dialog.getTranslation(TREATMENT_TYPE_PREFIX + TreatmentType.TRANSFER.name()),
        dialog.getHeaderTitle());
    assertEquals(dialog.getTranslation(TREATMENT_PREFIX + DELETED), dialog.deleted.getText());
    assertEquals(dialog.getTranslation(TREATMENT_PREFIX + TREATED_SAMPLES),
        dialog.samplesHeader.getText());
    HeaderRow headerRow = dialog.samples.getHeaderRows().get(0);
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + SAMPLE),
        headerRow.getCell(dialog.sample).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + CONTAINER),
        headerRow.getCell(dialog.container).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + SOURCE_VOLUME),
        headerRow.getCell(dialog.sourceVolume).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + SOLVENT),
        headerRow.getCell(dialog.solvent).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + SOLVENT_VOLUME),
        headerRow.getCell(dialog.solventVolume).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + NAME),
        headerRow.getCell(dialog.name).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + QUANTITY),
        headerRow.getCell(dialog.quantity).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + DESTINATION_CONTAINER),
        headerRow.getCell(dialog.destinationContainer).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + NUMBER),
        headerRow.getCell(dialog.number).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + PI_INTERVAL),
        headerRow.getCell(dialog.piInterval).getText());
    assertEquals(dialog.getTranslation(TREATED_SAMPLE_PREFIX + COMMENT),
        headerRow.getCell(dialog.comment).getText());
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
    dialog.samples.setItems(treatedSamples);
    dialog.samples.getColumns().forEach(col -> col.setVisible(true));
    for (int i = 0; i < treatedSamples.size(); i++) {
      TreatedSample treatedSample = treatedSamples.get(i);
      assertEquals(treatedSample.getSample().getName(),
          test(dialog.samples).getCellText(i, indexOfColumn(SAMPLE)));
      assertEquals(treatedSample.getContainer().getFullName(),
          test(dialog.samples).getCellText(i, indexOfColumn(CONTAINER)));
      assertEquals(Objects.toString(treatedSample.getSourceVolume(), ""),
          test(dialog.samples).getCellText(i, indexOfColumn(SOURCE_VOLUME)));
      assertEquals(Objects.toString(treatedSample.getSolvent(), ""),
          test(dialog.samples).getCellText(i, indexOfColumn(SOLVENT)));
      assertEquals(Objects.toString(treatedSample.getSolventVolume(), ""),
          test(dialog.samples).getCellText(i, indexOfColumn(SOLVENT_VOLUME)));
      assertEquals(Objects.toString(treatedSample.getName(), ""),
          test(dialog.samples).getCellText(i, indexOfColumn(NAME)));
      assertEquals(Objects.toString(treatedSample.getQuantity(), ""),
          test(dialog.samples).getCellText(i, indexOfColumn(QUANTITY)));
      assertEquals(
          Optional.ofNullable(treatedSample.getDestinationContainer())
              .map(SampleContainer::getFullName).orElse(""),
          test(dialog.samples).getCellText(i, indexOfColumn(DESTINATION_CONTAINER)));
      assertEquals(Objects.toString(treatedSample.getNumber(), ""),
          test(dialog.samples).getCellText(i, indexOfColumn(NUMBER)));
      assertEquals(Objects.toString(treatedSample.getPiInterval(), ""),
          test(dialog.samples).getCellText(i, indexOfColumn(PI_INTERVAL)));
      assertEquals(Objects.toString(treatedSample.getComment(), ""),
          test(dialog.samples).getCellText(i, indexOfColumn(COMMENT)));
    }
  }

  @Test
  public void getTreatmentId() {
    assertEquals(194L, dialog.getTreatmentId());
  }

  @Test
  public void setTreatmentId_Solubilisation() {
    Treatment treatment = repository.findById(1L).orElseThrow();

    dialog.setTreatmentId(1L);

    assertEquals(dialog.getTranslation(TREATMENT_TYPE_PREFIX + treatment.getType().name()),
        dialog.getHeaderTitle());
    assertFalse(dialog.deleted.isVisible());
    assertFalse(dialog.protocol.isVisible());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + INSERT_TIME,
        dateFormatter.format(treatment.getInsertTime())), dialog.date.getText());
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
  public void setTreatmentId_FractionationMudPit() {
    Treatment treatment = repository.findById(2L).orElseThrow();

    dialog.setTreatmentId(2L);

    assertEquals(dialog.getTranslation(TREATMENT_TYPE_PREFIX + treatment.getType().name()),
        dialog.getHeaderTitle());
    assertFalse(dialog.deleted.isVisible());
    assertFalse(dialog.protocol.isVisible());
    assertTrue(dialog.fractionationType.isVisible());
    assertNotNull(treatment.getFractionationType());
    assertEquals(
        dialog.getTranslation(MESSAGES_PREFIX + FRACTIONATION_TYPE,
            dialog.getTranslation(
                FRACTIONATION_TYPE_PREFIX + treatment.getFractionationType().name())),
        dialog.fractionationType.getText());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + INSERT_TIME,
        dateFormatter.format(treatment.getInsertTime())), dialog.date.getText());
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
  public void setTreatmentId_FractionationPi() {
    Treatment treatment = repository.findById(2L).orElseThrow();
    treatment.setFractionationType(FractionationType.PI);

    dialog.setTreatmentId(2L);

    assertEquals(dialog.getTranslation(TREATMENT_TYPE_PREFIX + treatment.getType().name()),
        dialog.getHeaderTitle());
    assertFalse(dialog.deleted.isVisible());
    assertFalse(dialog.protocol.isVisible());
    assertTrue(dialog.fractionationType.isVisible());
    assertNotNull(treatment.getFractionationType());
    assertEquals(
        dialog.getTranslation(MESSAGES_PREFIX + FRACTIONATION_TYPE,
            dialog.getTranslation(
                FRACTIONATION_TYPE_PREFIX + treatment.getFractionationType().name())),
        dialog.fractionationType.getText());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + INSERT_TIME,
        dateFormatter.format(treatment.getInsertTime())), dialog.date.getText());
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
  public void setTreatmentId_Transfer() {
    Treatment treatment = repository.findById(3L).orElseThrow();

    dialog.setTreatmentId(3L);

    assertEquals(dialog.getTranslation(TREATMENT_TYPE_PREFIX + treatment.getType().name()),
        dialog.getHeaderTitle());
    assertFalse(dialog.deleted.isVisible());
    assertFalse(dialog.protocol.isVisible());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + INSERT_TIME,
        dateFormatter.format(treatment.getInsertTime())), dialog.date.getText());
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
  public void setTreatmentId_Dilution() {
    Treatment treatment = repository.findById(4L).orElseThrow();

    dialog.setTreatmentId(4L);

    assertEquals(dialog.getTranslation(TREATMENT_TYPE_PREFIX + treatment.getType().name()),
        dialog.getHeaderTitle());
    assertFalse(dialog.deleted.isVisible());
    assertFalse(dialog.protocol.isVisible());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + INSERT_TIME,
        dateFormatter.format(treatment.getInsertTime())), dialog.date.getText());
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
  public void setTreatmentId_StandardAddition() {
    Treatment treatment = repository.findById(5L).orElseThrow();

    dialog.setTreatmentId(5L);

    assertEquals(dialog.getTranslation(TREATMENT_TYPE_PREFIX + treatment.getType().name()),
        dialog.getHeaderTitle());
    assertFalse(dialog.deleted.isVisible());
    assertFalse(dialog.protocol.isVisible());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + INSERT_TIME,
        dateFormatter.format(treatment.getInsertTime())), dialog.date.getText());
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
  public void setTreatmentId_Digestion() {
    Treatment treatment = repository.findById(6L).orElseThrow();

    dialog.setTreatmentId(6L);

    assertEquals(dialog.getTranslation(TREATMENT_TYPE_PREFIX + treatment.getType().name()),
        dialog.getHeaderTitle());
    assertFalse(dialog.deleted.isVisible());
    assertTrue(dialog.protocol.isVisible());
    assertNotNull(treatment.getProtocol());
    assertEquals(
        dialog.getTranslation(MESSAGES_PREFIX + PROTOCOL, treatment.getProtocol().getName()),
        dialog.protocol.getText());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + INSERT_TIME,
        dateFormatter.format(treatment.getInsertTime())), dialog.date.getText());
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
  public void setTreatmentId_Enrichment() {
    Treatment treatment = repository.findById(7L).orElseThrow();

    dialog.setTreatmentId(7L);

    assertEquals(dialog.getTranslation(TREATMENT_TYPE_PREFIX + treatment.getType().name()),
        dialog.getHeaderTitle());
    assertFalse(dialog.deleted.isVisible());
    assertTrue(dialog.protocol.isVisible());
    assertNotNull(treatment.getProtocol());
    assertEquals(
        dialog.getTranslation(MESSAGES_PREFIX + PROTOCOL, treatment.getProtocol().getName()),
        dialog.protocol.getText());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + INSERT_TIME,
        dateFormatter.format(treatment.getInsertTime())), dialog.date.getText());
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
  public void setTreatmentId_Deleted() {
    Treatment treatment = repository.findById(323L).orElseThrow();

    dialog.setTreatmentId(323L);

    assertEquals(dialog.getTranslation(TREATMENT_TYPE_PREFIX + treatment.getType().name()),
        dialog.getHeaderTitle());
    assertTrue(dialog.deleted.isVisible());
    assertFalse(dialog.protocol.isVisible());
    assertFalse(dialog.fractionationType.isVisible());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + INSERT_TIME,
        dateFormatter.format(treatment.getInsertTime())), dialog.date.getText());
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
  public void setTreatmentId_0() {
    assertThrows(NoSuchElementException.class, () -> dialog.setTreatmentId(0));
  }
}
