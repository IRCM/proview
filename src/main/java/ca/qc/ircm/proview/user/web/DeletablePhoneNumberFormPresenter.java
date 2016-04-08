package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Deletable phone number form presenter.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DeletablePhoneNumberFormPresenter {
  public static final String DELETE_PROPERTY = "delete";
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private DeletablePhoneNumberForm view;
  private PhoneNumberForm phoneNumberForm;
  private Button deleteButton;
  @Inject
  private PhoneNumberFormPresenter phoneNumberFormPresenter;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(DeletablePhoneNumberForm view) {
    this.view = view;
    view.setPresenter(this);
    setFields();
    addListeners();
    updateEditable();
    phoneNumberFormPresenter.init(phoneNumberForm);
  }

  private void setFields() {
    phoneNumberForm = view.getPhoneNumberForm();
    deleteButton = view.getDeleteButton();
  }

  private void addListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
  }

  private void updateEditable() {
    boolean editable = editableProperty.getValue();
    phoneNumberFormPresenter.setEditable(editable);
    deleteButton.setVisible(editable);
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setCaptions();
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    deleteButton.setCaption(resources.message(DELETE_PROPERTY));
  }

  public void commit() throws CommitException {
    phoneNumberFormPresenter.commit();
  }

  public boolean isValid() {
    return phoneNumberFormPresenter.isValid();
  }

  public Item getItemDataSource() {
    return phoneNumberFormPresenter.getItemDataSource();
  }

  public void setItemDataSource(Item item) {
    phoneNumberFormPresenter.setItemDataSource(item);
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }

  public void addDeleteClickListener(ClickListener listener) {
    deleteButton.addClickListener(listener);
  }

  public void removeDeleteClickListener(ClickListener listener) {
    deleteButton.removeClickListener(listener);
  }
}
