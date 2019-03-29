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

import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;

import ca.qc.ircm.proview.files.web.GuidelinesWindow;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.web.HelpWindow;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import javax.inject.Inject;
import javax.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Submission view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER_STYLE = "header";
  public static final String HELP = "help";
  public static final String SAMPLE_TYPE_WARNING = "sampleTypeWarning";
  public static final String INACTIVE_WARNING = "inactive";
  public static final String GUIDELINES = "guidelines";
  public static final String SUBMISSION = "submission";
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
  private Provider<GuidelinesWindow> guidelinesWindowProvider;
  @Inject
  private Provider<HelpWindow> helpWindowProvider;
  @Value("${spring.application.name}")
  private String applicationName;

  protected SubmissionViewPresenter() {
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
    design.sampleTypeWarning.addStyleName(SAMPLE_TYPE_WARNING);
    design.sampleTypeWarning.setValue(resources.message(SAMPLE_TYPE_WARNING));
    design.inactiveWarning.addStyleName(INACTIVE_WARNING);
    design.inactiveWarning.setValue(resources.message(INACTIVE_WARNING));
    design.guidelines.addStyleName(GUIDELINES);
    design.guidelines.setCaption(resources.message(GUIDELINES));
    design.guidelines.addClickListener(e -> {
      GuidelinesWindow guidelinesWindow = guidelinesWindowProvider.get();
      guidelinesWindow.center();
      view.addWindow(guidelinesWindow);
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
