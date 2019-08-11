/*
 * Copyright (c) 2018 Institut de recherches cliniques de Montreal (IRCM)
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

import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.AVERAGE_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FORMULA;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.LIGHT_SENSITIVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MONOISOTOPIC_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOLUTION_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STORAGE_TEMPERATURE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TOXICITY;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.CLASS_NAME;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.SAMPLE_NAME;
import static ca.qc.ircm.proview.submission.web.SmallMoleculeSubmissionForm.SAMPLE_TYPE;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.FRENCH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.text.MessageResource;
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
  private SmallMoleculeSubmissionForm view;
  @Mock
  private SmallMoleculeSubmissionFormPresenter presenter;
  @Mock
  private Submission submission;
  private Locale locale = ENGLISH;
  private MessageResource resources = new MessageResource(SmallMoleculeSubmissionForm.class,
      locale);
  private MessageResource submissionResources = new MessageResource(Submission.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    view = new SmallMoleculeSubmissionForm(presenter);
    view.init();
  }

  @Test
  public void presenter_Init() {
    verify(presenter).init(view);
  }

  @Test
  public void styles() {
    assertTrue(view.getClassName().contains(CLASS_NAME));
    assertTrue(view.sampleType.getClassName().contains(SAMPLE_TYPE));
    assertTrue(view.sampleName.getClassName().contains(SAMPLE_NAME));
    assertTrue(view.solvent.getClassName().contains(SOLUTION_SOLVENT));
    assertTrue(view.formula.getClassName().contains(FORMULA));
    assertTrue(view.monoisotopicMass.getClassName().contains(MONOISOTOPIC_MASS));
    assertTrue(view.averageMass.getClassName().contains(AVERAGE_MASS));
    assertTrue(view.toxicity.getClassName().contains(TOXICITY));
    assertTrue(view.lightSensitive.getClassName().contains(LIGHT_SENSITIVE));
    assertTrue(view.storageTemperature.getClassName().contains(STORAGE_TEMPERATURE));
    assertTrue(view.highResolution.getClassName().contains(HIGH_RESOLUTION));
  }

  @Test
  public void labels() {
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(SAMPLE_TYPE), view.sampleType.getLabel());
    assertEquals(resources.message(SAMPLE_NAME), view.sampleName.getLabel());
    assertEquals(submissionResources.message(SOLUTION_SOLVENT), view.solvent.getLabel());
    assertEquals(submissionResources.message(FORMULA), view.formula.getLabel());
    assertEquals(submissionResources.message(MONOISOTOPIC_MASS), view.monoisotopicMass.getLabel());
    assertEquals(submissionResources.message(AVERAGE_MASS), view.averageMass.getLabel());
    assertEquals(submissionResources.message(TOXICITY), view.toxicity.getLabel());
    assertEquals(submissionResources.message(LIGHT_SENSITIVE), view.lightSensitive.getLabel());
    assertEquals(submissionResources.message(STORAGE_TEMPERATURE),
        view.storageTemperature.getLabel());
    assertEquals(submissionResources.message(HIGH_RESOLUTION), view.highResolution.getLabel());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void localeChange() {
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final MessageResource resources = new MessageResource(SmallMoleculeSubmissionForm.class,
        locale);
    final MessageResource submissionResources = new MessageResource(Submission.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(SAMPLE_TYPE), view.sampleType.getLabel());
    assertEquals(resources.message(SAMPLE_NAME), view.sampleName.getLabel());
    assertEquals(submissionResources.message(SOLUTION_SOLVENT), view.solvent.getLabel());
    assertEquals(submissionResources.message(FORMULA), view.formula.getLabel());
    assertEquals(submissionResources.message(MONOISOTOPIC_MASS), view.monoisotopicMass.getLabel());
    assertEquals(submissionResources.message(AVERAGE_MASS), view.averageMass.getLabel());
    assertEquals(submissionResources.message(TOXICITY), view.toxicity.getLabel());
    assertEquals(submissionResources.message(LIGHT_SENSITIVE), view.lightSensitive.getLabel());
    assertEquals(submissionResources.message(STORAGE_TEMPERATURE),
        view.storageTemperature.getLabel());
    assertEquals(submissionResources.message(HIGH_RESOLUTION), view.highResolution.getLabel());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void sampleTypes() {
    List<SampleType> items = items(view.sampleType);
    assertEquals(2, items.size());
    for (SampleType value : new SampleType[] { DRY, SOLUTION }) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale),
          view.sampleType.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void storageTemperatures() {
    List<StorageTemperature> items = items(view.storageTemperature);
    assertEquals(StorageTemperature.values().length, items.size());
    for (StorageTemperature value : StorageTemperature.values()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale),
          view.storageTemperature.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void highResolution() {
    List<Boolean> items = items(view.highResolution);
    assertEquals(2, items.size());
    for (Boolean value : new Boolean[] { false, true }) {
      assertTrue(items.contains(value));
      assertEquals(submissionResources.message(property(HIGH_RESOLUTION, value)),
          view.highResolution.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void getSubmission() {
    when(presenter.getSubmission()).thenReturn(submission);
    Submission submission = view.getSubmission();
    verify(presenter).getSubmission();
    assertEquals(this.submission, submission);
  }

  @Test
  public void setSubmission() {
    view.setSubmission(submission);
    verify(presenter).setSubmission(submission);
  }
}
