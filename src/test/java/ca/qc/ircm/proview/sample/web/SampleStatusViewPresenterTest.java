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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.CANCEL;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.DOWN;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.EXPERIENCE;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.HEADER;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.INVALID_SAMPLES;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.NAME;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.NEW_STATUS;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.OK;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.REGRESS;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.REGRESS_MESSAGE;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.SAMPLES;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.SAVE;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.STATUS;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.TITLE;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.errorMessage;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleService;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.components.grid.NoSelectionModel;
import com.vaadin.ui.renderers.ComponentRenderer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SampleStatusViewPresenterTest {
  private SampleStatusViewPresenter presenter;
  @Mock
  private SampleStatusView view;
  @Mock
  private SampleService sampleService;
  @Mock
  private SubmissionSampleService submissionSampleService;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  @Captor
  private ArgumentCaptor<Long> longCaptor;
  @Captor
  private ArgumentCaptor<Collection<SubmissionSample>> samplesCaptor;
  @Captor
  private ArgumentCaptor<ConfirmDialog.Listener> confirmDialogListenerCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;
  private SampleStatusViewDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SampleStatusView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<SubmissionSample> samples = new ArrayList<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter =
        new SampleStatusViewPresenter(sampleService, submissionSampleService, applicationName);
    design = new SampleStatusViewDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    samples.add(entityManager.find(SubmissionSample.class, 442L));
    samples.add(entityManager.find(SubmissionSample.class, 443L));
    samples.forEach(s -> entityManager.detach(s));
    when(view.savedSamples()).thenReturn(new ArrayList<>(samples));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void styles() {
    presenter.init(view);
    presenter.enter("");

    assertTrue(design.headerLabel.getStyleName().contains(HEADER));
    assertTrue(design.samplesGrid.getStyleName().contains(SAMPLES));
    assertTrue(design.samplesGrid.getStyleName().contains(COMPONENTS));
    assertTrue(design.saveButton.getStyleName().contains(SAVE));
    SubmissionSample sample = samples.get(0);
    ComboBox<SampleStatus> newStatus = (ComboBox<SampleStatus>) design.samplesGrid
        .getColumn(NEW_STATUS).getValueProvider().apply(sample);
    assertTrue(newStatus.getStyleName().contains(NEW_STATUS));
    Button down = (Button) design.samplesGrid.getColumn(DOWN).getValueProvider().apply(sample);
    assertTrue(down.getStyleName().contains(DOWN));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void captions() {
    presenter.init(view);
    presenter.enter("");

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.headerLabel.getValue());
    assertEquals(resources.message(NAME), design.samplesGrid.getColumn(NAME).getCaption());
    assertEquals(resources.message(EXPERIENCE),
        design.samplesGrid.getColumn(EXPERIENCE).getCaption());
    assertEquals(resources.message(STATUS), design.samplesGrid.getColumn(STATUS).getCaption());
    assertEquals(resources.message(NEW_STATUS),
        design.samplesGrid.getColumn(NEW_STATUS).getCaption());
    assertEquals(resources.message(DOWN), design.samplesGrid.getColumn(DOWN).getCaption());
    assertEquals(resources.message(SAVE), design.saveButton.getCaption());
    SubmissionSample sample = samples.get(0);
    Object statusValue = design.samplesGrid.getColumn(STATUS).getValueProvider().apply(sample);
    assertEquals(sample.getStatus().getLabel(locale), statusValue);
    ComboBox<SampleStatus> newStatus = (ComboBox<SampleStatus>) design.samplesGrid
        .getColumn(NEW_STATUS).getValueProvider().apply(sample);
    for (SampleStatus status : SampleStatus.values()) {
      assertTrue(dataProvider(newStatus).getItems().contains(status));
      assertEquals(status.getLabel(locale), newStatus.getItemCaptionGenerator().apply(status));
    }
    Button down = (Button) design.samplesGrid.getColumn(DOWN).getValueProvider().apply(sample);
    assertEquals(resources.message(DOWN), down.getCaption());
  }

  @Test
  public void samplesGrid_Column() {
    presenter.init(view);

    assertTrue(design.samplesGrid.getSelectionModel() instanceof NoSelectionModel);
    assertEquals(NAME, design.samplesGrid.getColumns().get(0).getId());
    assertEquals(EXPERIENCE, design.samplesGrid.getColumns().get(1).getId());
    assertEquals(STATUS, design.samplesGrid.getColumns().get(2).getId());
    assertEquals(NEW_STATUS, design.samplesGrid.getColumns().get(3).getId());
    assertTrue(containsInstanceOf(design.samplesGrid.getColumns().get(3).getExtensions(),
        ComponentRenderer.class));
    assertEquals(DOWN, design.samplesGrid.getColumns().get(4).getId());
    assertTrue(containsInstanceOf(design.samplesGrid.getColumns().get(4).getExtensions(),
        ComponentRenderer.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void down() {
    presenter.init(view);
    presenter.enter("");
    SubmissionSample sample1 = samples.get(0);
    SubmissionSample sample2 = samples.get(1);
    final ComboBox<SampleStatus> newStatus1 = (ComboBox<SampleStatus>) design.samplesGrid
        .getColumn(NEW_STATUS).getValueProvider().apply(sample1);
    final ComboBox<SampleStatus> newStatus2 = (ComboBox<SampleStatus>) design.samplesGrid
        .getColumn(NEW_STATUS).getValueProvider().apply(sample2);
    Button down1 = (Button) design.samplesGrid.getColumn(DOWN).getValueProvider().apply(sample1);
    newStatus1.setValue(SampleStatus.ANALYSED);

    down1.click();

    assertEquals(SampleStatus.ANALYSED, newStatus1.getValue());
    assertEquals(SampleStatus.ANALYSED, newStatus2.getValue());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void updateStatus_Null() {
    presenter.init(view);
    presenter.enter("");
    SubmissionSample sample = samples.get(0);
    ComboBox<SampleStatus> newStatus = (ComboBox<SampleStatus>) design.samplesGrid
        .getColumn(NEW_STATUS).getValueProvider().apply(sample);
    samples.stream().skip(1).forEach(otherSample -> {
      design.samplesGrid.getColumn(NEW_STATUS).getValueProvider().apply(otherSample);
    });
    newStatus.setValue(null);
    when(submissionSampleService.get(any()))
        .thenAnswer(i -> entityManager.find(SubmissionSample.class, i.getArguments()[0]));

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        newStatus.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionSampleService, never()).updateStatus(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void updateStatus() {
    presenter.init(view);
    presenter.enter("");
    SubmissionSample sample1 = samples.get(0);
    SubmissionSample sample2 = samples.get(1);
    ComboBox<SampleStatus> newStatus1 = (ComboBox<SampleStatus>) design.samplesGrid
        .getColumn(NEW_STATUS).getValueProvider().apply(sample1);
    ComboBox<SampleStatus> newStatus2 = (ComboBox<SampleStatus>) design.samplesGrid
        .getColumn(NEW_STATUS).getValueProvider().apply(sample2);
    newStatus1.setValue(SampleStatus.ANALYSED);
    newStatus2.setValue(SampleStatus.TO_DIGEST);
    when(submissionSampleService.get(any()))
        .thenAnswer(i -> entityManager.find(SubmissionSample.class, i.getArguments()[0]));

    design.saveButton.click();

    verify(submissionSampleService).updateStatus(samplesCaptor.capture());
    Collection<SubmissionSample> samples = samplesCaptor.getValue();
    assertEquals(2, samples.size());
    assertTrue(find(samples, sample1.getId()).isPresent());
    assertTrue(find(samples, sample2.getId()).isPresent());
    sample1 = find(samples, sample1.getId()).orElse(null);
    sample2 = find(samples, sample2.getId()).orElse(null);
    assertEquals(SampleStatus.ANALYSED, sample1.getStatus());
    assertEquals(SampleStatus.TO_DIGEST, sample2.getStatus());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", 2));
    samples = dataProvider(design.samplesGrid).getItems();
    sample1 = find(samples, sample1.getId()).orElse(null);
    sample2 = find(samples, sample2.getId()).orElse(null);
    assertEquals(sample1.getStatus().getLabel(locale),
        design.samplesGrid.getColumn(STATUS).getValueProvider().apply(sample1));
    assertEquals(sample2.getStatus().getLabel(locale),
        design.samplesGrid.getColumn(STATUS).getValueProvider().apply(sample2));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void updateStatus_Regress_Confirm() {
    presenter.init(view);
    presenter.enter("");
    SubmissionSample sample1 = samples.get(0);
    SubmissionSample sample2 = samples.get(1);
    ComboBox<SampleStatus> newStatus1 = (ComboBox<SampleStatus>) design.samplesGrid
        .getColumn(NEW_STATUS).getValueProvider().apply(sample1);
    design.samplesGrid.getColumn(NEW_STATUS).getValueProvider().apply(sample2);
    newStatus1.setValue(SampleStatus.TO_APPROVE);
    when(submissionSampleService.get(any()))
        .thenAnswer(i -> entityManager.find(SubmissionSample.class, i.getArguments()[0]));
    final ConfirmDialog confirmDialog = new TestConfirmDialog(true);

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view).showConfirmDialog(eq(resources.message(REGRESS)),
        eq(resources.message(REGRESS_MESSAGE)), eq(resources.message(OK)),
        eq(resources.message(CANCEL)), confirmDialogListenerCaptor.capture());
    ConfirmDialog.Listener listener = confirmDialogListenerCaptor.getValue();
    listener.onClose(confirmDialog);
    verify(view, never()).showError(any());
    verify(submissionSampleService).updateStatus(samplesCaptor.capture());
    Collection<SubmissionSample> samples = samplesCaptor.getValue();
    assertEquals(2, samples.size());
    assertTrue(find(samples, sample1.getId()).isPresent());
    assertTrue(find(samples, sample2.getId()).isPresent());
    sample1 = find(samples, sample1.getId()).orElse(null);
    sample2 = find(samples, sample2.getId()).orElse(null);
    assertEquals(SampleStatus.TO_APPROVE, sample1.getStatus());
    assertEquals(SampleStatus.TO_APPROVE, sample2.getStatus());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", 2));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void updateStatus_Regress_Cancel() {
    presenter.init(view);
    presenter.enter("");
    SubmissionSample sample1 = samples.get(0);
    SubmissionSample sample2 = samples.get(1);
    ComboBox<SampleStatus> newStatus1 = (ComboBox<SampleStatus>) design.samplesGrid
        .getColumn(NEW_STATUS).getValueProvider().apply(sample1);
    design.samplesGrid.getColumn(NEW_STATUS).getValueProvider().apply(sample2);
    newStatus1.setValue(SampleStatus.TO_APPROVE);
    when(submissionSampleService.get(any()))
        .thenAnswer(i -> entityManager.find(SubmissionSample.class, i.getArguments()[0]));
    final ConfirmDialog confirmDialog = new TestConfirmDialog(false);

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view).showConfirmDialog(eq(resources.message(REGRESS)),
        eq(resources.message(REGRESS_MESSAGE)), eq(resources.message(OK)),
        eq(resources.message(CANCEL)), confirmDialogListenerCaptor.capture());
    ConfirmDialog.Listener listener = confirmDialogListenerCaptor.getValue();
    listener.onClose(confirmDialog);
    verify(view, never()).showError(any());
    verify(submissionSampleService, never()).updateStatus(any());
  }

  @Test
  public void enter_Empty() {
    presenter.init(view);
    presenter.enter("");
    Collection<SubmissionSample> expectedSamples =
        samples.stream().map(s -> s).collect(Collectors.toSet());

    Collection<?> itemIds = dataProvider(design.samplesGrid).getItems();

    Set<SubmissionSample> samples = new HashSet<>();
    for (Object itemId : itemIds) {
      assertTrue(itemId instanceof SubmissionSample);
      SubmissionSample sample = (SubmissionSample) itemId;
      samples.add(sample);
    }
    assertTrue(expectedSamples.containsAll(samples));
    assertTrue(samples.containsAll(expectedSamples));
  }

  @Test
  public void enter_InvalidId() {
    when(sampleService.get(any()))
        .thenAnswer(i -> entityManager.find(Sample.class, i.getArguments()[0]));
    presenter.init(view);

    presenter.enter("a");

    verify(view).showWarning(stringCaptor.capture());
    assertEquals(resources.message(INVALID_SAMPLES), stringCaptor.getValue());
  }

  @Test
  public void enter_NotExists() {
    when(sampleService.get(any()))
        .thenAnswer(i -> entityManager.find(Sample.class, i.getArguments()[0]));
    presenter.init(view);

    presenter.enter("2");

    verify(view).showWarning(stringCaptor.capture());
    assertEquals(resources.message(INVALID_SAMPLES), stringCaptor.getValue());
  }

  @Test
  public void enter_EmptyId() {
    when(sampleService.get(any()))
        .thenAnswer(i -> entityManager.find(Sample.class, i.getArguments()[0]));
    presenter.init(view);

    presenter.enter("32,");

    verify(view).showWarning(stringCaptor.capture());
    assertEquals(resources.message(INVALID_SAMPLES), stringCaptor.getValue());
  }

  @Test
  public void enter_Sample() {
    Sample sample = entityManager.find(Sample.class, 445L);
    List<Sample> samples = new ArrayList<>();
    samples.add(sample);
    when(sampleService.get(any()))
        .thenAnswer(i -> entityManager.find(Sample.class, i.getArguments()[0]));
    presenter.init(view);

    presenter.enter("445");

    verify(sampleService, atLeastOnce()).get(sample.getId());
    Collection<?> itemIds = dataProvider(design.samplesGrid).getItems();
    Set<SubmissionSample> gridSamples = new HashSet<>();
    for (Object itemId : itemIds) {
      assertTrue(itemId instanceof SubmissionSample);
      gridSamples.add((SubmissionSample) itemId);
    }
    assertTrue(samples.containsAll(gridSamples));
    assertTrue(gridSamples.containsAll(samples));
  }

  @Test
  public void enter_MultipleSamples() {
    Sample sample1 = entityManager.find(Sample.class, 445L);
    Sample sample2 = entityManager.find(Sample.class, 446L);
    List<Sample> samples = new ArrayList<>();
    samples.add(sample1);
    samples.add(sample2);
    when(sampleService.get(any()))
        .thenAnswer(i -> entityManager.find(Sample.class, i.getArguments()[0]));
    presenter.init(view);

    presenter.enter("445,446");

    verify(sampleService, atLeastOnce()).get(sample1.getId());
    verify(sampleService, atLeastOnce()).get(sample2.getId());
    Collection<?> itemIds = dataProvider(design.samplesGrid).getItems();
    Set<SubmissionSample> gridSamples = new HashSet<>();
    for (Object itemId : itemIds) {
      assertTrue(itemId instanceof SubmissionSample);
      gridSamples.add((SubmissionSample) itemId);
    }
    assertTrue(samples.containsAll(gridSamples));
    assertTrue(gridSamples.containsAll(samples));
  }

  @Test
  public void enter_SampleWithControl() {
    Sample sample1 = entityManager.find(Sample.class, 445L);
    List<Sample> samples = new ArrayList<>();
    samples.add(sample1);
    when(sampleService.get(any()))
        .thenAnswer(i -> entityManager.find(Sample.class, i.getArguments()[0]));
    presenter.init(view);

    presenter.enter("445,444");

    verify(sampleService, atLeastOnce()).get(sample1.getId());
    verify(sampleService, atLeastOnce()).get(444L);
    Collection<?> itemIds = dataProvider(design.samplesGrid).getItems();
    Set<SubmissionSample> gridSamples = new HashSet<>();
    for (Object itemId : itemIds) {
      assertTrue(itemId instanceof SubmissionSample);
      gridSamples.add((SubmissionSample) itemId);
    }
    assertTrue(samples.containsAll(gridSamples));
    assertTrue(gridSamples.containsAll(samples));
  }

  @SuppressWarnings("serial")
  private static class TestConfirmDialog extends ConfirmDialog {
    TestConfirmDialog(boolean confirmed) {
      setConfirmed(confirmed);
    }
  }
}
