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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class MainUiComponentTest {
  private MainUiComponent mainUiComponent;
  @Mock
  private MainUi ui;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    mainUiComponent = new TestMainUiComponent();
  }

  @Test
  public void getMainUi() {
    MainUi mainUi = mainUiComponent.getMainUi();

    assertEquals(ui, mainUi);
  }

  @Test
  public void getUrl() {
    String expectedUrl = "testView";
    when(ui.getUrl(any())).thenReturn(expectedUrl);
    String viewName = ErrorView.VIEW_NAME;

    String url = mainUiComponent.getUrl(viewName);

    verify(ui).getUrl(viewName);
    assertEquals(expectedUrl, url);
  }

  @SuppressWarnings("serial")
  private class TestMainUiComponent extends CustomComponent implements MainUiComponent {
    @Override
    public UI getUI() {
      return ui;
    }
  }
}
