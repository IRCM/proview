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

package ca.qc.ircm.proview.submission;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Submission file.
 */
@Entity
@Table(name = SubmissionFile.TABLE_NAME)
public class SubmissionFile implements Serializable {
  public static final String TABLE_NAME = "submissionfiles";
  private static final long serialVersionUID = 2146676462335553712L;
  /**
   * Structure database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Filename as entered by user.
   */
  @Column(name = "filename", nullable = false)
  @Size(max = 255)
  private String filename;
  /**
   * Binary content of file.
   */
  @Column(name = "content", nullable = false)
  private byte[] content;

  public SubmissionFile() {
  }

  public SubmissionFile(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public byte[] getContent() {
    return content != null ? content.clone() : null;
  }

  public void setContent(byte[] content) {
    this.content = content != null ? content.clone() : null;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
