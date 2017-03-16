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

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;

import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Grid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

/**
 * Submission results form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionAnalysesFormPresenter {
  public static final String ANALYSIS = msAnalysis.getMetadata().getName();
  public static final String ACQUISITIONS = msAnalysis.acquisitions.getMetadata().getName();
  public static final String SAMPLE = acquisition.sample.getMetadata().getName();
  public static final String LIMS = SAMPLE + "." + acquisition.sample.lims.getMetadata().getName();
  public static final String NAME = SAMPLE + "." + acquisition.sample.name.getMetadata().getName();
  public static final String ACQUISITION_FILE = acquisition.acquisitionFile.getMetadata().getName();
  public static final String ACQUISITION_INDEX = acquisition.listIndex.getMetadata().getName();
  private static final Object[] ACQUISITIONS_COLUMNS = new Object[] { NAME, ACQUISITION_FILE };
  @SuppressWarnings("unused")
  private static final Logger logger =
      LoggerFactory.getLogger(SubmissionAnalysesFormPresenter.class);
  private SubmissionAnalysesForm view;
  private BeanItem<Submission> item = new BeanItem<>(null, Submission.class);
  @Inject
  private MsAnalysisService msAnalysisService;

  protected SubmissionAnalysesFormPresenter() {
  }

  protected SubmissionAnalysesFormPresenter(MsAnalysisService msAnalysisService) {
    this.msAnalysisService = msAnalysisService;
  }

  public void init(SubmissionAnalysesForm view) {
    this.view = view;
  }

  private LocalDate date(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
  }

  private void createAnalysisPanel(MsAnalysis analysis) {
    final MessageResource resources = view.getResources();
    Panel panel = new Panel();
    view.addComponent(panel);
    VerticalLayout layout = new VerticalLayout();
    panel.setContent(layout);
    panel.addStyleName(ANALYSIS);
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    panel.setCaption(resources.message(ANALYSIS, formatter.format(date(analysis.getInsertTime()))));
    BeanItemContainer<Acquisition> container = new BeanItemContainer<>(Acquisition.class);
    container.addAll(analysis.getAcquisitions());
    container.addNestedContainerProperty(NAME);
    container.sort(new Object[] { ACQUISITION_INDEX }, new boolean[] { true });
    Grid grid = new Grid();
    grid.setWidth("100%");
    grid.addStyleName(ACQUISITIONS);
    grid.setContainerDataSource(container);
    grid.setColumns(ACQUISITIONS_COLUMNS);
    for (Object propertyId : ACQUISITIONS_COLUMNS) {
      grid.getColumn(propertyId).setHeaderCaption(resources.message((String) propertyId));
    }
    layout.addComponent(grid);
  }

  public Item getItemDataSource() {
    return item;
  }

  /**
   * Sets submission as an item.
   *
   * @param item
   *          submission as an item
   */
  @SuppressWarnings("unchecked")
  public void setItemDataSource(Item item) {
    if (item != null && !(item instanceof BeanItem)) {
      throw new IllegalArgumentException("item must be null or a BeanItem containing a submission");
    }

    this.item = item != null ? (BeanItem<Submission>) item : new BeanItem<>(null, Submission.class);
    List<MsAnalysis> analyses = msAnalysisService.all(this.item.getBean());
    view.removeAllComponents();
    analyses.forEach(analysis -> {
      createAnalysisPanel(analysis);
    });
  }

  public static Object[] getAcquisitionsColumns() {
    return ACQUISITIONS_COLUMNS.clone();
  }
}
