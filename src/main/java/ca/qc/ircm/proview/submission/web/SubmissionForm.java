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

import ca.qc.ircm.proview.plate.web.PlateComponent;
import ca.qc.ircm.proview.sample.web.ContaminantsForm;
import ca.qc.ircm.proview.sample.web.StandardsForm;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.DefaultMultiFileUpload;
import ca.qc.ircm.proview.web.MultiFileUploadFileHandler;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.ui.CustomComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.vaadin.easyuploads.MultiFileUpload;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Submission form.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionForm extends CustomComponent implements BaseComponent {
  private static final long serialVersionUID = 7586918222688019429L;
  protected SubmissionFormDesign design = new SubmissionFormDesign();
  protected DefaultMultiFileUpload filesUploader;
  @Inject
  protected PlateComponent plateComponent;
  @Inject
  protected StandardsForm standardsForm;
  @Inject
  protected ContaminantsForm contaminantsForm;
  @Inject
  protected GelForm gelForm;
  @Inject
  private transient SubmissionFormPresenter presenter;

  protected SubmissionForm() {
  }

  protected SubmissionForm(SubmissionFormPresenter presenter, PlateComponent plateComponent,
      StandardsForm standardsForm, ContaminantsForm contaminantsForm, GelForm gelForm) {
    this.presenter = presenter;
    this.plateComponent = plateComponent;
    this.standardsForm = standardsForm;
    this.contaminantsForm = contaminantsForm;
    this.gelForm = gelForm;
  }

  /**
   * Initializes form.
   */
  @PostConstruct
  public void init() {
    setSizeFull();
    setCompositionRoot(design);
    design.samplesPlateContainer.addComponent(plateComponent);
    design.standardsPanel.setContent(standardsForm);
    design.contaminantsContainer.addComponent(contaminantsForm);
    design.gelPanel.setContent(gelForm);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  /**
   * Creates uploader for additional files.
   *
   * @param fileHandler
   *          handles uploaded files
   * @return uploader for additional files
   */
  public MultiFileUpload createFilesUploader(MultiFileUploadFileHandler fileHandler) {
    filesUploader = new DefaultMultiFileUpload();
    filesUploader.setFileHandler(fileHandler);
    design.filesUploaderLayout.addComponent(filesUploader);
    return filesUploader;
  }

  public Submission getValue() {
    return presenter.getValue();
  }

  public void setValue(Submission submission) {
    presenter.setValue(submission);
  }

  @Override
  public boolean isReadOnly() {
    return presenter.isReadOnly();
  }

  @Override
  public void setReadOnly(boolean readOnly) {
    presenter.setReadOnly(readOnly);
  }
}
