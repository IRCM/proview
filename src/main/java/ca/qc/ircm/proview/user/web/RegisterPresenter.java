package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.laboratory.QLaboratory;
import ca.qc.ircm.proview.user.QAddress;
import ca.qc.ircm.proview.user.QPhoneNumber;
import ca.qc.ircm.proview.user.QUser;

/**
 * Registers user presenter.
 */
public interface RegisterPresenter {
  public static final String emailProperty = QUser.user.email.getMetadata().getName();
  public static final String nameProperty = QUser.user.name.getMetadata().getName();
  public static final String laboratoryNameProperty =
      QLaboratory.laboratory.name.getMetadata().getName();
  public static final String organizationProperty =
      QLaboratory.laboratory.organization.getMetadata().getName();
  public static final String addressProperty = QAddress.address1.address.getMetadata().getName();
  public static final String addressSecondProperty =
      QAddress.address1.addressSecond.getMetadata().getName();
  public static final String townProperty = QAddress.address1.town.getMetadata().getName();
  public static final String stateProperty = QAddress.address1.state.getMetadata().getName();
  public static final String countryProperty = QAddress.address1.country.getMetadata().getName();
  public static final String postalCodeProperty =
      QAddress.address1.postalCode.getMetadata().getName();
  public static final String phoneNumberProperty =
      QPhoneNumber.phoneNumber.number.getMetadata().getName();
  public static final String phoneExtensionProperty =
      QPhoneNumber.phoneNumber.extension.getMetadata().getName();
  public static final String passwordProperty = "password";
  public static final String confirmPasswordProperty = "confirmPassword";

  /**
   * Called by view when view is initialized.
   * 
   * @param view
   *          view
   */
  public void init(RegisterView view);
}
