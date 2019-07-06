package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.user.UserRole.USER;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;

import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Guidelines view.
 */
@Tag("guidelines-view")
@Route(value = GuidelinesView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ USER })
public class GuidelinesView extends VerticalLayout
    implements LocaleChangeObserver, HasDynamicTitle {
  public static final String VIEW_NAME = "guidelines";
  public static final String HEADER = "header";
  private static final long serialVersionUID = 1881767150748374598L;
  protected H2 header = new H2();
  private transient GuidelinesConfiguration guidelinesConfiguration;

  @Autowired
  protected GuidelinesView(GuidelinesConfiguration guidelinesConfiguration) {
    this.guidelinesConfiguration = guidelinesConfiguration;
  }

  @PostConstruct
  void init() {
    add(header);
    header.setId(HEADER);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    removeAll();
    add(header);
    header.setText(resources.message(HEADER));
    guidelinesConfiguration.categories(getLocale())
        .forEach(category -> add(new CategoryComponent(category)));
  }

  @Override
  public String getPageTitle() {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    final MessageResource generalResources = new MessageResource(WebConstants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }
}
