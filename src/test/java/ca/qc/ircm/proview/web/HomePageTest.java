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

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.DontSkipAbout;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@DontSkipAbout
public class HomePageTest extends AbstractTestBenchTestCase {
  private void open() {
    openView(MainView.VIEW_NAME);
  }

  @Test
  public void intro_NotSigned() throws Throwable {
    open();

    assertEquals(viewUrl(AboutView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  @Ignore("Does not work since ShiroTestExecutionListener needs to access the app to set cookie")
  public void intro_Signed() throws Throwable {
    open();

    assertEquals(viewUrl(AboutView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}
