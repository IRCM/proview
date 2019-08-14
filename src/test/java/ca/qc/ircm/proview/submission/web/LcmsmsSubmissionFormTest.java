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
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.CONTAMINANTS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DECOLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DEVELOPMENT_TIME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.IDENTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.IDENTIFICATION_LINK;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_CONTENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_QUANTITY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION_COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SEPARATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STANDARDS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.THICKNESS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USED_DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.CLASS_NAME;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.CONTAMINANTS_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.DEVELOPMENT_TIME_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.PROTEIN_QUANTITY_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.QUANTIFICATION_COMMENT_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.QUANTIFICATION_COMMENT_PLACEHOLDER_TMT;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.QUANTITY_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_COUNT;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_TYPE;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.STANDARDS_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.VOLUME_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.WEIGHT_MARKER_QUANTITY_PLACEHOLDER;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.FRENCH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Quantification;
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
public class LcmsmsSubmissionFormTest extends AbstractViewTestCase {
  private LcmsmsSubmissionForm form;
  @Mock
  private LcmsmsSubmissionFormPresenter presenter;
  @Mock
  private Submission submission;
  private Locale locale = ENGLISH;
  private MessageResource resources = new MessageResource(LcmsmsSubmissionForm.class, locale);
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
    form = new LcmsmsSubmissionForm(presenter);
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
    assertTrue(form.contaminants.getClassName().contains(CONTAMINANTS));
    assertTrue(form.standards.getClassName().contains(STANDARDS));
    assertTrue(form.separation.getClassName().contains(SEPARATION));
    assertTrue(form.thickness.getClassName().contains(THICKNESS));
    assertTrue(form.coloration.getClassName().contains(COLORATION));
    assertTrue(form.otherColoration.getClassName().contains(OTHER_COLORATION));
    assertTrue(form.developmentTime.getClassName().contains(DEVELOPMENT_TIME));
    assertTrue(form.destained.getClassName().contains(DECOLORATION));
    assertTrue(form.weightMarkerQuantity.getClassName().contains(WEIGHT_MARKER_QUANTITY));
    assertTrue(form.proteinQuantity.getClassName().contains(PROTEIN_QUANTITY));
    assertTrue(form.digestion.getClassName().contains(DIGESTION));
    assertTrue(form.usedDigestion.getClassName().contains(USED_DIGESTION));
    assertTrue(form.otherDigestion.getClassName().contains(OTHER_DIGESTION));
    assertTrue(form.proteinContent.getClassName().contains(PROTEIN_CONTENT));
    assertTrue(form.instrument.getClassName().contains(INSTRUMENT));
    assertTrue(form.identification.getClassName().contains(IDENTIFICATION));
    assertTrue(form.identificationLink.getClassName().contains(IDENTIFICATION_LINK));
    assertTrue(form.quantification.getClassName().contains(QUANTIFICATION));
    assertTrue(form.quantificationComment.getClassName().contains(QUANTIFICATION_COMMENT));
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
    assertEquals(submissionResources.message(CONTAMINANTS), form.contaminants.getLabel());
    assertEquals(resources.message(CONTAMINANTS_PLACEHOLDER), form.contaminants.getPlaceholder());
    assertEquals(submissionResources.message(STANDARDS), form.standards.getLabel());
    assertEquals(resources.message(STANDARDS_PLACEHOLDER), form.standards.getPlaceholder());
    assertEquals(submissionResources.message(SEPARATION), form.separation.getLabel());
    assertEquals(submissionResources.message(THICKNESS), form.thickness.getLabel());
    assertEquals(submissionResources.message(COLORATION), form.coloration.getLabel());
    assertEquals(submissionResources.message(OTHER_COLORATION), form.otherColoration.getLabel());
    assertEquals(submissionResources.message(DEVELOPMENT_TIME), form.developmentTime.getLabel());
    assertEquals(resources.message(DEVELOPMENT_TIME_PLACEHOLDER),
        form.developmentTime.getPlaceholder());
    assertEquals(submissionResources.message(DECOLORATION), form.destained.getLabel());
    assertEquals(submissionResources.message(WEIGHT_MARKER_QUANTITY),
        form.weightMarkerQuantity.getLabel());
    assertEquals(resources.message(WEIGHT_MARKER_QUANTITY_PLACEHOLDER),
        form.weightMarkerQuantity.getPlaceholder());
    assertEquals(submissionResources.message(PROTEIN_QUANTITY), form.proteinQuantity.getLabel());
    assertEquals(resources.message(PROTEIN_QUANTITY_PLACEHOLDER),
        form.proteinQuantity.getPlaceholder());
    assertEquals(submissionResources.message(DIGESTION), form.digestion.getLabel());
    assertEquals(submissionResources.message(USED_DIGESTION), form.usedDigestion.getLabel());
    assertEquals(submissionResources.message(OTHER_DIGESTION), form.otherDigestion.getLabel());
    assertEquals(submissionResources.message(PROTEIN_CONTENT), form.proteinContent.getLabel());
    assertEquals(submissionResources.message(INSTRUMENT), form.instrument.getLabel());
    assertEquals(submissionResources.message(IDENTIFICATION), form.identification.getLabel());
    assertEquals(submissionResources.message(IDENTIFICATION_LINK),
        form.identificationLink.getLabel());
    assertEquals(submissionResources.message(QUANTIFICATION), form.quantification.getLabel());
    assertEquals(submissionResources.message(QUANTIFICATION_COMMENT),
        form.quantificationComment.getLabel());
    assertEquals(resources.message(QUANTIFICATION_COMMENT_PLACEHOLDER),
        form.quantificationComment.getPlaceholder());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void localeChange() {
    form.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final MessageResource resources = new MessageResource(LcmsmsSubmissionForm.class, locale);
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
    assertEquals(submissionResources.message(CONTAMINANTS), form.contaminants.getLabel());
    assertEquals(resources.message(CONTAMINANTS_PLACEHOLDER), form.contaminants.getPlaceholder());
    assertEquals(submissionResources.message(STANDARDS), form.standards.getLabel());
    assertEquals(resources.message(STANDARDS_PLACEHOLDER), form.standards.getPlaceholder());
    assertEquals(submissionResources.message(SEPARATION), form.separation.getLabel());
    assertEquals(submissionResources.message(THICKNESS), form.thickness.getLabel());
    assertEquals(submissionResources.message(COLORATION), form.coloration.getLabel());
    assertEquals(submissionResources.message(OTHER_COLORATION), form.otherColoration.getLabel());
    assertEquals(submissionResources.message(DEVELOPMENT_TIME), form.developmentTime.getLabel());
    assertEquals(resources.message(DEVELOPMENT_TIME_PLACEHOLDER),
        form.developmentTime.getPlaceholder());
    assertEquals(submissionResources.message(DECOLORATION), form.destained.getLabel());
    assertEquals(submissionResources.message(WEIGHT_MARKER_QUANTITY),
        form.weightMarkerQuantity.getLabel());
    assertEquals(resources.message(WEIGHT_MARKER_QUANTITY_PLACEHOLDER),
        form.weightMarkerQuantity.getPlaceholder());
    assertEquals(submissionResources.message(PROTEIN_QUANTITY), form.proteinQuantity.getLabel());
    assertEquals(resources.message(PROTEIN_QUANTITY_PLACEHOLDER),
        form.proteinQuantity.getPlaceholder());
    assertEquals(submissionResources.message(DIGESTION), form.digestion.getLabel());
    assertEquals(submissionResources.message(USED_DIGESTION), form.usedDigestion.getLabel());
    assertEquals(submissionResources.message(OTHER_DIGESTION), form.otherDigestion.getLabel());
    assertEquals(submissionResources.message(PROTEIN_CONTENT), form.proteinContent.getLabel());
    assertEquals(submissionResources.message(INSTRUMENT), form.instrument.getLabel());
    assertEquals(submissionResources.message(IDENTIFICATION), form.identification.getLabel());
    assertEquals(submissionResources.message(IDENTIFICATION_LINK),
        form.identificationLink.getLabel());
    assertEquals(submissionResources.message(QUANTIFICATION), form.quantification.getLabel());
    assertEquals(submissionResources.message(QUANTIFICATION_COMMENT),
        form.quantificationComment.getLabel());
    assertEquals(resources.message(QUANTIFICATION_COMMENT_PLACEHOLDER),
        form.quantificationComment.getPlaceholder());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void sampleTypes() {
    List<SampleType> items = items(form.sampleType);
    assertEquals(SampleType.values().length, items.size());
    for (SampleType value : SampleType.values()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale),
          form.sampleType.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void separations() {
    List<GelSeparation> items = items(form.separation);
    assertEquals(GelSeparation.values().length, items.size());
    for (GelSeparation value : GelSeparation.values()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale), form.separation.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void thicknesses() {
    List<GelThickness> items = items(form.thickness);
    assertEquals(GelThickness.values().length, items.size());
    for (GelThickness value : GelThickness.values()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale), form.thickness.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void colorations() {
    List<GelColoration> items = items(form.coloration);
    assertEquals(GelColoration.values().length, items.size());
    for (GelColoration value : GelColoration.values()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale), form.coloration.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void digestions() {
    List<ProteolyticDigestion> items = items(form.digestion);
    assertEquals(ProteolyticDigestion.values().length, items.size());
    for (ProteolyticDigestion value : ProteolyticDigestion.values()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale), form.digestion.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void proteinContents() {
    List<ProteinContent> items = items(form.proteinContent);
    assertEquals(ProteinContent.values().length, items.size());
    for (ProteinContent value : ProteinContent.values()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale),
          form.proteinContent.getItemRenderer().createComponent(value).getElement().getText());
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
  public void identifications() {
    List<ProteinIdentification> items = items(form.identification);
    assertEquals(ProteinIdentification.availables().size(), items.size());
    for (ProteinIdentification value : ProteinIdentification.availables()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale),
          form.identification.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void quantifications() {
    List<Quantification> items = items(form.quantification);
    assertEquals(Quantification.values().length, items.size());
    for (Quantification value : Quantification.values()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale),
          form.quantification.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void quantificationComment() {
    form.quantification.setValue(Quantification.TMT);
    assertEquals(resources.message(QUANTIFICATION_COMMENT_PLACEHOLDER_TMT),
        form.quantificationComment.getPlaceholder());
    form.quantification.setValue(Quantification.SILAC);
    assertEquals(resources.message(QUANTIFICATION_COMMENT_PLACEHOLDER),
        form.quantificationComment.getPlaceholder());
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