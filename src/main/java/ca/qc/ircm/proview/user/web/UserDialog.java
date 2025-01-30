package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.CANCEL;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.text.Strings.styleName;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.SavedEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.PostConstruct;
import java.io.Serial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * User dialog.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserDialog extends Dialog implements LocaleChangeObserver {

  public static final String ID = "user-dialog";
  public static final String HEADER = "header";
  private static final String MESSAGES_PREFIX = messagePrefix(UserDialog.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @Serial
  private static final long serialVersionUID = 3285639770914046262L;
  private static final Logger logger = LoggerFactory.getLogger(UserDialog.class);
  protected Button save = new Button();
  protected Button cancel = new Button();
  protected UserForm form;
  private transient UserService service;

  @Autowired
  UserDialog(UserForm form, UserService service) {
    this.form = form;
    this.service = service;
  }

  public static String id(String baseId) {
    return styleName(ID, baseId);
  }

  /**
   * Initializes user dialog.
   */
  @PostConstruct
  protected void init() {
    logger.debug("user dialog");
    setId(ID);
    setWidth("1000px");
    setResizable(true);
    VerticalLayout layout = new VerticalLayout();
    add(layout);
    layout.add(form);
    layout.setSizeFull();
    getFooter().add(cancel, save);
    save.setId(id(SAVE));
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    save.setIcon(VaadinIcon.CHECK.create());
    save.addClickListener(e -> save());
    cancel.setId(id(CANCEL));
    cancel.setIcon(VaadinIcon.CLOSE.create());
    cancel.addClickListener(e -> cancel());
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    updateHeader();
    save.setText(getTranslation(CONSTANTS_PREFIX + SAVE));
    cancel.setText(getTranslation(CONSTANTS_PREFIX + CANCEL));
  }

  private void updateHeader() {
    if (form.getUser().getId() != 0) {
      setHeaderTitle(getTranslation(MESSAGES_PREFIX + HEADER, 1, form.getUser().getName()));
    } else {
      setHeaderTitle(getTranslation(MESSAGES_PREFIX + HEADER, 0));
    }
  }

  /**
   * Adds listener to be informed when a user was saved.
   *
   * @param listener listener
   * @return listener registration
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Registration addSavedListener(ComponentEventListener<SavedEvent<UserDialog>> listener) {
    return addListener((Class) SavedEvent.class, listener);
  }

  void fireSavedEvent() {
    fireEvent(new SavedEvent<>(this, true));
  }

  public long getUserId() {
    return form.getUser().getId();
  }

  /**
   * Sets user's id.
   *
   * @param id user's id
   */
  public void setUserId(long id) {
    User user = id != 0 ? service.get(id).orElseThrow() : null;
    form.setUser(user);
    updateHeader();
  }

  void save() {
    if (form.isValid()) {
      User user = form.getUser();
      String password = form.getPassword();
      logger.debug("save user {} in laboratory {}", user, user.getLaboratory());
      service.save(user, password);
      close();
      fireSavedEvent();
    }
  }

  void cancel() {
    close();
  }
}
