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
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submission form for {@link Service#SMALL_MOLECULE}.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SmallMoleculeSubmissionForm extends FormLayout implements LocaleChangeObserver {
  public static final String ID = "small-molecule-submission-form";
  public static final String SAMPLE = "sample";
  public static final String SAMPLE_TYPE = SAMPLE + "Type";
  public static final String SAMPLE_NAME = SAMPLE + "Name";
  private static final long serialVersionUID = 7704703308278059432L;
  protected RadioButtonGroup<SampleType> sampleType = new RadioButtonGroup<>();
  protected TextField sampleName = new TextField();
  protected TextField solvent = new TextField();
  protected TextField formula = new TextField();
  protected TextField monoisotopicMass = new TextField();
  protected TextField averageMass = new TextField();
  protected TextField toxicity = new TextField();
  protected Checkbox lightSensitive = new Checkbox();
  protected RadioButtonGroup<StorageTemperature> storageTemperature = new RadioButtonGroup<>();
  protected RadioButtonGroup<Boolean> highResolution = new RadioButtonGroup<>();
  protected SolventsField solvents = new SolventsField();
  protected TextField otherSolvent = new TextField();
  private transient SmallMoleculeSubmissionFormPresenter presenter;

  @Autowired
  protected SmallMoleculeSubmissionForm(SmallMoleculeSubmissionFormPresenter presenter) {
    this.presenter = presenter;
  }

  public static String id(String baseId) {
    return styleName(ID, baseId);
  }

  @PostConstruct
  void init() {
    setId(ID);
    setMaxWidth("80em");
    setResponsiveSteps(new ResponsiveStep("15em", 1), new ResponsiveStep("15em", 2),
        new ResponsiveStep("15em", 3));
    add(new FormLayout(sampleType, sampleName, solvent, formula),
        new FormLayout(monoisotopicMass, averageMass, toxicity, lightSensitive, storageTemperature),
        new FormLayout(highResolution, solvents, otherSolvent));
    sampleType.setId(id(SAMPLE_TYPE));
    sampleType.setItems(DRY, SOLUTION);
    sampleType.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    sampleType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    sampleName.setId(id(SAMPLE_NAME));
    solvent.setId(id(SOLUTION_SOLVENT));
    formula.setId(id(FORMULA));
    monoisotopicMass.setId(id(MONOISOTOPIC_MASS));
    averageMass.setId(id(AVERAGE_MASS));
    toxicity.setId(id(TOXICITY));
    lightSensitive.setId(id(LIGHT_SENSITIVE));
    storageTemperature.setId(id(STORAGE_TEMPERATURE));
    storageTemperature.setItems(StorageTemperature.values());
    storageTemperature.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    storageTemperature.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    highResolution.setId(id(HIGH_RESOLUTION));
    highResolution.setItems(false, true);
    highResolution
        .setRenderer(new TextRenderer<>(value -> new AppResources(Submission.class, getLocale())
            .message(property(HIGH_RESOLUTION, value))));
    highResolution.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    otherSolvent.setId(id(OTHER_SOLVENT));
    presenter.init(this);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final AppResources resources = new AppResources(SmallMoleculeSubmissionForm.class, getLocale());
    final AppResources submissionResources = new AppResources(Submission.class, getLocale());
    sampleType.setLabel(resources.message(SAMPLE_TYPE));
    sampleName.setLabel(resources.message(SAMPLE_NAME));
    solvent.setLabel(submissionResources.message(SOLUTION_SOLVENT));
    formula.setLabel(submissionResources.message(FORMULA));
    monoisotopicMass.setLabel(submissionResources.message(MONOISOTOPIC_MASS));
    averageMass.setLabel(submissionResources.message(AVERAGE_MASS));
    toxicity.setLabel(submissionResources.message(TOXICITY));
    lightSensitive.setLabel(submissionResources.message(LIGHT_SENSITIVE));
    storageTemperature.setLabel(submissionResources.message(STORAGE_TEMPERATURE));
    highResolution.setLabel(submissionResources.message(HIGH_RESOLUTION));
    solvents.setLabel(submissionResources.message(SOLVENTS));
    otherSolvent.setLabel(submissionResources.message(OTHER_SOLVENT));
    presenter.localeChange(getLocale());
  }

  public boolean isValid() {
    return presenter.isValid();
  }

  public Submission getSubmission() {
    return presenter.getSubmission();
  }

  public void setSubmission(Submission submission) {
    presenter.setSubmission(submission);
  }
}
