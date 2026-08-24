package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.UI;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for {@link ViewLayout}.
 */
@ServiceTestAnnotations
@ActiveProfiles({"test", "context-path"})
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class ViewLayoutContextPathIT extends SpringUIUnitTest {

  @Test
  public void changeLanguage() {
    navigate(ContactView.class);
    assertEquals(UI.getCurrent().getLocale(), ENGLISH);
    ViewLayout view = $(ViewLayout.class).single();
    test(view.changeLanguage).click();
    $(ContactView.class).single();
    assertEquals(UI.getCurrent().getLocale(), FRENCH);
  }
}
