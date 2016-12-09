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

import static ca.qc.ircm.proview.web.WebConstants.SAVED_SUBMISSIONS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.component.SavedSubmissionsComponent;
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
@ServiceTestAnnotations
public class SavedSubmissionsComponentTest {
  private SavedSubmissionsComponent component;
  @Mock
  private UI ui;
  @Mock
  private ConnectorTracker connectorTracker;
  @Mock
  private VaadinSession session;
  private Collection<Submission> submissions = new ArrayList<>();
  private Submission submission1;
  private Submission submission2;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getConnectorTracker()).thenReturn(connectorTracker);
    when(ui.getSession()).thenReturn(session);
    when(session.hasLock()).thenReturn(true);
    component = new TestComponent();
    submissions.add(submission1);
    submissions.add(submission2);
  }

  @Test
  public void saveSubmissions() {
    component.saveSubmissions(submissions);

    verify(ui, atLeastOnce()).getSession();
    verify(session).setAttribute(SAVED_SUBMISSIONS, submissions);
  }

  @Test
  public void savedSubmissions() {
    when(session.getAttribute(any(String.class))).thenReturn(submissions);

    Collection<Submission> submissions = component.savedSubmissions();

    assertEquals(this.submissions.size(), submissions.size());
    assertTrue(this.submissions.containsAll(submissions));
    assertTrue(submissions.containsAll(this.submissions));
    verify(ui, atLeastOnce()).getSession();
    verify(session).getAttribute(SAVED_SUBMISSIONS);
  }

  @Test
  public void savedSubmissions_Null() {
    when(session.getAttribute(any(String.class))).thenReturn(null);

    Collection<Submission> submissions = component.savedSubmissions();

    assertTrue(submissions.isEmpty());
    verify(ui, atLeastOnce()).getSession();
    verify(session).getAttribute(SAVED_SUBMISSIONS);
  }

  @Test
  public void savedSubmissions_ModifyList() {
    when(session.getAttribute(any(String.class))).thenReturn(submissions);
    final int size = submissions.size();
    Collection<Submission> submissions = component.savedSubmissions();
    submissions.remove(0);

    submissions = component.savedSubmissions();

    assertEquals(size, submissions.size());
  }

  @SuppressWarnings("serial")
  private class TestComponent extends CustomComponent implements SavedSubmissionsComponent {
    @Override
    public UI getUI() {
      return ui;
    }
  }
}
