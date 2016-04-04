package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.QPhoneNumber;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Phone number form.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PhoneNumberFormPresenter {
  public static final String TYPE_PROPERTY = QPhoneNumber.phoneNumber.type.getMetadata().getName();
  public static final String NUMBER_PROPERTY =
      QPhoneNumber.phoneNumber.number.getMetadata().getName();
  public static final String EXTENSION_PROPERTY =
      QPhoneNumber.phoneNumber.extension.getMetadata().getName();
  private static final String ID_SEPARATOR = "-";
  private static final String TYPE_FIELD_ID = "type";
  private static final String NUMBER_FIELD_ID = "number";
  private static final String EXTENSION_FIELD_ID = "extension";
  private static final String SAVE_BUTTON_ID = "save";
  private static final String CANCEL_BUTTON_ID = "cancel";
  private BeanFieldGroup<PhoneNumber> phoneNumberFieldGroup =
      new BeanFieldGroup<>(PhoneNumber.class);
  private PhoneNumberForm view;
  private Label header;
  private ComboBox typeField;
  private TextField numberField;
  private TextField extensionField;
  private Button saveButton;
  private Button cancelButton;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(PhoneNumberForm view) {
    this.view = view;
    view.setPresenter(this);
    setFields();
    bindFields();
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setTypeValues();
    setCaptions();
  }

  private void setFields() {
    header = view.getHeader();
    typeField = view.getTypeField();
    numberField = view.getNumberField();
    extensionField = view.getExtensionField();
    saveButton = view.getSaveButton();
    cancelButton = view.getCancelButton();
  }

  private void bindFields() {
    phoneNumberFieldGroup.setItemDataSource(new BeanItem<>(new PhoneNumber()));
    phoneNumberFieldGroup.bind(typeField, TYPE_PROPERTY);
    phoneNumberFieldGroup.bind(numberField, NUMBER_PROPERTY);
    phoneNumberFieldGroup.bind(extensionField, EXTENSION_PROPERTY);
  }

  private void setTypeValues() {
    for (PhoneNumberType type : PhoneNumberType.values()) {
      typeField.addItem(type);
      typeField.setItemCaption(type, type.getLabel(view.getLocale()));
    }
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    header.setValue(resources.message("header"));
    typeField.setCaption(resources.message("type"));
    numberField.setCaption(resources.message("number"));
    extensionField.setCaption(resources.message("extension"));
    saveButton.setCaption(resources.message("save"));
    cancelButton.setCaption(resources.message("cancel"));
  }

  /**
   * Add default regular expression validators.
   */
  public void addDefaultRegexpValidators() {
    MessageResource resources = view.getResources();
    numberField.addValidator(new RegexpValidator("[\\d\\-]*", resources.message("number.invalid")));
    extensionField
        .addValidator(new RegexpValidator("[\\d\\-]*", resources.message("extension.invalid")));
  }

  public void commit() throws CommitException {
    phoneNumberFieldGroup.commit();
  }

  public boolean isValid() {
    return phoneNumberFieldGroup.isValid();
  }

  /**
   * Sets id prefix for view.
   *
   * @param idPrefix
   *          id prefix
   */
  public void setId(String idPrefix) {
    String subId = idPrefix == null || idPrefix.isEmpty() ? "" : idPrefix + ID_SEPARATOR;
    typeField.setId(subId + TYPE_FIELD_ID);
    numberField.setId(subId + NUMBER_FIELD_ID);
    extensionField.setId(subId + EXTENSION_FIELD_ID);
    saveButton.setId(subId + SAVE_BUTTON_ID);
    cancelButton.setId(subId + CANCEL_BUTTON_ID);
  }

  public Item getItemDataSource() {
    return phoneNumberFieldGroup.getItemDataSource();
  }

  public void setItemDataSource(Item item) {
    phoneNumberFieldGroup.setItemDataSource(item);
  }
}
