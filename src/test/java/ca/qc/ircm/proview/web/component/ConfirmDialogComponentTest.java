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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.vaadin.dialogs.ConfirmDialog;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class ConfirmDialogComponentTest {
  private ConfirmDialogComponent component;
  @Mock
  private UI ui;
  @Mock
  private ConnectorTracker connectorTracker;
  @Mock
  private Window window;
  @Mock
  private ConfirmDialog.Listener listener;
  @Mock
  private ConfirmDialog.Factory factory;
  @Mock
  private ConfirmDialog confirmDialog;
  private ConfirmDialog.Factory defaultFactory;
  private String windowCaption;
  private String message;
  private String okCaption;
  private String cancelCaption;
  private String notOkCaption;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getConnectorTracker()).thenReturn(connectorTracker);
    component = new TestComponent();
    defaultFactory = ConfirmDialog.getFactory();
    when(factory.create(any(), any(), any(), any(), any())).thenReturn(confirmDialog);
    ConfirmDialog.setFactory(factory);
  }

  @After
  public void afterTest() {
    ConfirmDialog.setFactory(defaultFactory);
  }

  @Test
  public void showConfirmDialog() {
    component.showConfirmDialog(windowCaption, message, okCaption, cancelCaption, listener);

    verify(factory).create(windowCaption, message, okCaption, cancelCaption, null);
    verify(confirmDialog).show(ui, listener, true);
  }

  @Test
  public void showConfirmDialog_NotOk() {
    component.showConfirmDialog(windowCaption, message, okCaption, cancelCaption, notOkCaption,
        listener);

    verify(factory).create(windowCaption, message, okCaption, cancelCaption, null);
    verify(confirmDialog).show(ui, listener, true);
  }

  @SuppressWarnings("serial")
  private class TestComponent extends CustomComponent implements ConfirmDialogComponent {
    @Override
    public UI getUI() {
      return ui;
    }
  }
}
