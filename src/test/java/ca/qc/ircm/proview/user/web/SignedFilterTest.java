package ca.qc.ircm.proview.user.web;

import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Signed;
import org.apache.shiro.SecurityUtils;
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
public class SignedFilterTest {
  private SignedFilter signedFilter;
  @Mock
  private Signed signed;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private FilterChain filterChain;

  @Before
  public void beforeTest() {
    signedFilter = new SignedFilter(signed);
  }

  @Test
  public void doFilter() throws Throwable {
    signedFilter.doFilter(request, response, filterChain);

    verify(request).setAttribute("signed", signed);
    verify(request).setAttribute("subject", SecurityUtils.getSubject());
    verify(filterChain).doFilter(request, response);
  }
}
