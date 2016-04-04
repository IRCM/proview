package ca.qc.ircm.proview.laboratory.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

/**
 * Laboratory form.
 */
public class LaboratoryForm extends LaboratoryFormDesign implements MessageResourcesComponent {
  private static final long serialVersionUID = 6501017653094381754L;
  private LaboratoryFormPresenter presenter;

  public void setPresenter(LaboratoryFormPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.attach();
  }

  public Label getHeader() {
    return header;
  }

  public TextField getOrganizationField() {
    return organizationField;
  }

  public TextField getNameField() {
    return nameField;
  }

  public HorizontalLayout getButtonsLayout() {
    return buttonsLayout;
  }

  public Button getSaveButton() {
    return saveButton;
  }

  public Button getCancelButton() {
    return cancelButton;
  }
}
