package ca.qc.ircm.proview.submission.web;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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
public class SubmissionsViewDesign extends VerticalLayout {
  protected Label headerLabel;
  protected Grid<ca.qc.ircm.proview.submission.Submission> submissionsGrid;
  protected Button addSubmission;
  protected HorizontalLayout sampleSelectionLayout;
  protected Button selectSamplesButton;
  protected Label selectedSamplesLabel;
  protected HorizontalLayout containerSelectionLayout;
  protected Button selectContainers;
  protected Label selectedContainersLabel;
  protected Button updateStatusButton;
  protected HorizontalLayout treatmentButtons;
  protected Button transfer;
  protected Button digestion;
  protected Button enrichment;
  protected Button solubilisation;
  protected Button dilution;
  protected Button standardAddition;
  protected HorizontalLayout analysisButtons;
  protected Button msAnalysis;
  protected Button dataAnalysis;

  public SubmissionsViewDesign() {
    Design.read(this);
  }
}
