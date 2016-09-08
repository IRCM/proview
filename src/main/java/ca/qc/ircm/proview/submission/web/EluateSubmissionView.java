package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import ca.qc.ircm.proview.web.Menu;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Eluate sample submission view.
 */
@SpringView(name = EluateSubmissionView.VIEW_NAME)
public class EluateSubmissionView extends EluateSubmissionViewDesign
    implements MessageResourcesView {
  public static final String VIEW_NAME = "submission/eluate";
  private static final long serialVersionUID = 7586918222688019429L;
  @Inject
  private EluateSubmissionViewPresenter presenter;

  @PostConstruct
  public void init() {
    presenter.init(this);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.attach();
  }

  public void setTitle(String title) {
    getUI().getPage().setTitle(title);
  }

  public void showError(String message) {
    Notification.show(message, Notification.Type.ERROR_MESSAGE);
  }

  public Menu getMenu() {
    return menu;
  }

  public Label getHeaderLabel() {
    return headerLabel;
  }

  public Label getServiceLabel() {
    return serviceLabel;
  }

  public Label getInformationLabel() {
    return informationLabel;
  }

  public Panel getSampleCountPanel() {
    return sampleCountPanel;
  }

  public TextField getSampleCountField() {
    return sampleCountField;
  }

  public Panel getSampleNamesPanel() {
    return sampleNamesPanel;
  }

  public FormLayout getSampleNamesForm() {
    return sampleNamesForm;
  }

  public TextField getSampleNameField() {
    return sampleNameField;
  }

  public Button getFillSampleNameButton() {
    return fillSampleNameButton;
  }

  public Panel getExperiencePanel() {
    return experiencePanel;
  }

  public ComboBox getProjectField() {
    return projectField;
  }

  public TextField getExperienceField() {
    return experienceField;
  }

  public TextField getExperienceGoalField() {
    return experienceGoalField;
  }

  public Panel getSampleDetailsPanel() {
    return sampleDetailsPanel;
  }

  public TextField getTaxonomyField() {
    return taxonomyField;
  }

  public TextField getProteinNameField() {
    return proteinNameField;
  }

  public TextField getProteinWeightField() {
    return proteinWeightField;
  }

  public TextField getPostTranslationModificationField() {
    return postTranslationModificationField;
  }

  public TextField getSampleVolumeField() {
    return sampleVolumeField;
  }

  public TextField getSampleQuantityField() {
    return sampleQuantityField;
  }

  public Panel getStandardsPanel() {
    return standardsPanel;
  }

  public Panel getContaminantsPanel() {
    return contaminantsPanel;
  }

  public Panel getDigestionPanel() {
    return digestionPanel;
  }

  public OptionGroup getDigestionOptions() {
    return digestionOptions;
  }

  public Panel getEnrichmentPanel() {
    return enrichmentPanel;
  }

  public Panel getExclusionsPanel() {
    return exclusionsPanel;
  }

  public Panel getInstrumentPanel() {
    return instrumentPanel;
  }

  public OptionGroup getInstrumentOptions() {
    return instrumentOptions;
  }

  public Panel getProteinIdentificationPanel() {
    return proteinIdentificationPanel;
  }

  public OptionGroup getProteinIdentificationOptions() {
    return proteinIdentificationOptions;
  }

  public Panel getCommentsPanel() {
    return commentsPanel;
  }

  public TextArea getCommentsField() {
    return commentsField;
  }
}
