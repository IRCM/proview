package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.HASHED_PASSWORD;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;

import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginI18n.ErrorMessage;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Signin view.
 */
@Route(value = SigninView.VIEW_NAME)
@HtmlImport("styles/shared-styles.html")
public class SigninView extends LoginOverlay
    implements LocaleChangeObserver, HasDynamicTitle, AfterNavigationObserver, BeforeEnterObserver {
  public static final String VIEW_NAME = "signin";
  public static final String HEADER = "header";
  public static final String DESCRIPTION = "description";
  public static final String FORM_TITLE = "form.title";
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";
  public static final String SIGNIN = "signin";
  public static final String FAIL = "fail";
  public static final String DISABLED = "disabled";
  public static final String LOCKED = "locked";
  private static final long serialVersionUID = -3000859016669509494L;
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SigninView.class);
  protected LoginI18n i18n;
  protected String error;

  public SigninView() {
  }

  @PostConstruct
  void init() {
    setId(VIEW_NAME);
    addLoginListener(e -> setError(false));
    setForgotPasswordButtonVisible(false);
    setAction(VIEW_NAME);
    setOpened(true);
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    // TODO Redirect to main view if user is known.
    /*
    if (!authorizationService.isAnonymous()) {
      UI.getCurrent().getPage().getHistory().replaceState(null, "");
      event.rerouteTo(MainView.class);
    }
    */
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    final MessageResource userResources = new MessageResource(User.class, getLocale());
    i18n = LoginI18n.createDefault();
    i18n.setHeader(new LoginI18n.Header());
    i18n.getHeader().setTitle(resources.message(HEADER));
    i18n.getHeader().setDescription(resources.message(DESCRIPTION));
    i18n.setAdditionalInformation(null);
    i18n.setForm(new LoginI18n.Form());
    i18n.getForm().setSubmit(resources.message(SIGNIN));
    i18n.getForm().setTitle(resources.message(FORM_TITLE));
    i18n.getForm().setUsername(userResources.message(EMAIL));
    i18n.getForm().setPassword(userResources.message(HASHED_PASSWORD));
    i18n.setErrorMessage(new ErrorMessage());
    if (error == null) {
      error = FAIL;
    }
    i18n.getErrorMessage().setTitle(resources.message(property(error, TITLE)));
    i18n.getErrorMessage().setMessage(resources.message(error));
    setI18n(i18n);
  }

  @Override
  public String getPageTitle() {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    final MessageResource generalResources = new MessageResource(WebConstants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }

  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    Map<String, List<String>> parameters = event.getLocation().getQueryParameters().getParameters();
    if (parameters.containsKey(DISABLED)) {
      error = DISABLED;
      setError(true);
    } else if (parameters.containsKey(LOCKED)) {
      error = LOCKED;
      setError(true);
    } else if (parameters.containsKey(FAIL)) {
      error = FAIL;
      setError(true);
    }
  }
}
