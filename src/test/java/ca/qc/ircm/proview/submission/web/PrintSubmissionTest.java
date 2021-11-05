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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFile;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.AbstractKaribuTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Tests for {@link PrintSubmission}.
 */
@ServiceTestAnnotations
public class PrintSubmissionTest extends AbstractKaribuTestCase {
  @Autowired
  private PrintSubmission component;
  @MockBean
  private SubmissionService service;
  @Mock
  private Submission submission;
  private Locale locale = ENGLISH;
  private final Random random = new Random();

  @BeforeEach
  public void beforeTest() {
    ui.setLocale(locale);
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
  public void printContent_ReplaceHref() throws Throwable {
    String content = "<div><a href=\"files-0\">abc.txt</a><a href=\"files-1\">def.txt</a></div>";
    when(service.print(any(), any())).thenReturn(content);
    SubmissionFile file1 = new SubmissionFile();
    file1.setFilename("file_1.txt");
    byte[] fileContent = new byte[1024];
    random.nextBytes(fileContent);
    file1.setContent(fileContent);
    SubmissionFile file2 = new SubmissionFile();
    file2.setFilename("file_2.xlsx");
    random.nextBytes(fileContent);
    file2.setContent(fileContent);
    when(submission.getFiles()).thenReturn(Arrays.asList(file1, file2));
    component.localeChange(mock(LocaleChangeEvent.class));
    component.setSubmission(submission);

    verify(service).print(submission, locale);
    Html html = findChild(component, Html.class).orElse(null);
    assertNotNull(html);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    Pattern pattern = Pattern.compile("<a href=\"(.*)\">");
    Matcher matcher = pattern.matcher(html.getElement().getOuterHTML());
    assertTrue(matcher.find());
    String uri = matcher.group(1);
    Optional<AbstractStreamResource> optionalResource =
        VaadinSession.getCurrent().getResourceRegistry().getResource(new URI(uri));
    assertTrue(optionalResource.isPresent());
    assertTrue(optionalResource.get() instanceof StreamResource);
    StreamResource resource = (StreamResource) optionalResource.get();
    resource.getWriter().accept(output, VaadinSession.getCurrent());
    assertArrayEquals(file1.getContent(), output.toByteArray());
    output.reset();
    assertTrue(matcher.find(matcher.end()));
    uri = matcher.group(1);
    optionalResource = VaadinSession.getCurrent().getResourceRegistry().getResource(new URI(uri));
    assertTrue(optionalResource.isPresent());
    assertTrue(optionalResource.get() instanceof StreamResource);
    resource = (StreamResource) optionalResource.get();
    resource.getWriter().accept(output, VaadinSession.getCurrent());
    assertArrayEquals(file2.getContent(), output.toByteArray());
  }

  @Test
  public void localeChange() {
    String content = "<div id=\"test-div\">test content</div>";
    when(service.print(any(), any())).thenReturn(content);
    component.localeChange(mock(LocaleChangeEvent.class));
    component.setSubmission(submission);
    Locale locale = FRENCH;
    String frenchcontent = "<div id=\"test-div\">test de contenu</div>";
    when(service.print(any(), eq(locale))).thenReturn(frenchcontent);
    ui.setLocale(locale);
    component.localeChange(mock(LocaleChangeEvent.class));

    verify(service).print(submission, locale);
    Html html = findChild(component, Html.class).orElse(null);
    assertNotNull(html);
    assertEquals(new Html(frenchcontent).getElement().getOuterHTML(),
        html.getElement().getOuterHTML());
  }
}
