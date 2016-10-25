package ca.qc.ircm.proview.plate.web;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateSpot;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Label;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Plate component that allows selection and drag and drop.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlateComponentPresenter {
  public static final String SELECTED_STYLE = "selected";
  private ObjectProperty<Boolean> multiSelectProperty = new ObjectProperty<>(false);
  private PlateComponent view;
  private ObjectProperty<Plate> plateProperty = new ObjectProperty<>(null, Plate.class);
  private Set<PlateSpot> selectedSpots = new HashSet<>();

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(PlateComponent view) {
    this.view = view;
    if (plateProperty.getValue() == null) {
      throw new NullPointerException();
    }
    view.plateLayout.setColumns(plateProperty.getValue().getColumnCount());
    view.plateLayout.setRows(plateProperty.getValue().getRowCount());
    setWellsContent();
    addListeners();
  }

  private IntStream columnsStream() {
    return IntStream.range(0, plateProperty.getValue().getColumnCount());
  }

  private IntStream rowsStream() {
    return IntStream.range(0, plateProperty.getValue().getRowCount());
  }

  private void setWellsContent() {
    plateProperty.getValue().getSpots().forEach(spot -> {
      Label sampleName = new Label(spot.getSample() != null ? spot.getSample().getName() : null);
      view.plateLayout.addWellComponent(sampleName, spot.getColumn(), spot.getRow());
    });
  }

  private void addListeners() {
    plateProperty.getValue().getSpots().forEach(spot -> view.plateLayout
        .addWellClickListener(e -> toggleWell(spot), spot.getColumn(), spot.getRow()));
    columnsStream().forEach(
        column -> view.plateLayout.addColumnHeaderClickListener(e -> toggleColumn(column), column));
    rowsStream()
        .forEach(row -> view.plateLayout.addRowHeaderClickListener(e -> toggleRow(row), row));
  }

  private void toggleColumn(int column) {
    if (multiSelectProperty.getValue()) {
      List<PlateSpot> spots = plateProperty.getValue().column(column);
      if (!selectedSpots.containsAll(spots)) {
        selectColumn(column);
      } else {
        deselectColumn(column);
      }
    }
  }

  private List<PlateSpot> plateRow(int row) {
    return plateProperty.getValue().getSpots().stream().filter(spot -> spot.getRow() == row)
        .collect(Collectors.toList());
  }

  private void toggleRow(int row) {
    if (multiSelectProperty.getValue()) {
      List<PlateSpot> spots = plateRow(row);
      if (!selectedSpots.containsAll(spots)) {
        selectRow(row);
      } else {
        deselectRow(row);
      }
    }
  }

  private void toggleWell(PlateSpot spot) {
    if (!selectedSpots.contains(spot)) {
      selectWell(spot);
    } else {
      deselectWell(spot);
    }
  }

  /**
   * Select a well.
   *
   * @param spot
   *          spot associated with well
   */
  public void selectWell(PlateSpot spot) {
    if (!multiSelectProperty.getValue()) {
      deselectAllWells();
    }
    selectedSpots.add(spot);
    view.plateLayout.addWellStyleName(SELECTED_STYLE, spot.getColumn(), spot.getRow());
  }

  /**
   * Deselect a well.
   *
   * @param spot
   *          spot associated with well
   */
  public void deselectWell(PlateSpot spot) {
    selectedSpots.remove(spot);
    view.plateLayout.removeWellStyleName(SELECTED_STYLE, spot.getColumn(), spot.getRow());
  }

  /**
   * Deselect all wells.
   */
  public void deselectAllWells() {
    new ArrayList<>(selectedSpots).forEach(spot -> deselectWell(spot));
  }

  /**
   * Select all wells in column.
   * <p>
   * <strong>Does nothing unless multi-select is enabled</strong>
   * </p>
   *
   * @param column
   *          column
   */
  public void selectColumn(int column) {
    if (multiSelectProperty.getValue()) {
      List<PlateSpot> spots = plateProperty.getValue().column(column);
      spots.forEach(spot -> selectWell(spot));
    }
  }

  /**
   * Deselect all wells in column.
   * <p>
   * <strong>Does nothing unless multi-select is enabled</strong>
   * </p>
   *
   * @param column
   *          column
   */
  public void deselectColumn(int column) {
    if (multiSelectProperty.getValue()) {
      List<PlateSpot> spots = plateProperty.getValue().column(column);
      spots.forEach(spot -> deselectWell(spot));
    }
  }

  /**
   * Select all wells in row.
   * <p>
   * <strong>Does nothing unless multi-select is enabled</strong>
   * </p>
   *
   * @param row
   *          row
   */
  public void selectRow(int row) {
    if (multiSelectProperty.getValue()) {
      List<PlateSpot> spots = plateRow(row);
      spots.forEach(spot -> selectWell(spot));
    }
  }

  /**
   * Deselect all wells in row.
   * <p>
   * <strong>Does nothing unless multi-select is enabled</strong>
   * </p>
   *
   * @param row
   *          row
   */
  public void deselectRow(int row) {
    if (multiSelectProperty.getValue()) {
      List<PlateSpot> spots = plateRow(row);
      spots.forEach(spot -> deselectWell(spot));
    }
  }

  public boolean isMultiSelect() {
    return multiSelectProperty.getValue();
  }

  public void setMultiSelect(boolean multiSelect) {
    this.multiSelectProperty.setValue(multiSelect);
  }

  public Collection<PlateSpot> getSelectedSpots() {
    return new ArrayList<>(selectedSpots);
  }

  public void setSelectedSpots(Collection<PlateSpot> selectedSpots) {
    new ArrayList<>(this.selectedSpots).forEach(spot -> deselectWell(spot));
    selectedSpots.forEach(spot -> selectWell(spot));
  }

  public Plate getPlate() {
    return plateProperty.getValue();
  }

  /**
   * Sets plate, cannot be null.
   *
   * @param plate
   *          plate, cannot be null
   */
  public void setPlate(Plate plate) {
    if (plate == null) {
      throw new NullPointerException();
    }
    this.plateProperty.setValue(plate);
  }
}
