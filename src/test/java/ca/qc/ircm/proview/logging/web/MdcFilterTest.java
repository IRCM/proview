package ca.qc.ircm.proview.logging.web;

import static ca.qc.ircm.proview.logging.web.MdcFilter.USER_CONTEXT_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Signed;
import ca.qc.ircm.proview.user.User;
import org.apache.log4j.MDC;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class MdcFilterTest {
  private MdcFilter mdcFilter;
  @Mock
  private Signed signed;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpSession session;
  @Mock
  private HttpServletResponse response;
  @Mock
  private FilterChain filterChain;

  @Before
  public void beforeTest() {
    mdcFilter = new MdcFilter(signed);
  }

  @Test
  public void doFilter_Anonymous() throws Throwable {
    String sessionId = "sessionId";
    when(request.getSession()).thenReturn(session);
    when(session.getId()).thenReturn(sessionId);
    doAnswer(i -> {
      assertEquals(sessionId, MDC.get(USER_CONTEXT_KEY));
      return null;
    }).when(filterChain).doFilter(any(), any());

    mdcFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(MDC.get(USER_CONTEXT_KEY));
  }

  @Test
  public void doFilter_User() throws Throwable {
    Long userId = 3L;
    String email = "test@ircm.qc.ca";
    when(signed.getUser()).thenReturn(new User(userId, email));
    doAnswer(i -> {
      assertEquals(userId + ":" + email, MDC.get(USER_CONTEXT_KEY));
      return null;
    }).when(filterChain).doFilter(any(), any());

    mdcFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(MDC.get(USER_CONTEXT_KEY));
  }
}
