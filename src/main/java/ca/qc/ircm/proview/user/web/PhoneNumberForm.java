package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

/**
 * Phone number form.
 */
public class PhoneNumberForm extends PhoneNumberFormDesign implements MessageResourcesComponent {
  private static final long serialVersionUID = -3211885433958000037L;

  private PhoneNumberFormPresenter presenter;

  public void setPresenter(PhoneNumberFormPresenter presenter) {
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

  public void setHeader(Label header) {
    this.header = header;
  }

  public ComboBox getTypeField() {
    return typeField;
  }

  public TextField getNumberField() {
    return numberField;
  }

  public TextField getExtensionField() {
    return extensionField;
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
