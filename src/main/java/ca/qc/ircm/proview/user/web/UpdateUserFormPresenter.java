package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.laboratory.web.LaboratoryForm;
import ca.qc.ircm.proview.laboratory.web.LaboratoryFormPresenter;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.QAddress;
import ca.qc.ircm.proview.user.QUser;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.utils.web.VaadinUtils;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * User form.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateUserFormPresenter {
  public static final String EMAIL_PROPERTY = QUser.user.email.getMetadata().getName();
  public static final String NAME_PROPERTY = QUser.user.name.getMetadata().getName();
  public static final String LABORATORY_PROPERTY = QUser.user.laboratory.getMetadata().getName();
  public static final String ADDRESSES_PROPERTY = QUser.user.address.getMetadata().getName();
  public static final String PHONE_NUMBERS_PROPERTY =
      QUser.user.phoneNumbers.getMetadata().getName();
  public static final String ADDRESS_PROPERTY = QAddress.address1.address.getMetadata().getName();
  public static final String ADDRESS_SECOND_PROPERTY =
      QAddress.address1.addressSecond.getMetadata().getName();
  public static final String TOWN_PROPERTY = QAddress.address1.town.getMetadata().getName();
  public static final String STATE_PROPERTY = QAddress.address1.state.getMetadata().getName();
  public static final String COUNTRY_PROPERTY = QAddress.address1.country.getMetadata().getName();
  public static final String POSTAL_CODE_PROPERTY =
      QAddress.address1.postalCode.getMetadata().getName();
  public static final Object[] ADDRESS_COLUMNS =
      new Object[] { ADDRESS_PROPERTY, ADDRESS_SECOND_PROPERTY, TOWN_PROPERTY, STATE_PROPERTY,
          COUNTRY_PROPERTY, POSTAL_CODE_PROPERTY };
  private static final Logger logger = LoggerFactory.getLogger(UpdateUserFormPresenter.class);
  private User user = new User();
  private UpdateUserForm view;
  private Panel userPanel;
  private UserForm userForm;
  private Panel laboratoryPanel;
  private LaboratoryForm laboratoryForm;
  private Panel addressPanel;
  private AddressForm addressForm;
  private Panel phoneNumbersPanel;
  private VerticalLayout phoneNumbersLayout;
  private Button addPhoneNumberButton;
  private Button saveButton;
  private Button cancelButton;
  @Inject
  private UserFormPresenter userFormPresenter;
  @Inject
  private LaboratoryFormPresenter laboratoryFormPresenter;
  @Inject
  private AddressFormPresenter addressFormPresenter;
  @Inject
  private Provider<PhoneNumberFormPresenter> phoneNumberPresenterProvider;
  @Inject
  private UserService userService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private VaadinUtils vaadinUtils;

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
    addFieldListeners();
    userFormPresenter.init(userForm);
    laboratoryFormPresenter.init(laboratoryForm);
    addressFormPresenter.init(addressForm);
  }

  private void setFields() {
    userPanel = view.getUserPanel();
    userForm = view.getUserForm();
    laboratoryPanel = view.getLaboratoryPanel();
    laboratoryForm = view.getLaboratoryForm();
    addressPanel = view.getAddressPanel();
    addressForm = view.getAddressForm();
    phoneNumbersPanel = view.getPhoneNumbersPanel();
    phoneNumbersLayout = view.getPhoneNumbersLayout();
    addPhoneNumberButton = view.getAddPhoneNumberButton();
    saveButton = view.getSaveButton();
    cancelButton = view.getCancelButton();
  }

  private void addFieldListeners() {
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setCaptions();
    addValidators();
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    userPanel.setCaption(resources.message("userHeader"));
    laboratoryPanel.setCaption(resources.message("laboratoryHeader"));
    addressPanel.setCaption(resources.message("addressHeader"));
    phoneNumbersPanel.setCaption(resources.message("phoneNumbersHeader"));
    addPhoneNumberButton.setCaption(resources.message("addPhoneNumber"));
    saveButton.setCaption(resources.message("save"));
    cancelButton.setCaption(resources.message("cancel"));
  }

  private void addValidators() {
    // TODO Program method.
  }

  /**
   * Sets user.
   *
   * @param user
   *          user
   */
  public void setUser(User user) {
    this.user = user;
    userFormPresenter.setItemDataSource(new BeanItem<>(user, User.class));
    laboratoryFormPresenter
        .setItemDataSource(new BeanItem<>(user.getLaboratory(), Laboratory.class));
    addressFormPresenter.setItemDataSource(new BeanItem<>(user.getAddress(), Address.class));
    phoneNumbersLayout.removeAllComponents();
    List<PhoneNumber> phoneNumbers = user.getPhoneNumbers();
    if (phoneNumbers == null || phoneNumbers.isEmpty()) {
      phoneNumbers = new ArrayList<>();
      PhoneNumber empty = new PhoneNumber();
      empty.setType(PhoneNumberType.WORK);
      phoneNumbers.add(empty);
      user.setPhoneNumbers(phoneNumbers);
    }
    for (PhoneNumber phoneNumber : phoneNumbers) {
      PhoneNumberForm form = new PhoneNumberForm();
      PhoneNumberFormPresenter presenter = phoneNumberPresenterProvider.get();
      presenter.init(form);
      phoneNumbersLayout.addComponent(form);
      presenter.setItemDataSource(new BeanItem<>(phoneNumber, PhoneNumber.class));
    }
  }
}
