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

import static ca.qc.ircm.proview.web.CloseWindowOnViewChange.closeWindowOnViewChange;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.StreamResource;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Windows that shows submission.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionWindowPresenter {
  public static final String WINDOW_STYLE = "submission-window";
  public static final String TITLE = "title";
  public static final String UPDATE = "update";
  public static final String PRINT = "print";
  public static final String PRINT_FILENAME = "submission-print-%s.html";
  public static final String PRINT_MIME = "text/html";
  private static final Logger logger = LoggerFactory.getLogger(SubmissionWindowPresenter.class);
  private SubmissionWindow window = new SubmissionWindow();
  private SubmissionWindowDesign design = new SubmissionWindowDesign();
  @Inject
  private SubmissionService submissionService;
  @Inject
  private AuthorizationService authorizationService;

  protected SubmissionWindowPresenter() {
  }

  /**
   * Initializes presenter.
   *
   * @param window
   *          window
   */
  public void init(SubmissionWindow window) {
    this.window = window;
    design = window.design;
    prepareComponents();
  }

  private void prepareComponents() {
    closeWindowOnViewChange(window);
    final MessageResource resources = window.getResources();
    window.addStyleName(WINDOW_STYLE);
    window.setHeight("700px");
    window.setWidth("1200px");
    design.update.addStyleName(UPDATE);
    design.update.setCaption(resources.message(UPDATE));
    design.print.addStyleName(PRINT);
    design.print.setCaption(resources.message(PRINT));
    window.submissionForm.setReadOnly(true);
  }

  private void preparePrint(Submission submission) {
    final Locale locale = window.getLocale();
    String content = submissionService.print(submission, locale);
    String filename = String.format(PRINT_FILENAME, submission != null ? submission.getId() : "");
    new ArrayList<>(design.print.getExtensions()).stream().forEach(ext -> ext.remove());
    StreamResource printResource = new StreamResource(
        () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), filename);
    printResource.setMIMEType(PRINT_MIME);
    printResource.setCacheTime(0);
    BrowserWindowOpener opener = new BrowserWindowOpener(printResource);
    opener.extend(design.print);
  }

  void setValue(Submission submission) {
    logger.debug("Submission window for submission {}", submission);
    MessageResource resources = window.getResources();
    window.setCaption(resources.message(TITLE, submission.getExperiment()));
    design.update.setVisible(authorizationService.hasSubmissionWritePermission(submission));
    design.update.addClickListener(e -> {
      window.navigateTo(SubmissionView.VIEW_NAME, String.valueOf(submission.getId()));
      window.close();
    });
    preparePrint(submission);
    window.submissionForm.setValue(submission);
  }
}
