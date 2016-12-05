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

import static ca.qc.ircm.proview.web.WebConstants.SAVED_SUBMISSIONS;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
  @Mock
  private Collection<Submission> submissions;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getConnectorTracker()).thenReturn(connectorTracker);
    when(ui.getSession()).thenReturn(session);
    when(session.hasLock()).thenReturn(true);
    component = new TestComponent();
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

    assertSame(this.submissions, submissions);
    verify(ui, atLeastOnce()).getSession();
    verify(session).getAttribute(SAVED_SUBMISSIONS);
  }

  @SuppressWarnings("serial")
  private class TestComponent extends CustomComponent implements SavedSubmissionsComponent {
    @Override
    public UI getUI() {
      return ui;
    }
  }
}
