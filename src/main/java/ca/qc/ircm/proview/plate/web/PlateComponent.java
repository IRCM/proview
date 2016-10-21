package ca.qc.ircm.proview.plate.web;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateSpot;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Label;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Plate component that allows selection and drag and drop.
 */
public class PlateComponent extends CustomComponent {
  public static final String SELECTED_STYLE = "selected";
  public static final String DATA_FLAVOR = "plate-wells";
  private static final long serialVersionUID = -5886354033312877270L;
  private PlateLayout plateLayout;
  private Plate plate;
  private Set<PlateSpot> selectedSpots = new HashSet<>();

  /**
   * Creates PlateComponent for specified plate.
   *
   * @param plate
   *          plate
   */
  public PlateComponent(Plate plate) {
    this.plate = plate;
    plateLayout = new PlateLayout(plate.getColumnCount(), plate.getRowCount());
    setCompositionRoot(plateLayout);
  }

  @Override
  public void attach() {
    super.attach();
    setWellsContent();
    addListeners();
  }

  private IntStream columnsStream() {
    return IntStream.range(0, plate.getColumnCount());
  }

  private IntStream rowsStream() {
    return IntStream.range(0, plate.getRowCount());
  }

  @SuppressWarnings("serial")
  private void setWellsContent() {
    plate.getSpots().forEach(spot -> {
      Label sampleName = new Label(spot.getSample() != null ? spot.getSample().getName() : null);
      plateLayout.addWellComponent(sampleName, spot.getColumn(), spot.getRow());
      DragAndDropWrapper wellWrapper =
          plateLayout.getWellDragAndDropWrapper(spot.getColumn(), spot.getRow());
      wellWrapper.setDragStartMode(DragStartMode.COMPONENT);
      wellWrapper.setData(spot);
      wellWrapper.setDropHandler(new DropHandler() {
        @Override
        public void drop(DragAndDropEvent event) {
          /*
          System.out.println(event.getTransferable().getSourceComponent());
          DragAndDropWrapper source =
              (DragAndDropWrapper) event.getTransferable().getSourceComponent();
          System.out.println(source.getData());
          */
          PlateComponent plateComponent =
              getPlateComponentParent(event.getTransferable().getSourceComponent());
          dropSpots(plateComponent.selectedSpots, spot.getColumn(), spot.getRow());
        }

        @Override
        public AcceptCriterion getAcceptCriterion() {
          return new ServerSideCriterion() {
            @Override
            public boolean accept(DragAndDropEvent dragEvent) {
              PlateComponent plateComponent =
                  getPlateComponentParent(dragEvent.getTransferable().getSourceComponent());
              return plateComponent != null;
            }
          };
        }
      });
    });
  }

  private void addListeners() {
    plate.getSpots().forEach(spot -> plateLayout.addWellClickListener(e -> toggleWell(spot),
        spot.getColumn(), spot.getRow()));
    columnsStream().forEach(
        column -> plateLayout.addColumnHeaderClickListener(e -> toggleColumn(column), column));
    rowsStream().forEach(row -> plateLayout.addRowHeaderClickListener(e -> toggleRow(row), row));
  }

  private void toggleColumn(int column) {
    List<PlateSpot> spots = plate.column(column);
    if (!selectedSpots.containsAll(spots)) {
      spots.forEach(spot -> selectWell(spot));
    } else {
      spots.forEach(spot -> deselectWell(spot));
    }
  }

  private List<PlateSpot> plateRow(int row) {
    return plate.getSpots().stream().filter(spot -> spot.getRow() == row)
        .collect(Collectors.toList());
  }

  private void toggleRow(int row) {
    List<PlateSpot> spots = plateRow(row);
    if (!selectedSpots.containsAll(spots)) {
      spots.forEach(spot -> selectWell(spot));
    } else {
      spots.forEach(spot -> deselectWell(spot));
    }
  }

  private void toggleWell(PlateSpot spot) {
    if (!selectedSpots.contains(spot)) {
      selectWell(spot);
    } else {
      deselectWell(spot);
    }
  }

  private void selectWell(PlateSpot spot) {
    selectedSpots.add(spot);
    plateLayout.addWellStyleName(SELECTED_STYLE, spot.getColumn(), spot.getRow());
  }

  private void deselectWell(PlateSpot spot) {
    selectedSpots.remove(spot);
    plateLayout.removeWellStyleName(SELECTED_STYLE, spot.getColumn(), spot.getRow());
  }

  private PlateComponent getPlateComponentParent(Component component) {
    Component parent = component;
    while (parent != null && !(parent instanceof PlateComponent)) {
      parent = parent.getParent();
    }
    return (PlateComponent) parent;
  }

  private void dropSpots(Collection<PlateSpot> spots, int column, int row) {
  }
}
