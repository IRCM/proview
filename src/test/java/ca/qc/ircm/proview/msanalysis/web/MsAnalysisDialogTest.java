package ca.qc.ircm.proview.msanalysis.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.messagePrefix;
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
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog.id;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.AcquisitionRepository;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisRepository;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.submission.web.HistoryView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link MsAnalysisDialog}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class MsAnalysisDialogTest extends SpringUIUnitTest {
  private static final String MESSAGES_PREFIX = messagePrefix(MsAnalysisDialog.class);
  private static final String MS_ANALYSIS_PREFIX = messagePrefix(MsAnalysis.class);
  private static final String ACQUISITION_PREFIX = messagePrefix(Acquisition.class);
  private static final String MASS_DETECTION_INSTRUMENT_PREFIX =
      messagePrefix(MassDetectionInstrument.class);
  private static final String MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX =
      messagePrefix(MassDetectionInstrumentSource.class);
  private MsAnalysisDialog dialog;
  @MockitoBean
  private MsAnalysisService service;
  @Autowired
  private MsAnalysisRepository repository;
  @Autowired
  private AcquisitionRepository acquisitionRepository;
  private Locale locale = ENGLISH;
  private List<Acquisition> acquisitions;

  /**
   * Before tests.
   */
  @BeforeEach
  public void beforeTest() throws NoSuchFieldException, IllegalAccessException {
    when(service.get(anyLong())).then(i -> repository.findById(i.getArgument(0)));
    UI.getCurrent().setLocale(locale);
    HistoryView view = navigate(HistoryView.class, 32L);
    @SuppressWarnings("unchecked")
    Grid<Activity> activities = test(view).find(Grid.class).id(HistoryView.ACTIVITIES);
    test(activities).doubleClickRow(1);
    dialog = $(MsAnalysisDialog.class).id(ID);
    acquisitions = acquisitionRepository.findAll();
  }

  private int indexOfColumn(String property) {
    return test(dialog.acquisitions).getColumnPosition(property);
  }

  @Test
  public void styles() {
    assertEquals(ID, dialog.getId().orElse(""));
    assertEquals(id(DELETED), dialog.deleted.getId().orElse(""));
    assertEquals(id(MASS_DETECTION_INSTRUMENT), dialog.instrument.getId().orElse(""));
    assertEquals(id(SOURCE), dialog.source.getId().orElse(""));
    assertEquals(id(INSERT_TIME), dialog.date.getId().orElse(""));
    assertEquals(id(styleName(ACQUISITIONS, HEADER)), dialog.acquisitionsHeader.getId().orElse(""));
    assertEquals(id(ACQUISITIONS), dialog.acquisitions.getId().orElse(""));
  }

  @Test
  public void labels() {
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + HEADER), dialog.getHeaderTitle());
    assertEquals(dialog.getTranslation(MS_ANALYSIS_PREFIX + DELETED), dialog.deleted.getText());
    assertEquals(dialog.getTranslation(MS_ANALYSIS_PREFIX + ACQUISITIONS),
        dialog.acquisitionsHeader.getText());
    HeaderRow header = dialog.acquisitions.getHeaderRows().get(0);
    assertEquals(dialog.getTranslation(ACQUISITION_PREFIX + SAMPLE),
        header.getCell(dialog.sample).getText());
    assertEquals(dialog.getTranslation(ACQUISITION_PREFIX + CONTAINER),
        header.getCell(dialog.container).getText());
    assertEquals(dialog.getTranslation(ACQUISITION_PREFIX + NUMBER_OF_ACQUISITION),
        header.getCell(dialog.numberOfAcquisition).getText());
    assertEquals(dialog.getTranslation(ACQUISITION_PREFIX + SAMPLE_LIST_NAME),
        header.getCell(dialog.sampleListName).getText());
    assertEquals(dialog.getTranslation(ACQUISITION_PREFIX + ACQUISITION_FILE),
        header.getCell(dialog.acquisitionFile).getText());
    assertEquals(dialog.getTranslation(ACQUISITION_PREFIX + POSITION),
        header.getCell(dialog.position).getText());
    assertEquals(dialog.getTranslation(ACQUISITION_PREFIX + COMMENT),
        header.getCell(dialog.comment).getText());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + HEADER), dialog.getHeaderTitle());
    assertEquals(dialog.getTranslation(MS_ANALYSIS_PREFIX + DELETED), dialog.deleted.getText());
    assertEquals(dialog.getTranslation(MS_ANALYSIS_PREFIX + ACQUISITIONS),
        dialog.acquisitionsHeader.getText());
    HeaderRow header = dialog.acquisitions.getHeaderRows().get(0);
    assertEquals(dialog.getTranslation(ACQUISITION_PREFIX + SAMPLE),
        header.getCell(dialog.sample).getText());
    assertEquals(dialog.getTranslation(ACQUISITION_PREFIX + CONTAINER),
        header.getCell(dialog.container).getText());
    assertEquals(dialog.getTranslation(ACQUISITION_PREFIX + NUMBER_OF_ACQUISITION),
        header.getCell(dialog.numberOfAcquisition).getText());
    assertEquals(dialog.getTranslation(ACQUISITION_PREFIX + SAMPLE_LIST_NAME),
        header.getCell(dialog.sampleListName).getText());
    assertEquals(dialog.getTranslation(ACQUISITION_PREFIX + ACQUISITION_FILE),
        header.getCell(dialog.acquisitionFile).getText());
    assertEquals(dialog.getTranslation(ACQUISITION_PREFIX + POSITION),
        header.getCell(dialog.position).getText());
    assertEquals(dialog.getTranslation(ACQUISITION_PREFIX + COMMENT),
        header.getCell(dialog.comment).getText());
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
    dialog.acquisitions.setItems(acquisitions);
    for (int i = 0; i < acquisitions.size(); i++) {
      Acquisition acquisition = acquisitions.get(i);
      assertEquals(acquisition.getSample().getName(),
          test(dialog.acquisitions).getCellText(i, indexOfColumn(SAMPLE)));
      assertEquals(acquisition.getContainer().getFullName(),
          test(dialog.acquisitions).getCellText(i, indexOfColumn(CONTAINER)));
      assertEquals(Objects.toString(acquisition.getNumberOfAcquisition()),
          test(dialog.acquisitions).getCellText(i, indexOfColumn(NUMBER_OF_ACQUISITION)));
      assertEquals(acquisition.getSampleListName(),
          test(dialog.acquisitions).getCellText(i, indexOfColumn(SAMPLE_LIST_NAME)));
      assertEquals(acquisition.getAcquisitionFile(),
          test(dialog.acquisitions).getCellText(i, indexOfColumn(ACQUISITION_FILE)));
      assertEquals(Objects.toString(acquisition.getPosition()),
          test(dialog.acquisitions).getCellText(i, indexOfColumn(POSITION)));
      assertEquals(Objects.toString(acquisition.getComment(), ""),
          test(dialog.acquisitions).getCellText(i, indexOfColumn(COMMENT)));
    }
  }

  @Test
  public void getMsAnalysisId() {
    assertEquals(12L, dialog.getMsAnalysisId());
  }

  @Test
  public void setMsAnalysisId() {
    MsAnalysis msAnalysis = repository.findById(1L).orElseThrow();

    dialog.setMsAnalysisId(1L);

    verify(service).get(1L);
    assertFalse(dialog.deleted.isVisible());
    assertEquals(
        dialog.getTranslation(MESSAGES_PREFIX + MASS_DETECTION_INSTRUMENT,
            dialog.getTranslation(
                MASS_DETECTION_INSTRUMENT_PREFIX + msAnalysis.getMassDetectionInstrument().name())),
        dialog.instrument.getText());
    assertEquals(
        dialog.getTranslation(MESSAGES_PREFIX + SOURCE,
            dialog.getTranslation(
                MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + msAnalysis.getSource().name())),
        dialog.source.getText());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + INSERT_TIME,
        dateFormatter.format(msAnalysis.getInsertTime())), dialog.date.getText());
  }

  @Test
  public void setMsAnalysisId_Deleted() {
    MsAnalysis msAnalysis = repository.findById(12L).orElseThrow();
    msAnalysis.setDeleted(true);
    msAnalysis.setDeletionExplanation("Test deletion\nexplanation");

    dialog.setMsAnalysisId(12L);

    verify(service, times(2)).get(12L);
    assertTrue(dialog.deleted.isVisible());
    assertEquals(
        dialog.getTranslation(MESSAGES_PREFIX + MASS_DETECTION_INSTRUMENT,
            dialog.getTranslation(
                MASS_DETECTION_INSTRUMENT_PREFIX + msAnalysis.getMassDetectionInstrument().name())),
        dialog.instrument.getText());
    assertEquals(
        dialog.getTranslation(MESSAGES_PREFIX + SOURCE,
            dialog.getTranslation(
                MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + msAnalysis.getSource().name())),
        dialog.source.getText());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + INSERT_TIME,
        dateFormatter.format(msAnalysis.getInsertTime())), dialog.date.getText());
  }

  @Test
  public void setMsAnalysisId_LocalChange() {
    MsAnalysis msAnalysis = repository.findById(1L).orElseThrow();
    dialog.setMsAnalysisId(1L);
    verify(service).get(1L);

    Locale locale = Locale.FRENCH;
    UI.getCurrent().setLocale(locale);

    assertFalse(dialog.deleted.isVisible());
    assertEquals(
        dialog.getTranslation(MESSAGES_PREFIX + MASS_DETECTION_INSTRUMENT,
            dialog.getTranslation(
                MASS_DETECTION_INSTRUMENT_PREFIX + msAnalysis.getMassDetectionInstrument().name())),
        dialog.instrument.getText());
    assertEquals(
        dialog.getTranslation(MESSAGES_PREFIX + SOURCE,
            dialog.getTranslation(
                MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + msAnalysis.getSource().name())),
        dialog.source.getText());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    assertEquals(dialog.getTranslation(MESSAGES_PREFIX + INSERT_TIME,
        dateFormatter.format(msAnalysis.getInsertTime())), dialog.date.getText());
  }

  @Test
  public void setMsAnalysisId_0() {
    assertThrows(NoSuchElementException.class, () -> {
      dialog.setMsAnalysisId(0);
    });
  }
}
