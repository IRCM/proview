package ca.qc.ircm.proview.submission.web;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.v7.ui.Table;

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
  protected Label sampleTypeLabel;
  protected Label inactiveLabel;
  protected Panel servicePanel;
  protected RadioButtonGroup<ca.qc.ircm.proview.submission.Service> serviceOptions;
  protected Panel samplesPanel;
  protected RadioButtonGroup<ca.qc.ircm.proview.sample.SampleSupport> sampleSupportOptions;
  protected TextField solutionSolventField;
  protected TextField sampleCountField;
  protected TextField sampleNameField;
  protected TextField formulaField;
  protected VerticalLayout structureLayout;
  protected Button structureButton;
  protected VerticalLayout structureUploaderLayout;
  protected TextField monoisotopicMassField;
  protected TextField averageMassField;
  protected TextField toxicityField;
  protected CheckBox lightSensitiveField;
  protected RadioButtonGroup<ca.qc.ircm.proview.submission.StorageTemperature> storageTemperatureOptions;
  protected RadioButtonGroup<ca.qc.ircm.proview.sample.SampleContainerType> sampleContainerTypeOptions;
  protected TextField plateNameField;
  protected Label samplesLabel;
  protected HorizontalLayout samplesTableLayout;
  protected Table samplesTable;
  protected Button fillSamplesButton;
  protected VerticalLayout samplesPlateContainer;
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
  protected ComboBox<ca.qc.ircm.proview.submission.GelSeparation> separationField;
  protected ComboBox<ca.qc.ircm.proview.submission.GelThickness> thicknessField;
  protected ComboBox<ca.qc.ircm.proview.submission.GelColoration> colorationField;
  protected TextField otherColorationField;
  protected TextField developmentTimeField;
  protected CheckBox decolorationField;
  protected TextField weightMarkerQuantityField;
  protected TextField proteinQuantityField;
  protected HorizontalLayout gelImagesLayout;
  protected VerticalLayout gelImagesUploaderLayout;
  protected Table gelImagesTable;
  protected Panel servicesPanel;
  protected RadioButtonGroup<ca.qc.ircm.proview.sample.ProteolyticDigestion> digestionOptions;
  protected TextField usedProteolyticDigestionMethodField;
  protected TextField otherProteolyticDigestionMethodField;
  protected Label otherProteolyticDigestionMethodNote;
  protected Label enrichmentLabel;
  protected Label exclusionsLabel;
  protected RadioButtonGroup<ca.qc.ircm.proview.msanalysis.InjectionType> injectionTypeOptions;
  protected RadioButtonGroup<ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource> sourceOptions;
  protected RadioButtonGroup<ca.qc.ircm.proview.submission.ProteinContent> proteinContentOptions;
  protected RadioButtonGroup<ca.qc.ircm.proview.msanalysis.MassDetectionInstrument> instrumentOptions;
  protected RadioButtonGroup<ca.qc.ircm.proview.sample.ProteinIdentification> proteinIdentificationOptions;
  protected TextField proteinIdentificationLinkField;
  protected RadioButtonGroup<ca.qc.ircm.proview.submission.Quantification> quantificationOptions;
  protected TextArea quantificationLabelsField;
  protected RadioButtonGroup<java.lang.Boolean> highResolutionOptions;
  protected VerticalLayout solventsLayout;
  protected CheckBox acetonitrileSolventsField;
  protected CheckBox methanolSolventsField;
  protected CheckBox chclSolventsField;
  protected CheckBox otherSolventsField;
  protected TextField otherSolventField;
  protected Label otherSolventNoteLabel;
  protected Panel commentsPanel;
  protected TextArea commentsField;
  protected Panel filesPanel;
  protected VerticalLayout filesUploaderLayout;
  protected Table filesTable;
  protected HorizontalLayout buttonsLayout;
  protected Button submitButton;

  public SubmissionFormDesign() {
    Design.read(this);
  }
}
