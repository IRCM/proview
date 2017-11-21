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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.digestion.web.DigestionView;
import ca.qc.ircm.proview.dilution.web.DilutionView;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.Registration;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class CloseWindowOnViewChangeTest {
  @Mock
  private Window window;
  @Mock
  private UI ui;
  @Mock
  private Navigator navigator;
  @Mock
  private Registration registration;
  @Captor
  private ArgumentCaptor<ViewChangeListener> listenerCaptor;

  @Test
  public void closeWindowOnViewChange() {
    when(window.getUI()).thenReturn(ui);
    when(ui.getNavigator()).thenReturn(navigator);
    when(navigator.addViewChangeListener(any())).thenReturn(registration);

    CloseWindowOnViewChange.closeWindowOnViewChange(window);

    verify(navigator).addViewChangeListener(listenerCaptor.capture());
    assertTrue(listenerCaptor.getValue().beforeViewChange(new ViewChangeEvent(navigator,
        new DigestionView(), new DilutionView(), DilutionView.VIEW_NAME, "")));
    verify(registration).remove();
    verify(window).close();
  }
}
