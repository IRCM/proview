/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.user.web;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
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
