package ca.qc.ircm.proview.security.web;

import ca.qc.ircm.proview.security.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Allows users to sign out.
 */
public class SignoutFilter extends GenericFilterBean {
  public static final String BEAN_NAME = "SignoutFilter";
  public static final String SIGNOUT_URL = "/signout";
  private static final Logger logger = LoggerFactory.getLogger(SignoutFilter.class);
  @Inject
  private AuthenticationService authenticationService;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    if (httpRequest.getServletPath().equals(SIGNOUT_URL)) {
      logger.debug("sign out user {}", httpRequest.getUserPrincipal());
      authenticationService.signout();
      httpResponse.sendRedirect(httpRequest.getContextPath());
    } else {
      chain.doFilter(httpRequest, response);
    }
  }
}
