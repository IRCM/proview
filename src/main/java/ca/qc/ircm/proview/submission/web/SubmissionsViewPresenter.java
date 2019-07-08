package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.submission.Submission;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submissions view.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionsViewPresenter {
  public void init(SubmissionsView view) {
  }

  public void view(Submission submission) {
  }

  public void filterExperiment(String experiment) {
  }

  public void filterUser(String string) {
  }

  public void filterDirector(String string) {
  }

  public void add() {
  }
}
