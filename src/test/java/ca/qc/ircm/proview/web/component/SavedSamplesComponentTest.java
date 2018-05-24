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

import static ca.qc.ircm.proview.web.WebConstants.SAVED_SAMPLES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.Sample;
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
public class SavedSamplesComponentTest {
  private SavedSamplesComponent component;
  @Mock
  private UI ui;
  @Mock
  private ConnectorTracker connectorTracker;
  @Mock
  private VaadinSession session;
  @PersistenceContext
  private EntityManager entityManager;
  private Collection<Sample> samples = new ArrayList<>();
  private Sample sample1;
  private Sample sample2;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getConnectorTracker()).thenReturn(connectorTracker);
    when(ui.getSession()).thenReturn(session);
    when(session.hasLock()).thenReturn(true);
    component = new TestComponent();
    samples.add(sample1);
    samples.add(sample2);
  }

  @Test
  public void saveSamples() {
    component.saveSamples(samples);

    verify(ui, atLeastOnce()).getSession();
    verify(session).setAttribute(SAVED_SAMPLES, samples);
  }

  @Test
  public void savedSamples() {
    when(session.getAttribute(any(String.class))).thenReturn(samples);

    Collection<Sample> samples = component.savedSamples();

    assertEquals(this.samples.size(), samples.size());
    assertTrue(this.samples.containsAll(samples));
    assertTrue(samples.containsAll(this.samples));
    verify(ui, atLeastOnce()).getSession();
    verify(session).getAttribute(SAVED_SAMPLES);
  }

  @Test
  public void savedSamples_Null() {
    when(session.getAttribute(any(String.class))).thenReturn(null);

    Collection<Sample> samples = component.savedSamples();

    assertTrue(samples.isEmpty());
    verify(ui, atLeastOnce()).getSession();
    verify(session).getAttribute(SAVED_SAMPLES);
  }

  @Test
  public void savedSamples_ModifyList() {
    when(session.getAttribute(any(String.class))).thenReturn(samples);
    final int size = samples.size();
    Collection<Sample> samples = component.savedSamples();
    samples.remove(samples.iterator().next());

    samples = component.savedSamples();

    assertEquals(size, samples.size());
  }

  @Test
  public void savedSampleFromMultipleUsers_True() {
    Collection<Sample> samples = new ArrayList<>();
    samples.add(entityManager.find(Sample.class, 442L));
    samples.add(entityManager.find(Sample.class, 443L));
    samples.add(entityManager.find(Sample.class, 444L));
    samples.add(entityManager.find(Sample.class, 446L));
    when(session.getAttribute(any(String.class))).thenReturn(samples);

    boolean multipleUsers = component.savedSampleFromMultipleUsers();

    assertTrue(multipleUsers);
  }

  @Test
  public void savedSampleFromMultipleUsers_False() {
    Collection<Sample> samples = new ArrayList<>();
    samples.add(entityManager.find(Sample.class, 442L));
    samples.add(entityManager.find(Sample.class, 443L));
    samples.add(entityManager.find(Sample.class, 444L));
    when(session.getAttribute(any(String.class))).thenReturn(samples);

    boolean multipleUsers = component.savedSampleFromMultipleUsers();

    assertFalse(multipleUsers);
  }

  @Test
  public void savedSampleFromMultipleUsers_FalseDueToNullUser() {
    Collection<Sample> samples = new ArrayList<>();
    samples.add(entityManager.find(Sample.class, 442L));
    samples.add(entityManager.find(Sample.class, 443L));
    samples.add(entityManager.find(Sample.class, 444L));
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 446L);
    sample.getSubmission().setUser(null);
    samples.add(sample);
    when(session.getAttribute(any(String.class))).thenReturn(samples);

    boolean multipleUsers = component.savedSampleFromMultipleUsers();

    assertFalse(multipleUsers);
  }

  @Test
  public void savedSampleFromMultipleUsers_Emtpy() {
    Collection<Sample> samples = new ArrayList<>();
    when(session.getAttribute(any(String.class))).thenReturn(samples);

    boolean multipleUsers = component.savedSampleFromMultipleUsers();

    assertFalse(multipleUsers);
  }

  @SuppressWarnings("serial")
  private class TestComponent extends CustomComponent implements SavedSamplesComponent {
    @Override
    public UI getUI() {
      return ui;
    }
  }
}
