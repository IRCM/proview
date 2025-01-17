package ca.qc.ircm.proview.msanalysis.web;

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
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;

import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * MS analysis dialog.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MsAnalysisDialog extends Dialog implements LocaleChangeObserver {
  public static final String ID = "ms-analysis-dialog";
  public static final String HEADER = "header";
  private static final String MESSAGES_PREFIX = messagePrefix(MsAnalysisDialog.class);
  private static final String MS_ANALYSIS_PREFIX = messagePrefix(MsAnalysis.class);
  private static final String ACQUISITION_PREFIX = messagePrefix(Acquisition.class);
  private static final String MASS_DETECTION_INSTRUMENT_PREFIX =
      messagePrefix(MassDetectionInstrument.class);
  private static final String MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX =
      messagePrefix(MassDetectionInstrumentSource.class);
  private static final long serialVersionUID = -6114501684325516594L;
  private static final Logger logger = LoggerFactory.getLogger(MsAnalysisDialog.class);
  protected Div deleted = new Div();
  protected Div instrument = new Div();
  protected Div source = new Div();
  protected Div date = new Div();
  protected H4 acquisitionsHeader = new H4();
  protected Grid<Acquisition> acquisitions = new Grid<>();
  protected Column<Acquisition> sample;
  protected Column<Acquisition> container;
  protected Column<Acquisition> numberOfAcquisition;
  protected Column<Acquisition> sampleListName;
  protected Column<Acquisition> acquisitionFile;
  protected Column<Acquisition> position;
  protected Column<Acquisition> comment;
  private MsAnalysis msAnalysis;
  private transient MsAnalysisService service;

  @Autowired
  protected MsAnalysisDialog(MsAnalysisService service) {
    this.service = service;
  }

  public static String id(String baseId) {
    return styleName(ID, baseId);
  }

  @PostConstruct
  void init() {
    logger.debug("treatment dialog");
    setId(ID);
    setWidth("1300px");
    setResizable(true);
    VerticalLayout layout = new VerticalLayout();
    add(layout);
    layout.add(deleted, instrument, source, date, acquisitionsHeader, acquisitions);
    layout.setSizeFull();
    layout.expand(acquisitions);
    deleted.setId(id(DELETED));
    deleted.setVisible(false);
    instrument.setId(id(MASS_DETECTION_INSTRUMENT));
    source.setId(id(SOURCE));
    date.setId(id(INSERT_TIME));
    acquisitionsHeader.setId(id(styleName(ACQUISITIONS, HEADER)));
    acquisitions.setId(id(ACQUISITIONS));
    sample = acquisitions.addColumn(ac -> ac.getSample().getName(), SAMPLE).setKey(SAMPLE);
    container =
        acquisitions.addColumn(ac -> ac.getContainer().getFullName(), CONTAINER).setKey(CONTAINER);
    numberOfAcquisition =
        acquisitions.addColumn(Acquisition::getNumberOfAcquisition, NUMBER_OF_ACQUISITION)
            .setKey(NUMBER_OF_ACQUISITION).setFlexGrow(0);
    sampleListName = acquisitions.addColumn(Acquisition::getSampleListName, SAMPLE_LIST_NAME)
        .setKey(SAMPLE_LIST_NAME);
    acquisitionFile = acquisitions.addColumn(Acquisition::getAcquisitionFile, ACQUISITION_FILE)
        .setKey(ACQUISITION_FILE);
    position =
        acquisitions.addColumn(Acquisition::getPosition, POSITION).setKey(POSITION).setFlexGrow(0);
    comment =
        acquisitions.addColumn(Acquisition::getComment, COMMENT).setKey(COMMENT).setSortable(false);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    localeChanged();
  }

  private void localeChanged() {
    setHeaderTitle(getTranslation(MESSAGES_PREFIX + HEADER));
    deleted.setText(getTranslation(MS_ANALYSIS_PREFIX + property(DELETED, true)));
    acquisitionsHeader.setText(getTranslation(MS_ANALYSIS_PREFIX + ACQUISITIONS));
    sample.setHeader(getTranslation(ACQUISITION_PREFIX + SAMPLE));
    container.setHeader(getTranslation(ACQUISITION_PREFIX + CONTAINER));
    numberOfAcquisition.setHeader(getTranslation(ACQUISITION_PREFIX + NUMBER_OF_ACQUISITION));
    sampleListName.setHeader(getTranslation(ACQUISITION_PREFIX + SAMPLE_LIST_NAME));
    acquisitionFile.setHeader(getTranslation(ACQUISITION_PREFIX + ACQUISITION_FILE));
    position.setHeader(getTranslation(ACQUISITION_PREFIX + POSITION));
    comment.setHeader(getTranslation(ACQUISITION_PREFIX + COMMENT));
    instrument.setText(getTranslation(MESSAGES_PREFIX + MASS_DETECTION_INSTRUMENT, getTranslation(
        MASS_DETECTION_INSTRUMENT_PREFIX + msAnalysis.getMassDetectionInstrument().name())));
    source.setText(getTranslation(MESSAGES_PREFIX + SOURCE,
        getTranslation(MASS_DETECTION_INSTRUMENT_SOURCE_PREFIX + msAnalysis.getSource().name())));
    deleted.setVisible(msAnalysis.isDeleted());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    date.setText(getTranslation(MESSAGES_PREFIX + INSERT_TIME,
        dateFormatter.format(msAnalysis.getInsertTime())));
    acquisitions.setItems(msAnalysis.getAcquisitions());
  }

  public long getMsAnalysisId() {
    return msAnalysis.getId();
  }

  public void setMsAnalysisId(long id) {
    this.msAnalysis = service.get(id).orElseThrow();
    localeChanged();
  }
}
