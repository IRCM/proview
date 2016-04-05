package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.user.QUser;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * User form.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserFormPresenter {
  public static final String EMAIL_PROPERTY = QUser.user.email.getMetadata().getName();
  public static final String NAME_PROPERTY = QUser.user.name.getMetadata().getName();
  public static final String LABORATORY_PROPERTY = QUser.user.laboratory.getMetadata().getName();
  public static final String ADDRESSES_PROPERTY = QUser.user.address.getMetadata().getName();
  public static final String PHONE_NUMBERS_PROPERTY =
      QUser.user.phoneNumbers.getMetadata().getName();
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private BeanFieldGroup<User> userFieldGroup = new BeanFieldGroup<>(User.class);
  private UserForm view;
  private Label header;
  private TextField emailField;
  private TextField nameField;
  private PasswordField passwordField;
  private PasswordField confirmPasswordField;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(UserForm view) {
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
    setCaptions();
    addValidators();
  }

  private void setFields() {
    header = view.getHeader();
    emailField = view.getEmailField();
    nameField = view.getNameField();
    passwordField = view.getPasswordField();
    confirmPasswordField = view.getConfirmPasswordField();
  }

  private void bindFields() {
    userFieldGroup.setItemDataSource(new BeanItem<>(new User()));
    userFieldGroup.bind(emailField, EMAIL_PROPERTY);
    userFieldGroup.bind(nameField, NAME_PROPERTY);
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
    // TODO Program method.
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    header.setValue(resources.message("header"));
    emailField.setCaption(resources.message("email"));
    nameField.setCaption(resources.message("name"));
  }

  private void addValidators() {
    // TODO Program method.
  }

  private void updateEditable() {
    boolean editable = editableProperty.getValue();
    emailField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    emailField.setReadOnly(!editable);
    nameField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    nameField.setReadOnly(!editable);
    passwordField.setVisible(editable);
    confirmPasswordField.setVisible(editable);
  }

  public void commit() throws CommitException {
    userFieldGroup.commit();
  }

  public boolean isValid() {
    return userFieldGroup.isValid();
  }

  public Item getItemDataSource() {
    return userFieldGroup.getItemDataSource();
  }

  public void setItemDataSource(Item item) {
    userFieldGroup.setItemDataSource(item);
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }
}
