package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.web.Passwords.NOT_MATCH;
import static ca.qc.ircm.proview.user.web.PasswordsProperties.CONFIRM_PASSWORD;
import static ca.qc.ircm.proview.user.web.PasswordsProperties.PASSWORD;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordService;
import ca.qc.ircm.proview.web.SigninView;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.PostConstruct;
import java.io.Serial;
import java.util.Locale;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Use forgot password view.
 */
@Route(value = UseForgotPasswordView.VIEW_NAME)
@AnonymousAllowed
public class UseForgotPasswordView extends VerticalLayout implements LocaleChangeObserver,
    HasUrlParameter<String>, HasDynamicTitle, NotificationComponent {

  public static final String VIEW_NAME = "useforgotpassword";
  public static final String ID = "useforgotpassword-view";
  public static final String SEPARATOR = "/";
  public static final String HEADER = "header";
  public static final String MESSAGE = "message";
  public static final String SAVED = "saved";
  public static final String INVALID = "invalid";
  private static final String MESSAGES_PREFIX = messagePrefix(UseForgotPasswordView.class);
  private static final String PASSWORDS_PREFIX = messagePrefix(Passwords.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @Serial
  private static final long serialVersionUID = 4760310643370830640L;
  private static final Logger logger = LoggerFactory.getLogger(UseForgotPasswordView.class);
  protected H2 header = new H2();
  protected Div message = new Div();
  protected HorizontalLayout buttonsLayout = new HorizontalLayout();
  protected Button save = new Button();
  protected FormLayout form = new FormLayout();
  protected PasswordField password = new PasswordField();
  protected PasswordField confirmPassword = new PasswordField();
  private ForgotPassword forgotPassword;
  private final Binder<Passwords> passwordBinder = new BeanValidationBinder<>(Passwords.class);
  private final transient ForgotPasswordService service;

  @Autowired
  protected UseForgotPasswordView(ForgotPasswordService service) {
    this.service = service;
  }

  /**
   * Initializes view.
   */
  @PostConstruct
  protected void init() {
    logger.debug("use forgot password view");
    setId(ID);
    add(header, message, form, buttonsLayout);
    buttonsLayout.add(save);
    header.setId(HEADER);
    message.setId(MESSAGE);
    form.add(password, confirmPassword);
    password.setId(PASSWORD);
    confirmPassword.setId(CONFIRM_PASSWORD);
    passwordBinder.setBean(new Passwords());
    save.setId(SAVE);
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    save.setIcon(VaadinIcon.CHECK.create());
    save.addClickListener(e -> save());
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    header.setText(getTranslation(MESSAGES_PREFIX + HEADER));
    message.setText(getTranslation(MESSAGES_PREFIX + MESSAGE));
    password.setLabel(getTranslation(PASSWORDS_PREFIX + PASSWORD));
    confirmPassword.setLabel(getTranslation(PASSWORDS_PREFIX + CONFIRM_PASSWORD));
    passwordBinder.forField(password).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withValidator(password -> {
          String confirmPassword = this.confirmPassword.getValue();
          return password.isEmpty() || confirmPassword.isEmpty() || password.equals(
              confirmPassword);
        }, getTranslation(PASSWORDS_PREFIX + NOT_MATCH)).bind(PASSWORD);
    passwordBinder.forField(confirmPassword).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .bind(CONFIRM_PASSWORD);
    save.setText(getTranslation(CONSTANTS_PREFIX + SAVE));
  }

  @Override
  public String getPageTitle() {
    return getTranslation(MESSAGES_PREFIX + TITLE,
        getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME));
  }

  private boolean validateParameter(String parameter, Locale locale) {
    String[] parameters = parameter.split(SEPARATOR, -1);
    boolean valid = true;
    if (parameters.length < 2) {
      valid = false;
    } else {
      try {
        long id = Long.parseLong(parameters[0]);
        String confirmNumber = parameters[1];
        if (service.get(id, confirmNumber).isEmpty()) {
          valid = false;
        }
      } catch (NumberFormatException e) {
        valid = false;
      }
    }
    if (!valid) {
      showNotification(getTranslation(MESSAGES_PREFIX + INVALID));
    }
    return valid;
  }

  @Override
  public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
    boolean valid = validateParameter(parameter, getLocale());
    if (valid) {
      String[] parameters = parameter.split(SEPARATOR, -1);
      long id = Long.parseLong(parameters[0]);
      String confirmNumber = parameters[1];
      forgotPassword = service.get(id, confirmNumber).orElseThrow();
    }
    save.setEnabled(valid);
    form.setEnabled(valid);
  }

  BinderValidationStatus<Passwords> validate() {
    return passwordBinder.validate();
  }

  void save() {
    if (validate().isOk()) {
      String password = this.password.getValue();
      logger.debug("save new password for user {}", forgotPassword.getUser());
      service.updatePassword(forgotPassword, Objects.requireNonNull(password));
      showNotification(getTranslation(MESSAGES_PREFIX + SAVED));
      UI.getCurrent().navigate(SigninView.class);
    }
  }
}
