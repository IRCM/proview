package ca.qc.ircm.proview.dilution.web;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
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
public class DilutionViewDesign extends VerticalLayout {
  protected Label header;
  protected Panel dilutionsPanel;
  protected Grid<ca.qc.ircm.proview.dilution.DilutedSample> dilutions;
  protected Button down;
  protected Button save;

  public DilutionViewDesign() {
    Design.read(this);
  }
}
