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

package ca.qc.ircm.proview.msanalysis;

import ca.qc.ircm.text.MessageResource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Instruments available for protein mass detection.
 */
public enum MassDetectionInstrument {
  NULL, VELOS, Q_EXACTIVE, TSQ_VANTAGE, ORBITRAP_FUSION, LTQ_ORBI_TRAP, Q_TOF, TOF;

  private static final List<MassDetectionInstrument> USER_CHOICES;
  private static final List<MassDetectionInstrument> PLATFORM_CHOICES;
  private static final List<MassDetectionInstrument> FILTER_CHOICES;
  static {
    List<MassDetectionInstrument> choices = new ArrayList<>();
    choices.add(VELOS);
    choices.add(Q_EXACTIVE);
    choices.add(ORBITRAP_FUSION);
    PLATFORM_CHOICES = Collections.unmodifiableList(new ArrayList<>(choices));
    choices.add(0, NULL);
    USER_CHOICES = Collections.unmodifiableList(new ArrayList<>(choices));
    FILTER_CHOICES = Collections.unmodifiableList(new ArrayList<>(choices));
  }

  public final boolean available;

  MassDetectionInstrument() {
    this(false);
  }

  MassDetectionInstrument(boolean available) {
    this.available = available;
  }

  public static List<MassDetectionInstrument> userChoices() {
    return new ArrayList<>(USER_CHOICES);
  }

  public static List<MassDetectionInstrument> platformChoices() {
    return new ArrayList<>(PLATFORM_CHOICES);
  }

  public static List<MassDetectionInstrument> filterChoices() {
    return new ArrayList<>(FILTER_CHOICES);
  }

  public static String getNullLabel(Locale locale) {
    return NULL.getLabel(locale);
  }

  public boolean isAvailable() {
    return USER_CHOICES.contains(this);
  }

  private MessageResource getResources(Locale locale) {
    return new MessageResource(MassDetectionInstrument.class, locale);
  }

  public String getLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message(name());
  }
}
