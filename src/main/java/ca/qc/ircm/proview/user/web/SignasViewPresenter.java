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

import ca.qc.ircm.proview.laboratory.QLaboratory;
import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.user.QUser;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserFilterBuilder;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.utils.web.FilterTextChangeListener;
import ca.qc.ircm.proview.web.MainUi;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.TextField;
import de.datenhahn.vaadin.componentrenderer.ComponentCellKeyExtension;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Sign as another user view.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SignasViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String USERS_GRID = "users";
  public static final String EMAIL = QUser.user.email.getMetadata().getName();
  public static final String NAME = QUser.user.name.getMetadata().getName();
  public static final String LABORATORY_PREFIX =
      QUser.user.laboratory.getMetadata().getName() + ".";
  public static final String LABORATORY_NAME =
      LABORATORY_PREFIX + QLaboratory.laboratory.name.getMetadata().getName();
  public static final String ORGANIZATION =
      LABORATORY_PREFIX + QLaboratory.laboratory.organization.getMetadata().getName();
  public static final String VIEW = "view";
  public static final String SIGN_AS = "signas";
  public static final String ALL = "all";
  public static final String COMPONENTS = "components";
  public static final String[] COLUMNS =
      { EMAIL, NAME, LABORATORY_NAME, ORGANIZATION, VIEW, SIGN_AS };
  private static final Logger logger = LoggerFactory.getLogger(SignasViewPresenter.class);
  private SignasView view;
  private BeanItemContainer<User> container;
  private GeneratedPropertyContainer gridContainer;
  @Inject
  private UserService userService;
  @Inject
  private AuthenticationService authenticationService;
  @Inject
  private MainUi ui;
  @Inject
  private Provider<UserWindow> userWindowProvider;
  @Value("${spring.application.name}")
  private String applicationName;

  public SignasViewPresenter() {
  }

  protected SignasViewPresenter(UserService userService,
      AuthenticationService authenticationService, MainUi ui,
      Provider<UserWindow> userWindowProvider, String applicationName) {
    this.userService = userService;
    this.authenticationService = authenticationService;
    this.ui = ui;
    this.userWindowProvider = userWindowProvider;
    this.applicationName = applicationName;
  }

  /**
   * Called by view when view is attached.
   *
   * @param view
   *          view
   */
  public void init(SignasView view) {
    this.view = view;
    logger.debug("Users access view");
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.addStyleName("h1");
    view.headerLabel.setValue(resources.message(HEADER));
    view.usersGrid.addStyleName(USERS_GRID);
    prepareUsersGrid();
  }

  @SuppressWarnings("serial")
  private void prepareUsersGrid() {
    MessageResource resources = view.getResources();
    container = new BeanItemContainer<>(User.class, searchUsers());
    container.addNestedContainerProperty(LABORATORY_NAME);
    container.addNestedContainerProperty(ORGANIZATION);
    gridContainer = new GeneratedPropertyContainer(container);
    gridContainer.addGeneratedProperty(VIEW, new PropertyValueGenerator<Button>() {
      @Override
      public Button getValue(Item item, Object itemId, Object propertyId) {
        MessageResource resources = view.getResources();
        User user = (User) itemId;
        Button button = new Button();
        button.setCaption(resources.message(VIEW));
        button.addClickListener(event -> viewUser(user));
        return button;
      }

      @Override
      public Class<Button> getType() {
        return Button.class;
      }
    });
    gridContainer.addGeneratedProperty(SIGN_AS, new PropertyValueGenerator<Button>() {
      @Override
      public Button getValue(Item item, Object itemId, Object propertyId) {
        MessageResource resources = view.getResources();
        User user = (User) itemId;
        Button button = new Button();
        button.setCaption(resources.message(SIGN_AS));
        button.addClickListener(event -> signasUser(user));
        return button;
      }

      @Override
      public Class<Button> getType() {
        return Button.class;
      }
    });
    ComponentCellKeyExtension.extend(view.usersGrid);
    view.usersGrid.setContainerDataSource(gridContainer);
    view.usersGrid.setColumns((Object[]) COLUMNS);
    view.usersGrid.setFrozenColumnCount(2);
    view.usersGrid.getColumn(VIEW).setRenderer(new ComponentRenderer());
    view.usersGrid.getColumn(SIGN_AS).setRenderer(new ComponentRenderer());
    view.usersGrid.addStyleName(COMPONENTS);
    view.usersGrid.sort(EMAIL, SortDirection.ASCENDING);
    for (String propertyId : COLUMNS) {
      view.usersGrid.getColumn(propertyId).setHeaderCaption(resources.message(propertyId));
    }
    HeaderRow filterRow = view.usersGrid.appendHeaderRow();
    for (String propertyId : COLUMNS) {
      HeaderCell cell = filterRow.getCell(propertyId);
      if (propertyId.equals(EMAIL)) {
        cell.setComponent(createFilterTextField(propertyId, resources));
      } else if (propertyId.equals(NAME)) {
        cell.setComponent(createFilterTextField(propertyId, resources));
      } else if (propertyId.equals(LABORATORY_NAME)) {
        cell.setComponent(createFilterTextField(propertyId, resources));
      } else if (propertyId.equals(ORGANIZATION)) {
        cell.setComponent(createFilterTextField(propertyId, resources));
      }
    }
  }

  private TextField createFilterTextField(Object propertyId, MessageResource resources) {
    TextField filter = new TextField();
    filter.addTextChangeListener(
        new FilterTextChangeListener(gridContainer, propertyId, true, false));
    filter.setWidth("100%");
    filter.addStyleName("tiny");
    filter.setInputPrompt(resources.message(ALL));
    return filter;
  }

  private List<User> searchUsers() {
    UserFilterBuilder parameters = new UserFilterBuilder();
    parameters.onlyNonAdmin();
    parameters.onlyActive();
    return userService.all(parameters);
  }

  private void viewUser(User user) {
    UserWindow userWindow = userWindowProvider.get();
    userWindow.center();
    userWindow.setUser(user);
    ui.addWindow(userWindow);
  }

  private void signasUser(User user) {
    MessageResource resources = view.getResources();
    authenticationService.runAs(user);
    view.showTrayNotification(resources.message(SIGN_AS + ".done", user.getEmail()));
    view.navigateTo(MainView.VIEW_NAME);
  }
}
