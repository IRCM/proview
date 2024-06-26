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
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.user.UserProperties.ACTIVE;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.UserProperties.LABORATORY;
import static ca.qc.ircm.proview.user.UserProperties.NAME;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.security.SwitchUserService;
import ca.qc.ircm.proview.text.NormalizedComparator;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryService;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import ca.qc.ircm.proview.web.component.UrlComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Users view.
 */
@Route(value = UsersView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ MANAGER, ADMIN })
public class UsersView extends VerticalLayout implements LocaleChangeObserver, HasDynamicTitle,
    AfterNavigationObserver, NotificationComponent, UrlComponent {
  public static final String VIEW_NAME = "users";
  public static final String ID = "users-view";
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
      "<vaadin-button class='" + ACTIVE + "' .theme='${item.activeTheme}' @click='${toggleActive}'>"
          + "<vaadin-icon .icon='${item.activeIcon}' slot='prefix'></vaadin-icon>"
          + "${item.activeValue}" + "</vaadin-button>";
  public static final String EDIT_BUTTON =
      "<vaadin-button class='" + EDIT + "' theme='icon' @click='${edit}'>"
          + "<vaadin-icon icon='vaadin:edit' slot='prefix'></vaadin-icon>" + "</vaadin-button>";
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
  private WebUserFilter filter = new WebUserFilter();
  private transient ObjectFactory<UserDialog> dialogFactory;
  private transient ObjectFactory<LaboratoryDialog> laboratoryDialogFactory;
  private transient UserService service;
  private transient LaboratoryService laboratoryService;
  private transient SwitchUserService switchUserService;
  private transient AuthenticatedUser authenticatedUser;

  @Autowired
  protected UsersView(ObjectFactory<UserDialog> dialogFactory,
      ObjectFactory<LaboratoryDialog> laboratoryDialogFactory, UserService service,
      LaboratoryService laboratoryService, SwitchUserService switchUserService,
      AuthenticatedUser authenticatedUser) {
    this.dialogFactory = dialogFactory;
    this.laboratoryDialogFactory = laboratoryDialogFactory;
    this.service = service;
    this.laboratoryService = laboratoryService;
    this.switchUserService = switchUserService;
    this.authenticatedUser = authenticatedUser;
  }

  @SuppressWarnings("unchecked")
  @PostConstruct
  void init() {
    logger.debug("users view");
    setId(ID);
    setSizeFull();
    HorizontalLayout buttonsLayout = new HorizontalLayout();
    add(header, users, error, buttonsLayout);
    expand(users);
    buttonsLayout.add(add, switchUser, viewLaboratory);
    header.setId(HEADER);
    users.setId(USERS);
    users.addItemDoubleClickListener(e -> {
      if (e.getColumn() == laboratory) {
        viewLaboratory(e.getItem().getLaboratory());
      } else {
        view(e.getItem());
      }
    });
    edit =
        users.addColumn(LitRenderer.<User>of(EDIT_BUTTON).withFunction("edit", user -> view(user)))
            .setKey(EDIT).setSortable(false).setFlexGrow(0);
    email = users.addColumn(user -> user.getEmail(), EMAIL).setKey(EMAIL)
        .setComparator(NormalizedComparator.of(user -> user.getEmail())).setFlexGrow(3);
    name = users.addColumn(user -> user.getName(), NAME).setKey(NAME)
        .setComparator(NormalizedComparator.of(user -> user.getName())).setFlexGrow(3);
    laboratory =
        users.addColumn(user -> user.getLaboratory().getName(), LABORATORY).setKey(LABORATORY)
            .setComparator(NormalizedComparator.of(user -> user.getLaboratory().getName()))
            .setFlexGrow(3);
    active = users
        .addColumn(LitRenderer.<User>of(ACTIVE_BUTTON)
            .withProperty("activeTheme", user -> activeTheme(user))
            .withProperty("activeValue", user -> activeValue(user))
            .withProperty("activeIcon", user -> activeIcon(user))
            .withFunction("toggleActive", user -> toggleActive(user)))
        .setKey(ACTIVE).setComparator((u1, u2) -> Boolean.compare(u1.isActive(), u2.isActive()));
    active.setVisible(authenticatedUser.hasAnyRole(ADMIN, MANAGER));
    users.appendHeaderRow(); // Headers.
    HeaderRow filtersRow = users.appendHeaderRow();
    filtersRow.getCell(email).setComponent(emailFilter);
    emailFilter.addValueChangeListener(e -> filterEmail(e.getValue()));
    emailFilter.setValueChangeMode(ValueChangeMode.EAGER);
    emailFilter.setSizeFull();
    filtersRow.getCell(name).setComponent(nameFilter);
    nameFilter.addValueChangeListener(e -> filterName(e.getValue()));
    nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
    nameFilter.setSizeFull();
    filtersRow.getCell(laboratory).setComponent(laboratoryFilter);
    laboratoryFilter.addValueChangeListener(e -> filterLaboratory(e.getValue()));
    laboratoryFilter.setValueChangeMode(ValueChangeMode.EAGER);
    laboratoryFilter.setSizeFull();
    filtersRow.getCell(active).setComponent(activeFilter);
    activeFilter.setItems(Optional.empty(), Optional.of(false), Optional.of(true));
    activeFilter.addValueChangeListener(e -> filterActive(e.getValue().orElse(null)));
    activeFilter.setSizeFull();
    error.setId(ERROR_TEXT);
    error.setVisible(false);
    add.setId(ADD);
    add.addClickListener(e -> add());
    add.setVisible(authenticatedUser.hasAnyRole(ADMIN, MANAGER));
    switchUser.setId(SWITCH_USER);
    switchUser.addClickListener(e -> switchUser());
    switchUser.setVisible(authenticatedUser.hasRole(ADMIN));
    viewLaboratory.setId(VIEW_LABORATORY);
    viewLaboratory.addClickListener(e -> viewLaboratory());
    viewLaboratory.setVisible(authenticatedUser.hasAnyRole(ADMIN, MANAGER));

    loadUsers();
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

  private void loadUsers() {
    List<User> users = authenticatedUser.hasRole(ADMIN) ? service.all(null)
        : service.all(null, authenticatedUser.getUser().get().getLaboratory());
    this.users.setItems(users);
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
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    activeFilter.setValue(Optional.empty());
  }

  void filterEmail(String value) {
    filter.emailContains = value.isEmpty() ? null : value;
    users.getDataProvider().refreshAll();
  }

  void filterName(String value) {
    filter.nameContains = value.isEmpty() ? null : value;
    users.getDataProvider().refreshAll();
  }

  void filterLaboratory(String value) {
    filter.laboratoryNameContains = value.isEmpty() ? null : value;
    users.getDataProvider().refreshAll();
  }

  void filterActive(Boolean value) {
    filter.active = value;
    users.getDataProvider().refreshAll();
  }

  private void clearError() {
    error.setVisible(false);
  }

  @Override
  public String getPageTitle() {
    AppResources resources = new AppResources(UsersView.class, getLocale());
    AppResources webResources = new AppResources(Constants.class, getLocale());
    return resources.message(TITLE, webResources.message(APPLICATION_NAME));
  }

  void view(User user) {
    clearError();
    UserDialog dialog = dialogFactory.getObject();
    dialog.setUserId(user.getId());
    dialog.open();
    dialog.addSavedListener(e -> loadUsers());
  }

  void viewLaboratory() {
    clearError();
    User user = users.getSelectedItems().stream().findFirst().orElse(null);
    if (user == null) {
      AppResources resources = new AppResources(UsersView.class, getLocale());
      error.setText(resources.message(USERS_REQUIRED));
      error.setVisible(true);
    } else {
      viewLaboratory(user.getLaboratory());
    }
  }

  void viewLaboratory(Laboratory laboratory) {
    clearError();
    LaboratoryDialog laboratoryDialog = laboratoryDialogFactory.getObject();
    laboratoryDialog.setLaboratoryId(laboratory != null ? laboratory.getId() : null);
    laboratoryDialog.open();
    laboratoryDialog.addSavedListener(e -> loadUsers());
  }

  void toggleActive(User user) {
    clearError();
    user.setActive(!user.isActive());
    service.save(user, null);
    users.getDataProvider().refreshItem(user);
  }

  void switchUser() {
    clearError();
    User user = users.getSelectedItems().stream().findFirst().orElse(null);
    if (user == null) {
      AppResources resources = new AppResources(UsersView.class, getLocale());
      error.setText(resources.message(USERS_REQUIRED));
      error.setVisible(true);
    } else {
      switchUserService.switchUser(user, VaadinServletRequest.getCurrent());
      UI.getCurrent().getPage().setLocation(getUrl(MainView.VIEW_NAME));
    }
  }

  void add() {
    UserDialog dialog = dialogFactory.getObject();
    dialog.open();
    dialog.addSavedListener(e -> loadUsers());
  }

  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    Map<String, List<String>> parameters = event.getLocation().getQueryParameters().getParameters();
    AppResources resources = new AppResources(UsersView.class, getLocale());
    if (parameters.containsKey(SWITCH_FAILED)) {
      showNotification(resources.message(SWITCH_FAILED));
    }
  }

  WebUserFilter filter() {
    return filter;
  }
}
