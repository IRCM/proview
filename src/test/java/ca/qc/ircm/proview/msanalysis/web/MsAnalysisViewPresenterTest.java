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
import static ca.qc.ircm.proview.msanalysis.AcquisitionProperties.SAMPLE;
import static ca.qc.ircm.proview.msanalysis.AcquisitionProperties.SAMPLE_LIST_NAME;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.MASS_DETECTION_INSTRUMENT;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.SOURCE;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.ACQUISITIONS;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.ACQUISITIONS_PANEL;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.DELETED;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.EXPLANATION;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.HEADER;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.INVALID_MS_ANALYSIS;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.MS_ANALYSIS_PANEL;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.TITLE;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisRepository;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class MsAnalysisViewPresenterTest {
  @Inject
  private MsAnalysisViewPresenter presenter;
  @Mock
  private MsAnalysisView view;
  @MockBean
  private MsAnalysisService msAnalysisService;
  @Captor
  private ArgumentCaptor<MsAnalysis> msAnalysisCaptor;
  @Inject
  private MsAnalysisRepository repository;
  @Value("${spring.application.name}")
  private String applicationName;
  private MsAnalysisViewDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(MsAnalysisView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    design = new MsAnalysisViewDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.header.getStyleName().contains(HEADER));
    assertTrue(design.header.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.deleted.getStyleName().contains(DELETED));
    assertTrue(design.deleted.getStyleName().contains(ValoTheme.LABEL_FAILURE));
    assertTrue(design.msAnalysisPanel.getStyleName().contains(MS_ANALYSIS_PANEL));
    assertTrue(design.massDetectionInstrument.getStyleName().contains(MASS_DETECTION_INSTRUMENT));
    assertTrue(design.source.getStyleName().contains(SOURCE));
    assertTrue(design.acquisitionsPanel.getStyleName().contains(ACQUISITIONS_PANEL));
    assertTrue(design.acquisitions.getStyleName().contains(ACQUISITIONS));
    assertTrue(design.acquisitions.getStyleName().contains(COMPONENTS));
    assertTrue(design.explanationPanel.getStyleName().contains(EXPLANATION_PANEL));
    assertTrue(design.explanation.getStyleName().contains(EXPLANATION));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
    assertEquals(resources.message(DELETED), design.deleted.getValue());
    assertEquals(resources.message(MS_ANALYSIS_PANEL), design.msAnalysisPanel.getCaption());
    assertEquals(resources.message(MASS_DETECTION_INSTRUMENT),
        design.massDetectionInstrument.getCaption());
    assertEquals(resources.message(SOURCE), design.source.getCaption());
    assertEquals(resources.message(ACQUISITIONS_PANEL), design.acquisitionsPanel.getCaption());
    assertEquals(resources.message(EXPLANATION_PANEL), design.explanationPanel.getCaption());
  }

  @Test
  public void acquisitions() {
    presenter.init(view);
    presenter.enter("");

    assertEquals(5, design.acquisitions.getColumns().size());
    assertEquals(SAMPLE, design.acquisitions.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE), design.acquisitions.getColumn(SAMPLE).getCaption());
    assertFalse(design.acquisitions.getColumn(SAMPLE).isSortable());
    assertEquals("sample_t", design.acquisitions.getColumn(SAMPLE).getValueProvider()
        .apply(acquisition(acquisition -> acquisition.getSample().setName("sample_t"))));
    assertEquals(CONTAINER, design.acquisitions.getColumns().get(1).getId());
    assertEquals(resources.message(CONTAINER),
        design.acquisitions.getColumn(CONTAINER).getCaption());
    assertFalse(design.acquisitions.getColumn(CONTAINER).isSortable());
    assertEquals("tube_t", design.acquisitions.getColumn(CONTAINER).getValueProvider()
        .apply(acquisition(acquisition -> ((Tube) acquisition.getContainer()).setName("tube_t"))));
    assertEquals(SAMPLE_LIST_NAME, design.acquisitions.getColumns().get(2).getId());
    assertEquals(resources.message(SAMPLE_LIST_NAME),
        design.acquisitions.getColumn(SAMPLE_LIST_NAME).getCaption());
    assertFalse(design.acquisitions.getColumn(SAMPLE_LIST_NAME).isSortable());
    assertEquals("samplelist_t", design.acquisitions.getColumn(SAMPLE_LIST_NAME).getValueProvider()
        .apply(acquisition(acquisition -> acquisition.setSampleListName("samplelist_t"))));
    assertEquals(ACQUISITION_FILE, design.acquisitions.getColumns().get(3).getId());
    assertEquals(resources.message(ACQUISITION_FILE),
        design.acquisitions.getColumn(ACQUISITION_FILE).getCaption());
    assertFalse(design.acquisitions.getColumn(ACQUISITION_FILE).isSortable());
    assertEquals("acquisitionfile_t",
        design.acquisitions.getColumn(ACQUISITION_FILE).getValueProvider().apply(
            acquisition(acquisition -> acquisition.setAcquisitionFile("acquisitionfile_t"))));
    assertEquals(COMMENT, design.acquisitions.getColumns().get(4).getId());
    assertEquals(resources.message(COMMENT), design.acquisitions.getColumn(COMMENT).getCaption());
    assertFalse(design.acquisitions.getColumn(COMMENT).isSortable());
    assertEquals("comment_t", design.acquisitions.getColumn(COMMENT).getValueProvider()
        .apply(acquisition(acquisition -> acquisition.setComment("comment_t"))));
  }

  private Acquisition acquisition(Consumer<Acquisition> initializer) {
    Acquisition acquisition = new Acquisition();
    acquisition.setSample(new SubmissionSample());
    acquisition.setContainer(new Tube());
    acquisition.getContainer().setSample(acquisition.getSample());
    initializer.accept(acquisition);
    return acquisition;
  }

  @Test
  public void enter() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(design.deleted.isVisible());
    assertEquals("", design.massDetectionInstrument.getValue());
    assertEquals("", design.source.getValue());
    assertFalse(design.explanationPanel.isVisible());
    List<Acquisition> acquisitions = items(design.acquisitions);
    assertTrue(acquisitions.isEmpty());
  }

  @Test
  public void enter_MsAnalysis() {
    MsAnalysis msAnalysis = repository.findById(14L).orElse(null);
    when(msAnalysisService.get(any(Long.class))).thenReturn(msAnalysis);
    presenter.init(view);
    presenter.enter("14");

    verify(msAnalysisService).get(14L);
    assertFalse(design.deleted.isVisible());
    assertEquals(msAnalysis.getMassDetectionInstrument().getLabel(view.getLocale()),
        design.massDetectionInstrument.getValue());
    assertEquals(msAnalysis.getSource().getLabel(view.getLocale()), design.source.getValue());
    assertFalse(design.explanationPanel.isVisible());
    List<Acquisition> acquisitions = items(design.acquisitions);
    assertEquals(msAnalysis.getAcquisitions().size(), acquisitions.size());
    for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
      assertTrue(acquisitions.contains(acquisition));
    }
  }

  @Test
  public void enter_MsAnalysisMultipleAcquisitionPerSample() {
    MsAnalysis msAnalysis = repository.findById(14L).orElse(null);
    msAnalysis.getAcquisitions().get(0).setNumberOfAcquisition(2);
    Acquisition newAcquisition = new Acquisition();
    newAcquisition.setMsAnalysis(msAnalysis);
    newAcquisition.setSample(msAnalysis.getAcquisitions().get(0).getSample());
    newAcquisition.setContainer(msAnalysis.getAcquisitions().get(0).getContainer());
    newAcquisition.setSampleListName(msAnalysis.getAcquisitions().get(0).getSampleListName());
    newAcquisition.setAcquisitionFile(msAnalysis.getAcquisitions().get(0).getAcquisitionFile() + 1);
    newAcquisition.setComment(msAnalysis.getAcquisitions().get(0).getComment());
    newAcquisition.setNumberOfAcquisition(2);
    newAcquisition.setPosition(2);
    msAnalysis.getAcquisitions().add(newAcquisition);
    when(msAnalysisService.get(any(Long.class))).thenReturn(msAnalysis);
    presenter.init(view);
    presenter.enter("14");

    verify(msAnalysisService).get(14L);
    assertFalse(design.deleted.isVisible());
    assertEquals(msAnalysis.getMassDetectionInstrument().getLabel(view.getLocale()),
        design.massDetectionInstrument.getValue());
    assertEquals(msAnalysis.getSource().getLabel(view.getLocale()), design.source.getValue());
    assertFalse(design.explanationPanel.isVisible());
    List<Acquisition> acquisitions = items(design.acquisitions);
    assertEquals(msAnalysis.getAcquisitions().size(), acquisitions.size());
    for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
      assertTrue(acquisitions.contains(acquisition));
    }
  }

  @Test
  public void enter_MsAnalysisDeleted() {
    MsAnalysis msAnalysis = repository.findById(14L).orElse(null);
    msAnalysis.setDeleted(true);
    msAnalysis.setDeletionExplanation("test on multiple\nlines");
    when(msAnalysisService.get(any(Long.class))).thenReturn(msAnalysis);
    presenter.init(view);
    presenter.enter("14");

    verify(msAnalysisService).get(14L);
    assertTrue(design.deleted.isVisible());
    assertEquals(msAnalysis.getMassDetectionInstrument().getLabel(view.getLocale()),
        design.massDetectionInstrument.getValue());
    assertEquals(msAnalysis.getSource().getLabel(view.getLocale()), design.source.getValue());
    assertTrue(design.explanationPanel.isVisible());
    assertEquals(msAnalysis.getDeletionExplanation(), design.explanation.getValue());
    List<Acquisition> acquisitions = items(design.acquisitions);
    assertEquals(msAnalysis.getAcquisitions().size(), acquisitions.size());
    for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
      assertTrue(acquisitions.contains(acquisition));
    }
  }

  @Test
  public void enter_MsAnalysisNotId() {
    presenter.init(view);
    presenter.enter("a");

    verify(view).showWarning(resources.message(INVALID_MS_ANALYSIS));
    List<Acquisition> acquisitions = items(design.acquisitions);
    assertTrue(acquisitions.isEmpty());
  }

  @Test
  public void enter_MsAnalysisIdNotExists() {
    presenter.init(view);
    presenter.enter("14");

    verify(msAnalysisService).get(14L);
    verify(view).showWarning(resources.message(INVALID_MS_ANALYSIS));
    List<Acquisition> acquisitions = items(design.acquisitions);
    assertTrue(acquisitions.isEmpty());
  }
}
