package ca.qc.ircm.proview.enrichment.web;

import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.COMMENT;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.CONTAINER;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.DOWN;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.ENRICHMENTS;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.ENRICHMENTS_PANEL;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.HEADER;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.INVALID_CONTAINERS;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.INVALID_ENRICHMENT;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.NO_CONTAINERS;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.PROTOCOL;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.PROTOCOL_PANEL;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.SAMPLE;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.SAVE;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.SAVED;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.TITLE;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.errorMessage;
import static ca.qc.ircm.proview.web.WebConstants.BUTTON_SKIP_ROW;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
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

import ca.qc.ircm.proview.enrichment.EnrichedSample;
import ca.qc.ircm.proview.enrichment.Enrichment;
import ca.qc.ircm.proview.enrichment.EnrichmentProtocol;
import ca.qc.ircm.proview.enrichment.EnrichmentProtocolService;
import ca.qc.ircm.proview.enrichment.EnrichmentService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
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
public class EnrichmentViewPresenterTest {
  private EnrichmentViewPresenter presenter;
  @Mock
  private EnrichmentView view;
  @Mock
  private EnrichmentService enrichmentService;
  @Mock
  private EnrichmentProtocolService enrichmentProtocolService;
  @Mock
  private SampleContainerService sampleContainerService;
  @Captor
  private ArgumentCaptor<Enrichment> enrichmentCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private EnrichmentProtocolService realEnrichmentProtocolService;
  @Value("${spring.application.name}")
  private String applicationName;
  private EnrichmentViewDesign design = new EnrichmentViewDesign();
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(EnrichmentView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<Sample> samples = new ArrayList<>();
  private List<SampleContainer> containers = new ArrayList<>();
  private List<EnrichmentProtocol> protocols;
  private List<String> comments = new ArrayList<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new EnrichmentViewPresenter(enrichmentService, enrichmentProtocolService,
        sampleContainerService, applicationName);
    design = new EnrichmentViewDesign();
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
    protocols = realEnrichmentProtocolService.all();
    comments = IntStream.range(0, containers.size()).mapToObj(i -> "comment" + i)
        .collect(Collectors.toList());
    when(enrichmentProtocolService.all()).thenReturn(new ArrayList<>(protocols));
    when(view.savedContainers()).thenReturn(new ArrayList<>(containers));
  }

  private void setFields() {
    final ListDataProvider<EnrichedSample> treatments = dataProvider(design.enrichments);
    int count = 0;
    for (EnrichedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.enrichments.getColumn(COMMENT).getValueProvider().apply(ts);
      field.setValue(comments.get(count++));
    }
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.header.getStyleName().contains(HEADER));
    assertTrue(design.header.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.protocolPanel.getStyleName().contains(PROTOCOL_PANEL));
    assertTrue(design.protocol.getStyleName().contains(PROTOCOL));
    assertTrue(design.enrichmentsPanel.getStyleName().contains(ENRICHMENTS_PANEL));
    assertTrue(design.enrichments.getStyleName().contains(ENRICHMENTS));
    assertTrue(design.enrichments.getStyleName().contains(COMPONENTS));
    assertTrue(design.down.getStyleName().contains(DOWN));
    assertTrue(design.down.getStyleName().contains(BUTTON_SKIP_ROW));
    assertTrue(design.save.getStyleName().contains(SAVE));
    assertTrue(design.save.getStyleName().contains(ValoTheme.BUTTON_PRIMARY));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
    assertEquals(resources.message(PROTOCOL_PANEL), design.protocolPanel.getCaption());
    assertEquals(resources.message(ENRICHMENTS_PANEL), design.enrichmentsPanel.getCaption());
    assertEquals(resources.message(DOWN), design.down.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.down.getIcon());
    assertEquals(resources.message(SAVE), design.save.getCaption());
  }

  @Test
  public void protocol() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(design.protocol.isReadOnly());
    assertFalse(design.protocol.isEmptySelectionAllowed());
    ListDataProvider<EnrichmentProtocol> protocols = dataProvider(design.protocol);
    assertEquals(this.protocols.size(), protocols.getItems().size());
    for (EnrichmentProtocol protocol : this.protocols) {
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
  public void enrichments() {
    presenter.init(view);
    presenter.enter("");

    final ListDataProvider<EnrichedSample> treatments = dataProvider(design.enrichments);
    assertEquals(3, design.enrichments.getColumns().size());
    assertEquals(SAMPLE, design.enrichments.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE), design.enrichments.getColumn(SAMPLE).getCaption());
    for (EnrichedSample ts : treatments.getItems()) {
      assertEquals(ts.getSample().getName(),
          design.enrichments.getColumn(SAMPLE).getValueProvider().apply(ts));
    }
    assertEquals(CONTAINER, design.enrichments.getColumns().get(1).getId());
    assertEquals(resources.message(CONTAINER),
        design.enrichments.getColumn(CONTAINER).getCaption());
    for (EnrichedSample ts : treatments.getItems()) {
      assertEquals(ts.getContainer().getFullName(),
          design.enrichments.getColumn(CONTAINER).getValueProvider().apply(ts));
    }
    assertEquals(COMMENT, design.enrichments.getColumns().get(2).getId());
    assertEquals(resources.message(COMMENT), design.enrichments.getColumn(COMMENT).getCaption());
    assertTrue(containsInstanceOf(design.enrichments.getColumn(COMMENT).getExtensions(),
        ComponentRenderer.class));
    for (EnrichedSample ts : treatments.getItems()) {
      TextField field =
          (TextField) design.enrichments.getColumn(COMMENT).getValueProvider().apply(ts);
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
    final ListDataProvider<EnrichedSample> treatments = dataProvider(design.enrichments);
    EnrichedSample firstTs = treatments.getItems().iterator().next();
    String comment = "test";
    TextField field =
        (TextField) design.enrichments.getColumn(COMMENT).getValueProvider().apply(firstTs);
    field.setValue(comment);

    design.down.click();

    for (EnrichedSample ts : treatments.getItems()) {
      field = (TextField) design.enrichments.getColumn(COMMENT).getValueProvider().apply(ts);
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
    verify(enrichmentService, never()).insert(any());
  }

  @Test
  public void save_NoProtocol() {
    when(enrichmentProtocolService.all()).thenReturn(new ArrayList<>());
    presenter.init(view);
    presenter.enter("");

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.protocol.getErrorMessage().getFormattedHtmlMessage());
    verify(enrichmentService, never()).insert(any());
  }

  @Test
  public void save() {
    presenter.init(view);
    presenter.enter("");
    setFields();
    doAnswer(i -> {
      Enrichment enrichment = i.getArgumentAt(0, Enrichment.class);
      assertNull(enrichment.getId());
      enrichment.setId(4L);
      return null;
    }).when(enrichmentService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(enrichmentService).insert(enrichmentCaptor.capture());
    Enrichment enrichment = enrichmentCaptor.getValue();
    assertEquals(protocols.get(0).getId(), enrichment.getProtocol().getId());
    assertEquals(containers.size(), enrichment.getTreatmentSamples().size());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      EnrichedSample enriched = enrichment.getTreatmentSamples().get(i);
      assertEquals(container.getSample(), enriched.getSample());
      assertEquals(container, enriched.getContainer());
      assertEquals(comments.get(i), enriched.getComment());
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).navigateTo(EnrichmentView.VIEW_NAME, "4");
  }

  @Test
  public void enter() {
    presenter.init(view);
    presenter.enter("");

    List<EnrichedSample> tss = new ArrayList<>(dataProvider(design.enrichments).getItems());
    assertEquals(containers.size(), tss.size());
    for (int i = 0; i < containers.size(); i++) {
      SampleContainer container = containers.get(i);
      EnrichedSample enriched = tss.get(i);
      assertEquals(container.getSample(), enriched.getSample());
      assertEquals(container, enriched.getContainer());
    }
  }

  @Test
  public void enter_Enrichment() {
    presenter = new EnrichmentViewPresenter(enrichmentService, realEnrichmentProtocolService,
        sampleContainerService, applicationName);
    Enrichment enrichment = entityManager.find(Enrichment.class, 7L);
    when(enrichmentService.get(any())).thenReturn(enrichment);
    presenter.init(view);
    presenter.enter("7");

    verify(enrichmentService).get(7L);
    assertTrue(design.protocol.isReadOnly());
    assertEquals(enrichment.getProtocol(), design.protocol.getValue());
    assertFalse(design.save.isVisible());
    List<EnrichedSample> tss = new ArrayList<>(dataProvider(design.enrichments).getItems());
    assertEquals(enrichment.getTreatmentSamples().size(), tss.size());
    for (int i = 0; i < enrichment.getTreatmentSamples().size(); i++) {
      assertEquals(enrichment.getTreatmentSamples().get(i), tss.get(i));
    }
  }

  @Test
  public void enter_EnrichmentNotId() {
    presenter.init(view);
    presenter.enter("a");

    ListDataProvider<EnrichedSample> tss = dataProvider(design.enrichments);
    verify(view).showWarning(resources.message(INVALID_ENRICHMENT));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_EnrichmentIdNotExists() {
    presenter.init(view);
    presenter.enter("6");

    verify(enrichmentService).get(6L);
    ListDataProvider<EnrichedSample> tss = dataProvider(design.enrichments);
    verify(view).showWarning(resources.message(INVALID_ENRICHMENT));
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

    ListDataProvider<EnrichedSample> tss = dataProvider(design.enrichments);
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

    ListDataProvider<EnrichedSample> tss = dataProvider(design.enrichments);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_ContainersNotId() {
    presenter.init(view);
    presenter.enter("containers/11,a");

    ListDataProvider<EnrichedSample> tss = dataProvider(design.enrichments);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_ContainersIdNotExists() {
    when(sampleContainerService.get(any())).thenReturn(null);
    presenter.init(view);
    presenter.enter("containers/11,12");

    ListDataProvider<EnrichedSample> tss = dataProvider(design.enrichments);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }
}
