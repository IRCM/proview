package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.SAVE;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link UserView} element.
 */
@Element("vaadin-vertical-layout")
@Attribute(name = "id", value = UserView.ID)
public class UserViewElement extends VerticalLayoutElement {
  public UserFormElement userForm() {
    return $(UserFormElement.class).first();
  }

  public ButtonElement save() {
    return $(ButtonElement.class).id(SAVE);
  }
}
