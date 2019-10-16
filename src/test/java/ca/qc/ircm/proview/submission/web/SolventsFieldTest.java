package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.FRENCH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class SolventsFieldTest extends AbstractViewTestCase {
  private SolventsField fields;
  private Locale locale = ENGLISH;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    fields = new SolventsField();
  }

  @Test
  public void styles() {
    for (Solvent value : Solvent.values()) {
      assertTrue(fields.fields.get(value).getClassName().contains(value.name()));
    }
  }

  @Test
  public void labels() {
    fields.localeChange(mock(LocaleChangeEvent.class));
    for (Solvent value : Solvent.values()) {
      assertTrue(fields.fields.get(value).getElement().getOuterHTML().replaceAll("\\s", "")
          .contains(value.getLabel(locale)));
    }
  }

  @Test
  public void localeChange() {
    fields.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    when(ui.getLocale()).thenReturn(locale);
    fields.localeChange(mock(LocaleChangeEvent.class));
    for (Solvent value : Solvent.values()) {
      assertTrue(fields.fields.get(value).getElement().getOuterHTML().replaceAll("\\s", "")
          .contains(value.getLabel(locale)));
    }
  }

  @Test
  public void generateModelValue_All() {
    for (Solvent value : Solvent.values()) {
      fields.fields.get(value).setValue(true);
    }

    List<Solvent> solvents = fields.getValue();
    assertEquals(Solvent.values().length, solvents.size());
    for (Solvent value : Solvent.values()) {
      assertTrue(solvents.contains(value));
    }
  }

  @Test
  public void getValue_Some() {
    List<Solvent> expected = Arrays.asList(Solvent.METHANOL, Solvent.OTHER);
    for (Solvent value : expected) {
      fields.fields.get(value).setValue(true);
    }

    List<Solvent> solvents = fields.getValue();
    assertEquals(expected.size(), solvents.size());
    for (Solvent value : expected) {
      assertTrue(solvents.contains(value));
    }
  }

  @Test
  public void setValue_All() {
    fields.setValue(Arrays.asList(Solvent.values()));

    for (Solvent value : Solvent.values()) {
      assertTrue(fields.fields.get(value).getValue());
    }
  }

  @Test
  public void setValue_Some() {
    List<Solvent> expected = Arrays.asList(Solvent.METHANOL, Solvent.OTHER);
    fields.setValue(expected);

    for (Solvent value : Solvent.values()) {
      assertEquals(expected.contains(value), fields.fields.get(value).getValue());
    }
  }

  @Test
  public void setValue_Null() {
    fields.setValue(null);

    for (Solvent value : Solvent.values()) {
      assertFalse(fields.fields.get(value).getValue());
    }
  }
}
