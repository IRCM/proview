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

import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class UiComponentTest {
  private UiComponent uiComponent;
  @Mock
  private UI ui;
  @Mock
  private Window window;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    uiComponent = new TestUiComponent();
  }

  @Test
  public void getMainUi() {
    uiComponent.addWindow(window);

    verify(ui).addWindow(window);
  }

  @SuppressWarnings("serial")
  private class TestUiComponent extends CustomComponent implements UiComponent {
    @Override
    public UI getUI() {
      return ui;
    }
  }
}
