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

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.submission.web.PrintSubmission.ID;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findChild;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PrintSubmissionTest extends AbstractViewTestCase {
  @Autowired
  private PrintSubmission component;
  @MockBean
  private SubmissionService service;
  @Mock
  private Submission submission;
  private Locale locale = ENGLISH;

  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    component.init();
  }

  @Test
  public void styles() {
    assertEquals(ID, component.getId().orElse(""));
  }

  @Test
  public void printContent() {
    String content = "<div id=\"test-div\">test content</div>";
    when(service.print(any(), any())).thenReturn(content);
    component.localeChange(mock(LocaleChangeEvent.class));
    component.setSubmission(submission);

    verify(service).print(submission, locale);
    Html html = findChild(component, Html.class).orElse(null);
    assertNotNull(html);
    assertEquals(new Html(content).getElement().getOuterHTML(), html.getElement().getOuterHTML());
  }

  @Test
  public void localeChange() {
    String content = "<div id=\"test-div\">test content</div>";
    when(service.print(any(), any())).thenReturn(content);
    component.localeChange(mock(LocaleChangeEvent.class));
    component.setSubmission(submission);
    Locale locale = FRENCH;
    when(ui.getLocale()).thenReturn(locale);
    String frenchcontent = "<div id=\"test-div\">test de contenu</div>";
    when(service.print(any(), eq(locale))).thenReturn(frenchcontent);
    component.localeChange(mock(LocaleChangeEvent.class));

    verify(service).print(submission, locale);
    Html html = findChild(component, Html.class).orElse(null);
    assertNotNull(html);
    assertEquals(new Html(frenchcontent).getElement().getOuterHTML(),
        html.getElement().getOuterHTML());
  }
}
