package ca.qc.ircm.proview.plate.render;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateSpot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Renders plate information into an image.
 */
public class PlateImageRenderer {
  /**
   * Height of a rectangle in a plate.
   */
  protected static final int HEADER_PADDING = 5;
  /**
   * Width of a rectangles in a plate.
   */
  protected static final int REC_WIDTH = 63;
  /**
   * Padding of rectangles on each sides.
   */
  protected static final int REC_PADDING = 5;
  /**
   * Width of a separation line between rectangles.
   */
  protected static final int LINE_WIDTH = 2;
  protected static final String BANNED_BACKGROUND_RESOURCE = "/banned_spot_background.png";
  /**
   * Width of a lines shown to indicate that spot is banned.
   */
  protected static final int BANNED_WIDTH = 10;
  /**
   * Maximum number of lines for sample tag.
   */
  protected static final int SAMPLE_TAG_MAX_LINES = 3;
  private static final Logger logger = LoggerFactory.getLogger(PlateImageRenderer.class);

  /**
   * Creates an image renderer for plate.
   *
   * @param plate
   *          Plate to render with it's spots.
   * @param info
   *          Used to get localised plate information.
   */
  public PlateImageRenderer(Plate plate, LocalizedPlate info) {
    this.plate = plate;
    this.spots = plate.getSpots();
    this.info = info;
    this.headerFont = new Font("SansSerif", Font.BOLD, 18);
    this.spotIdFont = new Font("SansSerif", Font.PLAIN, 9);
    this.spotFont = new Font("SansSerif", Font.PLAIN, 10);
    this.graphics = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED).createGraphics();
  }

  /**
   * Plate used in PDF.
   */
  private final Plate plate;
  /**
   * Plate's spots.
   */
  private final List<PlateSpot> spots;
  /**
   * To get specific strings to write in PDF.
   */
  private final LocalizedPlate info;
  /**
   * Font to be used for header.
   */
  private final Font headerFont;
  /**
   * Font to be used for spot id.
   */
  private final Font spotIdFont;
  /**
   * Font to be used for spot content.
   */
  private final Font spotFont;
  /**
   * Font render context to be used.
   */
  private final Graphics2D graphics;

  /**
   * Returns font to use for header.
   *
   * @return Font to use for header.
   */
  protected Font getHeaderFont() {
    return headerFont;
  }

  /**
   * Returns font to use for spot id.
   *
   * @return Font to use for spot id.
   */
  protected Font getSpotIdFont() {
    return spotIdFont;
  }

  /**
   * Returns font to use for sample id and name.
   *
   * @return Font to use for sample.
   */
  protected Font getSampleFont() {
    return spotFont;
  }

  /**
   * Total width of image containing plate.
   *
   * @return Total width of image.
   */
  protected double getWidth() {
    double width = 0;
    double padding = REC_PADDING + LINE_WIDTH;
    double headerWidth =
        this.getHeaderFont().getStringBounds(info.getHeader(plate), graphics.getFontRenderContext())
            .getWidth() + 2 * padding;
    double spotWidth = 0;
    for (int column = 0; column < plate.getColumnCount(); column++) {
      spotWidth += this.getRectangleWidth(column) - LINE_WIDTH;
    }
    spotWidth += LINE_WIDTH;
    return width + Math.max(headerWidth, spotWidth);
  }

  /**
   * Width of rectangles for spots on this column.
   *
   * @param column
   *          Column index.
   * @return Width of rectangles for spots on this column.
   */
  protected double getRectangleWidth(int column) {
    double width = REC_WIDTH;
    double padding = REC_PADDING + LINE_WIDTH;
    width += 2 * padding;
    return width;
  }

  /**
   * Total height of image containing plate.
   *
   * @return Total height of image.
   */
  protected double getHeight() {
    double height = 0;
    height += getHeaderHeight();
    for (int row = 0; row < plate.getRowCount(); row++) {
      height += this.getRectangleHeight(row) - LINE_WIDTH;
    }
    return height;
  }

  /**
   * Total height of image containing header.
   *
   * @return Total height of header.
   */
  protected double getHeaderHeight() {
    double height = 0;
    int padding = LINE_WIDTH + REC_PADDING;
    height += 2 * padding;
    height += graphics.getFontMetrics(getHeaderFont()).getHeight();
    return height;
  }

  /**
   * Total height of image containing spot.
   *
   * @param row
   *          Row index.
   * @return Total height of row.
   */
  protected double getRectangleHeight(int row) {
    double height = 0;
    int padding = LINE_WIDTH + REC_PADDING;
    height += 2 * padding;

    // Spot id height
    height += graphics.getFontMetrics(this.getSpotIdFont()).getHeight();

    // Sample tag height.
    height += graphics.getFontMetrics(this.getSampleFont()).getHeight() * SAMPLE_TAG_MAX_LINES;

    return height;
  }

  /**
   * Renders an image containing plate information.
   *
   * @return Image containing plate information.
   */
  public RenderedImage render() {
    // Create new image.
    BufferedImage image = new BufferedImage((int) Math.ceil(this.getWidth()),
        (int) Math.ceil(this.getHeight()), BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D graphics = image.createGraphics();
    graphics.setBackground(Color.WHITE);
    graphics.setPaint(Color.BLACK);
    graphics.clearRect(0, 0, image.getWidth(), image.getHeight());
    // Render header.
    renderHeader(graphics, 0, 0);
    // Compute graphical location of all spots.
    double[] xval = new double[plate.getColumnCount()];
    xval[0] = 0;
    for (int i = 1; i < plate.getColumnCount(); i++) {
      xval[i] = xval[i - 1] + this.getRectangleWidth(i - 1) - LINE_WIDTH;
    }
    double[] yval = new double[plate.getRowCount()];
    yval[0] = this.getHeaderHeight() - LINE_WIDTH;
    for (int i = 1; i < plate.getRowCount(); i++) {
      yval[i] = yval[i - 1] + this.getRectangleHeight(i - 1) - LINE_WIDTH;
    }
    // Render spots.
    for (int i = 0; i < spots.size(); i++) {
      PlateSpot spot = spots.get(i);
      renderSpot(spot, graphics, xval[spot.getColumn()], yval[spot.getRow()]);
    }
    return image;
  }

  /**
   * Draws borders of rectangle delimited by upper left and bottom right locations.
   *
   * @param graphics
   *          Graphic where to write borders.
   * @param upperLeft
   *          Upper left location.
   * @param bottomRight
   *          Bottom right location.
   */
  protected void renderBorder(Graphics2D graphics, Point2D upperLeft, Point2D bottomRight) {
    double recWidth = bottomRight.getX() - upperLeft.getX();
    double recHeight = bottomRight.getY() - upperLeft.getY();
    Rectangle2D top =
        new Rectangle2D.Double(upperLeft.getX(), upperLeft.getY(), recWidth, LINE_WIDTH);
    graphics.fill(top);
    Rectangle2D bottom = new Rectangle2D.Double(upperLeft.getX(), bottomRight.getY() - LINE_WIDTH,
        recWidth, LINE_WIDTH);
    graphics.fill(bottom);
    Rectangle2D left =
        new Rectangle2D.Double(upperLeft.getX(), upperLeft.getY(), LINE_WIDTH, recHeight);
    graphics.fill(left);
    Rectangle2D right = new Rectangle2D.Double(bottomRight.getX() - LINE_WIDTH, upperLeft.getY(),
        LINE_WIDTH, recHeight);
    graphics.fill(right);
  }

  /**
   * Render header and borders around it.
   *
   * @param graphics
   *          Graphic where to write header.
   * @param xval
   *          X position of header.
   * @param yval
   *          Y position of header.
   */
  protected void renderHeader(Graphics2D graphics, double xval, double yval) {
    // Header location.
    Point2D upperLeft = new Point2D.Double(xval, yval);
    Point2D bottomRight = new Point2D.Double(upperLeft.getX() + this.getWidth(),
        upperLeft.getY() + this.getHeaderHeight());
    // Draw borders around header.
    renderBorder(graphics, upperLeft, bottomRight);
    // Draw header content.
    String header = info.getHeader(plate);
    graphics.setFont(this.getHeaderFont());
    Point2D headerLocation = new Point2D.Double(upperLeft.getX() + LINE_WIDTH + HEADER_PADDING,
        upperLeft.getY() + LINE_WIDTH + HEADER_PADDING + graphics.getFontMetrics().getAscent());
    graphics.drawString(header, (float) headerLocation.getX(), (float) headerLocation.getY());
  }

  /**
   * Render spot.
   *
   * @param spot
   *          Spot to render.
   * @param graphics
   *          Graphic where to write spot.
   * @param xval
   *          X position of spot.
   * @param yval
   *          Y position of spot.
   */
  protected void renderSpot(PlateSpot spot, Graphics2D graphics, double xval, double yval) {
    Point2D borderUpperLeft = new Point2D.Double(xval, yval);
    Point2D borderBottomRight = new Point2D.Double(xval + this.getRectangleWidth(spot.getColumn()),
        yval + this.getRectangleHeight(spot.getRow()));
    if (spot.isBanned()) {
      renderBanned(spot, graphics, borderUpperLeft, borderBottomRight);
    }

    // Total padding in cell.
    int padding = LINE_WIDTH + REC_PADDING;

    // Spot information location.
    Point2D upperLeft = new Point2D.Double(xval + padding, yval + padding);
    Point2D bottomRight =
        new Point2D.Double(xval + this.getRectangleWidth(spot.getColumn()) - padding,
            yval + this.getRectangleHeight(spot.getRow()) - padding);

    // Draw borders around spot.
    renderBorder(graphics, borderUpperLeft, borderBottomRight);

    // Draw spot content.
    upperLeft = renderSpotName(spot, graphics, upperLeft);
    if (spot.getSample() != null) {
      renderSampleName(spot, graphics, upperLeft, bottomRight);
    }
  }

  /**
   * Write spot id in graphic.
   *
   * @param spot
   *          Spot to write.
   * @param graphics
   *          Graphic where to write spot id.
   * @param upperLeft
   *          Upper left location of text.
   * @return Upper left location for text to write below spot id.
   */
  protected Point2D renderSpotName(PlateSpot spot, Graphics2D graphics, Point2D upperLeft) {
    // set font.
    graphics.setFont(this.getSpotIdFont());
    // Compute text location from upper left location.
    Point2D spotIdLocation = new Point2D.Double(upperLeft.getX(),
        upperLeft.getY() + graphics.getFontMetrics().getAscent());
    String spotId = info.getSpot(spot);
    graphics.drawString(spotId, (float) spotIdLocation.getX(), (float) spotIdLocation.getY());

    // Upper left location for text following spot id.
    Point2D newUpperLeft = new Point2D.Double(upperLeft.getX(),
        upperLeft.getY() + graphics.getFontMetrics().getHeight());
    return newUpperLeft;
  }

  /**
   * Write spot's sample name in graphic.
   *
   * @param spot
   *          spot
   * @param graphics
   *          Graphic where to write sample name.
   * @param upperLeft
   *          Upper left location of text.
   * @param bottomRight
   *          Bottom right maximum location of text.
   * @return Upper left location for text to write below sample name.
   */
  protected Point2D renderSampleName(PlateSpot spot, Graphics2D graphics, Point2D upperLeft,
      Point2D bottomRight) {
    // Set font.
    graphics.setFont(this.getSampleFont());

    // Text location.
    Point2D sampleTagLocation = new Point2D.Double(upperLeft.getX(),
        upperLeft.getY() + graphics.getFontMetrics().getAscent());

    // Maximum width for text.
    double sampleTagMaxWidth = bottomRight.getX() - upperLeft.getX();

    // Number of lines that sample name takes.
    int lineCount = 0;

    // Write sample name.
    String sampleName = info.getSampleName(spot);
    if (sampleName == null) {
      sampleName = "";
    }
    while (!"".equals(sampleName)) {
      if (this.getSampleFont().getStringBounds(sampleName, graphics.getFontRenderContext())
          .getWidth() > sampleTagMaxWidth) {
        // Tag is too long, write maximum number of characters.
        StringBuilder partTag = new StringBuilder(sampleName);
        while (this.getSampleFont()
            .getStringBounds(partTag.toString(), graphics.getFontRenderContext())
            .getWidth() > sampleTagMaxWidth) {
          partTag.deleteCharAt(partTag.length() - 1);
        }

        graphics.drawString(partTag.toString(), (float) sampleTagLocation.getX(),
            (float) sampleTagLocation.getY());
        lineCount++;

        // Next line must be written below current line.
        sampleTagLocation = new Point2D.Double(sampleTagLocation.getX(),
            sampleTagLocation.getY() + graphics.getFontMetrics().getHeight());

        // Set next line to write in sampleTag.
        sampleName = sampleName.substring(partTag.length());
      } else {
        // Write remaining tag.
        graphics.drawString(sampleName, (float) sampleTagLocation.getX(),
            (float) sampleTagLocation.getY());
        lineCount++;
        sampleName = "";
      }
    }

    if (this.getSampleFont().getStringBounds(sampleName, graphics.getFontRenderContext())
        .getWidth() > sampleTagMaxWidth) {
      // If sample tag is too long, write name on 2 lines.
      StringBuilder one = new StringBuilder(sampleName);
      StringBuilder two = new StringBuilder();
      // Write first line.
      while (this.getSampleFont().getStringBounds(one.toString(), graphics.getFontRenderContext())
          .getWidth() > sampleTagMaxWidth) {
        two.insert(0, one.charAt(one.length() - 1));
        one.deleteCharAt(one.length() - 1);
      }
      graphics.drawString(one.toString(), (float) sampleTagLocation.getX(),
          (float) sampleTagLocation.getY());
      lineCount++;

      // Write second line. If second line is too long, reduce number of
      // characters to write.
      sampleTagLocation = new Point2D.Double(sampleTagLocation.getX(),
          sampleTagLocation.getY() + graphics.getFontMetrics().getHeight());
      while (this.getSampleFont().getStringBounds(two.toString(), graphics.getFontRenderContext())
          .getWidth() > sampleTagMaxWidth) {
        two.deleteCharAt(two.length() - 1);
      }
      graphics.drawString(two.toString(), (float) sampleTagLocation.getX(),
          (float) sampleTagLocation.getY());
      lineCount++;
    } else {
      graphics.drawString(sampleName, (float) sampleTagLocation.getX(),
          (float) sampleTagLocation.getY());
      lineCount = 1;
    }

    // Upper left location for text following spot name.
    Point2D newUpperLeft = new Point2D.Double(upperLeft.getX(),
        upperLeft.getY() + lineCount * graphics.getFontMetrics().getHeight());
    return newUpperLeft;
  }

  protected void renderBanned(PlateSpot spot, Graphics2D graphics, Point2D upperLeft,
      Point2D bottomRight) {
    double width = bottomRight.getY() - upperLeft.getY();
    double height = bottomRight.getY() - upperLeft.getY();

    try {
      BufferedImage bannedBackground =
          ImageIO.read(getClass().getResource(BANNED_BACKGROUND_RESOURCE));
      graphics.drawImage(bannedBackground, (int) upperLeft.getX(), (int) upperLeft.getY(),
          (int) Math.ceil(width), (int) Math.ceil(height), null);
    } catch (IOException e) {
      logger.warn("Banned background {} not found");
    }
  }
}