package ca.qc.ircm.proview.plate.web.platelayout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.client.platelayout.PlateLayoutServerRpc;
import ca.qc.ircm.proview.client.platelayout.PlateLayoutState;
import ca.qc.ircm.proview.client.platelayout.PlateLayoutState.WellData;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.server.ServerRpcMethodInvocation;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateLayoutTest {
  private PlateLayout plateLayout;
  @Mock
  private ColumnHeaderClickListener columnHeaderClickListener;
  @Mock
  private RowHeaderClickListener rowHeaderClickListener;
  @Mock
  private WellClickListener wellClickListener;
  @Mock
  private MouseEventDetails mouseDetails;
  @Captor
  private ArgumentCaptor<ColumnHeaderClickEvent> columnHeaderClickEventCaptor;
  @Captor
  private ArgumentCaptor<RowHeaderClickEvent> rowHeaderClickEventCaptor;
  @Captor
  private ArgumentCaptor<WellClickEvent> wellClickEventCaptor;
  private int columns = 12;
  private int rows = 8;

  @Before
  public void beforeTest() {
    plateLayout = new PlateLayout(columns, rows);
  }

  @Test
  public void addComponent_Coordinates() {
    Label label = new Label("test");

    plateLayout.addComponent(label, 1, 2);

    assertEquals(1, plateLayout.getComponentCount());
    List<Component> components = new ArrayList<>();
    plateLayout.iterator().forEachRemaining(components::add);
    assertEquals(label, components.get(0));
    assertTrue(plateLayout.getState().wellData.containsKey(label));
    assertNotNull(plateLayout.getState().wellData.get(label));
    WellData wellData = plateLayout.getState().wellData.get(label);
    assertEquals(1, wellData.column);
    assertEquals(2, wellData.row);
    assertEquals(PlateLayoutState.ALIGNMENT_DEFAULT.getBitMask(), wellData.alignment);
    assertTrue(wellData.styles == null || wellData.styles.isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void addComponent_Coordinates_AlreadyContainsComponent() {
    plateLayout.addComponent(new Label(), 1, 2);
    plateLayout.addComponent(new Label(), 1, 2);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void addComponent_Coordinates_ColumnTooLarge() {
    plateLayout.addComponent(new Label(), 20, 2);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void addComponent_Coordinates_ColumnTooLow() {
    plateLayout.addComponent(new Label(), -1, 2);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void addComponent_Coordinates_RowTooLarge() {
    plateLayout.addComponent(new Label(), 1, 20);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void addComponent_Coordinates_RowTooLow() {
    plateLayout.addComponent(new Label(), 1, -2);
  }

  @Test(expected = NullPointerException.class)
  public void addComponent_Coordinates_NullComponent() {
    plateLayout.addComponent(null, 1, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addComponent_Coordinates_AlreadyInContainer() {
    Label label = new Label("test");

    plateLayout.addComponent(label, 1, 2);
    plateLayout.addComponent(label, 2, 2);
  }

  @Test
  public void addComponent() {
    plateLayout = new PlateLayout(2, 2);
    Label label1 = new Label("test");
    Label label2 = new Label("test 2");
    Label label3 = new Label("test 3");

    plateLayout.addComponent(label1);
    plateLayout.addComponent(label2);
    plateLayout.addComponent(label3);

    assertEquals(3, plateLayout.getComponentCount());
    List<Component> components = new ArrayList<>();
    plateLayout.iterator().forEachRemaining(components::add);
    assertEquals(label1, components.get(0));
    assertEquals(label2, components.get(1));
    assertEquals(label3, components.get(2));
    assertTrue(plateLayout.getState().wellData.containsKey(label1));
    assertNotNull(plateLayout.getState().wellData.get(label1));
    WellData wellData = plateLayout.getState().wellData.get(label1);
    assertEquals(0, wellData.column);
    assertEquals(0, wellData.row);
    assertEquals(PlateLayoutState.ALIGNMENT_DEFAULT.getBitMask(), wellData.alignment);
    assertTrue(wellData.styles == null || wellData.styles.isEmpty());
    assertTrue(plateLayout.getState().wellData.containsKey(label2));
    assertNotNull(plateLayout.getState().wellData.get(label2));
    wellData = plateLayout.getState().wellData.get(label2);
    assertEquals(1, wellData.column);
    assertEquals(0, wellData.row);
    assertEquals(PlateLayoutState.ALIGNMENT_DEFAULT.getBitMask(), wellData.alignment);
    assertTrue(wellData.styles == null || wellData.styles.isEmpty());
    assertTrue(plateLayout.getState().wellData.containsKey(label3));
    assertNotNull(plateLayout.getState().wellData.get(label3));
    wellData = plateLayout.getState().wellData.get(label3);
    assertEquals(0, wellData.column);
    assertEquals(1, wellData.row);
    assertEquals(PlateLayoutState.ALIGNMENT_DEFAULT.getBitMask(), wellData.alignment);
    assertTrue(wellData.styles == null || wellData.styles.isEmpty());
  }

  @Test(expected = NullPointerException.class)
  public void addComponent_NullComponent() {
    plateLayout.addComponent(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addComponent_AlreadyInContainer() {
    Label label = new Label("test");

    plateLayout.addComponent(label);
    plateLayout.addComponent(label);
  }

  @Test(expected = IllegalStateException.class)
  public void addComponent_TooMany() {
    plateLayout = new PlateLayout(2, 2);

    plateLayout.addComponent(new Label());
    plateLayout.addComponent(new Label());
    plateLayout.addComponent(new Label());
    plateLayout.addComponent(new Label());
    plateLayout.addComponent(new Label());
  }

  @Test
  public void replaceComponent_Swap() {
    Label label1 = new Label("test");
    Label label2 = new Label("test");

    plateLayout.addComponent(label1, 1, 2);
    plateLayout.addComponent(label2, 2, 1);
    plateLayout.replaceComponent(label1, label2);

    assertTrue(plateLayout.getState().wellData.containsKey(label1));
    assertNotNull(plateLayout.getState().wellData.get(label1));
    WellData wellData = plateLayout.getState().wellData.get(label1);
    assertEquals(2, wellData.column);
    assertEquals(1, wellData.row);
    assertEquals(PlateLayoutState.ALIGNMENT_DEFAULT.getBitMask(), wellData.alignment);
    assertTrue(wellData.styles == null || wellData.styles.isEmpty());
    assertTrue(plateLayout.getState().wellData.containsKey(label2));
    assertNotNull(plateLayout.getState().wellData.get(label2));
    wellData = plateLayout.getState().wellData.get(label2);
    assertEquals(1, wellData.column);
    assertEquals(2, wellData.row);
    assertEquals(PlateLayoutState.ALIGNMENT_DEFAULT.getBitMask(), wellData.alignment);
    assertTrue(wellData.styles == null || wellData.styles.isEmpty());
  }

  @Test
  public void replaceComponent_AddNew() {
    Label label1 = new Label("test");
    Label label2 = new Label("test");

    plateLayout.replaceComponent(label1, label2);

    assertFalse(plateLayout.getState().wellData.containsKey(label1));
    assertTrue(plateLayout.getState().wellData.containsKey(label2));
    assertNotNull(plateLayout.getState().wellData.get(label2));
    WellData wellData = plateLayout.getState().wellData.get(label2);
    assertEquals(0, wellData.column);
    assertEquals(0, wellData.row);
    assertEquals(PlateLayoutState.ALIGNMENT_DEFAULT.getBitMask(), wellData.alignment);
    assertTrue(wellData.styles == null || wellData.styles.isEmpty());
  }

  @Test
  public void replaceComponent_ReplaceAndAddNew() {
    Label label1 = new Label("test");
    Label label2 = new Label("test");

    plateLayout.addComponent(label1, 1, 2);
    plateLayout.replaceComponent(label1, label2);

    assertFalse(plateLayout.getState().wellData.containsKey(label1));
    assertTrue(plateLayout.getState().wellData.containsKey(label2));
    assertNotNull(plateLayout.getState().wellData.get(label2));
    WellData wellData = plateLayout.getState().wellData.get(label2);
    assertEquals(1, wellData.column);
    assertEquals(2, wellData.row);
    assertEquals(PlateLayoutState.ALIGNMENT_DEFAULT.getBitMask(), wellData.alignment);
    assertTrue(wellData.styles == null || wellData.styles.isEmpty());
  }

  @Test
  public void replaceComponent_NullOld() {
    Label label2 = new Label("test");

    plateLayout.replaceComponent(null, label2);

    assertTrue(plateLayout.getState().wellData.containsKey(label2));
    assertNotNull(plateLayout.getState().wellData.get(label2));
    WellData wellData = plateLayout.getState().wellData.get(label2);
    assertEquals(0, wellData.column);
    assertEquals(0, wellData.row);
    assertEquals(PlateLayoutState.ALIGNMENT_DEFAULT.getBitMask(), wellData.alignment);
    assertTrue(wellData.styles == null || wellData.styles.isEmpty());
  }

  @Test(expected = NullPointerException.class)
  public void replaceComponent_NullNew() {
    Label label1 = new Label("test");

    plateLayout.addComponent(label1, 1, 2);
    plateLayout.replaceComponent(label1, null);
  }

  @Test
  public void removeComponent() {
    Label label = new Label("test");
    plateLayout.addComponent(label, 1, 2);

    plateLayout.removeComponent(label);

    assertEquals(0, plateLayout.getComponentCount());
    assertFalse(plateLayout.getState().wellData.containsKey(label));
  }

  @Test
  public void removeComponent_NotInContainer() {
    Label label = new Label("test");

    plateLayout.removeComponent(label);

    assertEquals(0, plateLayout.getComponentCount());
    assertFalse(plateLayout.getState().wellData.containsKey(label));
  }

  @Test
  public void removeComponent_NullComponent() {
    plateLayout.removeComponent(null);

    assertEquals(0, plateLayout.getComponentCount());
  }

  @Test
  public void removeComponent_Coordinates() {
    Label label = new Label("test");
    plateLayout.addComponent(label, 1, 2);

    plateLayout.removeComponent(1, 2);

    assertEquals(0, plateLayout.getComponentCount());
    assertFalse(plateLayout.getState().wellData.containsKey(label));
  }

  @Test
  public void removeComponent_Coordinates_NotInContainer() {
    plateLayout.removeComponent(1, 2);

    assertEquals(0, plateLayout.getComponentCount());
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void removeComponent_Coordinates_ColumnTooLarge() {
    plateLayout.removeComponent(20, 2);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void removeComponent_Coordinates_ColumnTooLow() {
    plateLayout.removeComponent(-1, 2);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void removeComponent_Coordinates_RowTooLarge() {
    plateLayout.removeComponent(1, 20);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void removeComponent_Coordinates_RowTooLow() {
    plateLayout.removeComponent(1, -1);
  }

  @Test
  public void clickColumnHeader() {
    plateLayout.addColumnHeaderClickListener(columnHeaderClickListener);

    plateLayout.clickColumnHeader(1);

    verify(columnHeaderClickListener).columnHeaderClick(columnHeaderClickEventCaptor.capture());
    ColumnHeaderClickEvent event = columnHeaderClickEventCaptor.getValue();
    assertEquals(1, event.getColumn());
    assertEquals(plateLayout, event.getSource());
  }

  @Test
  public void clickColumnHeader_TooLarge() {
    plateLayout.addColumnHeaderClickListener(columnHeaderClickListener);

    try {
      plateLayout.clickColumnHeader(20);
      fail("Expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      // Success.
    }

    verify(columnHeaderClickListener, never()).columnHeaderClick(any());
  }

  @Test
  public void clickColumnHeader_TooLow() {
    plateLayout.addColumnHeaderClickListener(columnHeaderClickListener);

    try {
      plateLayout.clickColumnHeader(-1);
      fail("Expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      // Success.
    }

    verify(columnHeaderClickListener, never()).columnHeaderClick(any());
  }

  @Test
  public void clickRowHeader() {
    plateLayout.addRowHeaderClickListener(rowHeaderClickListener);

    plateLayout.clickRowHeader(1);

    verify(rowHeaderClickListener).rowHeaderClick(rowHeaderClickEventCaptor.capture());
    RowHeaderClickEvent event = rowHeaderClickEventCaptor.getValue();
    assertEquals(1, event.getRow());
    assertEquals(plateLayout, event.getSource());
  }

  @Test
  public void clickRowHeader_TooLarge() {
    plateLayout.addRowHeaderClickListener(rowHeaderClickListener);

    try {
      plateLayout.clickRowHeader(20);
      fail("Expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      // Success.
    }

    verify(rowHeaderClickListener, never()).rowHeaderClick(any());
  }

  @Test
  public void clickRowHeader_TooLow() {
    plateLayout.addRowHeaderClickListener(rowHeaderClickListener);

    try {
      plateLayout.clickRowHeader(-1);
      fail("Expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      // Success.
    }

    verify(rowHeaderClickListener, never()).rowHeaderClick(any());
  }

  @Test
  public void clickWell() {
    plateLayout.addWellClickListener(wellClickListener);

    plateLayout.clickWell(1, 2);

    verify(wellClickListener).wellClick(wellClickEventCaptor.capture());
    WellClickEvent event = wellClickEventCaptor.getValue();
    assertEquals(1, event.getColumn());
    assertEquals(2, event.getRow());
    assertEquals(plateLayout, event.getSource());
  }

  @Test
  public void clickWell_ColumnTooLarge() {
    plateLayout.addWellClickListener(wellClickListener);

    try {
      plateLayout.clickWell(20, 2);
      fail("Expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      // Success.
    }

    verify(wellClickListener, never()).wellClick(any());
  }

  @Test
  public void clickWell_ColumnTooLow() {
    plateLayout.addWellClickListener(wellClickListener);

    try {
      plateLayout.clickWell(-1, 2);
      fail("Expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      // Success.
    }

    verify(wellClickListener, never()).wellClick(any());
  }

  @Test
  public void clickWell_RowTooLarge() {
    plateLayout.addWellClickListener(wellClickListener);

    try {
      plateLayout.clickWell(1, 20);
      fail("Expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      // Success.
    }

    verify(wellClickListener, never()).wellClick(any());
  }

  @Test
  public void clickWell_RowTooLow() {
    plateLayout.addWellClickListener(wellClickListener);

    try {
      plateLayout.clickWell(1, -1);
      fail("Expected IndexOutOfBoundsException");
    } catch (IndexOutOfBoundsException e) {
      // Success.
    }

    verify(wellClickListener, never()).wellClick(any());
  }

  @Test
  public void addColumnHeaderClickListener() {
    plateLayout.addColumnHeaderClickListener(columnHeaderClickListener);

    List<?> listeners = new ArrayList<>(plateLayout.getListeners(ColumnHeaderClickEvent.class));
    assertEquals(1, listeners.size());
    assertEquals(columnHeaderClickListener, listeners.get(0));
    plateLayout.clickColumnHeader(1);
    verify(columnHeaderClickListener).columnHeaderClick(columnHeaderClickEventCaptor.capture());
    ColumnHeaderClickEvent event = columnHeaderClickEventCaptor.getValue();
    assertEquals(1, event.getColumn());
    assertEquals(plateLayout, event.getSource());
  }

  @Test
  public void removeColumnHeaderClickListener() {
    plateLayout.addColumnHeaderClickListener(columnHeaderClickListener);

    plateLayout.removeColumnHeaderClickListener(columnHeaderClickListener);

    List<?> listeners = new ArrayList<>(plateLayout.getListeners(ColumnHeaderClickEvent.class));
    assertEquals(0, listeners.size());
    plateLayout.clickColumnHeader(1);
    verify(columnHeaderClickListener, never()).columnHeaderClick(any());
  }

  @Test
  public void addRowHeaderClickListener() {
    plateLayout.addRowHeaderClickListener(rowHeaderClickListener);

    List<?> listeners = new ArrayList<>(plateLayout.getListeners(RowHeaderClickEvent.class));
    assertEquals(1, listeners.size());
    assertEquals(rowHeaderClickListener, listeners.get(0));
    plateLayout.clickRowHeader(1);
    verify(rowHeaderClickListener).rowHeaderClick(rowHeaderClickEventCaptor.capture());
    RowHeaderClickEvent event = rowHeaderClickEventCaptor.getValue();
    assertEquals(1, event.getRow());
    assertEquals(plateLayout, event.getSource());
  }

  @Test
  public void removeRowHeaderClickListener() {
    plateLayout.addRowHeaderClickListener(rowHeaderClickListener);

    plateLayout.removeRowHeaderClickListener(rowHeaderClickListener);

    List<?> listeners = new ArrayList<>(plateLayout.getListeners(RowHeaderClickEvent.class));
    assertEquals(0, listeners.size());
    plateLayout.clickRowHeader(1);
    verify(rowHeaderClickListener, never()).rowHeaderClick(any());
  }

  @Test
  public void addWellClickListener() {
    plateLayout.addWellClickListener(wellClickListener);

    List<?> listeners = new ArrayList<>(plateLayout.getListeners(WellClickEvent.class));
    assertEquals(1, listeners.size());
    assertEquals(wellClickListener, listeners.get(0));
    plateLayout.clickWell(1, 2);
    verify(wellClickListener).wellClick(wellClickEventCaptor.capture());
    WellClickEvent event = wellClickEventCaptor.getValue();
    assertEquals(1, event.getColumn());
    assertEquals(2, event.getRow());
    assertEquals(plateLayout, event.getSource());
  }

  @Test
  public void removeWellClickListener() {
    plateLayout.addWellClickListener(wellClickListener);

    plateLayout.removeWellClickListener(wellClickListener);

    List<?> listeners = new ArrayList<>(plateLayout.getListeners(RowHeaderClickEvent.class));
    assertEquals(0, listeners.size());
    plateLayout.clickWell(1, 2);
    verify(wellClickListener, never()).wellClick(any());
  }

  @Test
  public void getWellStyle() {
    Label label = new Label("test");
    plateLayout.addComponent(label, 1, 2);

    String styleName = plateLayout.getWellStyleName(1, 2);

    assertEquals("", styleName);
  }

  @Test
  public void addWellStyleName() {
    Label label = new Label("test");
    plateLayout.addComponent(label, 1, 2);
    String style = "test";

    plateLayout.addWellStyleName(1, 2, style);

    String styleName = plateLayout.getWellStyleName(1, 2);
    assertEquals(style, styleName);
    WellData wellData = plateLayout.getState().wellData.get(label);
    assertEquals(1, wellData.styles.size());
    assertEquals(style, wellData.styles.get(0));
  }

  @Test
  public void addWellStyleName_Multiple() {
    Label label = new Label("test");
    plateLayout.addComponent(label, 1, 2);
    String style = "test";
    String style2 = "test2";

    plateLayout.addWellStyleName(1, 2, style);
    plateLayout.addWellStyleName(1, 2, style2);

    String styleName = plateLayout.getWellStyleName(1, 2);
    assertEquals(style + " " + style2, styleName);
    WellData wellData = plateLayout.getState().wellData.get(label);
    assertEquals(2, wellData.styles.size());
    assertEquals(style, wellData.styles.get(0));
    assertEquals(style2, wellData.styles.get(1));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void addWellStyleName_ColumnTooLarge() {
    plateLayout.addWellStyleName(20, 2, "test");
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void addWellStyleName_ColumnTooLow() {
    plateLayout.addWellStyleName(-1, 2, "test");
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void addWellStyleName_RowTooLarge() {
    plateLayout.addWellStyleName(1, 20, "test");
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void addWellStyleName_RowTooLow() {
    plateLayout.addWellStyleName(1, -2, "test");
  }

  @Test
  public void removeWellStyleName() {
    Label label = new Label("test");
    plateLayout.addComponent(label, 1, 2);
    String style = "test";
    plateLayout.addWellStyleName(1, 2, style);

    plateLayout.removeWellStyleName(1, 2, style);

    String styleName = plateLayout.getWellStyleName(1, 2);
    assertEquals("", styleName);
    WellData wellData = plateLayout.getState().wellData.get(label);
    assertEquals(0, wellData.styles.size());
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void removeWellStyleName_ColumnTooLarge() {
    plateLayout.removeWellStyleName(20, 2, "test");
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void removeWellStyleName_ColumnTooLow() {
    plateLayout.removeWellStyleName(-1, 2, "test");
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void removeWellStyleName_RowTooLarge() {
    plateLayout.removeWellStyleName(1, 20, "test");
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void removeWellStyleName_RowTooLow() {
    plateLayout.removeWellStyleName(1, -2, "test");
  }

  @Test
  public void getColumns() {
    int columns = plateLayout.getColumns();

    assertEquals(this.columns, columns);
  }

  @Test
  public void setColumns() {
    int columns = 6;

    plateLayout.setColumns(columns);

    assertEquals(columns, plateLayout.getColumns());
    assertEquals(columns, plateLayout.getState().columns);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setColumns_TooLow() {
    int columns = -1;

    plateLayout.setColumns(columns);
  }

  @Test
  public void getRows() {
    int rows = plateLayout.getRows();

    assertEquals(this.rows, rows);
  }

  @Test
  public void setRows() {
    int rows = 6;

    plateLayout.setRows(rows);

    assertEquals(rows, plateLayout.getRows());
    assertEquals(rows, plateLayout.getState().rows);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setRows_TooLow() {
    int rows = -1;

    plateLayout.setRows(rows);
  }

  @Test
  public void clickColumnHeader_Rpc() throws Throwable {
    plateLayout.addColumnHeaderClickListener(columnHeaderClickListener);

    ServerRpcMethodInvocation invocation =
        new ServerRpcMethodInvocation(null, PlateLayoutServerRpc.class, "columnHeaderClicked", 2);
    invocation.setParameters(new Object[] { 1, mouseDetails });
    plateLayout.getRpcManager(PlateLayoutServerRpc.class.getName()).applyInvocation(invocation);

    verify(columnHeaderClickListener).columnHeaderClick(columnHeaderClickEventCaptor.capture());
    ColumnHeaderClickEvent event = columnHeaderClickEventCaptor.getValue();
    assertEquals(1, event.getColumn());
    assertEquals(plateLayout, event.getSource());
    assertEquals(mouseDetails, event.getDetails());
  }

  @Test
  public void clickRowHeader_Rpc() throws Throwable {
    plateLayout.addRowHeaderClickListener(rowHeaderClickListener);

    ServerRpcMethodInvocation invocation =
        new ServerRpcMethodInvocation(null, PlateLayoutServerRpc.class, "rowHeaderClicked", 2);
    invocation.setParameters(new Object[] { 1, mouseDetails });
    plateLayout.getRpcManager(PlateLayoutServerRpc.class.getName()).applyInvocation(invocation);

    verify(rowHeaderClickListener).rowHeaderClick(rowHeaderClickEventCaptor.capture());
    RowHeaderClickEvent event = rowHeaderClickEventCaptor.getValue();
    assertEquals(1, event.getRow());
    assertEquals(plateLayout, event.getSource());
    assertEquals(mouseDetails, event.getDetails());
  }

  @Test
  public void clickWell_Rpc() throws Throwable {
    plateLayout.addWellClickListener(wellClickListener);

    ServerRpcMethodInvocation invocation =
        new ServerRpcMethodInvocation(null, PlateLayoutServerRpc.class, "wellClicked", 3);
    invocation.setParameters(new Object[] { 1, 2, mouseDetails });
    plateLayout.getRpcManager(PlateLayoutServerRpc.class.getName()).applyInvocation(invocation);

    verify(wellClickListener).wellClick(wellClickEventCaptor.capture());
    WellClickEvent event = wellClickEventCaptor.getValue();
    assertEquals(1, event.getColumn());
    assertEquals(2, event.getRow());
    assertEquals(plateLayout, event.getSource());
    assertEquals(mouseDetails, event.getDetails());
  }
}
