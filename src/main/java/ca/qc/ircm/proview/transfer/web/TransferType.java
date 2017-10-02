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

package ca.qc.ircm.proview.transfer.web;

import static ca.qc.ircm.proview.sample.SampleContainerType.TUBE;
import static ca.qc.ircm.proview.sample.SampleContainerType.WELL;

import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Types of transfer.
 */
public enum TransferType {
  PLATE_TO_PLATE(WELL, WELL), TUBES_TO_PLATE(TUBE, WELL), TUBES_TO_TUBES(TUBE, TUBE);

  public final SampleContainerType sourceType;
  public final SampleContainerType destinationType;

  TransferType(SampleContainerType sourceType, SampleContainerType destinationType) {
    this.sourceType = sourceType;
    this.destinationType = destinationType;
  }

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(TransferType.class, locale);
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
