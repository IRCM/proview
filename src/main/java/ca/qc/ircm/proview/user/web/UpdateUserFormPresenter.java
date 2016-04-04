package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.laboratory.web.LaboratoryForm;
import ca.qc.ircm.proview.laboratory.web.LaboratoryFormPresenter;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.QUser;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * User form.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateUserFormPresenter {
  public static final String EMAIL_PROPERTY = QUser.user.email.getMetadata().getName();
  public static final String NAME_PROPERTY = QUser.user.name.getMetadata().getName();
  public static final String LABORATORY_PROPERTY = QUser.user.laboratory.getMetadata().getName();
  public static final String ADDRESSES_PROPERTY = QUser.user.addresses.getMetadata().getName();
  public static final String PHONE_NUMBERS_PROPERTY =
      QUser.user.phoneNumbers.getMetadata().getName();
  private UpdateUserForm view;
  private UserForm userForm;
  private LaboratoryForm laboratoryForm;
  private Label addressesHeader;
  private Button toggleAddressesButton;
  private AddressForm addressForm;
  private Button addAddressButton;
  private Label phoneNumbersHeader;
  private Button togglePhoneNumbersButton;
  private PhoneNumberForm phoneNumberForm;
  private Button addPhoneNumberButton;
  private Button saveButton;
  private Button cancelButton;
  @Inject
  private UserFormPresenter userFormPresenter;
  @Inject
  private LaboratoryFormPresenter laboratoryPresenter;
  @Inject
  private AddressFormPresenter addressPresenter;
  @Inject
  private PhoneNumberFormPresenter phoneNumberPresenter;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(UpdateUserForm view) {
    this.view = view;
    view.setPresenter(this);
    setFields();
    userFormPresenter.init(userForm);
    laboratoryPresenter.init(laboratoryForm);
    addressPresenter.init(addressForm);
    phoneNumberPresenter.init(phoneNumberForm);
    updateEditable();
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setCaptions();
    addValidators();
  }

  private void setFields() {
    userForm = view.getUserForm();
    laboratoryForm = view.getLaboratoryForm();
    addressesHeader = view.getAddressesHeader();
    toggleAddressesButton = view.getToggleAddressesButton();
    addressForm = view.getAddressForm();
    addAddressButton = view.getAddAddressButton();
    phoneNumbersHeader = view.getPhoneNumbersHeader();
    togglePhoneNumbersButton = view.getTogglePhoneNumbersButton();
    phoneNumberForm = view.getPhoneNumberForm();
    addPhoneNumberButton = view.getAddPhoneNumberButton();
    saveButton = view.getSaveButton();
    cancelButton = view.getCancelButton();
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    addressesHeader.setValue(resources.message("addressesHeader"));
    addAddressButton.setCaption(resources.message("addAddress"));
    phoneNumbersHeader.setValue(resources.message("phoneNumbersHeader"));
    addPhoneNumberButton.setCaption(resources.message("addPhoneNumber"));
    saveButton.setCaption(resources.message("save"));
    cancelButton.setCaption(resources.message("cancel"));
  }

  private void addValidators() {
    // TODO Program method.
  }

  private void updateEditable() {
    /*
    boolean editable = editableProperty.getValue();
    emailField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    emailField.setReadOnly(!editable);
    nameField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    nameField.setReadOnly(!editable);
    passwordField.setVisible(editable);
    confirmPasswordField.setVisible(editable);
    laboratoryForm.getOrganizationField()
        .setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    laboratoryForm.getOrganizationField().setReadOnly(!editable);
    laboratoryForm.getNameField().setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    laboratoryForm.getNameField().setReadOnly(!editable);
    addressForm.getAddressField().setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    addressForm.getAddressField().setReadOnly(!editable);
    addressForm.getAddressSecondField()
        .setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    addressForm.getAddressSecondField().setReadOnly(!editable);
    addressForm.getTownField().setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    addressForm.getTownField().setReadOnly(!editable);
    addressForm.getStateField().setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    addressForm.getStateField().setReadOnly(!editable);
    addressForm.getCountryField().setStyleName(editable ? "" : ValoTheme.COMBOBOX_BORDERLESS);
    addressForm.getCountryField().setReadOnly(!editable);
    addressForm.getPostalCodeField().setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    addressForm.getPostalCodeField().setReadOnly(!editable);
    phoneNumberForm.getTypeField().setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    phoneNumberForm.getTypeField().setReadOnly(!editable);
    phoneNumberForm.getNumberField().setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    phoneNumberForm.getNumberField().setReadOnly(!editable);
    phoneNumberForm.getExtensionField()
        .setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    phoneNumberForm.getExtensionField().setReadOnly(!editable);
    */
  }

  public void setUser(User user) {
    userFormPresenter.setItemDataSource(new BeanItem<>(user, User.class));
    laboratoryPresenter.setItemDataSource(new BeanItem<>(user.getLaboratory(), Laboratory.class));
    List<Address> addresses = user.getAddresses();
    if (addresses == null || addresses.isEmpty()) {
      addresses = new ArrayList<>();
      addresses.add(new Address());
      user.setAddresses(addresses);
    }
    addressPresenter.setItemDataSource(new BeanItem<>(addresses.get(0), Address.class));
    List<PhoneNumber> phoneNumbers = user.getPhoneNumbers();
    if (phoneNumbers == null || phoneNumbers.isEmpty()) {
      phoneNumbers = new ArrayList<>();
      phoneNumbers.add(new PhoneNumber());
      user.setPhoneNumbers(phoneNumbers);
    }
    phoneNumberPresenter.setItemDataSource(new BeanItem<>(phoneNumbers.get(0), PhoneNumber.class));
  }
}