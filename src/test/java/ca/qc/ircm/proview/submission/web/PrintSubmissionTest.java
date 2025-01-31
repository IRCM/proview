package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.submission.web.PrintSubmission.ID;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findChild;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link PrintSubmission}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class PrintSubmissionTest extends SpringUIUnitTest {

  private PrintSubmission component;
  @MockitoBean
  private SubmissionService service;
  @Autowired
  private SubmissionRepository repository;
  private final Locale locale = ENGLISH;

  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    when(service.get(anyLong())).thenReturn(repository.findById(164L));
    when(service.print(any(), any())).thenReturn("");
    navigate(PrintSubmissionView.class, 164L);
    component = $(PrintSubmission.class).first();
  }

  @Test
  public void styles() {
    assertEquals(ID, component.getId().orElse(""));
  }

  @Test
  public void printContent() {
    String content = "<div id=\"test-div\">test content</div>";
    when(service.print(any(), any())).thenReturn(content);
    Submission submission = repository.findById(164L).orElseThrow();
    component.setSubmission(submission);

    verify(service, atLeast(2)).print(submission, locale);
    Html html = findChild(component, Html.class).orElseThrow();
    assertEquals(new Html(content).getElement().getOuterHTML(), html.getElement().getOuterHTML());
  }

  @Test
  public void printContent_ReplaceHref() throws Throwable {
    String content = "<div><a href=\"files-0\">abc.txt</a><a href=\"files-1\">def.txt</a></div>";
    when(service.print(any(), any())).thenReturn(content);
    Submission submission = repository.findById(1L).orElseThrow();
    component.setSubmission(submission);

    verify(service).print(submission, locale);
    Html html = findChild(component, Html.class).orElseThrow();
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    Pattern pattern = Pattern.compile("<a href=\"([^>]*)\">");
    Matcher matcher = pattern.matcher(html.getElement().getOuterHTML());
    assertTrue(matcher.find());
    String uri = matcher.group(1);
    Optional<AbstractStreamResource> optionalResource =
        VaadinSession.getCurrent().getResourceRegistry().getResource(new URI(uri));
    assertTrue(optionalResource.isPresent());
    assertInstanceOf(StreamResource.class, optionalResource.get());
    StreamResource resource = (StreamResource) optionalResource.get();
    resource.getWriter().accept(output, VaadinSession.getCurrent());
    byte[] file1Content = Files.readAllBytes(
        Path.of(Objects.requireNonNull(getClass().getResource("/submissionfile1.txt")).toURI()));
    assertArrayEquals(file1Content, output.toByteArray());
    output.reset();
    assertTrue(matcher.find(matcher.end()));
    uri = matcher.group(1);
    optionalResource = VaadinSession.getCurrent().getResourceRegistry().getResource(new URI(uri));
    assertTrue(optionalResource.isPresent());
    assertInstanceOf(StreamResource.class, optionalResource.get());
    resource = (StreamResource) optionalResource.get();
    resource.getWriter().accept(output, VaadinSession.getCurrent());
    byte[] file2Content = Files.readAllBytes(
        Path.of(Objects.requireNonNull(getClass().getResource("/gelimages1.png")).toURI()));
    assertArrayEquals(file2Content, output.toByteArray());
  }

  @Test
  public void localeChange() {
    String content = "<div id=\"test-div\">test content</div>";
    when(service.print(any(), any())).thenReturn(content);
    Submission submission = repository.findById(164L).orElseThrow();
    component.setSubmission(submission);
    Locale locale = FRENCH;
    String frenchcontent = "<div id=\"test-div\">test de contenu</div>";
    when(service.print(any(), eq(locale))).thenReturn(frenchcontent);
    UI.getCurrent().setLocale(locale);

    verify(service).print(submission, locale);
    Html html = findChild(component, Html.class).orElseThrow();
    assertEquals(new Html(frenchcontent).getElement().getOuterHTML(),
        html.getElement().getOuterHTML());
  }
}
