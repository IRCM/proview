package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.web.LaboratoryDialog.SAVED;
import static ca.qc.ircm.proview.user.web.UsersView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.testbench.BrowserTest;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.transaction.TestTransaction;

/**
 * Integration tests for {@link LaboratoryDialog}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class LaboratoryDialogIT extends AbstractBrowserTestCase {

  private static final String MESSAGES_PREFIX = messagePrefix(LaboratoryDialog.class);
  @Autowired
  private LaboratoryRepository repository;
  @Autowired
  private MessageSource messageSource;
  private final String name = "new laboratory name";

  private void open() {
    openView(VIEW_NAME);
  }

  private void fill(LaboratoryDialogElement dialog) {
    dialog.name().setValue(name);
  }

  @BrowserTest
  public void fieldsExistence() {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(0);
    view.viewLaboratory().click();
    LaboratoryDialogElement dialog = view.laboratoryDialog();
    assertTrue(optional(dialog::header).isPresent());
    assertTrue(optional(dialog::name).isPresent());
    assertTrue(optional(dialog::save).isPresent());
    assertTrue(optional(dialog::cancel).isPresent());
  }

  @BrowserTest
  public void save() {
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
    Assertions.assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + SAVED, new Object[]{name}, currentLocale()),
        notification.getText());
    Laboratory laboratory = repository.findById(1L).orElseThrow();
    Assertions.assertEquals(name, laboratory.getName());
    Assertions.assertEquals("Robot", laboratory.getDirector());
  }

  @BrowserTest
  public void cancel() {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(0);
    view.viewLaboratory().click();
    LaboratoryDialogElement dialog = view.laboratoryDialog();
    fill(dialog);

    dialog.cancel().click();

    assertFalse(optional(() -> $(NotificationElement.class).first()).isPresent());
    Laboratory laboratory = repository.findById(1L).orElseThrow();
    Assertions.assertEquals("Admin", laboratory.getName());
    Assertions.assertEquals("Robot", laboratory.getDirector());
  }
}
