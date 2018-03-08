package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
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
  private Logger logger = LoggerFactory.getLogger(PrintSubmissionController.class);
  @Inject
  private SubmissionService submissionService;
  @Inject
  private AuthorizationService authorizationService;

  @GetMapping("/submission/print/{id}")
  public String submission(@PathVariable(name = "id", required = false) Long id, Model model) {
    logger.debug("Print submission {}", id);
    Locale locale = Locale.CANADA;
    if (authorizationService.getCurrentUser() != null) {
      locale = authorizationService.getCurrentUser().getLocale();
    }
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
}
