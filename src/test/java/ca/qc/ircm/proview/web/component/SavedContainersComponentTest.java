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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SavedContainersComponentTest {
  private SavedContainersComponent component;
  @Mock
  private UI ui;
  @Mock
  private ConnectorTracker connectorTracker;
  @Mock
  private VaadinSession session;
  @PersistenceContext
  private EntityManager entityManager;
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

  @Test
  public void savedContainersFromMultipleUsers_True() {
    Collection<SampleContainer> containers = new ArrayList<>();
    containers.add(entityManager.find(SampleContainer.class, 2L));
    containers.add(entityManager.find(SampleContainer.class, 3L));
    containers.add(entityManager.find(SampleContainer.class, 4L));
    containers.add(entityManager.find(SampleContainer.class, 8L));
    when(session.getAttribute(any(String.class))).thenReturn(containers);

    boolean multipleUsers = component.savedContainersFromMultipleUsers();

    assertTrue(multipleUsers);
  }

  @Test
  public void savedContainersFromMultipleUsers_False() {
    Collection<SampleContainer> containers = new ArrayList<>();
    containers.add(entityManager.find(SampleContainer.class, 2L));
    containers.add(entityManager.find(SampleContainer.class, 3L));
    containers.add(entityManager.find(SampleContainer.class, 4L));
    when(session.getAttribute(any(String.class))).thenReturn(containers);

    boolean multipleUsers = component.savedContainersFromMultipleUsers();

    assertFalse(multipleUsers);
  }

  @Test
  public void savedContainersFromMultipleUsers_FalseDueToNullUser() {
    Collection<SampleContainer> containers = new ArrayList<>();
    containers.add(entityManager.find(SampleContainer.class, 2L));
    containers.add(entityManager.find(SampleContainer.class, 3L));
    containers.add(entityManager.find(SampleContainer.class, 4L));
    SampleContainer container = entityManager.find(SampleContainer.class, 8L);
    ((SubmissionSample) container.getSample()).getSubmission().setUser(null);
    containers.add(container);
    when(session.getAttribute(any(String.class))).thenReturn(containers);

    boolean multipleUsers = component.savedContainersFromMultipleUsers();

    assertFalse(multipleUsers);
  }

  @Test
  public void savedContainersFromMultipleUsers_FalseDueToNullSample() {
    Collection<SampleContainer> containers = new ArrayList<>();
    containers.add(entityManager.find(SampleContainer.class, 2L));
    containers.add(entityManager.find(SampleContainer.class, 3L));
    containers.add(entityManager.find(SampleContainer.class, 4L));
    containers.add(entityManager.find(SampleContainer.class, 130L));
    when(session.getAttribute(any(String.class))).thenReturn(containers);

    boolean multipleUsers = component.savedContainersFromMultipleUsers();

    assertFalse(multipleUsers);
  }

  @Test
  public void savedContainersFromMultipleUsers_Emtpy() {
    Collection<SampleContainer> containers = new ArrayList<>();
    when(session.getAttribute(any(String.class))).thenReturn(containers);

    boolean multipleUsers = component.savedContainersFromMultipleUsers();

    assertFalse(multipleUsers);
  }

  @SuppressWarnings("serial")
  private class TestComponent extends CustomComponent implements SavedContainersComponent {
    @Override
    public UI getUI() {
      return ui;
    }
  }
}
