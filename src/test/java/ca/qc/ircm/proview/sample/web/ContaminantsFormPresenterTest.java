package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.web.ContaminantsFormPresenter.COMMENT;
import static ca.qc.ircm.proview.sample.web.ContaminantsFormPresenter.CONTAMINANTS;
import static ca.qc.ircm.proview.sample.web.ContaminantsFormPresenter.COUNT;
import static ca.qc.ircm.proview.sample.web.ContaminantsFormPresenter.FILL;
import static ca.qc.ircm.proview.sample.web.ContaminantsFormPresenter.NAME;
import static ca.qc.ircm.proview.sample.web.ContaminantsFormPresenter.QUANTITY;
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

import ca.qc.ircm.proview.sample.Contaminant;
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
public class ContaminantsFormPresenterTest {
  private ContaminantsFormPresenter presenter;
  @Mock
  private ContaminantsForm view;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  private ContaminantsFormDesign design = new ContaminantsFormDesign();
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(ContaminantsForm.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);

  @Before
  public void beforeTest() {
    presenter = new ContaminantsFormPresenter();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
  }

  private Contaminant contaminant() {
    Contaminant contaminant = new Contaminant();
    contaminant.setId(RandomUtils.nextLong());
    contaminant.setName(RandomStringUtils.randomAlphabetic(10));
    contaminant.setQuantity(RandomStringUtils.randomAlphabetic(12));
    contaminant.setComment(RandomStringUtils.randomAlphabetic(50));
    return contaminant;
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.count.getStyleName().contains(COUNT));
    assertTrue(design.contaminants.getStyleName().contains(CONTAMINANTS));
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

    assertFalse(design.contaminantsLayout.isVisible());
  }

  @Test
  public void visible_ZeroCount() {
    presenter.init(view);
    design.count.setValue("0");

    assertFalse(design.contaminantsLayout.isVisible());
  }

  @Test
  public void visible_OneCount() {
    presenter.init(view);
    design.count.setValue("1");

    assertTrue(design.contaminantsLayout.isVisible());
    assertTrue(design.fill.isVisible());
  }

  @Test
  public void visible_OneCount_ReadOnly() {
    presenter.init(view);
    design.count.setValue("1");

    presenter.setReadOnly(true);

    assertTrue(design.contaminantsLayout.isVisible());
    assertFalse(design.fill.isVisible());
  }

  @Test
  public void contaminants() {
    presenter.init(view);
    List<Contaminant> contaminants = Arrays.asList(contaminant(), contaminant());
    presenter.setValue(contaminants);

    assertEquals(3, design.contaminants.getColumns().size());
    assertEquals(NAME, design.contaminants.getColumns().get(0).getId());
    assertTrue(containsInstanceOf(design.contaminants.getColumn(NAME).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.contaminants.getColumn(NAME).isSortable());
    for (Contaminant contaminant : contaminants) {
      TextField field =
          (TextField) design.contaminants.getColumn(NAME).getValueProvider().apply(contaminant);
      assertTrue(field.getStyleName().contains(NAME));
      assertEquals(contaminant.getName(), field.getValue());
      assertFalse(field.isReadOnly());
    }
    assertEquals(QUANTITY, design.contaminants.getColumns().get(1).getId());
    assertTrue(containsInstanceOf(design.contaminants.getColumn(QUANTITY).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.contaminants.getColumn(QUANTITY).isSortable());
    for (Contaminant contaminant : contaminants) {
      TextField field =
          (TextField) design.contaminants.getColumn(QUANTITY).getValueProvider().apply(contaminant);
      assertTrue(field.getStyleName().contains(QUANTITY));
      assertEquals(contaminant.getQuantity(), field.getValue());
      assertFalse(field.isReadOnly());
    }
    assertEquals(COMMENT, design.contaminants.getColumns().get(2).getId());
    assertTrue(containsInstanceOf(design.contaminants.getColumn(COMMENT).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.contaminants.getColumn(COMMENT).isSortable());
    for (Contaminant contaminant : contaminants) {
      TextField field =
          (TextField) design.contaminants.getColumn(COMMENT).getValueProvider().apply(contaminant);
      assertTrue(field.getStyleName().contains(COMMENT));
      assertEquals(contaminant.getComment(), field.getValue());
      assertFalse(field.isReadOnly());
    }
  }

  @Test
  public void contaminants_AfterSetValue() {
    presenter.init(view);
    List<Contaminant> contaminants = Arrays.asList(contaminant(), contaminant());
    presenter.setValue(contaminants);

    assertFalse(design.contaminants.getColumn(NAME).isSortable());
    assertFalse(design.contaminants.getColumn(QUANTITY).isSortable());
    assertFalse(design.contaminants.getColumn(COMMENT).isSortable());
  }

  @Test
  public void updateCount() {
    presenter.init(view);
    design.count.setValue("4");

    List<Contaminant> gridContaminants = items(design.contaminants);
    List<Contaminant> contaminants = presenter.getValue();

    assertEquals(4, gridContaminants.size());
    assertEquals(4, contaminants.size());
  }

  @Test
  public void fill() {
    presenter.init(view);
    design.count.setValue("4");
    List<Contaminant> contaminants = items(design.contaminants);
    Contaminant first = contaminants.get(0);
    Contaminant filler = contaminant();
    for (Contaminant contaminant : contaminants) {
      design.contaminants.getColumn(NAME).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(QUANTITY).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(COMMENT).getValueProvider().apply(contaminant);
    }
    ((TextField) design.contaminants.getColumn(NAME).getValueProvider().apply(first))
        .setValue(filler.getName());
    ((TextField) design.contaminants.getColumn(QUANTITY).getValueProvider().apply(first))
        .setValue(filler.getQuantity());
    ((TextField) design.contaminants.getColumn(COMMENT).getValueProvider().apply(first))
        .setValue(filler.getComment());

    design.fill.click();

    for (Contaminant contaminant : contaminants) {
      assertEquals(filler.getName(), contaminant.getName());
      assertEquals(filler.getQuantity(), contaminant.getQuantity());
      assertEquals(filler.getComment(), contaminant.getComment());
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
  public void validate_MissingContaminantName_1() {
    presenter.init(view);
    List<Contaminant> contaminants = Arrays.asList(contaminant(), contaminant());
    Contaminant first = contaminants.get(0);
    presenter.setValue(contaminants);
    for (Contaminant contaminant : contaminants) {
      design.contaminants.getColumn(NAME).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(QUANTITY).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(COMMENT).getValueProvider().apply(contaminant);
    }
    TextField field =
        (TextField) design.contaminants.getColumn(NAME).getValueProvider().apply(first);

    field.setValue("");

    assertFalse(presenter.validate());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_MissingContaminantQuantity_1() {
    presenter.init(view);
    List<Contaminant> contaminants = Arrays.asList(contaminant(), contaminant());
    Contaminant first = contaminants.get(0);
    presenter.setValue(contaminants);
    for (Contaminant contaminant : contaminants) {
      design.contaminants.getColumn(NAME).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(QUANTITY).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(COMMENT).getValueProvider().apply(contaminant);
    }
    TextField field =
        (TextField) design.contaminants.getColumn(QUANTITY).getValueProvider().apply(first);

    field.setValue("");

    assertFalse(presenter.validate());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_MissingContaminantName_2() {
    presenter.init(view);
    List<Contaminant> contaminants = Arrays.asList(contaminant(), contaminant());
    Contaminant second = contaminants.get(1);
    presenter.setValue(contaminants);
    for (Contaminant contaminant : contaminants) {
      design.contaminants.getColumn(NAME).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(QUANTITY).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(COMMENT).getValueProvider().apply(contaminant);
    }
    TextField field =
        (TextField) design.contaminants.getColumn(NAME).getValueProvider().apply(second);

    field.setValue("");

    assertFalse(presenter.validate());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_MissingContaminantQuantity_2() {
    presenter.init(view);
    List<Contaminant> contaminants = Arrays.asList(contaminant(), contaminant());
    Contaminant second = contaminants.get(1);
    presenter.setValue(contaminants);
    for (Contaminant contaminant : contaminants) {
      design.contaminants.getColumn(NAME).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(QUANTITY).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(COMMENT).getValueProvider().apply(contaminant);
    }
    TextField field =
        (TextField) design.contaminants.getColumn(QUANTITY).getValueProvider().apply(second);

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
    List<Contaminant> contaminants = Arrays.asList(contaminant(), contaminant());
    presenter.setValue(contaminants);
    for (Contaminant contaminant : contaminants) {
      design.contaminants.getColumn(NAME).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(QUANTITY).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(COMMENT).getValueProvider().apply(contaminant);
    }

    presenter.setReadOnly(false);

    assertFalse(design.count.isReadOnly());
    for (Contaminant contaminant : contaminants) {
      assertFalse(
          ((TextField) design.contaminants.getColumn(NAME).getValueProvider().apply(contaminant))
              .isReadOnly());
      assertFalse(((TextField) design.contaminants.getColumn(QUANTITY).getValueProvider()
          .apply(contaminant)).isReadOnly());
      assertFalse(
          ((TextField) design.contaminants.getColumn(COMMENT).getValueProvider().apply(contaminant))
              .isReadOnly());
    }
  }

  @Test
  public void setReadOnly_True() {
    presenter.setReadOnly(true);
    presenter.init(view);
    List<Contaminant> contaminants = Arrays.asList(contaminant(), contaminant());
    presenter.setValue(contaminants);

    assertTrue(design.count.isReadOnly());
    for (Contaminant contaminant : contaminants) {
      assertTrue(
          ((TextField) design.contaminants.getColumn(NAME).getValueProvider().apply(contaminant))
              .isReadOnly());
      assertTrue(((TextField) design.contaminants.getColumn(QUANTITY).getValueProvider()
          .apply(contaminant)).isReadOnly());
      assertTrue(
          ((TextField) design.contaminants.getColumn(COMMENT).getValueProvider().apply(contaminant))
              .isReadOnly());
    }
  }

  @Test
  public void setReadOnly_TrueAfterInit() {
    presenter.init(view);
    List<Contaminant> contaminants = Arrays.asList(contaminant(), contaminant());
    presenter.setValue(contaminants);
    for (Contaminant contaminant : contaminants) {
      design.contaminants.getColumn(NAME).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(QUANTITY).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(COMMENT).getValueProvider().apply(contaminant);
    }

    presenter.setReadOnly(true);

    assertTrue(design.count.isReadOnly());
    for (Contaminant contaminant : contaminants) {
      assertTrue(
          ((TextField) design.contaminants.getColumn(NAME).getValueProvider().apply(contaminant))
              .isReadOnly());
      assertTrue(((TextField) design.contaminants.getColumn(QUANTITY).getValueProvider()
          .apply(contaminant)).isReadOnly());
      assertTrue(
          ((TextField) design.contaminants.getColumn(COMMENT).getValueProvider().apply(contaminant))
              .isReadOnly());
    }
  }

  @Test
  public void getValue_Default() {
    presenter.init(view);

    List<Contaminant> contaminants = presenter.getValue();

    assertTrue(contaminants.isEmpty());
  }

  @Test
  public void getValue_Zero() {
    presenter.init(view);
    design.count.setValue("0");

    List<Contaminant> contaminants = presenter.getValue();

    assertTrue(contaminants.isEmpty());
  }

  @Test
  public void getValue_Two() {
    presenter.init(view);
    design.count.setValue("2");
    List<Contaminant> gridContaminants = items(design.contaminants);
    List<Contaminant> fillers = Arrays.asList(contaminant(), contaminant());
    IntStream.range(0, gridContaminants.size()).forEach(i -> {
      Contaminant contaminant = gridContaminants.get(i);
      Contaminant filler = fillers.get(i);
      ((TextField) design.contaminants.getColumn(NAME).getValueProvider().apply(contaminant))
          .setValue(filler.getName());
      ((TextField) design.contaminants.getColumn(QUANTITY).getValueProvider().apply(contaminant))
          .setValue(filler.getQuantity());
      ((TextField) design.contaminants.getColumn(COMMENT).getValueProvider().apply(contaminant))
          .setValue(filler.getComment());
    });

    List<Contaminant> contaminants = presenter.getValue();

    assertEquals(2, contaminants.size());
    for (int i = 0; i < 2; i++) {
      Contaminant contaminant = contaminants.get(i);
      Contaminant filler = fillers.get(i);
      assertEquals(filler.getName(), contaminant.getName());
      assertEquals(filler.getQuantity(), contaminant.getQuantity());
      assertEquals(filler.getComment(), contaminant.getComment());
    }
  }

  @Test
  public void getValue_Two_NoValues() {
    presenter.init(view);
    design.count.setValue("2");

    List<Contaminant> contaminants = presenter.getValue();

    assertEquals(2, contaminants.size());
    for (int i = 0; i < 2; i++) {
      Contaminant contaminant = contaminants.get(i);
      assertNull(contaminant.getName());
      assertNull(contaminant.getQuantity());
      assertNull(contaminant.getComment());
    }
  }

  @Test
  public void getValue_AfterSet() {
    presenter.init(view);
    List<Contaminant> values = Arrays.asList(contaminant(), contaminant());
    presenter.setValue(values);

    List<Contaminant> contaminants = presenter.getValue();

    assertEquals(2, contaminants.size());
    for (int i = 0; i < 2; i++) {
      Contaminant contaminant = contaminants.get(i);
      Contaminant value = values.get(i);
      assertEquals(value.getId(), contaminant.getId());
      assertEquals(value.getName(), contaminant.getName());
      assertEquals(value.getQuantity(), contaminant.getQuantity());
      assertEquals(value.getComment(), contaminant.getComment());
    }
  }

  @Test
  public void getValue_Set_Increase() {
    presenter.init(view);
    List<Contaminant> values = Arrays.asList(contaminant(), contaminant());
    presenter.setValue(values);
    design.count.setValue("3");

    List<Contaminant> contaminants = presenter.getValue();

    assertEquals(3, contaminants.size());
    for (int i = 0; i < 3; i++) {
      Contaminant contaminant = contaminants.get(i);
      if (i < 2) {
        Contaminant value = values.get(i);
        assertEquals(value.getId(), contaminant.getId());
        assertEquals(value.getName(), contaminant.getName());
        assertEquals(value.getQuantity(), contaminant.getQuantity());
        assertEquals(value.getComment(), contaminant.getComment());
      } else {
        assertNull(contaminant.getId());
        assertNull(contaminant.getName());
        assertNull(contaminant.getQuantity());
        assertNull(contaminant.getComment());
      }
    }
  }

  @Test
  public void getValue_Set_Decrease() {
    presenter.init(view);
    List<Contaminant> values = Arrays.asList(contaminant(), contaminant());
    presenter.setValue(values);
    design.count.setValue("1");

    List<Contaminant> contaminants = presenter.getValue();

    assertEquals(1, contaminants.size());
    for (int i = 0; i < 1; i++) {
      Contaminant contaminant = contaminants.get(i);
      Contaminant value = values.get(i);
      assertEquals(value.getId(), contaminant.getId());
      assertEquals(value.getName(), contaminant.getName());
      assertEquals(value.getQuantity(), contaminant.getQuantity());
      assertEquals(value.getComment(), contaminant.getComment());
    }
  }

  @Test
  public void getValue_Set_DecreaseAndIncrease() {
    presenter.init(view);
    List<Contaminant> values = Arrays.asList(contaminant(), contaminant());
    presenter.setValue(values);
    design.count.setValue("1");
    design.count.setValue("2");

    List<Contaminant> contaminants = presenter.getValue();

    assertEquals(2, contaminants.size());
    for (int i = 0; i < 2; i++) {
      Contaminant contaminant = contaminants.get(i);
      Contaminant value = values.get(i);
      assertEquals(value.getId(), contaminant.getId());
      assertEquals(value.getName(), contaminant.getName());
      assertEquals(value.getQuantity(), contaminant.getQuantity());
      assertEquals(value.getComment(), contaminant.getComment());
    }
  }

  @Test
  public void setValue_Null() {
    presenter.init(view);
    presenter.setValue(null);

    List<Contaminant> contaminants = presenter.getValue();

    assertEquals(0, contaminants.size());
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