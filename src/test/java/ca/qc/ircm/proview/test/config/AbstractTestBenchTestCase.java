package ca.qc.ircm.proview.test.config;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.web.ViewLayout.SUBMISSIONS;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.user.web.UseForgotPasswordView;
import ca.qc.ircm.proview.web.SigninView;
import ca.qc.ircm.proview.web.ViewLayout;
import com.vaadin.flow.component.sidenav.testbench.SideNavElement;
import com.vaadin.flow.component.sidenav.testbench.SideNavItemElement;
import com.vaadin.testbench.TestBenchTestCase;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;

/**
 * Additional functions for TestBenchTestCase.
 */
public abstract class AbstractTestBenchTestCase extends TestBenchTestCase {
  private static final String LAYOUT_PREFIX = messagePrefix(ViewLayout.class);
  private static final String SIGNIN_PREFIX = messagePrefix(SigninView.class);
  private static final String USE_FORGOT_PASSWORD_PREFIX =
      messagePrefix(UseForgotPasswordView.class);
  private static final String ACCESS_DENIED_PREFIX = messagePrefix(AccessDeniedView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final Logger logger = LoggerFactory.getLogger(AbstractTestBenchTestCase.class);
  @Value("http://localhost:${local.server.port}${server.servlet.context-path:}")
  protected String baseUrl;
  @Autowired
  private MessageSource messageSource;

  protected String homeUrl() {
    return baseUrl + "/";
  }

  protected String viewUrl(String view) {
    return baseUrl + "/" + view;
  }

  protected String viewUrl(String view, String parameters) {
    return baseUrl + "/" + view + "/" + parameters;
  }

  protected void openView(String view) {
    openView(view, null);
  }

  protected void openView(String view, String parameters) {
    String url = viewUrl(view);
    if (parameters != null && !parameters.isEmpty()) {
      url += "/" + parameters;
    }
    if (url.equals(getDriver().getCurrentUrl())) {
      getDriver().navigate().refresh();
    } else {
      getDriver().get(url);
    }
  }

  protected Locale currentLocale() {
    List<Locale> locales = Constants.getLocales();
    Function<Locale, String> applicationName =
        locale -> messageSource.getMessage(CONSTANTS_PREFIX + APPLICATION_NAME, null, locale);
    SideNavItemElement home =
        optional(() -> $(SideNavElement.class).first().$(SideNavItemElement.class).first())
            .orElse(null);
    Optional<Locale> optlocale = locales.stream()
        .filter(locale -> messageSource.getMessage(LAYOUT_PREFIX + SUBMISSIONS, null, locale)
            .equals(home != null ? home.getLabel() : ""))
        .findAny();
    if (optlocale.isEmpty()) {
      optlocale = locales.stream()
          .filter(locale -> messageSource.getMessage(SIGNIN_PREFIX + TITLE,
              new Object[] { applicationName.apply(locale) }, locale)
              .equals(getDriver().getTitle()))
          .findAny();
    }
    if (optlocale.isEmpty()) {
      optlocale = locales.stream()
          .filter(locale -> messageSource
              .getMessage(USE_FORGOT_PASSWORD_PREFIX + TITLE,
                  new Object[] { applicationName.apply(locale) }, locale)
              .equals(getDriver().getTitle()))
          .findAny();
    }
    if (optlocale.isEmpty()) {
      optlocale = locales.stream()
          .filter(locale -> messageSource
              .getMessage(ACCESS_DENIED_PREFIX + TITLE,
                  new Object[] { applicationName.apply(locale) }, locale)
              .equals(getDriver().getTitle()))
          .findAny();
    }
    return optlocale.orElse(Constants.DEFAULT_LOCALE);
  }

  protected <T> Optional<T> optional(Supplier<T> supplier) {
    try {
      return Optional.of(supplier.get());
    } catch (Throwable e) {
      return Optional.empty();
    }
  }
}
