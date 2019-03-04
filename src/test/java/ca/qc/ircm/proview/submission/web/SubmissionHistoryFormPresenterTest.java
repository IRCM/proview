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

import static ca.qc.ircm.proview.history.ActivityProperties.ACTION_TYPE;
import static ca.qc.ircm.proview.history.ActivityProperties.EXPLANATION;
import static ca.qc.ircm.proview.history.ActivityProperties.TIMESTAMP;
import static ca.qc.ircm.proview.history.ActivityProperties.USER;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.ACTIVITIES;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.ACTIVITIES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.DESCRIPTION;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.DESCRIPTION_LONG;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.LAST_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.SAMPLES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.SAMPLE_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.SAMPLE_STATUS;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.VIEW;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDateTime;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.dataanalysis.DataAnalysisRepository;
import ca.qc.ircm.proview.digestion.DigestionRepository;
import ca.qc.ircm.proview.digestion.web.DigestionView;
import ca.qc.ircm.proview.dilution.DilutionRepository;
import ca.qc.ircm.proview.dilution.web.DilutionView;
import ca.qc.ircm.proview.enrichment.EnrichmentRepository;
import ca.qc.ircm.proview.enrichment.web.EnrichmentView;
import ca.qc.ircm.proview.fractionation.FractionationRepository;
import ca.qc.ircm.proview.fractionation.web.FractionationView;
import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityRepository;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.msanalysis.MsAnalysisRepository;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisView;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.sample.SampleRepository;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.web.SampleView;
import ca.qc.ircm.proview.solubilisation.SolubilisationRepository;
import ca.qc.ircm.proview.solubilisation.web.SolubilisationView;
import ca.qc.ircm.proview.standard.StandardAdditionRepository;
import ca.qc.ircm.proview.standard.web.StandardAdditionView;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.transfer.TransferRepository;
import ca.qc.ircm.proview.transfer.web.TransferView;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.renderers.ComponentRenderer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionHistoryFormPresenterTest {
  @Inject
  private SubmissionHistoryFormPresenter presenter;
  @Mock
  private SubmissionHistoryForm view;
  @MockBean
  private ActivityService activityService;
  @MockBean
  private SampleContainerService sampleContainerService;
  @MockBean
  private SubmissionAnalysesWindow submissionAnalysesWindow;
  @Inject
  private ActivityRepository repository;
  @Inject
  private SubmissionRepository submissionRepository;
  @Inject
  private SampleRepository sampleRepository;
  @Inject
  private DigestionRepository digestionRepository;
  @Inject
  private DilutionRepository dilutionRepository;
  @Inject
  private EnrichmentRepository enrichmentRepository;
  @Inject
  private FractionationRepository fractionationRepository;
  @Inject
  private SolubilisationRepository solubilisationRepository;
  @Inject
  private StandardAdditionRepository standardAdditionRepository;
  @Inject
  private TransferRepository transferRepository;
  @Inject
  private MsAnalysisRepository msAnalysisRepository;
  @Inject
  private DataAnalysisRepository dataAnalysisRepository;
  private SubmissionHistoryFormDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SubmissionHistoryForm.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private SubmissionSample sample1;
  private SubmissionSample sample2;
  private Submission submission;
  private Tube tube1;
  private Tube tube2;
  private Tube last1;
  private Well last2;
  private Activity activity1;
  private Activity activity2;
  private String activityDescription1;
  private String activityDescription2;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    design = new SubmissionHistoryFormDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    submission = new Submission();
    sample1 = new SubmissionSample();
    sample1.setName("sample_name");
    sample1.setType(SampleType.SOLUTION);
    sample1.setStatus(SampleStatus.ANALYSED);
    sample1.setQuantity("10.4 ug");
    sample1.setVolume("10.3 ul");
    sample1.setNumberProtein(4);
    sample1.setMolecularWeight(5.6);
    sample1.setSubmission(submission);
    tube1 = new Tube();
    tube1.setName("tube_name");
    sample1.setOriginalContainer(tube1);
    sample2 = new SubmissionSample();
    sample2.setName("sample_name");
    sample2.setType(SampleType.SOLUTION);
    sample2.setStatus(SampleStatus.ANALYSED);
    sample2.setQuantity("10.4 ug");
    sample2.setVolume("10.3 ul");
    sample2.setNumberProtein(4);
    sample2.setMolecularWeight(5.6);
    sample2.setSubmission(submission);
    tube2 = new Tube();
    tube2.setName("tube_name");
    sample2.setOriginalContainer(tube1);
    submission.setSamples(Arrays.asList(sample1, sample2));
    last1 = new Tube();
    last1.setName("sample1 last tube");
    last2 = new Well(1, 2);
    last2.setPlate(new Plate(10L, "sample2 last plate"));
    when(sampleContainerService.last(sample1)).thenReturn(last1);
    when(sampleContainerService.last(sample2)).thenReturn(last2);
    activity1 = new Activity();
    activity1.setUser(new User(1L, "test@ircm.qc.ca"));
    activity1.setActionType(ActionType.INSERT);
    activity1.setTimestamp(Instant.now().minus(2, ChronoUnit.DAYS).minus(1, ChronoUnit.HOURS));
    activity2 = new Activity();
    activity2.setUser(new User(1L, "test@ircm.qc.ca"));
    activity2.setActionType(ActionType.UPDATE);
    activity2.setTimestamp(Instant.now());
    activity2.setExplanation("test_explanation");
    when(activityService.all(any(Submission.class)))
        .thenReturn(Arrays.asList(activity1, activity2));
    activityDescription1 = "description 1";
    activityDescription2 = "description 2\nAnother line";
    when(activityService.description(activity1, locale)).thenReturn(activityDescription1);
    when(activityService.description(activity2, locale)).thenReturn(activityDescription2);
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.setValue(submission);

    assertTrue(design.samplesPanel.getStyleName().contains(SAMPLES_PANEL));
    assertTrue(design.samples.getStyleName().contains(SAMPLES));
    assertTrue(design.activitiesPanel.getStyleName().contains(ACTIVITIES_PANEL));
    assertTrue(design.activities.getStyleName().contains(ACTIVITIES));
    assertTrue(design.activities.getStyleName().contains(COMPONENTS));
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.setValue(submission);

    assertEquals(resources.message(SAMPLES_PANEL), design.samplesPanel.getCaption());
    assertEquals(resources.message(ACTIVITIES_PANEL), design.activitiesPanel.getCaption());
  }

  @Test
  public void samplesGrid() {
    presenter.init(view);
    presenter.setValue(submission);

    assertEquals(3, design.samples.getColumns().size());
    assertEquals(SAMPLE_NAME, design.samples.getColumns().get(0).getId());
    assertEquals(SAMPLE_STATUS, design.samples.getColumns().get(1).getId());
    assertEquals(LAST_CONTAINER, design.samples.getColumns().get(2).getId());
    assertEquals(resources.message(SAMPLE_NAME),
        design.samples.getColumn(SAMPLE_NAME).getCaption());
    assertEquals(sample1.getName(),
        design.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(sample1));
    assertEquals(sample2.getName(),
        design.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(sample2));
    assertEquals(resources.message(SAMPLE_STATUS),
        design.samples.getColumn(SAMPLE_STATUS).getCaption());
    assertEquals(sample1.getStatus().getLabel(locale),
        design.samples.getColumn(SAMPLE_STATUS).getValueProvider().apply(sample1));
    assertEquals(sample2.getName(),
        design.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(sample2));
    assertEquals(resources.message(LAST_CONTAINER),
        design.samples.getColumn(LAST_CONTAINER).getCaption());
    assertEquals(last1.getFullName(),
        design.samples.getColumn(LAST_CONTAINER).getValueProvider().apply(sample1));
    assertEquals(last2.getFullName(),
        design.samples.getColumn(LAST_CONTAINER).getValueProvider().apply(sample2));

    Collection<SubmissionSample> samples = dataProvider(design.samples).getItems();
    assertEquals(2, samples.size());
    assertTrue(samples.contains(sample1));
    assertTrue(samples.contains(sample2));
  }

  @Test
  public void activitiesGrid() {
    presenter.init(view);
    presenter.setValue(submission);

    final List<Activity> activities = items(design.activities);
    final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    assertEquals(6, design.activities.getColumns().size());
    assertEquals(USER, design.activities.getColumns().get(0).getId());
    assertEquals(ACTION_TYPE, design.activities.getColumns().get(1).getId());
    assertEquals(TIMESTAMP, design.activities.getColumns().get(2).getId());
    assertEquals(DESCRIPTION, design.activities.getColumns().get(3).getId());
    assertEquals(EXPLANATION, design.activities.getColumns().get(4).getId());
    assertEquals(VIEW, design.activities.getColumns().get(5).getId());
    assertEquals(resources.message(USER), design.activities.getColumn(USER).getCaption());
    assertEquals(activity1.getUser().getName(),
        design.activities.getColumn(USER).getValueProvider().apply(activity1));
    assertEquals(activity2.getUser().getName(),
        design.activities.getColumn(USER).getValueProvider().apply(activity2));
    assertEquals(activity1.getUser().getEmail(),
        design.activities.getColumn(USER).getDescriptionGenerator().apply(activity1));
    assertEquals(activity2.getUser().getEmail(),
        design.activities.getColumn(USER).getDescriptionGenerator().apply(activity2));
    assertEquals(resources.message(ACTION_TYPE),
        design.activities.getColumn(ACTION_TYPE).getCaption());
    assertEquals(activity1.getActionType().getLabel(locale),
        design.activities.getColumn(ACTION_TYPE).getValueProvider().apply(activity1));
    assertEquals(activity2.getActionType().getLabel(locale),
        design.activities.getColumn(ACTION_TYPE).getValueProvider().apply(activity2));
    assertEquals(resources.message(TIMESTAMP), design.activities.getColumn(TIMESTAMP).getCaption());
    assertEquals(dateFormatter.format(toLocalDateTime(activity1.getTimestamp())),
        design.activities.getColumn(TIMESTAMP).getValueProvider().apply(activity1));
    assertEquals(dateFormatter.format(toLocalDateTime(activity2.getTimestamp())),
        design.activities.getColumn(TIMESTAMP).getValueProvider().apply(activity2));
    assertEquals(resources.message(DESCRIPTION),
        design.activities.getColumn(DESCRIPTION).getCaption());
    assertEquals(activityDescription1,
        design.activities.getColumn(DESCRIPTION).getValueProvider().apply(activity1));
    assertEquals(activityDescription1,
        design.activities.getColumn(DESCRIPTION).getDescriptionGenerator().apply(activity1));
    assertEquals(
        resources.message(DESCRIPTION_LONG,
            activityDescription2.substring(0, activityDescription2.indexOf("\n"))),
        design.activities.getColumn(DESCRIPTION).getValueProvider().apply(activity2));
    assertEquals(activityDescription2,
        design.activities.getColumn(DESCRIPTION).getDescriptionGenerator().apply(activity2));
    assertEquals(resources.message(EXPLANATION),
        design.activities.getColumn(EXPLANATION).getCaption());
    assertEquals(activity1.getExplanation(),
        design.activities.getColumn(EXPLANATION).getValueProvider().apply(activity1));
    assertEquals(activity2.getExplanation(),
        design.activities.getColumn(EXPLANATION).getValueProvider().apply(activity2));
    assertEquals(resources.message(VIEW), design.activities.getColumn(VIEW).getCaption());
    assertTrue(containsInstanceOf(design.activities.getColumn(VIEW).getExtensions(),
        ComponentRenderer.class));
    for (Activity activity : activities) {
      Button button = (Button) design.activities.getColumn(VIEW).getValueProvider().apply(activity);
      assertTrue(button.getStyleName().contains(VIEW));
      assertEquals(resources.message(VIEW), button.getCaption());
    }

    verify(activityService).all(submission);
    assertEquals(2, activities.size());
    assertTrue(activities.contains(activity1));
    assertTrue(activities.contains(activity2));
    List<GridSortOrder<Activity>> sortOrders = design.activities.getSortOrder();
    assertEquals(1, sortOrders.size());
    GridSortOrder<Activity> sortOrder = sortOrders.get(0);
    assertEquals(TIMESTAMP, sortOrder.getSorted().getId());
    assertEquals(SortDirection.DESCENDING, sortOrder.getDirection());
  }

  @Test
  public void view_Submission() {
    Activity activity = repository.findOne(5543L);
    when(activityService.all(any(Submission.class))).thenReturn(Arrays.asList(activity));
    when(activityService.record(any())).thenAnswer(i -> submissionRepository.findOne(1L));
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.activities.getColumn(VIEW).getValueProvider().apply(activity);

    button.click();

    verify(view).navigateTo(SubmissionView.VIEW_NAME, "1");
  }

  @Test
  public void view_SubmissionOnSampleUpdate() {
    Activity activity = repository.findOne(5635L);
    when(activityService.all(any(Submission.class))).thenReturn(Arrays.asList(activity));
    when(activityService.record(any())).thenAnswer(i -> sampleRepository.findOne(559L));
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.activities.getColumn(VIEW).getValueProvider().apply(activity);

    button.click();

    verify(view).navigateTo(SampleView.VIEW_NAME, "559");
  }

  @Test
  public void view_Digestion() {
    Activity activity = repository.findOne(5639L);
    when(activityService.all(any(Submission.class))).thenReturn(Arrays.asList(activity));
    when(activityService.record(any())).thenAnswer(i -> digestionRepository.findOne(195L));
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.activities.getColumn(VIEW).getValueProvider().apply(activity);

    button.click();

    verify(view).navigateTo(DigestionView.VIEW_NAME, "195");
  }

  @Test
  public void view_Dilution() {
    Activity activity = repository.findOne(5680L);
    when(activityService.all(any(Submission.class))).thenReturn(Arrays.asList(activity));
    when(activityService.record(any())).thenAnswer(i -> dilutionRepository.findOne(210L));
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.activities.getColumn(VIEW).getValueProvider().apply(activity);

    button.click();

    verify(view).navigateTo(DilutionView.VIEW_NAME, "210");
  }

  @Test
  public void view_Enrichment() {
    Activity activity = repository.findOne(5719L);
    when(activityService.all(any(Submission.class))).thenReturn(Arrays.asList(activity));
    when(activityService.record(any())).thenAnswer(i -> enrichmentRepository.findOne(225L));
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.activities.getColumn(VIEW).getValueProvider().apply(activity);

    button.click();

    verify(view).navigateTo(EnrichmentView.VIEW_NAME, "225");
  }

  @Test
  public void view_Fractionation() {
    Activity activity = repository.findOne(5659L);
    when(activityService.all(any(Submission.class))).thenReturn(Arrays.asList(activity));
    when(activityService.record(any())).thenAnswer(i -> fractionationRepository.findOne(203L));
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.activities.getColumn(VIEW).getValueProvider().apply(activity);

    button.click();

    verify(view).navigateTo(FractionationView.VIEW_NAME, "203");
  }

  @Test
  public void view_Solubilisation() {
    Activity activity = repository.findOne(5763L);
    when(activityService.all(any(Submission.class))).thenReturn(Arrays.asList(activity));
    when(activityService.record(any())).thenAnswer(i -> solubilisationRepository.findOne(236L));
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.activities.getColumn(VIEW).getValueProvider().apply(activity);

    button.click();

    verify(view).navigateTo(SolubilisationView.VIEW_NAME, "236");
  }

  @Test
  public void view_StandardAddition() {
    Activity activity = repository.findOne(5796L);
    when(activityService.all(any(Submission.class))).thenReturn(Arrays.asList(activity));
    when(activityService.record(any())).thenAnswer(i -> standardAdditionRepository.findOne(248L));
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.activities.getColumn(VIEW).getValueProvider().apply(activity);

    button.click();

    verify(view).navigateTo(StandardAdditionView.VIEW_NAME, "248");
  }

  @Test
  public void view_Transfer() {
    Activity activity = repository.findOne(5657L);
    when(activityService.all(any(Submission.class))).thenReturn(Arrays.asList(activity));
    when(activityService.record(any())).thenAnswer(i -> transferRepository.findOne(201L));
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.activities.getColumn(VIEW).getValueProvider().apply(activity);

    button.click();

    verify(view).navigateTo(TransferView.VIEW_NAME, "201");
  }

  @Test
  public void view_MsAnalysis() {
    Activity activity = repository.findOne(5828L);
    when(activityService.all(any(Submission.class))).thenReturn(Arrays.asList(activity));
    when(activityService.record(any())).thenAnswer(i -> msAnalysisRepository.findOne(19L));
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.activities.getColumn(VIEW).getValueProvider().apply(activity);

    button.click();

    verify(view).navigateTo(MsAnalysisView.VIEW_NAME, "19");
  }

  @Test
  public void view_DataAnalysis() {
    Activity activity = repository.findOne(5566L);
    when(activityService.all(any(Submission.class))).thenReturn(Arrays.asList(activity));
    when(activityService.record(any())).thenAnswer(i -> dataAnalysisRepository.findOne(5L));
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.activities.getColumn(VIEW).getValueProvider().apply(activity);

    button.click();

    verify(submissionAnalysesWindow).setValue(submission);
    verify(submissionAnalysesWindow).center();
    verify(view).addWindow(submissionAnalysesWindow);
  }
}
