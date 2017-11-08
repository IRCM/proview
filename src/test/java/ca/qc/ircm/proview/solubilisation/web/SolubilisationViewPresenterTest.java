package ca.qc.ircm.proview.solubilisation.web;

import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.BAN_CONTAINERS;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.COMMENT;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.CONTAINER;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.DELETED;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.DOWN;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.EXPLANATION;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.HEADER;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.INVALID_CONTAINERS;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.INVALID_SOLUBILISATION;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.NO_CONTAINERS;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.REMOVE;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.REMOVED;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.SAMPLE;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.SAVE;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.SAVED;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.SOLUBILISATIONS;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.SOLUBILISATIONS_PANEL;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.SOLVENT;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.SOLVENT_VOLUME;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.TITLE;
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

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.solubilisation.Solubilisation;
import ca.qc.ircm.proview.solubilisation.SolubilisationService;
import ca.qc.ircm.proview.solubilisation.SolubilisedSample;
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
public class SolubilisationViewPresenterTest {
  private SolubilisationViewPresenter presenter;
  @Mock
  private SolubilisationView view;
  @Mock
  private SolubilisationService solubilisationService;
  @Mock
  private SampleContainerService sampleContainerService;
  @Captor
  private ArgumentCaptor<Solubilisation> solubilisationCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;
  private SolubilisationViewDesign design = new SolubilisationViewDesign();
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SolubilisationView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<Sample> samples = new ArrayList<>();
  private List<SampleContainer> containers = new ArrayList<>();
  private List<String> solvents = new ArrayList<>();
  private List<Double> solventVolumes = new ArrayList<>();
  private List<String> comments = new ArrayList<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SolubilisationViewPresenter(solubilisationService, sampleContainerService,
        applicationName);
    design = new SolubilisationViewDesign();
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
    solvents = IntStream.range(0, containers.size()).mapToObj(i -> "solvent" + i)
        .collect(Collectors.toList());
    solventVolumes =
        IntStream.range(0, containers.size()).mapToObj(i -> i * 5.0).collect(Collectors.toList());
    comments = IntStream.range(0, containers.size()).mapToObj(i -> "comment" + i)
        .collect(Collectors.toList());
    when(view.savedContainers()).thenReturn(new ArrayList<>(containers));
  }

  private void setFields() {
    final ListDataProvider<SolubilisedSample> treatments = dataProvider(design.solubilisations);
    int count = 0;
    for (SolubilisedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.solubilisations.getColumn(SOLVENT).getValueProvider().apply(ts);
      field.setValue(solvents.get(count++));
    }
    count = 0;
    for (SolubilisedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      field.setValue(Objects.toString(solventVolumes.get(count++), ""));
    }
    count = 0;
    for (SolubilisedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.solubilisations.getColumn(COMMENT).getValueProvider().apply(ts);
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
    assertTrue(design.solubilisationsPanel.getStyleName().contains(SOLUBILISATIONS_PANEL));
    assertTrue(design.solubilisations.getStyleName().contains(SOLUBILISATIONS));
    assertTrue(design.solubilisations.getStyleName().contains(COMPONENTS));
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
    assertEquals(resources.message(SOLUBILISATIONS_PANEL),
        design.solubilisationsPanel.getCaption());
    assertEquals(resources.message(DOWN), design.down.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.down.getIcon());
    assertEquals(resources.message(EXPLANATION_PANEL), design.explanationPanel.getCaption());
    assertEquals(resources.message(SAVE), design.save.getCaption());
    assertEquals(resources.message(REMOVE), design.remove.getCaption());
    assertEquals(resources.message(BAN_CONTAINERS), design.banContainers.getCaption());
  }

  @Test
  public void solubilisations() {
    containers.get(1).setBanned(true);
    presenter.init(view);
    presenter.enter("");

    final ListDataProvider<SolubilisedSample> treatments = dataProvider(design.solubilisations);
    assertEquals(5, design.solubilisations.getColumns().size());
    assertEquals(SAMPLE, design.solubilisations.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE), design.solubilisations.getColumn(SAMPLE).getCaption());
    for (SolubilisedSample ts : treatments.getItems()) {
      assertEquals(ts.getSample().getName(),
          design.solubilisations.getColumn(SAMPLE).getValueProvider().apply(ts));
    }
    assertEquals(CONTAINER, design.solubilisations.getColumns().get(1).getId());
    assertEquals(resources.message(CONTAINER),
        design.solubilisations.getColumn(CONTAINER).getCaption());
    for (SolubilisedSample ts : treatments.getItems()) {
      assertEquals(ts.getContainer().getFullName(),
          design.solubilisations.getColumn(CONTAINER).getValueProvider().apply(ts));
      assertEquals(ts.getContainer().isBanned() ? BANNED : "",
          design.solubilisations.getColumn(CONTAINER).getStyleGenerator().apply(ts));
    }
    assertEquals(SOLVENT, design.solubilisations.getColumns().get(2).getId());
    assertEquals(resources.message(SOLVENT),
        design.solubilisations.getColumn(SOLVENT).getCaption());
    assertTrue(containsInstanceOf(design.solubilisations.getColumn(SOLVENT).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.solubilisations.getColumn(SOLVENT).isSortable());
    for (SolubilisedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.solubilisations.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertTrue(field.getStyleName().contains(SOLVENT));
    }
    assertEquals(SOLVENT_VOLUME, design.solubilisations.getColumns().get(3).getId());
    assertEquals(resources.message(SOLVENT_VOLUME),
        design.solubilisations.getColumn(SOLVENT_VOLUME).getCaption());
    assertTrue(containsInstanceOf(design.solubilisations.getColumn(SOLVENT_VOLUME).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.solubilisations.getColumn(SOLVENT_VOLUME).isSortable());
    for (SolubilisedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertTrue(field.getStyleName().contains(SOLVENT_VOLUME));
    }
    assertEquals(COMMENT, design.solubilisations.getColumns().get(4).getId());
    assertEquals(resources.message(COMMENT),
        design.solubilisations.getColumn(COMMENT).getCaption());
    assertTrue(containsInstanceOf(design.solubilisations.getColumn(COMMENT).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.solubilisations.getColumn(COMMENT).isSortable());
    for (SolubilisedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.solubilisations.getColumn(COMMENT).getValueProvider().apply(ts);
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
    final ListDataProvider<SolubilisedSample> treatments = dataProvider(design.solubilisations);
    SolubilisedSample firstTs = treatments.getItems().iterator().next();
    String solvent = "test solvent";
    TextField field =
        (TextField) design.solubilisations.getColumn(SOLVENT).getValueProvider().apply(firstTs);
    field.setValue(solvent);
    String solventVolume = "10";
    field = (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider()
        .apply(firstTs);
    field.setValue(solventVolume);
    String comment = "test";
    field = (TextField) design.solubilisations.getColumn(COMMENT).getValueProvider().apply(firstTs);
    field.setValue(comment);

    design.down.click();

    for (SolubilisedSample ts : treatments.getItems()) {
      field = (TextField) design.solubilisations.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertEquals(solvent, field.getValue());
      field =
          (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertEquals(solventVolume, field.getValue());
      field = (TextField) design.solubilisations.getColumn(COMMENT).getValueProvider().apply(ts);
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
    design.solubilisations.sort(SAMPLE, SortDirection.DESCENDING);
    final List<SolubilisedSample> treatments =
        new ArrayList<>(dataProvider(design.solubilisations).getItems());
    String solvent = "test solvent";
    TextField field = (TextField) design.solubilisations.getColumn(SOLVENT).getValueProvider()
        .apply(treatments.get(4));
    field.setValue(solvent);
    String solventVolume = "10";
    field = (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider()
        .apply(treatments.get(4));
    field.setValue(solventVolume);
    String comment = "test";
    field = (TextField) design.solubilisations.getColumn(COMMENT).getValueProvider()
        .apply(treatments.get(4));
    field.setValue(comment);

    design.down.click();

    for (SolubilisedSample ts : treatments) {
      field = (TextField) design.solubilisations.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertEquals(solvent, field.getValue());
      field =
          (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertEquals(solventVolume, field.getValue());
      field = (TextField) design.solubilisations.getColumn(COMMENT).getValueProvider().apply(ts);
      assertEquals(comment, field.getValue());
    }
  }

  @Test
  public void down_OrderedByContainerDesc() {
    presenter.init(view);
    presenter.enter("");
    design.solubilisations.sort(CONTAINER, SortDirection.DESCENDING);
    final List<SolubilisedSample> treatments =
        new ArrayList<>(dataProvider(design.solubilisations).getItems());
    String solvent = "test solvent";
    TextField field = (TextField) design.solubilisations.getColumn(SOLVENT).getValueProvider()
        .apply(treatments.get(5));
    field.setValue(solvent);
    String solventVolume = "10";
    field = (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider()
        .apply(treatments.get(5));
    field.setValue(solventVolume);
    String comment = "test";
    field = (TextField) design.solubilisations.getColumn(COMMENT).getValueProvider()
        .apply(treatments.get(5));
    field.setValue(comment);

    design.down.click();

    for (SolubilisedSample ts : treatments) {
      field = (TextField) design.solubilisations.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertEquals(solvent, field.getValue());
      field =
          (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertEquals(solventVolume, field.getValue());
      field = (TextField) design.solubilisations.getColumn(COMMENT).getValueProvider().apply(ts);
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
    verify(solubilisationService, never()).insert(any());
  }

  @Test
  public void save_NoSolvent() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    SolubilisedSample ts = dataProvider(design.solubilisations).getItems().iterator().next();
    TextField field =
        (TextField) design.solubilisations.getColumn(SOLVENT).getValueProvider().apply(ts);
    field.setValue("");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(solubilisationService, never()).insert(any());
  }

  @Test
  public void save_NoSolventVolume() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    SolubilisedSample ts = dataProvider(design.solubilisations).getItems().iterator().next();
    TextField field =
        (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
    field.setValue("");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(solubilisationService, never()).insert(any());
  }

  @Test
  public void save_SolventVolumeInvalid() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    SolubilisedSample ts = dataProvider(design.solubilisations).getItems().iterator().next();
    TextField field =
        (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
    field.setValue("a");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(solubilisationService, never()).insert(any());
  }

  @Test
  public void save_SolventVolumeBelowZero() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    SolubilisedSample ts = dataProvider(design.solubilisations).getItems().iterator().next();
    TextField field =
        (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
    field.setValue("-1");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertNotNull(field.getErrorMessage().getFormattedHtmlMessage());
    verify(solubilisationService, never()).insert(any());
  }

  @Test
  public void save() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    doAnswer(i -> {
      Solubilisation solubilisation = i.getArgumentAt(0, Solubilisation.class);
      assertNull(solubilisation.getId());
      solubilisation.setId(4L);
      return null;
    }).when(solubilisationService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(solubilisationService).insert(solubilisationCaptor.capture());
    Solubilisation solubilisation = solubilisationCaptor.getValue();
    assertEquals(containers.size(), solubilisation.getTreatmentSamples().size());
    int count = 0;
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      SolubilisedSample diluted = solubilisation.getTreatmentSamples().get(i);
      assertEquals(container.getSample(), diluted.getSample());
      assertEquals(container, diluted.getContainer());
      assertEquals(solvents.get(count), diluted.getSolvent());
      assertEquals(solventVolumes.get(count), diluted.getSolventVolume(), 0.0001);
      assertEquals(comments.get(count), diluted.getComment());
      count++;
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).navigateTo(SolubilisationView.VIEW_NAME, "4");
  }

  @Test
  public void save_IllegalArgumentException() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    doThrow(new IllegalArgumentException()).when(solubilisationService).insert(any());

    design.save.click();

    verify(view).showError(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
    verify(view, never()).showTrayNotification(any());
    verify(view, never()).navigateTo(any(), any());
  }

  @Test
  public void save_Update() {
    presenter = new SolubilisationViewPresenter(solubilisationService, sampleContainerService,
        applicationName);
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 1L);
    when(solubilisationService.get(any())).thenReturn(solubilisation);
    presenter.init(view);
    presenter.enter("1");
    setFields();
    design.explanation.setValue("test explanation");

    design.save.click();

    verify(view, never()).showError(any());
    verify(solubilisationService).update(solubilisationCaptor.capture(), eq("test explanation"));
    Solubilisation savedSolubilisation = solubilisationCaptor.getValue();
    assertEquals((Long) 1L, savedSolubilisation.getId());
    assertEquals(solubilisation.getTreatmentSamples().size(),
        savedSolubilisation.getTreatmentSamples().size());
    for (int i = 0; i < solubilisation.getTreatmentSamples().size(); i++) {
      SolubilisedSample original = solubilisation.getTreatmentSamples().get(i);
      SolubilisedSample diluted = savedSolubilisation.getTreatmentSamples().get(i);
      assertEquals(original.getId(), diluted.getId());
      assertEquals(original.getSample(), diluted.getSample());
      assertEquals(original.getContainer(), diluted.getContainer());
      assertEquals(solvents.get(i), diluted.getSolvent());
      assertEquals(solventVolumes.get(i), diluted.getSolventVolume(), 0.0001);
      assertEquals(comments.get(i), diluted.getComment());
    }
    verify(view).showTrayNotification(resources.message(SAVED, solubilisation.getTreatmentSamples()
        .stream().map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(SolubilisationView.VIEW_NAME, "1");
  }

  @Test
  public void remove_NoExplanation() {
    presenter = new SolubilisationViewPresenter(solubilisationService, sampleContainerService,
        applicationName);
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 4L);
    when(solubilisationService.get(any())).thenReturn(solubilisation);
    presenter.init(view);
    presenter.enter("4");

    design.remove.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.explanation.getErrorMessage().getFormattedHtmlMessage());
    verify(solubilisationService, never()).undo(any(), any(), anyBoolean());
  }

  @Test
  public void remove() {
    presenter = new SolubilisationViewPresenter(solubilisationService, sampleContainerService,
        applicationName);
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 1L);
    when(solubilisationService.get(any())).thenReturn(solubilisation);
    presenter.init(view);
    presenter.enter("1");
    setFields();
    design.explanation.setValue("test explanation");

    design.remove.click();

    verify(view, never()).showError(any());
    verify(solubilisationService).undo(solubilisationCaptor.capture(), eq("test explanation"),
        eq(false));
    Solubilisation savedSolubilisation = solubilisationCaptor.getValue();
    assertEquals((Long) 1L, savedSolubilisation.getId());
    verify(view).showTrayNotification(resources.message(REMOVED, solubilisation
        .getTreatmentSamples().stream().map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(SolubilisationView.VIEW_NAME, "1");
  }

  @Test
  public void remove_BanContainers() {
    presenter = new SolubilisationViewPresenter(solubilisationService, sampleContainerService,
        applicationName);
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 1L);
    when(solubilisationService.get(any())).thenReturn(solubilisation);
    presenter.init(view);
    presenter.enter("1");
    setFields();
    design.explanation.setValue("test explanation");
    design.banContainers.setValue(true);

    design.remove.click();

    verify(view, never()).showError(any());
    verify(solubilisationService).undo(solubilisationCaptor.capture(), eq("test explanation"),
        eq(true));
    Solubilisation savedSolubilisation = solubilisationCaptor.getValue();
    assertEquals((Long) 1L, savedSolubilisation.getId());
    verify(view).showTrayNotification(resources.message(REMOVED, solubilisation
        .getTreatmentSamples().stream().map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(SolubilisationView.VIEW_NAME, "1");
  }

  @Test
  public void enter() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(design.deleted.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.save.isVisible());
    assertFalse(design.removeLayout.isVisible());
    List<SolubilisedSample> tss = new ArrayList<>(dataProvider(design.solubilisations).getItems());
    assertEquals(containers.size(), tss.size());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      SolubilisedSample diluted = tss.get(i);
      assertEquals(container.getSample(), diluted.getSample());
      assertEquals(container, diluted.getContainer());
    }
    for (SolubilisedSample ts : tss) {
      TextField field =
          (TextField) design.solubilisations.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field =
          (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.solubilisations.getColumn(COMMENT).getValueProvider().apply(ts);
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
    List<SolubilisedSample> tss = new ArrayList<>(dataProvider(design.solubilisations).getItems());
    assertEquals(containers.size(), tss.size());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      SolubilisedSample diluted = tss.get(i);
      assertEquals(container.getSample(), diluted.getSample());
      assertEquals(container, diluted.getContainer());
    }
    for (SolubilisedSample ts : tss) {
      TextField field =
          (TextField) design.solubilisations.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field =
          (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.solubilisations.getColumn(COMMENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void enter_Solubilisation() {
    presenter = new SolubilisationViewPresenter(solubilisationService, sampleContainerService,
        applicationName);
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 1L);
    when(solubilisationService.get(any())).thenReturn(solubilisation);
    presenter.init(view);
    presenter.enter("1");

    verify(solubilisationService).get(1L);
    assertFalse(design.deleted.isVisible());
    assertTrue(design.explanationPanel.isVisible());
    assertTrue(design.save.isVisible());
    assertTrue(design.removeLayout.isVisible());
    List<SolubilisedSample> tss = new ArrayList<>(dataProvider(design.solubilisations).getItems());
    assertEquals(solubilisation.getTreatmentSamples().size(), tss.size());
    for (int i = 0; i < solubilisation.getTreatmentSamples().size(); i++) {
      assertEquals(solubilisation.getTreatmentSamples().get(i), tss.get(i));
    }
    for (SolubilisedSample ts : tss) {
      TextField field =
          (TextField) design.solubilisations.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field =
          (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.solubilisations.getColumn(COMMENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void enter_SolubilisationDeleted() {
    presenter = new SolubilisationViewPresenter(solubilisationService, sampleContainerService,
        applicationName);
    Solubilisation solubilisation = entityManager.find(Solubilisation.class, 1L);
    solubilisation.setDeleted(true);
    when(solubilisationService.get(any())).thenReturn(solubilisation);
    presenter.init(view);
    presenter.enter("1");

    verify(solubilisationService).get(1L);
    assertTrue(design.deleted.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.save.isVisible());
    assertFalse(design.removeLayout.isVisible());
    List<SolubilisedSample> tss = new ArrayList<>(dataProvider(design.solubilisations).getItems());
    assertEquals(solubilisation.getTreatmentSamples().size(), tss.size());
    for (int i = 0; i < solubilisation.getTreatmentSamples().size(); i++) {
      assertEquals(solubilisation.getTreatmentSamples().get(i), tss.get(i));
    }
    for (SolubilisedSample ts : tss) {
      TextField field =
          (TextField) design.solubilisations.getColumn(SOLVENT).getValueProvider().apply(ts);
      assertTrue(field.isReadOnly());
      field =
          (TextField) design.solubilisations.getColumn(SOLVENT_VOLUME).getValueProvider().apply(ts);
      assertTrue(field.isReadOnly());
      field = (TextField) design.solubilisations.getColumn(COMMENT).getValueProvider().apply(ts);
      assertTrue(field.isReadOnly());
    }
  }

  @Test
  public void enter_SolubilisationNotId() {
    presenter.init(view);
    presenter.enter("a");

    ListDataProvider<SolubilisedSample> tss = dataProvider(design.solubilisations);
    verify(view).showWarning(resources.message(INVALID_SOLUBILISATION));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_SolubilisationIdNotExists() {
    presenter.init(view);
    presenter.enter("6");

    verify(solubilisationService).get(6L);
    ListDataProvider<SolubilisedSample> tss = dataProvider(design.solubilisations);
    verify(view).showWarning(resources.message(INVALID_SOLUBILISATION));
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

    ListDataProvider<SolubilisedSample> tss = dataProvider(design.solubilisations);
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

    ListDataProvider<SolubilisedSample> tss = dataProvider(design.solubilisations);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_ContainersNotId() {
    presenter.init(view);
    presenter.enter("containers/11,a");

    ListDataProvider<SolubilisedSample> tss = dataProvider(design.solubilisations);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_ContainersIdNotExists() {
    when(sampleContainerService.get(any())).thenReturn(null);
    presenter.init(view);
    presenter.enter("containers/11,12");

    ListDataProvider<SolubilisedSample> tss = dataProvider(design.solubilisations);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }
}
