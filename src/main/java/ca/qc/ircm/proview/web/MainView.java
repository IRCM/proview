package ca.qc.ircm.proview.web;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.templatemodel.TemplateModel;

/**
 * A Designer generated component for the main-view template.
 *
 * Designer will add and remove fields with @Id mappings but does not overwrite or otherwise change
 * this file.
 */
@Tag("main-view")
@HtmlImport("src/main-view.html")
public class MainView extends PolymerTemplate<MainView.MainViewModel> {
  public static final String VIEW_NAME = "";

  /**
   * Creates a new MainView.
   */
  public MainView() {
    // You can initialise any data required for the connected UI components here.
  }

  /**
   * This model binds properties between MainView and main-view
   */
  public interface MainViewModel extends TemplateModel {
    // Add setters and getters for template properties here.
  }
}
