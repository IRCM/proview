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

package ca.qc.ircm.proview.test.config;

import static org.mockito.Mockito.when;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.UI;
import org.junit.Before;
import org.mockito.Mock;

/**
 * Additional functions for component testing.
 */
public abstract class AbstractComponentTestCase {
  @Mock
  protected UI ui;
  @Mock
  protected ConnectorTracker connectorTracker;
  @Mock
  protected VaadinSession vaadinSession;

  /**
   * Mock UI for tests.
   */
  @Before
  public void mockUiForTest() {
    when(ui.getConnectorTracker()).thenReturn(connectorTracker);
    when(ui.getSession()).thenReturn(vaadinSession);
    when(vaadinSession.hasLock()).thenReturn(true);
  }
}
