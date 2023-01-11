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

import static ca.qc.ircm.proview.security.Permission.WRITE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionView.FILES_IOEXCEPTION;
import static ca.qc.ircm.proview.submission.web.SubmissionView.FILES_OVER_MAXIMUM;
import static ca.qc.ircm.proview.submission.web.SubmissionView.MAXIMUM_FILES_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionView.REMOVE;
import static ca.qc.ircm.proview.submission.web.SubmissionView.SAVED;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFile;
import ca.qc.ircm.proview.submission.SubmissionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.util.FileCopyUtils;

/**
 * Submission view presenter.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionViewPresenter {
  private static final Logger logger = LoggerFactory.getLogger(SubmissionViewPresenter.class);
  private SubmissionView view;
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private ListDataProvider<SubmissionFile> filesDataProvider =
      DataProvider.ofCollection(new ArrayList<>());
  private SubmissionService service;
  private AuthenticatedUser authenticatedUser;

  @Autowired
  protected SubmissionViewPresenter(SubmissionService service,
      AuthenticatedUser authenticatedUser) {
    this.service = service;
    this.authenticatedUser = authenticatedUser;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  void init(SubmissionView view) {
    this.view = view;
    view.files.setDataProvider(filesDataProvider);
    setSubmission(null);
  }

  void localeChange(Locale locale) {
    binder.forField(view.comment).withNullRepresentation("").bind(COMMENT);
    setReadOnly();
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

  void addFile(String filename, InputStream input, Locale locale) {
    logger.debug("received file {}", filename);
    SubmissionFile file = new SubmissionFile();
    file.setFilename(filename);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      FileCopyUtils.copy(input, output);
    } catch (IOException e) {
      AppResources resources = new AppResources(SubmissionView.class, locale);
      view.showNotification(resources.message(FILES_IOEXCEPTION, filename));
      return;
    }
    file.setContent(output.toByteArray());
    if (filesDataProvider.getItems().size() >= MAXIMUM_FILES_COUNT) {
      AppResources resources = new AppResources(SubmissionView.class, locale);
      view.showNotification(resources.message(FILES_OVER_MAXIMUM, MAXIMUM_FILES_COUNT));
      return;
    }
    filesDataProvider.getItems().add(file);
    filesDataProvider.refreshAll();
  }

  void removeFile(SubmissionFile file) {
    filesDataProvider.getItems().remove(file);
    filesDataProvider.refreshAll();
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
      submission.setFiles(new ArrayList<>(filesDataProvider.getItems()));
      if (submission.getId() == null) {
        logger.debug("save new submission {}", submission);
        service.insert(submission);
      } else {
        logger.debug("save submission {}", submission);
        service.update(submission, null);
      }
      final AppResources resources = new AppResources(SubmissionView.class, locale);
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
    if (submission.getFiles() == null) {
      submission.setFiles(new ArrayList<>());
    }
    binder.setBean(submission);
    filesDataProvider.getItems().clear();
    filesDataProvider.getItems().addAll(submission.getFiles());
    filesDataProvider.refreshAll();
    view.lcmsmsSubmissionForm.setSubmission(submission);
    view.smallMoleculeSubmissionForm.setSubmission(submission);
    view.intactProteinSubmissionForm.setSubmission(submission);
    setReadOnly();
  }

  private void setReadOnly() {
    boolean readOnly = !authenticatedUser.hasPermission(binder.getBean(), WRITE);
    binder.setReadOnly(readOnly);
    view.upload.setVisible(!readOnly);
    view.files.getColumnByKey(REMOVE).setVisible(!readOnly);
    view.save.setEnabled(!readOnly);
  }

  void setParameter(Long parameter) {
    if (parameter != null) {
      setSubmission(service.get(parameter).orElse(null));
    }
  }
}
