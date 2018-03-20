package ca.qc.ircm.proview.sample.web;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
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
public class ControlFormDesign extends VerticalLayout {
  protected Panel samplePanel;
  protected TextField nameField;
  protected ComboBox<ca.qc.ircm.proview.sample.SampleType> type;
  protected TextField volumeField;
  protected TextField quantityField;
  protected ComboBox<ca.qc.ircm.proview.sample.ControlType> controlTypeField;
  protected Panel standardsPanel;
  protected Panel explanationPanel;
  protected TextArea explanation;
  protected Button saveButton;

  public ControlFormDesign() {
    Design.read(this);
  }
}
