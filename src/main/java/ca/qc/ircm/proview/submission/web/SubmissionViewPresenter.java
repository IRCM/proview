package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;

import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.ArrayList;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submission view presenter.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionViewPresenter {
  private static final Logger logger = LoggerFactory.getLogger(SubmissionViewPresenter.class);
  private SubmissionView view;
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private SubmissionService service;

  @Autowired
  protected SubmissionViewPresenter(SubmissionService service) {
    this.service = service;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  void init(SubmissionView view) {
    this.view = view;
    setSubmission(null);
  }

  void localeChange(Locale locale) {
    final MessageResource webResources = new MessageResource(WebConstants.class, locale);
    binder.forField(view.comment).bind(COMMENT);
  }

  private void setSubmission(Submission submission) {
    if (submission == null) {
      submission = new Submission();
      submission.setService(Service.LC_MS_MS);
      submission.setStorageTemperature(StorageTemperature.MEDIUM);
      submission.setSeparation(GelSeparation.ONE_DIMENSION);
      submission.setThickness(GelThickness.ONE);
      submission.setDigestion(ProteolyticDigestion.TRYPSIN);
      submission.setProteinContent(ProteinContent.SMALL);
      submission.setInjectionType(InjectionType.LC_MS);
      submission.setSource(MassDetectionInstrumentSource.ESI);
      submission.setIdentification(ProteinIdentification.REFSEQ);
    }
    if (submission.getSamples() == null) {
      submission.setSamples(new ArrayList<>());
    }
    if (submission.getSamples() == null || submission.getSamples().isEmpty()) {
      SubmissionSample sample = new SubmissionSample();
      sample.setType(SampleType.SOLUTION);
      submission.getSamples().add(sample);
    }
    binder.setBean(submission);
    view.lcmsmsSubmissionForm.setSubmission(submission);
    view.smallMoleculeSubmissionForm.setSubmission(submission);
    view.intactProteinSubmissionForm.setSubmission(submission);
  }

  void setParameter(Long parameter) {
    if (parameter != null) {
      setSubmission(service.get(parameter));
    }
  }
}
