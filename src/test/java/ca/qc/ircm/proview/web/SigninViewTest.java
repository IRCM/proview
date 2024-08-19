package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.HASHED_PASSWORD;
import static ca.qc.ircm.proview.web.SigninView.ADDITIONAL_INFORMATION;
import static ca.qc.ircm.proview.web.SigninView.DESCRIPTION;
import static ca.qc.ircm.proview.web.SigninView.FAIL;
import static ca.qc.ircm.proview.web.SigninView.FORGOT_PASSWORD;
import static ca.qc.ircm.proview.web.SigninView.FORM_TITLE;
import static ca.qc.ircm.proview.web.SigninView.HEADER;
import static ca.qc.ircm.proview.web.SigninView.ID;
import static ca.qc.ircm.proview.web.SigninView.SIGNIN;
import static ca.qc.ircm.proview.web.SigninView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.SecurityConfiguration;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.web.ForgotPasswordView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;

/**
 * Tests for {@link SigninView}.
 */
@ServiceTestAnnotations
@WithAnonymousUser
public class SigninViewTest extends SpringUIUnitTest {
  private static final String MESSAGES_PREFIX = messagePrefix(SigninView.class);
  private static final String USER_PREFIX = messagePrefix(User.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private SigninView view;
  @Autowired
  private SecurityConfiguration configuration;
  @Autowired
  private AuthenticatedUser authenticatedUser;
  @Mock
  private AfterNavigationEvent afterNavigationEvent;
  @Mock
  private Location location;
  @Mock
  private QueryParameters queryParameters;
  private Locale locale = ENGLISH;
  private Map<String, List<String>> parameters = new HashMap<>();

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    when(afterNavigationEvent.getLocation()).thenReturn(location);
    when(location.getQueryParameters()).thenReturn(queryParameters);
    when(queryParameters.getParameters()).thenReturn(parameters);
    UI.getCurrent().setLocale(locale);
    view = navigate(SigninView.class);
  }

  @Test
  public void init() {
    assertEquals(VIEW_NAME, view.getAction());
    assertTrue(view.isOpened());
    assertTrue(view.isForgotPasswordButtonVisible());
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
  }

  @Test
  public void labels() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER), view.i18n.getHeader().getTitle());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + DESCRIPTION),
        view.i18n.getHeader().getDescription());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ADDITIONAL_INFORMATION),
        view.i18n.getAdditionalInformation());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + FORM_TITLE), view.i18n.getForm().getTitle());
    assertEquals(view.getTranslation(USER_PREFIX + EMAIL), view.i18n.getForm().getUsername());
    assertEquals(view.getTranslation(USER_PREFIX + HASHED_PASSWORD),
        view.i18n.getForm().getPassword());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SIGNIN), view.i18n.getForm().getSubmit());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + FORGOT_PASSWORD),
        view.i18n.getForm().getForgotPassword());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + property(FAIL, TITLE)),
        view.i18n.getErrorMessage().getTitle());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + FAIL),
        view.i18n.getErrorMessage().getMessage());
    assertFalse(view.isError());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER), view.i18n.getHeader().getTitle());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + DESCRIPTION),
        view.i18n.getHeader().getDescription());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + ADDITIONAL_INFORMATION),
        view.i18n.getAdditionalInformation());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + FORM_TITLE), view.i18n.getForm().getTitle());
    assertEquals(view.getTranslation(USER_PREFIX + EMAIL), view.i18n.getForm().getUsername());
    assertEquals(view.getTranslation(USER_PREFIX + HASHED_PASSWORD),
        view.i18n.getForm().getPassword());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SIGNIN), view.i18n.getForm().getSubmit());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + FORGOT_PASSWORD),
        view.i18n.getForm().getForgotPassword());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + property(FAIL, TITLE)),
        view.i18n.getErrorMessage().getTitle());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + FAIL),
        view.i18n.getErrorMessage().getMessage());
    assertFalse(view.isError());
  }

  @Test
  public void getPageTitle() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + TITLE,
        view.getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME)), view.getPageTitle());
  }

  @Test
  public void forgotPassword() {
    view.fireForgotPasswordEvent();
    assertTrue($(ForgotPasswordView.class).exists());
  }
}
