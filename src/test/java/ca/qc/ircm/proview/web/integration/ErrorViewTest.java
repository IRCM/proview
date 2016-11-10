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

package ca.qc.ircm.proview.web.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.ErrorView;
import ca.qc.ircm.proview.web.MainView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
public class ErrorViewTest extends ErrorPageObject {
  @Test
  public void title() throws Throwable {
    open();

    assertTrue(message(resources(ErrorView.class), "title").contains(getDriver().getTitle()));
  }

  @Test
  public void fieldPositions() throws Throwable {
    open();

    int previous = 0;
    int current;
    current = errorLabel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = mainViewButton().getLocation().y;
    assertTrue(previous < current);
  }

  @Test
  public void returnMainView() throws Throwable {
    open();

    clickMainViewButton();

    assertEquals(viewUrl(MainView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}
