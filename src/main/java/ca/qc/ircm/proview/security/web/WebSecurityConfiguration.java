package ca.qc.ircm.proview.security.web;

import static ca.qc.ircm.proview.UsedBy.SPRING;

import ca.qc.ircm.proview.UsedBy;
import ca.qc.ircm.proview.security.DaoAuthenticationProviderWithLdap;
import ca.qc.ircm.proview.security.LdapConfiguration;
import ca.qc.ircm.proview.security.LdapService;
import ca.qc.ircm.proview.security.SecurityConfiguration;
import ca.qc.ircm.proview.security.ShiroPasswordEncoder;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import jakarta.servlet.Filter;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.context.SecurityContextHolderFilter;

/**
 * Security configuration.
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfiguration extends VaadinWebSecurity {

  public static final String SIGNIN_PROCESSING_URL = "/" + SigninView.VIEW_NAME;
  public static final String SIGNOUT_URL = "/signout";
  private static final String SIGNIN_DEFAULT_FAILURE_URL =
      SIGNIN_PROCESSING_URL + "?" + SigninView.FAIL;
  private static final String SIGNIN_LOCKED_URL = SIGNIN_PROCESSING_URL + "?" + SigninView.LOCKED;
  private static final String SIGNIN_DISABLED_URL =
      SIGNIN_PROCESSING_URL + "?" + SigninView.DISABLED;
  private static final String PASSWORD_ENCRYPTION = "bcrypt";
  private UserDetailsService userDetailsService;
  private UserRepository userRepository;
  private LdapService ldapService;
  private SecurityConfiguration configuration;
  private LdapConfiguration ldapConfiguration;
  private PermissionEvaluator permissionEvaluator;

  /**
   * Returns password encoder that supports password upgrades.
   *
   * @return password encoder that supports password upgrades
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    Map<String, PasswordEncoder> encoders = new HashMap<>();
    PasswordEncoder defaultPasswordEncoder = new BCryptPasswordEncoder();
    encoders.put(PASSWORD_ENCRYPTION, defaultPasswordEncoder);
    configuration.passwords().forEach(pv -> encoders.put(String.valueOf(pv.version()),
        new ShiroPasswordEncoder(pv.algorithm(), pv.iterations())));

    DelegatingPasswordEncoder passworEncoder =
        new DelegatingPasswordEncoder(PASSWORD_ENCRYPTION, encoders);
    passworEncoder.setDefaultPasswordEncoderForMatches(defaultPasswordEncoder);

    return passworEncoder;
  }

  /**
   * Returns {@link DaoAuthenticationProviderWithLdap}.
   *
   * @return {@link DaoAuthenticationProviderWithLdap}
   */
  @Bean
  public DaoAuthenticationProviderWithLdap authenticationProvider() {
    DaoAuthenticationProviderWithLdap authenticationProvider =
        new DaoAuthenticationProviderWithLdap();
    authenticationProvider.setUserDetailsService(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    authenticationProvider.setUserRepository(userRepository);
    authenticationProvider.setLdapService(ldapService);
    authenticationProvider.setLdapConfiguration(ldapConfiguration);
    authenticationProvider.setSecurityConfiguration(configuration);
    return authenticationProvider;
  }

  @Bean
  public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
    DefaultMethodSecurityExpressionHandler expressionHandler =
        new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setPermissionEvaluator(permissionEvaluator);
    return expressionHandler;
  }

  /**
   * Returns {@link AuthenticationFailureHandler}.
   *
   * @return {@link AuthenticationFailureHandler}
   */
  @Bean
  public AuthenticationFailureHandler authenticationFailureHandler() {
    final Map<String, String> failureUrlMap = new HashMap<>();
    failureUrlMap.put(LockedException.class.getName(), SIGNIN_LOCKED_URL);
    failureUrlMap.put(DisabledException.class.getName(), SIGNIN_DISABLED_URL);
    ExceptionMappingAuthenticationFailureHandler authenticationFailureHandler =
        new ExceptionMappingAuthenticationFailureHandler();
    authenticationFailureHandler.setDefaultFailureUrl(SIGNIN_DEFAULT_FAILURE_URL);
    authenticationFailureHandler.setExceptionMappings(failureUrlMap);
    return authenticationFailureHandler;
  }

  /**
   * Require login to access internal pages and configure login form.
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // Configure the login page.
    http.formLogin(login -> login.failureHandler(authenticationFailureHandler()));
    // Remember me
    http.rememberMe(
        rememberMe -> rememberMe.alwaysRemember(true).key(configuration.rememberMeKey()));

    super.configure(http);

    // Configure the login page.
    setLoginView(http, SigninView.class);

    // Used for TestBench.
    try {
      Class<?> clazz = Class.forName("ca.qc.ircm.proview.test.config.TestBenchSecurityFilter");
      http.addFilterBefore((Filter) clazz.getDeclaredConstructor().newInstance(),
          SecurityContextHolderFilter.class);
    } catch (ClassNotFoundException e) {
      // Ignore, not running unit tests.
    }
  }

  /**
   * Allows access to static resources, bypassing Spring security.
   */
  @Override
  public void configure(WebSecurity web) throws Exception {
    super.configure(web);
  }

  @Autowired
  @UsedBy(SPRING)
  public void setUserDetailsService(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Autowired
  @UsedBy(SPRING)
  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Autowired
  @UsedBy(SPRING)
  public void setLdapService(LdapService ldapService) {
    this.ldapService = ldapService;
  }

  @Autowired
  @UsedBy(SPRING)
  public void setConfiguration(SecurityConfiguration configuration) {
    this.configuration = configuration;
  }

  @Autowired
  @UsedBy(SPRING)
  public void setLdapConfiguration(LdapConfiguration ldapConfiguration) {
    this.ldapConfiguration = ldapConfiguration;
  }

  @Autowired
  @UsedBy(SPRING)
  public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
    this.permissionEvaluator = permissionEvaluator;
  }
}
