package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.FindbugsJustifications.DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD;

import com.vaadin.ui.themes.ValoTheme;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * View base layout.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MainLayout extends MainLayoutDesign {
  private static final long serialVersionUID = -3818536803897529844L;
  @Inject
  protected Menu menu;

  /**
   * Initializes layout.
   */
  @PostConstruct
  @SuppressFBWarnings(
      value = "NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD",
      justification = DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD)
  public void init() {
    menuLayout.addComponent(menu);
    menuLayout.addStyleName(ValoTheme.LAYOUT_CARD);
    content.addStyleName(ValoTheme.PANEL_BORDERLESS);
  }

  @Override
  public void attach() {
    super.attach();
    getUI().getNavigator().addViewChangeListener(menu);
  }
}
