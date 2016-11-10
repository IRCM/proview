package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Windows that shows submission.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionWindow extends Window implements MessageResourcesComponent {
  public static final String WINDOW_STYLE = "submission-window";
  private static final long serialVersionUID = 4789125002422549258L;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionWindow.class);
  private SubmissionForm view = new SubmissionForm();
  private Panel panel;
  private Submission submission;
  @Inject
  private SubmissionFormPresenter presenter;

  @PostConstruct
  protected void init() {
    view.setPresenter(presenter);
    addStyleName(WINDOW_STYLE);
    panel = new Panel();
    setContent(panel);
    panel.setContent(view);
    view.setMargin(true);
    setHeight("700px");
    setWidth("1200px");
    panel.setSizeFull();
  }

  @Override
  public void attach() {
    super.attach();
    logger.debug("Submission window for submission {}", submission);
    setCaption(getResources().message("title", submission.getExperience()));
    presenter.setItemDataSource(new BeanItem<>(submission));
  }

  public void setSubmission(Submission submission) {
    this.submission = submission;
  }
}
