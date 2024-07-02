package ca.qc.ircm.proview.web;

import com.vaadin.flow.component.login.testbench.LoginOverlayElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link SigninView} element.
 */
@Element("vaadin-login-overlay")
@Attribute(name = "id", value = SigninView.ID)
public class SigninViewElement extends LoginOverlayElement {
}
