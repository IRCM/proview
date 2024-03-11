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

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link NotificationComponent}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class NotificationComponentTest extends SpringUIUnitTest {
  private NotificationComponentForTest notificationComponent = new NotificationComponentForTest();

  @Test
  public void showNotification_Text() {
    notificationComponent.showNotification("abc");

    Notification notification = $(Notification.class).last();
    assertEquals("abc", test(notification).getText());
    assertEquals(NotificationComponent.DEFAULT_DURATION, notification.getDuration());
    assertEquals(Position.BOTTOM_START, notification.getPosition());
  }

  @Test
  public void showNotification_TextDuration() {
    notificationComponent.showNotification("abc", 100);

    Notification notification = $(Notification.class).last();
    assertEquals("abc", test(notification).getText());
    assertEquals(100, notification.getDuration());
    assertEquals(Position.BOTTOM_START, notification.getPosition());
  }

  @Test
  public void showNotification_TextDurationPosition() {
    notificationComponent.showNotification("abc", 100, Position.TOP_END);

    Notification notification = $(Notification.class).last();
    assertEquals("abc", test(notification).getText());
    assertEquals(100, notification.getDuration());
    assertEquals(Position.TOP_END, notification.getPosition());
  }

  private class NotificationComponentForTest implements NotificationComponent {
  }
}
