package ca.qc.ircm.proview.submission.web;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
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
public class SubmissionFormDesign extends VerticalLayout {
  protected Label headerLabel;
  protected Label sampleTypeLabel;
  protected Label inactiveLabel;
  protected Panel servicePanel;
  protected OptionGroup serviceOptions;
  protected Panel samplesPanel;
  protected OptionGroup sampleSupportOptions;
  protected TextField solutionSolventField;
  protected TextField sampleCountField;
  protected TextField sampleNameField;
  protected TextField formulaField;
  protected VerticalLayout structureLayout;
  protected Button structureButton;
  protected VerticalLayout structureUploaderLayout;
  protected ProgressBar structureProgress;
  protected TextField monoisotopicMassField;
  protected TextField averageMassField;
  protected TextField toxicityField;
  protected CheckBox lightSensitiveField;
  protected OptionGroup storageTemperatureOptions;
  protected HorizontalLayout sampleNamesLayout;
  protected Table sampleNamesTable;
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
  protected Panel gelPanel;
  protected ComboBox separationField;
  protected ComboBox thicknessField;
  protected ComboBox colorationField;
  protected TextField otherColorationField;
  protected TextField developmentTimeField;
  protected CheckBox decolorationField;
  protected TextField weightMarkerQuantityField;
  protected TextField proteinQuantityField;
  protected HorizontalLayout gelImagesLayout;
  protected VerticalLayout gelImagesUploaderLayout;
  protected ProgressBar gelImageProgress;
  protected Table gelImagesTable;
  protected Panel servicesPanel;
  protected OptionGroup digestionOptions;
  protected TextField usedProteolyticDigestionMethodField;
  protected TextField otherProteolyticDigestionMethodField;
  protected Label otherProteolyticDigestionMethodNote;
  protected Label enrichmentLabel;
  protected Label exclusionsLabel;
  protected TextField sampleNumberProteinField;
  protected OptionGroup sourceOptions;
  protected OptionGroup instrumentOptions;
  protected OptionGroup proteinIdentificationOptions;
  protected TextField proteinIdentificationLinkField;
  protected OptionGroup quantificationOptions;
  protected TextArea quantificationLabelsField;
  protected OptionGroup highResolutionOptions;
  protected VerticalLayout solventsLayout;
  protected CheckBox acetonitrileSolventsField;
  protected CheckBox methanolSolventsField;
  protected CheckBox chclSolventsField;
  protected CheckBox otherSolventsField;
  protected TextField otherSolventField;
  protected Label otherSolventNoteLabel;
  protected Panel commentsPanel;
  protected TextArea commentsField;
  protected HorizontalLayout buttonsLayout;
  protected Button submitButton;

  public SubmissionFormDesign() {
    Design.read(this);
  }
}
