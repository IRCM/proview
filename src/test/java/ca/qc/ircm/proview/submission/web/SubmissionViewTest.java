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
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.AVERAGE_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DECOLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DEVELOPMENT_TIME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FORMULA;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.IDENTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.IDENTIFICATION_LINK;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INJECTION_TYPE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.LIGHT_SENSITIVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MONOISOTOPIC_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_CONTENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_QUANTITY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION_COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SEPARATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOURCE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STORAGE_TEMPERATURE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.THICKNESS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TOXICITY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USED_DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.submission.web.SubmissionView.DEVELOPMENT_TIME_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.SubmissionView.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionView.ID;
import static ca.qc.ircm.proview.submission.web.SubmissionView.PROTEIN_QUANTITY_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.SubmissionView.QUANTITY_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.SubmissionView.SAMPLES_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionView.SAMPLES_NAMES;
import static ca.qc.ircm.proview.submission.web.SubmissionView.SAMPLES_TYPE;
import static ca.qc.ircm.proview.submission.web.SubmissionView.SAMPLE_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionView.VOLUME_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.SubmissionView.WEIGHT_MARKER_QUANTITY_PLACEHOLDER;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.FRENCH;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleProperties;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.BeforeEvent;
import java.util.Arrays;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class SubmissionViewTest extends AbstractViewTestCase {
  private SubmissionView view;
  @Mock
  private SubmissionViewPresenter presenter;
  @Mock
  private BeforeEvent beforeEvent;
  private Locale locale = ENGLISH;
  private MessageResource resources = new MessageResource(SubmissionView.class, locale);
  private MessageResource submissionResources = new MessageResource(Submission.class, locale);
  private MessageResource sampleResources = new MessageResource(Sample.class, locale);
  private MessageResource submissionSampleResources =
      new MessageResource(SubmissionSample.class, locale);
  private MessageResource webResources = new MessageResource(WebConstants.class, locale);

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    view = new SubmissionView(presenter);
    view.init();
  }

  @Test
  public void presenter_Init() {
    verify(presenter).init(view);
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(HEADER, view.header.getId().orElse(""));
    assertEquals(SERVICE, view.service.getId().orElse(""));
    assertEquals(LC_MS_MS.name(), view.lcmsms.getId().orElse(""));
    assertEquals(SMALL_MOLECULE.name(), view.smallMolecule.getId().orElse(""));
    assertEquals(INTACT_PROTEIN.name(), view.intactProtein.getId().orElse(""));
    assertEquals(GOAL, view.goal.getId().orElse(""));
    assertEquals(TAXONOMY, view.taxonomy.getId().orElse(""));
    assertEquals(PROTEIN, view.protein.getId().orElse(""));
    assertEquals(MOLECULAR_WEIGHT, view.molecularWeight.getId().orElse(""));
    assertEquals(POST_TRANSLATION_MODIFICATION,
        view.postTranslationModification.getId().orElse(""));
    assertEquals(SAMPLES_TYPE, view.sampleType.getId().orElse(""));
    assertEquals(SAMPLES_COUNT, view.samplesCount.getId().orElse(""));
    assertEquals(SAMPLES_NAMES, view.samplesNames.getId().orElse(""));
    assertEquals(QUANTITY, view.quantity.getId().orElse(""));
    assertEquals(VOLUME, view.volume.getId().orElse(""));
    assertEquals(SAMPLE_NAME, view.sampleName.getId().orElse(""));
    assertEquals(FORMULA, view.formula.getId().orElse(""));
    assertEquals(MONOISOTOPIC_MASS, view.monoisotopicMass.getId().orElse(""));
    assertEquals(AVERAGE_MASS, view.averageMass.getId().orElse(""));
    assertEquals(TOXICITY, view.toxicity.getId().orElse(""));
    assertEquals(LIGHT_SENSITIVE, view.lightSensitive.getId().orElse(""));
    assertEquals(STORAGE_TEMPERATURE, view.storageTemperature.getId().orElse(""));
    assertEquals(SEPARATION, view.separation.getId().orElse(""));
    assertEquals(THICKNESS, view.thickness.getId().orElse(""));
    assertEquals(COLORATION, view.coloration.getId().orElse(""));
    assertEquals(OTHER_COLORATION, view.otherColoration.getId().orElse(""));
    assertEquals(DEVELOPMENT_TIME, view.developmentTime.getId().orElse(""));
    assertEquals(DECOLORATION, view.destained.getId().orElse(""));
    assertEquals(WEIGHT_MARKER_QUANTITY, view.weightMarkerQuantity.getId().orElse(""));
    assertEquals(PROTEIN_QUANTITY, view.proteinQuantity.getId().orElse(""));
    assertEquals(DIGESTION, view.digestion.getId().orElse(""));
    assertEquals(USED_DIGESTION, view.usedDigestion.getId().orElse(""));
    assertEquals(OTHER_DIGESTION, view.otherDigestion.getId().orElse(""));
    assertEquals(PROTEIN_CONTENT, view.proteinContent.getId().orElse(""));
    assertEquals(INJECTION_TYPE, view.injection.getId().orElse(""));
    assertEquals(SOURCE, view.source.getId().orElse(""));
    assertEquals(INSTRUMENT, view.instrument.getId().orElse(""));
    assertEquals(IDENTIFICATION, view.identification.getId().orElse(""));
    assertEquals(IDENTIFICATION_LINK, view.identificationLink.getId().orElse(""));
    assertEquals(QUANTIFICATION, view.quantification.getId().orElse(""));
    assertEquals(QUANTIFICATION_COMMENT, view.quantificationComment.getId().orElse(""));
    assertEquals(HIGH_RESOLUTION, view.highResolution.getId().orElse(""));
    assertEquals(COMMENT, view.comment.getId().orElse(""));
  }

  @Test
  public void labels() {
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    assertEquals(LC_MS_MS.getLabel(locale), view.lcmsms.getLabel());
    assertEquals(SMALL_MOLECULE.getLabel(locale), view.smallMolecule.getLabel());
    assertEquals(INTACT_PROTEIN.getLabel(locale), view.intactProtein.getLabel());
    assertEquals(submissionResources.message(GOAL), view.goal.getLabel());
    assertEquals(submissionResources.message(TAXONOMY), view.taxonomy.getLabel());
    assertEquals(submissionResources.message(PROTEIN), view.protein.getLabel());
    assertEquals(submissionSampleResources.message(MOLECULAR_WEIGHT),
        view.molecularWeight.getLabel());
    assertEquals(submissionResources.message(POST_TRANSLATION_MODIFICATION),
        view.postTranslationModification.getLabel());
    assertEquals(resources.message(SAMPLES_TYPE), view.sampleType.getLabel());
    for (SampleType type : SampleType.values()) {
      assertEquals(type.getLabel(locale),
          view.sampleType.getItemRenderer().createComponent(type).getElement().getText());
    }
    assertEquals(resources.message(SAMPLES_COUNT), view.samplesCount.getLabel());
    assertEquals(resources.message(SAMPLES_NAMES), view.samplesNames.getLabel());
    assertEquals(sampleResources.message(QUANTITY), view.quantity.getLabel());
    assertEquals(resources.message(QUANTITY_PLACEHOLDER), view.quantity.getPlaceholder());
    assertEquals(sampleResources.message(VOLUME), view.volume.getLabel());
    assertEquals(resources.message(VOLUME_PLACEHOLDER), view.volume.getPlaceholder());
    assertEquals(sampleResources.message(SampleProperties.NAME), view.sampleName.getLabel());
    assertEquals(submissionResources.message(FORMULA), view.formula.getLabel());
    assertEquals(submissionResources.message(MONOISOTOPIC_MASS), view.monoisotopicMass.getLabel());
    assertEquals(submissionResources.message(AVERAGE_MASS), view.averageMass.getLabel());
    assertEquals(submissionResources.message(TOXICITY), view.toxicity.getLabel());
    assertEquals(submissionResources.message(LIGHT_SENSITIVE), view.lightSensitive.getLabel());
    assertEquals(submissionResources.message(STORAGE_TEMPERATURE),
        view.storageTemperature.getLabel());
    assertEquals(submissionResources.message(SEPARATION), view.separation.getLabel());
    for (GelSeparation separation : GelSeparation.values()) {
      assertEquals(separation.getLabel(locale),
          view.separation.getItemLabelGenerator().apply(separation));
    }
    assertEquals(submissionResources.message(THICKNESS), view.thickness.getLabel());
    for (GelThickness thickness : GelThickness.values()) {
      assertEquals(thickness.getLabel(locale),
          view.thickness.getItemLabelGenerator().apply(thickness));
    }
    assertEquals(submissionResources.message(COLORATION), view.coloration.getLabel());
    for (GelColoration coloration : GelColoration.values()) {
      assertEquals(coloration.getLabel(locale),
          view.coloration.getItemLabelGenerator().apply(coloration));
    }
    assertEquals(submissionResources.message(OTHER_COLORATION), view.otherColoration.getLabel());
    assertEquals(submissionResources.message(DEVELOPMENT_TIME), view.developmentTime.getLabel());
    assertEquals(resources.message(DEVELOPMENT_TIME_PLACEHOLDER),
        view.developmentTime.getPlaceholder());
    assertEquals(submissionResources.message(DECOLORATION), view.destained.getLabel());
    assertEquals(submissionResources.message(WEIGHT_MARKER_QUANTITY),
        view.weightMarkerQuantity.getLabel());
    assertEquals(resources.message(WEIGHT_MARKER_QUANTITY_PLACEHOLDER),
        view.weightMarkerQuantity.getPlaceholder());
    assertEquals(submissionResources.message(PROTEIN_QUANTITY), view.proteinQuantity.getLabel());
    assertEquals(resources.message(PROTEIN_QUANTITY_PLACEHOLDER),
        view.proteinQuantity.getPlaceholder());
    assertEquals(submissionResources.message(DIGESTION), view.digestion.getLabel());
    for (ProteolyticDigestion digestion : ProteolyticDigestion.values()) {
      assertEquals(digestion.getLabel(locale),
          view.digestion.getItemLabelGenerator().apply(digestion));
    }
    assertEquals(submissionResources.message(USED_DIGESTION), view.usedDigestion.getLabel());
    assertEquals(submissionResources.message(OTHER_DIGESTION), view.otherDigestion.getLabel());
    assertEquals(submissionResources.message(PROTEIN_CONTENT), view.proteinContent.getLabel());
    for (ProteinContent content : ProteinContent.values()) {
      assertEquals(content.getLabel(locale),
          view.proteinContent.getItemRenderer().createComponent(content).getElement().getText());
    }
    assertEquals(submissionResources.message(INJECTION_TYPE), view.injection.getLabel());
    for (InjectionType injection : InjectionType.values()) {
      assertEquals(injection.getLabel(locale),
          view.injection.getItemRenderer().createComponent(injection).getElement().getText());
    }
    assertEquals(submissionResources.message(SOURCE), view.source.getLabel());
    for (MassDetectionInstrumentSource source : MassDetectionInstrumentSource.availables()) {
      assertEquals(source.getLabel(locale),
          view.source.getItemRenderer().createComponent(source).getElement().getText());
    }
    assertEquals(submissionResources.message(INSTRUMENT), view.instrument.getLabel());
    for (MassDetectionInstrument instrument : MassDetectionInstrument.userChoices()) {
      assertEquals(instrument.getLabel(locale),
          view.instrument.getItemLabelGenerator().apply(instrument));
    }
    assertEquals(submissionResources.message(IDENTIFICATION), view.identification.getLabel());
    for (ProteinIdentification identification : ProteinIdentification.availables()) {
      assertEquals(identification.getLabel(locale), view.identification.getItemRenderer()
          .createComponent(identification).getElement().getText());
    }
    assertEquals(submissionResources.message(IDENTIFICATION_LINK),
        view.identificationLink.getLabel());
    assertEquals(submissionResources.message(QUANTIFICATION), view.quantification.getLabel());
    assertEquals(submissionResources.message(QUANTIFICATION_COMMENT),
        view.quantificationComment.getLabel());
    assertEquals(submissionResources.message(HIGH_RESOLUTION), view.highResolution.getLabel());
    for (Boolean value : Arrays.asList(false, true)) {
      assertEquals(submissionResources.message(property(HIGH_RESOLUTION, value)),
          view.highResolution.getItemRenderer().createComponent(value).getElement().getText());
    }
    assertEquals(submissionResources.message(COMMENT), view.comment.getLabel());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void localeChange() {
    view.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = FRENCH;
    final MessageResource resources = new MessageResource(SubmissionView.class, locale);
    final MessageResource submissionResources = new MessageResource(Submission.class, locale);
    final MessageResource sampleResources = new MessageResource(Sample.class, locale);
    final MessageResource submissionSampleResources =
        new MessageResource(SubmissionSample.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), view.header.getText());
    assertEquals(LC_MS_MS.getLabel(locale), view.lcmsms.getLabel());
    assertEquals(SMALL_MOLECULE.getLabel(locale), view.smallMolecule.getLabel());
    assertEquals(INTACT_PROTEIN.getLabel(locale), view.intactProtein.getLabel());
    assertEquals(submissionResources.message(GOAL), view.goal.getLabel());
    assertEquals(submissionResources.message(TAXONOMY), view.taxonomy.getLabel());
    assertEquals(submissionResources.message(PROTEIN), view.protein.getLabel());
    assertEquals(submissionSampleResources.message(MOLECULAR_WEIGHT),
        view.molecularWeight.getLabel());
    assertEquals(submissionResources.message(POST_TRANSLATION_MODIFICATION),
        view.postTranslationModification.getLabel());
    assertEquals(resources.message(SAMPLES_TYPE), view.sampleType.getLabel());
    for (SampleType type : SampleType.values()) {
      assertEquals(type.getLabel(locale),
          view.sampleType.getItemRenderer().createComponent(type).getElement().getText());
    }
    assertEquals(resources.message(SAMPLES_COUNT), view.samplesCount.getLabel());
    assertEquals(resources.message(SAMPLES_NAMES), view.samplesNames.getLabel());
    assertEquals(sampleResources.message(QUANTITY), view.quantity.getLabel());
    assertEquals(resources.message(QUANTITY_PLACEHOLDER), view.quantity.getPlaceholder());
    assertEquals(sampleResources.message(VOLUME), view.volume.getLabel());
    assertEquals(resources.message(VOLUME_PLACEHOLDER), view.volume.getPlaceholder());
    assertEquals(sampleResources.message(SampleProperties.NAME), view.sampleName.getLabel());
    assertEquals(submissionResources.message(FORMULA), view.formula.getLabel());
    assertEquals(submissionResources.message(MONOISOTOPIC_MASS), view.monoisotopicMass.getLabel());
    assertEquals(submissionResources.message(AVERAGE_MASS), view.averageMass.getLabel());
    assertEquals(submissionResources.message(TOXICITY), view.toxicity.getLabel());
    assertEquals(submissionResources.message(LIGHT_SENSITIVE), view.lightSensitive.getLabel());
    assertEquals(submissionResources.message(STORAGE_TEMPERATURE),
        view.storageTemperature.getLabel());
    assertEquals(submissionResources.message(SEPARATION), view.separation.getLabel());
    for (GelSeparation separation : GelSeparation.values()) {
      assertEquals(separation.getLabel(locale),
          view.separation.getItemLabelGenerator().apply(separation));
    }
    assertEquals(submissionResources.message(THICKNESS), view.thickness.getLabel());
    for (GelThickness thickness : GelThickness.values()) {
      assertEquals(thickness.getLabel(locale),
          view.thickness.getItemLabelGenerator().apply(thickness));
    }
    assertEquals(submissionResources.message(COLORATION), view.coloration.getLabel());
    for (GelColoration coloration : GelColoration.values()) {
      assertEquals(coloration.getLabel(locale),
          view.coloration.getItemLabelGenerator().apply(coloration));
    }
    assertEquals(submissionResources.message(OTHER_COLORATION), view.otherColoration.getLabel());
    assertEquals(submissionResources.message(DEVELOPMENT_TIME), view.developmentTime.getLabel());
    assertEquals(resources.message(DEVELOPMENT_TIME_PLACEHOLDER),
        view.developmentTime.getPlaceholder());
    assertEquals(submissionResources.message(DECOLORATION), view.destained.getLabel());
    assertEquals(submissionResources.message(WEIGHT_MARKER_QUANTITY),
        view.weightMarkerQuantity.getLabel());
    assertEquals(resources.message(WEIGHT_MARKER_QUANTITY_PLACEHOLDER),
        view.weightMarkerQuantity.getPlaceholder());
    assertEquals(submissionResources.message(PROTEIN_QUANTITY), view.proteinQuantity.getLabel());
    assertEquals(resources.message(PROTEIN_QUANTITY_PLACEHOLDER),
        view.proteinQuantity.getPlaceholder());
    assertEquals(submissionResources.message(DIGESTION), view.digestion.getLabel());
    for (ProteolyticDigestion digestion : ProteolyticDigestion.values()) {
      assertEquals(digestion.getLabel(locale),
          view.digestion.getItemLabelGenerator().apply(digestion));
    }
    assertEquals(submissionResources.message(USED_DIGESTION), view.usedDigestion.getLabel());
    assertEquals(submissionResources.message(OTHER_DIGESTION), view.otherDigestion.getLabel());
    assertEquals(submissionResources.message(PROTEIN_CONTENT), view.proteinContent.getLabel());
    for (ProteinContent content : ProteinContent.values()) {
      assertEquals(content.getLabel(locale),
          view.proteinContent.getItemRenderer().createComponent(content).getElement().getText());
    }
    assertEquals(submissionResources.message(INJECTION_TYPE), view.injection.getLabel());
    for (InjectionType injection : InjectionType.values()) {
      assertEquals(injection.getLabel(locale),
          view.injection.getItemRenderer().createComponent(injection).getElement().getText());
    }
    assertEquals(submissionResources.message(SOURCE), view.source.getLabel());
    for (MassDetectionInstrumentSource source : MassDetectionInstrumentSource.availables()) {
      assertEquals(source.getLabel(locale),
          view.source.getItemRenderer().createComponent(source).getElement().getText());
    }
    assertEquals(submissionResources.message(INSTRUMENT), view.instrument.getLabel());
    for (MassDetectionInstrument instrument : MassDetectionInstrument.userChoices()) {
      assertEquals(instrument.getLabel(locale),
          view.instrument.getItemLabelGenerator().apply(instrument));
    }
    assertEquals(submissionResources.message(IDENTIFICATION), view.identification.getLabel());
    for (ProteinIdentification identification : ProteinIdentification.availables()) {
      assertEquals(identification.getLabel(locale), view.identification.getItemRenderer()
          .createComponent(identification).getElement().getText());
    }
    assertEquals(submissionResources.message(IDENTIFICATION_LINK),
        view.identificationLink.getLabel());
    assertEquals(submissionResources.message(QUANTIFICATION), view.quantification.getLabel());
    assertEquals(submissionResources.message(QUANTIFICATION_COMMENT),
        view.quantificationComment.getLabel());
    assertEquals(submissionResources.message(HIGH_RESOLUTION), view.highResolution.getLabel());
    for (Boolean value : Arrays.asList(false, true)) {
      assertEquals(submissionResources.message(property(HIGH_RESOLUTION, value)),
          view.highResolution.getItemRenderer().createComponent(value).getElement().getText());
    }
    assertEquals(submissionResources.message(COMMENT), view.comment.getLabel());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void services() {
    assertEquals(3, view.service.getComponentCount());
    assertEquals(view.lcmsms, view.service.getComponentAt(0));
    assertEquals(view.smallMolecule, view.service.getComponentAt(1));
    assertEquals(view.intactProtein, view.service.getComponentAt(2));
  }

  @Test
  public void getPageTitle() {
    assertEquals(resources.message(TITLE, webResources.message(APPLICATION_NAME)),
        view.getPageTitle());
  }

  @Test
  public void setParameter() {
    view.setParameter(beforeEvent, 12L);
    verify(presenter).setParameter(12L);
  }
}
