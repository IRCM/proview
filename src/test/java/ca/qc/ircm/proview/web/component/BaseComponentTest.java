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

package ca.qc.ircm.proview.web.component;

import static org.junit.Assert.assertTrue;

import com.vaadin.ui.CustomComponent;
import org.junit.Before;
import org.junit.Test;

public class BaseComponentTest {
  private BaseComponent baseComponent;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    baseComponent = new TestBaseComponent();
  }

  @Test
  public void implementations() {
    assertTrue(baseComponent instanceof MessageResourcesComponent);
    assertTrue(baseComponent instanceof NavigationComponent);
    assertTrue(baseComponent instanceof NotificationComponent);
    assertTrue(baseComponent instanceof UiComponent);
    assertTrue(baseComponent instanceof ConfirmDialogComponent);
  }

  @SuppressWarnings("serial")
  private static class TestBaseComponent extends CustomComponent implements BaseComponent {
  }
}
