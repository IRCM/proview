package ca.qc.ircm.proview.utils.web;

import static ca.qc.ircm.proview.user.QUser.user;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.user.User;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import org.junit.Before;
import org.junit.Test;

public class EmptyNullTableFieldFactoryTest {
  private EmptyNullTableFieldFactory factory;

  @Before
  public void beforeTest() {
    factory = new EmptyNullTableFieldFactory();
  }

  @Test
  public void createField_TextField() {
    Object propertyId = user.email.getMetadata().getName();
    BeanItemContainer<User> container = new BeanItemContainer<>(User.class);
    User user = new User(1L, "christian.poitras@ircm.qc.ca");
    container.addItem(user);
    Table table = new Table();

    Field<?> field = factory.createField(container, user, propertyId, table);

    assertTrue(field instanceof TextField);
    TextField textField = (TextField) field;
    assertEquals("", textField.getNullRepresentation());
  }
}
