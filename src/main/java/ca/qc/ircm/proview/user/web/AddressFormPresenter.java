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
import com.vaadin.ui.ComboBox;
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
  private TextField addressField;
  private TextField addressSecondField;
  private TextField townField;
  private TextField stateField;
  private ComboBox countryField;
  private TextField postalCodeField;
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
    updateEditable();
  }

  private void setFields() {
    addressField = view.getAddressField();
    addressSecondField = view.getAddressSecondField();
    townField = view.getTownField();
    stateField = view.getStateField();
    countryField = view.getCountryField();
    postalCodeField = view.getPostalCodeField();
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

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setCaptions();
    setRequired();
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    addressField.setCaption(resources.message(ADDRESS_PROPERTY));
    addressSecondField.setCaption(resources.message(ADDRESS_SECOND_PROPERTY));
    townField.setCaption(resources.message(TOWN_PROPERTY));
    stateField.setCaption(resources.message(STATE_PROPERTY));
    countryField.setCaption(resources.message(COUNTRY_PROPERTY));
    postalCodeField.setCaption(resources.message(POSTAL_CODE_PROPERTY));
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
  }

  public void commit() throws CommitException {
    addressFieldGroup.commit();
  }

  public boolean isValid() {
    return addressFieldGroup.isValid();
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
