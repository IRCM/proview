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

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Locale;

import javax.inject.Inject;

@Controller
public class PrintSubmissionController {
  public static final String PRINT_SUBMISSION_URL = "/submission/print/{id}";
  private Logger logger = LoggerFactory.getLogger(PrintSubmissionController.class);
  @Inject
  private SubmissionService submissionService;

  protected PrintSubmissionController() {
  }

  protected PrintSubmissionController(SubmissionService submissionService) {
    this.submissionService = submissionService;
  }

  @GetMapping(PRINT_SUBMISSION_URL)
  public String printSubmission(@PathVariable(name = "id", required = false) Long id, Model model,
      Locale locale) {
    logger.debug("Print submission {} {}", id, locale);
    Submission submission = submissionService.get(id);
    model.addAttribute("locale", locale);
    model.addAttribute("submission", submission);
    if (submission != null) {
      model.addAttribute("user", submission.getUser());
      model.addAttribute("laboratory", submission.getLaboratory());
      if (submission.getSamples() != null && !submission.getSamples().isEmpty()) {
        SubmissionSample sample = submission.getSamples().get(0);
        model.addAttribute("sample", sample);
        if (sample.getOriginalContainer().getType() == SampleContainerType.WELL) {
          Plate plate = ((Well) sample.getOriginalContainer()).getPlate();
          model.addAttribute("plate", plate);
        }
      }
    }
    return "submission-print";
  }

  public static String printSubmissionUrl(Submission submission, Locale locale) {
    String url = PRINT_SUBMISSION_URL.replace("/{id}",
        submission != null && submission.getId() != null ? "/" + submission.getId() : "");
    return url + (locale != null ? "?lang=" + locale.getLanguage() : "");
  }
}
