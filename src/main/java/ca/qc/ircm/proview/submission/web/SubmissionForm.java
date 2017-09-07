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

import ca.qc.ircm.proview.web.DefaultMultiFileUpload;
import ca.qc.ircm.proview.web.MultiFileUploadFileHandler;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.addon.spreadsheet.Spreadsheet;
import com.vaadin.ui.Upload;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.easyuploads.MultiFileUpload;

import java.io.IOException;

/**
 * Submission form.
 */
public class SubmissionForm extends SubmissionFormDesign implements BaseComponent {
  private static final long serialVersionUID = 7586918222688019429L;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionForm.class);
  private transient SubmissionFormPresenter presenter;
  protected Upload structureUploader;
  protected DefaultMultiFileUpload gelImagesUploader;
  protected DefaultMultiFileUpload filesUploader;
  protected Spreadsheet samplesSpreadsheet;

  public void setPresenter(SubmissionFormPresenter presenter) {
    this.presenter = presenter;
  }

  /**
   * Creates SubmissionForm.
   */
  public SubmissionForm() {
    try {
      samplesSpreadsheet =
          new Spreadsheet(new XSSFWorkbook(getClass().getResourceAsStream("/Plate-Template.xlsx")));
    } catch (IOException e) {
      logger.error("Could not load plate-template");
      samplesSpreadsheet = new Spreadsheet();
    }
    samplesSpreadsheet.setWidth("1024px");
    samplesSpreadsheet.setHeight("250px");
    samplesPlateContainer.addComponent(samplesSpreadsheet);
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
    structureUploaderLayout.addComponent(structureUploader);
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
    gelImagesUploaderLayout.addComponent(gelImagesUploader);
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
    filesUploaderLayout.addComponent(filesUploader);
    return filesUploader;
  }
}
