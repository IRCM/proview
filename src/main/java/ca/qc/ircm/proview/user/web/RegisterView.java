package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.spring.annotation.SpringView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/**
 * Registers user.
 */
@SpringView(name = RegisterView.VIEW_NAME)
public class RegisterView extends RegisterDesign implements MessageResourcesView {
  private static final long serialVersionUID = 7586918222688019429L;
  public static final String VIEW_NAME = "register";
  private static final Logger logger = LoggerFactory.getLogger(RegisterView.class);

  /**
   * Initialize view.
   */
  @PostConstruct
  public void init() {
  }

  @Override
  public void attach() {
    super.attach();
    logger.debug("Register view");
    MessageResource resources = getResources();
    setCaption(resources.message("title"));
  }
}
