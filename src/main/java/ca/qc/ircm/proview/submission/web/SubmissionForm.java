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
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.DefaultMultiFileUpload;
import ca.qc.ircm.proview.web.MultiFileUploadFileHandler;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Upload;
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
  protected Upload structureUploader;
  protected DefaultMultiFileUpload gelImagesUploader;
  protected DefaultMultiFileUpload filesUploader;
  @Inject
  protected PlateComponent plateComponent;
  @Inject
  private transient SubmissionFormPresenter presenter;

  protected SubmissionForm() {
  }

  protected SubmissionForm(SubmissionFormPresenter presenter, PlateComponent plateComponent) {
    this.presenter = presenter;
    this.plateComponent = plateComponent;
  }

  /**
   * Initializes form.
   */
  @PostConstruct
  public void init() {
    setSizeFull();
    setCompositionRoot(design);
    design.samplesPlateContainer.addComponent(plateComponent);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  /**
   * Creates uploader for molecule structure.
   *
   * @return uploader for molecule structure
   */
  public Upload createStructureUploader() {
    structureUploader = new Upload();
    design.structureUploaderLayout.addComponent(structureUploader);
    return structureUploader;
  }

  /**
   * Creates uploader for gel images.
   *
   * @param fileHandler
   *          handles uploaded files
   * @return uploader for gel images
   */
  public MultiFileUpload createGelImagesUploader(MultiFileUploadFileHandler fileHandler) {
    gelImagesUploader = new DefaultMultiFileUpload();
    gelImagesUploader.setFileHandler(fileHandler);
    design.gelImagesUploaderLayout.addComponent(gelImagesUploader);
    return gelImagesUploader;
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
