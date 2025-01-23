package ca.qc.ircm.proview.treatment.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.text.Strings.property;
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
import static ca.qc.ircm.proview.treatment.TreatmentType.DILUTION;
import static ca.qc.ircm.proview.treatment.TreatmentType.FRACTIONATION;
import static ca.qc.ircm.proview.treatment.TreatmentType.SOLUBILISATION;
import static ca.qc.ircm.proview.treatment.TreatmentType.STANDARD_ADDITION;
import static ca.qc.ircm.proview.treatment.TreatmentType.TRANSFER;

import ca.qc.ircm.proview.treatment.FractionationType;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.TreatmentService;
import ca.qc.ircm.proview.treatment.TreatmentType;
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
import java.io.Serial;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Treatment dialog.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TreatmentDialog extends Dialog implements LocaleChangeObserver {
  public static final String ID = "treatment-dialog";
  public static final String HEADER = "header";
  private static final String MESSAGES_PREFIX = messagePrefix(TreatmentDialog.class);
  private static final String TREATMENT_PREFIX = messagePrefix(Treatment.class);
  private static final String TREATED_SAMPLE_PREFIX = messagePrefix(TreatedSample.class);
  private static final String FRACTIONATION_TYPE_PREFIX = messagePrefix(FractionationType.class);
  private static final String TREATMENT_TYPE_PREFIX = messagePrefix(TreatmentType.class);
  @Serial
  private static final long serialVersionUID = -3458086086713549138L;
  private static final Logger logger = LoggerFactory.getLogger(TreatmentDialog.class);
  protected Div deleted = new Div();
  protected Div protocol = new Div();
  protected Div fractionationType = new Div();
  protected Div date = new Div();
  protected H4 samplesHeader = new H4();
  protected Grid<TreatedSample> samples = new Grid<>();
  protected Column<TreatedSample> sample;
  protected Column<TreatedSample> container;
  protected Column<TreatedSample> sourceVolume;
  protected Column<TreatedSample> solvent;
  protected Column<TreatedSample> solventVolume;
  protected Column<TreatedSample> name;
  protected Column<TreatedSample> quantity;
  protected Column<TreatedSample> destinationContainer;
  protected Column<TreatedSample> number;
  protected Column<TreatedSample> piInterval;
  protected Column<TreatedSample> comment;
  private Treatment treatment;
  private transient TreatmentService service;

  @Autowired
  protected TreatmentDialog(TreatmentService service) {
    this.service = service;
  }

  public static String id(String baseId) {
    return styleName(ID, baseId);
  }

  @PostConstruct
  void init() {
    logger.debug("treatment dialog");
    setId(ID);
    setWidth("1000px");
    setResizable(true);
    VerticalLayout layout = new VerticalLayout();
    add(layout);
    layout.add(deleted, protocol, fractionationType, date, samplesHeader, samples);
    layout.setSizeFull();
    layout.expand(samples);
    deleted.setId(id(DELETED));
    deleted.setVisible(false);
    protocol.setId(id(PROTOCOL));
    protocol.setVisible(false);
    fractionationType.setId(id(FRACTIONATION_TYPE));
    fractionationType.setVisible(false);
    date.setId(id(INSERT_TIME));
    samplesHeader.setId(id(styleName(TREATED_SAMPLES, HEADER)));
    samples.setId(id(TREATED_SAMPLES));
    sample = samples.addColumn(ts -> ts.getSample().getName(), SAMPLE).setKey(SAMPLE);
    container =
        samples.addColumn(ts -> ts.getContainer().getFullName(), CONTAINER).setKey(CONTAINER);
    sourceVolume =
        samples.addColumn(TreatedSample::getSourceVolume, SOURCE_VOLUME).setKey(SOURCE_VOLUME);
    sourceVolume.setVisible(false);
    solvent = samples.addColumn(TreatedSample::getSolvent, SOLVENT).setKey(SOLVENT);
    solvent.setVisible(false);
    solventVolume =
        samples.addColumn(TreatedSample::getSolventVolume, SOLVENT_VOLUME).setKey(SOLVENT_VOLUME);
    solventVolume.setVisible(false);
    name = samples.addColumn(TreatedSample::getName, NAME).setKey(NAME);
    name.setVisible(false);
    quantity = samples.addColumn(TreatedSample::getQuantity, QUANTITY).setKey(QUANTITY);
    quantity.setVisible(false);
    destinationContainer = samples.addColumn(
        ts -> ts.getDestinationContainer() != null ? ts.getDestinationContainer().getFullName()
            : "",
        DESTINATION_CONTAINER).setKey(DESTINATION_CONTAINER);
    destinationContainer.setVisible(false);
    number = samples.addColumn(TreatedSample::getNumber, NUMBER).setKey(NUMBER);
    number.setVisible(false);
    piInterval = samples.addColumn(TreatedSample::getPiInterval, PI_INTERVAL).setKey(PI_INTERVAL);
    piInterval.setVisible(false);
    comment =
        samples.addColumn(TreatedSample::getComment, COMMENT).setKey(COMMENT).setSortable(false);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    updateHeaderTitle();
    deleted.setText(getTranslation(TREATMENT_PREFIX + property(DELETED, true)));
    samplesHeader.setText(getTranslation(TREATMENT_PREFIX + TREATED_SAMPLES));
    sample.setHeader(getTranslation(TREATED_SAMPLE_PREFIX + SAMPLE));
    container.setHeader(getTranslation(TREATED_SAMPLE_PREFIX + CONTAINER));
    sourceVolume.setHeader(getTranslation(TREATED_SAMPLE_PREFIX + SOURCE_VOLUME));
    solvent.setHeader(getTranslation(TREATED_SAMPLE_PREFIX + SOLVENT));
    solventVolume.setHeader(getTranslation(TREATED_SAMPLE_PREFIX + SOLVENT_VOLUME));
    name.setHeader(getTranslation(TREATED_SAMPLE_PREFIX + NAME));
    quantity.setHeader(getTranslation(TREATED_SAMPLE_PREFIX + QUANTITY));
    destinationContainer.setHeader(getTranslation(TREATED_SAMPLE_PREFIX + DESTINATION_CONTAINER));
    number.setHeader(getTranslation(TREATED_SAMPLE_PREFIX + NUMBER));
    piInterval.setHeader(getTranslation(TREATED_SAMPLE_PREFIX + PI_INTERVAL));
    comment.setHeader(getTranslation(TREATED_SAMPLE_PREFIX + COMMENT));
  }

  private void updateHeaderTitle() {
    setHeaderTitle(Optional.ofNullable(treatment)
        .map(tr -> getTranslation(TREATMENT_TYPE_PREFIX + tr.getType().name()))
        .orElse(getTranslation(MESSAGES_PREFIX + HEADER)));
  }

  public long getTreatmentId() {
    return treatment.getId();
  }

  /**
   * Sets dialog's treatment id.
   *
   * @param id
   *          treatment id
   */
  public void setTreatmentId(long id) {
    treatment = service.get(id).orElseThrow();
    updateHeaderTitle();
    if (treatment.getProtocol() != null) {
      protocol
          .setText(getTranslation(MESSAGES_PREFIX + PROTOCOL, treatment.getProtocol().getName()));
      protocol.setVisible(true);
    }
    if (treatment.getFractionationType() != null) {
      fractionationType.setText(getTranslation(MESSAGES_PREFIX + FRACTIONATION_TYPE,
          getTranslation(FRACTIONATION_TYPE_PREFIX + treatment.getFractionationType().name())));
      fractionationType.setVisible(true);
    }
    deleted.setVisible(treatment.isDeleted());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    date.setText(getTranslation(MESSAGES_PREFIX + INSERT_TIME,
        dateFormatter.format(treatment.getInsertTime())));
    samples.setItems(treatment.getTreatedSamples());
    sourceVolume.setVisible(treatment.getType() == DILUTION);
    solvent.setVisible(treatment.getType() == DILUTION || treatment.getType() == SOLUBILISATION);
    solventVolume
        .setVisible(treatment.getType() == DILUTION || treatment.getType() == SOLUBILISATION);
    name.setVisible(treatment.getType() == STANDARD_ADDITION);
    quantity.setVisible(treatment.getType() == STANDARD_ADDITION);
    destinationContainer
        .setVisible(treatment.getType() == TRANSFER || treatment.getType() == FRACTIONATION);
    number.setVisible(treatment.getType() == FRACTIONATION
        && treatment.getFractionationType() == FractionationType.MUDPIT);
    piInterval.setVisible(treatment.getType() == FRACTIONATION
        && treatment.getFractionationType() == FractionationType.PI);
  }
}
