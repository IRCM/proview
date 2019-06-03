package ca.qc.ircm.proview.web;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.templatemodel.TemplateModel;

/**
 * A Designer generated component for the signin-view template.
 *
 * Designer will add and remove fields with @Id mappings but does not overwrite or otherwise change
 * this file.
 */
@Tag("signin-view")
@HtmlImport("src/signin-view.html")
public class SigninView extends PolymerTemplate<SigninView.SigninViewModel> {
  public static final String VIEW_NAME = "signin";
  public static final String HEADER = "header";
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";
  public static final String SIGNIN = "signin";
  public static final String DO_SIGNIN = "dosignin";
  public static final String FAIL = "fail";
  public static final String DISABLED = "disabled";
  public static final String EXCESSIVE_ATTEMPTS = "excessiveAttempts";

  /**
   * Creates a new SigninView.
   */
  public SigninView() {
    // You can initialise any data required for the connected UI components here.
  }

  /**
   * This model binds properties between SigninView and signin-view
   */
  public interface SigninViewModel extends TemplateModel {
    // Add setters and getters for template properties here.
  }
}
