package ca.qc.ircm.proview.sample.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.web.PlateComponent;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import com.vaadin.shared.Registration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ContainerSelectionFormTest {
  private ContainerSelectionForm view;
  @Mock
  private ContainerSelectionFormPresenter presenter;
  @Mock
  private PlateComponent plateComponent;
  @Mock
  private SaveListener<List<SampleContainer>> saveListener;
  @Mock
  private List<Sample> samples;
  @Mock
  private List<SampleContainer> containers;
  @Captor
  private ArgumentCaptor<SaveEvent<List<SampleContainer>>> saveEventCaptor;

  @Before
  public void beforeTest() {
    view = new ContainerSelectionForm(presenter, plateComponent);
  }

  @Test
  public void addSaveListener() {
    Registration registration = view.addSaveListener(saveListener);

    List<?> listeners = new ArrayList<>(view.getListeners(SaveEvent.class));
    assertEquals(1, listeners.size());
    assertEquals(saveListener, listeners.get(0));
    assertNotNull(registration);
  }

  @Test
  public void fireSaveEvent() {
    view.addSaveListener(saveListener);

    view.fireSaveEvent(containers);

    verify(saveListener).saved(saveEventCaptor.capture());
    assertEquals(containers, saveEventCaptor.getValue().getSavedObject());
  }

  @Test
  public void getSamples() {
    when(presenter.getSamples()).thenReturn(samples);

    List<Sample> samples = view.getSamples();

    assertEquals(this.samples, samples);
  }

  @Test
  public void setSamples() {
    view.setSamples(samples);

    verify(presenter).setSamples(samples);
  }
}
