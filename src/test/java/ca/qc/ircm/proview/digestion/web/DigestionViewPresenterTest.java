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

package ca.qc.ircm.proview.digestion.web;

import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.BAN_CONTAINERS;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.COMMENT;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.CONTAINER;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.DELETED;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.DIGESTIONS;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.DIGESTIONS_PANEL;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.DOWN;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.EXPLANATION;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.HEADER;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.INVALID_CONTAINERS;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.INVALID_DIGESTION;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.NO_CONTAINERS;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.PROTOCOL;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.PROTOCOL_PANEL;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.REMOVE;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.REMOVED;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.SAMPLE;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.SAVE;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.SAVED;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.TITLE;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.errorMessage;
import static ca.qc.ircm.proview.web.WebConstants.BANNED;
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

import ca.qc.ircm.proview.digestion.DigestedSample;
import ca.qc.ircm.proview.digestion.Digestion;
import ca.qc.ircm.proview.digestion.DigestionProtocol;
import ca.qc.ircm.proview.digestion.DigestionProtocolService;
import ca.qc.ircm.proview.digestion.DigestionService;
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

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class DigestionViewPresenterTest {
  private DigestionViewPresenter presenter;
  @Mock
  private DigestionView view;
  @Mock
  private DigestionService digestionService;
  @Mock
  private DigestionProtocolService digestionProtocolService;
  @Mock
  private SampleContainerService sampleContainerService;
  @Captor
  private ArgumentCaptor<Digestion> digestionCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private DigestionProtocolService realDigestionProtocolService;
  @Value("${spring.application.name}")
  private String applicationName;
  private DigestionViewDesign design = new DigestionViewDesign();
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(DigestionView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<Sample> samples = new ArrayList<>();
  private List<SampleContainer> containers = new ArrayList<>();
  private List<DigestionProtocol> protocols;
  private List<String> comments = new ArrayList<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new DigestionViewPresenter(digestionService, digestionProtocolService,
        sampleContainerService, applicationName);
    design = new DigestionViewDesign();
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
    protocols = realDigestionProtocolService.all();
    comments = IntStream.range(0, containers.size()).mapToObj(i -> "comment" + i)
        .collect(Collectors.toList());
    when(digestionProtocolService.all()).thenReturn(new ArrayList<>(protocols));
    when(view.savedContainers()).thenReturn(new ArrayList<>(containers));
  }

  private void setFields() {
    final ListDataProvider<DigestedSample> treatments = dataProvider(design.digestions);
    int count = 0;
    for (DigestedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.digestions.getColumn(COMMENT).getValueProvider().apply(ts);
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
    assertTrue(design.protocolPanel.getStyleName().contains(PROTOCOL_PANEL));
    assertTrue(design.protocol.getStyleName().contains(PROTOCOL));
    assertTrue(design.digestionsPanel.getStyleName().contains(DIGESTIONS_PANEL));
    assertTrue(design.digestions.getStyleName().contains(DIGESTIONS));
    assertTrue(design.digestions.getStyleName().contains(COMPONENTS));
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
    assertEquals(resources.message(PROTOCOL_PANEL), design.protocolPanel.getCaption());
    assertEquals(resources.message(DIGESTIONS_PANEL), design.digestionsPanel.getCaption());
    assertEquals(resources.message(EXPLANATION_PANEL), design.explanationPanel.getCaption());
    assertEquals(resources.message(DOWN), design.down.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.down.getIcon());
    assertEquals(resources.message(SAVE), design.save.getCaption());
    assertEquals(resources.message(REMOVE), design.remove.getCaption());
    assertEquals(resources.message(BAN_CONTAINERS), design.banContainers.getCaption());
  }

  @Test
  public void protocol() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(design.protocol.isReadOnly());
    assertFalse(design.protocol.isEmptySelectionAllowed());
    ListDataProvider<DigestionProtocol> protocols = dataProvider(design.protocol);
    assertEquals(this.protocols.size(), protocols.getItems().size());
    for (DigestionProtocol protocol : this.protocols) {
      assertTrue(protocols.getItems().contains(protocol));
      assertEquals(protocol.getName(), design.protocol.getItemCaptionGenerator().apply(protocol));
    }
    String newProtocolName = "test protocol";
    design.protocol.getNewItemHandler().accept(newProtocolName);
    assertEquals(newProtocolName, design.protocol.getValue().getName());
    assertEquals(this.protocols.size() + 1, protocols.getItems().size());
    assertTrue(protocols.getItems().stream()
        .filter(protocol -> protocol.getName().equals(newProtocolName)).findAny().isPresent());
  }

  @Test
  public void digestions() {
    containers.get(1).setBanned(true);
    presenter.init(view);
    presenter.enter("");

    final ListDataProvider<DigestedSample> treatments = dataProvider(design.digestions);
    assertEquals(3, design.digestions.getColumns().size());
    assertEquals(SAMPLE, design.digestions.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE), design.digestions.getColumn(SAMPLE).getCaption());
    for (DigestedSample ts : treatments.getItems()) {
      assertEquals(ts.getSample().getName(),
          design.digestions.getColumn(SAMPLE).getValueProvider().apply(ts));
    }
    assertEquals(CONTAINER, design.digestions.getColumns().get(1).getId());
    assertEquals(resources.message(CONTAINER), design.digestions.getColumn(CONTAINER).getCaption());
    for (DigestedSample ts : treatments.getItems()) {
      assertEquals(ts.getContainer().getFullName(),
          design.digestions.getColumn(CONTAINER).getValueProvider().apply(ts));
      assertEquals(ts.getContainer().isBanned() ? BANNED : "",
          design.digestions.getColumn(CONTAINER).getStyleGenerator().apply(ts));
    }
    assertEquals(COMMENT, design.digestions.getColumns().get(2).getId());
    assertEquals(resources.message(COMMENT), design.digestions.getColumn(COMMENT).getCaption());
    assertTrue(containsInstanceOf(design.digestions.getColumn(COMMENT).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.digestions.getColumn(COMMENT).isSortable());
    for (DigestedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.digestions.getColumn(COMMENT).getValueProvider().apply(ts);
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
    final ListDataProvider<DigestedSample> treatments = dataProvider(design.digestions);
    DigestedSample firstTs = treatments.getItems().iterator().next();
    String comment = "test";
    TextField field =
        (TextField) design.digestions.getColumn(COMMENT).getValueProvider().apply(firstTs);
    field.setValue(comment);

    design.down.click();

    for (DigestedSample ts : treatments.getItems()) {
      field = (TextField) design.digestions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertEquals(comment, field.getValue());
    }
  }

  @Test
  public void down_OrderedBySampleDesc() {
    presenter.init(view);
    presenter.enter("");
    design.digestions.sort(SAMPLE, SortDirection.DESCENDING);
    final List<DigestedSample> treatments =
        new ArrayList<>(dataProvider(design.digestions).getItems());
    String comment = "test";
    TextField field = (TextField) design.digestions.getColumn(COMMENT).getValueProvider()
        .apply(treatments.get(4));
    field.setValue(comment);

    design.down.click();

    for (DigestedSample ts : treatments) {
      field = (TextField) design.digestions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertEquals(comment, field.getValue());
    }
  }

  @Test
  public void down_OrderedByContainerDesc() {
    presenter.init(view);
    presenter.enter("");
    design.digestions.sort(CONTAINER, SortDirection.DESCENDING);
    final List<DigestedSample> treatments =
        new ArrayList<>(dataProvider(design.digestions).getItems());
    String comment = "test";
    TextField field = (TextField) design.digestions.getColumn(COMMENT).getValueProvider()
        .apply(treatments.get(5));
    field.setValue(comment);

    design.down.click();

    for (DigestedSample ts : treatments) {
      field = (TextField) design.digestions.getColumn(COMMENT).getValueProvider().apply(ts);
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
    verify(digestionService, never()).insert(any());
  }

  @Test
  public void save_NoProtocol() {
    when(digestionProtocolService.all()).thenReturn(new ArrayList<>());
    presenter.init(view);
    presenter.enter("");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.protocol.getErrorMessage().getFormattedHtmlMessage());
    verify(digestionService, never()).insert(any());
  }

  @Test
  public void save() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    doAnswer(i -> {
      Digestion digestion = i.getArgumentAt(0, Digestion.class);
      assertNull(digestion.getId());
      digestion.setId(4L);
      return null;
    }).when(digestionService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(digestionService).insert(digestionCaptor.capture());
    Digestion digestion = digestionCaptor.getValue();
    assertEquals(protocols.get(0).getId(), digestion.getProtocol().getId());
    assertEquals(containers.size(), digestion.getTreatmentSamples().size());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      DigestedSample digested = digestion.getTreatmentSamples().get(i);
      assertEquals(container.getSample(), digested.getSample());
      assertEquals(container, digested.getContainer());
      assertEquals(comments.get(i), digested.getComment());
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).navigateTo(DigestionView.VIEW_NAME, "4");
  }

  @Test
  public void save_IllegalArgumentException() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    doThrow(new IllegalArgumentException()).when(digestionService).insert(any());

    design.save.click();

    verify(view).showError(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
    verify(view, never()).showTrayNotification(any());
    verify(view, never()).navigateTo(any(), any());
  }

  @Test
  public void save_Update() {
    presenter = new DigestionViewPresenter(digestionService, realDigestionProtocolService,
        sampleContainerService, applicationName);
    Digestion digestion = entityManager.find(Digestion.class, 6L);
    when(digestionService.get(any())).thenReturn(digestion);
    presenter.init(view);
    presenter.enter("6");
    design.protocol.setValue(entityManager.find(DigestionProtocol.class, 3L));
    setFields();
    design.explanation.setValue("test explanation");

    design.save.click();

    verify(view, never()).showError(any());
    verify(digestionService).update(digestionCaptor.capture(), eq("test explanation"));
    Digestion savedDigestion = digestionCaptor.getValue();
    assertEquals((Long) 6L, savedDigestion.getId());
    assertEquals((Long) 3L, savedDigestion.getProtocol().getId());
    assertEquals(digestion.getTreatmentSamples().size(),
        savedDigestion.getTreatmentSamples().size());
    for (int i = 0; i < digestion.getTreatmentSamples().size(); i++) {
      DigestedSample original = digestion.getTreatmentSamples().get(i);
      DigestedSample digested = savedDigestion.getTreatmentSamples().get(i);
      assertEquals(original.getId(), digested.getId());
      assertEquals(original.getSample(), digested.getSample());
      assertEquals(original.getContainer(), digested.getContainer());
      assertEquals(comments.get(i), digested.getComment());
    }
    verify(view).showTrayNotification(resources.message(SAVED, digestion.getTreatmentSamples()
        .stream().map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(DigestionView.VIEW_NAME, "6");
  }

  @Test
  public void remove_NoExplanation() {
    presenter = new DigestionViewPresenter(digestionService, realDigestionProtocolService,
        sampleContainerService, applicationName);
    Digestion digestion = entityManager.find(Digestion.class, 6L);
    when(digestionService.get(any())).thenReturn(digestion);
    presenter.init(view);
    presenter.enter("6");

    design.remove.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.explanation.getErrorMessage().getFormattedHtmlMessage());
    verify(digestionService, never()).undo(any(), any(), anyBoolean());
  }

  @Test
  public void remove() {
    presenter = new DigestionViewPresenter(digestionService, realDigestionProtocolService,
        sampleContainerService, applicationName);
    Digestion digestion = entityManager.find(Digestion.class, 6L);
    when(digestionService.get(any())).thenReturn(digestion);
    presenter.init(view);
    presenter.enter("6");
    design.protocol.setValue(entityManager.find(DigestionProtocol.class, 3L));
    setFields();
    design.explanation.setValue("test explanation");

    design.remove.click();

    verify(view, never()).showError(any());
    verify(digestionService).undo(digestionCaptor.capture(), eq("test explanation"),
        eq(false));
    Digestion savedDigestion = digestionCaptor.getValue();
    assertEquals((Long) 6L, savedDigestion.getId());
    verify(view).showTrayNotification(resources.message(REMOVED, digestion.getTreatmentSamples()
        .stream().map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(DigestionView.VIEW_NAME, "6");
  }

  @Test
  public void remove_BanContainers() {
    presenter = new DigestionViewPresenter(digestionService, realDigestionProtocolService,
        sampleContainerService, applicationName);
    Digestion digestion = entityManager.find(Digestion.class, 6L);
    when(digestionService.get(any())).thenReturn(digestion);
    presenter.init(view);
    presenter.enter("6");
    design.protocol.setValue(entityManager.find(DigestionProtocol.class, 3L));
    setFields();
    design.explanation.setValue("test explanation");
    design.banContainers.setValue(true);

    design.remove.click();

    verify(view, never()).showError(any());
    verify(digestionService).undo(digestionCaptor.capture(), eq("test explanation"),
        eq(true));
    Digestion savedDigestion = digestionCaptor.getValue();
    assertEquals((Long) 6L, savedDigestion.getId());
    verify(view).showTrayNotification(resources.message(REMOVED, digestion.getTreatmentSamples()
        .stream().map(ts -> ts.getSample().getId()).distinct().count()));
    verify(view).navigateTo(DigestionView.VIEW_NAME, "6");
  }

  @Test
  public void enter() {
    presenter.init(view);
    presenter.enter("");

    List<DigestedSample> tss = new ArrayList<>(dataProvider(design.digestions).getItems());
    assertFalse(design.deleted.isVisible());
    assertFalse(design.protocol.isReadOnly());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.save.isVisible());
    assertFalse(design.removeLayout.isVisible());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      DigestedSample digested = tss.get(i);
      assertEquals(container.getSample(), digested.getSample());
      assertEquals(container, digested.getContainer());
    }
    for (DigestedSample ts : tss) {
      TextField field =
          (TextField) design.digestions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void enter_SavedContainersFromMultipleUsers() {
    when(view.savedContainersFromMultipleUsers()).thenReturn(true);
    presenter.init(view);
    presenter.enter("");

    verify(view).showWarning(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
    List<DigestedSample> tss = new ArrayList<>(dataProvider(design.digestions).getItems());
    assertFalse(design.deleted.isVisible());
    assertFalse(design.protocol.isReadOnly());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.save.isVisible());
    assertFalse(design.removeLayout.isVisible());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      DigestedSample digested = tss.get(i);
      assertEquals(container.getSample(), digested.getSample());
      assertEquals(container, digested.getContainer());
    }
    for (DigestedSample ts : tss) {
      TextField field =
          (TextField) design.digestions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void enter_Digestion() {
    presenter = new DigestionViewPresenter(digestionService, realDigestionProtocolService,
        sampleContainerService, applicationName);
    Digestion digestion = entityManager.find(Digestion.class, 6L);
    when(digestionService.get(any())).thenReturn(digestion);
    presenter.init(view);
    presenter.enter("6");

    verify(digestionService).get(6L);
    assertFalse(design.deleted.isVisible());
    assertFalse(design.protocol.isReadOnly());
    assertTrue(design.explanationPanel.isVisible());
    assertTrue(design.save.isVisible());
    assertTrue(design.removeLayout.isVisible());
    assertEquals(digestion.getProtocol(), design.protocol.getValue());
    List<DigestedSample> tss = new ArrayList<>(dataProvider(design.digestions).getItems());
    assertEquals(digestion.getTreatmentSamples().size(), tss.size());
    for (int i = 0; i < digestion.getTreatmentSamples().size(); i++) {
      assertEquals(digestion.getTreatmentSamples().get(i), tss.get(i));
    }
    for (DigestedSample ts : tss) {
      TextField field =
          (TextField) design.digestions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void enter_DigestionDeleted() {
    presenter = new DigestionViewPresenter(digestionService, realDigestionProtocolService,
        sampleContainerService, applicationName);
    Digestion digestion = entityManager.find(Digestion.class, 6L);
    digestion.setDeleted(true);
    when(digestionService.get(any())).thenReturn(digestion);
    presenter.init(view);
    presenter.enter("6");

    verify(digestionService).get(6L);
    assertTrue(design.deleted.isVisible());
    assertTrue(design.protocol.isReadOnly());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.save.isVisible());
    assertFalse(design.removeLayout.isVisible());
    List<DigestedSample> tss = new ArrayList<>(dataProvider(design.digestions).getItems());
    assertEquals(digestion.getTreatmentSamples().size(), tss.size());
    for (int i = 0; i < digestion.getTreatmentSamples().size(); i++) {
      assertEquals(digestion.getTreatmentSamples().get(i), tss.get(i));
    }
    for (DigestedSample ts : tss) {
      TextField field =
          (TextField) design.digestions.getColumn(COMMENT).getValueProvider().apply(ts);
      assertTrue(field.isReadOnly());
    }
  }

  @Test
  public void enter_DigestionNotId() {
    presenter.init(view);
    presenter.enter("a");

    ListDataProvider<DigestedSample> tss = dataProvider(design.digestions);
    verify(view).showWarning(resources.message(INVALID_DIGESTION));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_DigestionIdNotExists() {
    presenter.init(view);
    presenter.enter("6");

    verify(digestionService).get(6L);
    ListDataProvider<DigestedSample> tss = dataProvider(design.digestions);
    verify(view).showWarning(resources.message(INVALID_DIGESTION));
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

    ListDataProvider<DigestedSample> tss = dataProvider(design.digestions);
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

    ListDataProvider<DigestedSample> tss = dataProvider(design.digestions);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_ContainersNotId() {
    presenter.init(view);
    presenter.enter("containers/11,a");

    ListDataProvider<DigestedSample> tss = dataProvider(design.digestions);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_ContainersIdNotExists() {
    when(sampleContainerService.get(any())).thenReturn(null);
    presenter.init(view);
    presenter.enter("containers/11,12");

    ListDataProvider<DigestedSample> tss = dataProvider(design.digestions);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }
}
