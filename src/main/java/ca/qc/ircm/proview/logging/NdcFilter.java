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

package ca.qc.ircm.proview.logging;

import ca.qc.ircm.proview.user.Signed;
import org.apache.log4j.NDC;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Request filter that set NDC context for loggers.
 */
public class NdcFilter implements Filter {
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;

    try {
      this.setNdc(httpRequest);
      filterChain.doFilter(request, response);
    } finally {
      this.removeNdc();
    }
  }

  private void setNdc(HttpServletRequest request) {
    Signed signed = (Signed) request.getAttribute("signed");
    if (signed == null || signed.getUser() == null) {
      NDC.push(request.getSession().getId());
    } else {
      NDC.push(signed.getUser().getEmail());
    }
  }

  private void removeNdc() {
    NDC.pop();
    NDC.remove();
  }
}
