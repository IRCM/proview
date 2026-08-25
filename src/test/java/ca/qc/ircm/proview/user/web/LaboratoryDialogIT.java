package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.user.web.LaboratoryDialog.SAVED;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryRepository;
import ca.qc.ircm.proview.user.LaboratoryService;
import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.notification.Notification;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

/**
 * Integration tests for {@link LaboratoryDialog}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class LaboratoryDialogIT extends SpringBrowserlessTest {

  private static final String MESSAGES_PREFIX = messagePrefix(LaboratoryDialog.class);
  @MockitoSpyBean
  private LaboratoryService laboratoryService;
  @Autowired
  private LaboratoryRepository repository;
  @Autowired
  private EntityManager entityManager;
  private final String name = "new laboratory name";

  private void fill(LaboratoryDialog dialog) {
    test(dialog.name).setValue(name);
  }

  private void detachOnServiceGet() {
    when(laboratoryService.get(anyLong())).then(a -> {
      @SuppressWarnings("unchecked") Optional<Laboratory> optionalLaboratory = (Optional<Laboratory>) a.callRealMethod();
      optionalLaboratory.ifPresent(d -> entityManager.detach(d));
      return optionalLaboratory;
    });
  }

  @Test
  public void save() {
    detachOnServiceGet();
    UsersView view = navigate(UsersView.class);
    test(view.users).select(0);
    test(view.viewLaboratory).click();
    LaboratoryDialog dialog = $(LaboratoryDialog.class).single();
    fill(dialog);

    test(dialog.save).click();

    Notification notification = $(Notification.class).single();
    Assertions.assertEquals(dialog.getTranslation(MESSAGES_PREFIX + SAVED, name),
        test(notification).getText());
    Laboratory laboratory = repository.findById(1L).orElseThrow();
    Assertions.assertEquals(name, laboratory.getName());
    Assertions.assertEquals("Robot", laboratory.getDirector());
  }

  @Test
  public void cancel() {
    detachOnServiceGet();
    UsersView view = navigate(UsersView.class);
    test(view.users).select(0);
    test(view.viewLaboratory).click();
    LaboratoryDialog dialog = $(LaboratoryDialog.class).single();
    fill(dialog);

    test(dialog.cancel).click();

    assertFalse($(Notification.class).exists());
    Laboratory laboratory = repository.findById(1L).orElseThrow();
    Assertions.assertEquals("Admin", laboratory.getName());
    Assertions.assertEquals("Robot", laboratory.getDirector());
  }
}
