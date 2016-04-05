package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.laboratory.web.LaboratoryForm;
import ca.qc.ircm.proview.laboratory.web.LaboratoryFormPresenter;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.QAddress;
import ca.qc.ircm.proview.user.QUser;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.utils.web.VaadinUtils;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.vaadin.teemu.VaadinIcons;

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
  private ObjectProperty<Boolean> addressesVisibleProperty = new ObjectProperty<>(false);
  private UpdateUserForm view;
  private UserForm userForm;
  private LaboratoryForm laboratoryForm;
  private Label addressesHeader;
  private Button addressesVisibleButton;
  private Grid addressesGrid;
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
  private LaboratoryFormPresenter laboratoryFormPresenter;
  @Inject
  private PhoneNumberFormPresenter phoneNumberPresenter;
  @Inject
  private UserService userService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private Provider<AddressWindow> addressWindowProvider;
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
    initGrids();
    addFieldListeners();
    userFormPresenter.init(userForm);
    laboratoryFormPresenter.init(laboratoryForm);
    phoneNumberPresenter.init(phoneNumberForm);
  }

  private void setFields() {
    userForm = view.getUserForm();
    laboratoryForm = view.getLaboratoryForm();
    addressesHeader = view.getAddressesHeader();
    addressesVisibleButton = view.getAddressesVisibleButton();
    addressesGrid = view.getAddressesGrid();
    addAddressButton = view.getAddAddressButton();
    phoneNumbersHeader = view.getPhoneNumbersHeader();
    togglePhoneNumbersButton = view.getTogglePhoneNumbersButton();
    phoneNumberForm = view.getPhoneNumberForm();
    addPhoneNumberButton = view.getAddPhoneNumberButton();
    saveButton = view.getSaveButton();
    cancelButton = view.getCancelButton();
  }

  private void initGrids() {
    addressesGrid.setColumns(ADDRESS_COLUMNS);
  }

  private void addFieldListeners() {
    addressesVisibleProperty.addValueChangeListener(e -> setAddressesVisibility());
    addressesVisibleButton.addClickListener(
        e -> addressesVisibleProperty.setValue(!addressesVisibleProperty.getValue()));
    addressesGrid.addItemClickListener(e -> {
      if (e.isDoubleClick()) {
        editAddress(e.getItem(), (Address) e.getItemId());
      }
    });
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setCaptions();
    addValidators();
    setAddressesVisibility();
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    addressesHeader.setValue(resources.message("addressesHeader"));
    MessageResource addressResources = view.getResources(Address.class);
    for (Column column : addressesGrid.getColumns()) {
      column.setHeaderCaption(addressResources.message((String) column.getPropertyId()));
    }
    addAddressButton.setCaption(resources.message("addAddress"));
    phoneNumbersHeader.setValue(resources.message("phoneNumbersHeader"));
    addPhoneNumberButton.setCaption(resources.message("addPhoneNumber"));
    saveButton.setCaption(resources.message("save"));
    cancelButton.setCaption(resources.message("cancel"));
  }

  private void addValidators() {
    // TODO Program method.
  }

  private void setAddressesVisibility() {
    boolean visible = addressesVisibleProperty.getValue();
    addressesGrid.setVisible(visible);
    addAddressButton.setVisible(visible);
    MessageResource resources = view.getResources();
    addressesVisibleButton.setIcon(
        visible ? VaadinIcons.CHEVRON_CIRCLE_UP : VaadinIcons.CHEVRON_CIRCLE_DOWN,
        resources.message("addressesVisible." + visible));
  }

  private void editAddress(Item item, Address address) {
    final MessageResource resources = view.getResources();
    AddressWindow window = addressWindowProvider.get();
    window.center();
    window.setModal(true);
    window.setCaption(resources.message("addAddress.title"));
    AddressFormPresenter presenter = window.getPresenter();
    presenter.setItemDataSource(item);
    presenter.addSaveClickListener(e -> saveAddress(window, presenter, address));
    view.showWindow(window);
  }

  private void saveAddress(Window window, AddressFormPresenter presenter, Address address) {
    try {
      presenter.commit();
      userService.update(user, null);
      window.close();
      final MessageResource resources = view.getResources();
      view.afterSuccessfulUpdate(resources.message("addAddress.done", address.getAddress()));
    } catch (CommitException e) {
      String message = vaadinUtils.getFieldMessage(e, view.getLocale());
      logger.debug("Validation failed with message {}", message);
      view.showError(message);
    }
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
    List<Address> addresses = new ArrayList<>();
    addresses.add(user.getAddress());
    addressesGrid.setContainerDataSource(new BeanItemContainer<>(Address.class, addresses));
    List<PhoneNumber> phoneNumbers = user.getPhoneNumbers();
    if (phoneNumbers == null || phoneNumbers.isEmpty()) {
      phoneNumbers = new ArrayList<>();
      phoneNumbers.add(new PhoneNumber());
      user.setPhoneNumbers(phoneNumbers);
    }
    phoneNumberPresenter.setItemDataSource(new BeanItem<>(phoneNumbers.get(0), PhoneNumber.class));
  }
}
