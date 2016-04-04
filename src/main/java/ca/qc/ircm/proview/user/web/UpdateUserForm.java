package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.laboratory.web.LaboratoryForm;
import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

/**
 * User view.
 */
public class UpdateUserForm extends UpdateUserFormDesign implements MessageResourcesComponent {
  private static final long serialVersionUID = -4585597583437283309L;

  private UpdateUserFormPresenter presenter;

  public void setPresenter(UpdateUserFormPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.attach();
  }

  public UserForm getUserForm() {
    return userForm;
  }

  public LaboratoryForm getLaboratoryForm() {
    return laboratoryForm;
  }

  public Label getAddressesHeader() {
    return addressesHeader;
  }

  public Button getToggleAddressesButton() {
    return toggleAddressesButton;
  }

  public AddressForm getAddressForm() {
    return addressForm;
  }

  public Button getAddAddressButton() {
    return addAddressButton;
  }

  public Label getPhoneNumbersHeader() {
    return phoneNumbersHeader;
  }

  public Button getTogglePhoneNumbersButton() {
    return togglePhoneNumbersButton;
  }

  public PhoneNumberForm getPhoneNumberForm() {
    return phoneNumberForm;
  }

  public Button getAddPhoneNumberButton() {
    return addPhoneNumberButton;
  }

  public Button getSaveButton() {
    return saveButton;
  }

  public Button getCancelButton() {
    return cancelButton;
  }
}
