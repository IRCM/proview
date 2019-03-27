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

import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
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
import static ca.qc.ircm.proview.treatment.TreatmentProperties.FRACTIONATION_TYPE;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.PROTOCOL;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.TREATED_SAMPLES;
import static ca.qc.ircm.proview.treatment.web.TreatmentViewPresenter.DELETED;
import static ca.qc.ircm.proview.treatment.web.TreatmentViewPresenter.EXPLANATION;
import static ca.qc.ircm.proview.treatment.web.TreatmentViewPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.treatment.web.TreatmentViewPresenter.FRACTIONATION_TYPE_PANEL;
import static ca.qc.ircm.proview.treatment.web.TreatmentViewPresenter.HEADER;
import static ca.qc.ircm.proview.treatment.web.TreatmentViewPresenter.INVALID_TREATMENT;
import static ca.qc.ircm.proview.treatment.web.TreatmentViewPresenter.PROTOCOL_PANEL;
import static ca.qc.ircm.proview.treatment.web.TreatmentViewPresenter.TITLE;
import static ca.qc.ircm.proview.treatment.web.TreatmentViewPresenter.TREATED_SAMPLES_PANEL;
import static ca.qc.ircm.proview.web.WebConstants.BANNED;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.FractionationType;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.TreatmentRepository;
import ca.qc.ircm.proview.treatment.TreatmentService;
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
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class TreatmentViewPresenterTest {
  @Inject
  private TreatmentViewPresenter presenter;
  @Inject
  private TreatmentRepository repository;
  @MockBean
  private TreatmentService treatmentService;
  @Mock
  private TreatmentView view;
  @Value("${spring.application.name}")
  private String applicationName;
  private TreatmentViewDesign design = new TreatmentViewDesign();
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(TreatmentView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    design = new TreatmentViewDesign();
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
    assertTrue(design.protocolPanel.getStyleName().contains(PROTOCOL_PANEL));
    assertTrue(design.protocol.getStyleName().contains(PROTOCOL));
    assertTrue(design.fractionationTypePanel.getStyleName().contains(FRACTIONATION_TYPE_PANEL));
    assertTrue(design.fractionationType.getStyleName().contains(FRACTIONATION_TYPE));
    assertTrue(design.treatedSamplesPanel.getStyleName().contains(TREATED_SAMPLES_PANEL));
    assertTrue(design.treatedSamples.getStyleName().contains(TREATED_SAMPLES));
    assertTrue(design.treatedSamples.getStyleName().contains(COMPONENTS));
    assertTrue(design.explanationPanel.getStyleName().contains(EXPLANATION_PANEL));
    assertTrue(design.explanation.getStyleName().contains(EXPLANATION));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
    assertEquals(resources.message(DELETED), design.deleted.getValue());
    assertEquals(resources.message(PROTOCOL_PANEL), design.protocolPanel.getCaption());
    assertEquals(resources.message(FRACTIONATION_TYPE_PANEL),
        design.fractionationTypePanel.getCaption());
    assertEquals(resources.message(TREATED_SAMPLES_PANEL), design.treatedSamplesPanel.getCaption());
    assertEquals(resources.message(EXPLANATION_PANEL), design.explanationPanel.getCaption());
  }

  @Test
  public void treatedSamples() {
    presenter.init(view);
    presenter.enter("");

    assertEquals(11, design.treatedSamples.getColumns().size());
    assertEquals(SAMPLE, design.treatedSamples.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE), design.treatedSamples.getColumn(SAMPLE).getCaption());
    assertEquals("sample_t", design.treatedSamples.getColumn(SAMPLE).getValueProvider()
        .apply(ts(ts -> ts.getSample().setName("sample_t"))));
    assertEquals(CONTAINER, design.treatedSamples.getColumns().get(1).getId());
    assertEquals(resources.message(CONTAINER),
        design.treatedSamples.getColumn(CONTAINER).getCaption());
    assertEquals("tube_t", design.treatedSamples.getColumn(CONTAINER).getValueProvider()
        .apply(ts(ts -> ((Tube) ts.getContainer()).setName("tube_t"))));
    assertEquals("", design.treatedSamples.getColumn(CONTAINER).getStyleGenerator()
        .apply(ts(ts -> ts.getContainer().setBanned(false))));
    assertEquals(BANNED, design.treatedSamples.getColumn(CONTAINER).getStyleGenerator()
        .apply(ts(ts -> ts.getContainer().setBanned(true))));
    assertEquals(SOURCE_VOLUME, design.treatedSamples.getColumns().get(2).getId());
    assertEquals(resources.message(SOURCE_VOLUME),
        design.treatedSamples.getColumn(SOURCE_VOLUME).getCaption());
    assertEquals(12.0, design.treatedSamples.getColumn(SOURCE_VOLUME).getValueProvider()
        .apply(ts(ts -> ts.setSourceVolume(12.0))));
    assertEquals(SOLVENT, design.treatedSamples.getColumns().get(3).getId());
    assertEquals(resources.message(SOLVENT), design.treatedSamples.getColumn(SOLVENT).getCaption());
    assertEquals("CH3OH", design.treatedSamples.getColumn(SOLVENT).getValueProvider()
        .apply(ts(ts -> ts.setSolvent("CH3OH"))));
    assertEquals(SOLVENT_VOLUME, design.treatedSamples.getColumns().get(4).getId());
    assertEquals(resources.message(SOLVENT_VOLUME),
        design.treatedSamples.getColumn(SOLVENT_VOLUME).getCaption());
    assertEquals(13.0, design.treatedSamples.getColumn(SOLVENT_VOLUME).getValueProvider()
        .apply(ts(ts -> ts.setSolventVolume(13.0))));
    assertEquals(NAME, design.treatedSamples.getColumns().get(5).getId());
    assertEquals(resources.message(NAME), design.treatedSamples.getColumn(NAME).getCaption());
    assertEquals("std_t", design.treatedSamples.getColumn(NAME).getValueProvider()
        .apply(ts(ts -> ts.setName("std_t"))));
    assertEquals(QUANTITY, design.treatedSamples.getColumns().get(6).getId());
    assertEquals(resources.message(QUANTITY),
        design.treatedSamples.getColumn(QUANTITY).getCaption());
    assertEquals("8mg", design.treatedSamples.getColumn(QUANTITY).getValueProvider()
        .apply(ts(ts -> ts.setQuantity("8mg"))));
    assertEquals(DESTINATION_CONTAINER, design.treatedSamples.getColumns().get(7).getId());
    assertEquals(resources.message(DESTINATION_CONTAINER),
        design.treatedSamples.getColumn(DESTINATION_CONTAINER).getCaption());
    assertEquals("destube_t",
        design.treatedSamples.getColumn(DESTINATION_CONTAINER).getValueProvider()
            .apply(ts(ts -> ((Tube) ts.getDestinationContainer()).setName("destube_t"))));
    assertEquals("", design.treatedSamples.getColumn(DESTINATION_CONTAINER).getStyleGenerator()
        .apply(ts(ts -> ts.getDestinationContainer().setBanned(false))));
    assertEquals(BANNED, design.treatedSamples.getColumn(DESTINATION_CONTAINER).getStyleGenerator()
        .apply(ts(ts -> ts.getDestinationContainer().setBanned(true))));
    assertEquals(NUMBER, design.treatedSamples.getColumns().get(8).getId());
    assertEquals(resources.message(NUMBER), design.treatedSamples.getColumn(NUMBER).getCaption());
    assertEquals(3, design.treatedSamples.getColumn(NUMBER).getValueProvider()
        .apply(ts(ts -> ts.setNumber(3))));
    assertEquals(PI_INTERVAL, design.treatedSamples.getColumns().get(9).getId());
    assertEquals(resources.message(PI_INTERVAL),
        design.treatedSamples.getColumn(PI_INTERVAL).getCaption());
    assertEquals("7-10", design.treatedSamples.getColumn(PI_INTERVAL).getValueProvider()
        .apply(ts(ts -> ts.setPiInterval("7-10"))));
    assertEquals(COMMENT, design.treatedSamples.getColumns().get(10).getId());
    assertEquals(resources.message(COMMENT), design.treatedSamples.getColumn(COMMENT).getCaption());
    assertEquals("comment_t", design.treatedSamples.getColumn(COMMENT).getValueProvider()
        .apply(ts(ts -> ts.setComment("comment_t"))));
    assertFalse(design.treatedSamples.getColumn(COMMENT).isSortable());
  }

  private TreatedSample ts(Consumer<TreatedSample> initializer) {
    TreatedSample ts = new TreatedSample();
    ts.setSample(new SubmissionSample());
    ts.setContainer(new Tube());
    ts.getContainer().setSample(ts.getSample());
    ts.setDestinationContainer(new Tube());
    ts.getDestinationContainer().setSample(ts.getSample());
    initializer.accept(ts);
    return ts;
  }

  @Test
  public void enter() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(design.deleted.isVisible());
    assertFalse(design.protocolPanel.isVisible());
    assertFalse(design.fractionationTypePanel.isVisible());
    assertFalse(design.treatedSamples.getColumn(SAMPLE).isHidden());
    assertFalse(design.treatedSamples.getColumn(CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOURCE_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(NAME).isHidden());
    assertTrue(design.treatedSamples.getColumn(QUANTITY).isHidden());
    assertTrue(design.treatedSamples.getColumn(DESTINATION_CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(NUMBER).isHidden());
    assertTrue(design.treatedSamples.getColumn(PI_INTERVAL).isHidden());
    assertFalse(design.treatedSamples.getColumn(COMMENT).isHidden());
    assertFalse(design.explanationPanel.isVisible());
    List<TreatedSample> tss = items(design.treatedSamples);
    assertTrue(tss.isEmpty());
  }

  @Test
  public void enter_Digestion() {
    Treatment treatment = repository.findOne(6L);
    when(treatmentService.get(any())).thenReturn(treatment);
    presenter.init(view);
    presenter.enter("6");

    verify(treatmentService).get(6L);
    assertFalse(design.deleted.isVisible());
    assertTrue(design.protocolPanel.isVisible());
    assertEquals(treatment.getProtocol().getName(), design.protocol.getValue());
    assertFalse(design.fractionationTypePanel.isVisible());
    assertFalse(design.treatedSamples.getColumn(SAMPLE).isHidden());
    assertFalse(design.treatedSamples.getColumn(CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOURCE_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(NAME).isHidden());
    assertTrue(design.treatedSamples.getColumn(QUANTITY).isHidden());
    assertTrue(design.treatedSamples.getColumn(DESTINATION_CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(NUMBER).isHidden());
    assertTrue(design.treatedSamples.getColumn(PI_INTERVAL).isHidden());
    assertFalse(design.treatedSamples.getColumn(COMMENT).isHidden());
    assertFalse(design.explanationPanel.isVisible());
    List<TreatedSample> tss = items(design.treatedSamples);
    assertEquals(treatment.getTreatedSamples().size(), tss.size());
    for (TreatedSample ts : treatment.getTreatedSamples()) {
      assertTrue(tss.contains(ts));
    }
  }

  @Test
  public void enter_DigestionDeleted() {
    Treatment treatment = repository.findOne(6L);
    treatment.setDeleted(true);
    treatment.setDeletionExplanation("Multiple line\nTest");
    when(treatmentService.get(any())).thenReturn(treatment);
    presenter.init(view);
    presenter.enter("6");

    verify(treatmentService).get(6L);
    assertTrue(design.deleted.isVisible());
    assertTrue(design.protocolPanel.isVisible());
    assertEquals(treatment.getProtocol().getName(), design.protocol.getValue());
    assertFalse(design.fractionationTypePanel.isVisible());
    assertFalse(design.treatedSamples.getColumn(SAMPLE).isHidden());
    assertFalse(design.treatedSamples.getColumn(CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOURCE_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(NAME).isHidden());
    assertTrue(design.treatedSamples.getColumn(QUANTITY).isHidden());
    assertTrue(design.treatedSamples.getColumn(DESTINATION_CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(NUMBER).isHidden());
    assertTrue(design.treatedSamples.getColumn(PI_INTERVAL).isHidden());
    assertFalse(design.treatedSamples.getColumn(COMMENT).isHidden());
    assertTrue(design.explanationPanel.isVisible());
    assertEquals(treatment.getDeletionExplanation(), design.explanation.getValue());
  }

  @Test
  public void enter_Dilution() {
    Treatment treatment = repository.findOne(4L);
    when(treatmentService.get(any())).thenReturn(treatment);
    presenter.init(view);
    presenter.enter("4");

    verify(treatmentService).get(4L);
    assertFalse(design.deleted.isVisible());
    assertFalse(design.protocolPanel.isVisible());
    assertFalse(design.fractionationTypePanel.isVisible());
    assertFalse(design.treatedSamples.getColumn(SAMPLE).isHidden());
    assertFalse(design.treatedSamples.getColumn(CONTAINER).isHidden());
    assertFalse(design.treatedSamples.getColumn(SOURCE_VOLUME).isHidden());
    assertFalse(design.treatedSamples.getColumn(SOLVENT).isHidden());
    assertFalse(design.treatedSamples.getColumn(SOLVENT_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(NAME).isHidden());
    assertTrue(design.treatedSamples.getColumn(QUANTITY).isHidden());
    assertTrue(design.treatedSamples.getColumn(DESTINATION_CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(NUMBER).isHidden());
    assertTrue(design.treatedSamples.getColumn(PI_INTERVAL).isHidden());
    assertFalse(design.treatedSamples.getColumn(COMMENT).isHidden());
    assertFalse(design.explanationPanel.isVisible());
    List<TreatedSample> tss = items(design.treatedSamples);
    assertEquals(treatment.getTreatedSamples().size(), tss.size());
    for (TreatedSample ts : treatment.getTreatedSamples()) {
      assertTrue(tss.contains(ts));
    }
  }

  @Test
  public void enter_Enrichment() {
    Treatment treatment = repository.findOne(7L);
    when(treatmentService.get(any())).thenReturn(treatment);
    presenter.init(view);
    presenter.enter("7");

    verify(treatmentService).get(7L);
    assertFalse(design.deleted.isVisible());
    assertTrue(design.protocolPanel.isVisible());
    assertEquals(treatment.getProtocol().getName(), design.protocol.getValue());
    assertFalse(design.fractionationTypePanel.isVisible());
    assertFalse(design.treatedSamples.getColumn(SAMPLE).isHidden());
    assertFalse(design.treatedSamples.getColumn(CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOURCE_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(NAME).isHidden());
    assertTrue(design.treatedSamples.getColumn(QUANTITY).isHidden());
    assertTrue(design.treatedSamples.getColumn(DESTINATION_CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(NUMBER).isHidden());
    assertTrue(design.treatedSamples.getColumn(PI_INTERVAL).isHidden());
    assertFalse(design.treatedSamples.getColumn(COMMENT).isHidden());
    assertFalse(design.explanationPanel.isVisible());
    List<TreatedSample> tss = items(design.treatedSamples);
    assertEquals(treatment.getTreatedSamples().size(), tss.size());
    for (TreatedSample ts : treatment.getTreatedSamples()) {
      assertTrue(tss.contains(ts));
    }
  }

  @Test
  public void enter_Fractionation_Mudpit() {
    Treatment treatment = repository.findOne(2L);
    when(treatmentService.get(any())).thenReturn(treatment);
    presenter.init(view);
    presenter.enter("2");

    verify(treatmentService).get(2L);
    assertFalse(design.deleted.isVisible());
    assertFalse(design.protocolPanel.isVisible());
    assertTrue(design.fractionationTypePanel.isVisible());
    assertEquals(treatment.getFractionationType().getLabel(locale),
        design.fractionationType.getValue());
    assertFalse(design.treatedSamples.getColumn(SAMPLE).isHidden());
    assertFalse(design.treatedSamples.getColumn(CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOURCE_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(NAME).isHidden());
    assertTrue(design.treatedSamples.getColumn(QUANTITY).isHidden());
    assertFalse(design.treatedSamples.getColumn(DESTINATION_CONTAINER).isHidden());
    assertFalse(design.treatedSamples.getColumn(NUMBER).isHidden());
    assertTrue(design.treatedSamples.getColumn(PI_INTERVAL).isHidden());
    assertFalse(design.treatedSamples.getColumn(COMMENT).isHidden());
    assertFalse(design.explanationPanel.isVisible());
    List<TreatedSample> tss = items(design.treatedSamples);
    assertEquals(treatment.getTreatedSamples().size(), tss.size());
    for (TreatedSample ts : treatment.getTreatedSamples()) {
      assertTrue(tss.contains(ts));
    }
  }

  @Test
  public void enter_Fractionation_Pi() {
    Treatment treatment = repository.findOne(2L);
    treatment.setFractionationType(FractionationType.PI);
    when(treatmentService.get(any())).thenReturn(treatment);
    presenter.init(view);
    presenter.enter("2");

    verify(treatmentService).get(2L);
    assertFalse(design.deleted.isVisible());
    assertFalse(design.protocolPanel.isVisible());
    assertTrue(design.fractionationTypePanel.isVisible());
    assertEquals(treatment.getFractionationType().getLabel(locale),
        design.fractionationType.getValue());
    assertFalse(design.treatedSamples.getColumn(SAMPLE).isHidden());
    assertFalse(design.treatedSamples.getColumn(CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOURCE_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(NAME).isHidden());
    assertTrue(design.treatedSamples.getColumn(QUANTITY).isHidden());
    assertFalse(design.treatedSamples.getColumn(DESTINATION_CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(NUMBER).isHidden());
    assertFalse(design.treatedSamples.getColumn(PI_INTERVAL).isHidden());
    assertFalse(design.treatedSamples.getColumn(COMMENT).isHidden());
    assertFalse(design.explanationPanel.isVisible());
    List<TreatedSample> tss = items(design.treatedSamples);
    assertEquals(treatment.getTreatedSamples().size(), tss.size());
    for (TreatedSample ts : treatment.getTreatedSamples()) {
      assertTrue(tss.contains(ts));
    }
  }

  @Test
  public void enter_Solubilisation() {
    Treatment treatment = repository.findOne(1L);
    when(treatmentService.get(any())).thenReturn(treatment);
    presenter.init(view);
    presenter.enter("1");

    verify(treatmentService).get(1L);
    assertFalse(design.deleted.isVisible());
    assertFalse(design.protocolPanel.isVisible());
    assertFalse(design.fractionationTypePanel.isVisible());
    assertFalse(design.treatedSamples.getColumn(SAMPLE).isHidden());
    assertFalse(design.treatedSamples.getColumn(CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOURCE_VOLUME).isHidden());
    assertFalse(design.treatedSamples.getColumn(SOLVENT).isHidden());
    assertFalse(design.treatedSamples.getColumn(SOLVENT_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(NAME).isHidden());
    assertTrue(design.treatedSamples.getColumn(QUANTITY).isHidden());
    assertTrue(design.treatedSamples.getColumn(DESTINATION_CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(NUMBER).isHidden());
    assertTrue(design.treatedSamples.getColumn(PI_INTERVAL).isHidden());
    assertFalse(design.treatedSamples.getColumn(COMMENT).isHidden());
    assertFalse(design.explanationPanel.isVisible());
    List<TreatedSample> tss = items(design.treatedSamples);
    assertEquals(treatment.getTreatedSamples().size(), tss.size());
    for (TreatedSample ts : treatment.getTreatedSamples()) {
      assertTrue(tss.contains(ts));
    }
  }

  @Test
  public void enter_StandardAddition() {
    Treatment treatment = repository.findOne(5L);
    when(treatmentService.get(any())).thenReturn(treatment);
    presenter.init(view);
    presenter.enter("5");

    verify(treatmentService).get(5L);
    assertFalse(design.deleted.isVisible());
    assertFalse(design.protocolPanel.isVisible());
    assertFalse(design.fractionationTypePanel.isVisible());
    assertFalse(design.treatedSamples.getColumn(SAMPLE).isHidden());
    assertFalse(design.treatedSamples.getColumn(CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOURCE_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT_VOLUME).isHidden());
    assertFalse(design.treatedSamples.getColumn(NAME).isHidden());
    assertFalse(design.treatedSamples.getColumn(QUANTITY).isHidden());
    assertTrue(design.treatedSamples.getColumn(DESTINATION_CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(NUMBER).isHidden());
    assertTrue(design.treatedSamples.getColumn(PI_INTERVAL).isHidden());
    assertFalse(design.treatedSamples.getColumn(COMMENT).isHidden());
    assertFalse(design.explanationPanel.isVisible());
    List<TreatedSample> tss = items(design.treatedSamples);
    assertEquals(treatment.getTreatedSamples().size(), tss.size());
    for (TreatedSample ts : treatment.getTreatedSamples()) {
      assertTrue(tss.contains(ts));
    }
  }

  @Test
  public void enter_Transfer() {
    Treatment treatment = repository.findOne(9L);
    when(treatmentService.get(any())).thenReturn(treatment);
    presenter.init(view);
    presenter.enter("9");

    verify(treatmentService).get(9L);
    assertFalse(design.deleted.isVisible());
    assertFalse(design.protocolPanel.isVisible());
    assertFalse(design.fractionationTypePanel.isVisible());
    assertFalse(design.treatedSamples.getColumn(SAMPLE).isHidden());
    assertFalse(design.treatedSamples.getColumn(CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOURCE_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT).isHidden());
    assertTrue(design.treatedSamples.getColumn(SOLVENT_VOLUME).isHidden());
    assertTrue(design.treatedSamples.getColumn(NAME).isHidden());
    assertTrue(design.treatedSamples.getColumn(QUANTITY).isHidden());
    assertFalse(design.treatedSamples.getColumn(DESTINATION_CONTAINER).isHidden());
    assertTrue(design.treatedSamples.getColumn(NUMBER).isHidden());
    assertTrue(design.treatedSamples.getColumn(PI_INTERVAL).isHidden());
    assertFalse(design.treatedSamples.getColumn(COMMENT).isHidden());
    assertFalse(design.explanationPanel.isVisible());
    List<TreatedSample> tss = items(design.treatedSamples);
    assertEquals(treatment.getTreatedSamples().size(), tss.size());
    for (TreatedSample ts : treatment.getTreatedSamples()) {
      assertTrue(tss.contains(ts));
    }
  }

  @Test
  public void enter_TreatmentNotId() {
    presenter.init(view);
    presenter.enter("a");

    verify(view).showWarning(resources.message(INVALID_TREATMENT));
    List<TreatedSample> tss = items(design.treatedSamples);
    assertTrue(tss.isEmpty());
  }

  @Test
  public void enter_TreatmentIdNotExists() {
    presenter.init(view);
    presenter.enter("6");

    verify(treatmentService).get(6L);
    verify(view).showWarning(resources.message(INVALID_TREATMENT));
    List<TreatedSample> tss = items(design.treatedSamples);
    assertTrue(tss.isEmpty());
  }
}
