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

import static ca.qc.ircm.proview.submission.SubmissionProperties.ANALYSIS_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLE_DELIVERY_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SUBMISSION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USER;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.ADD;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.SUBMISSIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.VIEW_NAME;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.clickButton;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.doubleClickItem;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.getFormattedValue;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.user.LaboratoryProperties.DIRECTOR;
import static ca.qc.ircm.proview.web.WebConstants.ALL;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.selection.SelectionModel;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
  @Captor
  private ArgumentCaptor<ValueProvider<Submission, String>> valueProviderCaptor;
  @Captor
  private ArgumentCaptor<LocalDateRenderer<Submission>> dateRendererCaptor;
  @Captor
  private ArgumentCaptor<ComponentRenderer<Button, Submission>> buttonRendererCaptor;
  @Captor
  private ArgumentCaptor<Comparator<Submission>> comparatorCaptor;
  @Autowired
  private SubmissionRepository submissionRepository;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(SubmissionsView.class, locale);
  private MessageResource submissionResources = new MessageResource(Submission.class, locale);
  private MessageResource laboratoryResources = new MessageResource(Laboratory.class, locale);
  private MessageResource webResources = new MessageResource(WebConstants.class, locale);
  private List<Submission> submissions;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    view = new SubmissionsView(presenter);
    view.init();
    submissions = submissionRepository.findAll();
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
    view.sampleDeliveryDate = mock(Column.class);
    when(view.submissions.addColumn(any(LocalDateRenderer.class), eq(SAMPLE_DELIVERY_DATE)))
        .thenReturn(view.sampleDeliveryDate);
    when(view.sampleDeliveryDate.setKey(any())).thenReturn(view.sampleDeliveryDate);
    when(view.sampleDeliveryDate.setHeader(any(String.class))).thenReturn(view.sampleDeliveryDate);
    view.digestionDate = mock(Column.class);
    when(view.submissions.addColumn(any(LocalDateRenderer.class), eq(DIGESTION_DATE)))
        .thenReturn(view.digestionDate);
    when(view.digestionDate.setKey(any())).thenReturn(view.digestionDate);
    when(view.digestionDate.setHeader(any(String.class))).thenReturn(view.digestionDate);
    view.analysisDate = mock(Column.class);
    when(view.submissions.addColumn(any(LocalDateRenderer.class), eq(ANALYSIS_DATE)))
        .thenReturn(view.analysisDate);
    when(view.analysisDate.setKey(any())).thenReturn(view.analysisDate);
    when(view.analysisDate.setHeader(any(String.class))).thenReturn(view.analysisDate);
    view.dataAvailableDate = mock(Column.class);
    when(view.submissions.addColumn(any(LocalDateRenderer.class), eq(DATA_AVAILABLE_DATE)))
        .thenReturn(view.dataAvailableDate);
    when(view.dataAvailableDate.setKey(any())).thenReturn(view.dataAvailableDate);
    when(view.dataAvailableDate.setHeader(any(String.class))).thenReturn(view.dataAvailableDate);
    view.date = mock(Column.class);
    when(view.submissions.addColumn(any(LocalDateRenderer.class), eq(SUBMISSION_DATE)))
        .thenReturn(view.date);
    when(view.date.setKey(any())).thenReturn(view.date);
    when(view.date.setHeader(any(String.class))).thenReturn(view.date);
    HeaderRow filtersRow = mock(HeaderRow.class);
    when(view.submissions.appendHeaderRow()).thenReturn(filtersRow);
    HeaderCell experienceFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.experiment)).thenReturn(experienceFilterCell);
    HeaderCell userFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.user)).thenReturn(userFilterCell);
    HeaderCell directorFilterCell = mock(HeaderCell.class);
    when(filtersRow.getCell(view.director)).thenReturn(directorFilterCell);
  }

  @Test
  public void presenter_Init() {
    verify(presenter).init(view);
  }

  @Test
  public void columns_User() {
    assertTrue(view.getId().orElse("").equals(VIEW_NAME));
    assertTrue(view.header.getId().orElse("").contains(HEADER));
    assertTrue(view.submissions.getId().orElse("").contains(SUBMISSIONS));
    assertTrue(view.add.getId().orElse("").contains(ADD));
  }

  @Test
  public void styles() {
    assertTrue(view.getId().orElse("").equals(VIEW_NAME));
    assertTrue(view.header.getId().orElse("").contains(HEADER));
    assertTrue(view.submissions.getId().orElse("").contains(SUBMISSIONS));
    assertTrue(view.add.getId().orElse("").contains(ADD));
  }

  @Test
  public void labels() {
    mockColumns();
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    verify(view.experiment).setHeader(submissionResources.message(EXPERIMENT));
    verify(view.experiment).setFooter(submissionResources.message(EXPERIMENT));
    verify(view.user).setHeader(submissionResources.message(USER));
    verify(view.user).setFooter(submissionResources.message(USER));
    verify(view.director).setHeader(laboratoryResources.message(DIRECTOR));
    verify(view.director).setFooter(laboratoryResources.message(DIRECTOR));
    verify(view.sampleDeliveryDate).setHeader(submissionResources.message(SAMPLE_DELIVERY_DATE));
    verify(view.sampleDeliveryDate).setFooter(submissionResources.message(SAMPLE_DELIVERY_DATE));
    verify(view.digestionDate).setHeader(submissionResources.message(DIGESTION_DATE));
    verify(view.digestionDate).setFooter(submissionResources.message(DIGESTION_DATE));
    verify(view.analysisDate).setHeader(submissionResources.message(ANALYSIS_DATE));
    verify(view.analysisDate).setFooter(submissionResources.message(ANALYSIS_DATE));
    verify(view.dataAvailableDate).setHeader(submissionResources.message(DATA_AVAILABLE_DATE));
    verify(view.dataAvailableDate).setFooter(submissionResources.message(DATA_AVAILABLE_DATE));
    verify(view.date).setHeader(submissionResources.message(SUBMISSION_DATE));
    verify(view.date).setFooter(submissionResources.message(SUBMISSION_DATE));
    assertEquals(resources.message(ALL), view.experimentFilter.getPlaceholder());
    assertEquals(resources.message(ADD), view.add.getText());
    validateIcon(VaadinIcon.PLUS.create(), view.add.getIcon());
  }

  @Test
  public void localeChange() {
    view = new SubmissionsView(presenter);
    mockColumns();
    view.init();
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = Locale.FRENCH;
    final MessageResource resources = new MessageResource(SubmissionsView.class, locale);
    final MessageResource submissionResources = new MessageResource(Submission.class, locale);
    final MessageResource laboratoryResources = new MessageResource(Laboratory.class, locale);
    final MessageResource webResources = new MessageResource(WebConstants.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    verify(view.experiment).setHeader(submissionResources.message(EXPERIMENT));
    verify(view.experiment).setFooter(submissionResources.message(EXPERIMENT));
    verify(view.user).setHeader(submissionResources.message(USER));
    verify(view.user).setFooter(submissionResources.message(USER));
    verify(view.director).setHeader(laboratoryResources.message(DIRECTOR));
    verify(view.director).setFooter(laboratoryResources.message(DIRECTOR));
    verify(view.sampleDeliveryDate).setHeader(submissionResources.message(SAMPLE_DELIVERY_DATE));
    verify(view.sampleDeliveryDate).setFooter(submissionResources.message(SAMPLE_DELIVERY_DATE));
    verify(view.digestionDate, atLeastOnce())
        .setHeader(submissionResources.message(DIGESTION_DATE));
    verify(view.digestionDate, atLeastOnce())
        .setFooter(submissionResources.message(DIGESTION_DATE));
    verify(view.analysisDate).setHeader(submissionResources.message(ANALYSIS_DATE));
    verify(view.analysisDate).setFooter(submissionResources.message(ANALYSIS_DATE));
    verify(view.dataAvailableDate).setHeader(submissionResources.message(DATA_AVAILABLE_DATE));
    verify(view.dataAvailableDate).setFooter(submissionResources.message(DATA_AVAILABLE_DATE));
    verify(view.date, atLeastOnce()).setHeader(submissionResources.message(SUBMISSION_DATE));
    verify(view.date, atLeastOnce()).setFooter(submissionResources.message(SUBMISSION_DATE));
    assertEquals(resources.message(ALL), view.experimentFilter.getPlaceholder());
    assertEquals(resources.message(ADD), view.add.getText());
    validateIcon(VaadinIcon.PLUS.create(), view.add.getIcon());
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
    assertEquals(8, view.submissions.getColumns().size());
    assertNotNull(view.submissions.getColumnByKey(EXPERIMENT));
    assertTrue(view.submissions.getColumnByKey(EXPERIMENT).isSortable());
    assertNotNull(view.submissions.getColumnByKey(USER));
    assertTrue(view.submissions.getColumnByKey(USER).isSortable());
    assertNotNull(view.submissions.getColumnByKey(DIRECTOR));
    assertTrue(view.submissions.getColumnByKey(DIRECTOR).isSortable());
    assertNotNull(view.submissions.getColumnByKey(SAMPLE_DELIVERY_DATE));
    assertTrue(view.submissions.getColumnByKey(SAMPLE_DELIVERY_DATE).isSortable());
    assertNotNull(view.submissions.getColumnByKey(DIGESTION_DATE));
    assertTrue(view.submissions.getColumnByKey(DIGESTION_DATE).isSortable());
    assertNotNull(view.submissions.getColumnByKey(ANALYSIS_DATE));
    assertTrue(view.submissions.getColumnByKey(ANALYSIS_DATE).isSortable());
    assertNotNull(view.submissions.getColumnByKey(DATA_AVAILABLE_DATE));
    assertTrue(view.submissions.getColumnByKey(DATA_AVAILABLE_DATE).isSortable());
    assertNotNull(view.submissions.getColumnByKey(SUBMISSION_DATE));
    assertTrue(view.submissions.getColumnByKey(SUBMISSION_DATE).isSortable());
  }

  @Test
  public void submissions_ColumnsValueProvider() {
    view = new SubmissionsView(presenter);
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
    verify(view.submissions).addColumn(dateRendererCaptor.capture(), eq(SAMPLE_DELIVERY_DATE));
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    LocalDateRenderer<Submission> dateRenderer = dateRendererCaptor.getValue();
    for (Submission submission : submissions) {
      assertEquals(submission.getSampleDeliveryDate() != null
          ? dateFormatter.format(submission.getSampleDeliveryDate())
          : null, getFormattedValue(dateRenderer, submission));
    }
    verify(view.submissions).addColumn(dateRendererCaptor.capture(), eq(DIGESTION_DATE));
    dateRenderer = dateRendererCaptor.getValue();
    for (Submission submission : submissions) {
      assertEquals(submission.getDigestionDate() != null
          ? dateFormatter.format(submission.getDigestionDate())
          : null, getFormattedValue(dateRenderer, submission));
    }
    verify(view.submissions).addColumn(dateRendererCaptor.capture(), eq(ANALYSIS_DATE));
    dateRenderer = dateRendererCaptor.getValue();
    for (Submission submission : submissions) {
      assertEquals(
          submission.getAnalysisDate() != null ? dateFormatter.format(submission.getAnalysisDate())
              : null,
          getFormattedValue(dateRenderer, submission));
    }
    verify(view.submissions).addColumn(dateRendererCaptor.capture(), eq(DATA_AVAILABLE_DATE));
    dateRenderer = dateRendererCaptor.getValue();
    for (Submission submission : submissions) {
      assertEquals(submission.getDataAvailableDate() != null
          ? dateFormatter.format(submission.getDataAvailableDate())
          : null, getFormattedValue(dateRenderer, submission));
    }
    verify(view.submissions).addColumn(dateRendererCaptor.capture(), eq(SUBMISSION_DATE));
    dateRenderer = dateRendererCaptor.getValue();
    for (Submission submission : submissions) {
      assertEquals(submission.getSubmissionDate() != null
          ? dateFormatter.format(submission.getSubmissionDate())
          : null, getFormattedValue(dateRenderer, submission));
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
  public void add() {
    clickButton(view.add);
    verify(presenter).add();
  }
}
