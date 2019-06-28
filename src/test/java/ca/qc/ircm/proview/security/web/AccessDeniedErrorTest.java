/*
 * Copyright (c) 2018 Institut de recherches cliniques de Montreal (IRCM)
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

package ca.qc.ircm.proview.security.web;

import static ca.qc.ircm.proview.security.web.AccessDeniedError.TEXT;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class AccessDeniedErrorTest extends AbstractViewTestCase {
  private AccessDeniedError view;
  @Mock
  private BeforeEnterEvent event;
  @Mock
  private ErrorParameter<AccessDeniedException> parameter;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(AccessDeniedError.class, locale);
  private MessageResource generalResources = new MessageResource(WebConstants.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    view = new AccessDeniedError();
  }

  @Test
  public void labels() {
    view.setErrorParameter(event, parameter);
    assertEquals(resources.message(TEXT), view.getText());
  }

  @Test
  public void getPageTitle() {
    assertEquals(resources.message(TITLE, generalResources.message(APPLICATION_NAME)),
        view.getPageTitle());
  }
}
