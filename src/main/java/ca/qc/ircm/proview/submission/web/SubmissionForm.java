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

import ca.qc.ircm.proview.plate.PlateType;
import ca.qc.ircm.proview.plate.web.platelayout.PlateLayout;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStartedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

/**
 * Submission form.
 */
public class SubmissionForm extends SubmissionFormDesign implements BaseComponent {
  private static final long serialVersionUID = 7586918222688019429L;
  private transient SubmissionFormPresenter presenter;
  protected UploadStateWindow uploadStateWindow;
  protected MultiFileUpload structureUploader;
  protected MultiFileUpload gelImagesUploader;
  protected MultiFileUpload filesUploader;
  protected PlateLayout samplesPlateLayout;

  public void setPresenter(SubmissionFormPresenter presenter) {
    this.presenter = presenter;
  }

  /**
   * Creates SubmissionForm.
   */
  public SubmissionForm() {
    uploadStateWindow = new UploadStateWindow();
    int columns = PlateType.SUBMISSION.getColumnCount();
    int rows = PlateType.SUBMISSION.getRowCount();
    samplesPlateLayout = new PlateLayout(columns, rows);
    samplesPlateContainer.addComponent(samplesPlateLayout);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  /**
   * Creates uploader for molecule structure.
   *
   * @param startedHandler
   *          handler for upload started
   * @param finishedHandler
   *          handler for upload finished
   * @param multi
   *          true to allow multiple file upload, false otherwise
   * @return uploader for molecule structure
   */
  public MultiFileUpload createStructureUploader(UploadStartedHandler startedHandler,
      UploadFinishedHandler finishedHandler, boolean multi) {
    structureUploader =
        new MultiFileUpload(startedHandler, finishedHandler, uploadStateWindow, multi);
    structureUploaderLayout.addComponent(structureUploader);
    return structureUploader;
  }

  /**
   * Creates uploader for gel images.
   *
   * @param startedHandler
   *          handler for upload started
   * @param finishedHandler
   *          handler for upload finished
   * @param multi
   *          true to allow multiple file upload, false otherwise
   * @return uploader for gel images
   */
  public MultiFileUpload createGelImagesUploader(UploadStartedHandler startedHandler,
      UploadFinishedHandler finishedHandler, boolean multi) {
    gelImagesUploader =
        new MultiFileUpload(startedHandler, finishedHandler, uploadStateWindow, multi);
    gelImagesUploaderLayout.addComponent(gelImagesUploader);
    return gelImagesUploader;
  }

  /**
   * Creates uploader for additional files.
   *
   * @param startedHandler
   *          handler for upload started
   * @param finishedHandler
   *          handler for upload finished
   * @param multi
   *          true to allow multiple file upload, false otherwise
   * @return uploader for additional files
   */
  public MultiFileUpload createFilesUploader(UploadStartedHandler startedHandler,
      UploadFinishedHandler finishedHandler, boolean multi) {
    filesUploader = new MultiFileUpload(startedHandler, finishedHandler, uploadStateWindow, multi);
    filesUploaderLayout.addComponent(filesUploader);
    return filesUploader;
  }
}
