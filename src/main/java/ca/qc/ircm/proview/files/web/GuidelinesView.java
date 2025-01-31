package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.proview.web.ViewLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import java.io.Serial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Guidelines view.
 */
@Route(value = GuidelinesView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({USER})
public class GuidelinesView extends VerticalLayout
    implements LocaleChangeObserver, HasDynamicTitle {

  public static final String VIEW_NAME = "guidelines";
  public static final String ID = "guidelines-view";
  private static final String MESSAGES_PREFIX = messagePrefix(GuidelinesView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @Serial
  private static final long serialVersionUID = 1881767150748374598L;
  private static final Logger logger = LoggerFactory.getLogger(GuidelinesView.class);
  private final transient GuidelinesConfiguration guidelinesConfiguration;

  @Autowired
  protected GuidelinesView(GuidelinesConfiguration guidelinesConfiguration) {
    this.guidelinesConfiguration = guidelinesConfiguration;
  }

  @PostConstruct
  void init() {
    logger.debug("guidelines view");
    setId(ID);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    removeAll();
    guidelinesConfiguration.categories(getLocale())
        .forEach(category -> add(new CategoryComponent(category)));
  }

  @Override
  public String getPageTitle() {
    return getTranslation(MESSAGES_PREFIX + TITLE,
        getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME));
  }
}
