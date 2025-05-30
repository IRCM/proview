package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.DEFAULT_LOCALE;
import static ca.qc.ircm.proview.UsedBy.SPRING;

import ca.qc.ircm.proview.UsedBy;
import ca.qc.ircm.proview.logging.web.MdcFilter;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.IntrospectorCleanupListener;

/**
 * Enable Spring Web MVC for REST services.
 */
@Configuration
public class SpringWebConfiguration implements WebMvcConfigurer {

  private final AuthenticatedUser authenticatedUser;

  @UsedBy(SPRING)
  protected SpringWebConfiguration(AuthenticatedUser authenticatedUser) {
    this.authenticatedUser = authenticatedUser;
  }

  /**
   * Spring's request logging filter.
   *
   * @return request logging filter
   */
  @Bean
  public FilterRegistrationBean<CommonsRequestLoggingFilter> requestLoggingFilter() {
    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
    loggingFilter.setIncludeClientInfo(true);
    loggingFilter.setIncludeQueryString(true);
    loggingFilter.setIncludePayload(false);
    loggingFilter.setIncludeHeaders(true);
    FilterRegistrationBean<CommonsRequestLoggingFilter> registration =
        new FilterRegistrationBean<>(loggingFilter);
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return registration;
  }

  @Bean(name = MdcFilter.BEAN_NAME)
  public MdcFilter mdcFilter() {
    return new MdcFilter(authenticatedUser);
  }

  @Bean
  public ServletListenerRegistrationBean<IntrospectorCleanupListener>
  introspectorCleanupListener() {
    return new ServletListenerRegistrationBean<>(new IntrospectorCleanupListener());
  }

  @Bean
  public ServletListenerRegistrationBean<RequestContextListener> requestContextListener() {
    return new ServletListenerRegistrationBean<>(new RequestContextListener());
  }

  /**
   * Returns {@link LocaleResolver} instance.
   *
   * @return {@link LocaleResolver} instance
   */
  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver slr = new SessionLocaleResolver();
    slr.setDefaultLocale(DEFAULT_LOCALE);
    return slr;
  }

  /**
   * Returns {@link LocaleChangeInterceptor} instance.
   *
   * @return {@link LocaleChangeInterceptor} instance
   */
  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
    lci.setParamName("lang");
    return lci;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localeChangeInterceptor());
  }
}
