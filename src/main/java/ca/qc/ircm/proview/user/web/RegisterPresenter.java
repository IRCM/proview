package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.laboratory.QLaboratory.laboratory;
import static ca.qc.ircm.proview.user.QAddress.address1;
import static ca.qc.ircm.proview.user.QPhoneNumber.phoneNumber;
import static ca.qc.ircm.proview.user.QUser.user;

/**
 * Registers user presenter.
 */
public interface RegisterPresenter {
  public static final String emailProperty = user.email.getMetadata().getName();
  public static final String nameProperty = user.name.getMetadata().getName();
  public static final String laboratoryNameProperty = laboratory.name.getMetadata().getName();
  public static final String organizationProperty = laboratory.organization.getMetadata().getName();
  public static final String addressProperty = address1.address.getMetadata().getName();
  public static final String addressSecondProperty = address1.addressSecond.getMetadata().getName();
  public static final String townProperty = address1.town.getMetadata().getName();
  public static final String stateProperty = address1.state.getMetadata().getName();
  public static final String countryProperty = address1.country.getMetadata().getName();
  public static final String postalCodeProperty = address1.postalCode.getMetadata().getName();
  public static final String phoneNumberProperty = phoneNumber.number.getMetadata().getName();
  public static final String phoneExtensionProperty = phoneNumber.extension.getMetadata().getName();
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
