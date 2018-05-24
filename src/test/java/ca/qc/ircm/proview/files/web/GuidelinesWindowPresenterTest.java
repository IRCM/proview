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

package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.files.web.GuidelinesWindowPresenter.TITLE;
import static ca.qc.ircm.proview.files.web.GuidelinesWindowPresenter.WINDOW_STYLE;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.CloseWindowOnViewChange.CloseWindowOnViewChangeListener;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.navigator.Navigator;
import com.vaadin.ui.UI;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class GuidelinesWindowPresenterTest {
  private GuidelinesWindowPresenter presenter;
  @Mock
  private GuidelinesWindow view;
  @Mock
  private UI ui;
  @Mock
  private Navigator navigator;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(GuidelinesWindow.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new GuidelinesWindowPresenter();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getUI()).thenReturn(ui);
    when(ui.getNavigator()).thenReturn(navigator);
  }

  @Test
  public void styles() {
    presenter.init(view);

    verify(view).addStyleName(WINDOW_STYLE);
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setCaption(resources.message(TITLE));
  }

  @Test
  public void closeWindowOnViewChange() {
    presenter.init(view);

    verify(navigator).addViewChangeListener(any(CloseWindowOnViewChangeListener.class));
  }
}
