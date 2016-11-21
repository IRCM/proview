package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.laboratory.QLaboratory.laboratory;
import static ca.qc.ircm.proview.user.QAddress.address;
import static ca.qc.ircm.proview.user.QPhoneNumber.phoneNumber;
import static ca.qc.ircm.proview.user.QUser.user;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.GENERAL_MESSAGES;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_EMAIL;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * User form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewUserFormPresenter {
  public static final String USER = user.getMetadata().getName();
  public static final String ID = user.id.getMetadata().getName();
  public static final String EMAIL = user.email.getMetadata().getName();
  public static final String NAME = user.name.getMetadata().getName();
  public static final String PASSWORD = "password";
  public static final String CONFIRM_PASSWORD = "confirmPassword";
  public static final String LABORATORY = laboratory.getMetadata().getName();
  public static final String LABORATORY_ORGANIZATION =
      laboratory.organization.getMetadata().getName();
  public static final String LABORATORY_NAME = laboratory.name.getMetadata().getName();
  public static final String ADDRESS = address.getMetadata().getName();
  public static final String ADDRESS_LINE = address.line.getMetadata().getName();
  public static final String ADDRESS_TOWN = address.town.getMetadata().getName();
  public static final String ADDRESS_STATE = address.state.getMetadata().getName();
  public static final String ADDRESS_COUNTRY = address.country.getMetadata().getName();
  public static final String ADDRESS_POSTAL_CODE = address.postalCode.getMetadata().getName();
  public static final String PHONE_NUMBERS = user.phoneNumbers.getMetadata().getName();
  public static final String PHONE_NUMBER = phoneNumber.getMetadata().getName();
  public static final String PHONE_NUMBER_TYPE = phoneNumber.type.getMetadata().getName();
  public static final String PHONE_NUMBER_NUMBER = phoneNumber.number.getMetadata().getName();
  public static final String PHONE_NUMBER_EXTENSION = phoneNumber.extension.getMetadata().getName();
  public static final String REMOVE_PHONE_NUMBER = "removePhoneNumber";
  public static final String ADD_PHONE_NUMBER = "addPhoneNumber";
  public static final String SAVE = "save";
  private static final Logger logger = LoggerFactory.getLogger(NewUserFormPresenter.class);
  private NewUserForm view;
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private BeanFieldGroup<User> userFieldGroup = new BeanFieldGroup<>(User.class);
  private FieldGroup passwordFieldGroup = new FieldGroup();
  private List<BeanFieldGroup<PhoneNumber>> phoneNumberFieldGroups = new ArrayList<>();
  @Inject
  private UserService userService;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(NewUserForm view) {
    this.view = view;
    prepareFields();
    bindFields();
    addListeners();
  }

  private void prepareFields() {
    MessageResource resources = view.getResources();
    final MessageResource generalResources =
        new MessageResource(GENERAL_MESSAGES, view.getLocale());
    view.userPanel.addStyleName(USER);
    view.userPanel.setCaption(resources.message(USER));
    view.emailField.addStyleName(EMAIL);
    view.emailField.setCaption(resources.message(EMAIL));
    view.emailField.setRequired(true);
    view.emailField.setRequiredError(generalResources.message(REQUIRED));
    view.emailField.addValidator(new EmailValidator(generalResources.message(INVALID_EMAIL)));
    view.emailField.addValidator((value) -> {
      String email = view.emailField.getValue();
      Long userId = null;
      if (userFieldGroup.getField(ID) != null && userFieldGroup.getField(ID).getValue() != null) {
        userId = (Long) userFieldGroup.getField(ID).getValue();
      }
      if (userService.exists(email)
          && (userId == null || !userService.get(userId).getEmail().equals(email))) {
        throw new InvalidValueException(generalResources.message(ALREADY_EXISTS));
      }
    });
    view.nameField.addStyleName(NAME);
    view.nameField.setCaption(resources.message(NAME));
    view.nameField.setRequired(true);
    view.nameField.setRequiredError(generalResources.message(REQUIRED));
    view.passwordField.addStyleName(PASSWORD);
    view.passwordField.setCaption(resources.message(PASSWORD));
    view.passwordField.addValidator((value) -> {
      String password = view.passwordField.getValue();
      String confirmPassword = view.confirmPasswordField.getValue();
      if (password != null && !password.isEmpty() && confirmPassword != null
          && !confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
        throw new InvalidValueException(resources.message(PASSWORD + ".notMatch"));
      }
    });
    view.confirmPasswordField.addStyleName(CONFIRM_PASSWORD);
    view.confirmPasswordField.setCaption(resources.message(CONFIRM_PASSWORD));
    view.laboratoryPanel.addStyleName(LABORATORY);
    view.laboratoryPanel.setCaption(resources.message(LABORATORY));
    view.organizationField.addStyleName(LABORATORY_ORGANIZATION);
    view.organizationField.setCaption(resources.message(LABORATORY_ORGANIZATION));
    view.organizationField.setRequired(true);
    view.organizationField.setRequiredError(generalResources.message(REQUIRED));
    view.laboratoryNameField.addStyleName(LABORATORY_NAME);
    view.laboratoryNameField.setCaption(resources.message(LABORATORY_NAME));
    view.laboratoryNameField.setRequired(true);
    view.laboratoryNameField.setRequiredError(generalResources.message(REQUIRED));
    view.addressPanel.addStyleName(ADDRESS);
    view.addressPanel.setCaption(resources.message(ADDRESS));
    view.addressLineField.addStyleName(ADDRESS_LINE);
    view.addressLineField.setCaption(resources.message(ADDRESS_LINE));
    view.addressLineField.setRequired(true);
    view.addressLineField.setRequiredError(generalResources.message(REQUIRED));
    view.townField.addStyleName(ADDRESS_TOWN);
    view.townField.setCaption(resources.message(ADDRESS_TOWN));
    view.townField.setRequired(true);
    view.townField.setRequiredError(generalResources.message(REQUIRED));
    view.stateField.addStyleName(ADDRESS_STATE);
    view.stateField.setCaption(resources.message(ADDRESS_STATE));
    view.stateField.setRequired(true);
    view.stateField.setRequiredError(generalResources.message(REQUIRED));
    view.countryField.addStyleName(ADDRESS_COUNTRY);
    view.countryField.setCaption(resources.message(ADDRESS_COUNTRY));
    view.countryField.setRequired(true);
    view.countryField.setRequiredError(generalResources.message(REQUIRED));
    view.postalCodeField.addStyleName(ADDRESS_POSTAL_CODE);
    view.postalCodeField.setCaption(resources.message(ADDRESS_POSTAL_CODE));
    view.postalCodeField.setRequired(true);
    view.postalCodeField.setRequiredError(generalResources.message(REQUIRED));
    view.phoneNumbersPanel.addStyleName(PHONE_NUMBERS);
    view.phoneNumbersPanel.setCaption(resources.message(PHONE_NUMBERS));
    view.addPhoneNumberButton.addStyleName(ADD_PHONE_NUMBER);
    view.addPhoneNumberButton.setCaption(resources.message(ADD_PHONE_NUMBER));
    view.saveButton.addStyleName(SAVE);
    view.saveButton.setCaption(resources.message(SAVE));
  }

  private void bindFields() {
    userFieldGroup.bind(view.emailField, EMAIL);
    userFieldGroup.bind(view.nameField, NAME);
    passwordFieldGroup.bind(view.passwordField, PASSWORD);
    passwordFieldGroup.bind(view.confirmPasswordField, CONFIRM_PASSWORD);
    userFieldGroup.bind(view.organizationField, LABORATORY + "." + LABORATORY_ORGANIZATION);
    userFieldGroup.bind(view.laboratoryNameField, LABORATORY + "." + LABORATORY_NAME);
    userFieldGroup.bind(view.addressLineField, ADDRESS + "." + ADDRESS_LINE);
    userFieldGroup.bind(view.townField, ADDRESS + "." + ADDRESS_TOWN);
    userFieldGroup.bind(view.stateField, ADDRESS + "." + ADDRESS_STATE);
    userFieldGroup.bind(view.countryField, ADDRESS + "." + ADDRESS_COUNTRY);
    userFieldGroup.bind(view.postalCodeField, ADDRESS + "." + ADDRESS_POSTAL_CODE);
  }

  private void addListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
    view.addPhoneNumberButton.addClickListener(e -> addPhoneNumber());
    view.saveButton.addClickListener(e -> save());
  }

  private void updateEditable() {
    boolean editable = editableProperty.getValue();
    view.emailField.setReadOnly(!editable);
    view.nameField.setReadOnly(!editable);
    view.organizationField.setReadOnly(!editable);
    view.laboratoryNameField.setReadOnly(!editable);
    view.addressLineField.setReadOnly(!editable);
    view.townField.setReadOnly(!editable);
    view.stateField.setReadOnly(!editable);
    view.countryField.setReadOnly(!editable);
    view.postalCodeField.setReadOnly(!editable);
    view.addPhoneNumberButton.setVisible(editable);
    view.buttonsLayout.setVisible(editable);
    view.saveButton.setVisible(editable);
  }

  private void addPhoneNumber() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources =
        new MessageResource(GENERAL_MESSAGES, view.getLocale());
    BeanFieldGroup<PhoneNumber> phoneNumberFieldGroup = new BeanFieldGroup<>(PhoneNumber.class);
    phoneNumberFieldGroups.add(phoneNumberFieldGroup);
    FormLayout layout = new FormLayout();
    layout.setMargin(false);
    view.phoneNumbersLayout.addComponent(layout);
    ComboBox typeField = new ComboBox();
    typeField.addStyleName(PHONE_NUMBER + "-" + PHONE_NUMBER_TYPE);
    typeField.setCaption(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_TYPE));
    for (PhoneNumberType type : PhoneNumberType.values()) {
      typeField.addItem(type);
      typeField.setItemCaption(type, type.getLabel(view.getLocale()));
    }
    typeField.setRequired(true);
    typeField.setRequiredError(generalResources.message(REQUIRED));
    layout.addComponent(typeField);
    TextField numberField = new TextField();
    numberField.setNullRepresentation("");
    numberField.addStyleName(PHONE_NUMBER + "-" + PHONE_NUMBER_NUMBER);
    numberField.setCaption(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_NUMBER));
    numberField.setRequired(true);
    numberField.setRequiredError(generalResources.message(REQUIRED));
    numberField.addValidator(new RegexpValidator("[\\d\\-]*",
        resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_NUMBER + ".invalid")));
    layout.addComponent(numberField);
    TextField extensionField = new TextField();
    extensionField.setNullRepresentation("");
    extensionField.addStyleName(PHONE_NUMBER + "-" + PHONE_NUMBER_EXTENSION);
    extensionField.setCaption(resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_EXTENSION));
    extensionField.addValidator(new RegexpValidator("[\\d\\-]*",
        resources.message(PHONE_NUMBER + "." + PHONE_NUMBER_EXTENSION + ".invalid")));
    layout.addComponent(extensionField);
    Button removeButton = new Button();
    removeButton.addStyleName(REMOVE_PHONE_NUMBER);
    removeButton.setCaption(resources.message(REMOVE_PHONE_NUMBER));
    removeButton.addClickListener(e -> removePhoneNumber(phoneNumberFieldGroup, layout));
    layout.addComponent(removeButton);
    phoneNumberFieldGroup.bind(typeField, PHONE_NUMBER_TYPE);
    phoneNumberFieldGroup.bind(numberField, PHONE_NUMBER_NUMBER);
    phoneNumberFieldGroup.bind(extensionField, PHONE_NUMBER_EXTENSION);
  }

  private void removePhoneNumber(BeanFieldGroup<PhoneNumber> phoneNumberFieldGroup,
      Component layout) {
    phoneNumberFieldGroups.remove(phoneNumberFieldGroup);
    view.phoneNumbersLayout.removeComponent(layout);
  }

  private boolean validate() {
    logger.trace("Validate user");
    boolean valid = true;
    try {
      userFieldGroup.commit();
      view.passwordField.commit();
      view.confirmPasswordField.commit();
      for (BeanFieldGroup<PhoneNumber> phoneNumberFieldGroup : phoneNumberFieldGroups) {
        phoneNumberFieldGroup.commit();
      }
    } catch (InvalidValueException | CommitException e) {
      final MessageResource generalResources =
          new MessageResource(GENERAL_MESSAGES, view.getLocale());
      logger.trace("Validation {} failed with message {}",
          e instanceof CommitException ? "commit" : "value", e.getMessage(), e);
      view.showError(generalResources.message(FIELD_NOTIFICATION));
      valid = false;
    }
    return valid;
  }

  private void save() {
    if (validate()) {
      User user = userFieldGroup.getItemDataSource().getBean();
      String password = view.passwordField.getValue();
      user.getPhoneNumbers().clear();
      for (BeanFieldGroup<PhoneNumber> phoneNumberFieldGroup : phoneNumberFieldGroups) {
        user.getPhoneNumbers().add(phoneNumberFieldGroup.getItemDataSource().getBean());
      }
      userService.update(user, password);
      final MessageResource resources = view.getResources();
      view.showTrayNotification(resources.message("save.done", user.getEmail()));
      view.fireSaveEvent(user);
    }
  }

  public Item getItemDataSource() {
    return userFieldGroup.getItemDataSource();
  }

  /**
   * Sets user as an item.
   *
   * @param item
   *          user as an item
   */
  public void setItemDataSource(Item item) {
    if (item == null) {
      item = new BeanItem<>(null, User.class);
    } else if (!(item instanceof BeanItem)) {
      throw new IllegalArgumentException("item must be an instance of BeanItem");
    }

    userFieldGroup.setItemDataSource(item);
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }

  public void addSaveListener(SaveListener listener) {
    view.addListener(SaveEvent.class, listener, SaveListener.SAVED_METHOD);
  }

  public void removeSaveListener(SaveListener listener) {
    view.removeListener(SaveEvent.class, listener, SaveListener.SAVED_METHOD);
  }
}
