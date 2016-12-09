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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.vaadin.server.Page;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
@SuppressWarnings("deprecation")
public class NotificationComponentTest {
  private NotificationComponent notificationComponent;
  @Mock
  private UI ui;
  @Mock
  private Page page;
  @Mock
  private ConnectorTracker connectorTracker;
  @Captor
  private ArgumentCaptor<Notification> notificationCaptor;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getPage()).thenReturn(page);
    when(ui.getConnectorTracker()).thenReturn(connectorTracker);
    notificationComponent = new TestNotificationComponent();
  }

  @Test
  public void showError() {
    String message = "test_error";

    notificationComponent.showError(message);

    verify(ui).getPage();
    verify(page).showNotification(notificationCaptor.capture());
    Notification notification = notificationCaptor.getValue();
    assertEquals(message, notification.getCaption());
    assertEquals(Notification.Type.ERROR_MESSAGE.getStyle(), notification.getStyleName());
  }

  @Test
  public void showWarning() {
    String message = "test_warning";

    notificationComponent.showWarning(message);

    verify(ui).getPage();
    verify(page).showNotification(notificationCaptor.capture());
    Notification notification = notificationCaptor.getValue();
    assertEquals(message, notification.getCaption());
    assertEquals(Notification.Type.WARNING_MESSAGE.getStyle(), notification.getStyleName());
  }

  @Test
  public void showMessage() {
    String message = "test_message";

    notificationComponent.showMessage(message);

    verify(ui).getPage();
    verify(page).showNotification(notificationCaptor.capture());
    Notification notification = notificationCaptor.getValue();
    assertEquals(message, notification.getCaption());
    assertEquals(Notification.Type.HUMANIZED_MESSAGE.getStyle(), notification.getStyleName());
  }

  @Test
  public void showTrayNotification() {
    String message = "test_tray";

    notificationComponent.showTrayNotification(message);

    verify(ui).getPage();
    verify(page).showNotification(notificationCaptor.capture());
    Notification notification = notificationCaptor.getValue();
    assertEquals(message, notification.getCaption());
    assertEquals(Notification.Type.TRAY_NOTIFICATION.getStyle(), notification.getStyleName());
  }

  @SuppressWarnings("serial")
  private class TestNotificationComponent extends CustomComponent implements NotificationComponent {
    @Override
    public UI getUI() {
      return ui;
    }
  }
}
