package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.web.StandardsFormPresenter.COMMENT;
import static ca.qc.ircm.proview.sample.web.StandardsFormPresenter.COUNT;
import static ca.qc.ircm.proview.sample.web.StandardsFormPresenter.FILL;
import static ca.qc.ircm.proview.sample.web.StandardsFormPresenter.NAME;
import static ca.qc.ircm.proview.sample.web.StandardsFormPresenter.QUANTITY;
import static ca.qc.ircm.proview.sample.web.StandardsFormPresenter.STANDARDS;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.errorMessage;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ComponentRenderer;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class StandardsFormPresenterTest {
  private StandardsFormPresenter presenter;
  @Mock
  private StandardsForm view;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  private StandardsFormDesign design = new StandardsFormDesign();
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(StandardsForm.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);

  @Before
  public void beforeTest() {
    presenter = new StandardsFormPresenter();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
  }

  private Standard standard() {
    Standard standard = new Standard();
    standard.setId(RandomUtils.nextLong());
    standard.setName(RandomStringUtils.randomAlphabetic(10));
    standard.setQuantity(RandomStringUtils.randomAlphabetic(12));
    standard.setComment(RandomStringUtils.randomAlphabetic(50));
    return standard;
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.count.getStyleName().contains(COUNT));
    assertTrue(design.standards.getStyleName().contains(STANDARDS));
    assertTrue(design.fill.getStyleName().contains(FILL));
  }

  @Test
  public void captions() {
    presenter.init(view);

    assertEquals(resources.message(COUNT), design.count.getCaption());
    assertEquals(resources.message(FILL), design.fill.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.fill.getIcon());
  }

  @Test
  public void visible_Default() {
    presenter.init(view);

    assertFalse(design.standardsLayout.isVisible());
  }

  @Test
  public void visible_ZeroCount() {
    presenter.init(view);
    design.count.setValue("0");

    assertFalse(design.standardsLayout.isVisible());
  }

  @Test
  public void visible_OneCount() {
    presenter.init(view);
    design.count.setValue("1");

    assertTrue(design.standardsLayout.isVisible());
    assertTrue(design.fill.isVisible());
  }

  @Test
  public void visible_OneCount_ReadOnly() {
    presenter.init(view);
    design.count.setValue("1");

    presenter.setReadOnly(true);

    assertTrue(design.standardsLayout.isVisible());
    assertFalse(design.fill.isVisible());
  }

  @Test
  public void standards() {
    presenter.init(view);
    List<Standard> standards = Arrays.asList(standard(), standard());
    presenter.setValue(standards);

    assertEquals(3, design.standards.getColumns().size());
    assertEquals(NAME, design.standards.getColumns().get(0).getId());
    assertTrue(containsInstanceOf(design.standards.getColumn(NAME).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.standards.getColumn(NAME).isSortable());
    for (Standard standard : standards) {
      TextField field =
          (TextField) design.standards.getColumn(NAME).getValueProvider().apply(standard);
      assertTrue(field.getStyleName().contains(NAME));
      assertEquals(standard.getName(), field.getValue());
      assertFalse(field.isReadOnly());
    }
    assertEquals(QUANTITY, design.standards.getColumns().get(1).getId());
    assertTrue(containsInstanceOf(design.standards.getColumn(QUANTITY).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.standards.getColumn(QUANTITY).isSortable());
    for (Standard standard : standards) {
      TextField field =
          (TextField) design.standards.getColumn(QUANTITY).getValueProvider().apply(standard);
      assertTrue(field.getStyleName().contains(QUANTITY));
      assertEquals(standard.getQuantity(), field.getValue());
      assertFalse(field.isReadOnly());
    }
    assertEquals(COMMENT, design.standards.getColumns().get(2).getId());
    assertTrue(containsInstanceOf(design.standards.getColumn(COMMENT).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.standards.getColumn(COMMENT).isSortable());
    for (Standard standard : standards) {
      TextField field =
          (TextField) design.standards.getColumn(COMMENT).getValueProvider().apply(standard);
      assertTrue(field.getStyleName().contains(COMMENT));
      assertEquals(standard.getComment(), field.getValue());
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void standards_AfterSetValue() {
    presenter.init(view);
    List<Standard> standards = Arrays.asList(standard(), standard());
    presenter.setValue(standards);

    assertFalse(design.standards.getColumn(NAME).isSortable());
    assertFalse(design.standards.getColumn(QUANTITY).isSortable());
    assertFalse(design.standards.getColumn(COMMENT).isSortable());
  }

  @Test
  public void updateCount() {
    presenter.init(view);
    design.count.setValue("4");

    List<Standard> gridStandards = items(design.standards);
    List<Standard> standards = presenter.getValue();

    assertEquals(4, gridStandards.size());
    assertEquals(4, standards.size());
  }

  @Test
  public void fill() {
    presenter.init(view);
    design.count.setValue("4");
    List<Standard> standards = items(design.standards);
    Standard first = standards.get(0);
    Standard filler = standard();
    for (Standard standard : standards) {
      design.standards.getColumn(NAME).getValueProvider().apply(standard);
      design.standards.getColumn(QUANTITY).getValueProvider().apply(standard);
      design.standards.getColumn(COMMENT).getValueProvider().apply(standard);
    }
    ((TextField) design.standards.getColumn(NAME).getValueProvider().apply(first))
        .setValue(filler.getName());
    ((TextField) design.standards.getColumn(QUANTITY).getValueProvider().apply(first))
        .setValue(filler.getQuantity());
    ((TextField) design.standards.getColumn(COMMENT).getValueProvider().apply(first))
        .setValue(filler.getComment());

    design.fill.click();

    for (Standard standard : standards) {
      assertEquals(filler.getName(), standard.getName());
      assertEquals(filler.getQuantity(), standard.getQuantity());
      assertEquals(filler.getComment(), standard.getComment());
    }
  }

  @Test
  public void validate_Empty() {
    presenter.init(view);

    design.count.setValue("");

    assertTrue(presenter.validate());
  }

  @Test
  public void validate_CountNotNumber() {
    presenter.init(view);

    design.count.setValue("a");

    assertFalse(presenter.validate());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.count.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_CountBelowZero() {
    presenter.init(view);

    design.count.setValue("-1");

    assertFalse(presenter.validate());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        design.count.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_CountAboveLimit_Default() {
    presenter.init(view);

    design.count.setValue("11");

    assertFalse(presenter.validate());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        design.count.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_CountAboveLimit_Custom() {
    presenter.setMaxCount(15);
    presenter.init(view);

    design.count.setValue("16");

    assertFalse(presenter.validate());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 15)),
        design.count.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_CountAboveLimit_CustomAfterInit() {
    presenter.init(view);
    presenter.setMaxCount(15);

    design.count.setValue("16");

    assertFalse(presenter.validate());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 15)),
        design.count.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_CountDouble() {
    presenter.init(view);

    design.count.setValue("1.2");

    assertFalse(presenter.validate());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.count.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_MissingStandardName_1() {
    presenter.init(view);
    List<Standard> standards = Arrays.asList(standard(), standard());
    Standard first = standards.get(0);
    presenter.setValue(standards);
    for (Standard standard : standards) {
      design.standards.getColumn(NAME).getValueProvider().apply(standard);
      design.standards.getColumn(QUANTITY).getValueProvider().apply(standard);
      design.standards.getColumn(COMMENT).getValueProvider().apply(standard);
    }
    TextField field = (TextField) design.standards.getColumn(NAME).getValueProvider().apply(first);

    field.setValue("");

    assertFalse(presenter.validate());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_MissingStandardQuantity_1() {
    presenter.init(view);
    List<Standard> standards = Arrays.asList(standard(), standard());
    Standard first = standards.get(0);
    presenter.setValue(standards);
    for (Standard standard : standards) {
      design.standards.getColumn(NAME).getValueProvider().apply(standard);
      design.standards.getColumn(QUANTITY).getValueProvider().apply(standard);
      design.standards.getColumn(COMMENT).getValueProvider().apply(standard);
    }
    TextField field =
        (TextField) design.standards.getColumn(QUANTITY).getValueProvider().apply(first);

    field.setValue("");

    assertFalse(presenter.validate());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_MissingStandardName_2() {
    presenter.init(view);
    List<Standard> standards = Arrays.asList(standard(), standard());
    Standard second = standards.get(1);
    presenter.setValue(standards);
    for (Standard standard : standards) {
      design.standards.getColumn(NAME).getValueProvider().apply(standard);
      design.standards.getColumn(QUANTITY).getValueProvider().apply(standard);
      design.standards.getColumn(COMMENT).getValueProvider().apply(standard);
    }
    TextField field = (TextField) design.standards.getColumn(NAME).getValueProvider().apply(second);

    field.setValue("");

    assertFalse(presenter.validate());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_MissingStandardQuantity_2() {
    presenter.init(view);
    List<Standard> standards = Arrays.asList(standard(), standard());
    Standard second = standards.get(1);
    presenter.setValue(standards);
    for (Standard standard : standards) {
      design.standards.getColumn(NAME).getValueProvider().apply(standard);
      design.standards.getColumn(QUANTITY).getValueProvider().apply(standard);
      design.standards.getColumn(COMMENT).getValueProvider().apply(standard);
    }
    TextField field =
        (TextField) design.standards.getColumn(QUANTITY).getValueProvider().apply(second);

    field.setValue("");

    assertFalse(presenter.validate());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void getReadOnly() {
    presenter.init(view);

    assertFalse(presenter.isReadOnly());
  }

  @Test
  public void getReadOnly_False() {
    presenter.init(view);

    presenter.setReadOnly(false);

    assertFalse(presenter.isReadOnly());
  }

  @Test
  public void getReadOnly_True() {
    presenter.init(view);

    presenter.setReadOnly(true);

    assertTrue(presenter.isReadOnly());
  }

  @Test
  public void setReadOnly_FalseAfterInit() {
    presenter.init(view);
    List<Standard> standards = Arrays.asList(standard(), standard());
    presenter.setValue(standards);
    for (Standard standard : standards) {
      design.standards.getColumn(NAME).getValueProvider().apply(standard);
      design.standards.getColumn(QUANTITY).getValueProvider().apply(standard);
      design.standards.getColumn(COMMENT).getValueProvider().apply(standard);
    }

    presenter.setReadOnly(false);

    assertFalse(design.count.isReadOnly());
    for (Standard standard : standards) {
      assertFalse(((TextField) design.standards.getColumn(NAME).getValueProvider().apply(standard))
          .isReadOnly());
      assertFalse(
          ((TextField) design.standards.getColumn(QUANTITY).getValueProvider().apply(standard))
              .isReadOnly());
      assertFalse(
          ((TextField) design.standards.getColumn(COMMENT).getValueProvider().apply(standard))
              .isReadOnly());
    }
  }

  @Test
  public void setReadOnly_True() {
    presenter.setReadOnly(true);
    presenter.init(view);
    List<Standard> standards = Arrays.asList(standard(), standard());
    presenter.setValue(standards);

    assertTrue(design.count.isReadOnly());
    for (Standard standard : standards) {
      assertTrue(((TextField) design.standards.getColumn(NAME).getValueProvider().apply(standard))
          .isReadOnly());
      assertTrue(
          ((TextField) design.standards.getColumn(QUANTITY).getValueProvider().apply(standard))
              .isReadOnly());
      assertTrue(
          ((TextField) design.standards.getColumn(COMMENT).getValueProvider().apply(standard))
              .isReadOnly());
    }
  }

  @Test
  public void setReadOnly_TrueAfterInit() {
    presenter.init(view);
    List<Standard> standards = Arrays.asList(standard(), standard());
    presenter.setValue(standards);
    for (Standard standard : standards) {
      design.standards.getColumn(NAME).getValueProvider().apply(standard);
      design.standards.getColumn(QUANTITY).getValueProvider().apply(standard);
      design.standards.getColumn(COMMENT).getValueProvider().apply(standard);
    }

    presenter.setReadOnly(true);

    assertTrue(design.count.isReadOnly());
    for (Standard standard : standards) {
      assertTrue(((TextField) design.standards.getColumn(NAME).getValueProvider().apply(standard))
          .isReadOnly());
      assertTrue(
          ((TextField) design.standards.getColumn(QUANTITY).getValueProvider().apply(standard))
              .isReadOnly());
      assertTrue(
          ((TextField) design.standards.getColumn(COMMENT).getValueProvider().apply(standard))
              .isReadOnly());
    }
  }

  @Test
  public void getValue_Default() {
    presenter.init(view);

    List<Standard> standards = presenter.getValue();

    assertTrue(standards.isEmpty());
  }

  @Test
  public void getValue_Zero() {
    presenter.init(view);
    design.count.setValue("0");

    List<Standard> standards = presenter.getValue();

    assertTrue(standards.isEmpty());
  }

  @Test
  public void getValue_Two() {
    presenter.init(view);
    design.count.setValue("2");
    List<Standard> gridStandards = items(design.standards);
    List<Standard> fillers = Arrays.asList(standard(), standard());
    IntStream.range(0, gridStandards.size()).forEach(i -> {
      Standard standard = gridStandards.get(i);
      Standard filler = fillers.get(i);
      ((TextField) design.standards.getColumn(NAME).getValueProvider().apply(standard))
          .setValue(filler.getName());
      ((TextField) design.standards.getColumn(QUANTITY).getValueProvider().apply(standard))
          .setValue(filler.getQuantity());
      ((TextField) design.standards.getColumn(COMMENT).getValueProvider().apply(standard))
          .setValue(filler.getComment());
    });

    List<Standard> standards = presenter.getValue();

    assertEquals(2, standards.size());
    for (int i = 0; i < 2; i++) {
      Standard standard = standards.get(i);
      Standard filler = fillers.get(i);
      assertEquals(filler.getName(), standard.getName());
      assertEquals(filler.getQuantity(), standard.getQuantity());
      assertEquals(filler.getComment(), standard.getComment());
    }
  }

  @Test
  public void getValue_Two_NoValues() {
    presenter.init(view);
    design.count.setValue("2");

    List<Standard> standards = presenter.getValue();

    assertEquals(2, standards.size());
    for (int i = 0; i < 2; i++) {
      Standard standard = standards.get(i);
      assertNull(standard.getName());
      assertNull(standard.getQuantity());
      assertNull(standard.getComment());
    }
  }

  @Test
  public void getValue_AfterSet() {
    presenter.init(view);
    List<Standard> values = Arrays.asList(standard(), standard());
    presenter.setValue(values);

    List<Standard> standards = presenter.getValue();

    assertEquals(2, standards.size());
    for (int i = 0; i < 2; i++) {
      Standard standard = standards.get(i);
      Standard value = values.get(i);
      assertEquals(value.getId(), standard.getId());
      assertEquals(value.getName(), standard.getName());
      assertEquals(value.getQuantity(), standard.getQuantity());
      assertEquals(value.getComment(), standard.getComment());
    }
  }

  @Test
  public void getValue_Set_Increase() {
    presenter.init(view);
    List<Standard> values = Arrays.asList(standard(), standard());
    presenter.setValue(values);
    design.count.setValue("3");

    List<Standard> standards = presenter.getValue();

    assertEquals(3, standards.size());
    for (int i = 0; i < 3; i++) {
      Standard standard = standards.get(i);
      if (i < 2) {
        Standard value = values.get(i);
        assertEquals(value.getId(), standard.getId());
        assertEquals(value.getName(), standard.getName());
        assertEquals(value.getQuantity(), standard.getQuantity());
        assertEquals(value.getComment(), standard.getComment());
      } else {
        assertNull(standard.getId());
        assertNull(standard.getName());
        assertNull(standard.getQuantity());
        assertNull(standard.getComment());
      }
    }
  }

  @Test
  public void getValue_Set_Decrease() {
    presenter.init(view);
    List<Standard> values = Arrays.asList(standard(), standard());
    presenter.setValue(values);
    design.count.setValue("1");

    List<Standard> standards = presenter.getValue();

    assertEquals(1, standards.size());
    for (int i = 0; i < 1; i++) {
      Standard standard = standards.get(i);
      Standard value = values.get(i);
      assertEquals(value.getId(), standard.getId());
      assertEquals(value.getName(), standard.getName());
      assertEquals(value.getQuantity(), standard.getQuantity());
      assertEquals(value.getComment(), standard.getComment());
    }
  }

  @Test
  public void getValue_Set_DecreaseAndIncrease() {
    presenter.init(view);
    List<Standard> values = Arrays.asList(standard(), standard());
    presenter.setValue(values);
    design.count.setValue("1");
    design.count.setValue("2");

    List<Standard> standards = presenter.getValue();

    assertEquals(2, standards.size());
    for (int i = 0; i < 2; i++) {
      Standard standard = standards.get(i);
      Standard value = values.get(i);
      assertEquals(value.getId(), standard.getId());
      assertEquals(value.getName(), standard.getName());
      assertEquals(value.getQuantity(), standard.getQuantity());
      assertEquals(value.getComment(), standard.getComment());
    }
  }

  @Test
  public void setValue_Null() {
    presenter.init(view);
    presenter.setValue(null);

    List<Standard> standards = presenter.getValue();

    assertEquals(0, standards.size());
    assertTrue(presenter.validate());
  }

  @Test
  public void getMaxCount_Default() {
    presenter.init(view);

    assertEquals(10, presenter.getMaxCount());
  }

  @Test
  public void getMaxCount_Custom() {
    presenter.setMaxCount(15);
    presenter.init(view);

    assertEquals(15, presenter.getMaxCount());
  }

  @Test
  public void getMaxCount_CustomAfterInit() {
    presenter.init(view);
    presenter.setMaxCount(15);

    assertEquals(15, presenter.getMaxCount());
  }
}