package ca.qc.ircm.proview.standard.web;

import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.BAN_CONTAINERS;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.COMMENT;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.CONTAINER;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.DELETED;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.DOWN;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.EXPLANATION;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.HEADER;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.INVALID_CONTAINERS;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.INVALID_STANDARD_ADDITION;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.NAME;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.NO_CONTAINERS;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.QUANTITY;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.REMOVE;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.REMOVED;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.SAMPLE;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.SAVE;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.SAVED;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.STANDARD_ADDITIONS;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.STANDARD_ADDITIONS_PANEL;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.TITLE;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.errorMessage;
import static ca.qc.ircm.proview.web.WebConstants.BUTTON_SKIP_ROW;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
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

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.standard.AddedStandard;
import ca.qc.ircm.proview.standard.StandardAddition;
import ca.qc.ircm.proview.standard.StandardAdditionService;
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
public class StandardAdditionViewPresenterTest {
  private StandardAdditionViewPresenter presenter;
  @Mock
  private StandardAdditionView view;
  @Mock
  private StandardAdditionService standardAdditionService;
  @Mock
  private SampleContainerService sampleContainerService;
  @Captor
  private ArgumentCaptor<StandardAddition> standardAdditionCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;
  private StandardAdditionViewDesign design = new StandardAdditionViewDesign();
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(StandardAdditionView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<Sample> samples = new ArrayList<>();
  private List<SampleContainer> containers = new ArrayList<>();
  private List<String> name = new ArrayList<>();
  private List<String> quantities = new ArrayList<>();
  private List<String> comments = new ArrayList<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new StandardAdditionViewPresenter(standardAdditionService, sampleContainerService,
        applicationName);
    design = new StandardAdditionViewDesign();
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
    name = IntStream.range(0, containers.size()).mapToObj(i -> "standard" + i)
        .collect(Collectors.toList());
    quantities = IntStream.range(0, containers.size()).mapToObj(i -> "quantity" + i)
        .collect(Collectors.toList());
    comments = IntStream.range(0, containers.size()).mapToObj(i -> "comment" + i)
        .collect(Collectors.toList());
    when(view.savedContainers()).thenReturn(new ArrayList<>(containers));
  }

  private void setFields() {
    final ListDataProvider<AddedStandard> treatments = dataProvider(design.standardAdditions);
    int count = 0;
    for (AddedStandard ts : treatments.getItems()) {
      TextField field =
          (TextField) design.standardAdditions.getColumn(NAME).getValueProvider().apply(ts);
      field.setValue(Objects.toString(name.get(count++), ""));
    }
    count = 0;
    for (AddedStandard ts : treatments.getItems()) {
      TextField field =
          (TextField) design.standardAdditions.getColumn(QUANTITY).getValueProvider().apply(ts);
      field.setValue(quantities.get(count++));
    }
    count = 0;
    for (AddedStandard ts : treatments.getItems()) {
      TextField field =
          (TextField) design.standardAdditions.getColumn(COMMENT).getValueProvider().apply(ts);
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
    assertTrue(design.standardAdditionsPanel.getStyleName().contains(STANDARD_ADDITIONS_PANEL));
    assertTrue(design.standardAdditions.getStyleName().contains(STANDARD_ADDITIONS));
    assertTrue(design.standardAdditions.getStyleName().contains(COMPONENTS));
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
    assertEquals(resources.message(STANDARD_ADDITIONS_PANEL),
        design.standardAdditionsPanel.getCaption());
    assertEquals(resources.message(DOWN), design.down.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.down.getIcon());
    assertEquals(resources.message(EXPLANATION_PANEL), design.explanationPanel.getCaption());
    assertEquals(resources.message(SAVE), design.save.getCaption());
    assertEquals(resources.message(REMOVE), design.remove.getCaption());
    assertEquals(resources.message(BAN_CONTAINERS), design.banContainers.getCaption());
  }

  @Test
  public void standardAdditions() {
    presenter.init(view);
    presenter.enter("");

    final ListDataProvider<AddedStandard> treatments = dataProvider(design.standardAdditions);
    assertEquals(5, design.standardAdditions.getColumns().size());
    assertEquals(SAMPLE, design.standardAdditions.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE),
        design.standardAdditions.getColumn(SAMPLE).getCaption());
    for (AddedStandard ts : treatments.getItems()) {
      assertEquals(ts.getSample().getName(),
          design.standardAdditions.getColumn(SAMPLE).getValueProvider().apply(ts));
    }
    assertEquals(CONTAINER, design.standardAdditions.getColumns().get(1).getId());
    assertEquals(resources.message(CONTAINER),
        design.standardAdditions.getColumn(CONTAINER).getCaption());
    for (AddedStandard ts : treatments.getItems()) {
      assertEquals(ts.getContainer().getFullName(),
          design.standardAdditions.getColumn(CONTAINER).getValueProvider().apply(ts));
    }
    assertEquals(NAME, design.standardAdditions.getColumns().get(2).getId());
    assertEquals(resources.message(NAME), design.standardAdditions.getColumn(NAME).getCaption());
    assertTrue(containsInstanceOf(design.standardAdditions.getColumn(NAME).getExtensions(),
        ComponentRenderer.class));
    for (AddedStandard ts : treatments.getItems()) {
      TextField field =
          (TextField) design.standardAdditions.getColumn(NAME).getValueProvider().apply(ts);
      assertTrue(field.getStyleName().contains(NAME));
    }
    assertFalse(design.standardAdditions.getColumn(NAME).isSortable());
    assertEquals(QUANTITY, design.standardAdditions.getColumns().get(3).getId());
    assertEquals(resources.message(QUANTITY),
        design.standardAdditions.getColumn(QUANTITY).getCaption());
    assertTrue(containsInstanceOf(design.standardAdditions.getColumn(QUANTITY).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.standardAdditions.getColumn(QUANTITY).isSortable());
    for (AddedStandard ts : treatments.getItems()) {
      TextField field =
          (TextField) design.standardAdditions.getColumn(QUANTITY).getValueProvider().apply(ts);
      assertTrue(field.getStyleName().contains(QUANTITY));
    }
    assertEquals(COMMENT, design.standardAdditions.getColumns().get(4).getId());
    assertEquals(resources.message(COMMENT),
        design.standardAdditions.getColumn(COMMENT).getCaption());
    assertTrue(containsInstanceOf(design.standardAdditions.getColumn(COMMENT).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.standardAdditions.getColumn(COMMENT).isSortable());
    for (AddedStandard ts : treatments.getItems()) {
      TextField field =
          (TextField) design.standardAdditions.getColumn(COMMENT).getValueProvider().apply(ts);
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
    final ListDataProvider<AddedStandard> treatments = dataProvider(design.standardAdditions);
    AddedStandard firstTs = treatments.getItems().iterator().next();
    String sourceVolume = "2.0";
    TextField field =
        (TextField) design.standardAdditions.getColumn(NAME).getValueProvider().apply(firstTs);
    field.setValue(sourceVolume);
    String solvent = "test solvent";
    field =
        (TextField) design.standardAdditions.getColumn(QUANTITY).getValueProvider().apply(firstTs);
    field.setValue(solvent);
    String comment = "test";
    field =
        (TextField) design.standardAdditions.getColumn(COMMENT).getValueProvider().apply(firstTs);
    field.setValue(comment);

    design.down.click();

    for (AddedStandard ts : treatments.getItems()) {
      field = (TextField) design.standardAdditions.getColumn(NAME).getValueProvider().apply(ts);
      assertEquals(sourceVolume, field.getValue());
      field = (TextField) design.standardAdditions.getColumn(QUANTITY).getValueProvider().apply(ts);
      assertEquals(solvent, field.getValue());
      field = (TextField) design.standardAdditions.getColumn(COMMENT).getValueProvider().apply(ts);
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
    design.standardAdditions.sort(SAMPLE, SortDirection.DESCENDING);
    final List<AddedStandard> treatments =
        new ArrayList<>(dataProvider(design.standardAdditions).getItems());
    String sourceVolume = "2.0";
    TextField field = (TextField) design.standardAdditions.getColumn(NAME).getValueProvider()
        .apply(treatments.get(4));
    field.setValue(sourceVolume);
    String solvent = "test solvent";
    field = (TextField) design.standardAdditions.getColumn(QUANTITY).getValueProvider()
        .apply(treatments.get(4));
    field.setValue(solvent);
    String comment = "test";
    field = (TextField) design.standardAdditions.getColumn(COMMENT).getValueProvider()
        .apply(treatments.get(4));
    field.setValue(comment);

    design.down.click();

    for (AddedStandard ts : treatments) {
      field = (TextField) design.standardAdditions.getColumn(NAME).getValueProvider().apply(ts);
      assertEquals(sourceVolume, field.getValue());
      field = (TextField) design.standardAdditions.getColumn(QUANTITY).getValueProvider().apply(ts);
      assertEquals(solvent, field.getValue());
      field = (TextField) design.standardAdditions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertEquals(comment, field.getValue());
    }
  }

  @Test
  public void down_OrderedByContainerDesc() {
    presenter.init(view);
    presenter.enter("");
    design.standardAdditions.sort(CONTAINER, SortDirection.DESCENDING);
    final List<AddedStandard> treatments =
        new ArrayList<>(dataProvider(design.standardAdditions).getItems());
    String sourceVolume = "2.0";
    TextField field = (TextField) design.standardAdditions.getColumn(NAME).getValueProvider()
        .apply(treatments.get(5));
    field.setValue(sourceVolume);
    String solvent = "test solvent";
    field = (TextField) design.standardAdditions.getColumn(QUANTITY).getValueProvider()
        .apply(treatments.get(5));
    field.setValue(solvent);
    String comment = "test";
    field = (TextField) design.standardAdditions.getColumn(COMMENT).getValueProvider()
        .apply(treatments.get(5));
    field.setValue(comment);

    design.down.click();

    for (AddedStandard ts : treatments) {
      field = (TextField) design.standardAdditions.getColumn(NAME).getValueProvider().apply(ts);
      assertEquals(sourceVolume, field.getValue());
      field = (TextField) design.standardAdditions.getColumn(QUANTITY).getValueProvider().apply(ts);
      assertEquals(solvent, field.getValue());
      field = (TextField) design.standardAdditions.getColumn(COMMENT).getValueProvider().apply(ts);
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
    verify(standardAdditionService, never()).insert(any());
  }

  @Test
  public void save_NoName() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    AddedStandard ts = dataProvider(design.standardAdditions).getItems().iterator().next();
    TextField field =
        (TextField) design.standardAdditions.getColumn(NAME).getValueProvider().apply(ts);
    field.setValue("");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(standardAdditionService, never()).insert(any());
  }

  @Test
  public void save_NoQuantity() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    AddedStandard ts = dataProvider(design.standardAdditions).getItems().iterator().next();
    TextField field =
        (TextField) design.standardAdditions.getColumn(QUANTITY).getValueProvider().apply(ts);
    field.setValue("");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(standardAdditionService, never()).insert(any());
  }

  @Test
  public void save() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    doAnswer(i -> {
      StandardAddition standardAddition = i.getArgumentAt(0, StandardAddition.class);
      assertNull(standardAddition.getId());
      standardAddition.setId(4L);
      return null;
    }).when(standardAdditionService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(standardAdditionService).insert(standardAdditionCaptor.capture());
    StandardAddition standardAddition = standardAdditionCaptor.getValue();
    assertEquals(containers.size(), standardAddition.getTreatmentSamples().size());
    int count = 0;
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      AddedStandard diluted = standardAddition.getTreatmentSamples().get(i);
      assertEquals(container.getSample(), diluted.getSample());
      assertEquals(container, diluted.getContainer());
      assertEquals(name.get(count), diluted.getName());
      assertEquals(quantities.get(count), diluted.getQuantity());
      assertEquals(comments.get(count), diluted.getComment());
      count++;
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).navigateTo(StandardAdditionView.VIEW_NAME, "4");
  }

  @Test
  public void save_IllegalArgumentException() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    doThrow(new IllegalArgumentException()).when(standardAdditionService).insert(any());

    design.save.click();

    verify(view).showError(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
    verify(view, never()).showTrayNotification(any());
    verify(view, never()).navigateTo(any(), any());
  }

  @Test
  public void save_Update() {
    presenter = new StandardAdditionViewPresenter(standardAdditionService, sampleContainerService,
        applicationName);
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 5L);
    when(standardAdditionService.get(any())).thenReturn(standardAddition);
    presenter.init(view);
    presenter.enter("5");
    setFields();
    design.explanation.setValue("test explanation");

    design.save.click();

    verify(view, never()).showError(any());
    verify(standardAdditionService).update(standardAdditionCaptor.capture(),
        eq("test explanation"));
    StandardAddition savedStandardAddition = standardAdditionCaptor.getValue();
    assertEquals((Long) 5L, savedStandardAddition.getId());
    assertEquals(standardAddition.getTreatmentSamples().size(),
        savedStandardAddition.getTreatmentSamples().size());
    for (int i = 0; i < standardAddition.getTreatmentSamples().size(); i++) {
      AddedStandard original = standardAddition.getTreatmentSamples().get(i);
      AddedStandard diluted = savedStandardAddition.getTreatmentSamples().get(i);
      assertEquals(original.getId(), diluted.getId());
      assertEquals(original.getSample(), diluted.getSample());
      assertEquals(original.getContainer(), diluted.getContainer());
      assertEquals(name.get(i), diluted.getName());
      assertEquals(quantities.get(i), diluted.getQuantity());
      assertEquals(comments.get(i), diluted.getComment());
    }
    verify(view).showTrayNotification(resources.message(SAVED, standardAddition
        .getTreatmentSamples().stream().map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(StandardAdditionView.VIEW_NAME, "5");
  }

  @Test
  public void remove_NoExplanation() {
    presenter = new StandardAdditionViewPresenter(standardAdditionService, sampleContainerService,
        applicationName);
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 5L);
    when(standardAdditionService.get(any())).thenReturn(standardAddition);
    presenter.init(view);
    presenter.enter("5");

    design.remove.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.explanation.getErrorMessage().getFormattedHtmlMessage());
    verify(standardAdditionService, never()).undoFailed(any(), any(), anyBoolean());
  }

  @Test
  public void remove() {
    presenter = new StandardAdditionViewPresenter(standardAdditionService, sampleContainerService,
        applicationName);
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 5L);
    when(standardAdditionService.get(any())).thenReturn(standardAddition);
    presenter.init(view);
    presenter.enter("5");
    setFields();
    design.explanation.setValue("test explanation");

    design.remove.click();

    verify(view, never()).showError(any());
    verify(standardAdditionService).undoFailed(standardAdditionCaptor.capture(),
        eq("test explanation"), eq(false));
    StandardAddition savedStandardAddition = standardAdditionCaptor.getValue();
    assertEquals((Long) 5L, savedStandardAddition.getId());
    verify(view).showTrayNotification(resources.message(REMOVED, standardAddition
        .getTreatmentSamples().stream().map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(StandardAdditionView.VIEW_NAME, "5");
  }

  @Test
  public void remove_BanContainers() {
    presenter = new StandardAdditionViewPresenter(standardAdditionService, sampleContainerService,
        applicationName);
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 5L);
    when(standardAdditionService.get(any())).thenReturn(standardAddition);
    presenter.init(view);
    presenter.enter("5");
    setFields();
    design.explanation.setValue("test explanation");
    design.banContainers.setValue(true);

    design.remove.click();

    verify(view, never()).showError(any());
    verify(standardAdditionService).undoFailed(standardAdditionCaptor.capture(),
        eq("test explanation"), eq(true));
    StandardAddition savedStandardAddition = standardAdditionCaptor.getValue();
    assertEquals((Long) 5L, savedStandardAddition.getId());
    verify(view).showTrayNotification(resources.message(REMOVED, standardAddition
        .getTreatmentSamples().stream().map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(StandardAdditionView.VIEW_NAME, "5");
  }

  @Test
  public void enter() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(design.deleted.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.save.isVisible());
    assertFalse(design.removeLayout.isVisible());
    List<AddedStandard> tss = new ArrayList<>(dataProvider(design.standardAdditions).getItems());
    assertEquals(containers.size(), tss.size());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      AddedStandard diluted = tss.get(i);
      assertEquals(container.getSample(), diluted.getSample());
      assertEquals(container, diluted.getContainer());
    }
    for (AddedStandard ts : tss) {
      TextField field =
          (TextField) design.standardAdditions.getColumn(NAME).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.standardAdditions.getColumn(QUANTITY).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.standardAdditions.getColumn(COMMENT).getValueProvider().apply(ts);
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
    List<AddedStandard> tss = new ArrayList<>(dataProvider(design.standardAdditions).getItems());
    assertEquals(containers.size(), tss.size());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      AddedStandard diluted = tss.get(i);
      assertEquals(container.getSample(), diluted.getSample());
      assertEquals(container, diluted.getContainer());
    }
    for (AddedStandard ts : tss) {
      TextField field =
          (TextField) design.standardAdditions.getColumn(NAME).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.standardAdditions.getColumn(QUANTITY).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.standardAdditions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void enter_StandardAddition() {
    presenter = new StandardAdditionViewPresenter(standardAdditionService, sampleContainerService,
        applicationName);
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 5L);
    when(standardAdditionService.get(any())).thenReturn(standardAddition);
    presenter.init(view);
    presenter.enter("5");

    verify(standardAdditionService).get(5L);
    assertFalse(design.deleted.isVisible());
    assertTrue(design.explanationPanel.isVisible());
    assertTrue(design.save.isVisible());
    assertTrue(design.removeLayout.isVisible());
    List<AddedStandard> tss = new ArrayList<>(dataProvider(design.standardAdditions).getItems());
    assertEquals(standardAddition.getTreatmentSamples().size(), tss.size());
    for (int i = 0; i < standardAddition.getTreatmentSamples().size(); i++) {
      assertEquals(standardAddition.getTreatmentSamples().get(i), tss.get(i));
    }
    for (AddedStandard ts : tss) {
      TextField field =
          (TextField) design.standardAdditions.getColumn(NAME).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.standardAdditions.getColumn(QUANTITY).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
      field = (TextField) design.standardAdditions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void enter_StandardAdditionDeleted() {
    presenter = new StandardAdditionViewPresenter(standardAdditionService, sampleContainerService,
        applicationName);
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 5L);
    standardAddition.setDeleted(true);
    when(standardAdditionService.get(any())).thenReturn(standardAddition);
    presenter.init(view);
    presenter.enter("5");

    verify(standardAdditionService).get(5L);
    assertTrue(design.deleted.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.save.isVisible());
    assertFalse(design.removeLayout.isVisible());
    List<AddedStandard> tss = new ArrayList<>(dataProvider(design.standardAdditions).getItems());
    assertEquals(standardAddition.getTreatmentSamples().size(), tss.size());
    for (int i = 0; i < standardAddition.getTreatmentSamples().size(); i++) {
      assertEquals(standardAddition.getTreatmentSamples().get(i), tss.get(i));
    }
    for (AddedStandard ts : tss) {
      TextField field =
          (TextField) design.standardAdditions.getColumn(NAME).getValueProvider().apply(ts);
      assertTrue(field.isReadOnly());
      field = (TextField) design.standardAdditions.getColumn(QUANTITY).getValueProvider().apply(ts);
      assertTrue(field.isReadOnly());
      field = (TextField) design.standardAdditions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertTrue(field.isReadOnly());
    }
  }

  @Test
  public void enter_StandardAdditionNotId() {
    presenter.init(view);
    presenter.enter("a");

    ListDataProvider<AddedStandard> tss = dataProvider(design.standardAdditions);
    verify(view).showWarning(resources.message(INVALID_STANDARD_ADDITION));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_StandardAdditionIdNotExists() {
    presenter.init(view);
    presenter.enter("6");

    verify(standardAdditionService).get(6L);
    ListDataProvider<AddedStandard> tss = dataProvider(design.standardAdditions);
    verify(view).showWarning(resources.message(INVALID_STANDARD_ADDITION));
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

    ListDataProvider<AddedStandard> tss = dataProvider(design.standardAdditions);
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

    ListDataProvider<AddedStandard> tss = dataProvider(design.standardAdditions);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_ContainersNotId() {
    presenter.init(view);
    presenter.enter("containers/11,a");

    ListDataProvider<AddedStandard> tss = dataProvider(design.standardAdditions);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_ContainersIdNotExists() {
    when(sampleContainerService.get(any())).thenReturn(null);
    presenter.init(view);
    presenter.enter("containers/11,12");

    ListDataProvider<AddedStandard> tss = dataProvider(design.standardAdditions);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }
}
