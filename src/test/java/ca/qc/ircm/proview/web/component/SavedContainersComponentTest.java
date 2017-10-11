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

import static ca.qc.ircm.proview.web.WebConstants.SAVED_CONTAINERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class SavedContainersComponentTest {
  private SavedContainersComponent component;
  @Mock
  private UI ui;
  @Mock
  private ConnectorTracker connectorTracker;
  @Mock
  private VaadinSession session;
  private Collection<SampleContainer> containers = new ArrayList<>();
  private SampleContainer container1;
  private SampleContainer container2;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getConnectorTracker()).thenReturn(connectorTracker);
    when(ui.getSession()).thenReturn(session);
    when(session.hasLock()).thenReturn(true);
    component = new TestComponent();
    containers.add(container1);
    containers.add(container2);
  }

  @Test
  public void saveContainers() {
    component.saveContainers(containers);

    verify(ui, atLeastOnce()).getSession();
    verify(session).setAttribute(SAVED_CONTAINERS, containers);
  }

  @Test
  public void savedContainers() {
    when(session.getAttribute(any(String.class))).thenReturn(containers);

    Collection<SampleContainer> containers = component.savedContainers();

    assertEquals(this.containers.size(), containers.size());
    assertTrue(this.containers.containsAll(containers));
    assertTrue(containers.containsAll(this.containers));
    verify(ui, atLeastOnce()).getSession();
    verify(session).getAttribute(SAVED_CONTAINERS);
  }

  @Test
  public void savedContainers_Null() {
    when(session.getAttribute(any(String.class))).thenReturn(null);

    Collection<SampleContainer> containers = component.savedContainers();

    assertTrue(containers.isEmpty());
    verify(ui, atLeastOnce()).getSession();
    verify(session).getAttribute(SAVED_CONTAINERS);
  }

  @Test
  public void savedContainers_ModifyList() {
    when(session.getAttribute(any(String.class))).thenReturn(containers);
    final int size = containers.size();
    Collection<SampleContainer> containers = component.savedContainers();
    containers.remove(containers.iterator().next());

    containers = component.savedContainers();

    assertEquals(size, containers.size());
  }

  @SuppressWarnings("serial")
  private class TestComponent extends CustomComponent implements SavedContainersComponent {
    @Override
    public UI getUI() {
      return ui;
    }
  }
}
