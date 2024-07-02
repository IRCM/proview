package ca.qc.ircm.proview.web.component;

import com.vaadin.flow.server.VaadinServlet;

/**
 * Create URL for component.
 */
public interface UrlComponent {
  default String getUrl(String view) {
    String contextPath = VaadinServlet.getCurrent().getServletContext().getContextPath();
    return contextPath + "/" + view;
  }
}
