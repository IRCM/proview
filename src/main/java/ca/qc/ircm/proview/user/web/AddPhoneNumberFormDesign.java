package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.user.web.PhoneNumberForm;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

/** 
 * !! DO NOT EDIT THIS FILE !!
 * 
 * This class is generated by Vaadin Designer and will be overwritten.
 * 
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class AddPhoneNumberFormDesign extends VerticalLayout {
  protected Label header;
  protected PhoneNumberForm phoneNumberForm;
  protected Button saveButton;
  protected Button cancelButton;

  public AddPhoneNumberFormDesign() {
    Design.read(this);
  }
}