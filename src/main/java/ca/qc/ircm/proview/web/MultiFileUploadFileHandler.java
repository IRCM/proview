package ca.qc.ircm.proview.web;

import java.io.File;

/**
 * File handler for easy uploads' MultiFileUpload.
 */
public interface MultiFileUploadFileHandler {
  public void handleFile(File file, String fileName, String mimeType, long length);
}
