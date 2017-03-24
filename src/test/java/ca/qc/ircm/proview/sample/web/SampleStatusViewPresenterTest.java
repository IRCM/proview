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

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleService;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.Column;
import com.vaadin.v7.ui.Grid.SelectionModel;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
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
import java.util.Optional;
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
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SampleStatusView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<Sample> samples = new ArrayList<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter =
        new SampleStatusViewPresenter(sampleService, submissionSampleService, applicationName);
    view.headerLabel = new Label();
    view.samplesGrid = new Grid();
    view.saveButton = new Button();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    when(view.savedSamples()).thenReturn(samples);
    samples.add(entityManager.find(Sample.class, 442L));
    samples.add(entityManager.find(Sample.class, 443L));
    samples.forEach(s -> entityManager.detach(s));
  }

  private <T extends Data> Optional<T> find(Collection<T> data, long id) {
    return data.stream().filter(d -> d.getId() == id).findAny();
  }

  private String errorMessage(String message) {
    return new UserError(message).getFormattedHtmlMessage();
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.enter("");

    assertTrue(view.headerLabel.getStyleName().contains(HEADER));
    assertTrue(view.samplesGrid.getStyleName().contains(SAMPLES));
    assertTrue(view.samplesGrid.getStyleName().contains(COMPONENTS));
    assertTrue(view.saveButton.getStyleName().contains(SAVE));
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    SubmissionSample sample = (SubmissionSample) samples.get(0);
    ComboBox<SampleStatus> newStatus =
        (ComboBox<SampleStatus>) container.getItem(sample).getItemProperty(NEW_STATUS).getValue();
    assertTrue(newStatus.getStyleName().contains(NEW_STATUS));
    Button down = (Button) container.getItem(sample).getItemProperty(DOWN).getValue();
    assertTrue(down.getStyleName().contains(DOWN));
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.enter("");

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), view.headerLabel.getValue());
    for (Column column : view.samplesGrid.getColumns()) {
      assertEquals(resources.message((String) column.getPropertyId()), column.getHeaderCaption());
    }
    assertEquals(resources.message(SAVE), view.saveButton.getCaption());
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    SubmissionSample sample = (SubmissionSample) samples.get(0);
    Object statusValue = container.getItem(sample).getItemProperty(STATUS).getValue();
    assertEquals(sample.getStatus().getLabel(locale), statusValue);
    ComboBox<SampleStatus> newStatus =
        (ComboBox<SampleStatus>) container.getItem(sample).getItemProperty(NEW_STATUS).getValue();
    for (SampleStatus status : SampleStatus.values()) {
      assertTrue(((ListDataProvider) newStatus.getDataProvider()).getItems().contains(status));
      assertEquals(status.getLabel(locale), newStatus.getItemCaptionGenerator().apply(status));
    }
    Button down = (Button) container.getItem(sample).getItemProperty(DOWN).getValue();
    assertEquals(resources.message(DOWN), down.getCaption());
  }

  @Test
  public void samplesGrid_Column() {
    presenter.init(view);

    assertTrue(view.samplesGrid.getSelectionModel() instanceof SelectionModel.None);
    assertEquals(NAME, view.samplesGrid.getColumns().get(0).getPropertyId());
    assertEquals(EXPERIENCE, view.samplesGrid.getColumns().get(1).getPropertyId());
    assertEquals(STATUS, view.samplesGrid.getColumns().get(2).getPropertyId());
    assertEquals(NEW_STATUS, view.samplesGrid.getColumns().get(3).getPropertyId());
    assertTrue(view.samplesGrid.getColumns().get(3).getRenderer() instanceof ComponentRenderer);
    assertEquals(DOWN, view.samplesGrid.getColumns().get(4).getPropertyId());
    assertTrue(view.samplesGrid.getColumns().get(4).getRenderer() instanceof ComponentRenderer);
  }

  @Test
  public void down() {
    presenter.init(view);
    presenter.enter("");
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    SubmissionSample sample1 = (SubmissionSample) samples.get(0);
    SubmissionSample sample2 = (SubmissionSample) samples.get(1);
    final ComboBox<SampleStatus> newStatus1 =
        (ComboBox<SampleStatus>) container.getItem(sample1).getItemProperty(NEW_STATUS).getValue();
    final ComboBox<SampleStatus> newStatus2 =
        (ComboBox<SampleStatus>) container.getItem(sample2).getItemProperty(NEW_STATUS).getValue();
    Button down1 = (Button) container.getItem(sample1).getItemProperty(DOWN).getValue();
    newStatus1.setValue(SampleStatus.ANALYSED);

    down1.click();

    assertEquals(SampleStatus.ANALYSED, newStatus1.getValue());
    assertEquals(SampleStatus.ANALYSED, newStatus2.getValue());
  }

  @Test
  public void updateStatus_Null() {
    presenter.init(view);
    presenter.enter("");
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    SubmissionSample sample = (SubmissionSample) samples.get(0);
    ComboBox<SampleStatus> newStatus =
        (ComboBox<SampleStatus>) container.getItem(sample).getItemProperty(NEW_STATUS).getValue();
    samples.stream().skip(1).forEach(otherSample -> {
      container.getItem(otherSample).getItemProperty(NEW_STATUS).getValue();
    });
    newStatus.setValue(null);
    when(submissionSampleService.get(any()))
        .thenAnswer(i -> entityManager.find(SubmissionSample.class, i.getArguments()[0]));

    view.saveButton.click();

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
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    SubmissionSample sample1 = (SubmissionSample) samples.get(0);
    SubmissionSample sample2 = (SubmissionSample) samples.get(1);
    ComboBox<SampleStatus> newStatus1 =
        (ComboBox<SampleStatus>) container.getItem(sample1).getItemProperty(NEW_STATUS).getValue();
    ComboBox<SampleStatus> newStatus2 =
        (ComboBox<SampleStatus>) container.getItem(sample2).getItemProperty(NEW_STATUS).getValue();
    newStatus1.setValue(SampleStatus.ANALYSED);
    newStatus2.setValue(SampleStatus.TO_DIGEST);
    when(submissionSampleService.get(any()))
        .thenAnswer(i -> entityManager.find(SubmissionSample.class, i.getArguments()[0]));

    view.saveButton.click();

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
    samples = (Collection<SubmissionSample>) container.getItemIds();
    sample1 = find(samples, sample1.getId()).orElse(null);
    sample2 = find(samples, sample2.getId()).orElse(null);
    assertEquals(sample1.getStatus().getLabel(locale),
        container.getItem(sample1).getItemProperty(STATUS).getValue());
    assertEquals(sample2.getStatus().getLabel(locale),
        container.getItem(sample2).getItemProperty(STATUS).getValue());
  }

  @Test
  public void updateStatus_Regress_Confirm() {
    presenter.init(view);
    presenter.enter("");
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    SubmissionSample sample1 = (SubmissionSample) samples.get(0);
    SubmissionSample sample2 = (SubmissionSample) samples.get(1);
    ComboBox<SampleStatus> newStatus1 =
        (ComboBox<SampleStatus>) container.getItem(sample1).getItemProperty(NEW_STATUS).getValue();
    container.getItem(sample2).getItemProperty(NEW_STATUS).getValue();
    newStatus1.setValue(SampleStatus.TO_APPROVE);
    when(submissionSampleService.get(any()))
        .thenAnswer(i -> entityManager.find(SubmissionSample.class, i.getArguments()[0]));
    final ConfirmDialog confirmDialog = new TestConfirmDialog(true);

    view.saveButton.click();

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
  public void updateStatus_Regress_Cancel() {
    presenter.init(view);
    presenter.enter("");
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    SubmissionSample sample1 = (SubmissionSample) samples.get(0);
    SubmissionSample sample2 = (SubmissionSample) samples.get(1);
    ComboBox<SampleStatus> newStatus1 =
        (ComboBox<SampleStatus>) container.getItem(sample1).getItemProperty(NEW_STATUS).getValue();
    container.getItem(sample2).getItemProperty(NEW_STATUS).getValue();
    newStatus1.setValue(SampleStatus.TO_APPROVE);
    when(submissionSampleService.get(any()))
        .thenAnswer(i -> entityManager.find(SubmissionSample.class, i.getArguments()[0]));
    final ConfirmDialog confirmDialog = new TestConfirmDialog(false);

    view.saveButton.click();

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
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    Collection<SubmissionSample> expectedSamples =
        samples.stream().map(s -> (SubmissionSample) s).collect(Collectors.toSet());

    Collection<?> itemIds = container.getItemIds();

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
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    Collection<?> itemIds = container.getItemIds();
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
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    Collection<?> itemIds = container.getItemIds();
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
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    Collection<?> itemIds = container.getItemIds();
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
