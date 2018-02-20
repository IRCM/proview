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

package ca.qc.ircm.proview.persistence;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class InstantConverterTest {
  private InstantConverter instantConverter;

  @Before
  public void beforeTest() {
    instantConverter = new InstantConverter();
  }

  @Test
  public void convertToDatabaseColumn() {
    Instant now = Instant.now();
    Instant oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS);
    assertEquals(Timestamp.from(now), instantConverter.convertToDatabaseColumn(now));
    assertEquals(Timestamp.from(oneDayAgo), instantConverter.convertToDatabaseColumn(oneDayAgo));
  }

  @Test
  public void convertToEntityAttribute() {
    Instant now = Instant.now();
    Instant oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS);
    assertEquals(now, instantConverter.convertToEntityAttribute(Timestamp.from(now)));
    assertEquals(oneDayAgo, instantConverter.convertToEntityAttribute(Timestamp.from(oneDayAgo)));
  }
}
