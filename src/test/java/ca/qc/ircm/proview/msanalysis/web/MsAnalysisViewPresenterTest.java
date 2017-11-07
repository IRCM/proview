package ca.qc.ircm.proview.msanalysis.web;

import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.ACQUISITIONS;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.ACQUISITIONS_PANEL;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.ACQUISITION_COUNT;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.ACQUISITION_FILE;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.BAN_CONTAINERS;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.COMMENT;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.CONTAINER;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.CONTAINERS;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.CONTAINERS_PANEL;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.DELETED;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.DOWN;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.EXPLANATION;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.HEADER;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.INVALID_CONTAINERS;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.INVALID_MS_ANALYSIS;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.MASS_DETECTION_INSTRUMENT;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.MS_ANALYSIS_PANEL;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.NO_CONTAINERS;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.REMOVE;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.REMOVED;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.SAMPLE;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.SAMPLE_LIST_NAME;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.SAVE;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.SAVED;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.SAVE_ACQUISITION_REMOVED;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.SOURCE;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.TITLE;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.errorMessage;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.gridItems;
import static ca.qc.ircm.proview.web.WebConstants.BANNED;
import static ca.qc.ircm.proview.web.WebConstants.BUTTON_SKIP_ROW;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static ca.qc.ircm.proview.web.WebConstants.SAVED_SAMPLE_FROM_MULTIPLE_USERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class MsAnalysisViewPresenterTest {
  private MsAnalysisViewPresenter presenter;
  @Mock
  private MsAnalysisView view;
  @Mock
  private MsAnalysisService msAnalysisService;
  @Mock
  private SampleContainerService sampleContainerService;
  @Captor
  private ArgumentCaptor<MsAnalysis> msAnalysisCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;
  private MsAnalysisViewDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(MsAnalysisView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<Sample> samples = new ArrayList<>();
  private List<SampleContainer> containers = new ArrayList<>();
  private List<String> sampleListNames = new ArrayList<>();
  private List<String> acquisitionFiles = new ArrayList<>();
  private List<String> comments = new ArrayList<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter =
        new MsAnalysisViewPresenter(msAnalysisService, sampleContainerService, applicationName);
    design = new MsAnalysisViewDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    samples.add(entityManager.find(Sample.class, 559L));
    samples.add(entityManager.find(Sample.class, 560L));
    samples.add(entityManager.find(Sample.class, 444L));
    containers = samples.stream().flatMap(sample -> {
      Tube tube1 = new Tube(sample.getId(), sample.getName());
      tube1.setSample(sample);
      Tube tube2 = new Tube(sample.getId() + 10, sample.getName() + "_2");
      tube2.setSample(sample);
      return Arrays.asList(tube1, tube2).stream();
    }).collect(Collectors.toList());
    sampleListNames = IntStream.range(0, containers.size() * 2).mapToObj(i -> "sample_list" + i)
        .collect(Collectors.toList());
    acquisitionFiles = IntStream.range(0, containers.size() * 2)
        .mapToObj(i -> "acquisition_file" + i).collect(Collectors.toList());
    comments = IntStream.range(0, containers.size() * 2).mapToObj(i -> "comment" + i)
        .collect(Collectors.toList());
    when(view.savedContainers()).thenReturn(new ArrayList<>(containers));
  }

  private void setFields() {
    final ListDataProvider<Acquisition> acquisitions = dataProvider(design.acquisitions);
    int count = 0;
    for (Acquisition ac : acquisitions.getItems()) {
      TextField field =
          (TextField) design.acquisitions.getColumn(SAMPLE_LIST_NAME).getValueProvider().apply(ac);
      field.setValue(sampleListNames.get(count));
      field =
          (TextField) design.acquisitions.getColumn(ACQUISITION_FILE).getValueProvider().apply(ac);
      field.setValue(acquisitionFiles.get(count));
      field = (TextField) design.acquisitions.getColumn(COMMENT).getValueProvider().apply(ac);
      field.setValue(comments.get(count++));
    }
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
    assertTrue(design.containersPanel.getStyleName().contains(CONTAINERS_PANEL));
    assertTrue(design.containers.getStyleName().contains(CONTAINERS));
    assertTrue(design.containers.getStyleName().contains(COMPONENTS));
    assertTrue(design.acquisitionsPanel.getStyleName().contains(ACQUISITIONS_PANEL));
    assertTrue(design.acquisitions.getStyleName().contains(ACQUISITIONS));
    assertTrue(design.acquisitions.getStyleName().contains(COMPONENTS));
    assertTrue(design.explanationPanel.getStyleName().contains(EXPLANATION_PANEL));
    assertTrue(design.explanation.getStyleName().contains(EXPLANATION));
    assertTrue(design.down.getStyleName().contains(DOWN));
    assertTrue(design.down.getStyleName().contains(BUTTON_SKIP_ROW));
    assertTrue(design.save.getStyleName().contains(SAVE));
    assertTrue(design.save.getStyleName().contains(ValoTheme.BUTTON_PRIMARY));
    assertTrue(design.remove.getStyleName().contains(REMOVE));
    assertTrue(design.remove.getStyleName().contains(ValoTheme.BUTTON_DANGER));
    assertTrue(design.banContainers.getStyleName().contains(BAN_CONTAINERS));
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
    assertEquals(resources.message(CONTAINERS_PANEL), design.containersPanel.getCaption());
    assertEquals(resources.message(ACQUISITIONS_PANEL), design.acquisitionsPanel.getCaption());
    assertEquals(resources.message(EXPLANATION_PANEL), design.explanationPanel.getCaption());
    assertEquals(resources.message(DOWN), design.down.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.down.getIcon());
    assertEquals(resources.message(SAVE), design.save.getCaption());
    assertEquals(resources.message(REMOVE), design.remove.getCaption());
    assertEquals(resources.message(BAN_CONTAINERS), design.banContainers.getCaption());
  }

  @Test
  public void massDetectionInstrument() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(design.massDetectionInstrument.isReadOnly());
    assertFalse(design.massDetectionInstrument.isEmptySelectionAllowed());
    assertEquals(MassDetectionInstrument.VELOS, design.massDetectionInstrument.getValue());
    ListDataProvider<MassDetectionInstrument> instruments =
        dataProvider(design.massDetectionInstrument);
    assertEquals(MassDetectionInstrument.platformChoices().size(), instruments.getItems().size());
    for (MassDetectionInstrument instrument : MassDetectionInstrument.platformChoices()) {
      assertTrue(instruments.getItems().contains(instrument));
      assertEquals(instrument.getLabel(locale),
          design.massDetectionInstrument.getItemCaptionGenerator().apply(instrument));
    }
  }

  @Test
  public void source() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(design.source.isReadOnly());
    assertFalse(design.source.isEmptySelectionAllowed());
    assertEquals(MassDetectionInstrumentSource.ESI, design.source.getValue());
    ListDataProvider<MassDetectionInstrumentSource> sources = dataProvider(design.source);
    assertEquals(MassDetectionInstrumentSource.availables().size(), sources.getItems().size());
    for (MassDetectionInstrumentSource source : MassDetectionInstrumentSource.availables()) {
      assertTrue(sources.getItems().contains(source));
      assertEquals(source.getLabel(locale), design.source.getItemCaptionGenerator().apply(source));
    }
  }

  @Test
  public void containers() {
    presenter.init(view);
    presenter.enter("");

    final ListDataProvider<SampleContainer> containers = dataProvider(design.containers);
    assertEquals(3, design.containers.getColumns().size());
    assertEquals(SAMPLE, design.containers.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE), design.containers.getColumn(SAMPLE).getCaption());
    for (SampleContainer container : containers.getItems()) {
      assertEquals(container.getSample().getName(),
          design.containers.getColumn(SAMPLE).getValueProvider().apply(container));
    }
    assertEquals(CONTAINER, design.containers.getColumns().get(1).getId());
    assertEquals(resources.message(CONTAINER), design.containers.getColumn(CONTAINER).getCaption());
    for (SampleContainer container : containers.getItems()) {
      assertEquals(container.getFullName(),
          design.containers.getColumn(CONTAINER).getValueProvider().apply(container));
      assertEquals(container.isBanned() ? BANNED : "",
          design.containers.getColumn(CONTAINER).getStyleGenerator().apply(container));
    }
    assertEquals(ACQUISITION_COUNT, design.containers.getColumns().get(2).getId());
    assertEquals(resources.message(ACQUISITION_COUNT),
        design.containers.getColumn(ACQUISITION_COUNT).getCaption());
    assertTrue(containsInstanceOf(design.containers.getColumn(ACQUISITION_COUNT).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.containers.getColumn(ACQUISITION_COUNT).isSortable());
    for (SampleContainer container : containers.getItems()) {
      TextField field = (TextField) design.containers.getColumn(ACQUISITION_COUNT)
          .getValueProvider().apply(container);
      assertTrue(field.getStyleName().contains(ACQUISITION_COUNT));
      assertEquals("1", field.getValue());
    }
    assertEquals(this.containers.size(), containers.getItems().size());
    for (SampleContainer container : this.containers) {
      assertTrue(
          containers.getItems().stream().filter(sc -> sc.equals(container)).findAny().isPresent());
      assertTrue(containers.getItems().stream()
          .filter(sc -> sc.getSample().equals(container.getSample())).findAny().isPresent());
    }
  }

  @Test
  public void acquisitions() {
    presenter.init(view);
    presenter.enter("");

    final ListDataProvider<Acquisition> acquisitions = dataProvider(design.acquisitions);
    assertEquals(5, design.acquisitions.getColumns().size());
    assertEquals(SAMPLE, design.acquisitions.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE), design.acquisitions.getColumn(SAMPLE).getCaption());
    for (Acquisition acquisition : acquisitions.getItems()) {
      assertEquals(acquisition.getSample().getName(),
          design.acquisitions.getColumn(SAMPLE).getValueProvider().apply(acquisition));
    }
    assertEquals(CONTAINER, design.acquisitions.getColumns().get(1).getId());
    assertEquals(resources.message(CONTAINER),
        design.acquisitions.getColumn(CONTAINER).getCaption());
    for (Acquisition acquisition : acquisitions.getItems()) {
      assertEquals(acquisition.getContainer().getFullName(),
          design.acquisitions.getColumn(CONTAINER).getValueProvider().apply(acquisition));
      assertEquals(acquisition.getContainer().isBanned() ? BANNED : "",
          design.acquisitions.getColumn(CONTAINER).getStyleGenerator().apply(acquisition));
    }
    assertEquals(SAMPLE_LIST_NAME, design.acquisitions.getColumns().get(2).getId());
    assertEquals(resources.message(SAMPLE_LIST_NAME),
        design.acquisitions.getColumn(SAMPLE_LIST_NAME).getCaption());
    assertTrue(containsInstanceOf(design.acquisitions.getColumn(SAMPLE_LIST_NAME).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.acquisitions.getColumn(SAMPLE_LIST_NAME).isSortable());
    for (Acquisition acquisition : acquisitions.getItems()) {
      TextField field = (TextField) design.acquisitions.getColumn(SAMPLE_LIST_NAME)
          .getValueProvider().apply(acquisition);
      assertTrue(field.getStyleName().contains(SAMPLE_LIST_NAME));
    }
    assertEquals(ACQUISITION_FILE, design.acquisitions.getColumns().get(3).getId());
    assertEquals(resources.message(ACQUISITION_FILE),
        design.acquisitions.getColumn(ACQUISITION_FILE).getCaption());
    assertTrue(containsInstanceOf(design.acquisitions.getColumn(ACQUISITION_FILE).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.acquisitions.getColumn(ACQUISITION_FILE).isSortable());
    for (Acquisition acquisition : acquisitions.getItems()) {
      TextField field = (TextField) design.acquisitions.getColumn(ACQUISITION_FILE)
          .getValueProvider().apply(acquisition);
      assertTrue(field.getStyleName().contains(ACQUISITION_FILE));
    }
    assertEquals(COMMENT, design.acquisitions.getColumns().get(4).getId());
    assertEquals(resources.message(COMMENT), design.acquisitions.getColumn(COMMENT).getCaption());
    assertTrue(containsInstanceOf(design.acquisitions.getColumn(COMMENT).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.acquisitions.getColumn(COMMENT).isSortable());
    for (Acquisition acquisition : acquisitions.getItems()) {
      TextField field =
          (TextField) design.acquisitions.getColumn(COMMENT).getValueProvider().apply(acquisition);
      assertTrue(field.getStyleName().contains(COMMENT));
    }
    assertEquals(containers.size(), acquisitions.getItems().size());
    for (SampleContainer container : containers) {
      assertTrue(acquisitions.getItems().stream().filter(ts -> ts.getContainer().equals(container))
          .findAny().isPresent());
      assertTrue(acquisitions.getItems().stream()
          .filter(ts -> ts.getSample().equals(container.getSample())).findAny().isPresent());
    }
  }

  @Test
  public void changeAcquisitionCount() {
    presenter.init(view);
    presenter.enter("");
    final List<SampleContainer> containers =
        new ArrayList<>(dataProvider(design.containers).getItems());
    SampleContainer firstContainer = containers.get(0);
    TextField field = (TextField) design.containers.getColumn(ACQUISITION_COUNT).getValueProvider()
        .apply(firstContainer);

    field.setValue("2");
    List<Acquisition> acquisitions = new ArrayList<>(dataProvider(design.acquisitions).getItems());
    assertEquals(containers.size() + 1, acquisitions.size());
    assertEquals(firstContainer, acquisitions.get(0).getContainer());
    assertEquals(firstContainer, acquisitions.get(1).getContainer());
    for (int i = 1; i < this.containers.size(); i++) {
      assertEquals(this.containers.get(i), acquisitions.get(i + 1).getContainer());
    }

    field.setValue("1");
    acquisitions = new ArrayList<>(dataProvider(design.acquisitions).getItems());
    assertEquals(containers.size(), acquisitions.size());
    for (int i = 0; i < this.containers.size(); i++) {
      assertEquals(this.containers.get(i), acquisitions.get(i).getContainer());
    }
  }

  @Test
  public void changeAcquisitionCountInvalid() {
    presenter.init(view);
    presenter.enter("");
    final List<SampleContainer> containers =
        new ArrayList<>(dataProvider(design.containers).getItems());
    SampleContainer firstContainer = containers.get(0);
    TextField field = (TextField) design.containers.getColumn(ACQUISITION_COUNT).getValueProvider()
        .apply(firstContainer);
    field.setValue("2");
    field.setValue("a");

    final List<Acquisition> acquisitions =
        new ArrayList<>(dataProvider(design.acquisitions).getItems());
    assertEquals(containers.size() + 1, acquisitions.size());
    assertEquals(firstContainer, acquisitions.get(0).getContainer());
    assertEquals(firstContainer, acquisitions.get(1).getContainer());
    for (int i = 1; i < this.containers.size(); i++) {
      assertEquals(this.containers.get(i), acquisitions.get(i + 1).getContainer());
    }
  }

  @Test
  public void down() {
    presenter.init(view);
    presenter.enter("");
    final ListDataProvider<Acquisition> acquisitions = dataProvider(design.acquisitions);
    Acquisition firstAcquisition = acquisitions.getItems().iterator().next();
    String sampleListName = "sample_list";
    TextField field = (TextField) design.acquisitions.getColumn(SAMPLE_LIST_NAME).getValueProvider()
        .apply(firstAcquisition);
    field.setValue(sampleListName);
    String acquisitionFileWithoutIndex = "acquisition_file_0";
    String acquisitionFile = "acquisition_file_01";
    field = (TextField) design.acquisitions.getColumn(ACQUISITION_FILE).getValueProvider()
        .apply(firstAcquisition);
    field.setValue(acquisitionFile);
    String comment = "test";
    field = (TextField) design.acquisitions.getColumn(COMMENT).getValueProvider()
        .apply(firstAcquisition);
    field.setValue(comment);

    design.down.click();

    int index = 1;
    for (Acquisition acquisition : acquisitions.getItems()) {
      field = (TextField) design.acquisitions.getColumn(SAMPLE_LIST_NAME).getValueProvider()
          .apply(acquisition);
      assertEquals(sampleListName, field.getValue());
      field = (TextField) design.acquisitions.getColumn(ACQUISITION_FILE).getValueProvider()
          .apply(acquisition);
      assertEquals(acquisitionFileWithoutIndex + index++, field.getValue());
      field =
          (TextField) design.acquisitions.getColumn(COMMENT).getValueProvider().apply(acquisition);
      assertEquals(comment, field.getValue());
    }
  }

  @Test
  public void down_OrderedBySampleDesc() {
    presenter.init(view);
    presenter.enter("");
    design.acquisitions.sort(SAMPLE, SortDirection.DESCENDING);
    final List<Acquisition> acquisitions =
        new ArrayList<>(dataProvider(design.acquisitions).getItems());
    Acquisition firstAcquisition = acquisitions.get(4);
    String sampleListName = "sample_list";
    TextField field = (TextField) design.acquisitions.getColumn(SAMPLE_LIST_NAME).getValueProvider()
        .apply(firstAcquisition);
    field.setValue(sampleListName);
    String acquisitionFileWithoutIndex = "acquisition_file_0";
    String acquisitionFile = "acquisition_file_01";
    field = (TextField) design.acquisitions.getColumn(ACQUISITION_FILE).getValueProvider()
        .apply(firstAcquisition);
    field.setValue(acquisitionFile);
    String comment = "test";
    field = (TextField) design.acquisitions.getColumn(COMMENT).getValueProvider()
        .apply(firstAcquisition);
    field.setValue(comment);

    design.down.click();

    int index = 1;
    for (Acquisition acquisition : gridItems(design.acquisitions).collect(Collectors.toList())) {
      field = (TextField) design.acquisitions.getColumn(SAMPLE_LIST_NAME).getValueProvider()
          .apply(acquisition);
      assertEquals(sampleListName, field.getValue());
      field = (TextField) design.acquisitions.getColumn(ACQUISITION_FILE).getValueProvider()
          .apply(acquisition);
      assertEquals(acquisitionFileWithoutIndex + index++, field.getValue());
      field =
          (TextField) design.acquisitions.getColumn(COMMENT).getValueProvider().apply(acquisition);
      assertEquals(comment, field.getValue());
    }
  }

  @Test
  public void down_OrderedByContainerDesc() {
    presenter.init(view);
    presenter.enter("");
    design.acquisitions.sort(CONTAINER, SortDirection.DESCENDING);
    final List<Acquisition> acquisitions =
        new ArrayList<>(dataProvider(design.acquisitions).getItems());
    Acquisition firstAcquisition = acquisitions.get(5);
    String sampleListName = "sample_list";
    TextField field = (TextField) design.acquisitions.getColumn(SAMPLE_LIST_NAME).getValueProvider()
        .apply(firstAcquisition);
    field.setValue(sampleListName);
    String acquisitionFileWithoutIndex = "acquisition_file_0";
    String acquisitionFile = "acquisition_file_01";
    field = (TextField) design.acquisitions.getColumn(ACQUISITION_FILE).getValueProvider()
        .apply(firstAcquisition);
    field.setValue(acquisitionFile);
    String comment = "test";
    field = (TextField) design.acquisitions.getColumn(COMMENT).getValueProvider()
        .apply(firstAcquisition);
    field.setValue(comment);

    design.down.click();

    int index = 1;
    for (Acquisition acquisition : gridItems(design.acquisitions).collect(Collectors.toList())) {
      field = (TextField) design.acquisitions.getColumn(SAMPLE_LIST_NAME).getValueProvider()
          .apply(acquisition);
      assertEquals(sampleListName, field.getValue());
      field = (TextField) design.acquisitions.getColumn(ACQUISITION_FILE).getValueProvider()
          .apply(acquisition);
      assertEquals(acquisitionFileWithoutIndex + index++, field.getValue());
      field =
          (TextField) design.acquisitions.getColumn(COMMENT).getValueProvider().apply(acquisition);
      assertEquals(comment, field.getValue());
    }
  }

  @Test
  public void save_NoContainers() {
    when(view.savedContainers()).thenReturn(new ArrayList<>());
    presenter.init(view);
    presenter.enter("");

    design.save.click();

    verify(view).showError(resources.message(NO_CONTAINERS));
    verify(msAnalysisService, never()).insert(any());
  }

  @Test
  public void save_MissingAcquisitionCount() {
    presenter.init(view);
    presenter.enter("");
    final List<SampleContainer> containers =
        new ArrayList<>(dataProvider(design.containers).getItems());
    SampleContainer firstContainer = containers.get(0);
    TextField field = (TextField) design.containers.getColumn(ACQUISITION_COUNT).getValueProvider()
        .apply(firstContainer);
    field.setValue("");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(msAnalysisService, never()).insert(any());
  }

  @Test
  public void save_InvalidAcquisitionCount() {
    presenter.init(view);
    presenter.enter("");
    final List<SampleContainer> containers =
        new ArrayList<>(dataProvider(design.containers).getItems());
    SampleContainer firstContainer = containers.get(0);
    TextField field = (TextField) design.containers.getColumn(ACQUISITION_COUNT).getValueProvider()
        .apply(firstContainer);
    field.setValue("a");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(msAnalysisService, never()).insert(any());
  }

  @Test
  public void save_AcquisitionCountBelowOne() {
    presenter.init(view);
    presenter.enter("");
    final List<SampleContainer> containers =
        new ArrayList<>(dataProvider(design.containers).getItems());
    SampleContainer firstContainer = containers.get(0);
    TextField field = (TextField) design.containers.getColumn(ACQUISITION_COUNT).getValueProvider()
        .apply(firstContainer);
    field.setValue("-1");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 1, Integer.MAX_VALUE)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(msAnalysisService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleListName() {
    presenter.init(view);
    presenter.enter("");
    final ListDataProvider<Acquisition> acquisitions = dataProvider(design.acquisitions);
    Acquisition firstAcquisition = acquisitions.getItems().iterator().next();
    TextField field = (TextField) design.acquisitions.getColumn(SAMPLE_LIST_NAME).getValueProvider()
        .apply(firstAcquisition);

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(msAnalysisService, never()).insert(any());
  }

  @Test
  public void save_MissingAcquisitionFile() {
    presenter.init(view);
    presenter.enter("");
    final ListDataProvider<Acquisition> acquisitions = dataProvider(design.acquisitions);
    Acquisition firstAcquisition = acquisitions.getItems().iterator().next();
    TextField field = (TextField) design.acquisitions.getColumn(ACQUISITION_FILE).getValueProvider()
        .apply(firstAcquisition);

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(msAnalysisService, never()).insert(any());
  }

  @Test
  public void save() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    doAnswer(i -> {
      MsAnalysis msAnalysis = i.getArgumentAt(0, MsAnalysis.class);
      assertNull(msAnalysis.getId());
      msAnalysis.setId(4L);
      return null;
    }).when(msAnalysisService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(msAnalysisService).insert(msAnalysisCaptor.capture());
    MsAnalysis msAnalysis = msAnalysisCaptor.getValue();
    assertEquals(MassDetectionInstrument.VELOS, msAnalysis.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, msAnalysis.getSource());
    assertEquals(containers.size(), msAnalysis.getAcquisitions().size());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      Acquisition acquisition = msAnalysis.getAcquisitions().get(i);
      assertEquals(container.getSample(), acquisition.getSample());
      assertEquals(container, acquisition.getContainer());
      assertEquals((Integer) 1, acquisition.getNumberOfAcquisition());
      assertEquals((Integer) i, acquisition.getListIndex());
      assertEquals(null, acquisition.getPosition());
      assertEquals(sampleListNames.get(i), acquisition.getSampleListName());
      assertEquals(acquisitionFiles.get(i), acquisition.getAcquisitionFile());
      assertEquals(comments.get(i), acquisition.getComment());
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).navigateTo(MsAnalysisView.VIEW_NAME, "4");
  }

  @Test
  public void save_IllegalArgumentException() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    doThrow(new IllegalArgumentException()).when(msAnalysisService).insert(any());

    design.save.click();

    verify(view).showError(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
    verify(view, never()).showTrayNotification(any());
    verify(view, never()).navigateTo(any(), any());
  }

  @Test
  public void save_MultipleAcquisitions() {
    presenter.init(view);
    presenter.enter("");
    for (SampleContainer sc : dataProvider(design.containers).getItems()) {
      TextField field =
          (TextField) design.containers.getColumn(ACQUISITION_COUNT).getValueProvider().apply(sc);
      field.setValue("2");
    }
    setFields();
    doAnswer(i -> {
      MsAnalysis msAnalysis = i.getArgumentAt(0, MsAnalysis.class);
      assertNull(msAnalysis.getId());
      msAnalysis.setId(4L);
      return null;
    }).when(msAnalysisService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(msAnalysisService).insert(msAnalysisCaptor.capture());
    MsAnalysis msAnalysis = msAnalysisCaptor.getValue();
    assertEquals(MassDetectionInstrument.VELOS, msAnalysis.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, msAnalysis.getSource());
    assertEquals(containers.size() * 2, msAnalysis.getAcquisitions().size());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      Acquisition acquisition = msAnalysis.getAcquisitions().get(i * 2);
      assertEquals(container.getSample(), acquisition.getSample());
      assertEquals(container, acquisition.getContainer());
      assertEquals((Integer) 2, acquisition.getNumberOfAcquisition());
      assertEquals(i * 2, acquisition.getListIndex().intValue());
      assertEquals(null, acquisition.getPosition());
      assertEquals(sampleListNames.get(i * 2), acquisition.getSampleListName());
      assertEquals(acquisitionFiles.get(i * 2), acquisition.getAcquisitionFile());
      assertEquals(comments.get(i * 2), acquisition.getComment());
      acquisition = msAnalysis.getAcquisitions().get(i * 2 + 1);
      assertEquals(container.getSample(), acquisition.getSample());
      assertEquals(container, acquisition.getContainer());
      assertEquals((Integer) 2, acquisition.getNumberOfAcquisition());
      assertEquals(i * 2 + 1, acquisition.getListIndex().intValue());
      assertEquals(null, acquisition.getPosition());
      assertEquals(sampleListNames.get(i * 2 + 1), acquisition.getSampleListName());
      assertEquals(acquisitionFiles.get(i * 2 + 1), acquisition.getAcquisitionFile());
      assertEquals(comments.get(i * 2 + 1), acquisition.getComment());
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).navigateTo(MsAnalysisView.VIEW_NAME, "4");
  }

  @Test
  public void save_ReducedAcquisitions() {
    presenter.init(view);
    presenter.enter("");
    for (SampleContainer sc : dataProvider(design.containers).getItems()) {
      TextField field =
          (TextField) design.containers.getColumn(ACQUISITION_COUNT).getValueProvider().apply(sc);
      field.setValue("2");
    }
    for (SampleContainer sc : dataProvider(design.containers).getItems()) {
      TextField field =
          (TextField) design.containers.getColumn(ACQUISITION_COUNT).getValueProvider().apply(sc);
      field.setValue("1");
    }
    setFields();
    doAnswer(i -> {
      MsAnalysis msAnalysis = i.getArgumentAt(0, MsAnalysis.class);
      assertNull(msAnalysis.getId());
      msAnalysis.setId(4L);
      return null;
    }).when(msAnalysisService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(msAnalysisService).insert(msAnalysisCaptor.capture());
    MsAnalysis msAnalysis = msAnalysisCaptor.getValue();
    assertEquals(MassDetectionInstrument.VELOS, msAnalysis.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, msAnalysis.getSource());
    assertEquals(containers.size(), msAnalysis.getAcquisitions().size());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      Acquisition acquisition = msAnalysis.getAcquisitions().get(i);
      assertEquals(container.getSample(), acquisition.getSample());
      assertEquals(container, acquisition.getContainer());
      assertEquals((Integer) 1, acquisition.getNumberOfAcquisition());
      assertEquals((Integer) i, acquisition.getListIndex());
      assertEquals(null, acquisition.getPosition());
      assertEquals(sampleListNames.get(i), acquisition.getSampleListName());
      assertEquals(acquisitionFiles.get(i), acquisition.getAcquisitionFile());
      assertEquals(comments.get(i), acquisition.getComment());
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).navigateTo(MsAnalysisView.VIEW_NAME, "4");
  }

  @Test
  public void save_Update() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 14L);
    when(msAnalysisService.get(any(Long.class))).thenReturn(msAnalysis);
    presenter.init(view);
    presenter.enter("14");
    design.massDetectionInstrument.setValue(MassDetectionInstrument.ORBITRAP_FUSION);
    setFields();
    design.explanation.setValue("test explanation");

    design.save.click();

    verify(view, never()).showError(any());
    verify(msAnalysisService).update(msAnalysisCaptor.capture(), eq("test explanation"));
    MsAnalysis savedMsAnalysis = msAnalysisCaptor.getValue();
    assertEquals((Long) 14L, savedMsAnalysis.getId());
    assertEquals(MassDetectionInstrument.ORBITRAP_FUSION,
        savedMsAnalysis.getMassDetectionInstrument());
    assertEquals(msAnalysis.getAcquisitions().size(), savedMsAnalysis.getAcquisitions().size());
    for (int i = 0; i < msAnalysis.getAcquisitions().size(); i++) {
      Acquisition original = msAnalysis.getAcquisitions().get(i);
      Acquisition acquisition = savedMsAnalysis.getAcquisitions().get(i);
      assertEquals(original.getId(), acquisition.getId());
      assertEquals(original.getSample(), acquisition.getSample());
      assertEquals(original.getContainer(), acquisition.getContainer());
      assertEquals(original.getNumberOfAcquisition(), acquisition.getNumberOfAcquisition());
      assertEquals(original.getListIndex(), acquisition.getListIndex());
      assertEquals(original.getPosition(), acquisition.getPosition());
      assertEquals(sampleListNames.get(i), acquisition.getSampleListName());
      assertEquals(acquisitionFiles.get(i), acquisition.getAcquisitionFile());
      assertEquals(comments.get(i), acquisition.getComment());
    }
    verify(view).showTrayNotification(resources.message(SAVED, msAnalysis.getAcquisitions().stream()
        .map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(MsAnalysisView.VIEW_NAME, "14");
  }

  @Test
  public void save_Update_IllegalArgumentException() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 14L);
    when(msAnalysisService.get(any(Long.class))).thenReturn(msAnalysis);
    doThrow(new IllegalArgumentException()).when(msAnalysisService).update(any(), any());
    presenter.init(view);
    presenter.enter("14");
    design.massDetectionInstrument.setValue(MassDetectionInstrument.ORBITRAP_FUSION);
    setFields();
    design.explanation.setValue("test explanation");

    design.save.click();

    verify(view).showError(resources.message(SAVE_ACQUISITION_REMOVED));
    verify(view, never()).showTrayNotification(any());
    verify(view, never()).navigateTo(any(), any());
  }

  @Test
  public void remove_NoExplanation() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 14L);
    when(msAnalysisService.get(any(Long.class))).thenReturn(msAnalysis);
    presenter.init(view);
    presenter.enter("14");

    design.remove.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.explanation.getErrorMessage().getFormattedHtmlMessage());
    verify(msAnalysisService, never()).undoFailed(any(), any(), anyBoolean());
  }

  @Test
  public void remove() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 14L);
    when(msAnalysisService.get(any(Long.class))).thenReturn(msAnalysis);
    presenter.init(view);
    presenter.enter("14");
    setFields();
    design.explanation.setValue("test explanation");

    design.remove.click();

    verify(view, never()).showError(any());
    verify(msAnalysisService).undoFailed(msAnalysisCaptor.capture(), eq("test explanation"),
        eq(false));
    MsAnalysis savedMsAnalysis = msAnalysisCaptor.getValue();
    assertEquals((Long) 14L, savedMsAnalysis.getId());
    verify(view).showTrayNotification(resources.message(REMOVED, msAnalysis.getAcquisitions()
        .stream().map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(MsAnalysisView.VIEW_NAME, "14");
  }

  @Test
  public void remove_BanContainers() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 14L);
    when(msAnalysisService.get(any(Long.class))).thenReturn(msAnalysis);
    presenter.init(view);
    presenter.enter("14");
    setFields();
    design.explanation.setValue("test explanation");
    design.banContainers.setValue(true);

    design.remove.click();

    verify(view, never()).showError(any());
    verify(msAnalysisService).undoFailed(msAnalysisCaptor.capture(), eq("test explanation"),
        eq(true));
    MsAnalysis savedMsAnalysis = msAnalysisCaptor.getValue();
    assertEquals((Long) 14L, savedMsAnalysis.getId());
    verify(view).showTrayNotification(resources.message(REMOVED, msAnalysis.getAcquisitions()
        .stream().map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(MsAnalysisView.VIEW_NAME, "14");
  }

  @Test
  public void enter() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(design.deleted.isVisible());
    assertFalse(design.massDetectionInstrument.isReadOnly());
    assertFalse(design.source.isReadOnly());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.save.isVisible());
    assertFalse(design.removeLayout.isVisible());
    List<SampleContainer> containers = new ArrayList<>(dataProvider(design.containers).getItems());
    for (int i = 0; i < this.containers.size(); i++) {
      SampleContainer container = this.containers.get(i);
      SampleContainer gridContainer = containers.get(i);
      assertEquals(container.getSample(), gridContainer.getSample());
      assertEquals(container, gridContainer);
    }
    for (SampleContainer container : containers) {
      TextField field = (TextField) design.containers.getColumn(ACQUISITION_COUNT)
          .getValueProvider().apply(container);
      assertFalse(field.isReadOnly());
    }
    List<Acquisition> acquisitions = new ArrayList<>(dataProvider(design.acquisitions).getItems());
    for (int i = 0; i < this.containers.size(); i++) {
      SampleContainer container = this.containers.get(i);
      Acquisition acquisition = acquisitions.get(i);
      assertEquals(container.getSample(), acquisition.getSample());
      assertEquals(container, acquisition.getContainer());
    }
    for (Acquisition acquisition : acquisitions) {
      TextField field = (TextField) design.acquisitions.getColumn(SAMPLE_LIST_NAME)
          .getValueProvider().apply(acquisition);
      assertFalse(field.isReadOnly());
      field = (TextField) design.acquisitions.getColumn(ACQUISITION_FILE).getValueProvider()
          .apply(acquisition);
      assertFalse(field.isReadOnly());
      field =
          (TextField) design.acquisitions.getColumn(COMMENT).getValueProvider().apply(acquisition);
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void enter_SavedContainersFromMultipleUsers() {
    when(view.savedContainersFromMultipleUsers()).thenReturn(true);
    presenter.init(view);
    presenter.enter("");

    verify(view).showWarning(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
    List<SampleContainer> containers = new ArrayList<>(dataProvider(design.containers).getItems());
    for (int i = 0; i < this.containers.size(); i++) {
      SampleContainer container = this.containers.get(i);
      SampleContainer gridContainer = containers.get(i);
      assertEquals(container.getSample(), gridContainer.getSample());
      assertEquals(container, gridContainer);
    }
    for (SampleContainer container : containers) {
      TextField field = (TextField) design.containers.getColumn(ACQUISITION_COUNT)
          .getValueProvider().apply(container);
      assertFalse(field.isReadOnly());
    }
    List<Acquisition> acquisitions = new ArrayList<>(dataProvider(design.acquisitions).getItems());
    for (int i = 0; i < this.containers.size(); i++) {
      SampleContainer container = this.containers.get(i);
      Acquisition acquisition = acquisitions.get(i);
      assertEquals(container.getSample(), acquisition.getSample());
      assertEquals(container, acquisition.getContainer());
    }
    for (Acquisition acquisition : acquisitions) {
      TextField field = (TextField) design.acquisitions.getColumn(SAMPLE_LIST_NAME)
          .getValueProvider().apply(acquisition);
      assertFalse(field.isReadOnly());
      field = (TextField) design.acquisitions.getColumn(ACQUISITION_FILE).getValueProvider()
          .apply(acquisition);
      assertFalse(field.isReadOnly());
      field =
          (TextField) design.acquisitions.getColumn(COMMENT).getValueProvider().apply(acquisition);
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void enter_MsAnalysis() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 14L);
    when(msAnalysisService.get(any(Long.class))).thenReturn(msAnalysis);
    presenter.init(view);
    presenter.enter("14");

    verify(msAnalysisService).get(14L);
    assertFalse(design.deleted.isVisible());
    assertFalse(design.massDetectionInstrument.isReadOnly());
    assertFalse(design.source.isReadOnly());
    assertTrue(design.explanationPanel.isVisible());
    assertTrue(design.save.isVisible());
    assertTrue(design.removeLayout.isVisible());
    assertEquals(msAnalysis.getMassDetectionInstrument(),
        design.massDetectionInstrument.getValue());
    assertEquals(msAnalysis.getSource(), design.source.getValue());
    List<SampleContainer> containers = new ArrayList<>(dataProvider(design.containers).getItems());
    assertEquals(msAnalysis.getAcquisitions().size(), containers.size());
    for (int i = 0; i < msAnalysis.getAcquisitions().size(); i++) {
      SampleContainer container = msAnalysis.getAcquisitions().get(i).getContainer();
      SampleContainer gridContainer = containers.get(i);
      assertEquals(container.getSample(), gridContainer.getSample());
      assertEquals(container, gridContainer);
    }
    for (SampleContainer container : containers) {
      TextField field = (TextField) design.containers.getColumn(ACQUISITION_COUNT)
          .getValueProvider().apply(container);
      assertFalse(field.isReadOnly());
      assertEquals("1", field.getValue());
    }
    List<Acquisition> acquisitions = new ArrayList<>(dataProvider(design.acquisitions).getItems());
    assertEquals(msAnalysis.getAcquisitions().size(), acquisitions.size());
    for (int i = 0; i < msAnalysis.getAcquisitions().size(); i++) {
      assertEquals(msAnalysis.getAcquisitions().get(i), acquisitions.get(i));
    }
    for (Acquisition acquisition : acquisitions) {
      TextField field = (TextField) design.acquisitions.getColumn(SAMPLE_LIST_NAME)
          .getValueProvider().apply(acquisition);
      assertFalse(field.isReadOnly());
      field = (TextField) design.acquisitions.getColumn(ACQUISITION_FILE).getValueProvider()
          .apply(acquisition);
      assertFalse(field.isReadOnly());
      field =
          (TextField) design.acquisitions.getColumn(COMMENT).getValueProvider().apply(acquisition);
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void enter_MsAnalysisMultipleAcquisitionPerSample() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 14L);
    msAnalysis.getAcquisitions().get(0).setNumberOfAcquisition(2);
    Acquisition newAcquisition = new Acquisition();
    newAcquisition.setMsAnalysis(msAnalysis);
    newAcquisition.setSample(msAnalysis.getAcquisitions().get(0).getSample());
    newAcquisition.setContainer(msAnalysis.getAcquisitions().get(0).getContainer());
    newAcquisition.setSampleListName(msAnalysis.getAcquisitions().get(0).getSampleListName());
    newAcquisition.setAcquisitionFile(msAnalysis.getAcquisitions().get(0).getAcquisitionFile() + 1);
    newAcquisition.setComment(msAnalysis.getAcquisitions().get(0).getComment());
    newAcquisition.setListIndex(msAnalysis.getAcquisitions().size() + 1);
    newAcquisition.setNumberOfAcquisition(2);
    newAcquisition.setPosition(2);
    msAnalysis.getAcquisitions().add(newAcquisition);
    when(msAnalysisService.get(any(Long.class))).thenReturn(msAnalysis);
    presenter.init(view);
    presenter.enter("14");

    verify(msAnalysisService).get(14L);
    assertFalse(design.deleted.isVisible());
    assertFalse(design.massDetectionInstrument.isReadOnly());
    assertFalse(design.source.isReadOnly());
    assertTrue(design.explanationPanel.isVisible());
    assertTrue(design.save.isVisible());
    assertTrue(design.removeLayout.isVisible());
    assertEquals(msAnalysis.getMassDetectionInstrument(),
        design.massDetectionInstrument.getValue());
    assertEquals(msAnalysis.getSource(), design.source.getValue());
    List<SampleContainer> containers = new ArrayList<>(dataProvider(design.containers).getItems());
    assertEquals(msAnalysis.getAcquisitions().size() - 1, containers.size());
    for (int i = 0; i < msAnalysis.getAcquisitions().size() - 1; i++) {
      SampleContainer container = msAnalysis.getAcquisitions().get(i).getContainer();
      SampleContainer gridContainer = containers.get(i);
      assertEquals(container.getSample(), gridContainer.getSample());
      assertEquals(container, gridContainer);
    }
    for (SampleContainer container : containers) {
      TextField field = (TextField) design.containers.getColumn(ACQUISITION_COUNT)
          .getValueProvider().apply(container);
      assertFalse(field.isReadOnly());
      assertEquals(msAnalysis.getAcquisitions().get(0).getContainer() == container ? "2" : "1",
          field.getValue());
    }
    List<Acquisition> acquisitions = new ArrayList<>(dataProvider(design.acquisitions).getItems());
    assertEquals(msAnalysis.getAcquisitions().size(), acquisitions.size());
    for (int i = 0; i < msAnalysis.getAcquisitions().size(); i++) {
      assertEquals(msAnalysis.getAcquisitions().get(i), acquisitions.get(i));
    }
    for (Acquisition acquisition : acquisitions) {
      TextField field = (TextField) design.acquisitions.getColumn(SAMPLE_LIST_NAME)
          .getValueProvider().apply(acquisition);
      assertFalse(field.isReadOnly());
      field = (TextField) design.acquisitions.getColumn(ACQUISITION_FILE).getValueProvider()
          .apply(acquisition);
      assertFalse(field.isReadOnly());
      field =
          (TextField) design.acquisitions.getColumn(COMMENT).getValueProvider().apply(acquisition);
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void enter_MsAnalysisDeleted() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 14L);
    msAnalysis.setDeleted(true);
    when(msAnalysisService.get(any(Long.class))).thenReturn(msAnalysis);
    presenter.init(view);
    presenter.enter("14");

    verify(msAnalysisService).get(14L);
    assertTrue(design.deleted.isVisible());
    assertTrue(design.massDetectionInstrument.isReadOnly());
    assertTrue(design.source.isReadOnly());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.save.isVisible());
    assertFalse(design.removeLayout.isVisible());
    List<SampleContainer> containers = new ArrayList<>(dataProvider(design.containers).getItems());
    assertEquals(msAnalysis.getAcquisitions().size(), containers.size());
    for (int i = 0; i < msAnalysis.getAcquisitions().size(); i++) {
      SampleContainer container = msAnalysis.getAcquisitions().get(i).getContainer();
      SampleContainer gridContainer = containers.get(i);
      assertEquals(container.getSample(), gridContainer.getSample());
      assertEquals(container, gridContainer);
    }
    for (SampleContainer container : containers) {
      TextField field = (TextField) design.containers.getColumn(ACQUISITION_COUNT)
          .getValueProvider().apply(container);
      assertTrue(field.isReadOnly());
      assertEquals("1", field.getValue());
    }
    List<Acquisition> acquisitions = new ArrayList<>(dataProvider(design.acquisitions).getItems());
    assertEquals(msAnalysis.getAcquisitions().size(), acquisitions.size());
    for (int i = 0; i < msAnalysis.getAcquisitions().size(); i++) {
      assertEquals(msAnalysis.getAcquisitions().get(i), acquisitions.get(i));
    }
    for (Acquisition acquisition : acquisitions) {
      TextField field = (TextField) design.acquisitions.getColumn(SAMPLE_LIST_NAME)
          .getValueProvider().apply(acquisition);
      assertTrue(field.isReadOnly());
      field = (TextField) design.acquisitions.getColumn(ACQUISITION_FILE).getValueProvider()
          .apply(acquisition);
      assertTrue(field.isReadOnly());
      field =
          (TextField) design.acquisitions.getColumn(COMMENT).getValueProvider().apply(acquisition);
      assertTrue(field.isReadOnly());
    }
  }

  @Test
  public void enter_MsAnalysisNotId() {
    presenter.init(view);
    presenter.enter("a");

    verify(view).showWarning(resources.message(INVALID_MS_ANALYSIS));
    ListDataProvider<Acquisition> acquisitions = dataProvider(design.acquisitions);
    assertTrue(acquisitions.getItems().isEmpty());
  }

  @Test
  public void enter_MsAnalysisIdNotExists() {
    presenter.init(view);
    presenter.enter("14");

    verify(msAnalysisService).get(14L);
    verify(view).showWarning(resources.message(INVALID_MS_ANALYSIS));
    ListDataProvider<Acquisition> acquisitions = dataProvider(design.acquisitions);
    assertTrue(acquisitions.getItems().isEmpty());
  }

  @Test
  public void enter_Containers() {
    when(sampleContainerService.get(any())).thenAnswer(i -> {
      Long id = i.getArgumentAt(0, Long.class);
      return id != null ? entityManager.find(SampleContainer.class, id) : null;
    });
    List<SampleContainer> containers = new ArrayList<>();
    containers.add(entityManager.find(SampleContainer.class, 11L));
    containers.add(entityManager.find(SampleContainer.class, 12L));
    presenter.init(view);
    presenter.enter("containers/11,12");

    List<SampleContainer> gridContainers =
        new ArrayList<>(dataProvider(design.containers).getItems());
    assertEquals(containers.size(), gridContainers.size());
    for (SampleContainer container : containers) {
      assertTrue(gridContainers.stream().filter(sc -> container == sc).findAny().isPresent());
    }
    ListDataProvider<Acquisition> acquisitions = dataProvider(design.acquisitions);
    assertEquals(containers.size(), acquisitions.getItems().size());
    for (SampleContainer container : containers) {
      assertTrue(acquisitions.getItems().stream().filter(ts -> container == ts.getContainer())
          .findAny().isPresent());
    }
  }

  @Test
  public void enter_ContainersEmpty() {
    presenter.init(view);
    presenter.enter("containers/");

    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    ListDataProvider<Acquisition> acquisitions = dataProvider(design.acquisitions);
    assertTrue(acquisitions.getItems().isEmpty());
  }

  @Test
  public void enter_ContainersNotId() {
    presenter.init(view);
    presenter.enter("containers/11,a");

    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    ListDataProvider<Acquisition> acquisitions = dataProvider(design.acquisitions);
    assertTrue(acquisitions.getItems().isEmpty());
  }

  @Test
  public void enter_ContainersIdNotExists() {
    when(sampleContainerService.get(any())).thenReturn(null);
    presenter.init(view);
    presenter.enter("containers/11,12");

    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    ListDataProvider<Acquisition> acquisitions = dataProvider(design.acquisitions);
    assertTrue(acquisitions.getItems().isEmpty());
  }
}
