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
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.NAME;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.QUANTITY;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.SAMPLE_PANEL;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.SAVE;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.SAVED;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARDS_CONTAINER;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.TYPE;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.VOLUME;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.errorMessage;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.ONLY_WORDS;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlService;
import ca.qc.ircm.proview.sample.ControlType;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
  private ArgumentCaptor<Boolean> booleanCaptor;
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
  private SampleType type = SampleType.SOLUTION;
  private String quantity = "10 g/L";
  private String volume = "10000000.0 ul";
  private ControlType controlType = ControlType.NEGATIVE_CONTROL;
  private String standardName1 = "std1";
  private String standardQuantity1 = "1 ug";
  private String standardComment1 = "com1";
  private String standardName2 = "std2";
  private String standardQuantity2 = "2 ug";
  private String standardComment2 = "com2";
  private String explanation = "test explanation";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new ControlFormPresenter(controlService);
    design = new ControlFormDesign();
    view.design = design;
    view.standardsForm = mock(StandardsForm.class);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    when(view.standardsForm.validate()).thenReturn(true);
  }

  private void setFields() {
    design.nameField.setValue(name);
    design.type.setValue(type);
    design.quantityField.setValue(quantity);
    design.volumeField.setValue(volume);
    design.controlTypeField.setValue(controlType);
    List<Standard> standards = new ArrayList<>();
    Standard standard1 = new Standard();
    standard1.setName(standardName1);
    standard1.setQuantity(standardQuantity1);
    standard1.setComment(standardComment1);
    standards.add(standard1);
    Standard standard2 = new Standard();
    standard2.setName(standardName2);
    standard2.setQuantity(standardQuantity2);
    standard2.setComment(standardComment2);
    standards.add(standard2);
    when(view.standardsForm.getValue()).thenReturn(standards);
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.samplePanel.getStyleName().contains(SAMPLE_PANEL));
    assertTrue(design.nameField.getStyleName().contains(NAME));
    assertTrue(design.type.getStyleName().contains(TYPE));
    assertTrue(design.quantityField.getStyleName().contains(QUANTITY));
    assertTrue(design.volumeField.getStyleName().contains(VOLUME));
    assertTrue(design.controlTypeField.getStyleName().contains(CONTROL_TYPE));
    assertTrue(design.standardsContainer.getStyleName().contains(STANDARDS_CONTAINER));
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
    assertEquals(resources.message(TYPE), design.type.getCaption());
    for (SampleType type : SampleType.values()) {
      assertEquals(type.getLabel(locale), design.type.getItemCaptionGenerator().apply(type));
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
    assertEquals(resources.message(EXPLANATION_PANEL), design.explanationPanel.getCaption());
    assertEquals(null, design.explanation.getCaption());
    assertEquals(resources.message(SAVE), design.saveButton.getCaption());
  }

  @Test
  public void typeChoices() {
    presenter.init(view);

    assertEquals(SampleType.values().length, dataProvider(design.type).getItems().size());
    for (SampleType type : SampleType.values()) {
      assertTrue(dataProvider(design.type).getItems().contains(type));
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
  public void required() {
    presenter.init(view);

    assertTrue(design.nameField.isRequiredIndicatorVisible());
    assertTrue(design.type.isRequiredIndicatorVisible());
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
    assertTrue(design.type.isReadOnly());
    assertTrue(design.quantityField.isReadOnly());
    assertTrue(design.volumeField.isReadOnly());
    assertTrue(design.controlTypeField.isReadOnly());
    verify(view.standardsForm, atLeastOnce()).setReadOnly(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
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
    assertTrue(design.type.isReadOnly());
    assertTrue(design.quantityField.isReadOnly());
    assertTrue(design.volumeField.isReadOnly());
    assertTrue(design.controlTypeField.isReadOnly());
    verify(view.standardsForm, atLeastOnce()).setReadOnly(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    verify(view.standardsForm).setValue(control.getStandards());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.saveButton.isVisible());
  }

  @Test
  public void readOnly_Default() {
    presenter.init(view);

    assertFalse(design.nameField.isReadOnly());
    assertFalse(design.type.isReadOnly());
    assertFalse(design.quantityField.isReadOnly());
    assertFalse(design.volumeField.isReadOnly());
    assertFalse(design.controlTypeField.isReadOnly());
    verify(view.standardsForm, atLeastOnce()).setReadOnly(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.saveButton.isVisible());
  }

  @Test
  public void readOnly_False() {
    presenter.init(view);
    presenter.setReadOnly(false);

    assertFalse(design.nameField.isReadOnly());
    assertFalse(design.type.isReadOnly());
    assertFalse(design.quantityField.isReadOnly());
    assertFalse(design.volumeField.isReadOnly());
    assertFalse(design.controlTypeField.isReadOnly());
    verify(view.standardsForm, atLeastOnce()).setReadOnly(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.saveButton.isVisible());
  }

  @Test
  public void readOnly_False_Update() {
    presenter.init(view);
    Control control = entityManager.find(Control.class, 444L);
    presenter.setValue(control);

    assertFalse(design.nameField.isReadOnly());
    assertFalse(design.type.isReadOnly());
    assertFalse(design.quantityField.isReadOnly());
    assertFalse(design.volumeField.isReadOnly());
    assertFalse(design.controlTypeField.isReadOnly());
    verify(view.standardsForm, atLeastOnce()).setReadOnly(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    verify(view.standardsForm).setValue(control.getStandards());
    assertTrue(design.explanationPanel.isVisible());
    assertTrue(design.saveButton.isVisible());
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
  public void save_StandardsFormFail() throws Throwable {
    presenter.init(view);
    setFields();
    when(view.standardsForm.validate()).thenReturn(false);

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
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
    assertEquals(type, control.getType());
    assertEquals(quantity, control.getQuantity());
    assertEquals(volume, control.getVolume());
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
    assertEquals(type, control.getType());
    assertEquals(quantity, control.getQuantity());
    assertEquals(volume, control.getVolume());
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
    assertEquals(control.getType(), design.type.getValue());
    assertEquals(Objects.toString(control.getQuantity(), ""), design.quantityField.getValue());
    assertEquals(Objects.toString(control.getVolume(), ""), design.volumeField.getValue());
    assertEquals(control.getControlType(), design.controlTypeField.getValue());
    verify(view.standardsForm).setValue(control.getStandards());
    assertEquals("", design.explanation.getValue());
  }
}
