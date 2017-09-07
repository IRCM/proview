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

package ca.qc.ircm.proview.web;

import ca.qc.ircm.proview.web.component.BaseComponent;
import ca.qc.ircm.utils.MessageResource;
import org.vaadin.easyuploads.MultiFileUpload;

import java.io.File;

/**
 * Extension of MultiFileUpload to support localized messages.
 */
public class DefaultMultiFileUpload extends MultiFileUpload implements BaseComponent {
  private static final long serialVersionUID = -3962247385782398547L;
  private transient MultiFileUploadFileHandler fileHandler;

  @Override
  protected void handleFile(File file, String fileName, String mimeType, long length) {
    if (fileHandler != null) {
      fileHandler.handleFile(file, fileName, mimeType, length);
    }
  }

  @Override
  public void onFileCountExceeded() {
    super.onFileCountExceeded();
  }

  @Override
  public void onFileTypeMismatch() {
    super.onFileTypeMismatch();
  }

  @Override
  public void onMaxSizeExceeded(long contentLength) {
    final MessageResource generalResources = getGeneralResources();
    showError(generalResources.message(WebConstants.OVER_MAXIMUM_SIZE, contentLength));
  }

  public MultiFileUploadFileHandler getFileHandler() {
    return fileHandler;
  }

  public void setFileHandler(MultiFileUploadFileHandler fileHandler) {
    this.fileHandler = fileHandler;
  }
}
