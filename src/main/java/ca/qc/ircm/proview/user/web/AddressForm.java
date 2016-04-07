package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

/**
 * Address form.
 */
public class AddressForm extends AddressFormDesign implements MessageResourcesComponent {
  private static final long serialVersionUID = -1740032515671612030L;
  private AddressFormPresenter presenter;

  public void setPresenter(AddressFormPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.attach();
  }

  public TextField getAddressField() {
    return addressField;
  }

  public TextField getAddressSecondField() {
    return addressSecondField;
  }

  public TextField getTownField() {
    return townField;
  }

  public TextField getStateField() {
    return stateField;
  }

  public ComboBox getCountryField() {
    return countryField;
  }

  public TextField getPostalCodeField() {
    return postalCodeField;
  }
}
