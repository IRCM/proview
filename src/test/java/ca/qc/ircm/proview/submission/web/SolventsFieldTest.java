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

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SolventsField}.
 */
@NonTransactionalTestAnnotations
public class SolventsFieldTest extends AbstractViewTestCase {
  private SolventsField fields;
  private Locale locale = ENGLISH;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    fields = new SolventsField();
  }

  @Test
  public void styles() {
    for (Solvent value : Solvent.values()) {
      assertTrue(fields.fields.get(value).hasClassName(value.name()));
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
