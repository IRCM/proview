package ca.qc.ircm.proview.user.web;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.templatemodel.TemplateModel;

/**
 * A Designer generated component for the users-view template.
 *
 * Designer will add and remove fields with @Id mappings but does not overwrite or otherwise change
 * this file.
 */
@Tag("users-view")
@HtmlImport("src/user/users-view.html")
public class UsersView extends PolymerTemplate<UsersView.UsersViewModel> {
  public static final String VIEW_NAME = "users";
  public static final String SWITCH_USER = "switchUser";
  public static final String SWITCH_FAILED = "switchFailed";

  /**
   * Creates a new UsersView.
   */
  public UsersView() {
    // You can initialise any data required for the connected UI components here.
  }

  /**
   * This model binds properties between UsersView and users-view
   */
  public interface UsersViewModel extends TemplateModel {
    // Add setters and getters for template properties here.
  }
}
