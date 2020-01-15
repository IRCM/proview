package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.web.HistoryView.VIEW_ERROR;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.treatment.Treatment;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * History view presenter.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HistoryViewPresenter {
  private static final Logger logger = LoggerFactory.getLogger(HistoryViewPresenter.class);
  private HistoryView view;
  private ActivityService service;
  private SubmissionService submissionService;
  private Submission submission;

  protected HistoryViewPresenter(ActivityService service, SubmissionService submissionService) {
    this.service = service;
    this.submissionService = submissionService;
  }

  void init(HistoryView view) {
    this.view = view;
  }

  String description(Activity activity, Locale locale) {
    return service.description(activity, locale);
  }

  void view(Activity activity, Locale locale) {
    Object record = service.record(activity);
    if (record instanceof SubmissionSample) {
      record = ((SubmissionSample) record).getSubmission();
    }
    if (record instanceof Submission) {
      view.dialog.setSubmission((Submission) record);
      view.dialog.open();
      view.dialog.addSavedListener(e -> updateActivities());
    } else if (record instanceof MsAnalysis) {
      // TODO Program dialog for MS analysis.
      AppResources resources = new AppResources(HistoryView.class, locale);
      view.showNotification(resources.message(VIEW_ERROR, record.getClass().getSimpleName()));
    } else if (record instanceof Treatment) {
      // TODO Program dialog for treatment.
      AppResources resources = new AppResources(HistoryView.class, locale);
      view.showNotification(resources.message(VIEW_ERROR, record.getClass().getSimpleName()));
    } else {
      AppResources resources = new AppResources(HistoryView.class, locale);
      view.showNotification(resources.message(VIEW_ERROR, record.getClass().getSimpleName()));
    }
    logger.trace("view activity {}", activity);
  }

  private void updateActivities() {
    if (submission != null) {
      view.activities.setItems(service.all(submission));
    }
  }

  Submission getSubmission() {
    return submission;
  }

  public void setParameter(Long parameter) {
    if (parameter != null) {
      submission = submissionService.get(parameter);
      updateActivities();
    }
  }
}
