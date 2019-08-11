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
  private IntactProteinSubmissionForm view;
  @Mock
  private IntactProteinSubmissionFormPresenter presenter;
  @Mock
  private Submission submission;
  private Locale locale = ENGLISH;
  private MessageResource resources = new MessageResource(IntactProteinSubmissionForm.class,
      locale);
  private MessageResource submissionResources = new MessageResource(Submission.class, locale);
  private MessageResource sampleResources = new MessageResource(Sample.class, locale);
  private MessageResource submissionSampleResources = new MessageResource(SubmissionSample.class,
      locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    view = new IntactProteinSubmissionForm(presenter);
    view.init();
  }

  @Test
  public void presenter_Init() {
    verify(presenter).init(view);
  }

  @Test
  public void styles() {
    assertTrue(view.getClassName().contains(CLASS_NAME));
    assertTrue(view.goal.getClassName().contains(GOAL));
    assertTrue(view.taxonomy.getClassName().contains(TAXONOMY));
    assertTrue(view.protein.getClassName().contains(PROTEIN));
    assertTrue(view.molecularWeight.getClassName().contains(MOLECULAR_WEIGHT));
    assertTrue(
        view.postTranslationModification.getClassName().contains(POST_TRANSLATION_MODIFICATION));
    assertTrue(view.sampleType.getClassName().contains(SAMPLES_TYPE));
    assertTrue(view.samplesCount.getClassName().contains(SAMPLES_COUNT));
    assertTrue(view.samplesNames.getClassName().contains(SAMPLES_NAMES));
    assertTrue(view.quantity.getClassName().contains(QUANTITY));
    assertTrue(view.volume.getClassName().contains(VOLUME));
    assertTrue(view.injection.getClassName().contains(INJECTION_TYPE));
    assertTrue(view.source.getClassName().contains(SOURCE));
    assertTrue(view.instrument.getClassName().contains(INSTRUMENT));
  }

  @Test
  public void labels() {
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(submissionResources.message(GOAL), view.goal.getLabel());
    assertEquals(submissionResources.message(TAXONOMY), view.taxonomy.getLabel());
    assertEquals(submissionResources.message(PROTEIN), view.protein.getLabel());
    assertEquals(submissionSampleResources.message(MOLECULAR_WEIGHT),
        view.molecularWeight.getLabel());
    assertEquals(submissionResources.message(POST_TRANSLATION_MODIFICATION),
        view.postTranslationModification.getLabel());
    assertEquals(resources.message(SAMPLES_TYPE), view.sampleType.getLabel());
    assertEquals(resources.message(SAMPLES_COUNT), view.samplesCount.getLabel());
    assertEquals(resources.message(SAMPLES_NAMES), view.samplesNames.getLabel());
    assertEquals(sampleResources.message(QUANTITY), view.quantity.getLabel());
    assertEquals(resources.message(QUANTITY_PLACEHOLDER), view.quantity.getPlaceholder());
    assertEquals(sampleResources.message(VOLUME), view.volume.getLabel());
    assertEquals(resources.message(VOLUME_PLACEHOLDER), view.volume.getPlaceholder());
    assertEquals(submissionResources.message(INJECTION_TYPE), view.injection.getLabel());
    assertEquals(submissionResources.message(SOURCE), view.source.getLabel());
    assertEquals(submissionResources.message(INSTRUMENT), view.instrument.getLabel());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void localeChange() {
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final MessageResource resources = new MessageResource(IntactProteinSubmissionForm.class,
        locale);
    final MessageResource submissionResources = new MessageResource(Submission.class, locale);
    final MessageResource sampleResources = new MessageResource(Sample.class, locale);
    final MessageResource submissionSampleResources = new MessageResource(SubmissionSample.class,
        locale);
    when(ui.getLocale()).thenReturn(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(submissionResources.message(GOAL), view.goal.getLabel());
    assertEquals(submissionResources.message(TAXONOMY), view.taxonomy.getLabel());
    assertEquals(submissionResources.message(PROTEIN), view.protein.getLabel());
    assertEquals(submissionSampleResources.message(MOLECULAR_WEIGHT),
        view.molecularWeight.getLabel());
    assertEquals(submissionResources.message(POST_TRANSLATION_MODIFICATION),
        view.postTranslationModification.getLabel());
    assertEquals(resources.message(SAMPLES_TYPE), view.sampleType.getLabel());
    assertEquals(resources.message(SAMPLES_COUNT), view.samplesCount.getLabel());
    assertEquals(resources.message(SAMPLES_NAMES), view.samplesNames.getLabel());
    assertEquals(sampleResources.message(QUANTITY), view.quantity.getLabel());
    assertEquals(resources.message(QUANTITY_PLACEHOLDER), view.quantity.getPlaceholder());
    assertEquals(sampleResources.message(VOLUME), view.volume.getLabel());
    assertEquals(resources.message(VOLUME_PLACEHOLDER), view.volume.getPlaceholder());
    assertEquals(submissionResources.message(INJECTION_TYPE), view.injection.getLabel());
    assertEquals(submissionResources.message(SOURCE), view.source.getLabel());
    assertEquals(submissionResources.message(INSTRUMENT), view.instrument.getLabel());
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
  public void injectionTypes() {
    List<InjectionType> items = items(view.injection);
    assertEquals(InjectionType.values().length, items.size());
    for (InjectionType value : InjectionType.values()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale),
          view.injection.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void sources() {
    List<MassDetectionInstrumentSource> items = items(view.source);
    assertEquals(MassDetectionInstrumentSource.availables().size(), items.size());
    for (MassDetectionInstrumentSource value : MassDetectionInstrumentSource.availables()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale),
          view.source.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void instruments() {
    List<MassDetectionInstrument> items = items(view.instrument);
    assertEquals(MassDetectionInstrument.userChoices().size(), items.size());
    for (MassDetectionInstrument value : MassDetectionInstrument.userChoices()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale), view.instrument.getItemLabelGenerator().apply(value));
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
