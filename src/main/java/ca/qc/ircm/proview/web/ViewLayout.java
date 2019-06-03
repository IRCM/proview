package ca.qc.ircm.proview.web;

import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;

/**
 * A Designer generated component for the view-layout template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("view-layout")
@HtmlImport("src/view-layout.html")
public class ViewLayout extends PolymerTemplate<ViewLayout.ViewLayoutModel> {

    /**
     * Creates a new ViewLayout.
     */
    public ViewLayout() {
        // You can initialise any data required for the connected UI components here.
    }

    /**
     * This model binds properties between ViewLayout and view-layout
     */
    public interface ViewLayoutModel extends TemplateModel {
        // Add setters and getters for template properties here.
    }
}
