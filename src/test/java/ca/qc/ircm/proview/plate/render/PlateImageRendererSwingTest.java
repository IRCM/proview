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

import static ca.qc.ircm.proview.plate.render.PlateImageRendererSwing.HEIGHT;
import static ca.qc.ircm.proview.plate.render.PlateImageRendererSwing.IMAGE_TYPE;
import static ca.qc.ircm.proview.plate.render.PlateImageRendererSwing.WIDTH;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateImageRendererSwingTest {
  private PlateImageRendererSwing renderer;
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    renderer = new PlateImageRendererSwing();
  }

  @Test
  public void panel() throws Throwable {
    Plate plate = entityManager.find(Plate.class, 26L);
    plate.well(0, 1).setBanned(true);

    JPanel panel = renderer.panel(plate, Locale.CANADA);

    assertTrue(panel.getLayout() instanceof BorderLayout);
    BorderLayout borderLayout = (BorderLayout) panel.getLayout();
    assertEquals(2, panel.getComponentCount());
    assertTrue(panel.getComponent(0) instanceof JLabel);
    JLabel plateHeader = (JLabel) panel.getComponent(0);
    assertEquals(plate.getName(), plateHeader.getText());
    assertEquals(Color.WHITE, plateHeader.getBackground());
    assertTrue(plateHeader.isOpaque());
    assertEquals(BorderLayout.NORTH, borderLayout.getConstraints(plateHeader));
    assertTrue(panel.getComponent(1) instanceof JPanel);
    JPanel wellsPanel = (JPanel) panel.getComponent(1);
    assertEquals(BorderLayout.CENTER, borderLayout.getConstraints(wellsPanel));
    assertTrue(wellsPanel.getLayout() instanceof GridBagLayout);
    assertEquals(Color.WHITE, wellsPanel.getBackground());
    GridBagLayout gridBagLayout = (GridBagLayout) wellsPanel.getLayout();
    assertEquals((plate.getColumnCount() + 1) * (plate.getRowCount() + 1),
        wellsPanel.getComponentCount());
    Color labelDefaultColor = new JLabel().getForeground();
    for (Component wellComponent : wellsPanel.getComponents()) {
      assertTrue(wellComponent instanceof JLabel);
      JLabel wellLabel = (JLabel) wellComponent;
      GridBagConstraints contraints = gridBagLayout.getConstraints(wellLabel);
      assertEquals(GridBagConstraints.BOTH, contraints.fill);
      assertEquals(SwingConstants.CENTER, wellLabel.getHorizontalAlignment());
      assertTrue(wellLabel.isOpaque());
      if (contraints.gridx == 0 && contraints.gridy == 0) {
        assertEquals(Color.WHITE, wellLabel.getBackground());
        assertEquals("", wellLabel.getText());
      } else if (contraints.gridy == 0) {
        assertEquals(Color.WHITE, wellLabel.getBackground());
        assertEquals(Plate.columnLabel(contraints.gridx - 1), wellLabel.getText());
      } else if (contraints.gridx == 0) {
        assertEquals(Color.WHITE, wellLabel.getBackground());
        assertEquals(Plate.rowLabel(contraints.gridy - 1), wellLabel.getText());
      } else {
        Well well = plate.well(contraints.gridy - 1, contraints.gridx - 1);
        assertEquals(well.isBanned() ? Color.RED : Color.WHITE, wellLabel.getBackground());
        assertEquals(well.isBanned() ? Color.WHITE : labelDefaultColor, wellLabel.getForeground());
        assertEquals(well.getSample() != null ? well.getSample().getName() : "",
            wellLabel.getText());
      }
    }
  }

  @Test
  public void panel_Null() throws Throwable {
    JPanel panel = renderer.panel(null, Locale.CANADA);

    Plate plate = new Plate();
    plate.initWells();
    assertTrue(panel.getLayout() instanceof BorderLayout);
    BorderLayout borderLayout = (BorderLayout) panel.getLayout();
    assertEquals(2, panel.getComponentCount());
    assertTrue(panel.getComponent(0) instanceof JLabel);
    JLabel plateHeader = (JLabel) panel.getComponent(0);
    assertEquals("", plateHeader.getText());
    assertEquals(Color.WHITE, plateHeader.getBackground());
    assertTrue(plateHeader.isOpaque());
    assertEquals(BorderLayout.NORTH, borderLayout.getConstraints(plateHeader));
    assertTrue(panel.getComponent(1) instanceof JPanel);
    JPanel wellsPanel = (JPanel) panel.getComponent(1);
    assertEquals(BorderLayout.CENTER, borderLayout.getConstraints(wellsPanel));
    assertTrue(wellsPanel.getLayout() instanceof GridBagLayout);
    assertEquals(Color.WHITE, wellsPanel.getBackground());
    GridBagLayout gridBagLayout = (GridBagLayout) wellsPanel.getLayout();
    assertEquals((plate.getColumnCount() + 1) * (plate.getRowCount() + 1),
        wellsPanel.getComponentCount());
    Color labelDefaultColor = new JLabel().getForeground();
    for (Component wellComponent : wellsPanel.getComponents()) {
      assertTrue(wellComponent instanceof JLabel);
      JLabel wellLabel = (JLabel) wellComponent;
      GridBagConstraints contraints = gridBagLayout.getConstraints(wellLabel);
      assertEquals(GridBagConstraints.BOTH, contraints.fill);
      assertEquals(SwingConstants.CENTER, wellLabel.getHorizontalAlignment());
      assertTrue(wellLabel.isOpaque());
      if (contraints.gridx == 0 && contraints.gridy == 0) {
        assertEquals(Color.WHITE, wellLabel.getBackground());
        assertEquals("", wellLabel.getText());
      } else if (contraints.gridy == 0) {
        assertEquals(Color.WHITE, wellLabel.getBackground());
        assertEquals(Plate.columnLabel(contraints.gridx - 1), wellLabel.getText());
      } else if (contraints.gridx == 0) {
        assertEquals(Color.WHITE, wellLabel.getBackground());
        assertEquals(Plate.rowLabel(contraints.gridy - 1), wellLabel.getText());
      } else {
        Well well = plate.well(contraints.gridy - 1, contraints.gridx - 1);
        assertEquals(well.isBanned() ? Color.RED : Color.WHITE, wellLabel.getBackground());
        assertEquals(well.isBanned() ? Color.WHITE : labelDefaultColor, wellLabel.getForeground());
        assertEquals(well.getSample() != null ? well.getSample().getName() : "",
            wellLabel.getText());
      }
    }
  }

  @Test
  public void render() throws Throwable {
    Plate plate = entityManager.find(Plate.class, 26L);
    plate.well(0, 1).setBanned(true);

    byte[] image = renderer.render(plate, Locale.CANADA, "png");

    JPanel panel = renderer.panel(plate, Locale.CANADA);
    Dimension dimension = new Dimension(WIDTH, HEIGHT);
    panel.setSize(dimension);
    panel.setPreferredSize(dimension);
    doLayout(panel);
    BufferedImage expectedImage = new BufferedImage(dimension.width, dimension.height, IMAGE_TYPE);
    Graphics graphics = expectedImage.getGraphics();
    panel.printAll(graphics);
    graphics.dispose();
    ByteArrayOutputStream expectedOutput = new ByteArrayOutputStream();
    ImageIO.write(expectedImage, "png", expectedOutput);
    assertArrayEquals(expectedOutput.toByteArray(), image);
  }

  private void doLayout(java.awt.Component component) {
    component.doLayout();
    if (component instanceof Container) {
      Container container = (Container) component;
      for (java.awt.Component child : container.getComponents()) {
        doLayout(child);
      }
    }
  }
}
