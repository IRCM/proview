package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.laboratory.web.LaboratoryForm;
import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

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

  public void showError(String message) {
    Notification.show(message, Notification.Type.ERROR_MESSAGE);
  }

  public void showWindow(Window window) {
    getUI().addWindow(window);
  }

  public void afterSuccessfulUpdate(String message) {
    Notification.show(message, Notification.Type.TRAY_NOTIFICATION);
  }

  public Panel getUserPanel() {
    return userPanel;
  }

  public UserForm getUserForm() {
    return userForm;
  }

  public Panel getLaboratoryPanel() {
    return laboratoryPanel;
  }

  public LaboratoryForm getLaboratoryForm() {
    return laboratoryForm;
  }

  public Panel getAddressPanel() {
    return addressPanel;
  }

  public AddressForm getAddressForm() {
    return addressForm;
  }

  public Panel getPhoneNumbersPanel() {
    return phoneNumbersPanel;
  }

  public VerticalLayout getPhoneNumbersLayout() {
    return phoneNumbersLayout;
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
