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

package ca.qc.ircm.proview.utils.web;

import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.ui.CustomComponent;
import org.junit.Before;
import org.junit.Test;

public class BaseViewTest {
  private BaseView baseView;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    baseView = new TestBaseView();
  }

  @Test
  public void implementations() {
    assertTrue(baseView instanceof BaseComponent);
    assertTrue(baseView instanceof EnterView);
    assertTrue(baseView instanceof TitleView);
  }

  @SuppressWarnings("serial")
  private static class TestBaseView extends CustomComponent implements BaseView {
  }
}
