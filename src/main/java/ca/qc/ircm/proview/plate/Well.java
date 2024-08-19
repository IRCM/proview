package ca.qc.ircm.proview.plate;

import static ca.qc.ircm.proview.SpotbugsJustifications.ENTITY_EI_EXPOSE_REP;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.text.MessageFormat;

/**
 * A plate well.
 */
@Entity
@DiscriminatorValue("WELL")
@GeneratePropertyNames
@SuppressFBWarnings(
    value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" },
    justification = ENTITY_EI_EXPOSE_REP)
public class Well extends SampleContainer implements Data, Named, Serializable {
  private static final long serialVersionUID = 212003765334493656L;

  /**
   * Plate where this well is located.
   */
  @ManyToOne
  @JoinColumn(nullable = false)
  private Plate plate;
  /**
   * Row where well is located on plate.
   */
  @Column(name = "wellrow", updatable = false, nullable = false)
  private int row;
  /**
   * Column where well is located on plate.
   */
  @Column(name = "wellcolumn", updatable = false, nullable = false)
  private int column;

  /**
   * Constructor to be used only by persistence layer.
   */
  Well() {
    this.row = 0;
    this.column = 0;
  }

  public Well(Long id) {
    super(id);
  }

  /**
   * Creates a new well.
   *
   * @param row
   *          row where well is located on plate
   * @param column
   *          column where well is located on plate
   */
  public Well(Integer row, Integer column) {
    this.row = row;
    this.column = column;
  }

  @Override
  public String toString() {
    return "Well [row=" + row + ", column=" + column + ", getId()=" + getId() + "]";
  }

  @Override
  public String getName() {
    return Plate.rowLabel(row) + "-" + Plate.columnLabel(column);
  }

  @Override
  public String getFullName() {
    return MessageFormat.format("{0} ({1}-{2})", plate.getName(), Plate.rowLabel(row),
        Plate.columnLabel(column));
  }

  @Override
  public SampleContainerType getType() {
    return SampleContainerType.WELL;
  }

  public Plate getPlate() {
    return plate;
  }

  public void setPlate(Plate plate) {
    this.plate = plate;
  }

  public int getColumn() {
    return column;
  }

  public int getRow() {
    return row;
  }
}
