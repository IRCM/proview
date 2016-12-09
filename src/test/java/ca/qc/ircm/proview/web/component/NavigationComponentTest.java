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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.component.NavigationComponent;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class NavigationComponentTest {
  private NavigationComponent navigationComponent;
  @Mock
  private UI ui;
  @Mock
  private ConnectorTracker connectorTracker;
  @Mock
  private SpringNavigator navigator;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getConnectorTracker()).thenReturn(connectorTracker);
    when(ui.getNavigator()).thenReturn(navigator);
    navigationComponent = new TestNavigationComponent();
  }

  @Test
  public void navigateTo() {
    String view = "test_view";

    navigationComponent.navigateTo(view);

    verify(ui).getNavigator();
    verify(navigator).navigateTo(view);
  }

  @SuppressWarnings("serial")
  private class TestNavigationComponent extends CustomComponent implements NavigationComponent {
    @Override
    public UI getUI() {
      return ui;
    }
  }
}
