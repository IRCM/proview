package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionView.SAVED;

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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;
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
    binder.forField(view.comment).withNullRepresentation("").bind(COMMENT);
  }

  Service service() {
    Tab tab = view.service.getSelectedTab();
    if (tab == view.smallMolecule) {
      return Service.SMALL_MOLECULE;
    } else if (tab == view.intactProtein) {
      return Service.INTACT_PROTEIN;
    }
    return Service.LC_MS_MS;
  }

  boolean valid() {
    boolean valid = true;
    Service service = service();
    switch (service) {
      case LC_MS_MS:
        valid = view.lcmsmsSubmissionForm.isValid() && valid;
        break;
      case SMALL_MOLECULE:
        valid = view.smallMoleculeSubmissionForm.isValid() && valid;
        break;
      case INTACT_PROTEIN:
        valid = view.intactProteinSubmissionForm.isValid() && valid;
        break;
      default:
        valid = false;
        break;
    }
    valid = binder.isValid() && valid;
    return valid;
  }

  void save(Locale locale) {
    if (valid()) {
      Submission submission = binder.getBean();
      submission.setService(service());
      if (submission.getId() == null) {
        logger.debug("save new submission {}", submission);
        service.insert(submission);
      } else {
        logger.debug("save submission {}", submission);
        service.update(submission, null);
      }
      final MessageResource resources = new MessageResource(SubmissionView.class, locale);
      view.showNotification(resources.message(SAVED, submission.getExperiment()));
      UI.getCurrent().navigate(SubmissionsView.class);
    }
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
