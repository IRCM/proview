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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.CONTROL_TYPE;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.EXAMPLE;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.EXPLANATION;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.FILL_STANDARDS;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.NAME;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.QUANTITY;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.SAMPLE_PANEL;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.SAVE;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.SAVED;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARD;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARDS;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARDS_PANEL;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARD_COMMENT;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARD_COUNT;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARD_NAME;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARD_QUANTITY;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.SUPPORT;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.VOLUME;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.errorMessage;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.BUTTON_SKIP_ROW;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.ONLY_WORDS;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlService;
import ca.qc.ircm.proview.sample.ControlType;
import ca.qc.ircm.proview.sample.SampleSupport;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ControlFormPresenterTest {
  private ControlFormPresenter presenter;
  @Mock
  private ControlForm view;
  @Mock
  private ControlService controlService;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  @Captor
  private ArgumentCaptor<Control> controlCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  private ControlFormDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(ControlForm.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private String name = "ADH";
  private SampleSupport support = SampleSupport.SOLUTION;
  private String quantity = "10 g/L";
  private double volume = 10000000.0;
  private ControlType controlType = ControlType.NEGATIVE_CONTROL;
  private String standardName1 = "std1";
  private String standardQuantity1 = "1 ug";
  private String standardComment1 = "com1";
  private String standardName2 = "std2";
  private String standardQuantity2 = "2 ug";
  private String standardComment2 = "com2";
  private String explanation = "test explanation";
  private TextField standardNameField1;
  private TextField standardNameField2;
  private TextField standardQuantityField1;
  private TextField standardQuantityField2;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new ControlFormPresenter(controlService);
    design = new ControlFormDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
  }

  private void setFields() {
    design.nameField.setValue(name);
    design.supportField.setValue(support);
    design.quantityField.setValue(quantity);
    design.volumeField.setValue(Double.toString(volume));
    design.controlTypeField.setValue(controlType);
    design.standardCountField.setValue("2");
    List<Standard> standards = new ArrayList<>(dataProvider(design.standardsGrid).getItems());
    Standard standard = standards.get(0);
    standardNameField1 = (TextField) design.standardsGrid.getColumn(STANDARD_NAME)
        .getValueProvider().apply(standard);
    standardNameField1.setValue(standardName1);
    standardQuantityField1 = (TextField) design.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    standardQuantityField1.setValue(standardQuantity1);
    ((TextField) design.standardsGrid.getColumn(STANDARD_COMMENT).getValueProvider()
        .apply(standard)).setValue(standardComment1);
    standard = standards.get(1);
    standardNameField2 = (TextField) design.standardsGrid.getColumn(STANDARD_NAME)
        .getValueProvider().apply(standard);
    standardNameField2.setValue(standardName2);
    standardQuantityField2 = (TextField) design.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    standardQuantityField2.setValue(standardQuantity2);
    ((TextField) design.standardsGrid.getColumn(STANDARD_COMMENT).getValueProvider()
        .apply(standard)).setValue(standardComment2);
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.samplePanel.getStyleName().contains(SAMPLE_PANEL));
    assertTrue(design.nameField.getStyleName().contains(NAME));
    assertTrue(design.supportField.getStyleName().contains(SUPPORT));
    assertTrue(design.quantityField.getStyleName().contains(QUANTITY));
    assertTrue(design.volumeField.getStyleName().contains(VOLUME));
    assertTrue(design.controlTypeField.getStyleName().contains(CONTROL_TYPE));
    assertTrue(design.standardsPanel.getStyleName().contains(STANDARDS_PANEL));
    assertTrue(design.standardCountField.getStyleName().contains(STANDARD_COUNT));
    assertTrue(design.standardsGrid.getStyleName().contains(STANDARDS));
    Standard standard = new Standard();
    if (dataProvider(design.standardsGrid).getItems().size() < 1) {
      dataProvider(design.standardsGrid).getItems().add(standard);
    }
    TextField standardNameField = (TextField) design.standardsGrid.getColumn(STANDARD_NAME)
        .getValueProvider().apply(standard);
    assertTrue(standardNameField.getStyleName().contains(STANDARD + "." + STANDARD_NAME));
    assertTrue(standardNameField.getStyleName().contains(ValoTheme.TEXTFIELD_TINY));
    TextField standardQuantityField = (TextField) design.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    assertTrue(standardQuantityField.getStyleName().contains(STANDARD + "." + STANDARD_QUANTITY));
    assertTrue(standardQuantityField.getStyleName().contains(ValoTheme.TEXTFIELD_TINY));
    TextField standardCommentField = (TextField) design.standardsGrid.getColumn(STANDARD_COMMENT)
        .getValueProvider().apply(standard);
    assertTrue(standardCommentField.getStyleName().contains(STANDARD + "." + STANDARD_COMMENT));
    assertTrue(standardCommentField.getStyleName().contains(ValoTheme.TEXTFIELD_TINY));
    assertTrue(design.fillStandardsButton.getStyleName().contains(FILL_STANDARDS));
    assertTrue(design.fillStandardsButton.getStyleName().contains(BUTTON_SKIP_ROW));
    assertTrue(design.explanationPanel.getStyleName().contains(EXPLANATION_PANEL));
    assertTrue(design.explanationPanel.getStyleName().contains(REQUIRED));
    assertTrue(design.explanation.getStyleName().contains(EXPLANATION));
    assertTrue(design.saveButton.getStyleName().contains(SAVE));
    assertTrue(design.saveButton.getStyleName().contains(ValoTheme.BUTTON_PRIMARY));
  }

  @Test
  public void captions() {
    presenter.init(view);

    assertEquals(resources.message(SAMPLE_PANEL), design.samplePanel.getCaption());
    assertEquals(resources.message(NAME), design.nameField.getCaption());
    assertEquals(resources.message(SUPPORT), design.supportField.getCaption());
    for (SampleSupport support : SampleSupport.values()) {
      assertEquals(support.getLabel(locale),
          design.supportField.getItemCaptionGenerator().apply(support));
    }
    assertEquals(resources.message(QUANTITY), design.quantityField.getCaption());
    assertEquals(resources.message(QUANTITY + "." + EXAMPLE),
        design.quantityField.getPlaceholder());
    assertEquals(resources.message(VOLUME), design.volumeField.getCaption());
    assertEquals(resources.message(CONTROL_TYPE), design.controlTypeField.getCaption());
    for (ControlType type : ControlType.values()) {
      assertEquals(type.getLabel(locale),
          design.controlTypeField.getItemCaptionGenerator().apply(type));
    }
    assertEquals(resources.message(STANDARDS_PANEL), design.standardsPanel.getCaption());
    assertEquals(resources.message(STANDARD_COUNT), design.standardCountField.getCaption());
    assertEquals(resources.message(STANDARD + "." + STANDARD_NAME),
        design.standardsGrid.getColumn(STANDARD_NAME).getCaption());
    assertEquals(resources.message(STANDARD + "." + STANDARD_QUANTITY),
        design.standardsGrid.getColumn(STANDARD_QUANTITY).getCaption());
    assertEquals(resources.message(STANDARD + "." + STANDARD_COMMENT),
        design.standardsGrid.getColumn(STANDARD_COMMENT).getCaption());
    Standard standard = new Standard();
    if (dataProvider(design.standardsGrid).getItems().size() < 1) {
      dataProvider(design.standardsGrid).getItems().add(standard);
    }
    TextField standardQuantityField = (TextField) design.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    assertEquals(resources.message(STANDARD + "." + STANDARD_QUANTITY + "." + EXAMPLE),
        standardQuantityField.getPlaceholder());
    assertEquals(resources.message(FILL_STANDARDS), design.fillStandardsButton.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.fillStandardsButton.getIcon());
    assertEquals(resources.message(EXPLANATION_PANEL), design.explanationPanel.getCaption());
    assertEquals(null, design.explanation.getCaption());
    assertEquals(resources.message(SAVE), design.saveButton.getCaption());
  }

  @Test
  public void supportChoices() {
    presenter.init(view);

    assertEquals(SampleSupport.values().length,
        dataProvider(design.supportField).getItems().size());
    for (SampleSupport support : SampleSupport.values()) {
      assertTrue(dataProvider(design.supportField).getItems().contains(support));
    }
  }

  @Test
  public void controlTypeChoices() {
    presenter.init(view);

    assertEquals(ControlType.values().length,
        dataProvider(design.controlTypeField).getItems().size());
    for (ControlType type : ControlType.values()) {
      assertTrue(dataProvider(design.controlTypeField).getItems().contains(type));
    }
  }

  @Test
  public void standardsGrid_Column() {
    presenter.init(view);

    assertEquals(3, design.standardsGrid.getColumns().size());
    assertEquals(STANDARD_NAME, design.standardsGrid.getColumns().get(0).getId());
    assertTrue(containsInstanceOf(design.standardsGrid.getColumns().get(0).getExtensions(),
        ComponentRenderer.class));
    assertEquals(STANDARD_QUANTITY, design.standardsGrid.getColumns().get(1).getId());
    assertTrue(containsInstanceOf(design.standardsGrid.getColumns().get(1).getExtensions(),
        ComponentRenderer.class));
    assertEquals(STANDARD_COMMENT, design.standardsGrid.getColumns().get(2).getId());
    assertTrue(containsInstanceOf(design.standardsGrid.getColumns().get(2).getExtensions(),
        ComponentRenderer.class));
  }

  @Test
  public void required() {
    presenter.init(view);

    assertTrue(design.nameField.isRequiredIndicatorVisible());
    assertTrue(design.supportField.isRequiredIndicatorVisible());
    assertFalse(design.quantityField.isRequiredIndicatorVisible());
    assertFalse(design.volumeField.isRequiredIndicatorVisible());
    assertTrue(design.controlTypeField.isRequiredIndicatorVisible());
    assertFalse(design.explanation.isRequiredIndicatorVisible());
  }

  @Test
  public void readOnly_True() {
    presenter.init(view);
    presenter.setReadOnly(true);

    assertTrue(design.nameField.isReadOnly());
    assertTrue(design.supportField.isReadOnly());
    assertTrue(design.quantityField.isReadOnly());
    assertTrue(design.volumeField.isReadOnly());
    assertTrue(design.controlTypeField.isReadOnly());
    assertTrue(design.standardCountField.isReadOnly());
    Standard standard = new Standard();
    if (dataProvider(design.standardsGrid).getItems().size() < 1) {
      dataProvider(design.standardsGrid).getItems().add(standard);
    }
    TextField standardNameField = (TextField) design.standardsGrid.getColumn(STANDARD_NAME)
        .getValueProvider().apply(standard);
    assertTrue(standardNameField.isReadOnly());
    TextField standardQuantityField = (TextField) design.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    assertTrue(standardQuantityField.isReadOnly());
    TextField standardCommentField = (TextField) design.standardsGrid.getColumn(STANDARD_COMMENT)
        .getValueProvider().apply(standard);
    assertTrue(standardCommentField.isReadOnly());
    assertFalse(design.fillStandardsButton.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.saveButton.isVisible());
  }

  @Test
  public void readOnly_True_AfterStandards() {
    presenter.init(view);
    presenter.setReadOnly(false);

    Standard standard = new Standard();
    if (dataProvider(design.standardsGrid).getItems().size() < 1) {
      dataProvider(design.standardsGrid).getItems().add(standard);
    }
    final TextField standardNameField = (TextField) design.standardsGrid.getColumn(STANDARD_NAME)
        .getValueProvider().apply(standard);
    final TextField standardQuantityField = (TextField) design.standardsGrid
        .getColumn(STANDARD_QUANTITY).getValueProvider().apply(standard);
    final TextField standardCommentField = (TextField) design.standardsGrid
        .getColumn(STANDARD_COMMENT).getValueProvider().apply(standard);
    presenter.setReadOnly(true);
    assertTrue(design.nameField.isReadOnly());
    assertTrue(design.supportField.isReadOnly());
    assertTrue(design.quantityField.isReadOnly());
    assertTrue(design.volumeField.isReadOnly());
    assertTrue(design.controlTypeField.isReadOnly());
    assertTrue(design.standardCountField.isReadOnly());
    assertTrue(standardNameField.isReadOnly());
    assertTrue(standardQuantityField.isReadOnly());
    assertTrue(standardCommentField.isReadOnly());
    assertFalse(design.fillStandardsButton.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.saveButton.isVisible());
  }

  @Test
  public void readOnly_True_Update() {
    presenter.init(view);
    presenter.setReadOnly(true);
    Control control = entityManager.find(Control.class, 444L);
    presenter.setValue(control);

    assertTrue(design.nameField.isReadOnly());
    assertTrue(design.supportField.isReadOnly());
    assertTrue(design.quantityField.isReadOnly());
    assertTrue(design.volumeField.isReadOnly());
    assertTrue(design.controlTypeField.isReadOnly());
    assertTrue(design.standardCountField.isReadOnly());
    Standard standard = new Standard();
    if (dataProvider(design.standardsGrid).getItems().size() < 1) {
      dataProvider(design.standardsGrid).getItems().add(standard);
    }
    TextField standardNameField = (TextField) design.standardsGrid.getColumn(STANDARD_NAME)
        .getValueProvider().apply(standard);
    assertTrue(standardNameField.isReadOnly());
    TextField standardQuantityField = (TextField) design.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    assertTrue(standardQuantityField.isReadOnly());
    TextField standardCommentField = (TextField) design.standardsGrid.getColumn(STANDARD_COMMENT)
        .getValueProvider().apply(standard);
    assertTrue(standardCommentField.isReadOnly());
    assertFalse(design.fillStandardsButton.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.saveButton.isVisible());
  }

  @Test
  public void readOnly_False() {
    presenter.init(view);

    assertFalse(design.nameField.isReadOnly());
    assertFalse(design.supportField.isReadOnly());
    assertFalse(design.quantityField.isReadOnly());
    assertFalse(design.volumeField.isReadOnly());
    assertFalse(design.controlTypeField.isReadOnly());
    assertFalse(design.standardCountField.isReadOnly());
    Standard standard = new Standard();
    if (dataProvider(design.standardsGrid).getItems().size() < 1) {
      dataProvider(design.standardsGrid).getItems().add(standard);
    }
    TextField standardNameField = (TextField) design.standardsGrid.getColumn(STANDARD_NAME)
        .getValueProvider().apply(standard);
    assertFalse(standardNameField.isReadOnly());
    TextField standardQuantityField = (TextField) design.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    assertFalse(standardQuantityField.isReadOnly());
    TextField standardCommentField = (TextField) design.standardsGrid.getColumn(STANDARD_COMMENT)
        .getValueProvider().apply(standard);
    assertFalse(standardCommentField.isReadOnly());
    assertTrue(design.fillStandardsButton.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.saveButton.isVisible());
  }

  @Test
  public void readOnly_False_AfterStandards() {
    presenter.init(view);
    presenter.setReadOnly(true);

    Standard standard = new Standard();
    if (dataProvider(design.standardsGrid).getItems().size() < 1) {
      dataProvider(design.standardsGrid).getItems().add(standard);
    }
    final TextField standardNameField = (TextField) design.standardsGrid.getColumn(STANDARD_NAME)
        .getValueProvider().apply(standard);
    final TextField standardQuantityField = (TextField) design.standardsGrid
        .getColumn(STANDARD_QUANTITY).getValueProvider().apply(standard);
    final TextField standardCommentField = (TextField) design.standardsGrid
        .getColumn(STANDARD_COMMENT).getValueProvider().apply(standard);
    presenter.setReadOnly(false);
    assertFalse(design.nameField.isReadOnly());
    assertFalse(design.supportField.isReadOnly());
    assertFalse(design.quantityField.isReadOnly());
    assertFalse(design.volumeField.isReadOnly());
    assertFalse(design.controlTypeField.isReadOnly());
    assertFalse(design.standardCountField.isReadOnly());
    assertFalse(standardNameField.isReadOnly());
    assertFalse(standardQuantityField.isReadOnly());
    assertFalse(standardCommentField.isReadOnly());
    assertTrue(design.fillStandardsButton.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.saveButton.isVisible());
  }

  @Test
  public void readOnly_False_Update() {
    presenter.init(view);
    Control control = entityManager.find(Control.class, 444L);
    presenter.setValue(control);

    assertFalse(design.nameField.isReadOnly());
    assertFalse(design.supportField.isReadOnly());
    assertFalse(design.quantityField.isReadOnly());
    assertFalse(design.volumeField.isReadOnly());
    assertFalse(design.controlTypeField.isReadOnly());
    assertFalse(design.standardCountField.isReadOnly());
    Standard standard = new Standard();
    if (dataProvider(design.standardsGrid).getItems().size() < 1) {
      dataProvider(design.standardsGrid).getItems().add(standard);
    }
    TextField standardNameField = (TextField) design.standardsGrid.getColumn(STANDARD_NAME)
        .getValueProvider().apply(standard);
    assertFalse(standardNameField.isReadOnly());
    TextField standardQuantityField = (TextField) design.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    assertFalse(standardQuantityField.isReadOnly());
    TextField standardCommentField = (TextField) design.standardsGrid.getColumn(STANDARD_COMMENT)
        .getValueProvider().apply(standard);
    assertFalse(standardCommentField.isReadOnly());
    assertTrue(design.fillStandardsButton.isVisible());
    assertTrue(design.explanationPanel.isVisible());
    assertTrue(design.saveButton.isVisible());
  }

  @Test
  public void updateStandardCount() {
    presenter.init(view);
    assertFalse(design.standardsTableLayout.isVisible());

    design.standardCountField.setValue("2");

    assertTrue(design.standardsTableLayout.isVisible());
    assertEquals(2, dataProvider(design.standardsGrid).getItems().size());
    for (Standard standard : dataProvider(design.standardsGrid).getItems()) {
      TextField nameField = (TextField) design.standardsGrid.getColumn(STANDARD_NAME)
          .getValueProvider().apply(standard);
      assertEquals("", nameField.getValue());
      assertTrue(nameField.isRequiredIndicatorVisible());
      TextField quantityField = (TextField) design.standardsGrid.getColumn(STANDARD_QUANTITY)
          .getValueProvider().apply(standard);
      assertEquals("", quantityField.getValue());
      assertTrue(quantityField.isRequiredIndicatorVisible());
      TextField commentField = (TextField) design.standardsGrid.getColumn(STANDARD_COMMENT)
          .getValueProvider().apply(standard);
      assertEquals("", commentField.getValue());
      assertFalse(commentField.isRequiredIndicatorVisible());
    }
  }

  @Test
  public void fillStandards() {
    presenter.init(view);

    design.standardCountField.setValue("3");

    Standard first = dataProvider(design.standardsGrid).getItems().iterator().next();
    TextField firstName =
        (TextField) design.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(first);
    firstName.setValue("std1");
    TextField firstQuantity = (TextField) design.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(first);
    firstQuantity.setValue("2 ug");
    TextField firstComment = (TextField) design.standardsGrid.getColumn(STANDARD_COMMENT)
        .getValueProvider().apply(first);
    firstComment.setValue("com1");
    for (Standard standard : dataProvider(design.standardsGrid).getItems()) {
      design.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(standard);
      design.standardsGrid.getColumn(STANDARD_QUANTITY).getValueProvider().apply(standard);
      design.standardsGrid.getColumn(STANDARD_COMMENT).getValueProvider().apply(standard);
    }
    design.fillStandardsButton.click();
    for (Standard standard : dataProvider(design.standardsGrid).getItems()) {
      assertEquals("std1", standard.getName());
      assertEquals("2 ug", standard.getQuantity());
      assertEquals("com1", standard.getComment());
      assertEquals("std1", ((TextField) design.standardsGrid.getColumn(STANDARD_NAME)
          .getValueProvider().apply(standard)).getValue());
      assertEquals("2 ug", ((TextField) design.standardsGrid.getColumn(STANDARD_QUANTITY)
          .getValueProvider().apply(standard)).getValue());
      assertEquals("com1", ((TextField) design.standardsGrid.getColumn(STANDARD_COMMENT)
          .getValueProvider().apply(standard)).getValue());
    }
  }

  @Test
  public void save_MissingName() {
    presenter.init(view);
    setFields();
    design.nameField.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.nameField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(controlCaptor.capture());
  }

  @Test
  public void save_InvalidName() {
    presenter.init(view);
    setFields();
    design.nameField.setValue(name + "?");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ONLY_WORDS)),
        design.nameField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(controlCaptor.capture());
  }

  @Test
  public void save_NameExists() {
    presenter.init(view);
    final Control control = entityManager.find(Control.class, 444L);
    presenter.setValue(control);
    when(controlService.exists(any())).thenReturn(true);
    when(controlService.get(any())).thenReturn(control);
    setFields();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS)),
        design.nameField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(controlCaptor.capture());
  }

  @Test
  public void save_InvalidSampleVolume() throws Throwable {
    presenter.init(view);
    setFields();
    design.volumeField.setValue("a");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        design.volumeField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroSampleVolume() throws Throwable {
    presenter.init(view);
    setFields();
    design.volumeField.setValue("-1");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(design.volumeField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardCount() throws Throwable {
    presenter.init(view);
    setFields();
    design.standardCountField.setValue("");

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(controlService).insert(any());
  }

  @Test
  public void save_InvalidStandardCount() throws Throwable {
    presenter.init(view);
    setFields();
    design.standardCountField.setValue("a");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroStandardCount() throws Throwable {
    presenter.init(view);
    setFields();
    design.standardCountField.setValue("-1");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        design.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_AboveMaxStandardCount() throws Throwable {
    presenter.init(view);
    setFields();
    design.standardCountField.setValue("200");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        design.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_DoubleStandardCount() throws Throwable {
    presenter.init(view);
    setFields();
    design.standardCountField.setValue("1.2");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardName_1() throws Throwable {
    presenter.init(view);
    setFields();
    standardNameField1.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardNameField1.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardName_2() throws Throwable {
    presenter.init(view);
    setFields();
    standardNameField2.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardNameField2.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardQuantity_1() throws Throwable {
    presenter.init(view);
    setFields();
    standardQuantityField1.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardQuantityField1.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardQuantity_2() throws Throwable {
    presenter.init(view);
    setFields();
    standardQuantityField2.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardQuantityField2.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_MissingExplanation() throws Throwable {
    Control control = entityManager.find(Control.class, 444L);
    presenter.init(view);
    presenter.setValue(control);
    setFields();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.explanation.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).update(any(), any());
  }

  @Test
  public void save_Insert() {
    presenter.init(view);
    setFields();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(controlService).insert(controlCaptor.capture());
    Control control = controlCaptor.getValue();
    assertEquals(name, control.getName());
    assertEquals(support, control.getSupport());
    assertEquals(quantity, control.getQuantity());
    assertEquals(volume, control.getVolume(), 0.001);
    assertEquals(controlType, control.getControlType());
    assertEquals(2, control.getStandards().size());
    assertEquals(standardName1, control.getStandards().get(0).getName());
    assertEquals(standardQuantity1, control.getStandards().get(0).getQuantity());
    assertEquals(standardComment1, control.getStandards().get(0).getComment());
    assertEquals(standardName2, control.getStandards().get(1).getName());
    assertEquals(standardQuantity2, control.getStandards().get(1).getQuantity());
    assertEquals(standardComment2, control.getStandards().get(1).getComment());
    verify(view).showTrayNotification(stringCaptor.capture());
    assertEquals(resources.message(SAVED, name), stringCaptor.getValue());
    verify(view).fireSaveEvent(control);
  }

  @Test
  public void save_Update() {
    Control control = entityManager.find(Control.class, 444L);
    presenter.init(view);
    presenter.setValue(control);
    setFields();
    design.explanation.setValue(explanation);

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(controlService).update(controlCaptor.capture(), eq(explanation));
    control = controlCaptor.getValue();
    assertEquals((Long) 444L, control.getId());
    assertEquals(name, control.getName());
    assertEquals(support, control.getSupport());
    assertEquals(quantity, control.getQuantity());
    assertEquals(volume, control.getVolume(), 0.001);
    assertEquals(controlType, control.getControlType());
    assertEquals(2, control.getStandards().size());
    assertEquals(standardName1, control.getStandards().get(0).getName());
    assertEquals(standardQuantity1, control.getStandards().get(0).getQuantity());
    assertEquals(standardComment1, control.getStandards().get(0).getComment());
    assertEquals(standardName2, control.getStandards().get(1).getName());
    assertEquals(standardQuantity2, control.getStandards().get(1).getQuantity());
    assertEquals(standardComment2, control.getStandards().get(1).getComment());
    verify(view).showTrayNotification(stringCaptor.capture());
    assertEquals(resources.message(SAVED, name), stringCaptor.getValue());
    verify(view).fireSaveEvent(control);
  }

  @Test
  public void setBean_Standards() {
    final Control control = entityManager.find(Control.class, 444L);
    Standard standard = new Standard();
    standard.setName("std1");
    standard.setQuantity("1 ug");
    standard.setComment("com1");
    control.getStandards().add(standard);
    standard = new Standard();
    standard.setName("std2");
    standard.setQuantity("1.2 ug");
    standard.setComment("com2");
    control.getStandards().add(standard);
    presenter.init(view);
    presenter.setValue(control);

    assertEquals(control.getName(), design.nameField.getValue());
    assertEquals(control.getSupport(), design.supportField.getValue());
    assertEquals(Objects.toString(control.getQuantity(), ""), design.quantityField.getValue());
    assertEquals(Objects.toString(control.getVolume(), ""), design.volumeField.getValue());
    assertEquals(control.getControlType(), design.controlTypeField.getValue());
    assertEquals(control.getStandards().size(),
        dataProvider(design.standardsGrid).getItems().size());
    for (Standard cs : control.getStandards()) {
      assertEquals(cs.getName(),
          ((TextField) design.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(cs))
              .getValue());
      assertEquals(cs.getQuantity(), ((TextField) design.standardsGrid.getColumn(STANDARD_QUANTITY)
          .getValueProvider().apply(cs)).getValue());
      assertEquals(cs.getComment(), ((TextField) design.standardsGrid.getColumn(STANDARD_COMMENT)
          .getValueProvider().apply(cs)).getValue());
    }
    assertEquals("", design.explanation.getValue());
  }
}
