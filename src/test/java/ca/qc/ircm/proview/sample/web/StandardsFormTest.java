package ca.qc.ircm.proview.sample.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class StandardsFormTest {
  private StandardsForm view;
  @Mock
  private StandardsFormPresenter presenter;
  @Mock
  private List<Standard> standards;

  @Before
  public void beforeTest() {
    view = new StandardsForm(presenter);
  }

  @Test
  public void validate_False() {
    when(presenter.validate()).thenReturn(false);

    assertFalse(view.validate());

    verify(presenter).validate();
  }

  @Test
  public void validate_True() {
    when(presenter.validate()).thenReturn(true);

    assertTrue(view.validate());

    verify(presenter).validate();
  }

  @Test
  public void getValue() {
    when(presenter.getValue()).thenReturn(standards);

    assertSame(this.standards, view.getValue());

    verify(presenter).getValue();
  }

  @Test
  public void setValue() {
    view.setValue(standards);

    verify(presenter).setValue(standards);
  }

  @Test
  public void getMaxCount_2() {
    when(presenter.getMaxCount()).thenReturn(2);

    assertEquals(2, view.getMaxCount());

    verify(presenter).getMaxCount();
  }

  @Test
  public void getMaxCount_22() {
    when(presenter.getMaxCount()).thenReturn(22);

    assertEquals(22, view.getMaxCount());

    verify(presenter).getMaxCount();
  }

  @Test
  public void setMaxCount_1() {
    view.setMaxCount(1);

    verify(presenter).setMaxCount(1);
  }

  @Test
  public void setMaxCount_21() {
    view.setMaxCount(21);

    verify(presenter).setMaxCount(21);
  }

  @Test
  public void isReadOnly_False() {
    when(presenter.isReadOnly()).thenReturn(false);

    assertFalse(view.isReadOnly());

    verify(presenter).isReadOnly();
  }

  @Test
  public void isReadOnly_True() {
    when(presenter.isReadOnly()).thenReturn(true);

    assertTrue(view.isReadOnly());

    verify(presenter).isReadOnly();
  }

  @Test
  public void setReadOnly_False() {
    view.setReadOnly(false);

    verify(presenter).setReadOnly(false);
  }

  @Test
  public void setReadOnly_True() {
    view.setReadOnly(true);

    verify(presenter).setReadOnly(true);
  }
}
