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
