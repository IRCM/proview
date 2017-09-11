package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.utils.MessageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * Sample view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SampleViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String INVALID_SAMPLE = "sample.invalid";
  private static final Logger logger = LoggerFactory.getLogger(SampleViewPresenter.class);
  private SampleView view;
  @Inject
  private SampleService sampleService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected SampleViewPresenter() {
  }

  protected SampleViewPresenter(SampleService sampleService, String applicationName) {
    this.sampleService = sampleService;
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(SampleView view) {
    logger.debug("Sample view");
    this.view = view;
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
  }

  private boolean valid(String parameters) {
    boolean valid = true;
    try {
      Long id = Long.valueOf(parameters);
      if (sampleService.get(id) == null) {
        valid = false;
      }
    } catch (NumberFormatException e) {
      valid = false;
    }
    return valid;
  }

  /**
   * Called by view when entered.
   *
   * @param parameters
   *          parameters
   */
  public void enter(String parameters) {
    if (valid(parameters)) {
      Long id = Long.valueOf(parameters);
      Sample sample = sampleService.get(id);
      if (sample instanceof Control) {
        view.navigateTo(ControlView.VIEW_NAME, String.valueOf(sample.getId()));
      } else if (sample instanceof SubmissionSample) {
        view.navigateTo(SubmissionView.VIEW_NAME,
            String.valueOf(((SubmissionSample) sample).getSubmission().getId()));
      } else {
        view.showWarning(view.getResources().message(INVALID_SAMPLE));
      }
    } else {
      view.showWarning(view.getResources().message(INVALID_SAMPLE));
    }
  }
}
