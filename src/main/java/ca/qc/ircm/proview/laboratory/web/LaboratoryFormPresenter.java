package ca.qc.ircm.proview.laboratory.web;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.laboratory.QLaboratory;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Laboratory presenter.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LaboratoryFormPresenter {
  public static final String NAME_PROPERTY = QLaboratory.laboratory.name.getMetadata().getName();
  public static final String ORGANIZATION_PROPERTY =
      QLaboratory.laboratory.organization.getMetadata().getName();
  private static final String ID_SEPARATOR = "-";
  private static final String NAME_FIELD_ID = "name";
  private static final String ORGANIZATION_FIELD_ID = "organization";
  private static final String SAVE_BUTTON_ID = "save";
  private static final String CANCEL_BUTTON_ID = "cancel";
  private BeanFieldGroup<Laboratory> laboratoryFieldGroup = new BeanFieldGroup<>(Laboratory.class);
  private LaboratoryForm view;
  private Label header;
  private TextField organizationField;
  private TextField nameField;
  private Button saveButton;
  private Button cancelButton;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(LaboratoryForm view) {
    this.view = view;
    view.setPresenter(this);
    setFields();
    bindFields();
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setCaptions();
  }

  private void setFields() {
    header = view.getHeader();
    organizationField = view.getOrganizationField();
    nameField = view.getNameField();
    saveButton = view.getSaveButton();
    cancelButton = view.getCancelButton();
  }

  private void bindFields() {
    laboratoryFieldGroup.setItemDataSource(new BeanItem<>(new Laboratory()));
    laboratoryFieldGroup.bind(nameField, NAME_PROPERTY);
    laboratoryFieldGroup.bind(organizationField, ORGANIZATION_PROPERTY);
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    header.setValue(resources.message("header"));
    organizationField.setCaption(resources.message("organization"));
    nameField.setCaption(resources.message("name"));
    saveButton.setCaption(resources.message("save"));
    cancelButton.setCaption(resources.message("cancel"));
  }

  public void commit() throws CommitException {
    laboratoryFieldGroup.commit();
  }

  public boolean isValid() {
    return laboratoryFieldGroup.isValid();
  }

  /**
   * Sets id prefix for view.
   *
   * @param idPrefix
   *          id prefix
   */
  public void setId(String idPrefix) {
    String subId = idPrefix == null || idPrefix.isEmpty() ? "" : idPrefix + ID_SEPARATOR;
    nameField.setId(subId + NAME_FIELD_ID);
    organizationField.setId(subId + ORGANIZATION_FIELD_ID);
    saveButton.setId(subId + SAVE_BUTTON_ID);
    cancelButton.setId(subId + CANCEL_BUTTON_ID);
  }

  public Item getItemDataSource() {
    return laboratoryFieldGroup.getItemDataSource();
  }

  public void setItemDataSource(Item item) {
    laboratoryFieldGroup.setItemDataSource(item);
  }
}
