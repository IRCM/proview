package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.text.Strings.normalize;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.user.UserProperties.ACTIVE;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.LABORATORY;
import static ca.qc.ircm.proview.user.UserProperties.NAME;
import static ca.qc.ircm.proview.user.UserRole.USER;
import static ca.qc.ircm.proview.web.WebConstants.ALL;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.ERROR;
import static ca.qc.ircm.proview.web.WebConstants.ERROR_TEXT;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static ca.qc.ircm.proview.web.WebConstants.SUCCESS;
import static ca.qc.ircm.proview.web.WebConstants.THEME;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;

import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Users view.
 */
@Route(value = UsersView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ USER })
public class UsersView extends VerticalLayout implements LocaleChangeObserver, HasDynamicTitle,
    AfterNavigationObserver, NotificationComponent {
  public static final String VIEW_NAME = "users";
  public static final String HEADER = "header";
  public static final String USERS = "users";
  public static final String USERS_REQUIRED = property(USERS, REQUIRED);
  public static final String SWITCH_USER = "switchUser";
  public static final String SWITCH_FAILED = "switchFailed";
  public static final String ADD = "add";
  private static final long serialVersionUID = 1051684045824404864L;
  protected H2 header = new H2();
  protected Grid<User> users = new Grid<>();
  protected Column<User> email;
  protected Column<User> name;
  protected Column<User> laboratory;
  protected Column<User> active;
  protected TextField emailFilter = new TextField();
  protected TextField nameFilter = new TextField();
  protected TextField laboratoryFilter = new TextField();
  protected ComboBox<Optional<Boolean>> activeFilter = new ComboBox<>();
  protected Div error = new Div();
  protected Button add = new Button();
  protected Button switchUser = new Button();
  protected UserDialog userDialog;
  private Map<User, Button> actives = new HashMap<>();
  private transient UsersViewPresenter presenter;

  @Autowired
  protected UsersView(UsersViewPresenter presenter, UserDialog userDialog) {
    this.presenter = presenter;
    this.userDialog = userDialog;
  }

  @SuppressWarnings("unchecked")
  @PostConstruct
  void init() {
    setId(VIEW_NAME);
    HorizontalLayout buttonsLayout = new HorizontalLayout();
    add(header, users, error, buttonsLayout);
    buttonsLayout.add(add, switchUser);
    header.setId(HEADER);
    users.addClassName(USERS);
    users.addItemDoubleClickListener(e -> presenter.view(e.getItem()));
    email = users.addColumn(user -> user.getEmail(), EMAIL).setKey(EMAIL)
        .setComparator((u1, u2) -> u1.getEmail().compareToIgnoreCase(u2.getEmail()));
    name = users.addColumn(user -> user.getName(), NAME).setKey(NAME);
    laboratory = users.addColumn(user -> user.getLaboratory().getName(), LABORATORY)
        .setKey(LABORATORY).setComparator((u1, u2) -> normalize(u1.getLaboratory().getName())
            .compareToIgnoreCase(normalize(u2.getLaboratory().getName())));
    active = users.addColumn(new ComponentRenderer<>(user -> activeButton(user)), ACTIVE)
        .setKey(ACTIVE).setComparator((u1, u2) -> Boolean.compare(u1.isActive(), u2.isActive()));
    users.appendHeaderRow(); // Headers.
    HeaderRow filtersRow = users.appendHeaderRow();
    filtersRow.getCell(email).setComponent(emailFilter);
    emailFilter.addValueChangeListener(e -> presenter.filterEmail(e.getValue()));
    emailFilter.setValueChangeMode(ValueChangeMode.EAGER);
    emailFilter.setSizeFull();
    filtersRow.getCell(name).setComponent(nameFilter);
    nameFilter.addValueChangeListener(e -> presenter.filterName(e.getValue()));
    nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
    nameFilter.setSizeFull();
    filtersRow.getCell(laboratory).setComponent(laboratoryFilter);
    laboratoryFilter.addValueChangeListener(e -> presenter.filterLaboratory(e.getValue()));
    laboratoryFilter.setValueChangeMode(ValueChangeMode.EAGER);
    laboratoryFilter.setSizeFull();
    filtersRow.getCell(active).setComponent(activeFilter);
    activeFilter.setItems(Optional.empty(), Optional.of(false), Optional.of(true));
    activeFilter.addValueChangeListener(e -> presenter.filterActive(e.getValue().orElse(null)));
    activeFilter.setSizeFull();
    error.setId(ERROR_TEXT);
    add.setId(ADD);
    add.addClickListener(e -> presenter.add());
    switchUser.setId(SWITCH_USER);
    switchUser.addClickListener(e -> presenter.switchUser());
    presenter.init(this);
  }

  private Button activeButton(User user) {
    Button button = new Button();
    button.addClassName(ACTIVE);
    actives.put(user, button);
    updateActiveButton(button, user);
    button.addClickListener(e -> {
      presenter.toggleActive(user);
      updateActiveButton(button, user);
    });
    return button;
  }

  private void updateActiveButton(Button button, User user) {
    final MessageResource userResources = new MessageResource(User.class, getLocale());
    button.setIcon(user.isActive() ? VaadinIcon.EYE.create() : VaadinIcon.EYE_SLASH.create());
    button.setText(userResources.message(property(ACTIVE, user.isActive())));
    button.getElement().setAttribute(THEME, user.isActive() ? SUCCESS : ERROR);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final MessageResource resources = new MessageResource(UsersView.class, getLocale());
    final MessageResource userResources = new MessageResource(User.class, getLocale());
    final MessageResource webResources = new MessageResource(WebConstants.class, getLocale());
    header.setText(resources.message(HEADER));
    String emailHeader = userResources.message(EMAIL);
    email.setHeader(emailHeader).setFooter(emailHeader);
    String nameHeader = userResources.message(NAME);
    name.setHeader(nameHeader).setFooter(nameHeader);
    String laboratoryHeader = userResources.message(LABORATORY);
    laboratory.setHeader(laboratoryHeader).setFooter(laboratoryHeader);
    String activeHeader = userResources.message(ACTIVE);
    active.setHeader(activeHeader).setFooter(activeHeader);
    emailFilter.setPlaceholder(webResources.message(ALL));
    nameFilter.setPlaceholder(webResources.message(ALL));
    laboratoryFilter.setPlaceholder(webResources.message(ALL));
    activeFilter.setItemLabelGenerator(value -> value
        .map(bv -> userResources.message(property(ACTIVE, bv))).orElse(webResources.message(ALL)));
    actives.entrySet().stream().forEach(entry -> entry.getValue()
        .setText(userResources.message(property(ACTIVE, entry.getKey().isActive()))));
    add.setText(resources.message(ADD));
    add.setIcon(VaadinIcon.PLUS.create());
    switchUser.setText(resources.message(SWITCH_USER));
    switchUser.setIcon(VaadinIcon.BUG.create());
    presenter.localeChange(getLocale());
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    activeFilter.setValue(Optional.empty());
  }

  @Override
  public String getPageTitle() {
    MessageResource resources = new MessageResource(UsersView.class, getLocale());
    MessageResource webResources = new MessageResource(WebConstants.class, getLocale());
    return resources.message(TITLE, webResources.message(APPLICATION_NAME));
  }

  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    presenter.showError(event.getLocation().getQueryParameters().getParameters(), getLocale());
  }
}
