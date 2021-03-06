/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.ALL;
import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.EDIT;
import static ca.qc.ircm.proview.Constants.ERROR_TEXT;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.security.web.WebSecurityConfiguration.SWITCH_USERNAME_PARAMETER;
import static ca.qc.ircm.proview.security.web.WebSecurityConfiguration.SWITCH_USER_URL;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.user.UserProperties.ACTIVE;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.LABORATORY;
import static ca.qc.ircm.proview.user.UserProperties.NAME;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.text.NormalizedComparator;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Users view.
 */
@Route(value = UsersView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ MANAGER, ADMIN })
public class UsersView extends VerticalLayout implements LocaleChangeObserver, HasDynamicTitle,
    AfterNavigationObserver, NotificationComponent {
  public static final String VIEW_NAME = "users";
  public static final String ID = styleName(VIEW_NAME, "view");
  public static final String HEADER = "header";
  public static final String USERS = "users";
  public static final String USERS_REQUIRED = property(USERS, REQUIRED);
  public static final String SWITCH_USER = "switchUser";
  public static final String SWITCH_USER_FORM = "switchUserform";
  public static final String SWITCH_USERNAME = "switchUsername";
  public static final String SWITCH_FAILED = "switchFailed";
  public static final String ADD = "add";
  public static final String VIEW_LABORATORY = "viewLaboratory";
  public static final String ACTIVE_BUTTON =
      "<vaadin-button class='" + ACTIVE + "' theme$='[[item.activeTheme]]' on-click='toggleActive'>"
          + "<iron-icon icon$='[[item.activeIcon]]' slot='prefix'></iron-icon>"
          + "[[item.activeValue]]" + "</vaadin-button>";
  public static final String EDIT_BUTTON =
      "<vaadin-button class='" + EDIT + "' theme='icon' on-click='edit'>"
          + "<iron-icon icon='vaadin:edit' slot='prefix'></iron-icon>" + "</vaadin-button>";
  private static final long serialVersionUID = 1051684045824404864L;
  private static final Logger logger = LoggerFactory.getLogger(UsersView.class);
  protected H2 header = new H2();
  protected Grid<User> users = new Grid<>();
  protected Column<User> edit;
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
  protected Button viewLaboratory = new Button();
  protected Html switchUserForm = new Html(
      "<form action=\"" + SWITCH_USER_URL + "\" method=\"post\" style=\"display:none;\"></form>");
  protected Html switchUsername = new Html("<input name=\"" + SWITCH_USERNAME_PARAMETER + "\">");
  protected UserDialog dialog;
  protected LaboratoryDialog laboratoryDialog;
  private transient UsersViewPresenter presenter;

  @Autowired
  protected UsersView(UsersViewPresenter presenter, UserDialog dialog,
      LaboratoryDialog laboratoryDialog) {
    this.presenter = presenter;
    this.dialog = dialog;
    this.laboratoryDialog = laboratoryDialog;
  }

  @SuppressWarnings("unchecked")
  @PostConstruct
  void init() {
    logger.debug("users view");
    setId(ID);
    setSizeFull();
    HorizontalLayout buttonsLayout = new HorizontalLayout();
    add(header, users, error, buttonsLayout, switchUserForm);
    expand(users);
    buttonsLayout.add(add, switchUser, viewLaboratory);
    header.setId(HEADER);
    users.setId(USERS);
    users.addItemDoubleClickListener(e -> {
      if (e.getColumn() == laboratory) {
        presenter.viewLaboratory(e.getItem().getLaboratory());
      } else {
        presenter.view(e.getItem());
      }
    });
    edit = users.addColumn(TemplateRenderer.<User>of(EDIT_BUTTON).withEventHandler("edit",
        user -> presenter.view(user)), EDIT).setKey(EDIT).setSortable(false).setFlexGrow(0);
    email = users.addColumn(user -> user.getEmail(), EMAIL).setKey(EMAIL)
        .setComparator(NormalizedComparator.of(user -> user.getEmail())).setFlexGrow(3);
    name = users.addColumn(user -> user.getName(), NAME).setKey(NAME)
        .setComparator(NormalizedComparator.of(user -> user.getName())).setFlexGrow(3);
    laboratory =
        users.addColumn(user -> user.getLaboratory().getName(), LABORATORY).setKey(LABORATORY)
            .setComparator(NormalizedComparator.of(user -> user.getLaboratory().getName()))
            .setFlexGrow(3);
    active = users.addColumn(TemplateRenderer.<User>of(ACTIVE_BUTTON)
        .withProperty("activeTheme", user -> activeTheme(user))
        .withProperty("activeValue", user -> activeValue(user))
        .withProperty("activeIcon", user -> activeIcon(user))
        .withEventHandler("toggleActive", user -> {
          presenter.toggleActive(user);
          users.getDataProvider().refreshItem(user);
        }), ACTIVE).setKey(ACTIVE)
        .setComparator((u1, u2) -> Boolean.compare(u1.isActive(), u2.isActive()));
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
    switchUserForm.setId(SWITCH_USER_FORM);
    switchUserForm.getElement().appendChild(switchUsername.getElement());
    switchUsername.setId(SWITCH_USERNAME);
    viewLaboratory.setId(VIEW_LABORATORY);
    viewLaboratory.addClickListener(e -> presenter.viewLaboratory());
    presenter.init(this);
  }

  private String activeTheme(User user) {
    return user.isActive() ? ButtonVariant.LUMO_SUCCESS.getVariantName()
        : ButtonVariant.LUMO_ERROR.getVariantName();
  }

  private String activeValue(User user) {
    final AppResources userResources = new AppResources(User.class, getLocale());
    return userResources.message(property(ACTIVE, user.isActive()));
  }

  private String activeIcon(User user) {
    return user.isActive() ? "vaadin:eye" : "vaadin:eye-slash";
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final AppResources resources = new AppResources(UsersView.class, getLocale());
    final AppResources userResources = new AppResources(User.class, getLocale());
    final AppResources webResources = new AppResources(Constants.class, getLocale());
    header.setText(resources.message(HEADER));
    String editHeader = webResources.message(EDIT);
    edit.setHeader(editHeader).setFooter(editHeader);
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
    add.setText(resources.message(ADD));
    add.setIcon(VaadinIcon.PLUS.create());
    switchUser.setText(resources.message(SWITCH_USER));
    switchUser.setIcon(VaadinIcon.BUG.create());
    viewLaboratory.setText(resources.message(VIEW_LABORATORY));
    viewLaboratory.setIcon(VaadinIcon.EDIT.create());
    presenter.localeChange(getLocale());
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    activeFilter.setValue(Optional.empty());
  }

  @Override
  public String getPageTitle() {
    AppResources resources = new AppResources(UsersView.class, getLocale());
    AppResources webResources = new AppResources(Constants.class, getLocale());
    return resources.message(TITLE, webResources.message(APPLICATION_NAME));
  }

  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    presenter.showError(event.getLocation().getQueryParameters().getParameters(), getLocale());
  }
}
