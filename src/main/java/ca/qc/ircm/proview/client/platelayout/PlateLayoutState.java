package ca.qc.ircm.proview.client.platelayout;

import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.AlignmentInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plate layout state.
 */
public class PlateLayoutState extends com.vaadin.shared.AbstractComponentState {
  public static AlignmentInfo ALIGNMENT_DEFAULT = AlignmentInfo.TOP_LEFT;
  private static final long serialVersionUID = -1122609654126841136L;
  public int columns;
  public int rows;
  public Map<Connector, WellData> wellData = new HashMap<>();

  public static class WellData implements Serializable {
    private static final long serialVersionUID = -1900301599932252390L;
    public int column;
    public int row;
    public List<String> styles = null;
    public int alignment = ALIGNMENT_DEFAULT.getBitMask();
  }
}
