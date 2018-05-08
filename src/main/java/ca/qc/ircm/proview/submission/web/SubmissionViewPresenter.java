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

import static ca.qc.ircm.proview.persistence.QueryDsl.qname;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.web.HelpWindow;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Submission view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER_STYLE = "header";
  public static final String HELP = "help";
  public static final String SUBMISSION = qname(submission);
  public static final String SUBMISSION_DESCRIPTION = property(SUBMISSION, "description");
  public static final String INVALID_SUBMISSION = property("submission", "invalid");
  private static final Logger logger = LoggerFactory.getLogger(SubmissionViewPresenter.class);
  private SubmissionView view;
  private SubmissionViewDesign design;
  @Inject
  private SubmissionService submissionService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private Provider<HelpWindow> helpWindowProvider;
  @Value("${spring.application.name}")
  private String applicationName;

  protected SubmissionViewPresenter() {
  }

  protected SubmissionViewPresenter(SubmissionService submissionService,
      AuthorizationService authorizationService, Provider<HelpWindow> helpWindowProvider,
      String applicationName) {
    this.submissionService = submissionService;
    this.authorizationService = authorizationService;
    this.helpWindowProvider = helpWindowProvider;
    this.applicationName = applicationName;
  }

  /**
   * Initialize presenter.
   *
   * @param view
   *          view
   */
  public void init(SubmissionView view) {
    logger.debug("Submission view");
    this.view = view;
    design = view.design;
    prepareComponents();
    view.submissionForm.setReadOnly(false);
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.headerLabel.addStyleName(HEADER_STYLE);
    design.headerLabel.setValue(resources.message(HEADER_STYLE));
    design.help.addStyleName(HELP);
    design.help.setCaption(resources.message(HELP));
    design.help.addClickListener(e -> {
      HelpWindow helpWindow = helpWindowProvider.get();
      helpWindow.setHelp(resources.message(SUBMISSION_DESCRIPTION, VaadinIcons.MENU.getHtml()),
          ContentMode.HTML);
      view.addWindow(helpWindow);
    });
  }

  /**
   * Called when view is entered.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    if (parameters != null && !parameters.isEmpty()) {
      try {
        Long id = Long.valueOf(parameters);
        logger.debug("Set submission {}", id);
        Submission submission = submissionService.get(id);
        view.submissionForm.setValue(submission);
        view.submissionForm
            .setReadOnly(!authorizationService.hasSubmissionWritePermission(submission));
      } catch (NumberFormatException e) {
        view.showWarning(view.getResources().message(INVALID_SUBMISSION));
      }
    }
  }
}
