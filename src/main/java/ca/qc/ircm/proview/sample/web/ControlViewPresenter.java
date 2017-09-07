package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * Control view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings("serial")
public class ControlViewPresenter implements BinderValidator, SaveListener<Control> {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String INVALID_SAMPLE = "sample.invalid";
  private static final Logger logger = LoggerFactory.getLogger(ControlViewPresenter.class);
  private ControlView view;
  @Inject
  private ControlService controlService;
  @Inject
  private AuthorizationService authorizationService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected ControlViewPresenter() {
  }

  protected ControlViewPresenter(ControlService controlService,
      AuthorizationService authorizationService, String applicationName) {
    this.controlService = controlService;
    this.authorizationService = authorizationService;
    this.applicationName = applicationName;
  }

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(ControlView view) {
    logger.debug("Control view");
    this.view = view;
    view.form.getPresenter().setEditable(authorizationService.hasAdminRole());
    view.form.addSaveListener(this);
    final MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.addStyleName(ValoTheme.LABEL_H1);
    view.headerLabel.setValue(resources.message(HEADER));
  }

  private boolean validateParameters(String parameters) {
    boolean valid = true;
    try {
      Long id = Long.valueOf(parameters);
      if (controlService.get(id) == null) {
        valid = false;
      }
    } catch (NumberFormatException e) {
      valid = false;
    }
    return valid;
  }

  /**
   * Called by view when entered.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    Control control = null;
    if (parameters != null && !parameters.isEmpty()) {
      if (validateParameters(parameters)) {
        Long id = Long.valueOf(parameters);
        control = controlService.get(id);
        view.form.getPresenter().setBean(control);
      } else {
        view.showWarning(view.getResources().message(INVALID_SAMPLE));
      }
    }
  }

  @Override
  public void saved(SaveEvent<Control> event) {
    Control control = event.getSavedObject();
    view.navigateTo(ControlView.VIEW_NAME + "/" + control.getId());
  }
}
