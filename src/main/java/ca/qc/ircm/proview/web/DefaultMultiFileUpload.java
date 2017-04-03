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
  private MultiFileUploadFileHandler fileHandler;

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
