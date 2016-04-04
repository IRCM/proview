package ca.qc.ircm.proview.laboratory.web;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.laboratory.QLaboratory;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
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
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private BeanFieldGroup<Laboratory> laboratoryFieldGroup = new BeanFieldGroup<>(Laboratory.class);
  private LaboratoryForm view;
  private Label header;
  private TextField organizationField;
  private TextField nameField;

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
    addFieldListeners();
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
  }

  private void bindFields() {
    laboratoryFieldGroup.setItemDataSource(new BeanItem<>(new Laboratory()));
    laboratoryFieldGroup.bind(nameField, NAME_PROPERTY);
    laboratoryFieldGroup.bind(organizationField, ORGANIZATION_PROPERTY);
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    header.setValue(resources.message("header"));
    organizationField.setCaption(resources.message("organization"));
    nameField.setCaption(resources.message("name"));
  }

  public void commit() throws CommitException {
    laboratoryFieldGroup.commit();
  }

  public boolean isValid() {
    return laboratoryFieldGroup.isValid();
  }

  private void updateEditable() {
    boolean editable = editableProperty.getValue();
    organizationField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    organizationField.setReadOnly(!editable);
    nameField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    nameField.setReadOnly(!editable);
  }

  public Item getItemDataSource() {
    return laboratoryFieldGroup.getItemDataSource();
  }

  public void setItemDataSource(Item item) {
    laboratoryFieldGroup.setItemDataSource(item);
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }
}
