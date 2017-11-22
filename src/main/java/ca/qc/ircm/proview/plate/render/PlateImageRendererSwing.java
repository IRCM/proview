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
import ca.qc.ircm.proview.sample.Sample;
import org.springframework.stereotype.Component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Renders plate information into an image.
 */
@Component
public class PlateImageRendererSwing implements PlateImageRenderer {
  public static final int WIDTH = 1800;
  public static final int HEIGHT = 600;
  public static final int IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;

  protected JPanel panel(Plate plate, Locale locale) {
    if (plate == null) {
      plate = new Plate();
      plate.setName("");
      plate.initWells();
    }
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(plateHeader(plate.getName()), BorderLayout.NORTH);
    GridBagConstraints constraints = new GridBagConstraints();
    JPanel wellsPanel = new JPanel(new GridBagLayout());
    wellsPanel.setBorder(new LineBorder(Color.BLACK));
    wellsPanel.setBackground(Color.WHITE);
    panel.add(wellsPanel, BorderLayout.CENTER);
    constraints.fill = GridBagConstraints.BOTH;
    {
      constraints.gridx = 0;
      constraints.gridy = 0;
      wellsPanel.add(header(""), constraints);
    }
    for (int column = 1; column < plate.getColumnCount() + 1; column++) {
      constraints.gridx = column;
      constraints.gridy = 0;
      wellsPanel.add(header(Plate.columnLabel(column - 1)), constraints);
    }
    for (int row = 1; row < plate.getRowCount() + 1; row++) {
      constraints.gridx = 0;
      constraints.gridy = row;
      wellsPanel.add(header(Plate.rowLabel(row - 1)), constraints);
    }
    constraints.weightx = 1;
    constraints.weighty = 1;
    for (int row = 1; row < plate.getRowCount() + 1; row++) {
      for (int column = 1; column < plate.getColumnCount() + 1; column++) {
        Well well = plate.well(row - 1, column - 1);
        Sample sample = well.getSample();
        constraints.gridx = column;
        constraints.gridy = row;
        wellsPanel.add(sampleName(sample != null ? sample.getName() : "", well.isBanned()),
            constraints);
      }
    }
    return panel;
  }

  @Override
  public byte[] render(Plate plate, Locale locale, String type) throws IOException {
    JPanel panel = panel(plate, locale);
    Dimension dimension = new Dimension(WIDTH, HEIGHT);
    panel.setSize(dimension);
    panel.setPreferredSize(dimension);
    doLayout(panel);
    BufferedImage image = new BufferedImage(dimension.width, dimension.height, IMAGE_TYPE);
    Graphics graphics = image.getGraphics();
    panel.printAll(graphics);
    graphics.dispose();
    ByteArrayOutputStream expectedOutput = new ByteArrayOutputStream();
    ImageIO.write(image, type, expectedOutput);
    return expectedOutput.toByteArray();
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

  private JLabel plateHeader(String value) {
    JLabel label = label(value);
    label.setFont(new Font(label.getFont().getFamily(), Font.BOLD, 24));
    label.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(3, 3, 3, 3)));
    return label;
  }

  private JLabel header(String value) {
    JLabel label = label(value);
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(3, 3, 3, 3)));
    return label;
  }

  private JLabel sampleName(String value, boolean banned) {
    JLabel label = label(value);
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setBorder(new LineBorder(Color.BLACK));
    if (banned) {
      label.setForeground(Color.WHITE);
      label.setBackground(Color.RED);
    }
    return label;
  }

  private JLabel label(String value) {
    JLabel label = new JLabel(value);
    label.setBackground(Color.WHITE);
    label.setOpaque(true);
    return label;
  }
}
