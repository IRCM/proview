package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.ui.Notification;
import org.vaadin.hene.flexibleoptiongroup.FlexibleOptionGroup;

/**
 * Submission form.
 */
public class SubmissionForm extends SubmissionFormDesign implements MessageResourcesComponent {
  private static final long serialVersionUID = 7586918222688019429L;
  private SubmissionFormPresenter presenter;
  protected FlexibleOptionGroup digestionFlexibleOptions = new FlexibleOptionGroup();
  protected FlexibleOptionGroup proteinIdentificationFlexibleOptions = new FlexibleOptionGroup();

  public void setPresenter(SubmissionFormPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public void showError(String message) {
    Notification.show(message, Notification.Type.ERROR_MESSAGE);
  }
}
