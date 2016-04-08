package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

/**
 * Add phone number form.
 */
public class AddPhoneNumberForm extends AddPhoneNumberFormDesign
    implements MessageResourcesComponent {
  private static final long serialVersionUID = 6629591211365105609L;
  private AddPhoneNumberFormPresenter presenter;

  public void setPresenter(AddPhoneNumberFormPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.attach();
  }

  public Label getHeader() {
    return header;
  }

  public PhoneNumberForm getPhoneNumberForm() {
    return phoneNumberForm;
  }

  public Button getSaveButton() {
    return saveButton;
  }

  public Button getCancelButton() {
    return cancelButton;
  }
}
