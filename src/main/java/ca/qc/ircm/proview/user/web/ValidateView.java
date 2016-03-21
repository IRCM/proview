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
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.QUser;
import ca.qc.ircm.proview.user.SearchUserParametersBuilder;
import ca.qc.ircm.proview.user.Signed;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.renderers.ButtonRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Validate users.
 */
@SpringView(name = ValidateView.VIEW_NAME)
@RolesAllowed({ "PROTEOMIC", "MANAGER" })
public class ValidateView extends ValidateDesign implements MessageResourcesView {
  private static final long serialVersionUID = -1956061543048432065L;
  public static final String VIEW_NAME = "user/validate";
  private static final Logger logger = LoggerFactory.getLogger(ValidateView.class);
  private static final String EMAIL = QUser.user.email.getMetadata().getName();
  private static final String NAME = QUser.user.name.getMetadata().getName();
  private static final String LABORATORY_PREFIX =
      QUser.user.laboratory.getMetadata().getName() + ".";
  private static final String LABORATORY_NAME =
      LABORATORY_PREFIX + QLaboratory.laboratory.name.getMetadata().getName();
  private static final String ORGANIZATION =
      LABORATORY_PREFIX + QLaboratory.laboratory.organization.getMetadata().getName();
  private static final String VIEW = "viewUser";
  private static final String VALIDATE = "validateUser";
  private static final String[] COLUMNS =
      { EMAIL, NAME, LABORATORY_NAME, ORGANIZATION, VIEW, VALIDATE };
  private BeanItemContainer<User> container;
  @Inject
  private UserService userService;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private Signed signed;

  /**
   * Initialize view.
   */
  @SuppressWarnings("serial")
  @PostConstruct
  public void init() {
    container = new BeanItemContainer<User>(User.class, searchUsers());
    container.addNestedContainerProperty(LABORATORY_NAME);
    container.addNestedContainerProperty(ORGANIZATION);
    final GeneratedPropertyContainer gridContainer = new GeneratedPropertyContainer(container);
    gridContainer.addGeneratedProperty(VIEW, new PropertyValueGenerator<String>() {
      @Override
      public String getValue(Item item, Object itemId, Object propertyId) {
        MessageResource resources = getResources();
        return resources.message(VIEW);
      }

      @Override
      public Class<String> getType() {
        return String.class;
      }
    });
    gridContainer.addGeneratedProperty(VALIDATE, new PropertyValueGenerator<String>() {
      @Override
      public String getValue(Item item, Object itemId, Object propertyId) {
        MessageResource resources = getResources();
        return resources.message(VALIDATE);
      }

      @Override
      public Class<String> getType() {
        return String.class;
      }
    });
    users.setContainerDataSource(gridContainer);
    users.setColumns((Object[]) COLUMNS);
    users.setSelectionMode(SelectionMode.MULTI);
    users.sort(EMAIL, SortDirection.ASCENDING);
    Grid.Column viewColumn = users.getColumn(VIEW);
    viewColumn.setRenderer(new ButtonRenderer(event -> {
      User user = (User) event.getItemId();
      viewUser(user);
    }));
    Grid.Column validateColumn = users.getColumn(VALIDATE);
    validateColumn.setRenderer(new ButtonRenderer(event -> {
      User user = (User) event.getItemId();
      validateUser(user);
    }));
    validateSelected.addClickListener(event -> {
      validateMany();
    });
  }

  @Override
  public void attach() {
    super.attach();
    logger.debug("Validate users view");
    final MessageResource resources = getResources();
    getUI().getPage().setTitle(resources.message("title"));
    header.setValue(resources.message("header"));
    validateSelected.setCaption(resources.message("validateSelected"));
  }

  private List<User> searchUsers() {
    SearchUserParametersBuilder parameters = new SearchUserParametersBuilder();
    parameters = parameters.onlyInvalid();
    if (!authorizationService.hasProteomicRole()) {
      parameters = parameters.inLaboratory(signed.getUser().getLaboratory());
    }
    return userService.all(parameters);
  }

  private void refresh() {
    users.getSelectionModel().reset();
    container.removeAllItems();
    container.addAll(searchUsers());
    users.setSortOrder(new ArrayList<>(users.getSortOrder()));
  }

  private void viewUser(User user) {
    Notification.show("View user clicked for user " + user.getEmail());
  }

  private void validateUser(User user) {
    userService.validate(Collections.nCopies(1, user));
    final MessageResource resources = getResources();
    Notification.show(resources.message("done", 1, user.getEmail()),
        Notification.Type.TRAY_NOTIFICATION);
    refresh();
  }

  private void validateMany() {
    Collection<Object> ids = users.getSelectedRows();
    List<User> selected = new ArrayList<>();
    for (Object id : ids) {
      selected.add((User) id);
    }
    userService.validate(selected);
    final MessageResource resources = getResources();
    StringBuilder emails = new StringBuilder();
    for (int i = 0; i < selected.size(); i++) {
      User user = selected.get(i);
      emails.append(user.getEmail());
      if (i == selected.size() - 2) {
        emails.append(resources.message("userSeparator", 1));
      } else if (i < selected.size() - 2) {
        emails.append(resources.message("userSeparator", 0));
      }
    }
    Notification.show(resources.message("done", selected.size(), emails),
        Notification.Type.TRAY_NOTIFICATION);
    refresh();
  }
}
