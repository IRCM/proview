package ca.qc.ircm.proview.transfer.web;

import static ca.qc.ircm.proview.plate.QPlate.plate;
import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.tube.QTube.tube;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateFilterBuilder;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.plate.PlateSpotService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.themes.ValoTheme;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Sample transfer view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TransferViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String SOURCE = "source";
  public static final String SOURCE_TABS = "sourceTabs";
  public static final String SOURCE_TUBES = "sourceTubes";
  public static final String SOURCE_PLATES = "sourcePlates";
  public static final String SOURCE_PLATE_PANEL = "sourcePlatePanel";
  public static final String SOURCE_PLATE = "sourcePlate";
  public static final String DESTINATION = "destination";
  public static final String DESTINATION_TABS = "destinationTabs";
  public static final String DESTINATION_TUBES = "destinationTubes";
  public static final String DESTINATION_PLATES = "destinationPlates";
  public static final String DESTINATION_PLATES_TYPE = "destinationPlatesType";
  public static final String DESTINATION_PLATE_PANEL = "destinationPlatePanel";
  public static final String DESTINATION_PLATE = "destinationPlate";
  public static final String COMPONENTS = "components";
  public static final String NAME = sample.name.getMetadata().getName();
  public static final String TUBE = tube.getMetadata().getName();
  public static final String PLATE = plate.getMetadata().getName();
  public static final String PLATE_TYPE = plate.type.getMetadata().getName();
  public static final String PLATE_NAME = plate.name.getMetadata().getName();
  static final Object[] SOURCE_TUBE_COLUMNS = new Object[] { NAME, TUBE };
  static final Object[] DESTINATION_TUBE_COLUMNS = new Object[] { NAME, TUBE };
  private TransferView view;
  private ObjectProperty<List<Sample>> samplesProperty = new ObjectProperty<>(new ArrayList<>());
  private BeanFieldGroup<Plate> destinationPlateFieldGroup = new BeanFieldGroup<>(Plate.class);
  private BeanItemContainer<Sample> sourceTubesContainer = new BeanItemContainer<>(Sample.class);
  private GeneratedPropertyContainer sourceTubesGeneratedContainer =
      new GeneratedPropertyContainer(sourceTubesContainer);
  @Inject
  private TubeService tubeService;
  @Inject
  private PlateSpotService plateSpotService;
  @Inject
  private PlateService plateService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected TransferViewPresenter() {
  }

  protected TransferViewPresenter(TubeService tubeService, PlateSpotService plateSpotService,
      PlateService plateService, String applicationName) {
    this.tubeService = tubeService;
    this.plateSpotService = plateSpotService;
    this.plateService = plateService;
    this.applicationName = applicationName;
  }

  public void init(TransferView view) {
    this.view = view;
    prepareComponents();
    bindFields();
    addListeners();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message("title", applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.addStyleName(ValoTheme.LABEL_H1);
    view.headerLabel.setValue(resources.message(HEADER));
    view.sourceHeaderLabel.addStyleName(SOURCE);
    view.sourceHeaderLabel.addStyleName(ValoTheme.LABEL_H2);
    view.sourceHeaderLabel.setValue(resources.message(SOURCE));
    view.sourceTabs.addStyleName(SOURCE_TABS);
    view.sourceTabs.addStyleName(ValoTheme.TABSHEET_FRAMED);
    view.sourceTabs.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
    view.sourceTabs.getTab(view.sourceTubesGrid).setCaption(resources.message(SOURCE_TUBES));
    view.sourceTabs.getTab(view.sourcePlateLayout).setCaption(resources.message(SOURCE_PLATE));
    prepareSourceTubesGrid();
    view.sourcePlatesField.addStyleName(SOURCE_PLATES);
    view.sourcePlatesField.setCaption(resources.message(SOURCE_PLATES));
    view.sourcePlatePanel.addStyleName(SOURCE_PLATE_PANEL);
    view.sourcePlateForm.addStyleName(SOURCE_PLATE);
    view.sourcePlateFormPresenter.setMultiSelect(true);
    view.destinationHeaderLabel.addStyleName(DESTINATION);
    view.destinationHeaderLabel.addStyleName(ValoTheme.LABEL_H2);
    view.destinationHeaderLabel.setValue(resources.message(DESTINATION));
    view.destinationTabs.addStyleName(DESTINATION_TABS);
    view.destinationTabs.addStyleName(ValoTheme.TABSHEET_FRAMED);
    view.destinationTabs.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
    view.destinationTabs.getTab(view.destinationTubesGrid)
        .setCaption(resources.message(DESTINATION_TUBES));
    view.destinationTabs.getTab(view.destinationPlateLayout)
        .setCaption(resources.message(DESTINATION_PLATE));
    view.destinationTubesGrid.addStyleName(DESTINATION_TUBES);
    view.destinationPlatesTypeField.addStyleName(DESTINATION_PLATES_TYPE);
    view.destinationPlatesTypeField.setCaption(resources.message(DESTINATION_PLATES_TYPE));
    view.destinationPlatesField.addStyleName(DESTINATION_PLATES);
    view.destinationPlatesField.setCaption(resources.message(DESTINATION_PLATES));
    view.destinationPlatePanel.addStyleName(DESTINATION_PLATE_PANEL);
    view.destinationPlateForm.addStyleName(DESTINATION_PLATE);
    view.destinationPlateFormPresenter.setMultiSelect(true);
  }

  @SuppressWarnings("serial")
  private void prepareSourceTubesGrid() {
    MessageResource resources = view.getResources();
    sourceTubesGeneratedContainer.addGeneratedProperty(TUBE,
        new PropertyValueGenerator<ComboBox>() {
          @Override
          public ComboBox getValue(Item item, Object itemId, Object propertyId) {
            Sample sample = (Sample) itemId;
            ComboBox comboBox = prepareSourceTubeComboBox(sample);
            return comboBox;
          }

          @Override
          public Class<ComboBox> getType() {
            return ComboBox.class;
          }
        });
    view.sourceTubesGrid.addStyleName(SOURCE_TUBES);
    view.sourceTubesGrid.addStyleName(COMPONENTS);
    view.sourceTubesGrid.setContainerDataSource(sourceTubesGeneratedContainer);
    view.sourceTubesGrid.setColumns(SOURCE_TUBE_COLUMNS);
    view.sourceTubesGrid.getColumns().forEach(
        column -> column.setHeaderCaption(resources.message((String) column.getPropertyId())));
    view.sourceTubesGrid.getColumn(TUBE).setRenderer(new ComponentRenderer());
  }

  private ComboBox prepareSourceTubeComboBox(Sample sample) {
    ComboBox comboBox = new ComboBox();
    comboBox.addStyleName(TUBE);
    comboBox.setNullSelectionAllowed(false);
    comboBox.setNewItemsAllowed(false);
    tubeService.all(sample).forEach(tube -> {
      comboBox.addItem(tube);
      comboBox.setItemCaption(tube, tube.getName());
    });
    return comboBox;
  }

  private void bindFields() {
    destinationPlateFieldGroup.bind(view.destinationPlatesTypeField, PLATE_TYPE);
    destinationPlateFieldGroup.bind(view.destinationPlatesField, PLATE_NAME);
  }

  private void addListeners() {
    view.sourcePlatesField
        .addValueChangeListener(e -> updateSourcePlate((Plate) view.sourcePlatesField.getValue()));
    samplesProperty.addValueChangeListener(e -> updateSamples());
  }

  private void updateSourcePlate(Plate plate) {
    if (plate != null) {
      view.sourcePlatePanel.setCaption(plate.getName());
      List<Sample> samples = samplesProperty.getValue();
      List<PlateSpot> wells =
          samples.stream().flatMap(sample -> plateSpotService.location(sample, plate).stream())
              .collect(Collectors.toList());
      view.sourcePlateFormPresenter.setPlate(plate);
      view.sourcePlateFormPresenter.setSelectedSpots(wells.stream()
          .filter(w -> w.getPlate().getId().equals(plate.getId())).collect(Collectors.toList()));
    }
  }

  private void updateSamples() {
    List<Sample> samples = samplesProperty.getValue();
    sourceTubesContainer.addAll(samples);
    List<Plate> plates =
        plateService.all(new PlateFilterBuilder().containsAnySamples(samples).build());
    view.sourcePlatesField.removeAllItems();
    plates.forEach(plate -> {
      view.sourcePlatesField.addItem(plate);
      view.sourcePlatesField.setItemCaption(plate, plate.getName());
    });
    Plate last = samples.stream().map(s -> plateSpotService.last(s)).filter(w -> w != null)
        .map(w -> w.getPlate()).findFirst()
        .orElseGet(() -> plates.isEmpty() ? null : plates.get(0));
    if (last != null) {
      view.sourcePlatesField.setValue(last);
    }
  }

  public void enter(String parameters) {
    samplesProperty.setValue(view.savedSamples());
  }
}
