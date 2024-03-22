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

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.treatment.FractionationType;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.treatment.Treatment;
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
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  public TreatmentDialog() {
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
        samples.addColumn(ts -> ts.getSourceVolume(), SOURCE_VOLUME).setKey(SOURCE_VOLUME);
    sourceVolume.setVisible(false);
    solvent = samples.addColumn(ts -> ts.getSolvent(), SOLVENT).setKey(SOLVENT);
    solvent.setVisible(false);
    solventVolume =
        samples.addColumn(ts -> ts.getSolventVolume(), SOLVENT_VOLUME).setKey(SOLVENT_VOLUME);
    solventVolume.setVisible(false);
    name = samples.addColumn(ts -> ts.getName(), NAME).setKey(NAME);
    name.setVisible(false);
    quantity = samples.addColumn(ts -> ts.getQuantity(), QUANTITY).setKey(QUANTITY);
    quantity.setVisible(false);
    destinationContainer = samples.addColumn(
        ts -> ts.getDestinationContainer() != null ? ts.getDestinationContainer().getFullName()
            : "",
        DESTINATION_CONTAINER).setKey(DESTINATION_CONTAINER);
    destinationContainer.setVisible(false);
    number = samples.addColumn(ts -> ts.getNumber(), NUMBER).setKey(NUMBER);
    number.setVisible(false);
    piInterval = samples.addColumn(ts -> ts.getPiInterval(), PI_INTERVAL).setKey(PI_INTERVAL);
    piInterval.setVisible(false);
    comment = samples.addColumn(ts -> ts.getComment(), COMMENT).setKey(COMMENT).setSortable(false);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final AppResources resource = new AppResources(TreatmentDialog.class, getLocale());
    final AppResources treatmentResource = new AppResources(Treatment.class, getLocale());
    final AppResources treatedSampleResource = new AppResources(TreatedSample.class, getLocale());
    updateHeaderTitle();
    deleted.setText(treatmentResource.message(property(DELETED, true)));
    samplesHeader.setText(treatmentResource.message(TREATED_SAMPLES));
    sample.setHeader(treatedSampleResource.message(SAMPLE));
    container.setHeader(treatedSampleResource.message(CONTAINER));
    sourceVolume.setHeader(treatedSampleResource.message(SOURCE_VOLUME));
    solvent.setHeader(treatedSampleResource.message(SOLVENT));
    solventVolume.setHeader(treatedSampleResource.message(SOLVENT_VOLUME));
    name.setHeader(treatedSampleResource.message(NAME));
    quantity.setHeader(treatedSampleResource.message(QUANTITY));
    destinationContainer.setHeader(treatedSampleResource.message(DESTINATION_CONTAINER));
    number.setHeader(treatedSampleResource.message(NUMBER));
    piInterval.setHeader(treatedSampleResource.message(PI_INTERVAL));
    comment.setHeader(treatedSampleResource.message(COMMENT));
  }

  private void updateHeaderTitle() {
    final AppResources resource = new AppResources(TreatmentDialog.class, getLocale());
    setHeaderTitle(Optional.ofNullable(treatment).map(tr -> tr.getType().getLabel(getLocale()))
        .orElse(resource.message(HEADER)));
  }

  public Treatment getTreatment() {
    return treatment;
  }

  /**
   * Sets dialog's treatment.
   *
   * @param treatment
   *          treatment
   */
  public void setTreatment(Treatment treatment) {
    Objects.requireNonNull(treatment);
    this.treatment = treatment;
    AppResources resource = new AppResources(TreatmentDialog.class, getLocale());
    updateHeaderTitle();
    if (treatment.getProtocol() != null) {
      protocol.setText(resource.message(PROTOCOL, treatment.getProtocol().getName()));
      protocol.setVisible(true);
    }
    if (treatment.getFractionationType() != null) {
      fractionationType.setText(resource.message(FRACTIONATION_TYPE,
          treatment.getFractionationType().getLabel(getLocale())));
      fractionationType.setVisible(true);
    }
    deleted.setVisible(treatment.isDeleted());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    date.setText(resource.message(INSERT_TIME, dateFormatter.format(treatment.getInsertTime())));
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
