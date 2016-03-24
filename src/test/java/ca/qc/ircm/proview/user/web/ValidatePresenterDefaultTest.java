package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.web.ValidatePresenter.EMAIL;
import static ca.qc.ircm.proview.user.web.ValidatePresenter.LABORATORY_NAME;
import static ca.qc.ircm.proview.user.web.ValidatePresenter.NAME;
import static ca.qc.ircm.proview.user.web.ValidatePresenter.ORGANIZATION;
import static ca.qc.ircm.proview.user.web.ValidatePresenter.VALIDATE;
import static ca.qc.ircm.proview.user.web.ValidatePresenter.VIEW;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.DatabaseRule;
import ca.qc.ircm.proview.test.config.Rules;
import ca.qc.ircm.proview.user.Signed;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickListener;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;

public class ValidatePresenterDefaultTest {
  @ClassRule
  public static DatabaseRule jpaDatabaseRule = new DatabaseRule();
  @Rule
  public RuleChain rules = Rules.defaultRules(this).around(jpaDatabaseRule);
  @InjectMocks
  private ValidatePresenterDefault validatePresenterDefault = new ValidatePresenterDefault();
  @Mock
  private ValidateView view;
  @Mock
  private UserService userService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Signed signed;
  @Captor
  private ArgumentCaptor<Collection<User>> usersCaptor;
  private Label headerLabel = new Label();
  private Grid usersGrid = new Grid();
  private Button validateSelectedButton = new Button();
  private EntityManager entityManager;
  private User signedUser;
  private List<User> usersToValidate;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(ValidateView.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    entityManager = jpaDatabaseRule.getEntityManager();
    signedUser = entityManager.find(User.class, 1L);
    when(signed.getUser()).thenReturn(signedUser);
    usersToValidate = new ArrayList<>();
    usersToValidate.add(entityManager.find(User.class, 4L));
    usersToValidate.add(entityManager.find(User.class, 5L));
    usersToValidate.add(entityManager.find(User.class, 10L));
    when(userService.all(any())).thenReturn(usersToValidate);
    when(view.getHeaderLabel()).thenReturn(headerLabel);
    when(view.getUsersGrid()).thenReturn(usersGrid);
    when(view.getValidateSelectedButton()).thenReturn(validateSelectedButton);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    validatePresenterDefault.init(view);
  }

  private User find(Collection<User> users, long id) {
    for (User user : users) {
      if (id == user.getId()) {
        return user;
      }
    }
    return null;
  }

  @Test
  public void usersGridColumns() {
    List<Column> columns = usersGrid.getColumns();

    assertEquals(EMAIL, columns.get(0).getPropertyId());
    assertEquals(NAME, columns.get(1).getPropertyId());
    assertEquals(LABORATORY_NAME, columns.get(2).getPropertyId());
    assertEquals(ORGANIZATION, columns.get(3).getPropertyId());
    assertEquals(VIEW, columns.get(4).getPropertyId());
    assertEquals(VALIDATE, columns.get(5).getPropertyId());
  }

  @Test
  public void usersGridSelection() {
    SelectionModel selectionModel = usersGrid.getSelectionModel();

    assertTrue(selectionModel instanceof SelectionModel.Multi);
  }

  @Test
  public void usersGridOrder() {
    List<SortOrder> sortOrders = usersGrid.getSortOrder();

    assertFalse(sortOrders.isEmpty());
    SortOrder sortOrder = sortOrders.get(0);
    assertEquals(EMAIL, sortOrder.getPropertyId());
    assertEquals(SortDirection.ASCENDING, sortOrder.getDirection());
  }

  @Test
  public void title() {
    verify(view).setTitle(resources.message("title"));
  }

  @Test
  public void captions() {
    assertEquals(resources.message("header"), headerLabel.getValue());
    assertEquals(resources.message("validateSelected"), validateSelectedButton.getCaption());
  }

  @Test
  @SuppressWarnings({ "serial", "unchecked" })
  public void viewUser() {
    final User user = usersToValidate.get(0);
    Column column = usersGrid.getColumn(VIEW);
    ButtonRenderer renderer = (ButtonRenderer) column.getRenderer();
    RendererClickListener viewListener =
        ((Collection<RendererClickListener>) renderer.getListeners(RendererClickEvent.class))
            .iterator().next();

    viewListener.click(new RendererClickEvent(usersGrid, user, column, new MouseEventDetails()) {});

    verify(view).viewUser(user);
  }

  @Test
  @SuppressWarnings({ "serial", "unchecked" })
  public void validateOne() {
    final User user = usersToValidate.get(0);
    Column column = usersGrid.getColumn(VALIDATE);
    ButtonRenderer renderer = (ButtonRenderer) column.getRenderer();
    RendererClickListener viewListener =
        ((Collection<RendererClickListener>) renderer.getListeners(RendererClickEvent.class))
            .iterator().next();
    List<User> usersToValidateAfter = new ArrayList<>(usersToValidate);
    usersToValidateAfter.remove(0);
    when(userService.all(any())).thenReturn(usersToValidateAfter);

    viewListener.click(new RendererClickEvent(usersGrid, user, column, new MouseEventDetails()) {});

    verify(userService).validate(usersCaptor.capture());
    Collection<User> users = usersCaptor.getValue();
    assertEquals(1, users.size());
    assertNotNull(find(users, user.getId()));
    verify(view).afterSuccessfulValidate(resources.message("done", 1, user.getEmail()));
    verify(userService, times(2)).all(any());
    assertEquals(usersToValidateAfter.size(),
        usersGrid.getContainerDataSource().getItemIds().size());
  }

  @Test
  public void validateMany() {
    final User user1 = usersToValidate.get(0);
    final User user2 = usersToValidate.get(1);
    final User user3 = usersToValidate.get(2);
    usersGrid.select(user1);
    usersGrid.select(user2);
    usersGrid.select(user3);
    when(userService.all(any())).thenReturn(new ArrayList<>());

    validateSelectedButton.click();

    verify(userService).validate(usersCaptor.capture());
    Collection<User> users = usersCaptor.getValue();
    assertEquals(3, users.size());
    assertNotNull(find(users, user1.getId()));
    assertNotNull(find(users, user2.getId()));
    assertNotNull(find(users, user3.getId()));
    verify(view).afterSuccessfulValidate(
        resources.message("done", 3, user1.getEmail() + resources.message("userSeparator", 0)
            + user2.getEmail() + resources.message("userSeparator", 1) + user3.getEmail()));
    verify(userService, times(2)).all(any());
    assertEquals(0, usersGrid.getSelectedRows().size());
    assertEquals(0, usersGrid.getContainerDataSource().getItemIds().size());
  }
}
