package ca.qc.ircm.proview.sample.web;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
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
public class SampleSelectionFormDesign extends VerticalLayout {
  protected Panel samplesPanel;
  protected Grid<ca.qc.ircm.proview.sample.SubmissionSample> samplesGrid;
  protected Panel controlsPanel;
  protected Grid<ca.qc.ircm.proview.sample.Control> controlsGrid;
  protected Button selectButton;
  protected Button clearButton;

  public SampleSelectionFormDesign() {
    Design.read(this);
  }
}
