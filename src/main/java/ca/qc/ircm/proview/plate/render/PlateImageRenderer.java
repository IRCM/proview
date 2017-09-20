/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.plate.render;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;
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
  protected static final String BANNED_BACKGROUND_RESOURCE = "/banned_well_background.png";
  /**
   * Width of a lines shown to indicate that well is banned.
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
   *          plate to render with it's wells
   * @param info
   *          used to get localised plate information
   */
  public PlateImageRenderer(Plate plate, LocalizedPlate info) {
    this.plate = plate;
    this.wells = plate.getWells();
    this.info = info;
    this.headerFont = new Font("SansSerif", Font.BOLD, 18);
    this.wellIdFont = new Font("SansSerif", Font.PLAIN, 9);
    this.wellFont = new Font("SansSerif", Font.PLAIN, 10);
    this.graphics = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED).createGraphics();
  }

  /**
   * Plate used in PDF.
   */
  private final Plate plate;
  /**
   * Plate's wells.
   */
  private final List<Well> wells;
  /**
   * To get specific strings to write in PDF.
   */
  private final LocalizedPlate info;
  /**
   * Font to be used for header.
   */
  private final Font headerFont;
  /**
   * Font to be used for well id.
   */
  private final Font wellIdFont;
  /**
   * Font to be used for well content.
   */
  private final Font wellFont;
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
   * Returns font to use for well id.
   *
   * @return font to use for well id
   */
  protected Font getWellIdFont() {
    return wellIdFont;
  }

  /**
   * Returns font to use for sample id and name.
   *
   * @return Font to use for sample.
   */
  protected Font getSampleFont() {
    return wellFont;
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
    double wellWidth = 0;
    for (int column = 0; column < plate.getColumnCount(); column++) {
      wellWidth += this.getRectangleWidth(column) - LINE_WIDTH;
    }
    wellWidth += LINE_WIDTH;
    return width + Math.max(headerWidth, wellWidth);
  }

  /**
   * Width of rectangles for wells on this column.
   *
   * @param column
   *          Column index.
   * @return Width of rectangles for wells on this column.
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
   * Total height of image containing well.
   *
   * @param row
   *          Row index.
   * @return Total height of row.
   */
  protected double getRectangleHeight(int row) {
    double height = 0;
    int padding = LINE_WIDTH + REC_PADDING;
    height += 2 * padding;

    // Well id height
    height += graphics.getFontMetrics(this.getWellIdFont()).getHeight();

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
    // Compute graphical location of all wells.
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
    // Render wells.
    for (int i = 0; i < wells.size(); i++) {
      Well well = wells.get(i);
      renderWell(well, graphics, xval[well.getColumn()], yval[well.getRow()]);
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
   * Render well.
   *
   * @param well
   *          well to render
   * @param graphics
   *          graphic where to write well
   * @param xval
   *          X position of well
   * @param yval
   *          Y position of well
   */
  protected void renderWell(Well well, Graphics2D graphics, double xval, double yval) {
    Point2D borderUpperLeft = new Point2D.Double(xval, yval);
    Point2D borderBottomRight = new Point2D.Double(xval + this.getRectangleWidth(well.getColumn()),
        yval + this.getRectangleHeight(well.getRow()));
    if (well.isBanned()) {
      renderBanned(well, graphics, borderUpperLeft, borderBottomRight);
    }

    // Total padding in cell.
    int padding = LINE_WIDTH + REC_PADDING;

    // Well information location.
    Point2D upperLeft = new Point2D.Double(xval + padding, yval + padding);
    Point2D bottomRight =
        new Point2D.Double(xval + this.getRectangleWidth(well.getColumn()) - padding,
            yval + this.getRectangleHeight(well.getRow()) - padding);

    // Draw borders around well.
    renderBorder(graphics, borderUpperLeft, borderBottomRight);

    // Draw well content.
    upperLeft = renderWellName(well, graphics, upperLeft);
    if (well.getSample() != null) {
      renderSampleName(well, graphics, upperLeft, bottomRight);
    }
  }

  /**
   * Write well id in graphic.
   *
   * @param well
   *          well to write
   * @param graphics
   *          graphic where to write well id
   * @param upperLeft
   *          upper left location of text
   * @return upper left location for text to write below well id
   */
  protected Point2D renderWellName(Well well, Graphics2D graphics, Point2D upperLeft) {
    // set font.
    graphics.setFont(this.getWellIdFont());
    // Compute text location from upper left location.
    Point2D wellIdLocation = new Point2D.Double(upperLeft.getX(),
        upperLeft.getY() + graphics.getFontMetrics().getAscent());
    String wellId = info.getWell(well);
    graphics.drawString(wellId, (float) wellIdLocation.getX(), (float) wellIdLocation.getY());

    // Upper left location for text following well id.
    Point2D newUpperLeft = new Point2D.Double(upperLeft.getX(),
        upperLeft.getY() + graphics.getFontMetrics().getHeight());
    return newUpperLeft;
  }

  /**
   * Write well's sample name in graphic.
   *
   * @param well
   *          well
   * @param graphics
   *          Graphic where to write sample name.
   * @param upperLeft
   *          Upper left location of text.
   * @param bottomRight
   *          Bottom right maximum location of text.
   * @return Upper left location for text to write below sample name.
   */
  protected Point2D renderSampleName(Well well, Graphics2D graphics, Point2D upperLeft,
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
    String sampleName = info.getSampleName(well);
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

    // Upper left location for text following well name.
    Point2D newUpperLeft = new Point2D.Double(upperLeft.getX(),
        upperLeft.getY() + lineCount * graphics.getFontMetrics().getHeight());
    return newUpperLeft;
  }

  protected void renderBanned(Well well, Graphics2D graphics, Point2D upperLeft,
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
