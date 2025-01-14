package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.web.PasswordsForm.PASSWORD;
import static ca.qc.ircm.proview.user.web.PasswordsForm.PASSWORD_CONFIRM;

import com.vaadin.flow.component.formlayout.testbench.FormLayoutElement;
import com.vaadin.flow.component.textfield.testbench.PasswordFieldElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link PasswordsForm} element.
 */
@Element("vaadin-form-layout")
@Attribute(name = "class", value = PasswordsForm.CLASS_NAME)
public class PasswordsFormElement extends FormLayoutElement {
  public PasswordFieldElement password() {
    return $(PasswordFieldElement.class).withAttribute("class", PASSWORD).first();
  }

  public PasswordFieldElement passwordConfirm() {
    return $(PasswordFieldElement.class).withAttribute("class", PASSWORD_CONFIRM).first();
  }
}
