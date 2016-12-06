package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.COMPONENTS;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.EXPERIENCE;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.HEADER;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.NAME;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.NEW_STATUS;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.SAMPLES;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.SAVE;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.STATUS;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Container;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.Label;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
  @PersistenceContext
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SampleStatusView.class, locale);
  private List<Submission> submissions = new ArrayList<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SampleStatusViewPresenter(applicationName);
    view.headerLabel = new Label();
    view.samplesGrid = new Grid();
    view.saveButton = new Button();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.savedSubmissions()).thenReturn(submissions);
    submissions.add(entityManager.find(Submission.class, 32L));
    submissions.add(entityManager.find(Submission.class, 33L));
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(view.headerLabel.getStyleName().contains(HEADER));
    assertTrue(view.samplesGrid.getStyleName().contains(SAMPLES));
    assertTrue(view.samplesGrid.getStyleName().contains(COMPONENTS));
    assertTrue(view.saveButton.getStyleName().contains(SAVE));
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
    SubmissionSample sample = submissions.get(0).getSamples().get(0);
    ComboBox newStatus =
        (ComboBox) container.getItem(sample).getItemProperty(NEW_STATUS).getValue();
    for (SampleStatus status : SampleStatus.values()) {
      assertTrue(newStatus.getItemIds().contains(status));
      assertEquals(status.getLabel(locale), newStatus.getItemCaption(status));
    }
  }

  @Test
  public void samplesGrid_Column() {
    presenter.init(view);

    assertTrue(view.samplesGrid.getSelectionModel() instanceof SelectionModel.Multi);
    assertEquals(NAME, view.samplesGrid.getColumns().get(0).getPropertyId());
    assertEquals(EXPERIENCE, view.samplesGrid.getColumns().get(1).getPropertyId());
    assertEquals(STATUS, view.samplesGrid.getColumns().get(2).getPropertyId());
    assertEquals(NEW_STATUS, view.samplesGrid.getColumns().get(3).getPropertyId());
  }

  @Test
  public void defaultSamples() {
    presenter.init(view);
    presenter.enter("");
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    Collection<SubmissionSample> expectedSamples =
        submissions.stream().flatMap(s -> s.getSamples().stream()).collect(Collectors.toSet());

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
  public void updateStatus_Null() {
    presenter.init(view);
    presenter.enter("");
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    SubmissionSample sample = submissions.get(0).getSamples().get(0);
    ComboBox newStatus =
        (ComboBox) container.getItem(sample).getItemProperty(NEW_STATUS).getValue();
    newStatus.setValue(null);

    // TODO Test setting null value.
    System.out.println(newStatus.getValue());
  }

  @Test
  public void updateStatus_Regress() {
  }

  @Test
  public void updateStatus() {
    presenter.init(view);
    presenter.enter("");
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    SubmissionSample sample1 = submissions.get(0).getSamples().get(0);
    SubmissionSample sample2 = submissions.get(1).getSamples().get(0);
    ComboBox newStatus1 =
        (ComboBox) container.getItem(sample1).getItemProperty(NEW_STATUS).getValue();
    ComboBox newStatus2 =
        (ComboBox) container.getItem(sample2).getItemProperty(NEW_STATUS).getValue();
    newStatus1.setValue(SampleStatus.ANALYSED);
    newStatus2.setValue(SampleStatus.TO_DIGEST);

    view.saveButton.click();

    // TODO Test change in database.
  }
}
