package ca.qc.ircm.proview.submission.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.hene.flexibleoptiongroup.FlexibleOptionGroup;
import org.vaadin.hene.flexibleoptiongroup.FlexibleOptionGroupItemComponent;

import java.util.HashMap;
import java.util.Map;

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
  protected FlexibleOptionGroup digestionFlexibleOptions = new FlexibleOptionGroup();
  protected FlexibleOptionGroup proteinIdentificationFlexibleOptions = new FlexibleOptionGroup();
  private Map<Object, TextField> digestionOptionTextField = new HashMap<>();
  private Map<Object, Label> digestionOptionNoteLabel = new HashMap<>();
  private Map<Object, TextField> proteinIdentificationOptionTextField = new HashMap<>();

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

  /**
   * Creates layout for digestion option.
   *
   * @param itemId
   *          digestion option
   * @return layout for digestion option
   */
  public AbstractLayout createDigestionOptionLayout(Object itemId) {
    VerticalLayout optionTextLayout = new VerticalLayout();
    digestionOptionsLayout.addComponent(optionTextLayout);
    HorizontalLayout optionLayout = new HorizontalLayout();
    optionTextLayout.addComponent(optionLayout);
    FlexibleOptionGroupItemComponent comp = digestionFlexibleOptions.getItemComponent(itemId);
    optionLayout.addComponent(comp);
    Label captionLabel = new Label();
    captionLabel.setStyleName("formcaption");
    captionLabel.setValue(comp.getCaption());
    optionLayout.addComponent(captionLabel);
    HorizontalLayout textAndNoteLayout = new HorizontalLayout();
    textAndNoteLayout.setMargin(new MarginInfo(false, false, false, true));
    textAndNoteLayout.setSpacing(true);
    optionTextLayout.addComponent(textAndNoteLayout);
    FormLayout textLayout = new FormLayout();
    textLayout.setMargin(false);
    textAndNoteLayout.addComponent(textLayout);
    TextField text = new TextField();
    textLayout.addComponent(text);
    digestionOptionTextField.put(itemId, text);
    FormLayout noteLayout = new FormLayout();
    noteLayout.setMargin(false);
    textAndNoteLayout.addComponent(noteLayout);
    Label note = new Label();
    note.setStyleName("formcaption");
    noteLayout.addComponent(note);
    digestionOptionNoteLabel.put(itemId, note);
    return optionTextLayout;
  }

  public TextField getDigestionOptionTextField(Object itemId) {
    return digestionOptionTextField.get(itemId);
  }

  public AbstractLayout getDigestionOptionTextLayout(Object itemId) {
    return digestionOptionTextField.get(itemId).findAncestor(HorizontalLayout.class);
  }

  public Label getDigestionOptionNoteLabel(Object itemId) {
    return digestionOptionNoteLabel.get(itemId);
  }

  /**
   * Creates layout for protein identification option.
   *
   * @param itemId
   *          protein identification option
   * @return layout for protein identification option
   */
  public AbstractLayout createProteinIdentificationOptionLayout(Object itemId) {
    VerticalLayout optionTextLayout = new VerticalLayout();
    proteinIdentificationOptionsLayout.addComponent(optionTextLayout);
    HorizontalLayout optionLayout = new HorizontalLayout();
    optionTextLayout.addComponent(optionLayout);
    FlexibleOptionGroupItemComponent comp =
        proteinIdentificationFlexibleOptions.getItemComponent(itemId);
    optionLayout.addComponent(comp);
    Label captionLabel = new Label();
    captionLabel.setStyleName("formcaption");
    captionLabel.setValue(comp.getCaption());
    optionLayout.addComponent(captionLabel);
    FormLayout textLayout = new FormLayout();
    textLayout.setMargin(new MarginInfo(false, false, false, true));
    optionTextLayout.addComponent(textLayout);
    TextField text = new TextField();
    textLayout.addComponent(text);
    proteinIdentificationOptionTextField.put(itemId, text);
    return optionTextLayout;
  }

  public TextField getProteinIdentificationOptionTextField(Object itemId) {
    return proteinIdentificationOptionTextField.get(itemId);
  }

  public AbstractLayout getProteinIdentificationOptionTextLayout(Object itemId) {
    return proteinIdentificationOptionTextField.get(itemId).findAncestor(FormLayout.class);
  }
}
