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

package ca.qc.ircm.proview.sample;

import static jakarta.persistence.EnumType.STRING;

import ca.qc.ircm.processing.GeneratePropertyNames;
import ca.qc.ircm.proview.Named;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;

/**
 * Control samples.
 */
@Entity
@DiscriminatorValue("CONTROL")
@GeneratePropertyNames
public class Control extends Sample implements Named {
  private static final long serialVersionUID = 5008215649619278441L;

  /**
   * Control type.
   */
  @Column(nullable = false)
  @Enumerated(STRING)
  private ControlType controlType;

  public Control() {
  }

  public Control(Long id) {
    setId(id);
  }

  public Control(Long id, String name) {
    setId(id);
    setName(name);
  }

  @Override
  public Category getCategory() {
    return Sample.Category.CONTROL;
  }

  public void setControlType(ControlType controlType) {
    this.controlType = controlType;
  }

  public ControlType getControlType() {
    return controlType;
  }
}
