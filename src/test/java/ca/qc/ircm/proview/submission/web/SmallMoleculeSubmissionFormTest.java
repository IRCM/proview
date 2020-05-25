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

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.AVERAGE_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FORMULA;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.LIGHT_SENSITIVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MONOISOTOPIC_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOLUTION_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOLVENTS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STORAGE_TEMPERATURE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TOXICITY;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.CLASS_NAME;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.SAMPLE_NAME;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.SAMPLE_TYPE;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.text.Strings.property;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.List;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class SmallMoleculeSubmissionFormTest extends AbstractViewTestCase {
  private SmallMoleculeSubmissionForm form;
  @Mock
  private SmallMoleculeSubmissionFormPresenter presenter;
  @Mock
  private Submission submission;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(SmallMoleculeSubmissionForm.class, locale);
  private AppResources submissionResources = new AppResources(Submission.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    form = new SmallMoleculeSubmissionForm(presenter);
    form.init();
  }

  @Test
  public void presenter_Init() {
    verify(presenter).init(form);
  }

  @Test
  public void styles() {
    assertTrue(form.hasClassName(CLASS_NAME));
    assertTrue(form.sampleType.hasClassName(SAMPLE_TYPE));
    assertTrue(form.sampleName.hasClassName(SAMPLE_NAME));
    assertTrue(form.solvent.hasClassName(SOLUTION_SOLVENT));
    assertTrue(form.formula.hasClassName(FORMULA));
    assertTrue(form.monoisotopicMass.hasClassName(MONOISOTOPIC_MASS));
    assertTrue(form.averageMass.hasClassName(AVERAGE_MASS));
    assertTrue(form.toxicity.hasClassName(TOXICITY));
    assertTrue(form.lightSensitive.hasClassName(LIGHT_SENSITIVE));
    assertTrue(form.storageTemperature.hasClassName(STORAGE_TEMPERATURE));
    assertTrue(form.highResolution.hasClassName(HIGH_RESOLUTION));
    assertTrue(form.otherSolvent.hasClassName(OTHER_SOLVENT));
  }

  @Test
  public void labels() {
    form.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(SAMPLE_TYPE), form.sampleType.getLabel());
    assertEquals(resources.message(SAMPLE_NAME), form.sampleName.getLabel());
    assertEquals(submissionResources.message(SOLUTION_SOLVENT), form.solvent.getLabel());
    assertEquals(submissionResources.message(FORMULA), form.formula.getLabel());
    assertEquals(submissionResources.message(MONOISOTOPIC_MASS), form.monoisotopicMass.getLabel());
    assertEquals(submissionResources.message(AVERAGE_MASS), form.averageMass.getLabel());
    assertEquals(submissionResources.message(TOXICITY), form.toxicity.getLabel());
    assertEquals(submissionResources.message(LIGHT_SENSITIVE), form.lightSensitive.getLabel());
    assertEquals(submissionResources.message(STORAGE_TEMPERATURE),
        form.storageTemperature.getLabel());
    assertEquals(submissionResources.message(HIGH_RESOLUTION), form.highResolution.getLabel());
    assertEquals(submissionResources.message(SOLVENTS), form.solvents.getLabel());
    assertEquals(submissionResources.message(OTHER_SOLVENT), form.otherSolvent.getLabel());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void localeChange() {
    form.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(SmallMoleculeSubmissionForm.class, locale);
    final AppResources submissionResources = new AppResources(Submission.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    form.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(SAMPLE_TYPE), form.sampleType.getLabel());
    assertEquals(resources.message(SAMPLE_NAME), form.sampleName.getLabel());
    assertEquals(submissionResources.message(SOLUTION_SOLVENT), form.solvent.getLabel());
    assertEquals(submissionResources.message(FORMULA), form.formula.getLabel());
    assertEquals(submissionResources.message(MONOISOTOPIC_MASS), form.monoisotopicMass.getLabel());
    assertEquals(submissionResources.message(AVERAGE_MASS), form.averageMass.getLabel());
    assertEquals(submissionResources.message(TOXICITY), form.toxicity.getLabel());
    assertEquals(submissionResources.message(LIGHT_SENSITIVE), form.lightSensitive.getLabel());
    assertEquals(submissionResources.message(STORAGE_TEMPERATURE),
        form.storageTemperature.getLabel());
    assertEquals(submissionResources.message(HIGH_RESOLUTION), form.highResolution.getLabel());
    assertEquals(submissionResources.message(SOLVENTS), form.solvents.getLabel());
    assertEquals(submissionResources.message(OTHER_SOLVENT), form.otherSolvent.getLabel());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void sampleTypes() {
    List<SampleType> items = items(form.sampleType);
    assertEquals(2, items.size());
    for (SampleType value : new SampleType[] { DRY, SOLUTION }) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale),
          form.sampleType.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void storageTemperatures() {
    List<StorageTemperature> items = items(form.storageTemperature);
    assertEquals(StorageTemperature.values().length, items.size());
    for (StorageTemperature value : StorageTemperature.values()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale),
          form.storageTemperature.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void highResolution() {
    List<Boolean> items = items(form.highResolution);
    assertEquals(2, items.size());
    for (Boolean value : new Boolean[] { false, true }) {
      assertTrue(items.contains(value));
      assertEquals(submissionResources.message(property(HIGH_RESOLUTION, value)),
          form.highResolution.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void isValid_True() {
    when(presenter.isValid()).thenReturn(true);
    assertTrue(form.isValid());
    verify(presenter).isValid();
  }

  @Test
  public void isValid_False() {
    when(presenter.isValid()).thenReturn(false);
    assertFalse(form.isValid());
    verify(presenter).isValid();
  }

  @Test
  public void getSubmission() {
    when(presenter.getSubmission()).thenReturn(submission);
    Submission submission = form.getSubmission();
    verify(presenter).getSubmission();
    assertEquals(this.submission, submission);
  }

  @Test
  public void setSubmission() {
    form.setSubmission(submission);
    verify(presenter).setSubmission(submission);
  }
}
