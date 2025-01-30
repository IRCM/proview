package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.INVALID_EMAIL;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.user.ForgotPasswordService;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.SigninView;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import ca.qc.ircm.proview.web.component.UrlComponent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.PostConstruct;
import java.io.Serial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Forgot password view.
 */
@Route(value = ForgotPasswordView.VIEW_NAME)
@AnonymousAllowed
public class ForgotPasswordView extends VerticalLayout
    implements LocaleChangeObserver, HasDynamicTitle, NotificationComponent, UrlComponent {

  public static final String VIEW_NAME = "forgotpassword";
  public static final String ID = "forgotpassword-view";
  public static final String SEPARATOR = "/";
  public static final String HEADER = "header";
  public static final String MESSAGE = "message";
  public static final String SAVED = "saved";
  private static final String MESSAGES_PREFIX = messagePrefix(ForgotPasswordView.class);
  private static final String USER_PREFIX = messagePrefix(User.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @Serial
  private static final long serialVersionUID = 4760310643370830640L;
  private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordView.class);
  protected H2 header = new H2();
  protected Div message = new Div();
  protected TextField email = new TextField();
  protected HorizontalLayout buttonsLayout = new HorizontalLayout();
  protected Button save = new Button();
  private Binder<User> binder = new BeanValidationBinder<>(User.class);
  private transient ForgotPasswordService service;
  private transient UserService userService;

  @Autowired
  protected ForgotPasswordView(ForgotPasswordService service, UserService userService) {
    this.service = service;
    this.userService = userService;
  }

  /**
   * Initializes view.
   */
  @PostConstruct
  protected void init() {
    logger.debug("forgot password view");
    setId(ID);
    FormLayout emailLayout = new FormLayout();
    emailLayout.add(email);
    add(header, message, emailLayout, buttonsLayout);
    buttonsLayout.add(save);
    header.setId(HEADER);
    message.setId(MESSAGE);
    email.setId(EMAIL);
    save.setId(SAVE);
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    save.setIcon(VaadinIcon.CHECK.create());
    save.addClickListener(e -> save());
    binder.setBean(new User());
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    header.setText(getTranslation(MESSAGES_PREFIX + HEADER));
    message.setText(getTranslation(MESSAGES_PREFIX + MESSAGE));
    email.setLabel(getTranslation(USER_PREFIX + EMAIL));
    save.setText(getTranslation(CONSTANTS_PREFIX + SAVE));
    binder.forField(email).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("")
        .withValidator(new EmailValidator(getTranslation(CONSTANTS_PREFIX + INVALID_EMAIL)))
        .bind(EMAIL);
  }

  @Override
  public String getPageTitle() {
    return getTranslation(MESSAGES_PREFIX + TITLE,
        getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME));
  }

  BinderValidationStatus<User> validateUser() {
    return binder.validate();
  }

  boolean validate() {
    return validateUser().isOk();
  }

  void save() {
    if (validate()) {
      String email = this.email.getValue();
      logger.debug("create new forgot password for user {}", email);
      if (userService.exists(email)) {
        service.insert(email, (fp, locale) -> getUrl(UseForgotPasswordView.VIEW_NAME) + "/"
            + fp.getId() + UseForgotPasswordView.SEPARATOR + fp.getConfirmNumber());
      }
      showNotification(getTranslation(MESSAGES_PREFIX + SAVED, email));
      UI.getCurrent().navigate(SigninView.class);
    }
  }
}
