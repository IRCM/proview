package ca.qc.ircm.proview.msanalysis;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import ca.qc.ircm.proview.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Mascot date file.
 */
@Entity
@Table(name = "mascotfile")
public class MascotFile implements Data, Serializable {

  private static final long serialVersionUID = -1810264159489460122L;

  /**
   * Current start date for search.
   */
  private static final String LOCATION_PATTERN = ".*(data/\\d+/.*\\.dat)";

  /**
   * Database identifier.
   */
  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  /**
   * Mascot server where file is located.
   */
  @Column(name = "server", nullable = false)
  @Enumerated(STRING)
  private MascotServer server;
  /**
   * Name of mascot file.
   */
  @Column(name = "name", nullable = false)
  private String name;
  /**
   * Seach date.
   */
  @Column(name = "searchDate", nullable = false)
  private Instant searchDate;
  /**
   * Location of mascot file on disk.
   */
  @Column(name = "location", nullable = false)
  @Size(max = 255)
  private String location;
  /**
   * Location of RAW file.
   */
  @Column(name = "rawFile", nullable = false)
  @Size(max = 255)
  private String rawFile;
  /**
   * Comments of mascot file.
   */
  @Column(name = "comment")
  @Size(max = 255)
  private String comment;
  /**
   * Insertion time.
   */
  @Column(name = "insertTime", updatable = false, nullable = false)
  private Instant insertTime;

  public MascotFile() {
  }

  public MascotFile(Long id) {
    this.id = id;
  }

  public MascotFile(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Use this location to view results on mascot server.
   *
   * @return Location to view results on mascot server.
   */
  public String getViewLocation() {
    String location = this.location.replaceAll("\\\\", "/");
    Pattern pattern = Pattern.compile(LOCATION_PATTERN);
    Matcher matcher = pattern.matcher(location);
    if (matcher.matches()) {
      return matcher.group(1);
    } else {
      return location;
    }
  }

  @Override
  public String toString() {
    return "MascotFile [id=" + id + ", name=" + name + ", searchDate=" + searchDate + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((searchDate == null) ? 0 : searchDate.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    MascotFile other = (MascotFile) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (searchDate == null) {
      if (other.searchDate != null) {
        return false;
      }
    } else if (!searchDate.equals(other.searchDate)) {
      return false;
    }
    return true;
  }

  public String getName() {
    return name;
  }

  public Instant getSearchDate() {
    return searchDate;
  }

  public void setSearchDate(Instant searchDate) {
    this.searchDate = searchDate;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getRawFile() {
    return rawFile;
  }

  public void setRawFile(String rawFile) {
    this.rawFile = rawFile;
  }

  public Instant getInsertTime() {
    return insertTime;
  }

  public void setInsertTime(Instant insertTime) {
    this.insertTime = insertTime;
  }

  public MascotServer getServer() {
    return server;
  }

  public void setServer(MascotServer server) {
    this.server = server;
  }
}
