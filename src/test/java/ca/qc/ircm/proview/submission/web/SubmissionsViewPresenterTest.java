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

import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.submission.SubmissionProperties.ANALYSIS_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLE_DELIVERY_DATE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.ADD_SUBMISSION;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.ALL;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.COLUMN_ORDER;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.DATE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.DIRECTOR;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.EXPECTED_DATE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.EXPERIMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HELP;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HIDDEN;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HISTORY;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.INSTRUMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_STATUSES;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_STATUSES_SEPARATOR;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SERVICE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SUBMISSIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SUBMISSIONS_DESCRIPTION;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.TITLE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.UPDATE_STATUS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.USER;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.gridStartEdit;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.web.SampleStatusView;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFilter;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.AbstractComponentTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.UserPreferenceService;
import ca.qc.ircm.proview.web.HelpWindow;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.filter.LocalDateFilterComponent;
import ca.qc.ircm.utils.MessageResource;
import com.google.common.collect.Range;
import com.querydsl.core.types.OrderSpecifier;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.SelectionModel;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import java.text.Collator;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Ignore;
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
public class SubmissionsViewPresenterTest extends AbstractComponentTestCase {
  @Inject
  private SubmissionsViewPresenter presenter;
  @Inject
  private SubmissionRepository repository;
  @MockBean
  private SubmissionService submissionService;
  @MockBean
  private AuthorizationService authorizationService;
  @MockBean
  private UserPreferenceService userPreferenceService;
  @MockBean
  private LocalDateFilterComponent localDateFilterComponent;
  @MockBean
  private SubmissionWindow submissionWindow;
  @MockBean
  private SubmissionHistoryWindow submissionHistoryWindow;
  @MockBean
  private HelpWindow helpWindow;
  @Mock
  private SubmissionsView view;
  @Mock
  private ListDataProvider<Submission> submissionsDataProvider;
  @Mock
  private Registration registration;
  @Captor
  private ArgumentCaptor<Collection<Sample>> samplesCaptor;
  @Captor
  private ArgumentCaptor<Collection<Submission>> submissionsCaptor;
  @Captor
  private ArgumentCaptor<List<Sample>> samplesListCaptor;
  @Captor
  private ArgumentCaptor<SubmissionFilter> submissionFilterCaptor;
  @Captor
  private ArgumentCaptor<SaveListener<List<Sample>>> samplesSaveListenerCaptor;
  @Captor
  private ArgumentCaptor<SaveListener<List<SampleContainer>>> containersSaveListenerCaptor;
  @Captor
  private ArgumentCaptor<SaveListener<Range<LocalDate>>> localDateRangeSaveListenerCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private SubmissionsViewDesign design;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(SubmissionsView.class, locale);
  private List<Submission> submissions;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    design = new SubmissionsViewDesign();
    design.setParent(ui);
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    submissions = repository.findAll();
    when(submissionService.all(any())).thenReturn(submissions);
    when(submissionService.count(any())).thenReturn(submissions.size());
    when(userPreferenceService.get(any(), any(), any())).thenAnswer(i -> i.getArguments()[2]);
  }

  private String statusesValue(Submission submission) {
    return submission.getSamples().stream().map(s -> s.getStatus()).distinct().sorted()
        .map(s -> s.getLabel(locale))
        .collect(Collectors.joining(resources.message(SAMPLE_STATUSES_SEPARATOR)));
  }

  private boolean predictedStatus(Submission submission, SampleStatus status) {
    return !submission.getSamples().stream()
        .filter(sample -> sample.getStatus().compareTo(status) >= 0).findAny().isPresent();
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.headerLabel.getStyleName().contains(HEADER));
    assertTrue(design.help.getStyleName().contains(ValoTheme.BUTTON_LINK));
    assertTrue(design.help.getStyleName().contains(ValoTheme.BUTTON_LARGE));
    assertTrue(design.help.getStyleName().contains(ValoTheme.BUTTON_ICON_ONLY));
    assertTrue(design.help.getStyleName().contains(HELP));
    assertTrue(design.submissionsGrid.getStyleName().contains(SUBMISSIONS));
    assertTrue(design.submissionsGrid.getStyleName().contains(COMPONENTS));
    final Submission submission = submissions.get(0);
    Button designButton =
        (Button) design.submissionsGrid.getColumn(EXPERIMENT).getValueProvider().apply(submission);
    assertTrue(designButton.getStyleName().contains(EXPERIMENT));
    assertTrue(design.addSubmission.getStyleName().contains(ADD_SUBMISSION));
    assertTrue(design.updateStatusButton.getStyleName().contains(UPDATE_STATUS));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.headerLabel.getValue());
    assertEquals(resources.message(HELP), design.help.getCaption());
    assertEquals(VaadinIcons.QUESTION_CIRCLE_O, design.help.getIcon());
    for (Column<Submission, ?> column : design.submissionsGrid.getColumns()) {
      assertEquals(resources.message(column.getId()), column.getCaption());
    }
    Submission manyStatuses = repository.findOne(153L);
    assertEquals(statusesValue(manyStatuses),
        design.submissionsGrid.getColumn(SAMPLE_STATUSES).getValueProvider().apply(manyStatuses));
    assertEquals(resources.message(ADD_SUBMISSION), design.addSubmission.getCaption());
    assertEquals(resources.message(UPDATE_STATUS), design.updateStatusButton.getCaption());
  }

  @Test
  public void help() {
    presenter.init(view);

    design.help.click();

    verify(helpWindow).setHelp(
        resources.message(SUBMISSIONS_DESCRIPTION, VaadinIcons.MENU.getHtml()), ContentMode.HTML);
    verify(view).addWindow(helpWindow);
  }

  @Test
  public void submissionsGrid() {
    presenter.init(view);

    final List<Column<Submission, ?>> columns = design.submissionsGrid.getColumns();
    final List<GridSortOrder<Submission>> sortOrders = design.submissionsGrid.getSortOrder();

    final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    assertEquals(EXPERIMENT, columns.get(0).getId());
    assertTrue(containsInstanceOf(design.submissionsGrid.getColumn(EXPERIMENT).getExtensions(),
        ComponentRenderer.class));
    assertEquals(resources.message(EXPERIMENT),
        design.submissionsGrid.getColumn(EXPERIMENT).getCaption());
    for (Submission submission : submissions) {
      Button experimentButton = (Button) design.submissionsGrid.getColumn(EXPERIMENT)
          .getValueProvider().apply(submission);
      assertTrue(experimentButton.getStyleName().contains(EXPERIMENT));
      if (submission.getService() == Service.SMALL_MOLECULE) {
        assertEquals(submission.getSamples().get(0).getName(), experimentButton.getCaption());
      } else {
        assertEquals(submission.getExperiment(), experimentButton.getCaption());
      }
    }
    assertFalse(design.submissionsGrid.getColumn(EXPERIMENT).isHidable());
    assertFalse(design.submissionsGrid.getColumn(EXPERIMENT).isHidden());
    assertTrue(design.submissionsGrid.getColumn(EXPERIMENT).isSortable());
    Collator collator = Collator.getInstance(locale);
    Comparator<Submission> experimentComparator =
        (s1, s2) -> collator.compare(Objects.toString(s1.getExperiment(), ""),
            Objects.toString(s2.getExperiment(), ""));
    List<Submission> expectedSortedSubmissions = new ArrayList<>(submissions);
    List<Submission> sortedSubmissions = new ArrayList<>(submissions);
    expectedSortedSubmissions.sort(experimentComparator);
    sortedSubmissions
        .sort(design.submissionsGrid.getColumn(EXPERIMENT).getComparator(SortDirection.ASCENDING));
    assertEquals(expectedSortedSubmissions, sortedSubmissions);
    expectedSortedSubmissions.sort(experimentComparator.reversed());
    sortedSubmissions
        .sort(design.submissionsGrid.getColumn(EXPERIMENT).getComparator(SortDirection.DESCENDING));
    assertEquals(expectedSortedSubmissions, sortedSubmissions);
    assertEquals(USER, columns.get(1).getId());
    assertEquals(resources.message(USER), design.submissionsGrid.getColumn(USER).getCaption());
    for (Submission submission : submissions) {
      assertEquals(submission.getUser().getName(),
          design.submissionsGrid.getColumn(USER).getValueProvider().apply(submission));
      assertEquals(submission.getUser().getEmail(),
          design.submissionsGrid.getColumn(USER).getDescriptionGenerator().apply(submission));
    }
    assertFalse(design.submissionsGrid.getColumn(USER).isHidable());
    assertTrue(design.submissionsGrid.getColumn(USER).isHidden());
    assertTrue(design.submissionsGrid.getColumn(USER).isSortable());
    assertEquals(DIRECTOR, columns.get(2).getId());
    assertEquals(resources.message(DIRECTOR),
        design.submissionsGrid.getColumn(DIRECTOR).getCaption());
    for (Submission submission : submissions) {
      assertEquals(submission.getLaboratory().getDirector(),
          design.submissionsGrid.getColumn(DIRECTOR).getValueProvider().apply(submission));
    }
    assertFalse(design.submissionsGrid.getColumn(DIRECTOR).isHidable());
    assertTrue(design.submissionsGrid.getColumn(DIRECTOR).isHidden());
    assertTrue(design.submissionsGrid.getColumn(DIRECTOR).isSortable());
    assertEquals(SERVICE, columns.get(3).getId());
    assertEquals(resources.message(SERVICE),
        design.submissionsGrid.getColumn(SERVICE).getCaption());
    for (Submission submission : submissions) {
      assertEquals(submission.getService().getLabel(locale),
          design.submissionsGrid.getColumn(SERVICE).getValueProvider().apply(submission));
    }
    assertTrue(design.submissionsGrid.getColumn(SERVICE).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SERVICE).isSortable());
    assertEquals(SAMPLE_DELIVERY_DATE, columns.get(4).getId());
    assertEquals(resources.message(SAMPLE_DELIVERY_DATE),
        design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE).getCaption());
    assertTrue(
        containsInstanceOf(design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE).getExtensions(),
            ComponentRenderer.class));
    for (Submission submission : submissions) {
      Label label = (Label) design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE)
          .getValueProvider().apply(submission);
      assertTrue(label.getStyleName().contains(SAMPLE_DELIVERY_DATE));
      if (submission.getSampleDeliveryDate() != null) {
        assertEquals(dateFormatter.format(submission.getSampleDeliveryDate()), label.getValue());
      }
    }
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE).isSortable());
    assertEquals(DIGESTION_DATE, columns.get(5).getId());
    assertEquals(resources.message(DIGESTION_DATE),
        design.submissionsGrid.getColumn(DIGESTION_DATE).getCaption());
    assertTrue(containsInstanceOf(design.submissionsGrid.getColumn(DIGESTION_DATE).getExtensions(),
        ComponentRenderer.class));
    for (Submission submission : submissions) {
      Label label = (Label) design.submissionsGrid.getColumn(DIGESTION_DATE).getValueProvider()
          .apply(submission);
      assertTrue(label.getStyleName().contains(DIGESTION_DATE));
      if (submission.getDigestionDate() != null) {
        assertEquals(resources.message(EXPECTED_DATE,
            predictedStatus(submission, SampleStatus.DIGESTED) ? VaadinIcons.CLOCK.getHtml()
                : VaadinIcons.CHECK.getHtml(),
            dateFormatter.format(submission.getDigestionDate())), label.getValue());
      }
    }
    assertTrue(design.submissionsGrid.getColumn(DIGESTION_DATE).isHidable());
    assertTrue(design.submissionsGrid.getColumn(DIGESTION_DATE).isSortable());
    assertEquals(ANALYSIS_DATE, columns.get(6).getId());
    assertEquals(resources.message(ANALYSIS_DATE),
        design.submissionsGrid.getColumn(ANALYSIS_DATE).getCaption());
    assertTrue(containsInstanceOf(design.submissionsGrid.getColumn(ANALYSIS_DATE).getExtensions(),
        ComponentRenderer.class));
    for (Submission submission : submissions) {
      Label label = (Label) design.submissionsGrid.getColumn(ANALYSIS_DATE).getValueProvider()
          .apply(submission);
      assertTrue(label.getStyleName().contains(ANALYSIS_DATE));
      if (submission.getAnalysisDate() != null) {
        assertEquals(resources.message(EXPECTED_DATE,
            predictedStatus(submission, SampleStatus.CANCELLED) ? VaadinIcons.CLOCK.getHtml()
                : VaadinIcons.CHECK.getHtml(),
            dateFormatter.format(submission.getAnalysisDate())), label.getValue());
      }
    }
    assertTrue(design.submissionsGrid.getColumn(ANALYSIS_DATE).isHidable());
    assertTrue(design.submissionsGrid.getColumn(ANALYSIS_DATE).isSortable());
    assertEquals(DATA_AVAILABLE_DATE, columns.get(7).getId());
    assertEquals(resources.message(DATA_AVAILABLE_DATE),
        design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE).getCaption());
    assertTrue(
        containsInstanceOf(design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE).getExtensions(),
            ComponentRenderer.class));
    for (Submission submission : submissions) {
      Label label = (Label) design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE).getValueProvider()
          .apply(submission);
      assertTrue(label.getStyleName().contains(DATA_AVAILABLE_DATE));
      if (submission.getDataAvailableDate() != null) {
        assertEquals(dateFormatter.format(submission.getDataAvailableDate()), label.getValue());
      }
    }
    assertTrue(design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE).isHidable());
    assertTrue(design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE).isSortable());
    assertEquals(INSTRUMENT, columns.get(8).getId());
    assertEquals(resources.message(INSTRUMENT),
        design.submissionsGrid.getColumn(INSTRUMENT).getCaption());
    for (Submission submission : submissions) {
      assertEquals(
          submission.getMassDetectionInstrument() != null
              ? submission.getMassDetectionInstrument().getLabel(locale)
              : MassDetectionInstrument.getNullLabel(locale),
          design.submissionsGrid.getColumn(INSTRUMENT).getValueProvider().apply(submission));
    }
    assertTrue(design.submissionsGrid.getColumn(INSTRUMENT).isHidable());
    assertTrue(design.submissionsGrid.getColumn(INSTRUMENT).isSortable());
    assertEquals(SAMPLE_COUNT, columns.get(9).getId());
    assertEquals(resources.message(SAMPLE_COUNT),
        design.submissionsGrid.getColumn(SAMPLE_COUNT).getCaption());
    for (Submission submission : submissions) {
      assertEquals(submission.getSamples().size(),
          design.submissionsGrid.getColumn(SAMPLE_COUNT).getValueProvider().apply(submission));
    }
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isSortable());
    assertEquals(SAMPLE_NAME, columns.get(10).getId());
    assertEquals(resources.message(SAMPLE_NAME),
        design.submissionsGrid.getColumn(SAMPLE_NAME).getCaption());
    for (Submission submission : submissions) {
      assertEquals(
          resources.message(SAMPLE_NAME + ".value", submission.getSamples().get(0).getName(),
              submission.getSamples().size()),
          design.submissionsGrid.getColumn(SAMPLE_NAME).getValueProvider().apply(submission));
      assertEquals(
          submission.getSamples().stream().map(sample -> sample.getName()).sorted(collator)
              .collect(Collectors.joining("\n")),
          design.submissionsGrid.getColumn(SAMPLE_NAME).getDescriptionGenerator()
              .apply(submission));
    }
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_NAME).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SAMPLE_NAME).isSortable());
    assertEquals(SAMPLE_STATUSES, columns.get(11).getId());
    assertEquals(resources.message(SAMPLE_STATUSES),
        design.submissionsGrid.getColumn(SAMPLE_STATUSES).getCaption());
    for (Submission submission : submissions) {
      assertEquals(
          submission.getSamples().stream().map(sample -> sample.getStatus()).distinct().sorted()
              .map(status -> status.getLabel(locale))
              .collect(Collectors.joining(resources.message(SAMPLE_STATUSES_SEPARATOR))),
          design.submissionsGrid.getColumn(SAMPLE_STATUSES).getValueProvider().apply(submission));
      assertEquals(
          submission.getSamples().stream().map(sample -> sample.getStatus()).distinct().sorted()
              .map(status -> status.getLabel(locale)).collect(Collectors.joining("\n")),
          design.submissionsGrid.getColumn(SAMPLE_STATUSES).getDescriptionGenerator()
              .apply(submission));
    }
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isSortable());
    assertEquals(DATE, columns.get(12).getId());
    assertEquals(resources.message(DATE), design.submissionsGrid.getColumn(DATE).getCaption());
    final DateTimeFormatter instantFormatter =
        DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault());
    for (Submission submission : submissions) {
      assertEquals(instantFormatter.format(submission.getSubmissionDate()),
          design.submissionsGrid.getColumn(DATE).getValueProvider().apply(submission));
    }
    assertTrue(design.submissionsGrid.getColumn(DATE).isHidable());
    assertTrue(design.submissionsGrid.getColumn(DATE).isSortable());
    assertEquals(HIDDEN, columns.get(13).getId());
    assertEquals(resources.message(HIDDEN), design.submissionsGrid.getColumn(HIDDEN).getCaption());
    for (Submission submission : submissions) {
      Button hiddenButton =
          (Button) design.submissionsGrid.getColumn(HIDDEN).getValueProvider().apply(submission);
      assertTrue(hiddenButton.getStyleName()
          .contains(submission.isHidden() ? ValoTheme.BUTTON_DANGER : ValoTheme.BUTTON_FRIENDLY));
      assertTrue(hiddenButton.getStyleName().contains(HIDDEN));
      assertEquals(submission.isHidden() ? VaadinIcons.EYE_SLASH : VaadinIcons.EYE,
          hiddenButton.getIcon());
      assertEquals(resources.message(property(HIDDEN, submission.isHidden())),
          hiddenButton.getIconAlternateText());
    }
    assertFalse(design.submissionsGrid.getColumn(HIDDEN).isHidable());
    assertTrue(design.submissionsGrid.getColumn(HIDDEN).isHidden());
    assertTrue(design.submissionsGrid.getColumn(HIDDEN).isSortable());
    assertEquals(HISTORY, columns.get(14).getId());
    assertTrue(containsInstanceOf(design.submissionsGrid.getColumn(HISTORY).getExtensions(),
        ComponentRenderer.class));
    assertEquals(resources.message(HISTORY),
        design.submissionsGrid.getColumn(HISTORY).getCaption());
    for (Submission submission : submissions) {
      Button historyButton =
          (Button) design.submissionsGrid.getColumn(HISTORY).getValueProvider().apply(submission);
      assertTrue(historyButton.getStyleName().contains(HISTORY));
      assertEquals(resources.message(HISTORY), historyButton.getCaption());
    }
    assertFalse(design.submissionsGrid.getColumn(HISTORY).isHidable());
    assertTrue(design.submissionsGrid.getColumn(HISTORY).isHidden());
    assertFalse(design.submissionsGrid.getColumn(HISTORY).isSortable());
    assertEquals(1, design.submissionsGrid.getFrozenColumnCount());
    for (Submission submission : submissions) {
      assertEquals(submission.isHidden() ? "submission-hidden" : null,
          design.submissionsGrid.getStyleGenerator().apply(submission));
    }
    assertFalse(sortOrders.isEmpty());
    GridSortOrder<Submission> sortOrder = sortOrders.get(0);
    assertEquals(DATE, sortOrder.getSorted().getId());
    assertEquals(SortDirection.DESCENDING, sortOrder.getDirection());
  }

  @Test
  public void submissionsGrid_Manager() {
    when(authorizationService.hasManagerRole()).thenReturn(true);
    presenter.init(view);

    assertFalse(design.submissionsGrid.getColumn(EXPERIMENT).isHidable());
    assertFalse(design.submissionsGrid.getColumn(EXPERIMENT).isHidden());
    assertTrue(design.submissionsGrid.getColumn(USER).isHidable());
    assertTrue(design.submissionsGrid.getColumn(DIRECTOR).isHidden());
    assertTrue(design.submissionsGrid.getColumn(SERVICE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SERVICE).isHidden());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE).isHidden());
    assertTrue(design.submissionsGrid.getColumn(DIGESTION_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(DIGESTION_DATE).isHidden());
    assertTrue(design.submissionsGrid.getColumn(ANALYSIS_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(ANALYSIS_DATE).isHidden());
    assertTrue(design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE).isHidden());
    assertTrue(design.submissionsGrid.getColumn(INSTRUMENT).isHidable());
    assertFalse(design.submissionsGrid.getColumn(INSTRUMENT).isHidden());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_NAME).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isHidable());
    assertTrue(design.submissionsGrid.getColumn(DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(HIDDEN).isHidable());
    assertTrue(design.submissionsGrid.getColumn(HIDDEN).isHidden());
    assertFalse(design.submissionsGrid.getColumn(HISTORY).isHidable());
    assertTrue(design.submissionsGrid.getColumn(HISTORY).isHidden());
  }

  @Test
  public void submissionsGrid_Admin() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    assertFalse(design.submissionsGrid.getColumn(EXPERIMENT).isHidable());
    assertFalse(design.submissionsGrid.getColumn(EXPERIMENT).isHidden());
    assertTrue(design.submissionsGrid.getColumn(USER).isHidable());
    assertTrue(design.submissionsGrid.getColumn(DIRECTOR).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SERVICE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SERVICE).isHidden());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE).isHidden());
    assertTrue(design.submissionsGrid.getColumn(DIGESTION_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(DIGESTION_DATE).isHidden());
    assertTrue(design.submissionsGrid.getColumn(ANALYSIS_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(ANALYSIS_DATE).isHidden());
    assertTrue(design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE).isHidden());
    assertTrue(design.submissionsGrid.getColumn(INSTRUMENT).isHidable());
    assertFalse(design.submissionsGrid.getColumn(INSTRUMENT).isHidden());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_NAME).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isHidable());
    assertTrue(design.submissionsGrid.getColumn(DATE).isHidable());
    assertTrue(design.submissionsGrid.getColumn(HIDDEN).isHidable());
    assertFalse(design.submissionsGrid.getColumn(HIDDEN).isHidden());
    assertTrue(design.submissionsGrid.getColumn(HISTORY).isHidable());
    assertFalse(design.submissionsGrid.getColumn(HISTORY).isHidden());
  }

  @Test
  public void submissionsGrid_HiddenColumn() {
    when(userPreferenceService.get(any(), eq(SAMPLE_COUNT), any())).thenReturn(true);
    presenter.init(view);

    assertFalse(design.submissionsGrid.getColumn(EXPERIMENT).isHidable());
    assertFalse(design.submissionsGrid.getColumn(EXPERIMENT).isHidden());
    assertFalse(design.submissionsGrid.getColumn(USER).isHidable());
    assertTrue(design.submissionsGrid.getColumn(USER).isHidden());
    assertFalse(design.submissionsGrid.getColumn(DIRECTOR).isHidable());
    assertTrue(design.submissionsGrid.getColumn(DIRECTOR).isHidden());
    assertTrue(design.submissionsGrid.getColumn(SERVICE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SERVICE).isHidden());
    verify(userPreferenceService).get(presenter, SERVICE, false);
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE).isHidden());
    verify(userPreferenceService).get(presenter, SAMPLE_DELIVERY_DATE, false);
    assertTrue(design.submissionsGrid.getColumn(DIGESTION_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(DIGESTION_DATE).isHidden());
    verify(userPreferenceService).get(presenter, DIGESTION_DATE, false);
    assertTrue(design.submissionsGrid.getColumn(ANALYSIS_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(ANALYSIS_DATE).isHidden());
    verify(userPreferenceService).get(presenter, ANALYSIS_DATE, false);
    assertTrue(design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE).isHidden());
    verify(userPreferenceService).get(presenter, DATA_AVAILABLE_DATE, false);
    assertTrue(design.submissionsGrid.getColumn(INSTRUMENT).isHidable());
    assertFalse(design.submissionsGrid.getColumn(INSTRUMENT).isHidden());
    verify(userPreferenceService).get(presenter, INSTRUMENT, false);
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidden());
    verify(userPreferenceService).get(presenter, SAMPLE_COUNT, false);
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_NAME).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SAMPLE_NAME).isHidden());
    verify(userPreferenceService).get(presenter, SAMPLE_NAME, false);
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isHidden());
    verify(userPreferenceService).get(presenter, SAMPLE_STATUSES, false);
    assertTrue(design.submissionsGrid.getColumn(DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(DATE).isHidden());
    verify(userPreferenceService).get(presenter, DATE, false);
    assertFalse(design.submissionsGrid.getColumn(HIDDEN).isHidable());
    assertTrue(design.submissionsGrid.getColumn(HIDDEN).isHidden());
    assertFalse(design.submissionsGrid.getColumn(HISTORY).isHidable());
    assertTrue(design.submissionsGrid.getColumn(HISTORY).isHidden());
  }

  @Test
  public void submissionsGrid_HiddenColumnAdmin() {
    when(userPreferenceService.get(any(), eq(SAMPLE_COUNT), any())).thenReturn(true);
    when(userPreferenceService.get(any(), eq(HISTORY), any())).thenReturn(true);
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    assertFalse(design.submissionsGrid.getColumn(EXPERIMENT).isHidable());
    assertFalse(design.submissionsGrid.getColumn(EXPERIMENT).isHidden());
    assertTrue(design.submissionsGrid.getColumn(USER).isHidable());
    assertFalse(design.submissionsGrid.getColumn(USER).isHidden());
    verify(userPreferenceService).get(presenter, USER, false);
    assertFalse(design.submissionsGrid.getColumn(DIRECTOR).isHidden());
    verify(userPreferenceService).get(presenter, DIRECTOR, false);
    assertTrue(design.submissionsGrid.getColumn(SERVICE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SERVICE).isHidden());
    verify(userPreferenceService).get(presenter, SERVICE, false);
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE).isHidden());
    verify(userPreferenceService).get(presenter, SAMPLE_DELIVERY_DATE, false);
    assertTrue(design.submissionsGrid.getColumn(DIGESTION_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(DIGESTION_DATE).isHidden());
    verify(userPreferenceService).get(presenter, DIGESTION_DATE, false);
    assertTrue(design.submissionsGrid.getColumn(ANALYSIS_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(ANALYSIS_DATE).isHidden());
    verify(userPreferenceService).get(presenter, ANALYSIS_DATE, false);
    assertTrue(design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE).isHidden());
    verify(userPreferenceService).get(presenter, DATA_AVAILABLE_DATE, false);
    assertTrue(design.submissionsGrid.getColumn(INSTRUMENT).isHidable());
    assertFalse(design.submissionsGrid.getColumn(INSTRUMENT).isHidden());
    verify(userPreferenceService).get(presenter, INSTRUMENT, false);
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidden());
    verify(userPreferenceService).get(presenter, SAMPLE_COUNT, false);
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_NAME).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SAMPLE_NAME).isHidden());
    verify(userPreferenceService).get(presenter, SAMPLE_NAME, false);
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isHidden());
    verify(userPreferenceService).get(presenter, SAMPLE_STATUSES, false);
    assertTrue(design.submissionsGrid.getColumn(DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(DATE).isHidden());
    verify(userPreferenceService).get(presenter, DATE, false);
    assertTrue(design.submissionsGrid.getColumn(HIDDEN).isHidable());
    assertFalse(design.submissionsGrid.getColumn(HIDDEN).isHidden());
    verify(userPreferenceService).get(presenter, HIDDEN, false);
    assertTrue(design.submissionsGrid.getColumn(HISTORY).isHidable());
    assertTrue(design.submissionsGrid.getColumn(HISTORY).isHidden());
    verify(userPreferenceService).get(presenter, HISTORY, false);
  }

  @Test
  public void submissionsGrid_HideColumn() {
    presenter.init(view);
    design.submissionsGrid.getColumn(SAMPLE_COUNT).setHidden(true);

    verify(userPreferenceService).save(presenter, SAMPLE_COUNT, true);
  }

  @Test
  public void submissionsGrid_ColumnOrder() {
    String[] columnOrder = new String[] { EXPERIMENT, USER, DIRECTOR, SERVICE, SAMPLE_DELIVERY_DATE,
        DIGESTION_DATE, ANALYSIS_DATE, DATA_AVAILABLE_DATE, INSTRUMENT, SAMPLE_NAME, SAMPLE_COUNT,
        SAMPLE_STATUSES, DATE, HIDDEN, HISTORY };
    when(userPreferenceService.get(any(), eq(COLUMN_ORDER), any())).thenReturn(columnOrder);
    presenter.init(view);

    assertEquals(EXPERIMENT, design.submissionsGrid.getColumns().get(0).getId());
    assertEquals(USER, design.submissionsGrid.getColumns().get(1).getId());
    assertEquals(DIRECTOR, design.submissionsGrid.getColumns().get(2).getId());
    assertEquals(SERVICE, design.submissionsGrid.getColumns().get(3).getId());
    assertEquals(SAMPLE_DELIVERY_DATE, design.submissionsGrid.getColumns().get(4).getId());
    assertEquals(DIGESTION_DATE, design.submissionsGrid.getColumns().get(5).getId());
    assertEquals(ANALYSIS_DATE, design.submissionsGrid.getColumns().get(6).getId());
    assertEquals(DATA_AVAILABLE_DATE, design.submissionsGrid.getColumns().get(7).getId());
    assertEquals(INSTRUMENT, design.submissionsGrid.getColumns().get(8).getId());
    assertEquals(SAMPLE_NAME, design.submissionsGrid.getColumns().get(9).getId());
    assertEquals(SAMPLE_COUNT, design.submissionsGrid.getColumns().get(10).getId());
    assertEquals(SAMPLE_STATUSES, design.submissionsGrid.getColumns().get(11).getId());
    assertEquals(DATE, design.submissionsGrid.getColumns().get(12).getId());
    assertEquals(HIDDEN, design.submissionsGrid.getColumns().get(13).getId());
    assertEquals(HISTORY, design.submissionsGrid.getColumns().get(14).getId());
    String[] defaultColumnOrder = new String[] { EXPERIMENT, USER, DIRECTOR, SERVICE,
        SAMPLE_DELIVERY_DATE, DIGESTION_DATE, ANALYSIS_DATE, DATA_AVAILABLE_DATE, INSTRUMENT,
        SAMPLE_COUNT, SAMPLE_NAME, SAMPLE_STATUSES, DATE, HIDDEN, HISTORY };
    verify(userPreferenceService).get(presenter, COLUMN_ORDER, defaultColumnOrder);
  }

  @Test
  public void submissionsGrid_ColumnOrder_MissingHidden() {
    String[] columnOrder = new String[] { EXPERIMENT, USER, DIRECTOR, SERVICE, SAMPLE_DELIVERY_DATE,
        DIGESTION_DATE, ANALYSIS_DATE, DATA_AVAILABLE_DATE, INSTRUMENT, SAMPLE_NAME, SAMPLE_COUNT,
        SAMPLE_STATUSES, DATE, HISTORY };
    when(userPreferenceService.get(any(), eq(COLUMN_ORDER), any())).thenReturn(columnOrder);
    presenter.init(view);

    assertEquals(EXPERIMENT, design.submissionsGrid.getColumns().get(0).getId());
    assertEquals(USER, design.submissionsGrid.getColumns().get(1).getId());
    assertEquals(DIRECTOR, design.submissionsGrid.getColumns().get(2).getId());
    assertEquals(SERVICE, design.submissionsGrid.getColumns().get(3).getId());
    assertEquals(SAMPLE_DELIVERY_DATE, design.submissionsGrid.getColumns().get(4).getId());
    assertEquals(DIGESTION_DATE, design.submissionsGrid.getColumns().get(5).getId());
    assertEquals(ANALYSIS_DATE, design.submissionsGrid.getColumns().get(6).getId());
    assertEquals(DATA_AVAILABLE_DATE, design.submissionsGrid.getColumns().get(7).getId());
    assertEquals(INSTRUMENT, design.submissionsGrid.getColumns().get(8).getId());
    assertEquals(SAMPLE_NAME, design.submissionsGrid.getColumns().get(9).getId());
    assertEquals(SAMPLE_COUNT, design.submissionsGrid.getColumns().get(10).getId());
    assertEquals(SAMPLE_STATUSES, design.submissionsGrid.getColumns().get(11).getId());
    assertEquals(DATE, design.submissionsGrid.getColumns().get(12).getId());
    assertEquals(HISTORY, design.submissionsGrid.getColumns().get(13).getId());
    assertEquals(HIDDEN, design.submissionsGrid.getColumns().get(14).getId());
    String[] defaultColumnOrder = new String[] { EXPERIMENT, USER, DIRECTOR, SERVICE,
        SAMPLE_DELIVERY_DATE, DIGESTION_DATE, ANALYSIS_DATE, DATA_AVAILABLE_DATE, INSTRUMENT,
        SAMPLE_COUNT, SAMPLE_NAME, SAMPLE_STATUSES, DATE, HIDDEN, HISTORY };
    verify(userPreferenceService).get(presenter, COLUMN_ORDER, defaultColumnOrder);
  }

  @Test
  public void submissionsGrid_ColumnOrder_InvalidColumn() {
    String[] columnOrder = new String[] { EXPERIMENT, USER, DIRECTOR, SERVICE, "invalid_column",
        SAMPLE_DELIVERY_DATE, ANALYSIS_DATE, DATA_AVAILABLE_DATE, INSTRUMENT, SAMPLE_NAME,
        SAMPLE_COUNT, SAMPLE_STATUSES, DATE, HIDDEN, HISTORY };
    when(userPreferenceService.get(any(), eq(COLUMN_ORDER), any())).thenReturn(columnOrder);
    presenter.init(view);

    assertEquals(EXPERIMENT, design.submissionsGrid.getColumns().get(0).getId());
    assertEquals(USER, design.submissionsGrid.getColumns().get(1).getId());
    assertEquals(DIRECTOR, design.submissionsGrid.getColumns().get(2).getId());
    assertEquals(SERVICE, design.submissionsGrid.getColumns().get(3).getId());
    assertEquals(SAMPLE_DELIVERY_DATE, design.submissionsGrid.getColumns().get(4).getId());
    assertEquals(DIGESTION_DATE, design.submissionsGrid.getColumns().get(5).getId());
    assertEquals(ANALYSIS_DATE, design.submissionsGrid.getColumns().get(6).getId());
    assertEquals(DATA_AVAILABLE_DATE, design.submissionsGrid.getColumns().get(7).getId());
    assertEquals(INSTRUMENT, design.submissionsGrid.getColumns().get(8).getId());
    assertEquals(SAMPLE_COUNT, design.submissionsGrid.getColumns().get(9).getId());
    assertEquals(SAMPLE_NAME, design.submissionsGrid.getColumns().get(10).getId());
    assertEquals(SAMPLE_STATUSES, design.submissionsGrid.getColumns().get(11).getId());
    assertEquals(DATE, design.submissionsGrid.getColumns().get(12).getId());
    assertEquals(HIDDEN, design.submissionsGrid.getColumns().get(13).getId());
    assertEquals(HISTORY, design.submissionsGrid.getColumns().get(14).getId());
    String[] defaultColumnOrder = new String[] { EXPERIMENT, USER, DIRECTOR, SERVICE,
        SAMPLE_DELIVERY_DATE, DIGESTION_DATE, ANALYSIS_DATE, DATA_AVAILABLE_DATE, INSTRUMENT,
        SAMPLE_COUNT, SAMPLE_NAME, SAMPLE_STATUSES, DATE, HIDDEN, HISTORY };
    verify(userPreferenceService).get(presenter, COLUMN_ORDER, defaultColumnOrder);
  }

  @Test
  public void submissionsGrid_ChangeColumnOrder() {
    presenter.init(view);
    design.submissionsGrid.setColumnOrder(EXPERIMENT, USER, DIRECTOR, SERVICE, SAMPLE_DELIVERY_DATE,
        DIGESTION_DATE, ANALYSIS_DATE, DATA_AVAILABLE_DATE, INSTRUMENT, SAMPLE_NAME, SAMPLE_COUNT,
        SAMPLE_STATUSES, DATE, HIDDEN, HISTORY);

    String[] columnOrder =
        design.submissionsGrid.getColumns().stream().map(col -> col.getId()).toArray(String[]::new);
    verify(userPreferenceService).save(presenter, COLUMN_ORDER, columnOrder);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_ExperimentAsc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(EXPERIMENT, SortDirection.ASCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.experiment.asc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_ExperimentDesc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(EXPERIMENT, SortDirection.DESCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.experiment.desc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_UserAsc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(USER, SortDirection.ASCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.user.name.asc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_UserDesc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(USER, SortDirection.DESCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.user.name.desc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_DirectorAsc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(DIRECTOR, SortDirection.ASCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.laboratory.director.asc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_DirectorDesc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(DIRECTOR, SortDirection.DESCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.laboratory.director.desc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_ServiceAsc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(SERVICE, SortDirection.ASCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.service.asc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_ServiceDesc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(SERVICE, SortDirection.DESCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.service.desc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_SampleDeliveryDateAsc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(SAMPLE_DELIVERY_DATE, SortDirection.ASCENDING)), null,
        null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.sampleDeliveryDate.asc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_SampleDeliveryDateDesc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(SAMPLE_DELIVERY_DATE, SortDirection.DESCENDING)), null,
        null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.sampleDeliveryDate.desc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_DigestionDateAsc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(DIGESTION_DATE, SortDirection.ASCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.digestionDate.asc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_DigestionDateDesc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(DIGESTION_DATE, SortDirection.DESCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.digestionDate.desc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_AnalysisDateAsc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(ANALYSIS_DATE, SortDirection.ASCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.analysisDate.asc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_AnalysisDateDesc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(ANALYSIS_DATE, SortDirection.DESCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.analysisDate.desc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_DataAvailableDateAsc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(DATA_AVAILABLE_DATE, SortDirection.ASCENDING)), null,
        null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.dataAvailableDate.asc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_DataAvailableDateDesc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(DATA_AVAILABLE_DATE, SortDirection.DESCENDING)), null,
        null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.dataAvailableDate.desc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_InstrumentAsc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(INSTRUMENT, SortDirection.ASCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.massDetectionInstrument.asc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_InstrumentDesc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(INSTRUMENT, SortDirection.DESCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.massDetectionInstrument.desc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_SampleCountAsc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(SAMPLE_COUNT, SortDirection.ASCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.samples.size().asc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_SampleCountDesc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(SAMPLE_COUNT, SortDirection.DESCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.samples.size().desc(), orderSpecifier);
  }

  @Test
  @Ignore
  @SuppressWarnings("unchecked")
  public void sort_SampleNameAsc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(SAMPLE_NAME, SortDirection.ASCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.samples.any().name.asc(), orderSpecifier);
  }

  @Test
  @Ignore
  @SuppressWarnings("unchecked")
  public void sort_SampleNameDesc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(SAMPLE_NAME, SortDirection.DESCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.samples.any().name.desc(), orderSpecifier);
  }

  @Test
  @Ignore
  @SuppressWarnings("unchecked")
  public void sort_SampleStatusAsc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(SAMPLE_STATUSES, SortDirection.ASCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.samples.any().status.asc(), orderSpecifier);
  }

  @Test
  @Ignore
  @SuppressWarnings("unchecked")
  public void sort_SampleStatusDesc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(SAMPLE_STATUSES, SortDirection.DESCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.samples.any().status.desc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_DateAsc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(DATE, SortDirection.ASCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.submissionDate.asc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_DateDesc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(DATE, SortDirection.DESCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.submissionDate.desc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_HiddenAsc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(HIDDEN, SortDirection.ASCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.hidden.asc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sort_HiddenDesc() {
    presenter.init(view);
    DataProvider<Submission, Void> dataProvider =
        (DataProvider<Submission, Void>) design.submissionsGrid.getDataProvider();
    Query<Submission, Void> query = new Query<>(0, Integer.MAX_VALUE,
        Arrays.asList(new QuerySortOrder(HIDDEN, SortDirection.DESCENDING)), null, null);
    dataProvider.fetch(query);

    verify(submissionService, atLeastOnce()).all(submissionFilterCaptor.capture());
    SubmissionFilter submissionFilter = submissionFilterCaptor.getValue();
    assertEquals(1, submissionFilter.sortOrders.size());
    OrderSpecifier<?> orderSpecifier = submissionFilter.sortOrders.get(0);
    assertEquals(submission.hidden.desc(), orderSpecifier);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void experimentFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EXPERIMENT);
    TextField textField = (TextField) cell.getComponent();
    assertEquals(resources.message(ALL), textField.getPlaceholder());
    String filterValue = "test";
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    verify(submissionsDataProvider).refreshAll();
    SubmissionFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.experimentContains);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void userFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(USER);
    TextField textField = (TextField) cell.getComponent();
    assertEquals(resources.message(ALL), textField.getPlaceholder());
    String filterValue = "test";
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    verify(submissionsDataProvider).refreshAll();
    SubmissionFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.userContains);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void directorFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(DIRECTOR);
    TextField textField = (TextField) cell.getComponent();
    assertEquals(resources.message(ALL), textField.getPlaceholder());
    String filterValue = "test";
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    verify(submissionsDataProvider).refreshAll();
    SubmissionFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.directorContains);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void serviceFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(SERVICE);
    ComboBox<Service> field = (ComboBox<Service>) cell.getComponent();
    assertEquals(resources.message(ALL), field.getEmptySelectionCaption());
    assertEquals(resources.message(ALL), field.getPlaceholder());
    List<Service> services = items(field);
    for (Service service : services) {
      assertEquals(service.getLabel(locale), field.getItemCaptionGenerator().apply(service));
    }
    Service filterValue = Service.LC_MS_MS;

    field.setValue(filterValue);

    verify(submissionsDataProvider).refreshAll();
    SubmissionFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.service);
  }

  @Test
  public void sampleDeliveryDateFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);

    verify(localDateFilterComponent, atLeastOnce())
        .addSaveListener(localDateRangeSaveListenerCaptor.capture());
    HeaderCell cell = filterRow.getCell(SAMPLE_DELIVERY_DATE);
    assertTrue(cell.getComponent() instanceof LocalDateFilterComponent);

    Range<LocalDate> range = Range.open(LocalDate.now().minusDays(2), LocalDate.now());
    SaveListener<Range<LocalDate>> listener =
        localDateRangeSaveListenerCaptor.getAllValues().get(0);
    listener.saved(new SaveEvent<>(cell.getComponent(), range));

    verify(submissionsDataProvider).refreshAll();
    SubmissionFilter filter = presenter.getFilter();
    assertEquals(range, filter.sampleDeliveryDateRange);
  }

  @Test
  public void digestionDateFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);

    verify(localDateFilterComponent, atLeast(2))
        .addSaveListener(localDateRangeSaveListenerCaptor.capture());
    HeaderCell cell = filterRow.getCell(DIGESTION_DATE);
    assertTrue(cell.getComponent() instanceof LocalDateFilterComponent);

    Range<LocalDate> range = Range.open(LocalDate.now().minusDays(2), LocalDate.now());
    SaveListener<Range<LocalDate>> listener =
        localDateRangeSaveListenerCaptor.getAllValues().get(1);
    listener.saved(new SaveEvent<>(cell.getComponent(), range));

    verify(submissionsDataProvider).refreshAll();
    SubmissionFilter filter = presenter.getFilter();
    assertEquals(range, filter.digestionDateRange);
  }

  @Test
  public void analysisDateFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);

    verify(localDateFilterComponent, atLeast(3))
        .addSaveListener(localDateRangeSaveListenerCaptor.capture());
    HeaderCell cell = filterRow.getCell(ANALYSIS_DATE);
    assertTrue(cell.getComponent() instanceof LocalDateFilterComponent);

    Range<LocalDate> range = Range.open(LocalDate.now().minusDays(2), LocalDate.now());
    SaveListener<Range<LocalDate>> listener =
        localDateRangeSaveListenerCaptor.getAllValues().get(2);
    listener.saved(new SaveEvent<>(cell.getComponent(), range));

    verify(submissionsDataProvider).refreshAll();
    SubmissionFilter filter = presenter.getFilter();
    assertEquals(range, filter.analysisDateRange);
  }

  @Test
  public void dataAvailableDateFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);

    verify(localDateFilterComponent, atLeast(4))
        .addSaveListener(localDateRangeSaveListenerCaptor.capture());
    HeaderCell cell = filterRow.getCell(DATA_AVAILABLE_DATE);
    assertTrue(cell.getComponent() instanceof LocalDateFilterComponent);

    Range<LocalDate> range = Range.open(LocalDate.now().minusDays(2), LocalDate.now());
    SaveListener<Range<LocalDate>> listener =
        localDateRangeSaveListenerCaptor.getAllValues().get(3);
    listener.saved(new SaveEvent<>(cell.getComponent(), range));

    verify(submissionsDataProvider).refreshAll();
    SubmissionFilter filter = presenter.getFilter();
    assertEquals(range, filter.dataAvailableDateRange);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void instrumentFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(INSTRUMENT);
    ComboBox<MassDetectionInstrument> comboBox =
        (ComboBox<MassDetectionInstrument>) cell.getComponent();
    assertEquals(resources.message(ALL), comboBox.getEmptySelectionCaption());
    assertEquals(resources.message(ALL), comboBox.getPlaceholder());
    List<MassDetectionInstrument> instruments = items(comboBox);
    assertEquals(MassDetectionInstrument.filterChoices(), instruments);
    for (MassDetectionInstrument instrument : instruments) {
      assertEquals(instrument.getLabel(locale),
          comboBox.getItemCaptionGenerator().apply(instrument));
    }
    MassDetectionInstrument filterValue = MassDetectionInstrument.ORBITRAP_FUSION;

    comboBox.setValue(filterValue);

    verify(submissionsDataProvider).refreshAll();
    SubmissionFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.instrument);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sampleNameFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(SAMPLE_NAME);
    TextField textField = (TextField) cell.getComponent();
    assertEquals(resources.message(ALL), textField.getPlaceholder());
    String filterValue = "test";
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    verify(submissionsDataProvider).refreshAll();
    SubmissionFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.anySampleNameContains);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sampleStatusFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(SAMPLE_STATUSES);
    ComboBox<SampleStatus> comboBox = (ComboBox<SampleStatus>) cell.getComponent();
    assertEquals(resources.message(ALL), comboBox.getEmptySelectionCaption());
    assertEquals(resources.message(ALL), comboBox.getPlaceholder());
    List<SampleStatus> statuses = items(comboBox);
    for (SampleStatus status : statuses) {
      assertEquals(status.getLabel(locale), comboBox.getItemCaptionGenerator().apply(status));
    }
    SampleStatus filterValue = SampleStatus.ANALYSED;

    comboBox.setValue(filterValue);

    verify(submissionsDataProvider).refreshAll();
    SubmissionFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.anySampleStatus);
  }

  @Test
  public void dateFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);

    verify(localDateFilterComponent, atLeastOnce())
        .addSaveListener(localDateRangeSaveListenerCaptor.capture());
    HeaderCell cell = filterRow.getCell(DATE);
    assertTrue(cell.getComponent() instanceof LocalDateFilterComponent);

    Range<LocalDate> range = Range.open(LocalDate.now().minusDays(2), LocalDate.now());
    SaveListener<Range<LocalDate>> listener = localDateRangeSaveListenerCaptor.getValue();
    listener.saved(new SaveEvent<>(cell.getComponent(), range));

    verify(submissionsDataProvider).refreshAll();
    SubmissionFilter filter = presenter.getFilter();
    assertEquals(range, filter.dateRange);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void hiddenFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(HIDDEN);
    ComboBox<Boolean> comboBox = (ComboBox<Boolean>) cell.getComponent();
    assertEquals(resources.message(ALL), comboBox.getEmptySelectionCaption());
    assertEquals(resources.message(ALL), comboBox.getPlaceholder());
    List<Boolean> values = items(comboBox);
    for (Boolean value : values) {
      assertEquals(resources.message(property(HIDDEN, value)),
          comboBox.getItemCaptionGenerator().apply(value));
    }
    Boolean filterValue = true;

    comboBox.setValue(filterValue);

    verify(submissionsDataProvider).refreshAll();
    SubmissionFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.hidden);
  }

  @Test
  public void visible() {
    presenter.init(view);

    assertTrue(design.submissionsGrid.getSelectionModel() instanceof SelectionModel.Single);
    assertTrue(design.addSubmission.isVisible());
    assertFalse(design.updateStatusButton.isVisible());
  }

  @Test
  public void visible_Admin() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    assertTrue(design.submissionsGrid.getSelectionModel() instanceof SelectionModel.Single);
    assertFalse(design.addSubmission.isVisible());
    assertTrue(design.updateStatusButton.isVisible());
  }

  @Test
  public void defaultSubmissions() {
    presenter.init(view);
    Collection<Submission> gridSubmissions = items(design.submissionsGrid);

    Set<Long> expectedSubmissionIds =
        submissions.stream().map(s -> s.getId()).collect(Collectors.toSet());
    Set<Long> submissionIds =
        gridSubmissions.stream().map(s -> s.getId()).collect(Collectors.toSet());
    assertTrue(expectedSubmissionIds.containsAll(submissionIds));
    assertTrue(submissionIds.containsAll(expectedSubmissionIds));
  }

  @Test
  public void udpateSampleDeliveryDate() {
    presenter.init(view);
    Submission submission = submissions.get(0);
    LocalDate date = LocalDate.now().minusDays(1);
    gridStartEdit(design.submissionsGrid, submission);
    DateField field = (DateField) design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE)
        .getEditorBinding().getField();
    field.setValue(date);
    design.submissionsGrid.getEditor().save();

    verify(submissionService).update(submission, null);
    assertEquals(date, submission.getSampleDeliveryDate());
    assertEquals("yyyy-MM-dd", field.getDateFormat());
  }

  @Test
  public void udpateSampleDeliveryDate_Clear() {
    presenter.init(view);
    Submission submission = submissions.get(0);
    gridStartEdit(design.submissionsGrid, submission);
    DateField field = (DateField) design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE)
        .getEditorBinding().getField();
    field.setValue(null);
    design.submissionsGrid.getEditor().save();

    verify(submissionService).update(submission, null);
    assertNull(submission.getSampleDeliveryDate());
  }

  @Test
  public void udpateSampleDeliveryDate_Cancel() {
    presenter.init(view);
    Submission submission = submissions.get(0);
    LocalDate date = submission.getSampleDeliveryDate();
    gridStartEdit(design.submissionsGrid, submission);
    DateField field = (DateField) design.submissionsGrid.getColumn(SAMPLE_DELIVERY_DATE)
        .getEditorBinding().getField();
    field.setValue(date.minusDays(1));
    design.submissionsGrid.getEditor().cancel();

    verify(submissionService, never()).update(any(), any());
    assertEquals(date, submission.getSampleDeliveryDate());
  }

  @Test
  public void udpateDigestionDate() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    Submission submission = submissions.get(0);
    LocalDate date = LocalDate.now().minusDays(1);
    gridStartEdit(design.submissionsGrid, submission);
    DateField field =
        (DateField) design.submissionsGrid.getColumn(DIGESTION_DATE).getEditorBinding().getField();
    field.setValue(date);
    design.submissionsGrid.getEditor().save();

    verify(submissionService).update(submission, null);
    assertEquals(date, submission.getDigestionDate());
    assertEquals("yyyy-MM-dd", field.getDateFormat());
  }

  @Test
  public void udpateDigestionDate_Clear() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    Submission submission = submissions.get(0);
    gridStartEdit(design.submissionsGrid, submission);
    DateField field =
        (DateField) design.submissionsGrid.getColumn(DIGESTION_DATE).getEditorBinding().getField();
    field.setValue(null);
    design.submissionsGrid.getEditor().save();

    verify(submissionService).update(submission, null);
    assertNull(submission.getDigestionDate());
  }

  @Test
  public void udpateDigestionDate_Cancel() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    Submission submission = submissions.get(0);
    LocalDate date = submission.getDigestionDate();
    gridStartEdit(design.submissionsGrid, submission);
    DateField field =
        (DateField) design.submissionsGrid.getColumn(DIGESTION_DATE).getEditorBinding().getField();
    field.setValue(date.minusDays(1));
    design.submissionsGrid.getEditor().cancel();

    verify(submissionService, never()).update(any(), any());
    assertEquals(date, submission.getDigestionDate());
  }

  @Test
  public void udpateAnalysisDate() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    Submission submission = submissions.get(0);
    LocalDate date = LocalDate.now().minusDays(1);
    gridStartEdit(design.submissionsGrid, submission);
    DateField field =
        (DateField) design.submissionsGrid.getColumn(ANALYSIS_DATE).getEditorBinding().getField();
    field.setValue(date);
    design.submissionsGrid.getEditor().save();

    verify(submissionService).update(submission, null);
    assertEquals(date, submission.getAnalysisDate());
    assertEquals("yyyy-MM-dd", field.getDateFormat());
  }

  @Test
  public void udpateAnalysisDate_Clear() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    Submission submission = submissions.get(0);
    gridStartEdit(design.submissionsGrid, submission);
    DateField field =
        (DateField) design.submissionsGrid.getColumn(ANALYSIS_DATE).getEditorBinding().getField();
    field.setValue(null);
    design.submissionsGrid.getEditor().save();

    verify(submissionService).update(submission, null);
    assertNull(submission.getAnalysisDate());
  }

  @Test
  public void udpateAnalysisDate_Cancel() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    Submission submission = submissions.get(0);
    LocalDate date = submission.getAnalysisDate();
    gridStartEdit(design.submissionsGrid, submission);
    DateField field =
        (DateField) design.submissionsGrid.getColumn(ANALYSIS_DATE).getEditorBinding().getField();
    field.setValue(date.minusDays(1));
    design.submissionsGrid.getEditor().cancel();

    verify(submissionService, never()).update(any(), any());
    assertEquals(date, submission.getAnalysisDate());
  }

  @Test
  public void udpateDataAvailableDate() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    Submission submission = submissions.get(0);
    LocalDate date = LocalDate.now().minusDays(1);
    gridStartEdit(design.submissionsGrid, submission);
    DateField field = (DateField) design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE)
        .getEditorBinding().getField();
    field.setValue(date);
    design.submissionsGrid.getEditor().save();

    verify(submissionService).update(submission, null);
    assertEquals(date, submission.getDataAvailableDate());
    assertEquals("yyyy-MM-dd", field.getDateFormat());
  }

  @Test
  public void udpateDataAvailableDate_Clear() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    Submission submission = submissions.get(0);
    gridStartEdit(design.submissionsGrid, submission);
    DateField field = (DateField) design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE)
        .getEditorBinding().getField();
    field.setValue(null);
    design.submissionsGrid.getEditor().save();

    verify(submissionService).update(submission, null);
    assertNull(submission.getDataAvailableDate());
  }

  @Test
  public void udpateDataAvailableDate_Cancel() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    Submission submission = submissions.get(0);
    LocalDate date = submission.getDataAvailableDate();
    gridStartEdit(design.submissionsGrid, submission);
    DateField field = (DateField) design.submissionsGrid.getColumn(DATA_AVAILABLE_DATE)
        .getEditorBinding().getField();
    field.setValue(date.minusDays(1));
    design.submissionsGrid.getEditor().cancel();

    verify(submissionService, never()).update(any(), any());
    assertEquals(date, submission.getDataAvailableDate());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void udpateInstrument() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    Submission submission = submissions.get(0);
    gridStartEdit(design.submissionsGrid, submission);
    ComboBox<MassDetectionInstrument> field =
        (ComboBox<MassDetectionInstrument>) design.submissionsGrid.getColumn(INSTRUMENT)
            .getEditorBinding().getField();
    List<MassDetectionInstrument> instruments = items(field);
    assertEquals(MassDetectionInstrument.platformChoices(), instruments);
    assertEquals(MassDetectionInstrument.getNullLabel(locale), field.getEmptySelectionCaption());
    for (MassDetectionInstrument instrument : instruments) {
      assertEquals(instrument.getLabel(locale), field.getItemCaptionGenerator().apply(instrument));
    }
    MassDetectionInstrument instrument = MassDetectionInstrument.ORBITRAP_FUSION;
    field.setValue(instrument);
    design.submissionsGrid.getEditor().save();

    verify(submissionService).update(submission, null);
    assertEquals(instrument, submission.getMassDetectionInstrument());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void udpateInstrument_Clear() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    Submission submission = submissions.get(0);
    gridStartEdit(design.submissionsGrid, submission);
    ComboBox<MassDetectionInstrument> field =
        (ComboBox<MassDetectionInstrument>) design.submissionsGrid.getColumn(INSTRUMENT)
            .getEditorBinding().getField();
    field.setValue(null);
    design.submissionsGrid.getEditor().save();

    verify(submissionService).update(submission, null);
    assertNull(submission.getMassDetectionInstrument());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void udpateInstrument_Cancel() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    Submission submission = submissions.get(0);
    gridStartEdit(design.submissionsGrid, submission);
    ComboBox<MassDetectionInstrument> field =
        (ComboBox<MassDetectionInstrument>) design.submissionsGrid.getColumn(INSTRUMENT)
            .getEditorBinding().getField();
    final MassDetectionInstrument instrument = submission.getMassDetectionInstrument();
    field.setValue(MassDetectionInstrument.ORBITRAP_FUSION);
    design.submissionsGrid.getEditor().cancel();

    verify(submissionService, never()).update(submission, null);
    assertEquals(instrument, submission.getMassDetectionInstrument());
  }

  @Test
  public void udpateDigestionAndAnalysisAndDataAvailableDate() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    Submission submission = submissions.get(0);
    final LocalDate sampleDeliveryDate = LocalDate.now().minusDays(2);
    final LocalDate digestionDate = LocalDate.now().minusDays(1);
    final LocalDate analysisDate = LocalDate.now().plusDays(1);
    final LocalDate dataAvaibleDate = LocalDate.now().plusDays(2);
    gridStartEdit(design.submissionsGrid, submission);
    DateField sampleDeliveryDateField = (DateField) design.submissionsGrid
        .getColumn(SAMPLE_DELIVERY_DATE).getEditorBinding().getField();
    sampleDeliveryDateField.setValue(sampleDeliveryDate);
    DateField digestionDateField =
        (DateField) design.submissionsGrid.getColumn(DIGESTION_DATE).getEditorBinding().getField();
    digestionDateField.setValue(digestionDate);
    DateField analysisDateField =
        (DateField) design.submissionsGrid.getColumn(ANALYSIS_DATE).getEditorBinding().getField();
    analysisDateField.setValue(analysisDate);
    DateField dataAvailableDateField = (DateField) design.submissionsGrid
        .getColumn(DATA_AVAILABLE_DATE).getEditorBinding().getField();
    dataAvailableDateField.setValue(dataAvaibleDate);
    design.submissionsGrid.getEditor().save();

    verify(submissionService).update(submission, null);
    assertEquals(sampleDeliveryDate, submission.getSampleDeliveryDate());
    assertEquals(digestionDate, submission.getDigestionDate());
    assertEquals(analysisDate, submission.getAnalysisDate());
    assertEquals(dataAvaibleDate, submission.getDataAvailableDate());
  }

  @Test
  public void viewSubmission() {
    presenter.init(view);
    final Submission submission = submissions.get(0);
    Button button =
        (Button) design.submissionsGrid.getColumn(EXPERIMENT).getValueProvider().apply(submission);

    button.click();

    verify(submissionWindow).setValue(submission);
    verify(submissionWindow).center();
    verify(view).addWindow(submissionWindow);
  }

  @Test
  public void hide() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    final Submission submission = submissions.get(0);
    Button button =
        (Button) design.submissionsGrid.getColumn(HIDDEN).getValueProvider().apply(submission);

    button.click();

    verify(submissionService).hide(submission);
    assertTrue(submission.isHidden());
    verify(submissionsDataProvider).refreshItem(submission);
  }

  @Test
  public void show() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    final Submission submission = submissions.get(0);
    submission.setHidden(true);
    Button button =
        (Button) design.submissionsGrid.getColumn(HIDDEN).getValueProvider().apply(submission);

    button.click();

    verify(submissionService).show(submission);
    assertFalse(submission.isHidden());
    verify(submissionsDataProvider).refreshItem(submission);
  }

  @Test
  public void viewHistory() {
    presenter.init(view);
    final Submission submission = submissions.get(0);
    Button button =
        (Button) design.submissionsGrid.getColumn(HISTORY).getValueProvider().apply(submission);

    button.click();

    verify(submissionHistoryWindow).setValue(submission);
    verify(submissionHistoryWindow).center();
    verify(view).addWindow(submissionHistoryWindow);
  }

  @Test
  public void addSubmission() {
    presenter.init(view);

    design.addSubmission.click();

    verify(view).navigateTo(SubmissionView.VIEW_NAME);
  }

  @Test
  public void updateStatus() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    design.updateStatusButton.click();

    verify(view, never()).saveSamples(any());
    verify(view).navigateTo(SampleStatusView.VIEW_NAME);
  }

  @Test
  public void updateStatus_Selection() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    final Submission submission = find(submissions, 32L).orElse(null);
    design.submissionsGrid.select(submission);

    design.updateStatusButton.click();

    verify(view).saveSamples(samplesCaptor.capture());
    Collection<Sample> samples = samplesCaptor.getValue();
    assertEquals(submission.getSamples().size(), samples.size());
    assertTrue(samples.containsAll(submission.getSamples()));
    verify(view).navigateTo(SampleStatusView.VIEW_NAME);
  }
}
