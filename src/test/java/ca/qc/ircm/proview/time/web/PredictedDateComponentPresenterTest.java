package ca.qc.ircm.proview.time.web;

import static ca.qc.ircm.proview.time.web.PredictedDateComponentPresenter.DATE;
import static ca.qc.ircm.proview.time.web.PredictedDateComponentPresenter.DESCRIPTION;
import static ca.qc.ircm.proview.time.web.PredictedDateComponentPresenter.PREDICTED;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.time.PredictedDate;
import ca.qc.ircm.utils.MessageResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class PredictedDateComponentPresenterTest {
  private PredictedDateComponentPresenter presenter;
  @Mock
  private PredictedDateComponent component;
  @Captor
  private ArgumentCaptor<PredictedDate> predictedDateCaptor;
  private PredictedDateComponentDesign design = new PredictedDateComponentDesign();
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(PredictedDateComponent.class, locale);

  @Before
  public void beforeTest() {
    presenter = new PredictedDateComponentPresenter();
    component.design = design;
    when(component.getLocale()).thenReturn(locale);
    when(component.getResources()).thenReturn(resources);
  }

  @Test
  public void styles() {
    presenter.init(component);

    assertTrue(design.predicted.getStyleName().contains(PREDICTED));
    assertTrue(design.date.getStyleName().contains(DATE));
  }

  @Test
  public void captions() {
    presenter.init(component);

    assertEquals(resources.message(property(PREDICTED, DESCRIPTION)),
        design.predicted.getDescription());
  }

  @Test
  public void fireValueChangeEvent_DateChanged() {
    presenter.init(component);
    LocalDate now = LocalDate.now();
    design.date.setValue(now);

    verify(component).fireValueChangeEvent(predictedDateCaptor.capture(), eq(false));
    PredictedDate date = predictedDateCaptor.getValue();
    assertFalse(date.expected);
    assertNull(date.date);
  }

  @Test
  public void fireValueChangeEvent_PredictedChanged() {
    presenter.init(component);
    design.predicted.setValue(true);

    verify(component).fireValueChangeEvent(predictedDateCaptor.capture(), eq(false));
    PredictedDate date = predictedDateCaptor.getValue();
    assertFalse(date.expected);
    assertNull(date.date);
  }

  @Test
  public void getValue() {
    presenter.init(component);

    PredictedDate date = presenter.getValue();
    assertFalse(date.expected);
    assertNull(date.date);
  }

  @Test
  public void getValue_Filled() {
    presenter.init(component);
    LocalDate now = LocalDate.now();
    design.date.setValue(now);
    design.predicted.setValue(true);

    PredictedDate date = presenter.getValue();
    assertTrue(date.expected);
    assertEquals(now, date.date);
  }

  @Test
  public void setValue_Empty() {
    presenter.init(component);
    LocalDate now = LocalDate.now();
    design.date.setValue(now);
    design.predicted.setValue(true);

    presenter.setValue(new PredictedDate());

    assertFalse(design.predicted.getValue());
    assertNull(design.date.getValue());
  }

  @Test
  public void setValue_Filled() {
    presenter.init(component);
    LocalDate now = LocalDate.now();

    presenter.setValue(new PredictedDate(now, true));

    assertTrue(design.predicted.getValue());
    assertEquals(now, design.date.getValue());
  }

  @Test
  public void setValue_Null() {
    presenter.init(component);

    presenter.setValue(null);

    assertFalse(design.predicted.getValue());
    assertNull(design.date.getValue());
  }
}
