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

import ca.qc.ircm.utils.MessageResource;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Instruments available for protein mass detection.
 */
public enum MassDetectionInstrument {
  NULL(true, false), VELOS(true), Q_EXACTIVE(true), TSQ_VANTAGE, ORBITRAP_FUSION(true),
  LTQ_ORBI_TRAP, Q_TOF, TOF;

  public final boolean userChoice;
  public final boolean platformChoice;

  MassDetectionInstrument() {
    this(false, false);
  }

  MassDetectionInstrument(boolean available) {
    this(available, available);
  }

  MassDetectionInstrument(boolean userChoice, boolean platformChoice) {
    this.userChoice = userChoice;
    this.platformChoice = platformChoice;
  }

  public static List<MassDetectionInstrument> userChoices() {
    return Stream.of(MassDetectionInstrument.values()).filter(instrument -> instrument.userChoice)
        .collect(Collectors.toList());
  }

  public static List<MassDetectionInstrument> platformChoices() {
    return Stream.of(MassDetectionInstrument.values())
        .filter(instrument -> instrument.platformChoice).collect(Collectors.toList());
  }

  public static String getNullLabel(Locale locale) {
    return NULL.getLabel(locale);
  }

  private MessageResource getResources(Locale locale) {
    return new MessageResource(MassDetectionInstrument.class, locale);
  }

  public String getLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message(name());
  }
}
