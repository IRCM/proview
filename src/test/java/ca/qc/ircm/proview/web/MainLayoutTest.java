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

package ca.qc.ircm.proview.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class MainLayoutTest {
  private MainLayout layout;
  @Mock
  private Menu menu;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    layout = new MainLayout(menu);
  }

  @Test
  public void styles() {
    layout.init();

    assertEquals(1, layout.design.menuLayout.getComponentCount());
    assertEquals(menu, layout.design.menuLayout.getComponent(0));
    assertTrue(layout.design.menuLayout.getStyleName().contains(ValoTheme.LAYOUT_CARD));
    assertTrue(layout.design.content.getStyleName().contains(ValoTheme.PANEL_BORDERLESS));
  }
}
