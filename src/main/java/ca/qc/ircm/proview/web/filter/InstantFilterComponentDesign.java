package ca.qc.ircm.proview.web.filter;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import org.vaadin.hene.popupbutton.PopupButton;

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
public class InstantFilterComponentDesign extends VerticalLayout {
  protected PopupButton filter;
  protected VerticalLayout popup;
  protected InlineDateField from;
  protected InlineDateField to;
  protected Button set;
  protected Button clear;

  public InstantFilterComponentDesign() {
    Design.read(this);
  }
}
