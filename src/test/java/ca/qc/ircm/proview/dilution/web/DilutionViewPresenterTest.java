package ca.qc.ircm.proview.dilution.web;

import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.BAN_CONTAINERS;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.COMMENT;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.CONTAINER;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.DELETED;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.DILUTIONS;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.DILUTIONS_PANEL;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.DOWN;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.EXPLANATION;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.HEADER;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.INVALID_CONTAINERS;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.INVALID_DILUTION;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.NO_CONTAINERS;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.REMOVE;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.REMOVED;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.SAMPLE;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.SAVE;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.SAVED;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.SOLVENT;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.SOLVENT_VOLUME;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.SOURCE_VOLUME;
import static ca.qc.ircm.proview.dilution.web.DilutionViewPresenter.TITLE;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.errorMessage;
import static ca.qc.ircm.proview.web.WebConstants.BANNED;
import static ca.qc.ircm.proview.web.WebConstants.BUTTON_SKIP_ROW;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static ca.qc.ircm.proview.web.WebConstants.SAVED_SAMPLE_FROM_MULTIPLE_USERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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

import ca.qc.ircm.proview.dilution.DilutedSample;
import ca.qc.ircm.proview.dilution.Dilution;
import ca.qc.ircm.proview.dilution.DilutionService;
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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class DilutionViewPresenterTest {
  private DilutionViewPresenter presenter;
  @Mock
  private DilutionView view;
  @Mock
  private DilutionService dilutionService;
  @Mock
  private SampleContainerService sampleContainerService;
  @Captor
  private ArgumentCaptor<Dilution> dilutionCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;
  private DilutionViewDesign design = new DilutionViewDesign();
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(DilutionView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<Sample> samples = new ArrayList<>();
  private List<SampleContainer> containers = new ArrayList<>();
  private List<Double> sourceVolumes = new ArrayList<>();
  private List<String> solvents = new ArrayList<>();
  private List<Double> solventVolumes = new ArrayList<>();
  private List<String> comments = new ArrayList<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new DilutionViewPresenter(dilutionService, sampleContainerService, applicationName);
    design = new DilutionViewDesign();
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
    sourceVolumes =
        IntStream.range(0, containers.size()).mapToObj(i -> i * 1.0).collect(Collectors.toList());
    solvents = IntStream.range(0, containers.size()).mapToObj(i -> "solvent" + i)
        .collect(Collectors.toList());
    solventVolumes =
        IntStream.range(0, containers.size()).mapToObj(i -> i * 5.0).collect(Collectors.toList());
    comments = IntStream.range(0, containers.size()).mapToObj(i -> "comment" + i)
        .collect(Collectors.toList());
    when(view.savedContainers()).thenReturn(new ArrayList<>(containers));
  }

  private void setFields() {
    final ListDataProvider<DilutedSample> treatments = dataProvider(design.dilutions);
    int count = 0;
    for (DilutedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider().apply(ts);
      field.setValue(Objects.toString(sourceVolumes.get(count++), ""));
    }
    count = 0;
    for (DilutedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.dilutions.getColumn(SOLVENT).getValueProvider().apply(ts);
      field.setValue(solvents.get(count++));
    }
    count = 0;
    for (DilutedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      field.setValue(Objects.toString(solventVolumes.get(count++), ""));
    }
    count = 0;
    for (DilutedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.dilutions.getColumn(COMMENT).getValueProvider().apply(ts);
      field.setValue(Objects.toString(comments.get(count++), ""));
    }
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.header.getStyleName().contains(HEADER));
    assertTrue(design.header.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.deleted.getStyleName().contains(DELETED));
    assertTrue(design.deleted.getStyleName().contains(ValoTheme.LABEL_FAILURE));
    assertTrue(design.dilutionsPanel.getStyleName().contains(DILUTIONS_PANEL));
    assertTrue(design.dilutions.getStyleName().contains(DILUTIONS));
    assertTrue(design.dilutions.getStyleName().contains(COMPONENTS));
    assertTrue(design.down.getStyleName().contains(DOWN));
    assertTrue(design.down.getStyleName().contains(BUTTON_SKIP_ROW));
    assertTrue(design.explanationPanel.getStyleName().contains(EXPLANATION_PANEL));
    assertTrue(design.explanation.getStyleName().contains(EXPLANATION));
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
    assertEquals(resources.message(DILUTIONS_PANEL), design.dilutionsPanel.getCaption());
    assertEquals(resources.message(DOWN), design.down.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.down.getIcon());
    assertEquals(resources.message(EXPLANATION_PANEL), design.explanationPanel.getCaption());
    assertEquals(resources.message(SAVE), design.save.getCaption());
    assertEquals(resources.message(REMOVE), design.remove.getCaption());
    assertEquals(resources.message(BAN_CONTAINERS), design.banContainers.getCaption());
  }

  @Test
  public void dilutions() {
    containers.get(1).setBanned(true);
    presenter.init(view);
    presenter.enter("");

    final ListDataProvider<DilutedSample> treatments = dataProvider(design.dilutions);
    assertEquals(6, design.dilutions.getColumns().size());
    assertEquals(SAMPLE, design.dilutions.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE), design.dilutions.getColumn(SAMPLE).getCaption());
    for (DilutedSample ts : treatments.getItems()) {
      assertEquals(ts.getSample().getName(),
          design.dilutions.getColumn(SAMPLE).getValueProvider().apply(ts));
    }
    assertEquals(CONTAINER, design.dilutions.getColumns().get(1).getId());
    assertEquals(resources.message(CONTAINER), design.dilutions.getColumn(CONTAINER).getCaption());
    for (DilutedSample ts : treatments.getItems()) {
      assertEquals(ts.getContainer().getFullName(),
          design.dilutions.getColumn(CONTAINER).getValueProvider().apply(ts));
      assertEquals(ts.getContainer().isBanned() ? BANNED : "",
          design.dilutions.getColumn(CONTAINER).getStyleGenerator().apply(ts));
    }
    assertEquals(SOURCE_VOLUME, design.dilutions.getColumns().get(2).getId());
    assertEquals(resources.message(SOURCE_VOLUME),
        design.dilutions.getColumn(SOURCE_VOLUME).getCaption());
    assertTrue(containsInstanceOf(design.dilutions.getColumn(SOURCE_VOLUME).getExtensions(),
        ComponentRenderer.class));
    for (DilutedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider().apply(ts);
      assertTrue(field.getStyleName().contains(SOURCE_VOLUME));
    }
    assertFalse(design.dilutions.getColumn(SOURCE_VOLUME).isSortable());
    assertEquals(SOLVENT, design.dilutions.getColumns().get(3).getId());
    assertEquals(resources.message(SOLVENT), design.dilutions.getColumn(SOLVENT).getCaption());
    assertTrue(containsInstanceOf(design.dilutions.getColumn(SOLVENT).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.dilutions.getColumn(SOLVENT).isSortable());
    for (DilutedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.dilutions.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertTrue(field.getStyleName().contains(SOLVENT));
    }
    assertEquals(SOLVENT_VOLUME, design.dilutions.getColumns().get(4).getId());
    assertEquals(resources.message(SOLVENT_VOLUME),
        design.dilutions.getColumn(SOLVENT_VOLUME).getCaption());
    assertTrue(containsInstanceOf(design.dilutions.getColumn(SOLVENT_VOLUME).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.dilutions.getColumn(SOLVENT_VOLUME).isSortable());
    for (DilutedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertTrue(field.getStyleName().contains(SOLVENT_VOLUME));
    }
    assertEquals(COMMENT, design.dilutions.getColumns().get(5).getId());
    assertEquals(resources.message(COMMENT), design.dilutions.getColumn(COMMENT).getCaption());
    assertTrue(containsInstanceOf(design.dilutions.getColumn(COMMENT).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.dilutions.getColumn(COMMENT).isSortable());
    for (DilutedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.dilutions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertTrue(field.getStyleName().contains(COMMENT));
    }
    assertEquals(containers.size(), treatments.getItems().size());
    for (SampleContainer container : containers) {
      assertTrue(treatments.getItems().stream().filter(ts -> ts.getContainer().equals(container))
          .findAny().isPresent());
      assertTrue(treatments.getItems().stream()
          .filter(ts -> ts.getSample().equals(container.getSample())).findAny().isPresent());
    }
  }

  @Test
  public void down() {
    presenter.init(view);
    presenter.enter("");
    final ListDataProvider<DilutedSample> treatments = dataProvider(design.dilutions);
    DilutedSample firstTs = treatments.getItems().iterator().next();
    String sourceVolume = "2.0";
    TextField field =
        (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider().apply(firstTs);
    field.setValue(sourceVolume);
    String solvent = "test solvent";
    field = (TextField) design.dilutions.getColumn(SOLVENT).getValueProvider().apply(firstTs);
    field.setValue(solvent);
    String solventVolume = "10";
    field =
        (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider().apply(firstTs);
    field.setValue(solventVolume);
    String comment = "test";
    field = (TextField) design.dilutions.getColumn(COMMENT).getValueProvider().apply(firstTs);
    field.setValue(comment);

    design.down.click();

    for (DilutedSample ts : treatments.getItems()) {
      field = (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider().apply(ts);
      assertEquals(sourceVolume, field.getValue());
      field = (TextField) design.dilutions.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertEquals(solvent, field.getValue());
      field = (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertEquals(solventVolume, field.getValue());
      field = (TextField) design.dilutions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertEquals(comment, field.getValue());
    }
  }

  @Test
  public void down_NoContainers() {
    when(view.savedContainers()).thenReturn(new ArrayList<>());
    presenter.init(view);
    presenter.enter("");

    design.down.click();
  }

  @Test
  public void down_OrderedBySampleDesc() {
    presenter.init(view);
    presenter.enter("");
    design.dilutions.sort(SAMPLE, SortDirection.DESCENDING);
    final List<DilutedSample> treatments =
        new ArrayList<>(dataProvider(design.dilutions).getItems());
    String sourceVolume = "2.0";
    TextField field = (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider()
        .apply(treatments.get(4));
    field.setValue(sourceVolume);
    String solvent = "test solvent";
    field =
        (TextField) design.dilutions.getColumn(SOLVENT).getValueProvider().apply(treatments.get(4));
    field.setValue(solvent);
    String solventVolume = "10";
    field = (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider()
        .apply(treatments.get(4));
    field.setValue(solventVolume);
    String comment = "test";
    field =
        (TextField) design.dilutions.getColumn(COMMENT).getValueProvider().apply(treatments.get(4));
    field.setValue(comment);

    design.down.click();

    for (DilutedSample ts : treatments) {
      field = (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider().apply(ts);
      assertEquals(sourceVolume, field.getValue());
      field = (TextField) design.dilutions.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertEquals(solvent, field.getValue());
      field = (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertEquals(solventVolume, field.getValue());
      field = (TextField) design.dilutions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertEquals(comment, field.getValue());
    }
  }

  @Test
  public void down_OrderedByContainerDesc() {
    presenter.init(view);
    presenter.enter("");
    design.dilutions.sort(CONTAINER, SortDirection.DESCENDING);
    final List<DilutedSample> treatments =
        new ArrayList<>(dataProvider(design.dilutions).getItems());
    String sourceVolume = "2.0";
    TextField field = (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider()
        .apply(treatments.get(5));
    field.setValue(sourceVolume);
    String solvent = "test solvent";
    field =
        (TextField) design.dilutions.getColumn(SOLVENT).getValueProvider().apply(treatments.get(5));
    field.setValue(solvent);
    String solventVolume = "10";
    field = (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider()
        .apply(treatments.get(5));
    field.setValue(solventVolume);
    String comment = "test";
    field =
        (TextField) design.dilutions.getColumn(COMMENT).getValueProvider().apply(treatments.get(5));
    field.setValue(comment);

    design.down.click();

    for (DilutedSample ts : treatments) {
      field = (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider().apply(ts);
      assertEquals(sourceVolume, field.getValue());
      field = (TextField) design.dilutions.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertEquals(solvent, field.getValue());
      field = (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertEquals(solventVolume, field.getValue());
      field = (TextField) design.dilutions.getColumn(COMMENT).getValueProvider().apply(ts);
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
    verify(dilutionService, never()).insert(any());
  }

  @Test
  public void save_NoSourceVolume() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    DilutedSample ts = dataProvider(design.dilutions).getItems().iterator().next();
    TextField field =
        (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider().apply(ts);
    field.setValue("");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(dilutionService, never()).insert(any());
  }

  @Test
  public void save_SourceVolumeInvalid() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    DilutedSample ts = dataProvider(design.dilutions).getItems().iterator().next();
    TextField field =
        (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider().apply(ts);
    field.setValue("a");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(dilutionService, never()).insert(any());
  }

  @Test
  public void save_SourceVolumeBelowZero() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    DilutedSample ts = dataProvider(design.dilutions).getItems().iterator().next();
    TextField field =
        (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider().apply(ts);
    field.setValue("-1");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertNotNull(field.getErrorMessage().getFormattedHtmlMessage());
    verify(dilutionService, never()).insert(any());
  }

  @Test
  public void save_NoSolvent() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    DilutedSample ts = dataProvider(design.dilutions).getItems().iterator().next();
    TextField field = (TextField) design.dilutions.getColumn(SOLVENT).getValueProvider().apply(ts);
    field.setValue("");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(dilutionService, never()).insert(any());
  }

  @Test
  public void save_NoSolventVolume() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    DilutedSample ts = dataProvider(design.dilutions).getItems().iterator().next();
    TextField field =
        (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
    field.setValue("");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(dilutionService, never()).insert(any());
  }

  @Test
  public void save_SolventVolumeInvalid() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    DilutedSample ts = dataProvider(design.dilutions).getItems().iterator().next();
    TextField field =
        (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
    field.setValue("a");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(dilutionService, never()).insert(any());
  }

  @Test
  public void save_SolventVolumeBelowZero() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    DilutedSample ts = dataProvider(design.dilutions).getItems().iterator().next();
    TextField field =
        (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
    field.setValue("-1");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertNotNull(field.getErrorMessage().getFormattedHtmlMessage());
    verify(dilutionService, never()).insert(any());
  }

  @Test
  public void save() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    doAnswer(i -> {
      Dilution dilution = i.getArgumentAt(0, Dilution.class);
      assertNull(dilution.getId());
      dilution.setId(4L);
      return null;
    }).when(dilutionService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(dilutionService).insert(dilutionCaptor.capture());
    Dilution dilution = dilutionCaptor.getValue();
    assertEquals(containers.size(), dilution.getTreatmentSamples().size());
    int count = 0;
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      DilutedSample diluted = dilution.getTreatmentSamples().get(i);
      assertEquals(container.getSample(), diluted.getSample());
      assertEquals(container, diluted.getContainer());
      assertEquals(sourceVolumes.get(count), diluted.getSourceVolume(), 0.0001);
      assertEquals(solvents.get(count), diluted.getSolvent());
      assertEquals(solventVolumes.get(count), diluted.getSolventVolume(), 0.0001);
      assertEquals(comments.get(count), diluted.getComment());
      count++;
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).navigateTo(DilutionView.VIEW_NAME, "4");
  }

  @Test
  public void save_IllegalArgumentException() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    doThrow(new IllegalArgumentException()).when(dilutionService).insert(any());

    design.save.click();

    verify(view).showError(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
    verify(view, never()).showTrayNotification(any());
    verify(view, never()).navigateTo(any(), any());
  }

  @Test
  public void save_Update() {
    presenter = new DilutionViewPresenter(dilutionService, sampleContainerService, applicationName);
    Dilution dilution = entityManager.find(Dilution.class, 4L);
    when(dilutionService.get(any())).thenReturn(dilution);
    presenter.init(view);
    presenter.enter("4");
    setFields();
    design.explanation.setValue("test explanation");

    design.save.click();

    verify(view, never()).showError(any());
    verify(dilutionService).update(dilutionCaptor.capture(), eq("test explanation"));
    Dilution savedDilution = dilutionCaptor.getValue();
    assertEquals((Long) 4L, savedDilution.getId());
    assertEquals(dilution.getTreatmentSamples().size(), savedDilution.getTreatmentSamples().size());
    for (int i = 0; i < dilution.getTreatmentSamples().size(); i++) {
      DilutedSample original = dilution.getTreatmentSamples().get(i);
      DilutedSample diluted = savedDilution.getTreatmentSamples().get(i);
      assertEquals(original.getId(), diluted.getId());
      assertEquals(original.getSample(), diluted.getSample());
      assertEquals(original.getContainer(), diluted.getContainer());
      assertEquals(sourceVolumes.get(i), diluted.getSourceVolume(), 0.0001);
      assertEquals(solvents.get(i), diluted.getSolvent());
      assertEquals(solventVolumes.get(i), diluted.getSolventVolume(), 0.0001);
      assertEquals(comments.get(i), diluted.getComment());
    }
    verify(view).showTrayNotification(resources.message(SAVED, dilution.getTreatmentSamples()
        .stream().map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(DilutionView.VIEW_NAME, "4");
  }

  @Test
  public void remove_NoExplanation() {
    presenter = new DilutionViewPresenter(dilutionService, sampleContainerService, applicationName);
    Dilution dilution = entityManager.find(Dilution.class, 4L);
    when(dilutionService.get(any())).thenReturn(dilution);
    presenter.init(view);
    presenter.enter("4");

    design.remove.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.explanation.getErrorMessage().getFormattedHtmlMessage());
    verify(dilutionService, never()).undo(any(), any(), anyBoolean());
  }

  @Test
  public void remove() {
    presenter = new DilutionViewPresenter(dilutionService, sampleContainerService, applicationName);
    Dilution dilution = entityManager.find(Dilution.class, 4L);
    when(dilutionService.get(any())).thenReturn(dilution);
    presenter.init(view);
    presenter.enter("4");
    setFields();
    design.explanation.setValue("test explanation");

    design.remove.click();

    verify(view, never()).showError(any());
    verify(dilutionService).undo(dilutionCaptor.capture(), eq("test explanation"), eq(false));
    Dilution savedDilution = dilutionCaptor.getValue();
    assertEquals((Long) 4L, savedDilution.getId());
    verify(view).showTrayNotification(resources.message(REMOVED, dilution.getTreatmentSamples()
        .stream().map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(DilutionView.VIEW_NAME, "4");
  }

  @Test
  public void remove_BanContainers() {
    presenter = new DilutionViewPresenter(dilutionService, sampleContainerService, applicationName);
    Dilution dilution = entityManager.find(Dilution.class, 4L);
    when(dilutionService.get(any())).thenReturn(dilution);
    presenter.init(view);
    presenter.enter("4");
    setFields();
    design.explanation.setValue("test explanation");
    design.banContainers.setValue(true);

    design.remove.click();

    verify(view, never()).showError(any());
    verify(dilutionService).undo(dilutionCaptor.capture(), eq("test explanation"), eq(true));
    Dilution savedDilution = dilutionCaptor.getValue();
    assertEquals((Long) 4L, savedDilution.getId());
    verify(view).showTrayNotification(resources.message(REMOVED, dilution.getTreatmentSamples()
        .stream().map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(DilutionView.VIEW_NAME, "4");
  }

  @Test
  public void enter() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(design.deleted.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.save.isVisible());
    assertFalse(design.removeLayout.isVisible());
    List<DilutedSample> tss = new ArrayList<>(dataProvider(design.dilutions).getItems());
    assertEquals(containers.size(), tss.size());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      DilutedSample diluted = tss.get(i);
      assertEquals(container.getSample(), diluted.getSample());
      assertEquals(container, diluted.getContainer());
    }
    for (DilutedSample ts : tss) {
      TextField field =
          (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.dilutions.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.dilutions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void enter_SavedContainersFromMultipleUsers() {
    when(view.savedContainersFromMultipleUsers()).thenReturn(true);
    presenter.init(view);
    presenter.enter("");

    verify(view).showWarning(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
    assertFalse(design.deleted.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.save.isVisible());
    assertFalse(design.removeLayout.isVisible());
    List<DilutedSample> tss = new ArrayList<>(dataProvider(design.dilutions).getItems());
    assertEquals(containers.size(), tss.size());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      DilutedSample diluted = tss.get(i);
      assertEquals(container.getSample(), diluted.getSample());
      assertEquals(container, diluted.getContainer());
    }
    for (DilutedSample ts : tss) {
      TextField field =
          (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.dilutions.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.dilutions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void enter_Dilution() {
    presenter = new DilutionViewPresenter(dilutionService, sampleContainerService, applicationName);
    Dilution dilution = entityManager.find(Dilution.class, 4L);
    when(dilutionService.get(any())).thenReturn(dilution);
    presenter.init(view);
    presenter.enter("4");

    verify(dilutionService).get(4L);
    assertFalse(design.deleted.isVisible());
    assertTrue(design.explanationPanel.isVisible());
    assertTrue(design.save.isVisible());
    assertTrue(design.removeLayout.isVisible());
    List<DilutedSample> tss = new ArrayList<>(dataProvider(design.dilutions).getItems());
    assertEquals(dilution.getTreatmentSamples().size(), tss.size());
    for (int i = 0; i < dilution.getTreatmentSamples().size(); i++) {
      assertEquals(dilution.getTreatmentSamples().get(i), tss.get(i));
    }
    for (DilutedSample ts : tss) {
      TextField field =
          (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.dilutions.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.dilutions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void enter_DilutionDeleted() {
    presenter = new DilutionViewPresenter(dilutionService, sampleContainerService, applicationName);
    Dilution dilution = entityManager.find(Dilution.class, 4L);
    dilution.setDeleted(true);
    when(dilutionService.get(any())).thenReturn(dilution);
    presenter.init(view);
    presenter.enter("4");

    verify(dilutionService).get(4L);
    assertTrue(design.deleted.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.save.isVisible());
    assertFalse(design.removeLayout.isVisible());
    List<DilutedSample> tss = new ArrayList<>(dataProvider(design.dilutions).getItems());
    assertEquals(dilution.getTreatmentSamples().size(), tss.size());
    for (int i = 0; i < dilution.getTreatmentSamples().size(); i++) {
      assertEquals(dilution.getTreatmentSamples().get(i), tss.get(i));
    }
    for (DilutedSample ts : tss) {
      TextField field =
          (TextField) design.dilutions.getColumn(SOURCE_VOLUME).getValueProvider().apply(ts);
      assertTrue(field.isReadOnly());
      field = (TextField) design.dilutions.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertTrue(field.isReadOnly());
      field = (TextField) design.dilutions.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertTrue(field.isReadOnly());
      field = (TextField) design.dilutions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertTrue(field.isReadOnly());
    }
  }

  @Test
  public void enter_DilutionNotId() {
    presenter.init(view);
    presenter.enter("a");

    ListDataProvider<DilutedSample> tss = dataProvider(design.dilutions);
    verify(view).showWarning(resources.message(INVALID_DILUTION));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_DilutionIdNotExists() {
    presenter.init(view);
    presenter.enter("6");

    verify(dilutionService).get(6L);
    ListDataProvider<DilutedSample> tss = dataProvider(design.dilutions);
    verify(view).showWarning(resources.message(INVALID_DILUTION));
    assertTrue(tss.getItems().isEmpty());
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

    ListDataProvider<DilutedSample> tss = dataProvider(design.dilutions);
    assertEquals(containers.size(), tss.getItems().size());
    for (SampleContainer container : containers) {
      assertTrue(tss.getItems().stream().filter(ts -> container == ts.getContainer()).findAny()
          .isPresent());
    }
  }

  @Test
  public void enter_ContainersEmpty() {
    presenter.init(view);
    presenter.enter("containers/");

    ListDataProvider<DilutedSample> tss = dataProvider(design.dilutions);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_ContainersNotId() {
    presenter.init(view);
    presenter.enter("containers/11,a");

    ListDataProvider<DilutedSample> tss = dataProvider(design.dilutions);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_ContainersIdNotExists() {
    when(sampleContainerService.get(any())).thenReturn(null);
    presenter.init(view);
    presenter.enter("containers/11,12");

    ListDataProvider<DilutedSample> tss = dataProvider(design.dilutions);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }
}
