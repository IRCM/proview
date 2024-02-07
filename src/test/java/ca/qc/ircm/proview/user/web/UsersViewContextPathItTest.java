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

package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.web.UsersView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.ViewLayoutElement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for {@link UsersView}.
 */
@TestBenchTestAnnotations
@ActiveProfiles({ "integration-test", "context-path" })
@WithUserDetails("proview@ircm.qc.ca")
public class UsersViewContextPathItTest extends AbstractTestBenchTestCase {
  @Value("${spring.application.name}")
  private String applicationName;

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  public void switchUser() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(4);

    view.switchUser().click();

    $(SubmissionsViewElement.class).waitForFirst();
    ViewLayoutElement viewLayout = $(ViewLayoutElement.class).waitForFirst();
    assertTrue(optional(() -> viewLayout.exitSwitchUser()).isPresent());
    assertFalse(optional(() -> viewLayout.users()).isPresent());
  }
}
