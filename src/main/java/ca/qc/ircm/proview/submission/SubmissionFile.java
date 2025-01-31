package ca.qc.ircm.proview.submission;

import static jakarta.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

/**
 * Submission file.
 */
@Entity
@Table(name = SubmissionFile.TABLE_NAME)
@GeneratePropertyNames
public class SubmissionFile implements Data, Named, Serializable {

  public static final String TABLE_NAME = "submissionfiles";
  @Serial
  private static final long serialVersionUID = 2146676462335553712L;
  /**
   * Structure database identifier.
   */
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private long id;
  /**
   * Filename as entered by user.
   */
  @Column(nullable = false)
  @Size(max = 255)
  private String filename;
  /**
   * Binary content of file.
   */
  @Column(nullable = false)
  private byte[] content;

  public SubmissionFile() {
  }

  public SubmissionFile(String filename) {
    this.filename = filename;
  }

  @Override
  public String getName() {
    return filename;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public byte[] getContent() {
    return content.clone();
  }

  public void setContent(byte[] content) {
    this.content = content.clone();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
