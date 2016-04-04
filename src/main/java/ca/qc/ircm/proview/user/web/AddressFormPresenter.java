package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.user.Address;
import ca.qc.ircm.proview.user.QAddress;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Address form presenter.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AddressFormPresenter {
  public static final String ADDRESS_PROPERTY = QAddress.address1.address.getMetadata().getName();
  public static final String ADDRESS_SECOND_PROPERTY =
      QAddress.address1.addressSecond.getMetadata().getName();
  public static final String TOWN_PROPERTY = QAddress.address1.town.getMetadata().getName();
  public static final String STATE_PROPERTY = QAddress.address1.state.getMetadata().getName();
  public static final String COUNTRY_PROPERTY = QAddress.address1.country.getMetadata().getName();
  public static final String POSTAL_CODE_PROPERTY =
      QAddress.address1.postalCode.getMetadata().getName();
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private BeanFieldGroup<Address> addressFieldGroup = new BeanFieldGroup<>(Address.class);
  private AddressForm view;
  private Label header;
  private TextField addressField;
  private TextField addressSecondField;
  private TextField townField;
  private TextField stateField;
  private ComboBox countryField;
  private TextField postalCodeField;
  private HorizontalLayout buttonsLayout;
  private Button saveButton;
  private Button cancelButton;
  @Inject
  private ApplicationConfiguration applicationConfiguration;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(AddressForm view) {
    this.view = view;
    view.setPresenter(this);
    setFields();
    bindFields();
    addFieldListeners();
    setCountryValues();
    setDefaultAddress();
  }

  private String getDefaultCountry() {
    String[] countries = applicationConfiguration.getCountries();
    if (countries.length > 0) {
      return countries[0];
    } else {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private void setDefaultAddress() {
    addressFieldGroup.getItemDataSource().getItemProperty(ADDRESS_PROPERTY)
        .setValue(applicationConfiguration.getAddress());
    addressFieldGroup.getItemDataSource().getItemProperty(TOWN_PROPERTY)
        .setValue(applicationConfiguration.getTown());
    addressFieldGroup.getItemDataSource().getItemProperty(STATE_PROPERTY)
        .setValue(applicationConfiguration.getState());
    addressFieldGroup.getItemDataSource().getItemProperty(POSTAL_CODE_PROPERTY)
        .setValue(applicationConfiguration.getPostalCode());
    addressFieldGroup.getItemDataSource().getItemProperty(COUNTRY_PROPERTY)
        .setValue(getDefaultCountry());
  }

  private void setFields() {
    header = view.getHeader();
    addressField = view.getAddressField();
    addressSecondField = view.getAddressSecondField();
    townField = view.getTownField();
    stateField = view.getStateField();
    countryField = view.getCountryField();
    postalCodeField = view.getPostalCodeField();
    buttonsLayout = view.getButtonsLayout();
    saveButton = view.getSaveButton();
    cancelButton = view.getCancelButton();
  }

  private void bindFields() {
    addressFieldGroup.setItemDataSource(new BeanItem<>(new Address()));
    addressFieldGroup.bind(addressField, ADDRESS_PROPERTY);
    addressFieldGroup.bind(addressSecondField, ADDRESS_SECOND_PROPERTY);
    addressFieldGroup.bind(townField, TOWN_PROPERTY);
    addressFieldGroup.bind(stateField, STATE_PROPERTY);
    addressFieldGroup.bind(countryField, COUNTRY_PROPERTY);
    addressFieldGroup.bind(postalCodeField, POSTAL_CODE_PROPERTY);
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
  }

  private void setCountryValues() {
    for (String country : applicationConfiguration.getCountries()) {
      countryField.addItem(country);
    }
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setCaptions();
    setRequired();
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    header.setValue(resources.message("header"));
    MessageResource addressResources = view.getResources(Address.class);
    addressField.setCaption(addressResources.message(ADDRESS_PROPERTY));
    addressSecondField.setCaption(addressResources.message(ADDRESS_SECOND_PROPERTY));
    townField.setCaption(addressResources.message(TOWN_PROPERTY));
    stateField.setCaption(addressResources.message(STATE_PROPERTY));
    countryField.setCaption(addressResources.message(COUNTRY_PROPERTY));
    postalCodeField.setCaption(addressResources.message(POSTAL_CODE_PROPERTY));
    saveButton.setCaption(resources.message("save"));
    cancelButton.setCaption(resources.message("cancel"));
  }

  private void setRequired() {
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    addressField.setRequired(true);
    addressField.setRequiredError(generalResources.message("required", addressField.getCaption()));
    townField.setRequired(true);
    townField.setRequiredError(generalResources.message("required", townField.getCaption()));
    stateField.setRequired(true);
    stateField.setRequiredError(generalResources.message("required", stateField.getCaption()));
    countryField.setRequired(true);
    countryField.setRequiredError(generalResources.message("required", countryField.getCaption()));
    postalCodeField.setRequired(true);
    postalCodeField
        .setRequiredError(generalResources.message("required", postalCodeField.getCaption()));
  }

  private void updateEditable() {
    boolean editable = editableProperty.getValue();
    addressField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    addressField.setReadOnly(!editable);
    addressSecondField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    addressSecondField.setReadOnly(!editable);
    townField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    townField.setReadOnly(!editable);
    stateField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    stateField.setReadOnly(!editable);
    countryField.setStyleName(editable ? "" : ValoTheme.COMBOBOX_BORDERLESS);
    countryField.setReadOnly(!editable);
    postalCodeField.setStyleName(editable ? "" : ValoTheme.TEXTFIELD_BORDERLESS);
    postalCodeField.setReadOnly(!editable);
    buttonsLayout.setVisible(editable);
  }

  public void commit() throws CommitException {
    addressFieldGroup.commit();
  }

  public boolean isValid() {
    return addressFieldGroup.isValid();
  }

  public void addSaveClickListener(ClickListener listener) {
    saveButton.addClickListener(listener);
  }

  public void addCancelClickListener(ClickListener listener) {
    cancelButton.addClickListener(listener);
  }

  public Item getItemDataSource() {
    return addressFieldGroup.getItemDataSource();
  }

  public void setItemDataSource(Item item) {
    addressFieldGroup.setItemDataSource(item);
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }
}
