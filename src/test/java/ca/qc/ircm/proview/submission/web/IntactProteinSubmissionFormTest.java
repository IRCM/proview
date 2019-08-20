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

import static ca.qc.ircm.proview.sample.SampleProperties.QUANTITY;
import static ca.qc.ircm.proview.sample.SampleProperties.VOLUME;
import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INJECTION_TYPE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOURCE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.CLASS_NAME;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.QUANTITY_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_COUNT;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_NAMES;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_TYPE;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.VOLUME_PLACEHOLDER;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.FRENCH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
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
public class IntactProteinSubmissionFormTest extends AbstractViewTestCase {
  private IntactProteinSubmissionForm form;
  @Mock
  private IntactProteinSubmissionFormPresenter presenter;
  @Mock
  private Submission submission;
  private Locale locale = ENGLISH;
  private MessageResource resources =
      new MessageResource(IntactProteinSubmissionForm.class, locale);
  private MessageResource submissionResources = new MessageResource(Submission.class, locale);
  private MessageResource sampleResources = new MessageResource(Sample.class, locale);
  private MessageResource submissionSampleResources =
      new MessageResource(SubmissionSample.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    form = new IntactProteinSubmissionForm(presenter);
    form.init();
  }

  @Test
  public void presenter_Init() {
    verify(presenter).init(form);
  }

  @Test
  public void styles() {
    assertTrue(form.getClassName().contains(CLASS_NAME));
    assertTrue(form.goal.getClassName().contains(GOAL));
    assertTrue(form.taxonomy.getClassName().contains(TAXONOMY));
    assertTrue(form.protein.getClassName().contains(PROTEIN));
    assertTrue(form.molecularWeight.getClassName().contains(MOLECULAR_WEIGHT));
    assertTrue(
        form.postTranslationModification.getClassName().contains(POST_TRANSLATION_MODIFICATION));
    assertTrue(form.sampleType.getClassName().contains(SAMPLES_TYPE));
    assertTrue(form.samplesCount.getClassName().contains(SAMPLES_COUNT));
    assertTrue(form.samplesNames.getClassName().contains(SAMPLES_NAMES));
    assertTrue(form.quantity.getClassName().contains(QUANTITY));
    assertTrue(form.volume.getClassName().contains(VOLUME));
    assertTrue(form.injection.getClassName().contains(INJECTION_TYPE));
    assertTrue(form.source.getClassName().contains(SOURCE));
    assertTrue(form.instrument.getClassName().contains(INSTRUMENT));
  }

  @Test
  public void labels() {
    form.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(submissionResources.message(GOAL), form.goal.getLabel());
    assertEquals(submissionResources.message(TAXONOMY), form.taxonomy.getLabel());
    assertEquals(submissionResources.message(PROTEIN), form.protein.getLabel());
    assertEquals(submissionSampleResources.message(MOLECULAR_WEIGHT),
        form.molecularWeight.getLabel());
    assertEquals(submissionResources.message(POST_TRANSLATION_MODIFICATION),
        form.postTranslationModification.getLabel());
    assertEquals(resources.message(SAMPLES_TYPE), form.sampleType.getLabel());
    assertEquals(resources.message(SAMPLES_COUNT), form.samplesCount.getLabel());
    assertEquals(resources.message(SAMPLES_NAMES), form.samplesNames.getLabel());
    assertEquals(sampleResources.message(QUANTITY), form.quantity.getLabel());
    assertEquals(resources.message(QUANTITY_PLACEHOLDER), form.quantity.getPlaceholder());
    assertEquals(sampleResources.message(VOLUME), form.volume.getLabel());
    assertEquals(resources.message(VOLUME_PLACEHOLDER), form.volume.getPlaceholder());
    assertEquals(submissionResources.message(INJECTION_TYPE), form.injection.getLabel());
    assertEquals(submissionResources.message(SOURCE), form.source.getLabel());
    assertEquals(submissionResources.message(INSTRUMENT), form.instrument.getLabel());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void localeChange() {
    form.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final MessageResource resources =
        new MessageResource(IntactProteinSubmissionForm.class, locale);
    final MessageResource submissionResources = new MessageResource(Submission.class, locale);
    final MessageResource sampleResources = new MessageResource(Sample.class, locale);
    final MessageResource submissionSampleResources =
        new MessageResource(SubmissionSample.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    form.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(submissionResources.message(GOAL), form.goal.getLabel());
    assertEquals(submissionResources.message(TAXONOMY), form.taxonomy.getLabel());
    assertEquals(submissionResources.message(PROTEIN), form.protein.getLabel());
    assertEquals(submissionSampleResources.message(MOLECULAR_WEIGHT),
        form.molecularWeight.getLabel());
    assertEquals(submissionResources.message(POST_TRANSLATION_MODIFICATION),
        form.postTranslationModification.getLabel());
    assertEquals(resources.message(SAMPLES_TYPE), form.sampleType.getLabel());
    assertEquals(resources.message(SAMPLES_COUNT), form.samplesCount.getLabel());
    assertEquals(resources.message(SAMPLES_NAMES), form.samplesNames.getLabel());
    assertEquals(sampleResources.message(QUANTITY), form.quantity.getLabel());
    assertEquals(resources.message(QUANTITY_PLACEHOLDER), form.quantity.getPlaceholder());
    assertEquals(sampleResources.message(VOLUME), form.volume.getLabel());
    assertEquals(resources.message(VOLUME_PLACEHOLDER), form.volume.getPlaceholder());
    assertEquals(submissionResources.message(INJECTION_TYPE), form.injection.getLabel());
    assertEquals(submissionResources.message(SOURCE), form.source.getLabel());
    assertEquals(submissionResources.message(INSTRUMENT), form.instrument.getLabel());
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
  public void injectionTypes() {
    List<InjectionType> items = items(form.injection);
    assertEquals(InjectionType.values().length, items.size());
    for (InjectionType value : InjectionType.values()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale),
          form.injection.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void sources() {
    List<MassDetectionInstrumentSource> items = items(form.source);
    assertEquals(MassDetectionInstrumentSource.availables().size(), items.size());
    for (MassDetectionInstrumentSource value : MassDetectionInstrumentSource.availables()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale),
          form.source.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void instruments() {
    List<MassDetectionInstrument> items = items(form.instrument);
    assertEquals(MassDetectionInstrument.userChoices().size(), items.size());
    for (MassDetectionInstrument value : MassDetectionInstrument.userChoices()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale), form.instrument.getItemLabelGenerator().apply(value));
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
