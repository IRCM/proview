package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
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
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.transaction.TestTransaction;

/**
 * Integration tests for {@link LaboratoryDialog}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class LaboratoryDialogItTest extends AbstractTestBenchTestCase {
  private static final String MESSAGES_PREFIX = messagePrefix(LaboratoryDialog.class);
  @Autowired
  private LaboratoryRepository repository;
  @Autowired
  private MessageSource messageSource;
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
    assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + SAVED, new Object[] { name }, currentLocale()),
        notification.getText());
    Laboratory laboratory = repository.findById(1L).orElseThrow();
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
    Laboratory laboratory = repository.findById(1L).orElseThrow();
    assertEquals("Admin", laboratory.getName());
    assertEquals("Robot", laboratory.getDirector());
  }
}
