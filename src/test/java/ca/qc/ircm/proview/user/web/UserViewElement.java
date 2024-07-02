package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.user.web.UserView.HEADER;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link UserView} element.
 */
@Element("vaadin-vertical-layout")
@Attribute(name = "id", value = UserView.ID)
public class UserViewElement extends VerticalLayoutElement {
  public H2Element header() {
    return $(H2Element.class).id(HEADER);
  }

  public UserFormElement userForm() {
    return $(UserFormElement.class).first();
  }

  public ButtonElement save() {
    return $(ButtonElement.class).id(SAVE);
  }
}
