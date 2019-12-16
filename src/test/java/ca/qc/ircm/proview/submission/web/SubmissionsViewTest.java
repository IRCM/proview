/*
 * Copyright (c) 2018 Institut de recherches cliniques de Montreal (IRCM)
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

import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIDDEN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SUBMISSION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USER;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.ADD;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.ID;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.SAMPLES_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.SAMPLES_VALUE;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.STATUS_VALUE;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.SUBMISSIONS;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.clickButton;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.doubleClickItem;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.user.LaboratoryProperties.DIRECTOR;
import static ca.qc.ircm.proview.web.WebConstants.ALL;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.ERROR;
import static ca.qc.ircm.proview.web.WebConstants.FRENCH;
import static ca.qc.ircm.proview.web.WebConstants.SUCCESS;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertArrayEquals;
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
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.web.WebConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.SelectionModel;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
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
public class SubmissionsViewTest extends AbstractViewTestCase {
  private SubmissionsView view;
  @Mock
  private SubmissionsViewPresenter presenter;
  @Mock
  private SubmissionDialog dialog;
  @Captor
  private ArgumentCaptor<ValueProvider<Submission, String>> valueProviderCaptor;
  @Captor
  private ArgumentCaptor<ComponentRenderer<Span, Submission>> spanRendererCaptor;
  @Captor
  private ArgumentCaptor<ComponentRenderer<Button, Submission>> buttonRendererCaptor;
  @Captor
  private ArgumentCaptor<Comparator<Submission>> comparatorCaptor;
  @Autowired
  private SubmissionRepository repository;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(SubmissionsView.class, locale);
  private AppResources submissionResources = new AppResources(Submission.class, locale);
  private AppResources laboratoryResources = new AppResources(Laboratory.class, locale);
  private AppResources submissionSampleResources = new AppResources(SubmissionSample.class, locale);
  private AppResources webResources = new AppResources(WebConstants.class, locale);
  private List<Submission> submissions;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    view = new SubmissionsView(presenter, dialog);
    view.init();
    submissions = repository.findAll();
  }

  @SuppressWarnings("unchecked")
  private void mockColumns() {
    Element usersElement = view.submissions.getElement();
    view.submissions = mock(Grid.class);
    when(view.submissions.getElement()).thenReturn(usersElement);
    view.experiment = mock(Column.class);
    when(view.submissions.addColumn(any(ValueProvider.class), eq(EXPERIMENT)))
        .thenReturn(view.experiment);
    when(view.experiment.setKey(any())).thenReturn(view.experiment);
    when(view.experiment.setComparator(any(Comparator.class))).thenReturn(view.experiment);
    when(view.experiment.setHeader(any(String.class))).thenReturn(view.experiment);
    view.user = mock(Column.class);
    when(view.submissions.addColumn(any(ValueProvider.class), eq(USER))).thenReturn(view.user);
    when(view.user.setKey(any())).thenReturn(view.user);
    when(view.user.setComparator(any(Comparator.class))).thenReturn(view.user);
    when(view.user.setHeader(any(String.class))).thenReturn(view.user);
    view.director = mock(Column.class);
    when(view.submissions.addColumn(any(ValueProvider.class), eq(DIRECTOR)))
        .thenReturn(view.director);
    when(view.director.setKey(any())).thenReturn(view.director);
    when(view.director.setComparator(any(Comparator.class))).thenReturn(view.director);
    when(view.director.setHeader(any(String.class))).thenReturn(view.director);
    view.dataAvailableDate = mock(Column.class);
    when(view.submissions.addColumn(any(ValueProvider.class), eq(DATA_AVAILABLE_DATE)))
        .thenReturn(view.dataAvailableDate);
    when(view.dataAvailableDate.setKey(any())).thenReturn(view.dataAvailableDate);
    when(view.dataAvailableDate.setHeader(any(String.class))).thenReturn(view.dataAvailableDate);
    view.date = mock(Column.class);
    when(view.submissions.addColumn(any(ValueProvider.class), eq(SUBMISSION_DATE)))
        .thenReturn(view.date);
    when(view.date.setKey(any())).thenReturn(view.date);
    when(view.date.setHeader(any(String.class))).thenReturn(view.date);
    view.instrument = mock(Column.class);
    when(view.submissions.addColumn(any(ValueProvider.class), eq(INSTRUMENT)))
        .thenReturn(view.instrument);
    when(view.instrument.setKey(any())).thenReturn(view.instrument);
    when(view.instrument.setHeader(any(String.class))).thenReturn(view.instrument);
    view.service = mock(Column.class);
    when(view.submissions.addColumn(any(ValueProvider.class), eq(SERVICE)))
        .thenReturn(view.service);
    when(view.service.setKey(any())).thenReturn(view.service);
    when(view.service.setHeader(any(String.class))).thenReturn(view.service);
    view.samplesCount = mock(Column.class);
    when(view.submissions.addColumn(any(ValueProvider.class), eq(SAMPLES_COUNT)))
        .thenReturn(view.samplesCount);
    when(view.samplesCount.setKey(any())).thenReturn(view.samplesCount);
    when(view.samplesCount.setHeader(any(String.class))).thenReturn(view.samplesCount);
    view.samples = mock(Column.class);
    when(view.submissions.addColumn(any(ComponentRenderer.class), eq(SAMPLES)))
        .thenReturn(view.samples);
    when(view.samples.setKey(any())).thenReturn(view.samples);
    when(view.samples.setHeader(any(String.class))).thenReturn(view.samples);
    when(view.samples.setSortable(anyBoolean())).thenReturn(view.samples);
    view.status = mock(Column.class);
    when(view.submissions.addColumn(any(ComponentRenderer.class), eq(STATUS)))
        .thenReturn(view.status);
    when(view.status.setKey(any())).thenReturn(view.status);
    when(view.status.setHeader(any(String.class))).thenReturn(view.status);
    when(view.status.setSortable(anyBoolean())).thenReturn(view.status);
    view.hidden = mock(Column.class);
    when(view.submissions.addColumn(any(ComponentRenderer.class), eq(HIDDEN)))
        .thenReturn(view.hidden);
    when(view.hidden.setKey(any())).thenReturn(view.hidden);
    when(view.hidden.setHeader(any(String.class))).thenReturn(view.hidden);
    HeaderRow filtersRow = mock(HeaderRow.class);
    when(view.submissions.appendHeaderRow()).thenReturn(filtersRow);
    HeaderCell experienceFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.experiment)).thenReturn(experienceFilterCell);
    HeaderCell userFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.user)).thenReturn(userFilterCell);
    HeaderCell directorFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.director)).thenReturn(directorFilterCell);
    HeaderCell serviceFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.service)).thenReturn(serviceFilterCell);
    HeaderCell instrumentFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.instrument)).thenReturn(instrumentFilterCell);
    HeaderCell samplesFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.samples)).thenReturn(samplesFilterCell);
    HeaderCell statusFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.status)).thenReturn(statusFilterCell);
    HeaderCell hiddenFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.hidden)).thenReturn(hiddenFilterCell);
  }

  @Test
  public void presenter_Init() {
    verify(presenter).init(view);
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(HEADER, view.header.getId().orElse(""));
    assertEquals(SUBMISSIONS, view.submissions.getId().orElse(""));
    assertEquals(ADD, view.add.getId().orElse(""));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void labels() {
    mockColumns();
    when(view.submissions.getDataProvider()).thenReturn(mock(DataProvider.class));
    view.instrumentFilter.setDataProvider(mock(DataProvider.class));
    view.statusFilter.setDataProvider(mock(DataProvider.class));
    view.hiddenFilter.setDataProvider(mock(DataProvider.class));
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    verify(view.experiment).setHeader(submissionResources.message(EXPERIMENT));
    verify(view.experiment).setFooter(submissionResources.message(EXPERIMENT));
    verify(view.user).setHeader(submissionResources.message(USER));
    verify(view.user).setFooter(submissionResources.message(USER));
    verify(view.director).setHeader(laboratoryResources.message(DIRECTOR));
    verify(view.director).setFooter(laboratoryResources.message(DIRECTOR));
    verify(view.dataAvailableDate).setHeader(submissionResources.message(DATA_AVAILABLE_DATE));
    verify(view.dataAvailableDate).setFooter(submissionResources.message(DATA_AVAILABLE_DATE));
    verify(view.date).setHeader(submissionResources.message(SUBMISSION_DATE));
    verify(view.date).setFooter(submissionResources.message(SUBMISSION_DATE));
    verify(view.instrument).setHeader(submissionResources.message(INSTRUMENT));
    verify(view.instrument).setFooter(submissionResources.message(INSTRUMENT));
    verify(view.service).setHeader(submissionResources.message(SERVICE));
    verify(view.service).setFooter(submissionResources.message(SERVICE));
    verify(view.samplesCount).setHeader(resources.message(SAMPLES_COUNT));
    verify(view.samplesCount).setFooter(resources.message(SAMPLES_COUNT));
    verify(view.samples).setHeader(submissionResources.message(SAMPLES));
    verify(view.samples).setFooter(submissionResources.message(SAMPLES));
    verify(view.status).setHeader(submissionSampleResources.message(STATUS));
    verify(view.status).setFooter(submissionSampleResources.message(STATUS));
    verify(view.hidden).setHeader(submissionResources.message(HIDDEN));
    verify(view.hidden).setFooter(submissionResources.message(HIDDEN));
    assertEquals(resources.message(ALL), view.experimentFilter.getPlaceholder());
    assertEquals(resources.message(ALL), view.userFilter.getPlaceholder());
    assertEquals(resources.message(ALL), view.directorFilter.getPlaceholder());
    assertEquals(resources.message(ALL), view.instrumentFilter.getPlaceholder());
    assertEquals(resources.message(ALL), view.samplesFilter.getPlaceholder());
    assertEquals(resources.message(ALL), view.statusFilter.getPlaceholder());
    assertEquals(resources.message(ALL), view.hiddenFilter.getPlaceholder());
    assertEquals(resources.message(ADD), view.add.getText());
    validateIcon(VaadinIcon.PLUS.create(), view.add.getIcon());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void localeChange() {
    view = new SubmissionsView(presenter, dialog);
    mockColumns();
    view.init();
    when(view.submissions.getDataProvider()).thenReturn(mock(DataProvider.class));
    view.instrumentFilter.setDataProvider(mock(DataProvider.class));
    view.statusFilter.setDataProvider(mock(DataProvider.class));
    view.hiddenFilter.setDataProvider(mock(DataProvider.class));
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(SubmissionsView.class, locale);
    final AppResources submissionResources = new AppResources(Submission.class, locale);
    final AppResources laboratoryResources = new AppResources(Laboratory.class, locale);
    final AppResources submissionSampleResources = new AppResources(SubmissionSample.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    verify(view.experiment).setHeader(submissionResources.message(EXPERIMENT));
    verify(view.experiment).setFooter(submissionResources.message(EXPERIMENT));
    verify(view.user).setHeader(submissionResources.message(USER));
    verify(view.user).setFooter(submissionResources.message(USER));
    verify(view.director).setHeader(laboratoryResources.message(DIRECTOR));
    verify(view.director).setFooter(laboratoryResources.message(DIRECTOR));
    verify(view.dataAvailableDate).setHeader(submissionResources.message(DATA_AVAILABLE_DATE));
    verify(view.dataAvailableDate).setFooter(submissionResources.message(DATA_AVAILABLE_DATE));
    verify(view.date, atLeastOnce()).setHeader(submissionResources.message(SUBMISSION_DATE));
    verify(view.date, atLeastOnce()).setFooter(submissionResources.message(SUBMISSION_DATE));
    verify(view.instrument).setHeader(submissionResources.message(INSTRUMENT));
    verify(view.instrument).setFooter(submissionResources.message(INSTRUMENT));
    verify(view.service).setHeader(submissionResources.message(SERVICE));
    verify(view.service).setFooter(submissionResources.message(SERVICE));
    verify(view.samplesCount).setHeader(resources.message(SAMPLES_COUNT));
    verify(view.samplesCount).setFooter(resources.message(SAMPLES_COUNT));
    verify(view.samples).setHeader(submissionResources.message(SAMPLES));
    verify(view.samples).setFooter(submissionResources.message(SAMPLES));
    verify(view.status).setHeader(submissionSampleResources.message(STATUS));
    verify(view.status).setFooter(submissionSampleResources.message(STATUS));
    verify(view.hidden).setHeader(submissionResources.message(HIDDEN));
    verify(view.hidden).setFooter(submissionResources.message(HIDDEN));
    assertEquals(resources.message(ALL), view.experimentFilter.getPlaceholder());
    assertEquals(resources.message(ALL), view.userFilter.getPlaceholder());
    assertEquals(resources.message(ALL), view.directorFilter.getPlaceholder());
    assertEquals(resources.message(ALL), view.instrumentFilter.getPlaceholder());
    assertEquals(resources.message(ALL), view.samplesFilter.getPlaceholder());
    assertEquals(resources.message(ALL), view.statusFilter.getPlaceholder());
    assertEquals(resources.message(ALL), view.hiddenFilter.getPlaceholder());
    assertEquals(resources.message(ADD), view.add.getText());
    validateIcon(VaadinIcon.PLUS.create(), view.add.getIcon());
    verify(view.submissions.getDataProvider(), times(2)).refreshAll();
    verify(view.instrumentFilter.getDataProvider(), times(2)).refreshAll();
    verify(view.statusFilter.getDataProvider(), times(2)).refreshAll();
    verify(view.hiddenFilter.getDataProvider(), times(2)).refreshAll();
  }

  @Test
  public void getPageTitle() {
    assertEquals(resources.message(TITLE, webResources.message(APPLICATION_NAME)),
        view.getPageTitle());
  }

  @Test
  public void submissions_SelectionMode() {
    assertTrue(view.submissions.getSelectionModel() instanceof SelectionModel.Single);
  }

  @Test
  public void submissions_Columns() {
    assertEquals(11, view.submissions.getColumns().size());
    assertNotNull(view.submissions.getColumnByKey(EXPERIMENT));
    assertTrue(view.submissions.getColumnByKey(EXPERIMENT).isSortable());
    assertNotNull(view.submissions.getColumnByKey(USER));
    assertTrue(view.submissions.getColumnByKey(USER).isSortable());
    assertNotNull(view.submissions.getColumnByKey(DIRECTOR));
    assertTrue(view.submissions.getColumnByKey(DIRECTOR).isSortable());
    assertNotNull(view.submissions.getColumnByKey(DATA_AVAILABLE_DATE));
    assertTrue(view.submissions.getColumnByKey(DATA_AVAILABLE_DATE).isSortable());
    assertNotNull(view.submissions.getColumnByKey(SUBMISSION_DATE));
    assertTrue(view.submissions.getColumnByKey(SUBMISSION_DATE).isSortable());
    assertNotNull(view.submissions.getColumnByKey(SERVICE));
    assertTrue(view.submissions.getColumnByKey(SERVICE).isSortable());
    assertNotNull(view.submissions.getColumnByKey(INSTRUMENT));
    assertTrue(view.submissions.getColumnByKey(INSTRUMENT).isSortable());
    assertNotNull(view.submissions.getColumnByKey(SAMPLES_COUNT));
    assertTrue(view.submissions.getColumnByKey(SAMPLES_COUNT).isSortable());
    assertNotNull(view.submissions.getColumnByKey(SAMPLES));
    assertFalse(view.submissions.getColumnByKey(SAMPLES).isSortable());
    assertNotNull(view.submissions.getColumnByKey(STATUS));
    assertFalse(view.submissions.getColumnByKey(STATUS).isSortable());
    assertNotNull(view.submissions.getColumnByKey(HIDDEN));
    assertTrue(view.submissions.getColumnByKey(HIDDEN).isSortable());
  }

  @Test
  public void submissions_ColumnsValueProvider() {
    view = new SubmissionsView(presenter, dialog);
    mockColumns();
    view.init();
    verify(view.submissions).addColumn(valueProviderCaptor.capture(), eq(EXPERIMENT));
    ValueProvider<Submission, String> valueProvider = valueProviderCaptor.getValue();
    for (Submission submission : submissions) {
      assertEquals(submission.getExperiment() != null ? submission.getExperiment() : "",
          valueProvider.apply(submission));
    }
    verify(view.experiment).setComparator(comparatorCaptor.capture());
    Comparator<Submission> comparator = comparatorCaptor.getValue();
    assertTrue(comparator.compare(experiment("abc"), experiment("test")) < 0);
    assertTrue(comparator.compare(experiment("Abc"), experiment("test")) < 0);
    assertTrue(comparator.compare(experiment("test"), experiment("test")) == 0);
    assertTrue(comparator.compare(experiment("Test"), experiment("test")) == 0);
    assertTrue(comparator.compare(experiment("test"), experiment("abc")) > 0);
    assertTrue(comparator.compare(experiment("Test"), experiment("abc")) > 0);
    assertTrue(comparator.compare(experiment("tést"), experiment("test")) == 0);
    assertTrue(comparator.compare(experiment("tèst"), experiment("test")) == 0);
    verify(view.submissions).addColumn(valueProviderCaptor.capture(), eq(USER));
    valueProvider = valueProviderCaptor.getValue();
    for (Submission submission : submissions) {
      assertEquals(submission.getUser().getName(), valueProvider.apply(submission));
    }
    verify(view.user).setComparator(comparatorCaptor.capture());
    comparator = comparatorCaptor.getValue();
    assertTrue(comparator.compare(user("abc"), user("test")) < 0);
    assertTrue(comparator.compare(user("Abc"), user("test")) < 0);
    assertTrue(comparator.compare(user("test"), user("test")) == 0);
    assertTrue(comparator.compare(user("Test"), user("test")) == 0);
    assertTrue(comparator.compare(user("test"), user("abc")) > 0);
    assertTrue(comparator.compare(user("Test"), user("abc")) > 0);
    assertTrue(comparator.compare(user("tést"), user("test")) == 0);
    assertTrue(comparator.compare(user("tèst"), user("test")) == 0);
    verify(view.submissions).addColumn(valueProviderCaptor.capture(), eq(DIRECTOR));
    valueProvider = valueProviderCaptor.getValue();
    for (Submission submission : submissions) {
      assertEquals(submission.getLaboratory().getDirector(), valueProvider.apply(submission));
    }
    verify(view.director).setComparator(comparatorCaptor.capture());
    comparator = comparatorCaptor.getValue();
    assertTrue(comparator.compare(director("abc"), director("test")) < 0);
    assertTrue(comparator.compare(director("Abc"), director("test")) < 0);
    assertTrue(comparator.compare(director("test"), director("test")) == 0);
    assertTrue(comparator.compare(director("Test"), director("test")) == 0);
    assertTrue(comparator.compare(director("test"), director("abc")) > 0);
    assertTrue(comparator.compare(director("Test"), director("abc")) > 0);
    assertTrue(comparator.compare(director("tést"), director("test")) == 0);
    assertTrue(comparator.compare(director("tèst"), director("test")) == 0);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    verify(view.submissions).addColumn(valueProviderCaptor.capture(), eq(DATA_AVAILABLE_DATE));
    valueProvider = valueProviderCaptor.getValue();
    for (Submission submission : submissions) {
      assertEquals(submission.getDataAvailableDate() != null
          ? dateFormatter.format(submission.getDataAvailableDate())
          : "", valueProvider.apply(submission));
    }
    verify(view.submissions).addColumn(valueProviderCaptor.capture(), eq(SUBMISSION_DATE));
    valueProvider = valueProviderCaptor.getValue();
    for (Submission submission : submissions) {
      assertEquals(submission.getSubmissionDate() != null
          ? dateFormatter.format(submission.getSubmissionDate())
          : "", valueProvider.apply(submission));
    }
    verify(view.submissions).addColumn(valueProviderCaptor.capture(), eq(INSTRUMENT));
    valueProvider = valueProviderCaptor.getValue();
    for (Submission submission : submissions) {
      assertEquals(submission.getInstrument() != null ? submission.getInstrument().getLabel(locale)
          : MassDetectionInstrument.getNullLabel(locale), valueProvider.apply(submission));
    }
    verify(view.submissions).addColumn(valueProviderCaptor.capture(), eq(SERVICE));
    valueProvider = valueProviderCaptor.getValue();
    for (Submission submission : submissions) {
      assertEquals(submission.getService().getLabel(locale), valueProvider.apply(submission));
    }
    verify(view.submissions).addColumn(valueProviderCaptor.capture(), eq(SAMPLES_COUNT));
    valueProvider = valueProviderCaptor.getValue();
    for (Submission submission : submissions) {
      assertEquals(submission.getSamples().size(), valueProvider.apply(submission));
    }
    verify(view.submissions).addColumn(spanRendererCaptor.capture(), eq(SAMPLES));
    ComponentRenderer<Span, Submission> spanRenderer = spanRendererCaptor.getValue();
    for (Submission submission : submissions) {
      Span span = spanRenderer.createComponent(submission);
      assertEquals(resources.message(SAMPLES_VALUE, submission.getSamples().get(0).getName(),
          submission.getSamples().size()), span.getText());
      assertEquals(submission.getSamples().stream().map(SubmissionSample::getName)
          .collect(Collectors.joining("\n")), span.getTitle().orElse(""));
    }
    verify(view.submissions).addColumn(spanRendererCaptor.capture(), eq(STATUS));
    spanRenderer = spanRendererCaptor.getValue();
    for (Submission submission : submissions) {
      List<SampleStatus> statuses = submission.getSamples().stream()
          .map(SubmissionSample::getStatus).distinct().collect(Collectors.toList());
      Span span = spanRenderer.createComponent(submission);
      assertEquals(
          resources.message(STATUS_VALUE, statuses.get(0).getLabel(locale), statuses.size()),
          span.getText());
      assertEquals(statuses.stream().map(status -> status.getLabel(locale))
          .collect(Collectors.joining("\n")), span.getTitle().orElse(""));
    }
    verify(view.submissions).addColumn(buttonRendererCaptor.capture(), eq(HIDDEN));
    ComponentRenderer<Button, Submission> buttonRenderer = buttonRendererCaptor.getValue();
    for (Submission submission : submissions) {
      Button button = buttonRenderer.createComponent(submission);
      assertTrue(button.getClassName().contains(HIDDEN));
      assertTrue(button.getThemeName().contains(submission.isHidden() ? ERROR : SUCCESS));
      assertEquals(submissionResources.message(property(HIDDEN, submission.isHidden())),
          button.getText());
      validateIcon(submission.isHidden() ? VaadinIcon.EYE_SLASH.create() : VaadinIcon.EYE.create(),
          button.getIcon());
      button.click();
      verify(presenter).toggleHidden(submission);
    }
  }

  @Test
  public void view() {
    Submission submission = submissions.get(0);
    doubleClickItem(view.submissions, submission);

    verify(presenter).view(submission);
  }

  private Submission experiment(String experiment) {
    Submission submission = new Submission();
    submission.setExperiment(experiment);
    return submission;
  }

  private Submission user(String name) {
    User user = new User();
    user.setName(name);
    Submission submission = new Submission();
    submission.setUser(user);
    return submission;
  }

  private Submission director(String director) {
    Laboratory laboratory = new Laboratory();
    laboratory.setDirector(director);
    Submission submission = new Submission();
    submission.setLaboratory(laboratory);
    return submission;
  }

  @Test
  public void experimentFilter() {
    assertEquals("", view.experimentFilter.getValue());
    assertEquals(ValueChangeMode.EAGER, view.experimentFilter.getValueChangeMode());
  }

  @Test
  public void filterExperiment() {
    view.experimentFilter.setValue("test");

    verify(presenter).filterExperiment("test");
  }

  @Test
  public void userFilter() {
    assertEquals("", view.userFilter.getValue());
    assertEquals(ValueChangeMode.EAGER, view.userFilter.getValueChangeMode());
  }

  @Test
  public void filterUser() {
    view.userFilter.setValue("test");

    verify(presenter).filterUser("test");
  }

  @Test
  public void directorFilter() {
    assertEquals("", view.directorFilter.getValue());
    assertEquals(ValueChangeMode.EAGER, view.directorFilter.getValueChangeMode());
  }

  @Test
  public void filterDirector() {
    view.directorFilter.setValue("test");

    verify(presenter).filterDirector("test");
  }

  @Test
  public void instrumentFilter() {
    assertEquals(null, view.instrumentFilter.getValue());
    assertTrue(view.instrumentFilter.isClearButtonVisible());
    List<MassDetectionInstrument> instruments = items(view.instrumentFilter);
    assertArrayEquals(MassDetectionInstrument.values(),
        instruments.toArray(new MassDetectionInstrument[0]));
    for (MassDetectionInstrument instrument : instruments) {
      assertEquals(instrument.getLabel(locale),
          view.instrumentFilter.getItemLabelGenerator().apply(instrument));
    }
  }

  @Test
  public void filterInstrument() {
    view.instrumentFilter.setValue(MassDetectionInstrument.VELOS);

    verify(presenter).filterInstrument(MassDetectionInstrument.VELOS);
  }

  @Test
  public void serviceFilter() {
    assertEquals(null, view.serviceFilter.getValue());
    assertTrue(view.serviceFilter.isClearButtonVisible());
    List<Service> values = items(view.serviceFilter);
    assertArrayEquals(Service.values(), values.toArray(new Service[0]));
    for (Service value : values) {
      assertEquals(value.getLabel(locale), view.serviceFilter.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void filterService() {
    view.serviceFilter.setValue(Service.INTACT_PROTEIN);

    verify(presenter).filterService(Service.INTACT_PROTEIN);
  }

  @Test
  public void samplesFilter() {
    assertEquals("", view.samplesFilter.getValue());
    assertEquals(ValueChangeMode.EAGER, view.samplesFilter.getValueChangeMode());
  }

  @Test
  public void filterSamples() {
    view.samplesFilter.setValue("test");

    verify(presenter).filterSamples("test");
  }

  @Test
  public void statusFilter() {
    assertEquals(null, view.statusFilter.getValue());
    assertTrue(view.statusFilter.isClearButtonVisible());
    List<SampleStatus> statuses = items(view.statusFilter);
    assertArrayEquals(SampleStatus.values(), statuses.toArray(new SampleStatus[0]));
    for (SampleStatus status : statuses) {
      assertEquals(status.getLabel(locale),
          view.statusFilter.getItemLabelGenerator().apply(status));
    }
  }

  @Test
  public void filterStatus() {
    view.statusFilter.setValue(SampleStatus.ANALYSED);

    verify(presenter).filterStatus(SampleStatus.ANALYSED);
  }

  @Test
  public void hiddenFilter() {
    assertEquals(null, view.hiddenFilter.getValue());
    assertTrue(view.hiddenFilter.isClearButtonVisible());
    List<Boolean> values = items(view.hiddenFilter);
    assertArrayEquals(new Boolean[] { false, true }, values.toArray(new Boolean[0]));
    for (Boolean value : values) {
      assertEquals(submissionResources.message(property(HIDDEN, value)),
          view.hiddenFilter.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void filterHidden() {
    view.hiddenFilter.setValue(true);

    verify(presenter).filterHidden(true);
  }

  @Test
  public void add() {
    clickButton(view.add);
    verify(presenter).add();
  }
}
