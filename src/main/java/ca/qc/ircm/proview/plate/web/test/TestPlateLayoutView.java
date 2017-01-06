package ca.qc.ircm.proview.plate.web.test;

import ca.qc.ircm.proview.plate.web.platelayout.PlateLayout;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import java.util.stream.IntStream;

import javax.annotation.security.RolesAllowed;

@SpringView(name = TestPlateLayoutView.VIEW_NAME)
@RolesAllowed("ADMIN")
public class TestPlateLayoutView extends VerticalLayout implements View {
  public static final String VIEW_NAME = "plate/test/plate-layout";
  public static final int COLUMNS_DEFAULT = 4;
  public static final int ROWS_DEFAULT = 3;
  public static final String WELL_LABEL_VALUE = "well %1$d-%2$d";
  public static final String COLUMN_CLICKED = "column-%1$d";
  public static final String ROW_CLICKED = "row-%1$d";
  public static final String WELL_CLICKED = "well-%1$d-%2$d";
  public static final String COLUMNS = "columns";
  public static final String ROWS = "rows";
  public static final String RESIZE = "resize";
  public static final String STYLE = "style";
  public static final String STYLE_BUTTON = "styleButton";
  private static final long serialVersionUID = -4794576134256174739L;
  private PlateLayout plateLayout = new PlateLayout(COLUMNS_DEFAULT, ROWS_DEFAULT);
  private TextField columnsField = new TextField();
  private TextField rowsField = new TextField();
  private Button resizeButton = new Button();
  private TextField styleField = new TextField();
  private Button styleButton = new Button();

  public TestPlateLayoutView() {
    prepareLayout();
    prepareFields();
  }

  private void prepareLayout() {
    addComponent(plateLayout);
    IntStream.range(0, COLUMNS_DEFAULT).forEach(column -> {
      IntStream.range(0, ROWS_DEFAULT).forEach(row -> {
        plateLayout.addComponent(new Label(String.format(WELL_LABEL_VALUE, column, row)), column,
            row);
      });
    });
    HorizontalLayout resizeLayout = new HorizontalLayout();
    addComponent(resizeLayout);
    resizeLayout.addComponent(columnsField);
    resizeLayout.addComponent(rowsField);
    resizeLayout.addComponent(resizeButton);
    HorizontalLayout styleLayout = new HorizontalLayout();
    addComponent(styleLayout);
    styleLayout.addComponent(styleField);
    styleLayout.addComponent(styleButton);
  }

  private void prepareFields() {
    plateLayout.addColumnHeaderClickListener(
        e -> plateLayout.addStyleName(String.format(COLUMN_CLICKED, e.getColumn())));
    plateLayout.addRowHeaderClickListener(
        e -> plateLayout.addStyleName(String.format(ROW_CLICKED, e.getRow())));
    plateLayout.addWellClickListener(
        e -> plateLayout.addStyleName(String.format(WELL_CLICKED, e.getColumn(), e.getRow())));
    columnsField.setId(COLUMNS);
    columnsField.setValue(String.valueOf(COLUMNS_DEFAULT));
    rowsField.setId(ROWS);
    rowsField.setValue(String.valueOf(ROWS_DEFAULT));
    resizeButton.setId(RESIZE);
    resizeButton.addClickListener(e -> resize());
    styleField.setId(STYLE);
    styleButton.setId(STYLE_BUTTON);
    styleButton.addClickListener(e -> setWellStyle());
  }

  private void resize() {
    int columns = Integer.parseInt(columnsField.getValue());
    int rows = Integer.parseInt(rowsField.getValue());
    plateLayout.setColumns(columns);
    plateLayout.setRows(rows);
  }

  private void setWellStyle() {
    String style = styleField.getValue();
    IntStream.range(0, COLUMNS_DEFAULT).forEach(column -> {
      IntStream.range(0, ROWS_DEFAULT).forEach(row -> {
        plateLayout.clearWellStyleName(column, row);
        plateLayout.addWellStyleName(column, row, style);
      });
    });
  }

  @Override
  public void enter(ViewChangeEvent event) {
  }
}
