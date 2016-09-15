package ca.qc.ircm.proview.submission.web;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
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
public class EluateSubmissionViewDesign extends VerticalLayout {
  protected Label headerLabel;
  protected Label serviceLabel;
  protected Label sampleTypeLabel;
  protected Label inactiveLabel;
  protected Panel sampleCountPanel;
  protected TextField sampleCountField;
  protected Panel sampleNamesPanel;
  protected FormLayout sampleNamesForm;
  protected TextField sampleNameField;
  protected Button fillSampleNamesButton;
  protected Panel experiencePanel;
  protected TextField experienceField;
  protected TextField experienceGoalField;
  protected TextField taxonomyField;
  protected TextField proteinNameField;
  protected TextField proteinWeightField;
  protected TextField postTranslationModificationField;
  protected TextField sampleQuantityField;
  protected TextField sampleVolumeField;
  protected Panel standardsPanel;
  protected TextField standardCountField;
  protected HorizontalLayout standardsTableLayout;
  protected Table standardsTable;
  protected Button fillStandardsButton;
  protected Panel contaminantsPanel;
  protected TextField contaminantCountField;
  protected HorizontalLayout contaminantsTableLayout;
  protected Table contaminantsTable;
  protected Button fillContaminantsButton;
  protected Panel servicesPanel;
  protected VerticalLayout digestionOptionsLayout;
  protected Label enrichmentLabel;
  protected Label exclusionsLabel;
  protected OptionGroup instrumentOptions;
  protected VerticalLayout proteinIdentificationOptionsLayout;
  protected Panel commentsPanel;
  protected TextArea commentsField;
  protected Button submitButton;

  public EluateSubmissionViewDesign() {
    Design.read(this);
  }
}
