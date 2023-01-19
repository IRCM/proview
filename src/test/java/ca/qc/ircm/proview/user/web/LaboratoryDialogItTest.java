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

import static ca.qc.ircm.proview.user.web.LaboratoryDialog.SAVED;
import static ca.qc.ircm.proview.user.web.UsersView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.transaction.TestTransaction;

/**
 * Integration tests for {@link LaboratoryDialog}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class LaboratoryDialogItTest extends AbstractTestBenchTestCase {
  @Autowired
  private LaboratoryRepository repository;
  private String name = "new laboratory name";

  private void open() {
    openView(VIEW_NAME);
  }

  private void fill(LaboratoryDialogElement dialog) {
    dialog.name().setValue(name);
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(0);
    view.viewLaboratory().click();
    LaboratoryDialogElement dialog = view.laboratoryDialog();
    assertTrue(optional(() -> dialog.header()).isPresent());
    assertTrue(optional(() -> dialog.name()).isPresent());
    assertTrue(optional(() -> dialog.save()).isPresent());
    assertTrue(optional(() -> dialog.cancel()).isPresent());
  }

  @Test
  public void save() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(0);
    view.viewLaboratory().click();
    LaboratoryDialogElement dialog = view.laboratoryDialog();
    fill(dialog);

    TestTransaction.flagForCommit();
    dialog.save().click();
    TestTransaction.end();

    NotificationElement notification = $(NotificationElement.class).waitForFirst();
    assertEquals(resources(LaboratoryDialog.class).message(SAVED, name), notification.getText());
    Laboratory laboratory = repository.findById(1L).get();
    assertEquals(name, laboratory.getName());
    assertEquals("Robot", laboratory.getDirector());
  }

  @Test
  public void cancel() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(0);
    view.viewLaboratory().click();
    LaboratoryDialogElement dialog = view.laboratoryDialog();
    fill(dialog);

    dialog.cancel().click();

    assertFalse(optional(() -> $(NotificationElement.class).first()).isPresent());
    Laboratory laboratory = repository.findById(1L).get();
    assertEquals("Admin", laboratory.getName());
    assertEquals("Robot", laboratory.getDirector());
  }
}
