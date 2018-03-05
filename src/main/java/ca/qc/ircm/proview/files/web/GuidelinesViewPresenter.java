package ca.qc.ircm.proview.files.web;

import ca.qc.ircm.utils.MessageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Guidelines view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GuidelinesViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  private static final Logger logger = LoggerFactory.getLogger(GuidelinesViewPresenter.class);
  private GuidelinesView view;
  private GuidelinesViewDesign design;
  @Value("${spring.application.name}")
  private String applicationName;

  protected GuidelinesViewPresenter() {
  }

  protected GuidelinesViewPresenter(String applicationName) {
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(GuidelinesView view) {
    logger.debug("Guidelines view");
    this.view = view;
    design = view.design;
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
  }
}
