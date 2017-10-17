package ca.qc.ircm.proview.digestion.web;

import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.CONTAINER;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.DIGESTIONS;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.DIGESTIONS_PANEL;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.HEADER;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.INVALID_CONTAINERS;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.INVALID_DIGESTION;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.NO_CONTAINERS;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.PROTOCOL;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.PROTOCOL_PANEL;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.SAMPLE;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.SAVE;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.SAVED;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.TITLE;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.errorMessage;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
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
    when(digestionProtocolService.all()).thenReturn(new ArrayList<>(protocols));
    when(view.savedContainers()).thenReturn(new ArrayList<>(containers));
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.header.getStyleName().contains(HEADER));
    assertTrue(design.header.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.protocolPanel.getStyleName().contains(PROTOCOL_PANEL));
    assertTrue(design.protocol.getStyleName().contains(PROTOCOL));
    assertTrue(design.digestionsPanel.getStyleName().contains(DIGESTIONS_PANEL));
    assertTrue(design.digestions.getStyleName().contains(DIGESTIONS));
    assertTrue(design.save.getStyleName().contains(SAVE));
    assertTrue(design.save.getStyleName().contains(ValoTheme.BUTTON_PRIMARY));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
    assertEquals(resources.message(PROTOCOL_PANEL), design.protocolPanel.getCaption());
    assertEquals(resources.message(DIGESTIONS_PANEL), design.digestionsPanel.getCaption());
    assertEquals(resources.message(SAVE), design.save.getCaption());
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
    presenter.init(view);
    presenter.enter("");

    final ListDataProvider<DigestedSample> treatments = dataProvider(design.digestions);
    assertEquals(2, design.digestions.getColumns().size());
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
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).navigateTo(DigestionView.VIEW_NAME, "4");
  }

  @Test
  public void enter() {
    presenter.init(view);
    presenter.enter("");

    List<DigestedSample> tss = new ArrayList<>(dataProvider(design.digestions).getItems());
    assertEquals(containers.size(), tss.size());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      DigestedSample digested = tss.get(i);
      assertEquals(container.getSample(), digested.getSample());
      assertEquals(container, digested.getContainer());
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
    assertTrue(design.protocol.isReadOnly());
    assertEquals(digestion.getProtocol(), design.protocol.getValue());
    assertFalse(design.save.isVisible());
    List<DigestedSample> tss = new ArrayList<>(dataProvider(design.digestions).getItems());
    assertEquals(digestion.getTreatmentSamples().size(), tss.size());
    for (int i = 0; i < digestion.getTreatmentSamples().size(); i++) {
      assertEquals(digestion.getTreatmentSamples().get(i), tss.get(i));
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
