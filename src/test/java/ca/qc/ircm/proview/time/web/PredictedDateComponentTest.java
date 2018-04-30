package ca.qc.ircm.proview.time.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.AbstractComponentTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.time.PredictedDate;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.shared.Registration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class PredictedDateComponentTest extends AbstractComponentTestCase {
  private PredictedDateComponent component;
  @Mock
  private PredictedDateComponentPresenter presenter;
  @Mock
  private PredictedDate date;
  @Mock
  private PredictedDate oldDate;
  @Mock
  private ValueChangeListener<PredictedDate> listener;
  @Captor
  private ArgumentCaptor<ValueChangeEvent<PredictedDate>> eventCaptor;

  @Before
  public void beforeTest() {
    component = new PredictedDateComponent(presenter);
    component.setParent(ui);
  }

  @Test
  public void attach() {
    verify(presenter, atLeastOnce()).init(component);
  }

  @Test
  public void isReadOnly() {
    assertFalse(component.isReadOnly());
  }

  @Test
  public void isReadOnly_True() {
    component.design.date.setReadOnly(true);
    component.design.predicted.setReadOnly(true);

    assertTrue(component.isReadOnly());
  }

  @Test
  public void isReadOnly_False() {
    component.design.date.setReadOnly(false);
    component.design.predicted.setReadOnly(false);

    assertFalse(component.isReadOnly());
  }

  @Test
  public void setReadOnly_True() {
    component.setReadOnly(true);

    assertTrue(component.design.date.isReadOnly());
    assertTrue(component.design.predicted.isReadOnly());
  }

  @Test
  public void setReadOnly_False() {
    component.setReadOnly(false);

    assertFalse(component.design.date.isReadOnly());
    assertFalse(component.design.predicted.isReadOnly());
  }

  @Test
  public void isRequiredIndicatorVisible() {
    assertFalse(component.isRequiredIndicatorVisible());
  }

  @Test
  public void isRequiredIndicatorVisible_True() {
    component.design.date.setRequiredIndicatorVisible(true);

    assertTrue(component.isRequiredIndicatorVisible());
  }

  @Test
  public void isRequiredIndicatorVisible_False() {
    component.design.date.setRequiredIndicatorVisible(false);

    assertFalse(component.isRequiredIndicatorVisible());
  }

  @Test
  public void setRequiredIndicatorVisible_True() {
    component.setRequiredIndicatorVisible(true);

    assertTrue(component.design.date.isRequiredIndicatorVisible());
  }

  @Test
  public void setRequiredIndicatorVisible_False() {
    component.setRequiredIndicatorVisible(false);

    assertFalse(component.design.date.isRequiredIndicatorVisible());
  }

  @Test
  public void getValue() {
    when(presenter.getValue()).thenReturn(date);

    assertEquals(date, component.getValue());

    verify(presenter).getValue();
  }

  @Test
  public void setValue() {
    component.setValue(date);

    verify(presenter).setValue(date);
  }

  @Test
  public void addValueChangeListener() {
    when(presenter.getValue()).thenReturn(date);
    component.addValueChangeListener(listener);

    component.fireValueChangeEvent(oldDate, false);

    verify(listener).valueChange(eventCaptor.capture());
    ValueChangeEvent<PredictedDate> event = eventCaptor.getValue();
    assertEquals(component, event.getComponent());
    assertEquals(component, event.getSource());
    assertEquals(oldDate, event.getOldValue());
    assertEquals(date, event.getValue());
  }

  @Test
  public void addValueChangeListener_Remove() {
    Registration registration = component.addValueChangeListener(listener);
    registration.remove();

    component.fireValueChangeEvent(date, false);

    verify(listener, never()).valueChange(any());
  }
}
