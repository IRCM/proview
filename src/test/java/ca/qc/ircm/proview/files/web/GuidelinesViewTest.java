package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.files.web.GuidelinesView.ID;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findChild;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findChildren;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.files.Guideline;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.google.common.net.UrlEscapers;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link GuidelinesView}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class GuidelinesViewTest extends SpringUIUnitTest {
  private static final String MESSAGES_PREFIX = messagePrefix(GuidelinesView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private GuidelinesView view;
  @Autowired
  private GuidelinesConfiguration guidelinesConfiguration;
  private Locale locale = ENGLISH;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    view = navigate(GuidelinesView.class);
  }

  @Test
  public void ids() {
    assertEquals(ID, view.getId().orElse(null));
  }

  @Test
  public void labels() {
    List<Category> categories = guidelinesConfiguration.categories(locale);
    List<CategoryComponent> categoryComponents = findChildren(view, CategoryComponent.class);
    for (int i = 0; i < categories.size(); i++) {
      Category category = categories.get(i);
      CategoryComponent categoryComponent = categoryComponents.get(i);
      assertEquals(category.getName(), categoryComponent.header.getText());
      List<Anchor> anchors = findChildren(categoryComponent, Anchor.class);
      for (int j = 0; j < category.getGuidelines().size(); j++) {
        Guideline guideline = category.getGuidelines().get(j);
        Anchor anchor = anchors.get(j);
        Span text = findChild(anchor, Span.class).get();
        assertEquals(guideline.getName(), text.getText());
        validateIcon(VaadinIcon.DOWNLOAD.create(), findChild(anchor, Icon.class).get());
      }
    }
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    List<Category> categories = guidelinesConfiguration.categories(locale);
    List<CategoryComponent> categoryComponents = findChildren(view, CategoryComponent.class);
    for (int i = 0; i < categories.size(); i++) {
      Category category = categories.get(i);
      CategoryComponent categoryComponent = categoryComponents.get(i);
      assertEquals(category.getName(), categoryComponent.header.getText());
      List<Anchor> anchors = findChildren(categoryComponent, Anchor.class);
      for (int j = 0; j < category.getGuidelines().size(); j++) {
        Guideline guideline = category.getGuidelines().get(j);
        Anchor anchor = anchors.get(j);
        Span text = findChild(anchor, Span.class).get();
        assertEquals(guideline.getName(), text.getText());
        validateIcon(VaadinIcon.DOWNLOAD.create(), findChild(anchor, Icon.class).get());
      }
    }
  }

  @Test
  public void hrefs() throws Throwable {
    List<Category> categories = guidelinesConfiguration.categories(locale);
    List<CategoryComponent> categoryComponents = findChildren(view, CategoryComponent.class);
    for (int i = 0; i < categories.size(); i++) {
      Category category = categories.get(i);
      CategoryComponent categoryComponent = categoryComponents.get(i);
      List<Anchor> anchors = findChildren(categoryComponent, Anchor.class);
      for (int j = 0; j < category.getGuidelines().size(); j++) {
        Guideline guideline = category.getGuidelines().get(j);
        Anchor anchor = anchors.get(j);
        String guidelinePath =
            UrlEscapers.urlFragmentEscaper().escape(guideline.getPath().getFileName().toString());
        assertEquals(guidelinePath, Paths.get(anchor.getHref()).getFileName().toString());
      }
    }
  }

  @Test
  public void getPageTitle() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + TITLE,
        view.getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME)), view.getPageTitle());
  }
}
