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
import static ca.qc.ircm.proview.submission.web.HistoryView.ACTIVITIES;
import static ca.qc.ircm.proview.submission.web.HistoryView.DESCRIPTION;
import static ca.qc.ircm.proview.submission.web.HistoryView.DESCRIPTION_SPAN;
import static ca.qc.ircm.proview.submission.web.HistoryView.EXPLANATION_SPAN;
import static ca.qc.ircm.proview.submission.web.HistoryView.HEADER;
import static ca.qc.ircm.proview.submission.web.HistoryView.ID;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.doubleClickItem;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.rendererTemplate;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.FRENCH;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityRepository;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.web.TreatmentDialog;
import ca.qc.ircm.proview.web.WebConstants;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.BeforeEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class HistoryViewTest extends AbstractViewTestCase {
  private HistoryView view;
  @Mock
  private HistoryViewPresenter presenter;
  @Mock
  private SubmissionDialog dialog;
  @Mock
  private TreatmentDialog treatmentDialog;
  @Mock
  private MsAnalysisDialog msAnalysisDialog;
  @Mock
  private Submission submission;
  @Mock
  private BeforeEvent beforeEvent;
  @Autowired
  private ActivityRepository repository;
  @Captor
  private ArgumentCaptor<ValueProvider<Activity, String>> valueProviderCaptor;
  @Captor
  private ArgumentCaptor<TemplateRenderer<Activity>> templateRendererCaptor;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(HistoryView.class, locale);
  private AppResources webResources = new AppResources(WebConstants.class, locale);
  private AppResources activityResources = new AppResources(Activity.class, locale);
  private List<Activity> activities;

  /**
   * Before tests.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    view = new HistoryView(presenter, dialog, msAnalysisDialog, treatmentDialog);
    view.init();
    activities = repository.findAll();
  }

  @SuppressWarnings("unchecked")
  private void mockColumns() {
    Element gridElement = view.activities.getElement();
    view.activities = mock(Grid.class);
    when(view.activities.getElement()).thenReturn(gridElement);
    view.user = mock(Column.class);
    when(view.activities.addColumn(any(ValueProvider.class), eq(USER))).thenReturn(view.user);
    when(view.user.setKey(any())).thenReturn(view.user);
    when(view.user.setHeader(any(String.class))).thenReturn(view.user);
    view.type = mock(Column.class);
    when(view.activities.addColumn(any(ValueProvider.class), eq(ACTION_TYPE)))
        .thenReturn(view.type);
    when(view.type.setKey(any())).thenReturn(view.type);
    when(view.type.setHeader(any(String.class))).thenReturn(view.type);
    view.date = mock(Column.class);
    when(view.activities.addColumn(any(ValueProvider.class), eq(TIMESTAMP))).thenReturn(view.date);
    when(view.date.setKey(any())).thenReturn(view.date);
    when(view.date.setHeader(any(String.class))).thenReturn(view.date);
    view.description = mock(Column.class);
    when(view.activities.addColumn(any(TemplateRenderer.class), eq(DESCRIPTION)))
        .thenReturn(view.description);
    when(view.description.setKey(any())).thenReturn(view.description);
    when(view.description.setHeader(any(String.class))).thenReturn(view.description);
    when(view.description.setSortable(anyBoolean())).thenReturn(view.description);
    view.explanation = mock(Column.class);
    when(view.activities.addColumn(any(TemplateRenderer.class), eq(EXPLANATION)))
        .thenReturn(view.explanation);
    when(view.explanation.setKey(any())).thenReturn(view.explanation);
    when(view.explanation.setHeader(any(String.class))).thenReturn(view.explanation);
    when(view.explanation.setSortable(anyBoolean())).thenReturn(view.explanation);
  }

  @Test
  public void init() {
    verify(presenter).init(view);
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(HEADER, view.header.getId().orElse(""));
    assertEquals(ACTIVITIES, view.activities.getId().orElse(""));
  }

  @Test
  public void labels() {
    mockColumns();
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER, ""), view.header.getText());
    verify(view.user).setHeader(activityResources.message(USER));
    verify(view.user).setFooter(activityResources.message(USER));
    verify(view.type).setHeader(activityResources.message(ACTION_TYPE));
    verify(view.type).setFooter(activityResources.message(ACTION_TYPE));
    verify(view.date).setHeader(activityResources.message(TIMESTAMP));
    verify(view.date).setFooter(activityResources.message(TIMESTAMP));
    verify(view.description).setHeader(resources.message(DESCRIPTION));
    verify(view.description).setFooter(resources.message(DESCRIPTION));
    verify(view.explanation).setHeader(activityResources.message(EXPLANATION));
    verify(view.explanation).setFooter(activityResources.message(EXPLANATION));
  }

  @Test
  public void localeChange() {
    mockColumns();
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(HistoryView.class, locale);
    final AppResources activityResources = new AppResources(Activity.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER, ""), view.header.getText());
    verify(view.user).setHeader(activityResources.message(USER));
    verify(view.user).setFooter(activityResources.message(USER));
    verify(view.type, atLeastOnce()).setHeader(activityResources.message(ACTION_TYPE));
    verify(view.type, atLeastOnce()).setFooter(activityResources.message(ACTION_TYPE));
    verify(view.date, atLeastOnce()).setHeader(activityResources.message(TIMESTAMP));
    verify(view.date, atLeastOnce()).setFooter(activityResources.message(TIMESTAMP));
    verify(view.description, atLeastOnce()).setHeader(resources.message(DESCRIPTION));
    verify(view.description, atLeastOnce()).setFooter(resources.message(DESCRIPTION));
    verify(view.explanation).setHeader(activityResources.message(EXPLANATION));
    verify(view.explanation).setFooter(activityResources.message(EXPLANATION));
  }

  @Test
  public void activities_Columns() {
    assertEquals(5, view.activities.getColumns().size());
    assertNotNull(view.activities.getColumnByKey(USER));
    assertTrue(view.activities.getColumnByKey(USER).isSortable());
    assertNotNull(view.activities.getColumnByKey(ACTION_TYPE));
    assertTrue(view.activities.getColumnByKey(ACTION_TYPE).isSortable());
    assertNotNull(view.activities.getColumnByKey(TIMESTAMP));
    assertTrue(view.activities.getColumnByKey(TIMESTAMP).isSortable());
    assertNotNull(view.activities.getColumnByKey(DESCRIPTION));
    assertFalse(view.activities.getColumnByKey(DESCRIPTION).isSortable());
    assertNotNull(view.activities.getColumnByKey(EXPLANATION));
    assertFalse(view.activities.getColumnByKey(EXPLANATION).isSortable());
  }

  @Test
  public void activities_ColumnsValueProvider() {
    Map<Activity, String> descriptions = IntStream.range(0, activities.size()).boxed()
        .collect(Collectors.toMap(in -> activities.get(in), in -> "description " + in));
    when(presenter.description(any(), any())).thenAnswer(i -> descriptions.get(i.getArgument(0)));
    view = new HistoryView(presenter, dialog, msAnalysisDialog, treatmentDialog);
    mockColumns();
    view.init();
    verify(view.activities).addColumn(valueProviderCaptor.capture(), eq(USER));
    ValueProvider<Activity, String> valueProvider = valueProviderCaptor.getValue();
    for (Activity activity : activities) {
      assertEquals(activity.getUser().getName(), valueProvider.apply(activity));
    }
    verify(view.activities).addColumn(valueProviderCaptor.capture(), eq(ACTION_TYPE));
    valueProvider = valueProviderCaptor.getValue();
    for (Activity activity : activities) {
      assertEquals(activity.getActionType().getLabel(locale), valueProvider.apply(activity));
    }
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE_TIME;
    verify(view.activities).addColumn(valueProviderCaptor.capture(), eq(TIMESTAMP));
    valueProvider = valueProviderCaptor.getValue();
    for (Activity activity : activities) {
      assertEquals(dateFormatter.format(activity.getTimestamp()), valueProvider.apply(activity));
    }
    verify(view.activities).addColumn(templateRendererCaptor.capture(), eq(DESCRIPTION));
    TemplateRenderer<Activity> templateRenderer = templateRendererCaptor.getValue();
    for (Activity activity : activities) {
      assertEquals(DESCRIPTION_SPAN, rendererTemplate(templateRenderer));
      assertTrue(templateRenderer.getValueProviders().containsKey("descriptionValue"));
      assertEquals(descriptions.get(activity),
          templateRenderer.getValueProviders().get("descriptionValue").apply(activity));
      assertTrue(templateRenderer.getValueProviders().containsKey("descriptionTitle"));
      assertEquals(descriptions.get(activity),
          templateRenderer.getValueProviders().get("descriptionTitle").apply(activity));
      verify(presenter, times(2)).description(activity, locale);
    }
    verify(view.activities).addColumn(templateRendererCaptor.capture(), eq(EXPLANATION));
    templateRenderer = templateRendererCaptor.getValue();
    for (Activity activity : activities) {
      assertEquals(EXPLANATION_SPAN, rendererTemplate(templateRenderer));
      assertTrue(templateRenderer.getValueProviders().containsKey("explanationValue"));
      assertEquals(activity.getExplanation(),
          templateRenderer.getValueProviders().get("explanationValue").apply(activity));
      assertTrue(templateRenderer.getValueProviders().containsKey("explanationTitle"));
      assertEquals(activity.getExplanation(),
          templateRenderer.getValueProviders().get("explanationTitle").apply(activity));
    }
  }

  @Test
  public void activities_DoubleClick() {
    Activity activity = activities.get(0);
    doubleClickItem(view.activities, activity);

    verify(presenter).view(activity, locale);
  }

  @Test
  public void getPageTitle() {
    assertEquals(resources.message(TITLE, webResources.message(APPLICATION_NAME)),
        view.getPageTitle());
  }

  @Test
  public void setParameter() {
    Submission submission = new Submission(1L);
    String experiment = "test submission";
    submission.setExperiment(experiment);
    when(presenter.getSubmission()).thenReturn(submission);
    view.setParameter(beforeEvent, 12L);
    verify(presenter).setParameter(12L);
    assertEquals(resources.message(HEADER, experiment), view.header.getText());
  }

  @Test
  public void setParameter_Null() {
    view.setParameter(beforeEvent, null);
    verify(presenter).setParameter(null);
    assertEquals(resources.message(HEADER, ""), view.header.getText());
  }
}
