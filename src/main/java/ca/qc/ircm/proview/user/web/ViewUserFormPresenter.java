/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.laboratory.LaboratoryService;
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
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
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
public class ViewUserFormPresenter {
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
  private static final Logger logger = LoggerFactory.getLogger(ViewUserFormPresenter.class);
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private ObjectProperty<String> passwordProperty = new ObjectProperty<>(null, String.class);
  private PropertysetItem passwordItem = new PropertysetItem();
  private List<DeletablePhoneNumberFormPresenter> phoneNumberFormPresenters = new ArrayList<>();
  private User user = new User();
  private ViewUserForm view;
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
  private Provider<DeletablePhoneNumberFormPresenter> deletablePhoneNumberPresenterProvider;
  @Inject
  private Provider<AddPhoneNumberWindow> addPhoneNumberWindowProvider;
  @Inject
  private UserService userService;
  @Inject
  private LaboratoryService laboratoryService;
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
  public void init(ViewUserForm view) {
    this.view = view;
    view.setPresenter(this);
    setFields();
    userFormPresenter.init(userForm);
    laboratoryFormPresenter.init(laboratoryForm);
    addressFormPresenter.init(addressForm);
    bindFields();
    addFieldListeners();
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

  private void bindFields() {
    passwordItem.addItemProperty(UserFormPresenter.PASSWORD_PROPERTY, passwordProperty);
    passwordItem.addItemProperty(UserFormPresenter.CONFIRM_PASSWORD_PROPERTY, passwordProperty);
    userFormPresenter.setPasswordItemDataSource(passwordItem);
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
    addPhoneNumberButton.addClickListener(e -> addPhoneNumber());
    saveButton.addClickListener(e -> save());
    cancelButton.addClickListener(e -> refresh());
  }

  private void updateEditable() {
    boolean editable = editableProperty.getValue();
    userFormPresenter.setEditable(editable);
    laboratoryFormPresenter.setEditable(
        editable && authorizationService.hasLaboratoryManagerPermission(user.getLaboratory()));
    addressFormPresenter.setEditable(editable);
    for (DeletablePhoneNumberFormPresenter phoneNumberFormPresenter : phoneNumberFormPresenters) {
      phoneNumberFormPresenter.setEditable(editable);
    }
    addPhoneNumberButton.setVisible(editable);
    saveButton.setVisible(editable);
    cancelButton.setVisible(editable);
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setCaptions();
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

  private void addPhoneNumber() {
    AddPhoneNumberWindow window = addPhoneNumberWindowProvider.get();
    window.center();
    window.setModal(true);
    AddPhoneNumberFormPresenter presenter = window.getPresenter();
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setType(PhoneNumberType.values()[0]);
    presenter.setItemDataSource(new BeanItem<>(phoneNumber));
    presenter.addSaveClickListener(e -> saveNewPhoneNumber(window, presenter, phoneNumber));
    view.showWindow(window);
  }

  private void saveNewPhoneNumber(Window window, AddPhoneNumberFormPresenter presenter,
      PhoneNumber phoneNumber) {
    try {
      presenter.commit();
      user.getPhoneNumbers().add(phoneNumber);
      userService.update(user, null);
      window.close();
      refresh();
      final MessageResource resources = view.getResources();
      view.afterSuccessfulUpdate(resources.message("addPhoneNumber.done", phoneNumber.getNumber(),
          phoneNumber.getExtension() != null ? 1 : 0, phoneNumber.getExtension()));
    } catch (CommitException e) {
      String message = vaadinUtils.getFieldMessage(e, view.getLocale());
      logger.debug("Validation failed with message {}", message);
      view.showError(message);
    }
  }

  private void removePhoneNumber(PhoneNumber phoneNumber) {
    user.getPhoneNumbers().remove(phoneNumber);
    userService.update(user, null);
    refresh();
    final MessageResource resources = view.getResources();
    view.afterSuccessfulUpdate(resources.message("removePhoneNumber.done", phoneNumber.getNumber(),
        phoneNumber.getExtension() != null ? 1 : 0, phoneNumber.getExtension()));
  }

  private void save() {
    try {
      userFormPresenter.commit();
      laboratoryFormPresenter.commit();
      addressFormPresenter.commit();
      for (DeletablePhoneNumberFormPresenter phoneNumberFormPresenter : phoneNumberFormPresenters) {
        phoneNumberFormPresenter.commit();
      }
      String password = null;
      if (passwordProperty.getValue() != null && !passwordProperty.getValue().isEmpty()) {
        password = passwordProperty.getValue();
      }
      userService.update(user, password);
      if (laboratoryFormPresenter.isEditable()) {
        laboratoryService.update(user.getLaboratory());
      }
      refresh();
      final MessageResource resources = view.getResources();
      view.afterSuccessfulUpdate(resources.message("save.done", user.getEmail()));
    } catch (CommitException e) {
      String message = vaadinUtils.getFieldMessage(e, view.getLocale());
      logger.debug("Validation failed with message {}", message);
      view.showError(message);
    }
  }

  private void refresh() {
    user = userService.get(user.getId());
    setUser(user);
  }

  /**
   * Sets user.
   *
   * @param user
   *          user
   */
  public void setUser(User user) {
    this.user = user;
    editableProperty.setValue(authorizationService.hasUserWritePermission(user));
    userFormPresenter.setItemDataSource(new BeanItem<>(user, User.class));
    laboratoryFormPresenter
        .setItemDataSource(new BeanItem<>(user.getLaboratory(), Laboratory.class));
    addressFormPresenter.setItemDataSource(new BeanItem<>(user.getAddress(), Address.class));
    phoneNumbersLayout.removeAllComponents();
    phoneNumberFormPresenters.clear();
    List<PhoneNumber> phoneNumbers = user.getPhoneNumbers();
    if (phoneNumbers == null || phoneNumbers.isEmpty()) {
      phoneNumbers = new ArrayList<>();
      PhoneNumber empty = new PhoneNumber();
      empty.setType(PhoneNumberType.WORK);
      phoneNumbers.add(empty);
      user.setPhoneNumbers(phoneNumbers);
    }
    for (PhoneNumber phoneNumber : phoneNumbers) {
      DeletablePhoneNumberForm form = new DeletablePhoneNumberForm();
      DeletablePhoneNumberFormPresenter presenter = deletablePhoneNumberPresenterProvider.get();
      presenter.init(form);
      phoneNumberFormPresenters.add(presenter);
      presenter.addDeleteClickListener(e -> removePhoneNumber(phoneNumber));
      presenter.setEditable(editableProperty.getValue());
      phoneNumbersLayout.addComponent(form);
      presenter.setItemDataSource(new BeanItem<>(phoneNumber, PhoneNumber.class));
    }
  }

  public void addCancelClickListener(ClickListener listener) {
    cancelButton.addClickListener(listener);
  }

  public void removeCancelClickListener(ClickListener listener) {
    cancelButton.removeClickListener(listener);
  }
}
