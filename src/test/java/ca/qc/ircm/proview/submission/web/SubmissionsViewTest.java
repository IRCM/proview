package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.ALL;
import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.VIEW;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIDDEN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.LABORATORY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SUBMISSION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USER;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.ADD;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.EDIT_STATUS;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.HIDDEN_BUTTON;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.HIDE_COLUMNS;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.HISTORY;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.ID;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.SAMPLES_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.SAMPLES_SPAN;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.SAMPLES_VALUE;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.STATUS_SPAN;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.STATUS_VALUE;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.SUBMISSIONS;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.functions;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.rendererTemplate;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.user.LaboratoryProperties.DIRECTOR;
import static com.vaadin.flow.data.provider.SortDirection.ASCENDING;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.web.SamplesStatusDialog;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserPreferenceService;
import ca.qc.ircm.proview.user.UserProperties;
import ca.qc.ircm.proview.web.ContactView;
import ca.qc.ircm.proview.web.ErrorNotification;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.SortOrder;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.selection.SelectionModel;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.testbench.unit.MetaKeys;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link SubmissionsView}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionsViewTest extends SpringUIUnitTest {

  private static final String MESSAGES_PREFIX = messagePrefix(SubmissionsView.class);
  private static final String SUBMISSION_PREFIX = messagePrefix(Submission.class);
  private static final String SUBMISSION_SAMPLE_PREFIX = messagePrefix(SubmissionSample.class);
  private static final String LABORATORY_PREFIX = messagePrefix(Laboratory.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String MASS_DETECTION_INSTRUMENT_PREFIX = messagePrefix(
      MassDetectionInstrument.class);
  private static final String SAMPLE_STATUS_PREFIX = messagePrefix(SampleStatus.class);
  private static final String SERVICE_PREFIX = messagePrefix(Service.class);
  private SubmissionsView view;
  @MockitoBean
  private SubmissionService service;
  @MockitoBean
  private UserPreferenceService userPreferenceService;
  @Autowired
  private SubmissionRepository repository;
  @Mock
  private ListDataProvider<Submission> dataProvider;
  @Captor
  private ArgumentCaptor<Pageable> pageableCaptor;
  private final Locale locale = ENGLISH;
  private List<Submission> submissions;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    when(service.get(anyLong())).thenAnswer(i -> repository.findById(i.getArgument(0)));
    when(service.print(any(), any())).thenReturn("");
    when(userPreferenceService.get(any(), eq(SAMPLES))).thenReturn(Optional.of(false));
    when(userPreferenceService.get(any(), eq(STATUS))).thenReturn(Optional.of(true));
    UI.getCurrent().setLocale(locale);
    submissions = repository.findAll();
    navigate(ContactView.class);
    view = navigate(SubmissionsView.class);
  }

  private int indexOfColumn(String property) {
    return test(view.submissions).getColumnPosition(property);
  }

  private Submission experiment(String experiment) {
    Submission submission = new Submission();
    submission.setExperiment(experiment);
    return submission;
  }

  private Submission user(String name) {
    Submission submission = new Submission();
    submission.setUser(new User());
    submission.getUser().setName(name);
    return submission;
  }

  private Submission director(String director) {
    Submission submission = new Submission();
    submission.setLaboratory(new Laboratory());
    submission.getLaboratory().setDirector(director);
    return submission;
  }

  private Submission hidden(boolean hidden) {
    Submission submission = new Submission();
    submission.setHidden(hidden);
    return submission;
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(SUBMISSIONS, view.submissions.getId().orElse(""));
    assertEquals(ADD, view.add.getId().orElse(""));
    validateIcon(VaadinIcon.PLUS.create(), view.add.getIcon());
    assertEquals(VIEW, view.view.getId().orElse(""));
    validateIcon(VaadinIcon.EYE.create(), view.view.getIcon());
    assertEquals(EDIT_STATUS, view.editStatus.getId().orElse(""));
    validateIcon(VaadinIcon.EDIT.create(), view.editStatus.getIcon());
    assertEquals(HISTORY, view.history.getId().orElse(""));
    validateIcon(VaadinIcon.ARCHIVE.create(), view.history.getIcon());
    assertEquals(HIDE_COLUMNS, view.hideColumns.getId().orElse(""));
    validateIcon(VaadinIcon.COG.create(), view.hideColumns.getIcon());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void labels() {
    view.submissions.setItems(submissions);
    HeaderRow header = view.submissions.getHeaderRows().get(0);
    FooterRow footer = view.submissions.getFooterRows().get(0);
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + EXPERIMENT),
        header.getCell(view.experiment).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + EXPERIMENT),
        footer.getCell(view.experiment).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + USER),
        header.getCell(view.user).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + USER),
        footer.getCell(view.user).getText());
    assertEquals(view.getTranslation(LABORATORY_PREFIX + DIRECTOR),
        header.getCell(view.director).getText());
    assertEquals(view.getTranslation(LABORATORY_PREFIX + DIRECTOR),
        footer.getCell(view.director).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + DATA_AVAILABLE_DATE),
        header.getCell(view.dataAvailableDate).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + DATA_AVAILABLE_DATE),
        footer.getCell(view.dataAvailableDate).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SUBMISSION_DATE),
        header.getCell(view.date).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SUBMISSION_DATE),
        footer.getCell(view.date).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + INSTRUMENT),
        header.getCell(view.instrument).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + INSTRUMENT),
        footer.getCell(view.instrument).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SERVICE),
        header.getCell(view.service).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SERVICE),
        footer.getCell(view.service).getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAMPLES_COUNT),
        header.getCell(view.samplesCount).getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAMPLES_COUNT),
        footer.getCell(view.samplesCount).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SAMPLES),
        header.getCell(view.samples).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SAMPLES),
        footer.getCell(view.samples).getText());
    assertEquals(view.getTranslation(SUBMISSION_SAMPLE_PREFIX + STATUS),
        header.getCell(view.status).getText());
    assertEquals(view.getTranslation(SUBMISSION_SAMPLE_PREFIX + STATUS),
        footer.getCell(view.status).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + HIDDEN),
        header.getCell(view.hidden).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + HIDDEN),
        footer.getCell(view.hidden).getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL),
        view.experimentFilter.getPlaceholder());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL), view.userFilter.getPlaceholder());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL), view.directorFilter.getPlaceholder());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL),
        view.instrumentFilter.getPlaceholder());
    view.instrumentFilter.getListDataView().getItems().forEach(instrument -> assertEquals(
        view.getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + instrument.name()),
        view.instrumentFilter.getItemLabelGenerator().apply(instrument)));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL), view.serviceFilter.getPlaceholder());
    view.serviceFilter.getListDataView().getItems().forEach(
        service -> assertEquals(view.getTranslation(SERVICE_PREFIX + service.name()),
            view.serviceFilter.getItemLabelGenerator().apply(service)));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL), view.samplesFilter.getPlaceholder());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL), view.statusFilter.getPlaceholder());
    view.statusFilter.getListDataView().getItems().forEach(
        status -> assertEquals(view.getTranslation(SAMPLE_STATUS_PREFIX + status.name()),
            view.statusFilter.getItemLabelGenerator().apply(status)));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL), view.hiddenFilter.getPlaceholder());
    view.hiddenFilter.getListDataView().getItems().forEach(
        hidden -> assertEquals(view.getTranslation(SUBMISSION_PREFIX + property(HIDDEN, hidden)),
            view.hiddenFilter.getItemLabelGenerator().apply(hidden)));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ADD), view.add.getText());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + VIEW), view.view.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + EDIT_STATUS), view.editStatus.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HISTORY), view.history.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HIDE_COLUMNS), view.hideColumns.getText());
    assertEquals(view.getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + "NULL"),
        test(view.submissions).getCellText(18, 5));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void localeChange() {
    view.submissions.setItems(submissions);
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    HeaderRow header = view.submissions.getHeaderRows().get(0);
    FooterRow footer = view.submissions.getFooterRows().get(0);
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + EXPERIMENT),
        header.getCell(view.experiment).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + EXPERIMENT),
        footer.getCell(view.experiment).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + USER),
        header.getCell(view.user).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + USER),
        footer.getCell(view.user).getText());
    assertEquals(view.getTranslation(LABORATORY_PREFIX + DIRECTOR),
        header.getCell(view.director).getText());
    assertEquals(view.getTranslation(LABORATORY_PREFIX + DIRECTOR),
        footer.getCell(view.director).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + DATA_AVAILABLE_DATE),
        header.getCell(view.dataAvailableDate).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + DATA_AVAILABLE_DATE),
        footer.getCell(view.dataAvailableDate).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SUBMISSION_DATE),
        header.getCell(view.date).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SUBMISSION_DATE),
        footer.getCell(view.date).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + INSTRUMENT),
        header.getCell(view.instrument).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + INSTRUMENT),
        footer.getCell(view.instrument).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SERVICE),
        header.getCell(view.service).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SERVICE),
        footer.getCell(view.service).getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAMPLES_COUNT),
        header.getCell(view.samplesCount).getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAMPLES_COUNT),
        footer.getCell(view.samplesCount).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SAMPLES),
        header.getCell(view.samples).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SAMPLES),
        footer.getCell(view.samples).getText());
    assertEquals(view.getTranslation(SUBMISSION_SAMPLE_PREFIX + STATUS),
        header.getCell(view.status).getText());
    assertEquals(view.getTranslation(SUBMISSION_SAMPLE_PREFIX + STATUS),
        footer.getCell(view.status).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + HIDDEN),
        header.getCell(view.hidden).getText());
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + HIDDEN),
        footer.getCell(view.hidden).getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL),
        view.experimentFilter.getPlaceholder());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL), view.userFilter.getPlaceholder());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL), view.directorFilter.getPlaceholder());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL),
        view.instrumentFilter.getPlaceholder());
    view.instrumentFilter.getListDataView().getItems().forEach(instrument -> assertEquals(
        view.getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + instrument.name()),
        view.instrumentFilter.getItemLabelGenerator().apply(instrument)));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL), view.serviceFilter.getPlaceholder());
    view.serviceFilter.getListDataView().getItems().forEach(
        service -> assertEquals(view.getTranslation(SERVICE_PREFIX + service.name()),
            view.serviceFilter.getItemLabelGenerator().apply(service)));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL), view.samplesFilter.getPlaceholder());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL), view.statusFilter.getPlaceholder());
    view.statusFilter.getListDataView().getItems().forEach(
        status -> assertEquals(view.getTranslation(SAMPLE_STATUS_PREFIX + status.name()),
            view.statusFilter.getItemLabelGenerator().apply(status)));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ALL), view.hiddenFilter.getPlaceholder());
    view.hiddenFilter.getListDataView().getItems().forEach(
        hidden -> assertEquals(view.getTranslation(SUBMISSION_PREFIX + property(HIDDEN, hidden)),
            view.hiddenFilter.getItemLabelGenerator().apply(hidden)));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ADD), view.add.getText());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + VIEW), view.view.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + EDIT_STATUS), view.editStatus.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HISTORY), view.history.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HIDE_COLUMNS), view.hideColumns.getText());
    assertEquals(view.getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + "NULL"),
        test(view.submissions).getCellText(18, 5));
  }

  @Test
  public void hiddenButton_User() {
    assertFalse(view.editStatus.isVisible());
    assertFalse(view.history.isVisible());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void hiddenButton_Manager() {
    assertFalse(view.editStatus.isVisible());
    assertFalse(view.history.isVisible());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hiddenButton_Admin() {
    assertTrue(view.editStatus.isVisible());
    assertTrue(view.history.isVisible());
  }

  @Test
  public void getPageTitle() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + TITLE,
        view.getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME)), view.getPageTitle());
  }

  @Test
  public void submissions() {
    when(service.all(any(), any())).then(i -> submissions.stream());
    assertThrows(IllegalStateException.class, () -> view.submissions.getListDataView());
    assertDoesNotThrow(() -> view.submissions.getLazyDataView());
    DataProvider<Submission, ?> dataProvider = view.submissions.getDataProvider();
    List<Submission> submissions = dataProvider.fetch(
            new Query<>(0, Integer.MAX_VALUE, new ArrayList<>(), null, null))
        .collect(Collectors.toList());
    assertEquals(this.submissions, submissions);
    verify(service).all(any(), pageableCaptor.capture());
    Pageable pageable = pageableCaptor.getValue();
    assertEquals(0, pageable.getSort().stream().count());
  }

  @Test
  public void submissions_SortOrder() {
    when(service.all(any(), any())).then(i -> submissions.stream());
    assertThrows(IllegalStateException.class, () -> view.submissions.getListDataView());
    assertDoesNotThrow(() -> view.submissions.getLazyDataView());
    List<QuerySortOrder> sortOrders = Arrays.asList(new QuerySortOrder(EXPERIMENT, ASCENDING),
        new QuerySortOrder(USER + "." + UserProperties.NAME, SortDirection.DESCENDING));
    DataProvider<Submission, ?> dataProvider = view.submissions.getDataProvider();
    List<Submission> submissions = dataProvider.fetch(
        new Query<>(0, Integer.MAX_VALUE, sortOrders, null, null)).collect(Collectors.toList());
    assertEquals(this.submissions, submissions);
    verify(service).all(any(), pageableCaptor.capture());
    Pageable pageable = pageableCaptor.getValue();
    assertEquals(2, pageable.getSort().stream().count());
    List<Order> orders = pageable.getSort().stream().toList();
    assertEquals(ASC, orders.get(0).getDirection());
    assertEquals(EXPERIMENT, orders.get(0).getProperty());
    assertEquals(DESC, orders.get(1).getDirection());
    assertEquals(USER + "." + UserProperties.NAME, orders.get(1).getProperty());
  }

  @Test
  public void submissions_SelectionMode() {
    assertInstanceOf(SelectionModel.Single.class, view.submissions.getSelectionModel());
  }

  @Test
  public void submissions_Columns() {
    assertEquals(11, view.submissions.getColumns().size());
    assertNotNull(view.submissions.getColumnByKey(EXPERIMENT));
    assertTrue(view.submissions.getColumnByKey(EXPERIMENT).isSortable());
    assertEquals(EXPERIMENT,
        view.submissions.getColumnByKey(EXPERIMENT).getSortOrder(ASCENDING).findFirst()
            .map(SortOrder::getSorted).orElseThrow());
    assertNotNull(view.submissions.getColumnByKey(USER));
    assertTrue(view.submissions.getColumnByKey(USER).isSortable());
    assertEquals(USER + "." + UserProperties.NAME,
        view.submissions.getColumnByKey(USER).getSortOrder(ASCENDING).findFirst()
            .map(SortOrder::getSorted).orElseThrow());
    assertNotNull(view.submissions.getColumnByKey(DIRECTOR));
    assertTrue(view.submissions.getColumnByKey(DIRECTOR).isSortable());
    assertEquals(LABORATORY + "." + DIRECTOR,
        view.submissions.getColumnByKey(DIRECTOR).getSortOrder(ASCENDING).findFirst()
            .map(SortOrder::getSorted).orElseThrow());
    assertNotNull(view.submissions.getColumnByKey(DATA_AVAILABLE_DATE));
    assertTrue(view.submissions.getColumnByKey(DATA_AVAILABLE_DATE).isSortable());
    assertEquals(DATA_AVAILABLE_DATE,
        view.submissions.getColumnByKey(DATA_AVAILABLE_DATE).getSortOrder(ASCENDING).findFirst()
            .map(SortOrder::getSorted).orElseThrow());
    assertNotNull(view.submissions.getColumnByKey(SUBMISSION_DATE));
    assertTrue(view.submissions.getColumnByKey(SUBMISSION_DATE).isSortable());
    assertEquals(SUBMISSION_DATE,
        view.submissions.getColumnByKey(SUBMISSION_DATE).getSortOrder(ASCENDING).findFirst()
            .map(SortOrder::getSorted).orElseThrow());
    assertNotNull(view.submissions.getColumnByKey(SERVICE));
    assertTrue(view.submissions.getColumnByKey(SERVICE).isSortable());
    assertEquals(SERVICE,
        view.submissions.getColumnByKey(SERVICE).getSortOrder(ASCENDING).findFirst()
            .map(SortOrder::getSorted).orElseThrow());
    assertNotNull(view.submissions.getColumnByKey(INSTRUMENT));
    assertTrue(view.submissions.getColumnByKey(INSTRUMENT).isSortable());
    assertEquals(INSTRUMENT,
        view.submissions.getColumnByKey(INSTRUMENT).getSortOrder(ASCENDING).findFirst()
            .map(SortOrder::getSorted).orElseThrow());
    assertNotNull(view.submissions.getColumnByKey(SAMPLES_COUNT));
    assertFalse(view.submissions.getColumnByKey(SAMPLES_COUNT).isSortable());
    assertNotNull(view.submissions.getColumnByKey(SAMPLES));
    assertFalse(view.submissions.getColumnByKey(SAMPLES).isSortable());
    assertNotNull(view.submissions.getColumnByKey(STATUS));
    assertFalse(view.submissions.getColumnByKey(STATUS).isSortable());
    assertNotNull(view.submissions.getColumnByKey(HIDDEN));
    assertTrue(view.submissions.getColumnByKey(HIDDEN).isSortable());
    assertEquals(HIDDEN, view.submissions.getColumnByKey(HIDDEN).getSortOrder(ASCENDING).findFirst()
        .map(SortOrder::getSorted).orElseThrow());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void submissions_ColumnsValueProvider() {
    view.submissions.setItems(submissions);
    List<Submission> submissions = view.submissions.getListDataView().getItems().toList();
    view.samples.setVisible(true);
    for (int i = 0; i < submissions.size(); i++) {
      Submission submission = submissions.get(i);
      assertEquals(submission.getExperiment(),
          test(view.submissions).getCellText(i, indexOfColumn(EXPERIMENT)));
      assertEquals(submission.getUser().getName(),
          test(view.submissions).getCellText(i, indexOfColumn(USER)));
      assertEquals(submission.getLaboratory().getDirector(),
          test(view.submissions).getCellText(i, indexOfColumn(DIRECTOR)));
      DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
      assertEquals(submission.getDataAvailableDate() != null ? dateFormatter.format(
              submission.getDataAvailableDate()) : "",
          test(view.submissions).getCellText(i, indexOfColumn(DATA_AVAILABLE_DATE)));
      assertEquals(dateFormatter.format(submission.getSubmissionDate()),
          test(view.submissions).getCellText(i, indexOfColumn(SUBMISSION_DATE)));
      assertEquals(view.getTranslation(
              MASS_DETECTION_INSTRUMENT_PREFIX + Optional.ofNullable(submission.getInstrument())
                  .orElse(MassDetectionInstrument.NULL).name()),
          test(view.submissions).getCellText(i, indexOfColumn(INSTRUMENT)));
      assertEquals(view.getTranslation(SERVICE_PREFIX + submission.getService().name()),
          test(view.submissions).getCellText(i, indexOfColumn(SERVICE)));
      assertEquals(Objects.toString(submission.getSamples().size()),
          test(view.submissions).getCellText(i, indexOfColumn(SAMPLES_COUNT)));
      Renderer<Submission> samplesRawRenderer = view.submissions.getColumnByKey(SAMPLES)
          .getRenderer();
      assertInstanceOf(LitRenderer.class, samplesRawRenderer);
      LitRenderer<Submission> samplesRenderer = (LitRenderer<Submission>) samplesRawRenderer;
      assertEquals(SAMPLES_SPAN, rendererTemplate(samplesRenderer));
      assertTrue(samplesRenderer.getValueProviders().containsKey("samplesValue"));
      assertEquals(view.getTranslation(MESSAGES_PREFIX + SAMPLES_VALUE,
              submission.getSamples().get(0).getName(), submission.getSamples().size()),
          samplesRenderer.getValueProviders().get("samplesValue").apply(submission));
      assertTrue(samplesRenderer.getValueProviders().containsKey("samplesTitle"));
      assertEquals(submission.getSamples().stream().map(SubmissionSample::getName)
              .collect(Collectors.joining("\n")),
          samplesRenderer.getValueProviders().get("samplesTitle").apply(submission));
      List<SampleStatus> statuses = submission.getSamples().stream()
          .map(SubmissionSample::getStatus).distinct().toList();
      Renderer<Submission> statusRawRenderer = view.submissions.getColumnByKey(STATUS)
          .getRenderer();
      assertInstanceOf(LitRenderer.class, statusRawRenderer);
      LitRenderer<Submission> statusRenderer = (LitRenderer<Submission>) statusRawRenderer;
      assertEquals(STATUS_SPAN, rendererTemplate(statusRenderer));
      assertTrue(statusRenderer.getValueProviders().containsKey("statusValue"));
      assertEquals(view.getTranslation(MESSAGES_PREFIX + STATUS_VALUE,
              view.getTranslation(SAMPLE_STATUS_PREFIX + statuses.get(0).name()), statuses.size()),
          statusRenderer.getValueProviders().get("statusValue").apply(submission));
      assertTrue(statusRenderer.getValueProviders().containsKey("statusTitle"));
      assertEquals(
          statuses.stream().map(status -> view.getTranslation(SAMPLE_STATUS_PREFIX + status.name()))
              .collect(Collectors.joining("\n")),
          statusRenderer.getValueProviders().get("statusTitle").apply(submission));
      Renderer<Submission> hiddenRawRenderer = view.submissions.getColumnByKey(HIDDEN)
          .getRenderer();
      assertInstanceOf(LitRenderer.class, hiddenRawRenderer);
      LitRenderer<Submission> hiddenRenderer = (LitRenderer<Submission>) hiddenRawRenderer;
      assertEquals(HIDDEN_BUTTON, rendererTemplate(hiddenRenderer));
      assertTrue(hiddenRenderer.getValueProviders().containsKey("hiddenTheme"));
      assertEquals(submission.isHidden() ? ButtonVariant.LUMO_ERROR.getVariantName()
              : ButtonVariant.LUMO_SUCCESS.getVariantName(),
          hiddenRenderer.getValueProviders().get("hiddenTheme").apply(submission));
      assertTrue(hiddenRenderer.getValueProviders().containsKey("hiddenValue"));
      assertEquals(view.getTranslation(SUBMISSION_PREFIX + property(HIDDEN, submission.isHidden())),
          hiddenRenderer.getValueProviders().get("hiddenValue").apply(submission));
      assertTrue(hiddenRenderer.getValueProviders().containsKey("hiddenIcon"));
      assertEquals(submission.isHidden() ? "vaadin:eye-slash" : "vaadin:eye",
          hiddenRenderer.getValueProviders().get("hiddenIcon").apply(submission));
      assertTrue(functions(hiddenRenderer).containsKey("toggleHidden"));
      boolean hiddenBefore = submission.isHidden();
      functions(hiddenRenderer).get("toggleHidden").accept(submission, null);
      verify(service).update(submission, null);
      assertEquals(!hiddenBefore, submission.isHidden());
      assertEquals(submission.isHidden() ? "vaadin:eye-slash" : "vaadin:eye",
          hiddenRenderer.getValueProviders().get("hiddenIcon").apply(submission));
    }
  }

  @Test
  public void submissions_ExperimentColumnComparator() {
    Comparator<Submission> comparator = test(view.submissions).getColumn(EXPERIMENT)
        .getComparator(ASCENDING);
    assertEquals(0, comparator.compare(experiment("éê"), experiment("ee")));
    assertTrue(comparator.compare(experiment("a"), experiment("e")) < 0);
    assertTrue(comparator.compare(experiment("a"), experiment("é")) < 0);
    assertTrue(comparator.compare(experiment("e"), experiment("a")) > 0);
    assertTrue(comparator.compare(experiment("é"), experiment("a")) > 0);
  }

  @Test
  public void submissions_UserColumnComparator() {
    Comparator<Submission> comparator = test(view.submissions).getColumn(USER)
        .getComparator(ASCENDING);
    assertEquals(0, comparator.compare(user("éê"), user("ee")));
    assertTrue(comparator.compare(user("a"), user("e")) < 0);
    assertTrue(comparator.compare(user("a"), user("é")) < 0);
    assertTrue(comparator.compare(user("e"), user("a")) > 0);
    assertTrue(comparator.compare(user("é"), user("a")) > 0);
  }

  @Test
  public void submissions_DirectorColumnComparator() {
    Comparator<Submission> comparator = test(view.submissions).getColumn(DIRECTOR)
        .getComparator(ASCENDING);
    assertEquals(0, comparator.compare(director("éê"), director("ee")));
    assertTrue(comparator.compare(director("a"), director("e")) < 0);
    assertTrue(comparator.compare(director("a"), director("é")) < 0);
    assertTrue(comparator.compare(director("e"), director("a")) > 0);
    assertTrue(comparator.compare(director("é"), director("a")) > 0);
  }

  @Test
  public void submissions_HiddenColumnComparator() {
    Comparator<Submission> comparator = test(view.submissions).getColumn(HIDDEN)
        .getComparator(ASCENDING);
    assertTrue(comparator.compare(hidden(false), hidden(true)) < 0);
    assertEquals(0, comparator.compare(hidden(false), hidden(false)));
    assertEquals(0, comparator.compare(hidden(true), hidden(true)));
    assertTrue(comparator.compare(hidden(true), hidden(false)) > 0);
  }

  @Test
  public void submissions_HiddenColumnsUser() {
    verify(userPreferenceService, never()).get(view, view.experiment.getKey());
    verify(userPreferenceService, never()).get(view, view.user.getKey());
    verify(userPreferenceService, never()).get(view, view.director.getKey());
    verify(userPreferenceService, never()).get(view, view.service.getKey());
    verify(userPreferenceService).get(view, view.dataAvailableDate.getKey());
    verify(userPreferenceService).get(view, view.date.getKey());
    verify(userPreferenceService, never()).get(view, view.instrument.getKey());
    verify(userPreferenceService).get(view, view.samplesCount.getKey());
    verify(userPreferenceService).get(view, view.samples.getKey());
    verify(userPreferenceService).get(view, view.status.getKey());
    verify(userPreferenceService, never()).get(view, view.hidden.getKey());
    HeaderRow header = view.submissions.getHeaderRows().get(0);
    assertTrue(view.view.isVisible());
    assertTrue(view.experiment.isVisible());
    assertFalse(view.user.isVisible());
    assertFalse(view.director.isVisible());
    assertFalse(view.service.isVisible());
    assertTrue(view.dataAvailableDate.isVisible());
    assertTrue(view.date.isVisible());
    assertFalse(view.instrument.isVisible());
    assertTrue(view.samplesCount.isVisible());
    assertFalse(view.samples.isVisible());
    assertTrue(view.status.isVisible());
    assertFalse(view.hidden.isVisible());
    test(view.hideColumnsContextMenu).open();
    assertFalse(test(view.hideColumnsContextMenu).find(MenuItem.class)
        .withText(header.getCell(view.experiment).getText()).exists());
    assertFalse(test(view.hideColumnsContextMenu).find(MenuItem.class)
        .withText(header.getCell(view.user).getText()).exists());
    assertFalse(test(view.hideColumnsContextMenu).find(MenuItem.class)
        .withText(header.getCell(view.director).getText()).exists());
    assertFalse(test(view.hideColumnsContextMenu).find(MenuItem.class)
        .withText(header.getCell(view.service).getText()).exists());
    assertTrue(test(view.hideColumnsContextMenu).isItemChecked(
        header.getCell(view.dataAvailableDate).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.dataAvailableDate).getText());
    verify(userPreferenceService).save(view, view.dataAvailableDate.getKey(), false);
    assertFalse(test(view.hideColumnsContextMenu).isItemChecked(
        header.getCell(view.dataAvailableDate).getText()));
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.date).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.date).getText());
    verify(userPreferenceService).save(view, view.date.getKey(), false);
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.date).getText()));
    assertFalse(test(view.hideColumnsContextMenu).find(MenuItem.class)
        .withText(header.getCell(view.instrument).getText()).exists());
    assertTrue(test(view.hideColumnsContextMenu).isItemChecked(
        header.getCell(view.samplesCount).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.samplesCount).getText());
    verify(userPreferenceService).save(view, view.samplesCount.getKey(), false);
    assertFalse(test(view.hideColumnsContextMenu).isItemChecked(
        header.getCell(view.samplesCount).getText()));
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.samples).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.samples).getText());
    verify(userPreferenceService).save(view, view.samples.getKey(), true);
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.samples).getText()));
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.status).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.status).getText());
    verify(userPreferenceService).save(view, view.status.getKey(), false);
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.status).getText()));
    assertFalse(test(view.hideColumnsContextMenu).find(MenuItem.class)
        .withText(header.getCell(view.hidden).getText()).exists());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void submissions_HiddenColumnsManager() {
    verify(userPreferenceService, never()).get(view, view.experiment.getKey());
    verify(userPreferenceService).get(view, view.user.getKey());
    verify(userPreferenceService, never()).get(view, view.director.getKey());
    verify(userPreferenceService, never()).get(view, view.service.getKey());
    verify(userPreferenceService).get(view, view.dataAvailableDate.getKey());
    verify(userPreferenceService).get(view, view.date.getKey());
    verify(userPreferenceService, never()).get(view, view.instrument.getKey());
    verify(userPreferenceService).get(view, view.samplesCount.getKey());
    verify(userPreferenceService).get(view, view.samples.getKey());
    verify(userPreferenceService).get(view, view.status.getKey());
    verify(userPreferenceService, never()).get(view, view.hidden.getKey());
    HeaderRow header = view.submissions.getHeaderRows().get(0);
    assertTrue(view.view.isVisible());
    assertTrue(view.experiment.isVisible());
    assertTrue(view.user.isVisible());
    assertFalse(view.director.isVisible());
    assertFalse(view.service.isVisible());
    assertTrue(view.dataAvailableDate.isVisible());
    assertTrue(view.date.isVisible());
    assertFalse(view.instrument.isVisible());
    assertTrue(view.samplesCount.isVisible());
    assertFalse(view.samples.isVisible());
    assertTrue(view.status.isVisible());
    assertFalse(view.hidden.isVisible());
    test(view.hideColumnsContextMenu).open();
    assertFalse(test(view.hideColumnsContextMenu).find(MenuItem.class)
        .withText(header.getCell(view.experiment).getText()).exists());
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.user).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.user).getText());
    verify(userPreferenceService).save(view, view.user.getKey(), false);
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.user).getText()));
    assertFalse(test(view.hideColumnsContextMenu).find(MenuItem.class)
        .withText(header.getCell(view.director).getText()).exists());
    assertFalse(test(view.hideColumnsContextMenu).find(MenuItem.class)
        .withText(header.getCell(view.service).getText()).exists());
    assertTrue(test(view.hideColumnsContextMenu).isItemChecked(
        header.getCell(view.dataAvailableDate).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.dataAvailableDate).getText());
    verify(userPreferenceService).save(view, view.dataAvailableDate.getKey(), false);
    assertFalse(test(view.hideColumnsContextMenu).isItemChecked(
        header.getCell(view.dataAvailableDate).getText()));
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.date).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.date).getText());
    verify(userPreferenceService).save(view, view.date.getKey(), false);
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.date).getText()));
    assertFalse(test(view.hideColumnsContextMenu).find(MenuItem.class)
        .withText(header.getCell(view.instrument).getText()).exists());
    assertTrue(test(view.hideColumnsContextMenu).isItemChecked(
        header.getCell(view.samplesCount).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.samplesCount).getText());
    verify(userPreferenceService).save(view, view.samplesCount.getKey(), false);
    assertFalse(test(view.hideColumnsContextMenu).isItemChecked(
        header.getCell(view.samplesCount).getText()));
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.samples).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.samples).getText());
    verify(userPreferenceService).save(view, view.samples.getKey(), true);
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.samples).getText()));
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.status).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.status).getText());
    verify(userPreferenceService).save(view, view.status.getKey(), false);
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.status).getText()));
    assertFalse(test(view.hideColumnsContextMenu).find(MenuItem.class)
        .withText(header.getCell(view.hidden).getText()).exists());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void submissions_HiddenColumnsAdmin() {
    verify(userPreferenceService, never()).get(view, view.experiment.getKey());
    verify(userPreferenceService).get(view, view.user.getKey());
    verify(userPreferenceService).get(view, view.director.getKey());
    verify(userPreferenceService).get(view, view.service.getKey());
    verify(userPreferenceService).get(view, view.dataAvailableDate.getKey());
    verify(userPreferenceService).get(view, view.date.getKey());
    verify(userPreferenceService).get(view, view.instrument.getKey());
    verify(userPreferenceService).get(view, view.samplesCount.getKey());
    verify(userPreferenceService).get(view, view.samples.getKey());
    verify(userPreferenceService).get(view, view.status.getKey());
    verify(userPreferenceService).get(view, view.hidden.getKey());
    HeaderRow header = view.submissions.getHeaderRows().get(0);
    assertTrue(view.view.isVisible());
    assertTrue(view.experiment.isVisible());
    assertTrue(view.user.isVisible());
    assertTrue(view.director.isVisible());
    assertTrue(view.service.isVisible());
    assertTrue(view.dataAvailableDate.isVisible());
    assertTrue(view.date.isVisible());
    assertTrue(view.instrument.isVisible());
    assertTrue(view.samplesCount.isVisible());
    assertFalse(view.samples.isVisible());
    assertTrue(view.status.isVisible());
    assertTrue(view.hidden.isVisible());
    test(view.hideColumnsContextMenu).open();
    assertFalse(test(view.hideColumnsContextMenu).find(MenuItem.class)
        .withText(header.getCell(view.experiment).getText()).exists());
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.user).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.user).getText());
    verify(userPreferenceService).save(view, view.user.getKey(), false);
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.user).getText()));
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.director).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.director).getText());
    verify(userPreferenceService).save(view, view.director.getKey(), false);
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.director).getText()));
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.service).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.service).getText());
    verify(userPreferenceService).save(view, view.service.getKey(), false);
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.service).getText()));
    assertTrue(test(view.hideColumnsContextMenu).isItemChecked(
        header.getCell(view.dataAvailableDate).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.dataAvailableDate).getText());
    verify(userPreferenceService).save(view, view.dataAvailableDate.getKey(), false);
    assertFalse(test(view.hideColumnsContextMenu).isItemChecked(
        header.getCell(view.dataAvailableDate).getText()));
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.date).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.date).getText());
    verify(userPreferenceService).save(view, view.date.getKey(), false);
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.date).getText()));
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.instrument).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.instrument).getText());
    verify(userPreferenceService).save(view, view.instrument.getKey(), false);
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.instrument).getText()));
    assertTrue(test(view.hideColumnsContextMenu).isItemChecked(
        header.getCell(view.samplesCount).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.samplesCount).getText());
    verify(userPreferenceService).save(view, view.samplesCount.getKey(), false);
    assertFalse(test(view.hideColumnsContextMenu).isItemChecked(
        header.getCell(view.samplesCount).getText()));
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.samples).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.samples).getText());
    verify(userPreferenceService).save(view, view.samples.getKey(), true);
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.samples).getText()));
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.status).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.status).getText());
    verify(userPreferenceService).save(view, view.status.getKey(), false);
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.status).getText()));
    assertTrue(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.hidden).getText()));
    test(view.hideColumnsContextMenu).clickItem(header.getCell(view.hidden).getText());
    verify(userPreferenceService).save(view, view.hidden.getKey(), false);
    assertFalse(
        test(view.hideColumnsContextMenu).isItemChecked(header.getCell(view.hidden).getText()));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void singleClickSubmission() {
    view.submissions.setItems(submissions);
    test(view.submissions).clickRow(0);

    assertEquals(0, $(SubmissionDialog.class).all().size());
    assertEquals(0, $(SamplesStatusDialog.class).all().size());
  }

  @Test
  public void singleClickSubmission_ShiftUser() {
    view.submissions.setItems(submissions);
    test(view.submissions).clickRow(0, new MetaKeys().shift());

    assertFalse($(SamplesStatusDialog.class).exists());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void singleClickSubmission_ShiftAdmin() {
    view.submissions.setItems(submissions);
    Submission submission = submissions.get(19);
    when(service.get(anyLong())).thenReturn(Optional.of(submission));
    test(view.submissions).clickRow(0, new MetaKeys().shift());

    verify(service).get(164L);
    SamplesStatusDialog dialog = $(SamplesStatusDialog.class).first();
    assertEquals(164L, dialog.getSubmissionId());
  }

  @Test
  public void singleClickSubmission_ControlUser() {
    view.submissions.setItems(submissions);
    test(view.submissions).clickRow(0, new MetaKeys().ctrl());

    assertFalse($(SamplesStatusDialog.class).exists());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void singleClickSubmission_ControlAdmin() {
    view.submissions.setItems(submissions);
    Submission submission = submissions.get(19);
    when(service.get(anyLong())).thenReturn(Optional.of(submission));
    test(view.submissions).clickRow(0, new MetaKeys().ctrl());

    verify(service).get(164L);
    SamplesStatusDialog dialog = $(SamplesStatusDialog.class).first();
    assertEquals(164L, dialog.getSubmissionId());
  }

  @Test
  public void singleClickSubmission_MetaUser() {
    view.submissions.setItems(submissions);
    test(view.submissions).clickRow(0, new MetaKeys().meta());

    assertFalse($(SamplesStatusDialog.class).exists());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void singleClickSubmission_MetaAdmin() {
    view.submissions.setItems(submissions);
    Submission submission = submissions.get(19);
    when(service.get(anyLong())).thenReturn(Optional.of(submission));
    test(view.submissions).clickRow(0, new MetaKeys().meta());

    verify(service).get(164L);
    SamplesStatusDialog dialog = $(SamplesStatusDialog.class).first();
    assertEquals(164L, dialog.getSubmissionId());
  }

  @Test
  public void singleClickSubmission_AltUser() {
    view.submissions.setItems(submissions);
    test(view.submissions).clickRow(0, new MetaKeys().alt());

    assertFalse($(HistoryView.class).exists());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void singleClickSubmission_AltAdmin() {
    view.submissions.setItems(submissions);
    Submission submission = submissions.get(19);
    when(service.get(anyLong())).thenReturn(Optional.of(submission));
    test(view.submissions).clickRow(0, new MetaKeys().alt());

    verify(service).get(164L);
    HistoryView view = $(HistoryView.class).first();
    assertEquals(164L, view.getSubmissionId());
  }

  @Test
  public void doubleClickSubmission() {
    view.submissions.setItems(submissions);
    Submission submission = submissions.get(19);
    when(service.get(anyLong())).thenReturn(Optional.of(submission));
    test(view.submissions).doubleClickRow(0);

    verify(service).get(164L);
    SubmissionDialog dialog = $(SubmissionDialog.class).first();
    assertEquals(164L, dialog.getSubmissionId());
  }

  @Test
  public void experimentFilter() {
    assertEquals("", view.experimentFilter.getValue());
    assertEquals(ValueChangeMode.EAGER, view.experimentFilter.getValueChangeMode());
  }

  @Test
  public void filterExperiment() {
    view.submissions.setItems(dataProvider);
    view.experimentFilter.setValue("test");

    assertEquals("test", view.filter().experimentContains);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void userFilter() {
    assertEquals("", view.userFilter.getValue());
    assertEquals(ValueChangeMode.EAGER, view.userFilter.getValueChangeMode());
  }

  @Test
  public void filterUser() {
    view.submissions.setItems(dataProvider);
    view.userFilter.setValue("test");

    assertEquals("test", view.filter().userContains);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void directorFilter() {
    assertEquals("", view.directorFilter.getValue());
    assertEquals(ValueChangeMode.EAGER, view.directorFilter.getValueChangeMode());
  }

  @Test
  public void filterDirector() {
    view.submissions.setItems(dataProvider);
    view.directorFilter.setValue("test");

    assertEquals("test", view.filter().directorContains);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void filterDataAvailableDate() {
    view.submissions.setItems(dataProvider);
    Range<LocalDate> range = Range.closed(LocalDate.now().minusDays(1), LocalDate.now());
    view.dataAvailableDateFilter.setValue(range);

    assertEquals(range, view.filter().dataAvailableDateRange);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void filterDate() {
    view.submissions.setItems(dataProvider);
    Range<LocalDate> range = Range.closed(LocalDate.now().minusDays(1), LocalDate.now());
    view.dateFilter.setValue(range);

    assertEquals(range, view.filter().dateRange);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void instrumentFilter() {
    assertNull(view.instrumentFilter.getValue());
    assertTrue(view.instrumentFilter.isClearButtonVisible());
    List<MassDetectionInstrument> instruments = items(view.instrumentFilter);
    assertArrayEquals(MassDetectionInstrument.values(),
        instruments.toArray(new MassDetectionInstrument[0]));
    for (MassDetectionInstrument instrument : instruments) {
      assertEquals(view.getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + instrument.name()),
          view.instrumentFilter.getItemLabelGenerator().apply(instrument));
    }
  }

  @Test
  public void filterInstrument() {
    view.submissions.setItems(dataProvider);
    view.instrumentFilter.setValue(MassDetectionInstrument.VELOS);

    assertEquals(MassDetectionInstrument.VELOS, view.filter().instrument);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void serviceFilter() {
    assertNull(view.serviceFilter.getValue());
    assertTrue(view.serviceFilter.isClearButtonVisible());
    List<Service> values = items(view.serviceFilter);
    assertArrayEquals(Service.values(), values.toArray(new Service[0]));
    for (Service value : values) {
      assertEquals(view.getTranslation(SERVICE_PREFIX + value.name()),
          view.serviceFilter.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void filterService() {
    view.submissions.setItems(dataProvider);
    view.serviceFilter.setValue(Service.INTACT_PROTEIN);

    assertEquals(Service.INTACT_PROTEIN, view.filter().service);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void samplesFilter() {
    assertEquals("", view.samplesFilter.getValue());
    assertEquals(ValueChangeMode.EAGER, view.samplesFilter.getValueChangeMode());
  }

  @Test
  public void filterSamples() {
    view.submissions.setItems(dataProvider);
    view.samplesFilter.setValue("test");

    assertEquals("test", view.filter().anySampleNameContains);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void statusFilter() {
    assertNull(view.statusFilter.getValue());
    assertTrue(view.statusFilter.isClearButtonVisible());
    List<SampleStatus> statuses = items(view.statusFilter);
    assertArrayEquals(SampleStatus.values(), statuses.toArray(new SampleStatus[0]));
    for (SampleStatus status : statuses) {
      assertEquals(view.getTranslation(SAMPLE_STATUS_PREFIX + status.name()),
          view.statusFilter.getItemLabelGenerator().apply(status));
    }
  }

  @Test
  public void filterStatus() {
    view.submissions.setItems(dataProvider);
    view.statusFilter.setValue(SampleStatus.ANALYSED);

    assertEquals(SampleStatus.ANALYSED, view.filter().anySampleStatus);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void hiddenFilter() {
    assertNull(view.hiddenFilter.getValue());
    assertTrue(view.hiddenFilter.isClearButtonVisible());
    List<Boolean> values = items(view.hiddenFilter);
    assertArrayEquals(new Boolean[]{false, true}, values.toArray(new Boolean[0]));
    for (Boolean value : values) {
      assertEquals(view.getTranslation(SUBMISSION_PREFIX + property(HIDDEN, value)),
          view.hiddenFilter.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void filterHidden() {
    view.submissions.setItems(dataProvider);
    view.hiddenFilter.setValue(true);

    assertEquals(true, view.filter().hidden);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void getHeaderText() {
    view = navigate(SubmissionsView.class);
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + EXPERIMENT),
        view.getHeaderText(view.experiment));
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + USER), view.getHeaderText(view.user));
    assertEquals(view.getTranslation(LABORATORY_PREFIX + DIRECTOR),
        view.getHeaderText(view.director));
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + DATA_AVAILABLE_DATE),
        view.getHeaderText(view.dataAvailableDate));
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SUBMISSION_DATE),
        view.getHeaderText(view.date));
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + INSTRUMENT),
        view.getHeaderText(view.instrument));
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SERVICE),
        view.getHeaderText(view.service));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAMPLES_COUNT),
        view.getHeaderText(view.samplesCount));
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + SAMPLES),
        view.getHeaderText(view.samples));
    assertEquals(view.getTranslation(SUBMISSION_SAMPLE_PREFIX + STATUS),
        view.getHeaderText(view.status));
    assertEquals(view.getTranslation(SUBMISSION_PREFIX + HIDDEN), view.getHeaderText(view.hidden));
  }

  @Test
  public void add() {
    test(view.add).click();

    SubmissionView view = $(SubmissionView.class).first();
    assertEquals(0, view.getSubmission().getId());
  }

  @Test
  public void view_Enabled() {
    view.submissions.setItems(submissions);
    assertFalse(view.view.isEnabled());
    test(view.submissions).select(1);
    assertTrue(view.view.isEnabled());
    view.submissions.deselectAll();
    assertFalse(view.view.isEnabled());
  }

  @Test
  public void view() {
    view.submissions.setItems(submissions);
    Submission submission = submissions.get(1);
    when(service.get(anyLong())).thenReturn(Optional.of(submission));
    test(view.submissions).select(17);

    test(view.view).click();

    verify(service).get(32L);
    SubmissionDialog dialog = $(SubmissionDialog.class).first();
    assertEquals(32L, dialog.getSubmissionId());
  }

  @Test
  public void view_NoSelection() {
    view.submissions.setItems(submissions);
    view.view();

    assertFalse($(SamplesStatusDialog.class).exists());
    Notification error = $(Notification.class).first();
    assertInstanceOf(ErrorNotification.class, error);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + property(SUBMISSIONS, REQUIRED)),
        ((ErrorNotification) error).getText());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void editStatus_Enabled() {
    view.submissions.setItems(submissions);
    assertFalse(view.editStatus.isEnabled());
    test(view.submissions).select(1);
    assertTrue(view.editStatus.isEnabled());
    view.submissions.deselectAll();
    assertFalse(view.editStatus.isEnabled());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void editStatus() {
    view.submissions.setItems(submissions);
    Submission submission = submissions.get(1);
    when(service.get(anyLong())).thenReturn(Optional.of(submission));
    test(view.submissions).select(17);

    test(view.editStatus).click();

    verify(service).get(32L);
    SamplesStatusDialog dialog = $(SamplesStatusDialog.class).first();
    assertEquals(32L, dialog.getSubmissionId());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void editStatus_NoSelection() {
    view.submissions.setItems(submissions);
    view.editStatus();

    assertFalse($(SamplesStatusDialog.class).exists());
    Notification error = $(Notification.class).first();
    assertInstanceOf(ErrorNotification.class, error);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + property(SUBMISSIONS, REQUIRED)),
        ((ErrorNotification) error).getText());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void history_Enabled() {
    view.submissions.setItems(submissions);
    assertFalse(view.history.isEnabled());
    test(view.submissions).select(1);
    assertTrue(view.history.isEnabled());
    view.submissions.deselectAll();
    assertFalse(view.history.isEnabled());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void history() {
    view.submissions.setItems(submissions);
    Submission submission = submissions.get(1);
    when(service.get(anyLong())).thenReturn(Optional.of(submission));
    test(view.submissions).select(17);

    test(view.history).click();

    verify(service).get(32L);
    HistoryView view = $(HistoryView.class).first();
    assertEquals(32L, view.getSubmissionId());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void history_NoSelection() {
    view.submissions.setItems(submissions);
    view.history();

    assertFalse($(HistoryView.class).exists());
    Notification error = $(Notification.class).first();
    assertInstanceOf(ErrorNotification.class, error);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + property(SUBMISSIONS, REQUIRED)),
        ((ErrorNotification) error).getText());
  }

  @Test
  public void hideColumns() {
    assertTrue(view.hideColumnsContextMenu.isOpenOnClick());
    assertEquals(view.hideColumns, view.hideColumnsContextMenu.getTarget());
  }
}
