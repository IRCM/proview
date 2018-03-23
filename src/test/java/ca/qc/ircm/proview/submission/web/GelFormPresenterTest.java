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

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.web.GelFormPresenter.COLORATION;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.DECOLORATION;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.DEVELOPMENT_TIME;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.EXAMPLE;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.OTHER_COLORATION;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.PROTEIN_QUANTITY;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.SEPARATION;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.THICKNESS;
import static ca.qc.ircm.proview.submission.web.GelFormPresenter.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.errorMessage;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class GelFormPresenterTest {
  private GelFormPresenter presenter;
  @Mock
  private GelForm view;
  private GelFormDesign design;
  private final Locale locale = Locale.FRENCH;
  private final MessageResource resources = new MessageResource(GelForm.class, locale);
  private final MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private GelSeparation separation = GelSeparation.TWO_DIMENSION;
  private GelThickness thickness = GelThickness.ONE_HALF;
  private GelColoration coloration = GelColoration.OTHER;
  private String otherColoration = "my coloration";
  private String developmentTime = "300 seconds";
  private boolean decoloration = true;
  private double weightMarkerQuantity = 300;
  private String proteinQuantity = "30 ug";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    presenter = new GelFormPresenter();
    design = new GelFormDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
  }

  private void setFields() {
    design.separation.setValue(separation);
    design.thickness.setValue(thickness);
    design.coloration.setValue(coloration);
    design.otherColoration.setValue(otherColoration);
    design.developmentTime.setValue(developmentTime);
    design.decoloration.setValue(decoloration);
    design.weightMarkerQuantity.setValue(String.valueOf(weightMarkerQuantity));
    design.proteinQuantity.setValue(proteinQuantity);
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.separation.getStyleName().contains(SEPARATION));
    assertTrue(design.thickness.getStyleName().contains(THICKNESS));
    assertTrue(design.coloration.getStyleName().contains(COLORATION));
    assertTrue(design.otherColoration.getStyleName().contains(OTHER_COLORATION));
    assertTrue(design.developmentTime.getStyleName().contains(DEVELOPMENT_TIME));
    assertTrue(design.decoloration.getStyleName().contains(DECOLORATION));
    assertTrue(design.weightMarkerQuantity.getStyleName().contains(WEIGHT_MARKER_QUANTITY));
    assertTrue(design.proteinQuantity.getStyleName().contains(PROTEIN_QUANTITY));
  }

  @Test
  public void captions() {
    presenter.init(view);

    assertEquals(resources.message(SEPARATION), design.separation.getCaption());
    for (GelSeparation separation : GelSeparation.values()) {
      assertEquals(separation.getLabel(locale),
          design.separation.getItemCaptionGenerator().apply(separation));
    }
    assertEquals(resources.message(THICKNESS), design.thickness.getCaption());
    for (GelThickness thickness : GelThickness.values()) {
      assertEquals(thickness.getLabel(locale),
          design.thickness.getItemCaptionGenerator().apply(thickness));
    }
    assertEquals(resources.message(COLORATION), design.coloration.getCaption());
    assertEquals(GelColoration.getNullLabel(locale), design.coloration.getEmptySelectionCaption());
    for (GelColoration coloration : GelColoration.values()) {
      assertEquals(coloration.getLabel(locale),
          design.coloration.getItemCaptionGenerator().apply(coloration));
    }
    assertEquals(resources.message(OTHER_COLORATION), design.otherColoration.getCaption());
    assertEquals(resources.message(DEVELOPMENT_TIME), design.developmentTime.getCaption());
    assertEquals(resources.message(DEVELOPMENT_TIME + "." + EXAMPLE),
        design.developmentTime.getPlaceholder());
    assertEquals(resources.message(DECOLORATION), design.decoloration.getCaption());
    assertEquals(resources.message(WEIGHT_MARKER_QUANTITY),
        design.weightMarkerQuantity.getCaption());
    assertEquals(resources.message(WEIGHT_MARKER_QUANTITY + "." + EXAMPLE),
        design.weightMarkerQuantity.getPlaceholder());
    assertEquals(resources.message(PROTEIN_QUANTITY), design.proteinQuantity.getCaption());
    assertEquals(resources.message(PROTEIN_QUANTITY + "." + EXAMPLE),
        design.proteinQuantity.getPlaceholder());
  }

  @Test
  public void requiredFields() {
    presenter.init(view);

    assertTrue(design.separation.isRequiredIndicatorVisible());
    assertTrue(design.thickness.isRequiredIndicatorVisible());
    assertFalse(design.coloration.isRequiredIndicatorVisible());
    assertTrue(design.otherColoration.isRequiredIndicatorVisible());
    assertFalse(design.developmentTime.isRequiredIndicatorVisible());
    assertFalse(design.decoloration.isRequiredIndicatorVisible());
    assertFalse(design.weightMarkerQuantity.isRequiredIndicatorVisible());
    assertFalse(design.proteinQuantity.isRequiredIndicatorVisible());
  }

  @Test
  public void visible_Default() {
    presenter.init(view);

    assertFalse(design.otherColoration.isVisible());
  }

  @Test
  public void visible_OtherColoration() {
    presenter.init(view);
    design.coloration.setValue(GelColoration.OTHER);

    assertTrue(design.otherColoration.isVisible());
  }

  @Test
  public void visible_SetValueOtherColoration() {
    presenter.init(view);
    Submission submission = new Submission();
    submission.setColoration(GelColoration.OTHER);
    presenter.setValue(submission);

    assertTrue(design.otherColoration.isVisible());
  }

  @Test
  public void visible_ColorationUpdateFromOther() {
    presenter.init(view);
    Submission submission = new Submission();
    presenter.setValue(submission);

    design.coloration.setValue(GelColoration.COOMASSIE);

    assertFalse(design.otherColoration.isVisible());
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

    presenter.setReadOnly(false);

    assertFalse(design.separation.isReadOnly());
    assertFalse(design.thickness.isReadOnly());
    assertFalse(design.coloration.isReadOnly());
    assertFalse(design.otherColoration.isReadOnly());
    assertFalse(design.developmentTime.isReadOnly());
    assertFalse(design.decoloration.isReadOnly());
    assertFalse(design.weightMarkerQuantity.isReadOnly());
    assertFalse(design.proteinQuantity.isReadOnly());
  }

  @Test
  public void setReadOnly_True() {
    presenter.setReadOnly(true);
    presenter.init(view);

    assertTrue(design.separation.isReadOnly());
    assertTrue(design.thickness.isReadOnly());
    assertTrue(design.coloration.isReadOnly());
    assertTrue(design.otherColoration.isReadOnly());
    assertTrue(design.developmentTime.isReadOnly());
    assertTrue(design.decoloration.isReadOnly());
    assertTrue(design.weightMarkerQuantity.isReadOnly());
    assertTrue(design.proteinQuantity.isReadOnly());
  }

  @Test
  public void setReadOnly_TrueAfterInit() {
    presenter.init(view);

    presenter.setReadOnly(true);

    assertTrue(design.separation.isReadOnly());
    assertTrue(design.thickness.isReadOnly());
    assertTrue(design.coloration.isReadOnly());
    assertTrue(design.otherColoration.isReadOnly());
    assertTrue(design.developmentTime.isReadOnly());
    assertTrue(design.decoloration.isReadOnly());
    assertTrue(design.weightMarkerQuantity.isReadOnly());
    assertTrue(design.proteinQuantity.isReadOnly());
  }

  @Test
  public void validate_MissingGelSeparation() throws Throwable {
    presenter.init(view);
    setFields();
    design.separation.setValue(null);

    assertFalse(presenter.validate());

    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.separation.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_MissingGelThickness() throws Throwable {
    presenter.init(view);
    setFields();
    design.thickness.setValue(null);

    assertFalse(presenter.validate());

    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.thickness.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_MissingOtherGelColoration() throws Throwable {
    presenter.init(view);
    setFields();
    design.coloration.setValue(GelColoration.OTHER);
    design.otherColoration.setValue("");

    assertFalse(presenter.validate());

    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.otherColoration.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void validate_InvalidWeightMarkerQuantity() throws Throwable {
    presenter.init(view);
    setFields();
    design.weightMarkerQuantity.setValue("a");

    assertFalse(presenter.validate());

    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        design.weightMarkerQuantity.getErrorMessage().getFormattedHtmlMessage());
  }

  @Test
  public void getValue_Default() throws Throwable {
    presenter.init(view);

    Submission submission = presenter.getValue();

    assertEquals(null, submission.getSeparation());
    assertEquals(null, submission.getThickness());
    assertEquals(null, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(null, submission.getDevelopmentTime());
    assertEquals(false, submission.isDecoloration());
    assertEquals(null, submission.getWeightMarkerQuantity());
    assertEquals(null, submission.getProteinQuantity());
  }

  @Test
  public void getValue_Values() throws Throwable {
    presenter.init(view);
    setFields();

    Submission submission = presenter.getValue();

    assertEquals(separation, submission.getSeparation());
    assertEquals(thickness, submission.getThickness());
    assertEquals(coloration, submission.getColoration());
    assertEquals(otherColoration, submission.getOtherColoration());
    assertEquals(developmentTime, submission.getDevelopmentTime());
    assertEquals(decoloration, submission.isDecoloration());
    assertEquals(weightMarkerQuantity, submission.getWeightMarkerQuantity(), 0.0000001);
    assertEquals(proteinQuantity, submission.getProteinQuantity());
  }

  @Test
  public void getValue_NotOtherColoration() throws Throwable {
    presenter.init(view);
    setFields();
    design.coloration.setValue(GelColoration.COOMASSIE);

    Submission submission = presenter.getValue();

    assertEquals(separation, submission.getSeparation());
    assertEquals(thickness, submission.getThickness());
    assertEquals(GelColoration.COOMASSIE, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(developmentTime, submission.getDevelopmentTime());
    assertEquals(decoloration, submission.isDecoloration());
    assertEquals(weightMarkerQuantity, submission.getWeightMarkerQuantity(), 0.0000001);
    assertEquals(proteinQuantity, submission.getProteinQuantity());
  }

  @Test
  public void getValue_NoChange() throws Throwable {
    Submission filler = new Submission();
    filler.setSeparation(separation);
    filler.setThickness(thickness);
    filler.setColoration(coloration);
    filler.setOtherColoration(otherColoration);
    filler.setDevelopmentTime(developmentTime);
    filler.setDecoloration(decoloration);
    filler.setWeightMarkerQuantity(weightMarkerQuantity);
    filler.setProteinQuantity(proteinQuantity);
    presenter.setValue(filler);
    presenter.init(view);

    Submission submission = presenter.getValue();

    assertEquals(separation, submission.getSeparation());
    assertEquals(thickness, submission.getThickness());
    assertEquals(coloration, submission.getColoration());
    assertEquals(otherColoration, submission.getOtherColoration());
    assertEquals(developmentTime, submission.getDevelopmentTime());
    assertEquals(decoloration, submission.isDecoloration());
    assertEquals(weightMarkerQuantity, submission.getWeightMarkerQuantity(), 0.0000001);
    assertEquals(proteinQuantity, submission.getProteinQuantity());
  }

  @Test
  public void getValue_NoChangeAfterInit() throws Throwable {
    presenter.init(view);
    Submission filler = new Submission();
    filler.setSeparation(separation);
    filler.setThickness(thickness);
    filler.setColoration(coloration);
    filler.setOtherColoration(otherColoration);
    filler.setDevelopmentTime(developmentTime);
    filler.setDecoloration(decoloration);
    filler.setWeightMarkerQuantity(weightMarkerQuantity);
    filler.setProteinQuantity(proteinQuantity);
    presenter.setValue(filler);

    Submission submission = presenter.getValue();

    assertEquals(separation, submission.getSeparation());
    assertEquals(thickness, submission.getThickness());
    assertEquals(coloration, submission.getColoration());
    assertEquals(otherColoration, submission.getOtherColoration());
    assertEquals(developmentTime, submission.getDevelopmentTime());
    assertEquals(decoloration, submission.isDecoloration());
    assertEquals(weightMarkerQuantity, submission.getWeightMarkerQuantity(), 0.0000001);
    assertEquals(proteinQuantity, submission.getProteinQuantity());
  }

  @Test
  public void getValue_Update() throws Throwable {
    Submission filler = new Submission();
    filler.setSeparation(GelSeparation.ONE_DIMENSION);
    filler.setThickness(GelThickness.ONE);
    filler.setColoration(GelColoration.COOMASSIE);
    filler.setOtherColoration("other coloration");
    filler.setDevelopmentTime("2 sec");
    filler.setDecoloration(false);
    filler.setWeightMarkerQuantity(30.0);
    filler.setProteinQuantity("2 ug");
    presenter.setValue(filler);
    presenter.init(view);
    setFields();

    Submission submission = presenter.getValue();

    assertEquals(separation, submission.getSeparation());
    assertEquals(thickness, submission.getThickness());
    assertEquals(coloration, submission.getColoration());
    assertEquals(otherColoration, submission.getOtherColoration());
    assertEquals(developmentTime, submission.getDevelopmentTime());
    assertEquals(decoloration, submission.isDecoloration());
    assertEquals(weightMarkerQuantity, submission.getWeightMarkerQuantity(), 0.0000001);
    assertEquals(proteinQuantity, submission.getProteinQuantity());
  }

  @Test
  public void setValue_Default() throws Throwable {
    presenter.init(view);
    presenter.setValue(new Submission());

    assertEquals(null, design.separation.getValue());
    assertEquals(null, design.thickness.getValue());
    assertEquals(null, design.coloration.getValue());
    assertEquals("", design.otherColoration.getValue());
    assertEquals("", design.developmentTime.getValue());
    assertEquals(false, design.decoloration.getValue());
    assertEquals("", design.weightMarkerQuantity.getValue());
    assertEquals("", design.proteinQuantity.getValue());
  }

  @Test
  public void setValue_Values() throws Throwable {
    presenter.init(view);
    Submission filler = new Submission();
    filler.setSeparation(separation);
    filler.setThickness(thickness);
    filler.setColoration(coloration);
    filler.setOtherColoration(otherColoration);
    filler.setDevelopmentTime(developmentTime);
    filler.setDecoloration(decoloration);
    filler.setWeightMarkerQuantity(weightMarkerQuantity);
    filler.setProteinQuantity(proteinQuantity);
    presenter.setValue(filler);

    assertEquals(separation, design.separation.getValue());
    assertEquals(thickness, design.thickness.getValue());
    assertEquals(coloration, design.coloration.getValue());
    assertEquals(otherColoration, design.otherColoration.getValue());
    assertEquals(developmentTime, design.developmentTime.getValue());
    assertEquals(decoloration, design.decoloration.getValue());
    assertEquals(weightMarkerQuantity, Double.parseDouble(design.weightMarkerQuantity.getValue()),
        0.0000001);
    assertEquals(proteinQuantity, design.proteinQuantity.getValue());
  }
}
