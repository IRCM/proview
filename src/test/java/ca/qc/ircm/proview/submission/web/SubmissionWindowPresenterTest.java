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

import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PRINT_FILENAME;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PRINT_MIME;
import static ca.qc.ircm.proview.submission.web.SubmissionWindowPresenter.PRINT;
import static ca.qc.ircm.proview.submission.web.SubmissionWindowPresenter.TITLE;
import static ca.qc.ircm.proview.submission.web.SubmissionWindowPresenter.UPDATE;
import static ca.qc.ircm.proview.submission.web.SubmissionWindowPresenter.WINDOW_STYLE;
import static ca.qc.ircm.proview.test.utils.SearchUtils.findInstanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.AbstractComponentTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.CloseWindowOnViewChange.CloseWindowOnViewChangeListener;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.StreamResource;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionWindowPresenterTest extends AbstractComponentTestCase {
  @Inject
  private SubmissionWindowPresenter presenter;
  @Inject
  private SubmissionRepository repository;
  @MockBean
  private SubmissionService submissionService;
  @MockBean
  private AuthorizationService authorizationService;
  @Mock
  private SubmissionWindow window;
  @Mock
  private SubmissionForm submissionForm;
  private SubmissionWindowDesign design = new SubmissionWindowDesign();
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SubmissionWindow.class, locale);
  private Submission submission;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    window.submissionForm = submissionForm;
    window.design = design;
    when(window.getLocale()).thenReturn(locale);
    when(window.getResources()).thenReturn(resources);
    when(window.getUI()).thenReturn(ui);
    when(ui.getNavigator()).thenReturn(navigator);
    when(submissionService.print(any(), any())).thenReturn("");
    submission = repository.findById(1L).orElse(null);
  }

  @Test
  public void styles() {
    presenter.init(window);
    presenter.setValue(submission);

    verify(window).addStyleName(WINDOW_STYLE);
    assertTrue(design.update.getStyleName().contains(UPDATE));
    assertTrue(design.print.getStyleName().contains(PRINT));
  }

  @Test
  public void captions() {
    presenter.init(window);
    presenter.setValue(submission);

    verify(window).setCaption(resources.message(TITLE, submission.getExperiment()));
    assertEquals(resources.message(UPDATE), design.update.getCaption());
    assertEquals(resources.message(PRINT), design.print.getCaption());
  }

  @Test
  public void closeWindowOnClose() {
    presenter.init(window);
    presenter.setValue(submission);

    verify(navigator).addViewChangeListener(any(CloseWindowOnViewChangeListener.class));
  }

  @Test
  public void update() {
    when(authorizationService.hasPermission(submission, BasePermission.WRITE)).thenReturn(true);
    presenter.init(window);
    presenter.setValue(submission);

    design.update.click();

    verify(window).navigateTo(SubmissionView.VIEW_NAME, String.valueOf(submission.getId()));
    verify(window).close();
  }

  @Test
  public void update_VisibilityFalse() {
    presenter.init(window);
    presenter.setValue(submission);

    assertFalse(design.update.isVisible());
  }

  @Test
  public void update_VisibilityTrue() {
    when(authorizationService.hasPermission(submission, BasePermission.WRITE)).thenReturn(true);
    presenter.init(window);
    presenter.setValue(submission);

    assertTrue(design.update.isVisible());
  }

  @Test
  public void print() throws Throwable {
    presenter.init(window);
    String content = RandomStringUtils.randomAlphanumeric(1000);
    when(submissionService.print(any(), any())).thenReturn(content);
    presenter.setValue(submission);

    Optional<BrowserWindowOpener> optionalBrowserWindowOpener =
        findInstanceOf(design.print.getExtensions(), BrowserWindowOpener.class);
    assertTrue(optionalBrowserWindowOpener.isPresent());
    BrowserWindowOpener browserWindowOpener = optionalBrowserWindowOpener.get();
    assertTrue(browserWindowOpener.getResource() instanceof StreamResource);
    StreamResource resource = (StreamResource) browserWindowOpener.getResource();
    assertEquals(PRINT_MIME, resource.getMIMEType());
    assertEquals(0, resource.getCacheTime());
    assertEquals(String.format(PRINT_FILENAME, submission.getId()), resource.getFilename());
    ByteArrayOutputStream actualOutput = new ByteArrayOutputStream();
    IOUtils.copy(resource.getStream().getStream(), actualOutput);
    assertEquals(content, new String(actualOutput.toByteArray(), StandardCharsets.UTF_8));
  }
}
