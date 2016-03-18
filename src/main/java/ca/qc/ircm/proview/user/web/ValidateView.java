package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validate users.
 */
@SpringView(name = ValidateView.VIEW_NAME)
public class ValidateView extends VerticalLayout implements MessageResourcesView {
  private static final long serialVersionUID = -1956061543048432065L;
  public static final String VIEW_NAME = "user/validate";
  private static final Logger logger = LoggerFactory.getLogger(ValidateView.class);
}
