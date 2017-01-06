package ca.qc.ircm.proview.user.web;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
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
public class ForgotPasswordViewDesign extends VerticalLayout {
  protected VerticalLayout menuLayout;
  protected Label headerLabel;
  protected Panel passwordPanel;
  protected PasswordField passwordField;
  protected PasswordField confirmPasswordField;
  protected Button saveButton;

  public ForgotPasswordViewDesign() {
    Design.read(this);
  }
}