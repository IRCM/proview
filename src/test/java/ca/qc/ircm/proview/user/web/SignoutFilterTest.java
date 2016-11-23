package ca.qc.ircm.proview.user.web;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SignoutFilterTest {
  private SignoutFilter signoutFilter;
  @Mock
  private AuthenticationService authenticationService;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private FilterChain filterChain;

  @Before
  public void beforeTest() {
    signoutFilter = new SignoutFilter(authenticationService);
  }

  @Test
  public void doFilter_SignoutPath() throws Throwable {
    when(request.getServletPath()).thenReturn("/signout");
    String contextPath = "/contextpath";
    when(request.getContextPath()).thenReturn(contextPath);

    signoutFilter.doFilter(request, response, filterChain);

    verify(authenticationService).signout();
    verify(response).sendRedirect(contextPath);
    verify(filterChain, never()).doFilter(request, response);
  }

  @Test
  public void doFilter_OtherPath() throws Throwable {
    when(request.getServletPath()).thenReturn("/register");

    signoutFilter.doFilter(request, response, filterChain);

    verify(authenticationService, never()).signout();
    verify(response, never()).sendRedirect(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void doFilter_RootPath() throws Throwable {
    when(request.getServletPath()).thenReturn("");

    signoutFilter.doFilter(request, response, filterChain);

    verify(authenticationService, never()).signout();
    verify(response, never()).sendRedirect(any());
    verify(filterChain).doFilter(request, response);
  }
}
