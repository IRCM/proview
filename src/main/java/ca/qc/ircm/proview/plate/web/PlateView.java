package ca.qc.ircm.proview.plate.web;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import ca.qc.ircm.proview.web.Menu;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;

import java.util.stream.IntStream;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * View for a plate.
 */
@SpringView(name = PlateView.VIEW_NAME)
@RolesAllowed("USER")
public class PlateView extends PlateViewDesign implements MessageResourcesView {
  public static final String VIEW_NAME = "plate";
  private static final long serialVersionUID = -7006664525905554582L;
  private Menu menu = new Menu();
  private PlateLayout plateLayout = new PlateLayout(12, 8);
  private PlateComponent plateComponent;
  @Inject
  private PlateService plateService;

  public PlateView() {
    menuLayout.addComponent(menu);
    plateLayoutContainer.addComponent(plateLayout);
    IntStream.range(0, 12).forEach(i -> IntStream.range(0, 8)
        .forEach(j -> plateLayout.addWellComponent(new Label("Sample name"), i, j)));
  }

  @Override
  public void attach() {
    Plate plate = plateService.get(1L);
    plateComponent = new PlateComponent(plate);
    plateComponentPanel.setCaption(plate.getName());
    plateComponentPanel.setContent(plateComponent);
  }
}
