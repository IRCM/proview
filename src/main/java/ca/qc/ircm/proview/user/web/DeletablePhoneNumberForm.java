package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.ui.Button;

/**
 * Deletable phone number form.
 */
public class DeletablePhoneNumberForm extends DeletablePhoneNumberFormDesign
    implements MessageResourcesComponent {
  private static final long serialVersionUID = -2276634153004414957L;
  private DeletablePhoneNumberFormPresenter presenter;

  public void setPresenter(DeletablePhoneNumberFormPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.attach();
    removeComponent(buttonsLayout);
    phoneNumberForm.addComponent(buttonsLayout);
  }

  public PhoneNumberForm getPhoneNumberForm() {
    return phoneNumberForm;
  }

  public Button getDeleteButton() {
    return deleteButton;
  }
}
