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

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  public MsAnalysisDialog() {
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
        acquisitions.addColumn(ac -> ac.getNumberOfAcquisition(), NUMBER_OF_ACQUISITION)
            .setKey(NUMBER_OF_ACQUISITION).setFlexGrow(0);
    sampleListName = acquisitions.addColumn(ac -> ac.getSampleListName(), SAMPLE_LIST_NAME)
        .setKey(SAMPLE_LIST_NAME);
    acquisitionFile = acquisitions.addColumn(ac -> ac.getAcquisitionFile(), ACQUISITION_FILE)
        .setKey(ACQUISITION_FILE);
    position =
        acquisitions.addColumn(ac -> ac.getPosition(), POSITION).setKey(POSITION).setFlexGrow(0);
    comment =
        acquisitions.addColumn(ac -> ac.getComment(), COMMENT).setKey(COMMENT).setSortable(false);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    localeChanged();
  }

  private void localeChanged() {
    final AppResources resource = new AppResources(MsAnalysisDialog.class, getLocale());
    final AppResources treatmentResource = new AppResources(MsAnalysis.class, getLocale());
    final AppResources treatedSampleResource = new AppResources(Acquisition.class, getLocale());
    setHeaderTitle(resource.message(HEADER));
    deleted.setText(treatmentResource.message(property(DELETED, true)));
    acquisitionsHeader.setText(treatmentResource.message(ACQUISITIONS));
    sample.setHeader(treatedSampleResource.message(SAMPLE));
    container.setHeader(treatedSampleResource.message(CONTAINER));
    numberOfAcquisition.setHeader(treatedSampleResource.message(NUMBER_OF_ACQUISITION));
    sampleListName.setHeader(treatedSampleResource.message(SAMPLE_LIST_NAME));
    acquisitionFile.setHeader(treatedSampleResource.message(ACQUISITION_FILE));
    position.setHeader(treatedSampleResource.message(POSITION));
    comment.setHeader(treatedSampleResource.message(COMMENT));
    instrument.setText(resource.message(MASS_DETECTION_INSTRUMENT,
        msAnalysis.getMassDetectionInstrument().getLabel(getLocale())));
    source.setText(resource.message(SOURCE, msAnalysis.getSource().getLabel(getLocale())));
    deleted.setVisible(msAnalysis.isDeleted());
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    date.setText(resource.message(INSERT_TIME, dateFormatter.format(msAnalysis.getInsertTime())));
    acquisitions.setItems(msAnalysis.getAcquisitions());
  }

  public MsAnalysis getMsAnalysis() {
    return msAnalysis;
  }

  /**
   * Sets dialog's MS analysis.
   *
   * @param msAnalysis
   *          MS analysis
   */
  public void setMsAnalysis(MsAnalysis msAnalysis) {
    Objects.requireNonNull(msAnalysis);
    this.msAnalysis = msAnalysis;
    localeChanged();
  }
}
