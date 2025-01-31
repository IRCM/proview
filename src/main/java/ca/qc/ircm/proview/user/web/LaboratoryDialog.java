package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.CANCEL;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.user.LaboratoryProperties.NAME;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryService;
import ca.qc.ircm.proview.web.SavedEvent;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
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
 * Laboratory dialog.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LaboratoryDialog extends Dialog
    implements LocaleChangeObserver, NotificationComponent {

  public static final String ID = "laboratory-dialog";
  public static final String HEADER = "header";
  public static final String SAVED = "saved";
  private static final String MESSAGES_PREFIX = messagePrefix(LaboratoryDialog.class);
  private static final String LABORATORY_PREFIX = messagePrefix(Laboratory.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @Serial
  private static final long serialVersionUID = 3285639770914046262L;
  private static final Logger logger = LoggerFactory.getLogger(LaboratoryDialog.class);
  protected TextField name = new TextField();
  protected Button save = new Button();
  protected Button cancel = new Button();
  private final Binder<Laboratory> binder = new BeanValidationBinder<>(Laboratory.class);
  private final transient LaboratoryService service;
  private final transient AuthenticatedUser authenticatedUser;

  @Autowired
  protected LaboratoryDialog(LaboratoryService service, AuthenticatedUser authenticatedUser) {
    this.service = service;
    this.authenticatedUser = authenticatedUser;
  }

  public static String id(String baseId) {
    return styleName(ID, baseId);
  }

  /**
   * Initializes user dialog.
   */
  @PostConstruct
  protected void init() {
    logger.debug("laboratory dialog");
    setId(ID);
    setWidth("500px");
    setResizable(true);
    VerticalLayout layout = new VerticalLayout();
    add(layout);
    FormLayout form = new FormLayout();
    layout.add(form);
    layout.setSizeFull();
    form.add(name);
    form.setResponsiveSteps(new ResponsiveStep("15em", 1));
    getFooter().add(cancel, save);
    name.setId(id(NAME));
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
    name.setLabel(getTranslation(LABORATORY_PREFIX + NAME));
    save.setText(getTranslation(CONSTANTS_PREFIX + SAVE));
    cancel.setText(getTranslation(CONSTANTS_PREFIX + CANCEL));
    binder.forField(name).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").bind(NAME);
  }

  private void updateHeader() {
    if (binder.getBean() != null && binder.getBean().getId() != 0) {
      setHeaderTitle(getTranslation(MESSAGES_PREFIX + HEADER, 1, binder.getBean().getName()));
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
  public Registration
  addSavedListener(ComponentEventListener<SavedEvent<LaboratoryDialog>> listener) {
    return addListener((Class) SavedEvent.class, listener);
  }

  void fireSavedEvent() {
    fireEvent(new SavedEvent<>(this, true));
  }

  public long getLaboratoryId() {
    return binder.getBean().getId();
  }

  public void setLaboratoryId(long id) {
    if (id == 0) {
      throw new IllegalArgumentException("id parameter cannot be 0");
    }
    Laboratory laboratory = service.get(id).orElseThrow();
    binder.setBean(laboratory);
    updateHeader();
  }

  BinderValidationStatus<Laboratory> validate() {
    return binder.validate();
  }

  private boolean isValid() {
    return validate().isOk();
  }

  void save() {
    if (isValid()) {
      Laboratory laboratory = binder.getBean();
      service.save(laboratory);
      showNotification(getTranslation(MESSAGES_PREFIX + SAVED, laboratory.getName()));
      close();
      fireSavedEvent();
    }
  }

  void cancel() {
    close();
  }
}
