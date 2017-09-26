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
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.FILL_BUTTON_STYLE;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.FILL_STANDARDS;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.NAME;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.QUANTITY;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.SAMPLE_PANEL;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.SAVE;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.SAVED;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARD;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARDS;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARDS_PANEL;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARD_COMMENTS;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARD_COUNT;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARD_NAME;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARD_QUANTITY;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.SUPPORT;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.VOLUME;
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
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
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
import java.util.Collection;
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
    view.samplePanel = new Panel();
    view.nameField = new TextField();
    view.supportField = new ComboBox<>();
    view.volumeField = new TextField();
    view.quantityField = new TextField();
    view.controlTypeField = new ComboBox<>();
    view.standardsPanel = new Panel();
    view.standardCountField = new TextField();
    view.standardsTableLayout = new HorizontalLayout();
    view.standardsGrid = new Grid<>();
    view.fillStandardsButton = new Button();
    view.explanationLayout = new VerticalLayout();
    view.explanationField = new TextField();
    view.saveButton = new Button();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
  }

  private void setFields() {
    view.nameField.setValue(name);
    view.supportField.setValue(support);
    view.quantityField.setValue(quantity);
    view.volumeField.setValue(Double.toString(volume));
    view.controlTypeField.setValue(controlType);
    view.standardCountField.setValue("2");
    List<Standard> standards = new ArrayList<>(dataProvider(view.standardsGrid).getItems());
    Standard standard = standards.get(0);
    standardNameField1 =
        (TextField) view.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(standard);
    standardNameField1.setValue(standardName1);
    standardQuantityField1 = (TextField) view.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    standardQuantityField1.setValue(standardQuantity1);
    ((TextField) view.standardsGrid.getColumn(STANDARD_COMMENTS).getValueProvider().apply(standard))
        .setValue(standardComment1);
    standard = standards.get(1);
    standardNameField2 =
        (TextField) view.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(standard);
    standardNameField2.setValue(standardName2);
    standardQuantityField2 = (TextField) view.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    standardQuantityField2.setValue(standardQuantity2);
    ((TextField) view.standardsGrid.getColumn(STANDARD_COMMENTS).getValueProvider().apply(standard))
        .setValue(standardComment2);
  }

  @SuppressWarnings("unchecked")
  private <V> ListDataProvider<V> dataProvider(Grid<V> grid) {
    return (ListDataProvider<V>) grid.getDataProvider();
  }

  @SuppressWarnings("unchecked")
  private <V> ListDataProvider<V> dataProvider(ComboBox<V> comboBox) {
    return (ListDataProvider<V>) comboBox.getDataProvider();
  }

  private <V> boolean containsInstanceOf(Collection<V> extensions, Class<? extends V> clazz) {
    return extensions.stream().filter(extension -> clazz.isInstance(extension)).findAny()
        .isPresent();
  }

  private String errorMessage(String message) {
    return new UserError(message).getFormattedHtmlMessage();
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(view.samplePanel.getStyleName().contains(SAMPLE_PANEL));
    assertTrue(view.nameField.getStyleName().contains(NAME));
    assertTrue(view.supportField.getStyleName().contains(SUPPORT));
    assertTrue(view.quantityField.getStyleName().contains(QUANTITY));
    assertTrue(view.volumeField.getStyleName().contains(VOLUME));
    assertTrue(view.controlTypeField.getStyleName().contains(CONTROL_TYPE));
    assertTrue(view.standardsPanel.getStyleName().contains(STANDARDS_PANEL));
    assertTrue(view.standardCountField.getStyleName().contains(STANDARD_COUNT));
    assertTrue(view.standardsGrid.getStyleName().contains(STANDARDS));
    Standard standard = new Standard();
    if (dataProvider(view.standardsGrid).getItems().size() < 1) {
      dataProvider(view.standardsGrid).getItems().add(standard);
    }
    TextField standardNameField =
        (TextField) view.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(standard);
    assertTrue(standardNameField.getStyleName().contains(STANDARD + "." + STANDARD_NAME));
    assertTrue(standardNameField.getStyleName().contains(ValoTheme.TEXTFIELD_TINY));
    TextField standardQuantityField = (TextField) view.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    assertTrue(standardQuantityField.getStyleName().contains(STANDARD + "." + STANDARD_QUANTITY));
    assertTrue(standardQuantityField.getStyleName().contains(ValoTheme.TEXTFIELD_TINY));
    TextField standardCommentsField = (TextField) view.standardsGrid.getColumn(STANDARD_COMMENTS)
        .getValueProvider().apply(standard);
    assertTrue(standardCommentsField.getStyleName().contains(STANDARD + "." + STANDARD_COMMENTS));
    assertTrue(standardCommentsField.getStyleName().contains(ValoTheme.TEXTFIELD_TINY));
    assertTrue(view.fillStandardsButton.getStyleName().contains(FILL_STANDARDS));
    assertTrue(view.fillStandardsButton.getStyleName().contains(FILL_BUTTON_STYLE));
    assertTrue(view.explanationField.getStyleName().contains(EXPLANATION));
    assertTrue(view.saveButton.getStyleName().contains(SAVE));
  }

  @Test
  public void captions() {
    presenter.init(view);

    assertEquals(resources.message(SAMPLE_PANEL), view.samplePanel.getCaption());
    assertEquals(resources.message(NAME), view.nameField.getCaption());
    assertEquals(resources.message(SUPPORT), view.supportField.getCaption());
    for (SampleSupport support : SampleSupport.values()) {
      assertEquals(support.getLabel(locale),
          view.supportField.getItemCaptionGenerator().apply(support));
    }
    assertEquals(resources.message(QUANTITY), view.quantityField.getCaption());
    assertEquals(resources.message(QUANTITY + "." + EXAMPLE), view.quantityField.getPlaceholder());
    assertEquals(resources.message(VOLUME), view.volumeField.getCaption());
    assertEquals(resources.message(CONTROL_TYPE), view.controlTypeField.getCaption());
    for (ControlType type : ControlType.values()) {
      assertEquals(type.getLabel(locale),
          view.controlTypeField.getItemCaptionGenerator().apply(type));
    }
    assertEquals(resources.message(STANDARDS_PANEL), view.standardsPanel.getCaption());
    assertEquals(resources.message(STANDARD_COUNT), view.standardCountField.getCaption());
    assertEquals(resources.message(STANDARD + "." + STANDARD_NAME),
        view.standardsGrid.getColumn(STANDARD_NAME).getCaption());
    assertEquals(resources.message(STANDARD + "." + STANDARD_QUANTITY),
        view.standardsGrid.getColumn(STANDARD_QUANTITY).getCaption());
    assertEquals(resources.message(STANDARD + "." + STANDARD_COMMENTS),
        view.standardsGrid.getColumn(STANDARD_COMMENTS).getCaption());
    Standard standard = new Standard();
    if (dataProvider(view.standardsGrid).getItems().size() < 1) {
      dataProvider(view.standardsGrid).getItems().add(standard);
    }
    TextField standardQuantityField = (TextField) view.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    assertEquals(resources.message(STANDARD + "." + STANDARD_QUANTITY + "." + EXAMPLE),
        standardQuantityField.getPlaceholder());
    assertEquals(resources.message(FILL_STANDARDS), view.fillStandardsButton.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, view.fillStandardsButton.getIcon());
    assertEquals(resources.message(EXPLANATION), view.explanationField.getCaption());
    assertEquals(resources.message(SAVE), view.saveButton.getCaption());
  }

  @Test
  public void supportChoices() {
    presenter.init(view);

    assertEquals(SampleSupport.values().length, dataProvider(view.supportField).getItems().size());
    for (SampleSupport support : SampleSupport.values()) {
      assertTrue(dataProvider(view.supportField).getItems().contains(support));
    }
  }

  @Test
  public void controlTypeChoices() {
    presenter.init(view);

    assertEquals(ControlType.values().length,
        dataProvider(view.controlTypeField).getItems().size());
    for (ControlType type : ControlType.values()) {
      assertTrue(dataProvider(view.controlTypeField).getItems().contains(type));
    }
  }

  @Test
  public void standardsGrid_Column() {
    presenter.init(view);

    assertEquals(3, view.standardsGrid.getColumns().size());
    assertEquals(STANDARD_NAME, view.standardsGrid.getColumns().get(0).getId());
    assertTrue(containsInstanceOf(view.standardsGrid.getColumns().get(0).getExtensions(),
        ComponentRenderer.class));
    assertEquals(STANDARD_QUANTITY, view.standardsGrid.getColumns().get(1).getId());
    assertTrue(containsInstanceOf(view.standardsGrid.getColumns().get(1).getExtensions(),
        ComponentRenderer.class));
    assertEquals(STANDARD_COMMENTS, view.standardsGrid.getColumns().get(2).getId());
    assertTrue(containsInstanceOf(view.standardsGrid.getColumns().get(2).getExtensions(),
        ComponentRenderer.class));
  }

  @Test
  public void required() {
    presenter.init(view);

    assertTrue(view.nameField.isRequiredIndicatorVisible());
    assertTrue(view.supportField.isRequiredIndicatorVisible());
    assertFalse(view.quantityField.isRequiredIndicatorVisible());
    assertFalse(view.volumeField.isRequiredIndicatorVisible());
    assertTrue(view.controlTypeField.isRequiredIndicatorVisible());
    assertTrue(view.explanationField.isRequiredIndicatorVisible());
  }

  @Test
  public void readOnly_True() {
    presenter.init(view);
    presenter.setReadOnly(true);

    assertTrue(view.nameField.isReadOnly());
    assertTrue(view.supportField.isReadOnly());
    assertTrue(view.quantityField.isReadOnly());
    assertTrue(view.volumeField.isReadOnly());
    assertTrue(view.controlTypeField.isReadOnly());
    assertTrue(view.standardCountField.isReadOnly());
    Standard standard = new Standard();
    if (dataProvider(view.standardsGrid).getItems().size() < 1) {
      dataProvider(view.standardsGrid).getItems().add(standard);
    }
    TextField standardNameField =
        (TextField) view.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(standard);
    assertTrue(standardNameField.isReadOnly());
    TextField standardQuantityField = (TextField) view.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    assertTrue(standardQuantityField.isReadOnly());
    TextField standardCommentsField = (TextField) view.standardsGrid.getColumn(STANDARD_COMMENTS)
        .getValueProvider().apply(standard);
    assertTrue(standardCommentsField.isReadOnly());
    assertFalse(view.fillStandardsButton.isVisible());
    assertFalse(view.explanationLayout.isVisible());
    assertFalse(view.saveButton.isVisible());
  }

  @Test
  public void readOnly_True_AfterStandards() {
    presenter.init(view);
    presenter.setReadOnly(false);

    Standard standard = new Standard();
    if (dataProvider(view.standardsGrid).getItems().size() < 1) {
      dataProvider(view.standardsGrid).getItems().add(standard);
    }
    final TextField standardNameField =
        (TextField) view.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(standard);
    final TextField standardQuantityField = (TextField) view.standardsGrid
        .getColumn(STANDARD_QUANTITY).getValueProvider().apply(standard);
    final TextField standardCommentsField = (TextField) view.standardsGrid
        .getColumn(STANDARD_COMMENTS).getValueProvider().apply(standard);
    presenter.setReadOnly(true);
    assertTrue(view.nameField.isReadOnly());
    assertTrue(view.supportField.isReadOnly());
    assertTrue(view.quantityField.isReadOnly());
    assertTrue(view.volumeField.isReadOnly());
    assertTrue(view.controlTypeField.isReadOnly());
    assertTrue(view.standardCountField.isReadOnly());
    assertTrue(standardNameField.isReadOnly());
    assertTrue(standardQuantityField.isReadOnly());
    assertTrue(standardCommentsField.isReadOnly());
    assertFalse(view.fillStandardsButton.isVisible());
    assertFalse(view.explanationLayout.isVisible());
    assertFalse(view.saveButton.isVisible());
  }

  @Test
  public void readOnly_True_Update() {
    presenter.init(view);
    presenter.setReadOnly(true);
    Control control = entityManager.find(Control.class, 444L);
    presenter.setValue(control);

    assertTrue(view.nameField.isReadOnly());
    assertTrue(view.supportField.isReadOnly());
    assertTrue(view.quantityField.isReadOnly());
    assertTrue(view.volumeField.isReadOnly());
    assertTrue(view.controlTypeField.isReadOnly());
    assertTrue(view.standardCountField.isReadOnly());
    Standard standard = new Standard();
    if (dataProvider(view.standardsGrid).getItems().size() < 1) {
      dataProvider(view.standardsGrid).getItems().add(standard);
    }
    TextField standardNameField =
        (TextField) view.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(standard);
    assertTrue(standardNameField.isReadOnly());
    TextField standardQuantityField = (TextField) view.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    assertTrue(standardQuantityField.isReadOnly());
    TextField standardCommentsField = (TextField) view.standardsGrid.getColumn(STANDARD_COMMENTS)
        .getValueProvider().apply(standard);
    assertTrue(standardCommentsField.isReadOnly());
    assertFalse(view.fillStandardsButton.isVisible());
    assertFalse(view.explanationLayout.isVisible());
    assertFalse(view.saveButton.isVisible());
  }

  @Test
  public void readOnly_False() {
    presenter.init(view);

    assertFalse(view.nameField.isReadOnly());
    assertFalse(view.supportField.isReadOnly());
    assertFalse(view.quantityField.isReadOnly());
    assertFalse(view.volumeField.isReadOnly());
    assertFalse(view.controlTypeField.isReadOnly());
    assertFalse(view.standardCountField.isReadOnly());
    Standard standard = new Standard();
    if (dataProvider(view.standardsGrid).getItems().size() < 1) {
      dataProvider(view.standardsGrid).getItems().add(standard);
    }
    TextField standardNameField =
        (TextField) view.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(standard);
    assertFalse(standardNameField.isReadOnly());
    TextField standardQuantityField = (TextField) view.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    assertFalse(standardQuantityField.isReadOnly());
    TextField standardCommentsField = (TextField) view.standardsGrid.getColumn(STANDARD_COMMENTS)
        .getValueProvider().apply(standard);
    assertFalse(standardCommentsField.isReadOnly());
    assertTrue(view.fillStandardsButton.isVisible());
    assertFalse(view.explanationLayout.isVisible());
    assertTrue(view.saveButton.isVisible());
  }

  @Test
  public void readOnly_False_AfterStandards() {
    presenter.init(view);
    presenter.setReadOnly(true);

    Standard standard = new Standard();
    if (dataProvider(view.standardsGrid).getItems().size() < 1) {
      dataProvider(view.standardsGrid).getItems().add(standard);
    }
    final TextField standardNameField =
        (TextField) view.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(standard);
    final TextField standardQuantityField = (TextField) view.standardsGrid
        .getColumn(STANDARD_QUANTITY).getValueProvider().apply(standard);
    final TextField standardCommentsField = (TextField) view.standardsGrid
        .getColumn(STANDARD_COMMENTS).getValueProvider().apply(standard);
    presenter.setReadOnly(false);
    assertFalse(view.nameField.isReadOnly());
    assertFalse(view.supportField.isReadOnly());
    assertFalse(view.quantityField.isReadOnly());
    assertFalse(view.volumeField.isReadOnly());
    assertFalse(view.controlTypeField.isReadOnly());
    assertFalse(view.standardCountField.isReadOnly());
    assertFalse(standardNameField.isReadOnly());
    assertFalse(standardQuantityField.isReadOnly());
    assertFalse(standardCommentsField.isReadOnly());
    assertTrue(view.fillStandardsButton.isVisible());
    assertFalse(view.explanationLayout.isVisible());
    assertTrue(view.saveButton.isVisible());
  }

  @Test
  public void readOnly_False_Update() {
    presenter.init(view);
    Control control = entityManager.find(Control.class, 444L);
    presenter.setValue(control);

    assertFalse(view.nameField.isReadOnly());
    assertFalse(view.supportField.isReadOnly());
    assertFalse(view.quantityField.isReadOnly());
    assertFalse(view.volumeField.isReadOnly());
    assertFalse(view.controlTypeField.isReadOnly());
    assertFalse(view.standardCountField.isReadOnly());
    Standard standard = new Standard();
    if (dataProvider(view.standardsGrid).getItems().size() < 1) {
      dataProvider(view.standardsGrid).getItems().add(standard);
    }
    TextField standardNameField =
        (TextField) view.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(standard);
    assertFalse(standardNameField.isReadOnly());
    TextField standardQuantityField = (TextField) view.standardsGrid.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(standard);
    assertFalse(standardQuantityField.isReadOnly());
    TextField standardCommentsField = (TextField) view.standardsGrid.getColumn(STANDARD_COMMENTS)
        .getValueProvider().apply(standard);
    assertFalse(standardCommentsField.isReadOnly());
    assertTrue(view.fillStandardsButton.isVisible());
    assertTrue(view.explanationLayout.isVisible());
    assertTrue(view.saveButton.isVisible());
  }

  @Test
  public void updateStandardCount() {
    presenter.init(view);
    assertFalse(view.standardsTableLayout.isVisible());

    view.standardCountField.setValue("2");

    assertTrue(view.standardsTableLayout.isVisible());
    assertEquals(2, dataProvider(view.standardsGrid).getItems().size());
    for (Standard standard : dataProvider(view.standardsGrid).getItems()) {
      TextField nameField = (TextField) view.standardsGrid.getColumn(STANDARD_NAME)
          .getValueProvider().apply(standard);
      assertEquals("", nameField.getValue());
      assertTrue(nameField.isRequiredIndicatorVisible());
      TextField quantityField = (TextField) view.standardsGrid.getColumn(STANDARD_QUANTITY)
          .getValueProvider().apply(standard);
      assertEquals("", quantityField.getValue());
      assertTrue(quantityField.isRequiredIndicatorVisible());
      TextField commentsField = (TextField) view.standardsGrid.getColumn(STANDARD_COMMENTS)
          .getValueProvider().apply(standard);
      assertEquals("", commentsField.getValue());
      assertFalse(commentsField.isRequiredIndicatorVisible());
    }
  }

  @Test
  public void fillStandards() {
    presenter.init(view);

    view.standardCountField.setValue("3");

    Standard first = dataProvider(view.standardsGrid).getItems().iterator().next();
    TextField firstName =
        (TextField) view.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(first);
    firstName.setValue("std1");
    TextField firstQuantity =
        (TextField) view.standardsGrid.getColumn(STANDARD_QUANTITY).getValueProvider().apply(first);
    firstQuantity.setValue("2 ug");
    TextField firstComments =
        (TextField) view.standardsGrid.getColumn(STANDARD_COMMENTS).getValueProvider().apply(first);
    firstComments.setValue("com1");
    for (Standard standard : dataProvider(view.standardsGrid).getItems()) {
      view.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(standard);
      view.standardsGrid.getColumn(STANDARD_QUANTITY).getValueProvider().apply(standard);
      view.standardsGrid.getColumn(STANDARD_COMMENTS).getValueProvider().apply(standard);
    }
    view.fillStandardsButton.click();
    for (Standard standard : dataProvider(view.standardsGrid).getItems()) {
      assertEquals("std1", standard.getName());
      assertEquals("2 ug", standard.getQuantity());
      assertEquals("com1", standard.getComments());
      assertEquals("std1", ((TextField) view.standardsGrid.getColumn(STANDARD_NAME)
          .getValueProvider().apply(standard)).getValue());
      assertEquals("2 ug", ((TextField) view.standardsGrid.getColumn(STANDARD_QUANTITY)
          .getValueProvider().apply(standard)).getValue());
      assertEquals("com1", ((TextField) view.standardsGrid.getColumn(STANDARD_COMMENTS)
          .getValueProvider().apply(standard)).getValue());
    }
  }

  @Test
  public void save_MissingName() {
    presenter.init(view);
    setFields();
    view.nameField.setValue("");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.nameField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(controlCaptor.capture());
  }

  @Test
  public void save_InvalidName() {
    presenter.init(view);
    setFields();
    view.nameField.setValue(name + "?");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ONLY_WORDS)),
        view.nameField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(controlCaptor.capture());
  }

  @Test
  public void save_InvalidSampleVolume() throws Throwable {
    presenter.init(view);
    setFields();
    view.volumeField.setValue("a");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        view.volumeField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroSampleVolume() throws Throwable {
    presenter.init(view);
    setFields();
    view.volumeField.setValue("-1");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(view.volumeField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardCount() throws Throwable {
    presenter.init(view);
    setFields();
    view.standardCountField.setValue("");

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(controlService).insert(any());
  }

  @Test
  public void save_InvalidStandardCount() throws Throwable {
    presenter.init(view);
    setFields();
    view.standardCountField.setValue("a");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        view.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroStandardCount() throws Throwable {
    presenter.init(view);
    setFields();
    view.standardCountField.setValue("-1");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        view.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_AboveMaxStandardCount() throws Throwable {
    presenter.init(view);
    setFields();
    view.standardCountField.setValue("200");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        view.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_DoubleStandardCount() throws Throwable {
    presenter.init(view);
    setFields();
    view.standardCountField.setValue("1.2");

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        view.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardName_1() throws Throwable {
    presenter.init(view);
    setFields();
    standardNameField1.setValue("");

    view.saveButton.click();

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

    view.saveButton.click();

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

    view.saveButton.click();

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

    view.saveButton.click();

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

    view.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.explanationField.getErrorMessage().getFormattedHtmlMessage());
    verify(controlService, never()).update(any(), any());
  }

  @Test
  public void save_Insert() {
    presenter.init(view);
    setFields();

    view.saveButton.click();

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
    assertEquals(standardComment1, control.getStandards().get(0).getComments());
    assertEquals(standardName2, control.getStandards().get(1).getName());
    assertEquals(standardQuantity2, control.getStandards().get(1).getQuantity());
    assertEquals(standardComment2, control.getStandards().get(1).getComments());
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
    view.explanationField.setValue(explanation);

    view.saveButton.click();

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
    assertEquals(standardComment1, control.getStandards().get(0).getComments());
    assertEquals(standardName2, control.getStandards().get(1).getName());
    assertEquals(standardQuantity2, control.getStandards().get(1).getQuantity());
    assertEquals(standardComment2, control.getStandards().get(1).getComments());
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
    standard.setComments("com1");
    control.getStandards().add(standard);
    standard = new Standard();
    standard.setName("std2");
    standard.setQuantity("1.2 ug");
    standard.setComments("com2");
    control.getStandards().add(standard);
    presenter.init(view);
    presenter.setValue(control);

    assertEquals(control.getName(), view.nameField.getValue());
    assertEquals(control.getSupport(), view.supportField.getValue());
    assertEquals(Objects.toString(control.getQuantity(), ""), view.quantityField.getValue());
    assertEquals(Objects.toString(control.getVolume(), ""), view.volumeField.getValue());
    assertEquals(control.getControlType(), view.controlTypeField.getValue());
    assertEquals(control.getStandards().size(), dataProvider(view.standardsGrid).getItems().size());
    for (Standard cs : control.getStandards()) {
      assertEquals(cs.getName(),
          ((TextField) view.standardsGrid.getColumn(STANDARD_NAME).getValueProvider().apply(cs))
              .getValue());
      assertEquals(cs.getQuantity(),
          ((TextField) view.standardsGrid.getColumn(STANDARD_QUANTITY).getValueProvider().apply(cs))
              .getValue());
      assertEquals(cs.getComments(),
          ((TextField) view.standardsGrid.getColumn(STANDARD_COMMENTS).getValueProvider().apply(cs))
              .getValue());
    }
    assertEquals("", view.explanationField.getValue());
  }
}
