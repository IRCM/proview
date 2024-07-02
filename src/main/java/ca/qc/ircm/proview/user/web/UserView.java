package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User view.
 */
@Route(value = UserView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ MANAGER, ADMIN })
public class UserView extends VerticalLayout
    implements LocaleChangeObserver, HasDynamicTitle, HasUrlParameter<Long>, NotificationComponent {
  private static final long serialVersionUID = 4760310643370830640L;
  private static final Logger logger = LoggerFactory.getLogger(UserView.class);
  public static final String VIEW_NAME = "user";
  public static final String ID = "user-view";
  public static final String HEADER = "header";
  public static final String SAVED = "saved";
  protected H2 header = new H2();
  protected HorizontalLayout buttonsLayout = new HorizontalLayout();
  protected Button save = new Button();
  protected UserForm form;
  private transient UserService service;

  @Autowired
  protected UserView(UserForm form, UserService service) {
    this.form = form;
    this.service = service;
  }

  /**
   * Initializes user dialog.
   */
  @PostConstruct
  protected void init() {
    logger.debug("user view");
    setId(ID);
    add(header, form, buttonsLayout);
    buttonsLayout.add(save);
    header.setId(HEADER);
    save.setId(SAVE);
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    save.setIcon(VaadinIcon.CHECK.create());
    save.addClickListener(e -> save());
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final AppResources webResources = new AppResources(Constants.class, getLocale());
    updateHeader();
    save.setText(webResources.message(SAVE));
  }

  private void updateHeader() {
    final AppResources resources = new AppResources(UserView.class, getLocale());
    if (form.getUser() != null && form.getUser().getId() != null) {
      header.setText(resources.message(HEADER, 1, form.getUser().getName()));
    } else {
      header.setText(resources.message(HEADER, 0));
    }
  }

  @Override
  public String getPageTitle() {
    final AppResources resources = new AppResources(getClass(), getLocale());
    final AppResources generalResources = new AppResources(Constants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
    if (parameter != null) {
      form.setUser(service.get(parameter).orElse(null));
    }
  }

  void save() {
    if (form.isValid()) {
      User user = form.getUser();
      String password = form.getPassword();
      logger.debug("save user {} in laboratory {}", user, user.getLaboratory());
      service.save(user, password);
      final AppResources resources = new AppResources(UserView.class, getLocale());
      showNotification(resources.message(SAVED, user.getName()));
      UI.getCurrent().navigate(UsersView.class);
    }
  }
}
