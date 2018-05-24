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

import ca.qc.ircm.utils.MessageResource;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Available protein identifications.
 */
public enum ProteinIdentification {
  REFSEQ(true), UNIPROT(true), NCBINR(false), MSDB_ID(false), OTHER(true);

  public final boolean available;

  ProteinIdentification(boolean available) {
    this.available = available;
  }

  public static List<ProteinIdentification> availables() {
    return Stream.of(ProteinIdentification.values())
        .filter(identification -> identification.available).collect(Collectors.toList());
  }

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(ProteinIdentification.class, locale);
  }

  public static String getNullLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message("NULL");
  }

  public String getLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message(name());
  }
}
