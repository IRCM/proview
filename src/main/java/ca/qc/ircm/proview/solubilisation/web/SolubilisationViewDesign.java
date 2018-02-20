package ca.qc.ircm.proview.solubilisation.web;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
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
public class SolubilisationViewDesign extends VerticalLayout {
  protected Label header;
  protected Label deleted;
  protected Panel solubilisationsPanel;
  protected Grid<ca.qc.ircm.proview.solubilisation.SolubilisedSample> solubilisations;
  protected Button down;
  protected Panel explanationPanel;
  protected TextArea explanation;
  protected Button save;
  protected HorizontalLayout removeLayout;
  protected Button remove;
  protected CheckBox banContainers;

  public SolubilisationViewDesign() {
    Design.read(this);
  }
}
