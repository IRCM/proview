package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.AddressProperties.COUNTRY;
import static ca.qc.ircm.proview.user.AddressProperties.LINE;
import static ca.qc.ircm.proview.user.AddressProperties.POSTAL_CODE;
import static ca.qc.ircm.proview.user.AddressProperties.STATE;
import static ca.qc.ircm.proview.user.AddressProperties.TOWN;
import static ca.qc.ircm.proview.user.PhoneNumberProperties.EXTENSION;
import static ca.qc.ircm.proview.user.PhoneNumberProperties.NUMBER;
import static ca.qc.ircm.proview.user.PhoneNumberProperties.TYPE;
import static ca.qc.ircm.proview.user.UserProperties.ADMIN;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.LABORATORY;
import static ca.qc.ircm.proview.user.UserProperties.MANAGER;
import static ca.qc.ircm.proview.user.UserProperties.NAME;
import static ca.qc.ircm.proview.user.web.UserForm.CREATE_NEW_LABORATORY;
import static ca.qc.ircm.proview.user.web.UserForm.NEW_LABORATORY_NAME;
import static ca.qc.ircm.proview.user.web.UserForm.id;

import com.vaadin.flow.component.checkbox.testbench.CheckboxElement;
import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.component.formlayout.testbench.FormLayoutElement;
import com.vaadin.flow.component.textfield.testbench.PasswordFieldElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link UserForm} element.
 */
@Element("vaadin-form-layout")
@Attribute(name = "id", value = UserForm.ID)
public class UserFormElement extends FormLayoutElement {
  public TextFieldElement email() {
    return $(TextFieldElement.class).id(id(EMAIL));
  }

  public TextFieldElement name() {
    return $(TextFieldElement.class).id(id(NAME));
  }

  public CheckboxElement admin() {
    return $(CheckboxElement.class).id(id(ADMIN));
  }

  public CheckboxElement manager() {
    return $(CheckboxElement.class).id(id(MANAGER));
  }

  private PasswordsFormElement passwords() {
    return $(PasswordsFormElement.class).withAttribute("class", PasswordsForm.CLASS_NAME).first();
  }

  public PasswordFieldElement password() {
    return passwords().password();
  }

  public PasswordFieldElement passwordConfirm() {
    return passwords().passwordConfirm();
  }

  public ComboBoxElement laboratory() {
    return $(ComboBoxElement.class).id(id(LABORATORY));
  }

  public CheckboxElement createNewLaboratory() {
    return $(CheckboxElement.class).id(id(CREATE_NEW_LABORATORY));
  }

  public TextFieldElement newLaboratoryName() {
    return $(TextFieldElement.class).id(id(NEW_LABORATORY_NAME));
  }

  public TextFieldElement address() {
    return $(TextFieldElement.class).id(id(LINE));
  }

  public TextFieldElement town() {
    return $(TextFieldElement.class).id(id(TOWN));
  }

  public TextFieldElement state() {
    return $(TextFieldElement.class).id(id(STATE));
  }

  public TextFieldElement country() {
    return $(TextFieldElement.class).id(id(COUNTRY));
  }

  public TextFieldElement postalCode() {
    return $(TextFieldElement.class).id(id(POSTAL_CODE));
  }

  public ComboBoxElement phoneType() {
    return $(ComboBoxElement.class).id(id(TYPE));
  }

  public TextFieldElement number() {
    return $(TextFieldElement.class).id(id(NUMBER));
  }

  public TextFieldElement extension() {
    return $(TextFieldElement.class).id(id(EXTENSION));
  }
}
