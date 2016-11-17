package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;

import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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
  public static final Object[] ACQUISITIONS_COLUMNS = new Object[] { LIMS, NAME, ACQUISITION_FILE };
  @SuppressWarnings("unused")
  private static final Logger logger =
      LoggerFactory.getLogger(SubmissionAnalysesFormPresenter.class);
  private SubmissionAnalysesForm view;
  private BeanItem<Submission> item = new BeanItem<>(null, Submission.class);
  private List<MsAnalysis> analyses = new ArrayList<>();
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

  private void styles() {
    view.analysisPanels.forEach(panel -> panel.addStyleName(ANALYSIS));
    view.acquisitionsGrids.forEach(panel -> panel.addStyleName(ACQUISITIONS));
  }

  private LocalDate date(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
  }

  private void captions() {
    MessageResource resources = view.getResources();
    IntStream.range(0, analyses.size()).forEach(i -> {
      MsAnalysis analysis = analyses.get(i);
      Panel analysisPanel = view.analysisPanels.get(i);
      DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
      analysisPanel.setCaption(
          resources.message(ANALYSIS, formatter.format(date(analysis.getInsertTime()))));
    });
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    IntStream.range(0, analyses.size()).forEach(i -> {
      MsAnalysis analysis = analyses.get(i);
      BeanItemContainer<Acquisition> container = new BeanItemContainer<>(Acquisition.class);
      container.addAll(analysis.getAcquisitions());
      container.addNestedContainerProperty(LIMS);
      container.addNestedContainerProperty(NAME);
      container.sort(new Object[] { ACQUISITION_INDEX }, new boolean[] { true });
      Grid acquisitionsGrid = view.acquisitionsGrids.get(i);
      acquisitionsGrid.setContainerDataSource(container);
      acquisitionsGrid.setColumns(ACQUISITIONS_COLUMNS);
      for (Object propertyId : ACQUISITIONS_COLUMNS) {
        acquisitionsGrid.getColumn(propertyId)
            .setHeaderCaption(resources.message((String) propertyId));
      }
    });
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
    if (!(item instanceof BeanItem)) {
      throw new IllegalArgumentException("item must be null or a BeanItem containing a submission");
    }

    this.item = item != null ? (BeanItem<Submission>) item : new BeanItem<>(null, Submission.class);
    analyses = msAnalysisService.all(this.item.getBean());
    view.removeAllComponents();
    analyses.forEach(analysis -> {
      view.createAnalysisPanel();
    });
    styles();
    captions();
    prepareComponents();
  }
}
