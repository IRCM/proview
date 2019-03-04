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

package ca.qc.ircm.proview.dataanalysis.web;

import static ca.qc.ircm.proview.dataanalysis.DataAnalysisProperties.MAX_WORK_TIME;
import static ca.qc.ircm.proview.dataanalysis.DataAnalysisProperties.PEPTIDE;
import static ca.qc.ircm.proview.dataanalysis.DataAnalysisProperties.PROTEIN;
import static ca.qc.ircm.proview.dataanalysis.DataAnalysisProperties.SAMPLE;
import static ca.qc.ircm.proview.dataanalysis.DataAnalysisProperties.TYPE;
import static ca.qc.ircm.proview.dataanalysis.web.DataAnalysisViewPresenter.ANALYSES;
import static ca.qc.ircm.proview.dataanalysis.web.DataAnalysisViewPresenter.HEADER;
import static ca.qc.ircm.proview.dataanalysis.web.DataAnalysisViewPresenter.MULTIPLE_PROTEINS;
import static ca.qc.ircm.proview.dataanalysis.web.DataAnalysisViewPresenter.PEPTIDE_ANALYSIS;
import static ca.qc.ircm.proview.dataanalysis.web.DataAnalysisViewPresenter.PROTEIN_ANALYSIS;
import static ca.qc.ircm.proview.dataanalysis.web.DataAnalysisViewPresenter.SAVE;
import static ca.qc.ircm.proview.dataanalysis.web.DataAnalysisViewPresenter.SAVED;
import static ca.qc.ircm.proview.dataanalysis.web.DataAnalysisViewPresenter.TITLE;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.INVALID_SAMPLES;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.errorMessage;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.dataanalysis.DataAnalysisService;
import ca.qc.ircm.proview.dataanalysis.DataAnalysisType;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleRepository;
import ca.qc.ircm.proview.sample.SampleService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.AbstractComponentTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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
public class DataAnalysisViewPresenterTest extends AbstractComponentTestCase {
  @Inject
  private DataAnalysisViewPresenter presenter;
  @Mock
  private DataAnalysisView view;
  @MockBean
  private DataAnalysisService dataAnalysisService;
  @MockBean
  private SampleService sampleService;
  @Captor
  private ArgumentCaptor<Collection<DataAnalysis>> dataAnalysesCaptor;
  @Inject
  private SampleRepository sampleRepository;
  @Inject
  private SubmissionSampleRepository submissionSampleRepository;
  @Value("${spring.application.name}")
  private String applicationName;
  private DataAnalysisViewDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(DataAnalysisView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<SubmissionSample> samples = new ArrayList<>();
  private String protein1 = "123,456";
  private String protein2 = "123";
  private String peptide1 = null;
  private String peptide2 = "2";
  private DataAnalysisType type1 = DataAnalysisType.PROTEIN;
  private DataAnalysisType type2 = DataAnalysisType.PROTEIN_PEPTIDE;
  private Double maxWorkTime1 = 2.0;
  private Double maxWorkTime2 = 1.0;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new DataAnalysisViewPresenter(dataAnalysisService, sampleService, applicationName);
    design = new DataAnalysisViewDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    samples.add(submissionSampleRepository.findOne(442L));
    samples.add(submissionSampleRepository.findOne(443L));
    samples.forEach(s -> detach(s));
    when(view.savedSamples()).thenReturn(new ArrayList<>(samples));
    when(sampleService.get(any()))
        .thenAnswer(i -> i != null ? sampleRepository.findOne((Long) i.getArguments()[0]) : null);
  }

  @SuppressWarnings("unchecked")
  private void setFields() {
    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    DataAnalysis analysis = analyses.get(0);
    ((TextField) design.analyses.getColumn(PROTEIN).getValueProvider().apply(analysis))
        .setValue(protein1);
    ((TextField) design.analyses.getColumn(PEPTIDE).getValueProvider().apply(analysis))
        .setValue(Objects.toString(peptide1, ""));
    ((ComboBox<DataAnalysisType>) design.analyses.getColumn(TYPE).getValueProvider()
        .apply(analysis)).setValue(type1);
    ((ComboBox<Double>) design.analyses.getColumn(MAX_WORK_TIME).getValueProvider().apply(analysis))
        .setValue(maxWorkTime1);
    analysis = analyses.get(1);
    ((TextField) design.analyses.getColumn(PROTEIN).getValueProvider().apply(analysis))
        .setValue(protein2);
    ((TextField) design.analyses.getColumn(PEPTIDE).getValueProvider().apply(analysis))
        .setValue(Objects.toString(peptide2, ""));
    ((ComboBox<DataAnalysisType>) design.analyses.getColumn(TYPE).getValueProvider()
        .apply(analysis)).setValue(type2);
    ((ComboBox<Double>) design.analyses.getColumn(MAX_WORK_TIME).getValueProvider().apply(analysis))
        .setValue(maxWorkTime2);
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.enter("");

    assertTrue(design.header.getStyleName().contains(HEADER));
    assertTrue(design.header.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.proteinAnalysis.getStyleName().contains(PROTEIN_ANALYSIS));
    assertTrue(design.peptideAnalysis.getStyleName().contains(PEPTIDE_ANALYSIS));
    assertTrue(design.multipleProteins.getStyleName().contains(MULTIPLE_PROTEINS));
    assertTrue(design.analyses.getStyleName().contains(ANALYSES));
    assertTrue(design.analyses.getStyleName().contains(COMPONENTS));
    assertTrue(design.save.getStyleName().contains(SAVE));
    assertTrue(design.save.getStyleName().contains(ValoTheme.BUTTON_PRIMARY));
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.enter("");

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
    assertEquals(resources.message(PROTEIN_ANALYSIS), design.proteinAnalysis.getValue());
    assertEquals(resources.message(PEPTIDE_ANALYSIS), design.peptideAnalysis.getValue());
    assertEquals(resources.message(MULTIPLE_PROTEINS), design.multipleProteins.getValue());
    assertEquals(resources.message(SAVE), design.save.getCaption());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void analyses() {
    presenter.init(view);
    presenter.enter("");

    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    assertEquals(5, design.analyses.getColumns().size());
    assertEquals(SAMPLE, design.analyses.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE), design.analyses.getColumn(SAMPLE).getCaption());
    for (DataAnalysis analysis : analyses) {
      assertEquals(analysis.getSample().getName(),
          design.analyses.getColumn(SAMPLE).getValueProvider().apply(analysis));
    }
    assertEquals(PROTEIN, design.analyses.getColumns().get(1).getId());
    assertEquals(resources.message(PROTEIN), design.analyses.getColumn(PROTEIN).getCaption());
    assertTrue(containsInstanceOf(design.analyses.getColumn(PROTEIN).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.analyses.getColumn(PROTEIN).isSortable());
    for (DataAnalysis analysis : analyses) {
      TextField field =
          (TextField) design.analyses.getColumn(PROTEIN).getValueProvider().apply(analysis);
      assertTrue(field.getStyleName().contains(PROTEIN));
    }
    assertEquals(PEPTIDE, design.analyses.getColumns().get(2).getId());
    assertEquals(resources.message(PEPTIDE), design.analyses.getColumn(PEPTIDE).getCaption());
    assertTrue(containsInstanceOf(design.analyses.getColumn(PEPTIDE).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.analyses.getColumn(PEPTIDE).isSortable());
    for (DataAnalysis analysis : analyses) {
      TextField field =
          (TextField) design.analyses.getColumn(PEPTIDE).getValueProvider().apply(analysis);
      assertTrue(field.getStyleName().contains(PEPTIDE));
    }
    assertEquals(TYPE, design.analyses.getColumns().get(3).getId());
    assertEquals(resources.message(TYPE), design.analyses.getColumn(TYPE).getCaption());
    assertTrue(containsInstanceOf(design.analyses.getColumn(TYPE).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.analyses.getColumn(TYPE).isSortable());
    for (DataAnalysis analysis : analyses) {
      ComboBox<DataAnalysisType> field = (ComboBox<DataAnalysisType>) design.analyses
          .getColumn(TYPE).getValueProvider().apply(analysis);
      assertTrue(field.getStyleName().contains(TYPE));
      assertFalse(field.isEmptySelectionAllowed());
      List<DataAnalysisType> types = new ArrayList<>(dataProvider(field).getItems());
      assertEquals(DataAnalysisType.values().length, types.size());
      for (DataAnalysisType type : DataAnalysisType.values()) {
        assertTrue(types.contains(type));
        assertEquals(type.getLabel(locale), field.getItemCaptionGenerator().apply(type));
      }
    }
    assertEquals(MAX_WORK_TIME, design.analyses.getColumns().get(4).getId());
    assertEquals(resources.message(MAX_WORK_TIME),
        design.analyses.getColumn(MAX_WORK_TIME).getCaption());
    assertTrue(containsInstanceOf(design.analyses.getColumn(MAX_WORK_TIME).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.analyses.getColumn(MAX_WORK_TIME).isSortable());
    for (DataAnalysis analysis : analyses) {
      ComboBox<Double> field = (ComboBox<Double>) design.analyses.getColumn(MAX_WORK_TIME)
          .getValueProvider().apply(analysis);
      assertTrue(field.getStyleName().contains(MAX_WORK_TIME));
      assertFalse(field.isEmptySelectionAllowed());
      List<Double> values = new ArrayList<>(dataProvider(field).getItems());
      assertEquals(DataAnalysisViewPresenter.getMaxWorkTimeValues().length, values.size());
      for (Double value : DataAnalysisViewPresenter.getMaxWorkTimeValues()) {
        assertTrue(values.contains(value));
      }
    }
    assertEquals(2, analyses.size());
    for (SubmissionSample sample : samples) {
      assertTrue(
          analyses.stream().filter(da -> da.getSample().equals(sample)).findAny().isPresent());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void maxWorkTime_NewValue() {
    presenter.init(view);
    presenter.enter("");

    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    ComboBox<Double> field = (ComboBox<Double>) design.analyses.getColumn(MAX_WORK_TIME)
        .getValueProvider().apply(analyses.get(0));
    Optional<Double> optionalNewValue = field.getNewItemProvider().apply("8");
    assertTrue(optionalNewValue.isPresent());
    assertEquals(8.0, optionalNewValue.get(), 0.00001);
    List<Double> values = new ArrayList<>(dataProvider(field).getItems());
    assertEquals(DataAnalysisViewPresenter.getMaxWorkTimeValues().length + 1, values.size());
    assertTrue(values.contains(8.0));
    for (Double value : DataAnalysisViewPresenter.getMaxWorkTimeValues()) {
      assertTrue(values.contains(value));
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void maxWorkTime_NewValueInvalid() {
    presenter.init(view);
    presenter.enter("");

    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    ComboBox<Double> field = (ComboBox<Double>) design.analyses.getColumn(MAX_WORK_TIME)
        .getValueProvider().apply(analyses.get(0));
    Optional<Double> optionalNewValue = field.getNewItemProvider().apply("a");
    assertFalse(optionalNewValue.isPresent());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        field.getErrorMessage().getFormattedHtmlMessage());
    List<Double> values = new ArrayList<>(dataProvider(field).getItems());
    assertEquals(DataAnalysisViewPresenter.getMaxWorkTimeValues().length, values.size());
    for (Double value : DataAnalysisViewPresenter.getMaxWorkTimeValues()) {
      assertTrue(values.contains(value));
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void maxWorkTime_NewValueBelowMinimum() {
    presenter.init(view);
    presenter.enter("");

    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    ComboBox<Double> field = (ComboBox<Double>) design.analyses.getColumn(MAX_WORK_TIME)
        .getValueProvider().apply(analyses.get(0));
    Optional<Double> optionalNewValue = field.getNewItemProvider().apply("0.25");
    assertTrue(optionalNewValue.isPresent());
    assertEquals(0.25, optionalNewValue.get(), 0.00001);
    field.setValue(optionalNewValue.get());
    assertNotNull(field.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void save_EmptyProteinWithPeptide() {
    presenter.init(view);
    presenter.enter("");
    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    ((TextField) design.analyses.getColumn(PEPTIDE).getValueProvider().apply(analyses.get(0)))
        .setValue("1");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    TextField field =
        (TextField) design.analyses.getColumn(PROTEIN).getValueProvider().apply(analyses.get(0));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(dataAnalysisService, never()).insert(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_EmptyPeptideWithPeptideType() {
    presenter.init(view);
    presenter.enter("");
    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    ((TextField) design.analyses.getColumn(PROTEIN).getValueProvider().apply(analyses.get(0)))
        .setValue("1");
    ((ComboBox<DataAnalysisType>) design.analyses.getColumn(TYPE).getValueProvider()
        .apply(analyses.get(0))).setValue(DataAnalysisType.PEPTIDE);

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    TextField field =
        (TextField) design.analyses.getColumn(PEPTIDE).getValueProvider().apply(analyses.get(0));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(dataAnalysisService, never()).insert(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_EmptyPeptideWithProteinPeptideType() {
    presenter.init(view);
    presenter.enter("");
    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    ((TextField) design.analyses.getColumn(PROTEIN).getValueProvider().apply(analyses.get(0)))
        .setValue("1");
    ((ComboBox<DataAnalysisType>) design.analyses.getColumn(TYPE).getValueProvider()
        .apply(analyses.get(0))).setValue(DataAnalysisType.PROTEIN_PEPTIDE);

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    TextField field =
        (TextField) design.analyses.getColumn(PEPTIDE).getValueProvider().apply(analyses.get(0));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(dataAnalysisService, never()).insert(any());
  }

  @Test
  public void save() {
    presenter.init(view);
    presenter.enter("");
    setFields();

    design.save.click();

    verify(view, never()).showError(any());
    verify(dataAnalysisService).insert(dataAnalysesCaptor.capture());
    List<DataAnalysis> dataAnalyses = new ArrayList<>(dataAnalysesCaptor.getValue());
    assertEquals(samples.size(), dataAnalyses.size());
    Function<Sample, DataAnalysis> findBySample = sample -> dataAnalyses.stream()
        .filter(da -> da.getSample().equals(sample)).findFirst().orElse(null);
    DataAnalysis dataAnalysis = findBySample.apply(samples.get(0));
    assertEquals(samples.get(0), dataAnalysis.getSample());
    assertEquals(protein1, dataAnalysis.getProtein());
    assertEquals(peptide1, dataAnalysis.getPeptide());
    assertEquals(type1, dataAnalysis.getType());
    assertEquals(maxWorkTime1, dataAnalysis.getMaxWorkTime());
    dataAnalysis = findBySample.apply(samples.get(1));
    assertEquals(samples.get(1), dataAnalysis.getSample());
    assertEquals(protein2, dataAnalysis.getProtein());
    assertEquals(peptide2, dataAnalysis.getPeptide());
    assertEquals(type2, dataAnalysis.getType());
    assertEquals(maxWorkTime2, dataAnalysis.getMaxWorkTime());
    verify(view).showTrayNotification(resources.message(SAVED, 2));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void enter() {
    presenter.init(view);
    presenter.enter("");

    verify(view, never()).showWarning(any());
    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    assertEquals(samples.size(), analyses.size());
    for (int i = 0; i < samples.size(); i++) {
      assertEquals(samples.get(i), analyses.get(i).getSample());
    }
  }

  @Test
  public void enter_Sample() {
    SubmissionSample sample = submissionSampleRepository.findOne(445L);
    List<Sample> samples = new ArrayList<>();
    samples.add(sample);
    presenter.init(view);
    presenter.enter("445");

    verify(sampleService, atLeastOnce()).get(sample.getId());
    verify(view, never()).showWarning(any());
    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    assertEquals(samples.size(), analyses.size());
    for (int i = 0; i < samples.size(); i++) {
      assertEquals(samples.get(i), analyses.get(i).getSample());
    }
  }

  @Test
  public void enter_Samples() {
    Sample sample1 = sampleRepository.findOne(445L);
    Sample sample2 = sampleRepository.findOne(446L);
    List<Sample> samples = new ArrayList<>();
    samples.add(sample1);
    samples.add(sample2);
    presenter.init(view);
    presenter.enter("445,446");

    verify(sampleService, atLeastOnce()).get(sample1.getId());
    verify(sampleService, atLeastOnce()).get(sample2.getId());
    verify(view, never()).showWarning(any());
    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    assertEquals(samples.size(), analyses.size());
    for (int i = 0; i < samples.size(); i++) {
      assertEquals(samples.get(i), analyses.get(i).getSample());
    }
  }

  @Test
  public void enter_NotExists() {
    when(sampleService.get(any(Long.class))).thenReturn(null);
    presenter.init(view);
    presenter.enter("445");

    verify(sampleService, atLeastOnce()).get(445L);
    verify(view).showWarning(resources.message(INVALID_SAMPLES));
    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    assertTrue(analyses.isEmpty());
  }

  @Test
  public void enter_InvalidId() {
    presenter.init(view);
    presenter.enter("a");

    verify(view).showWarning(resources.message(INVALID_SAMPLES));
    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    assertTrue(analyses.isEmpty());
  }

  @Test
  public void enter_EmptyId() {
    SubmissionSample sample = submissionSampleRepository.findOne(445L);
    List<Sample> samples = new ArrayList<>();
    samples.add(sample);
    presenter.init(view);
    presenter.enter("445,");

    verify(view).showWarning(resources.message(INVALID_SAMPLES));
    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    assertTrue(analyses.isEmpty());
  }

  @Test
  public void enter_SampleWithControl() {
    Sample sample1 = sampleRepository.findOne(445L);
    List<Sample> samples = new ArrayList<>();
    samples.add(sample1);
    presenter.init(view);
    presenter.enter("445,444");

    verify(sampleService, atLeastOnce()).get(sample1.getId());
    verify(sampleService, atLeastOnce()).get(444L);
    verify(view, never()).showWarning(any());
    List<DataAnalysis> analyses = new ArrayList<>(dataProvider(design.analyses).getItems());
    assertEquals(samples.size(), analyses.size());
    for (int i = 0; i < samples.size(); i++) {
      assertEquals(samples.get(i), analyses.get(i).getSample());
    }
  }
}
