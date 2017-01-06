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

package ca.qc.ircm.proview.web.view;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class TitleViewTest {
  private TitleView titleView;
  @Mock
  private UI ui;
  @Mock
  private Page page;
  @Mock
  private ConnectorTracker connectorTracker;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getPage()).thenReturn(page);
    when(ui.getConnectorTracker()).thenReturn(connectorTracker);
    titleView = new TestTitleView();
  }

  @Test
  public void setTitle() {
    String title = "title_test";

    titleView.setTitle(title);

    verify(ui).getPage();
    verify(page).setTitle(title);
  }

  @SuppressWarnings("serial")
  private class TestTitleView extends CustomComponent implements TitleView {
    @Override
    public UI getUI() {
      return ui;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }
  }
}
