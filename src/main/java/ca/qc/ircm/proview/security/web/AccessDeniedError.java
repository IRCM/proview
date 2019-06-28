package ca.qc.ircm.proview.security.web;

import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;

import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasErrorParameter;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

/**
 * Access denied exception handler.
 */
public class AccessDeniedError extends Div
    implements HasDynamicTitle, HasErrorParameter<AccessDeniedException> {
  public static final String TEXT = "text";
  private static final long serialVersionUID = -7943776289990862803L;

  @Override
  public int setErrorParameter(BeforeEnterEvent event,
      ErrorParameter<AccessDeniedException> parameter) {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    setText(resources.message(TEXT));
    return HttpServletResponse.SC_FORBIDDEN;
  }

  @Override
  public String getPageTitle() {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    final MessageResource generalResources = new MessageResource(WebConstants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }
}
