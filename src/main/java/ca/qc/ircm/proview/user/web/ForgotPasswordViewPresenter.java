package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.GENERAL_MESSAGES;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordService;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * Forgot password view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ForgotPasswordViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String PASSWORD_PANEL = "passwordPanel";
  public static final String PASSWORD = "password";
  public static final String CONFIRM_PASSWORD = "confirmPassword";
  public static final String SAVE = "save";
  public static final String INVALID_FORGOT_PASSWORD = "invalidForgotPassword";
  private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordViewPresenter.class);
  private ForgotPasswordView view;
  private PropertysetItem passwordItem = new PropertysetItem();
  private FieldGroup passwordFieldGroup = new FieldGroup();
  private ForgotPassword forgotPassword;
  @Inject
  private ForgotPasswordService forgotPasswordService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected ForgotPasswordViewPresenter() {
  }

  protected ForgotPasswordViewPresenter(ForgotPasswordService forgotPasswordService,
      String applicationName) {
    this.forgotPasswordService = forgotPasswordService;
    this.applicationName = applicationName;
  }

  /**
   * Called by view when view is attached.
   *
   * @param view
   *          view
   */
  public void init(ForgotPasswordView view) {
    this.view = view;
    prepareFields();
    bindFields();
    addListeners();
  }

  private void prepareFields() {
    MessageResource resources = view.getResources();
    final MessageResource generalResources =
        new MessageResource(GENERAL_MESSAGES, view.getLocale());
    view.setTitle(resources.message(TITLE, applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.addStyleName("h1");
    view.headerLabel.setValue(resources.message(HEADER));
    passwordItem.addItemProperty(PASSWORD, new ObjectProperty<>(null, String.class));
    passwordItem.addItemProperty(CONFIRM_PASSWORD, new ObjectProperty<>(null, String.class));
    passwordFieldGroup.setItemDataSource(passwordItem);
    view.passwordPanel.addStyleName(PASSWORD_PANEL);
    view.passwordPanel.setCaption(resources.message(PASSWORD_PANEL));
    view.passwordField.addStyleName(PASSWORD);
    view.passwordField.setCaption(resources.message(PASSWORD));
    view.passwordField.setNullRepresentation("");
    view.passwordField.setRequired(true);
    view.passwordField.setRequiredError(generalResources.message(REQUIRED));
    view.passwordField.addValidator((value) -> {
      String password = view.passwordField.getValue();
      String confirmPassword = view.confirmPasswordField.getValue();
      if (password != null && !password.isEmpty() && confirmPassword != null
          && !confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
        throw new InvalidValueException(resources.message(PASSWORD + ".notMatch"));
      }
    });
    view.confirmPasswordField.addStyleName(CONFIRM_PASSWORD);
    view.confirmPasswordField.setCaption(resources.message(CONFIRM_PASSWORD));
    view.confirmPasswordField.setNullRepresentation("");
    view.confirmPasswordField.setRequired(true);
    view.confirmPasswordField.setRequiredError(generalResources.message(REQUIRED));
    view.saveButton.addStyleName(SAVE);
    view.saveButton.setCaption(resources.message(SAVE));
  }

  private void bindFields() {
    passwordFieldGroup.bind(view.passwordField, PASSWORD);
    passwordFieldGroup.bind(view.confirmPasswordField, CONFIRM_PASSWORD);
  }

  private void addListeners() {
    view.confirmPasswordField.addValueChangeListener(e -> {
      view.passwordField.isValid();
      view.passwordField.markAsDirty();
    });
    view.saveButton.addClickListener(e -> save());
  }

  private boolean validate() {
    if (forgotPassword == null) {
      view.showError(view.getResources().message(INVALID_FORGOT_PASSWORD));
      return false;
    }
    try {
      passwordFieldGroup.commit();
    } catch (CommitException e) {
      final MessageResource generalResources =
          new MessageResource(GENERAL_MESSAGES, view.getLocale());
      logger.trace("Validation {} failed with message {}",
          e instanceof CommitException ? "commit" : "value", e.getMessage(), e);
      view.showError(generalResources.message(FIELD_NOTIFICATION));
      return false;
    }

    return true;
  }

  private void save() {
    if (validate()) {
      String password = view.passwordField.getValue();
      forgotPasswordService.updatePassword(forgotPassword, password);
      MessageResource resources = view.getResources();
      view.showTrayNotification(resources.message("save.done"));
      view.navigateTo(MainView.VIEW_NAME);
    }
  }

  /**
   * Called by view when view is entered.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    forgotPassword = null;
    if (parameters != null && !parameters.isEmpty()) {
      String[] values = parameters.split("/", -1);
      if (values.length == 2) {
        try {
          Long id = Long.valueOf(values[0]);
          Integer confirmNumber = Integer.valueOf(values[1]);
          forgotPassword = forgotPasswordService.get(id, confirmNumber);
        } catch (NumberFormatException e) {
          forgotPassword = null;
        }
      }
    } else {
      forgotPassword = null;
    }

    if (forgotPassword == null) {
      view.showError(view.getResources().message(INVALID_FORGOT_PASSWORD));
    }
  }
}
