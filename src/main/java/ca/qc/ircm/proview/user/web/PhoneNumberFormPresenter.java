package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.user.PhoneNumber;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.QPhoneNumber;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Phone number form presenter.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PhoneNumberFormPresenter {
  public static final String TYPE_PROPERTY = QPhoneNumber.phoneNumber.type.getMetadata().getName();
  public static final String NUMBER_PROPERTY =
      QPhoneNumber.phoneNumber.number.getMetadata().getName();
  public static final String EXTENSION_PROPERTY =
      QPhoneNumber.phoneNumber.extension.getMetadata().getName();
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private BeanFieldGroup<PhoneNumber> phoneNumberFieldGroup =
      new BeanFieldGroup<>(PhoneNumber.class);
  private PhoneNumberForm view;
  private ComboBox typeField;
  private TextField numberField;
  private TextField extensionField;

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
    addFieldListeners();
    updateEditable();
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setTypeValues();
    setCaptions();
  }

  private void setFields() {
    typeField = view.getTypeField();
    numberField = view.getNumberField();
    extensionField = view.getExtensionField();
  }

  private void bindFields() {
    phoneNumberFieldGroup.setItemDataSource(new BeanItem<>(new PhoneNumber()));
    phoneNumberFieldGroup.bind(typeField, TYPE_PROPERTY);
    phoneNumberFieldGroup.bind(numberField, NUMBER_PROPERTY);
    phoneNumberFieldGroup.bind(extensionField, EXTENSION_PROPERTY);
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
  }

  private void setTypeValues() {
    typeField.removeAllItems();
    for (PhoneNumberType type : PhoneNumberType.values()) {
      typeField.addItem(type);
      typeField.setItemCaption(type, type.getLabel(view.getLocale()));
    }
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    typeField.setCaption(resources.message(TYPE_PROPERTY));
    numberField.setCaption(resources.message(NUMBER_PROPERTY));
    extensionField.setCaption(resources.message(EXTENSION_PROPERTY));
  }

  /**
   * Add default regular expression validators.
   */
  public void addDefaultRegexpValidators() {
    MessageResource resources = view.getResources();
    numberField.addValidator(
        new RegexpValidator("[\\d\\-]*", resources.message(NUMBER_PROPERTY + ".invalid")));
    extensionField.addValidator(
        new RegexpValidator("[\\d\\-]*", resources.message(EXTENSION_PROPERTY + ".invalid")));
  }

  private void updateEditable() {
    boolean editable = editableProperty.getValue();
    typeField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    typeField.setReadOnly(!editable);
    numberField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    numberField.setReadOnly(!editable);
    extensionField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    extensionField.setReadOnly(!editable);
  }

  public void commit() throws CommitException {
    phoneNumberFieldGroup.commit();
  }

  public boolean isValid() {
    return phoneNumberFieldGroup.isValid();
  }

  public Item getItemDataSource() {
    return phoneNumberFieldGroup.getItemDataSource();
  }

  public void setItemDataSource(Item item) {
    phoneNumberFieldGroup.setItemDataSource(item);
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }
}
