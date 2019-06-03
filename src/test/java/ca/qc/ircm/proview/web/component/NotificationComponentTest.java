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

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.server.Page;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.Notification;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
// TODO Fix tests.
public class NotificationComponentTest {
  private NotificationComponent notificationComponent;
  @Mock
  private Page page;
  @Mock
  private ConnectorTracker connectorTracker;
  @Captor
  private ArgumentCaptor<Notification> notificationCaptor;
  //private TestUi ui = new TestUi();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    //when(page.getUI()).thenReturn(ui);
    //notificationComponent = new TestNotificationComponent();
  }

  /*
  @Test
  public void showError() {
    String message = "test_error";
  
    notificationComponent.showError(message);
  
    assertTrue(containsInstanceOf(ui.getExtensions(), Notification.class));
    Notification notification = findInstanceOf(ui.getExtensions(), Notification.class).get();
    assertEquals(message, notification.getCaption());
    assertEquals(Notification.Type.ERROR_MESSAGE.getStyle(), notification.getStyleName());
  }
  
  @Test
  public void showWarning() {
    String message = "test_warning";
  
    notificationComponent.showWarning(message);
  
    assertTrue(containsInstanceOf(ui.getExtensions(), Notification.class));
    Notification notification = findInstanceOf(ui.getExtensions(), Notification.class).get();
    assertEquals(message, notification.getCaption());
    assertEquals(Notification.Type.WARNING_MESSAGE.getStyle(), notification.getStyleName());
  }
  
  @Test
  public void showMessage() {
    String message = "test_message";
  
    notificationComponent.showMessage(message);
  
    assertTrue(containsInstanceOf(ui.getExtensions(), Notification.class));
    Notification notification = findInstanceOf(ui.getExtensions(), Notification.class).get();
    assertEquals(message, notification.getCaption());
    assertEquals(Notification.Type.HUMANIZED_MESSAGE.getStyle(), notification.getStyleName());
  }
  
  @Test
  public void showTrayNotification() {
    String message = "test_tray";
  
    notificationComponent.showTrayNotification(message);
  
    assertTrue(containsInstanceOf(ui.getExtensions(), Notification.class));
    Notification notification = findInstanceOf(ui.getExtensions(), Notification.class).get();
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
  
  @SuppressWarnings("serial")
  private class TestUi extends UI {
    @Override
    public Page getPage() {
      return page;
    }
  
    @Override
    protected void init(VaadinRequest request) {
    }
  }
  */
}
