package ca.qc.ircm.proview.submission.web;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
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
  protected Label sampleTypeWarning;
  protected Label inactiveWarning;
  protected Panel servicePanel;
  protected RadioButtonGroup<ca.qc.ircm.proview.submission.Service> service;
  protected Panel samplesPanel;
  protected RadioButtonGroup<ca.qc.ircm.proview.sample.SampleSupport> sampleSupport;
  protected TextField solutionSolvent;
  protected TextField sampleCount;
  protected TextField sampleName;
  protected TextField formula;
  protected TextField monoisotopicMass;
  protected TextField averageMass;
  protected TextField toxicity;
  protected CheckBox lightSensitive;
  protected RadioButtonGroup<ca.qc.ircm.proview.submission.StorageTemperature> storageTemperature;
  protected RadioButtonGroup<ca.qc.ircm.proview.sample.SampleContainerType> sampleContainerType;
  protected TextField plateName;
  protected Label samplesLabel;
  protected HorizontalLayout samplesLayout;
  protected Grid<ca.qc.ircm.proview.sample.SubmissionSample> samples;
  protected Button fillSamples;
  protected VerticalLayout samplesPlateContainer;
  protected Panel experiencePanel;
  protected TextField experience;
  protected TextField experienceGoal;
  protected TextField taxonomy;
  protected TextField proteinName;
  protected TextField proteinWeight;
  protected TextField postTranslationModification;
  protected TextField sampleQuantity;
  protected TextField sampleVolume;
  protected Panel standardsPanel;
  protected TextField standardCount;
  protected HorizontalLayout standardsLayout;
  protected Grid<ca.qc.ircm.proview.sample.Standard> standards;
  protected Button fillStandards;
  protected Panel contaminantsPanel;
  protected TextField contaminantCount;
  protected HorizontalLayout contaminantsLayout;
  protected Grid<ca.qc.ircm.proview.sample.Contaminant> contaminants;
  protected Button fillContaminants;
  protected Panel gelPanel;
  protected ComboBox<ca.qc.ircm.proview.submission.GelSeparation> separation;
  protected ComboBox<ca.qc.ircm.proview.submission.GelThickness> thickness;
  protected ComboBox<ca.qc.ircm.proview.submission.GelColoration> coloration;
  protected TextField otherColoration;
  protected TextField developmentTime;
  protected CheckBox decoloration;
  protected TextField weightMarkerQuantity;
  protected TextField proteinQuantity;
  protected Panel servicesPanel;
  protected RadioButtonGroup<ca.qc.ircm.proview.sample.ProteolyticDigestion> digestion;
  protected TextField usedProteolyticDigestionMethod;
  protected TextField otherProteolyticDigestionMethod;
  protected Label otherProteolyticDigestionMethodNote;
  protected Label enrichment;
  protected Label exclusions;
  protected RadioButtonGroup<ca.qc.ircm.proview.msanalysis.InjectionType> injectionType;
  protected RadioButtonGroup<ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource> source;
  protected RadioButtonGroup<ca.qc.ircm.proview.submission.ProteinContent> proteinContent;
  protected RadioButtonGroup<ca.qc.ircm.proview.msanalysis.MassDetectionInstrument> instrument;
  protected RadioButtonGroup<ca.qc.ircm.proview.sample.ProteinIdentification> proteinIdentification;
  protected TextField proteinIdentificationLink;
  protected RadioButtonGroup<ca.qc.ircm.proview.submission.Quantification> quantification;
  protected TextArea quantificationComment;
  protected RadioButtonGroup<java.lang.Boolean> highResolution;
  protected VerticalLayout solventsLayout;
  protected CheckBox acetonitrileSolvents;
  protected CheckBox methanolSolvents;
  protected CheckBox chclSolvents;
  protected CheckBox otherSolvents;
  protected TextField otherSolvent;
  protected Label otherSolventNote;
  protected Panel commentPanel;
  protected TextArea comment;
  protected Panel filesPanel;
  protected Label gelImageFile;
  protected Label structureFile;
  protected VerticalLayout filesUploaderLayout;
  protected Grid<ca.qc.ircm.proview.submission.SubmissionFile> files;
  protected Panel explanationPanel;
  protected TextArea explanation;
  protected HorizontalLayout buttons;
  protected Button save;

  public SubmissionFormDesign() {
    Design.read(this);
  }
}
