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

package ca.qc.ircm.proview.test.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Additional functions for service testing.
 */
public abstract class AbstractServiceTestCase {
  @PersistenceContext
  private EntityManager entityManager;

  protected void detach(Object... entities) {
    for (Object entity : entities) {
      entityManager.detach(entity);
    }
  }

  protected void refresh(Object... entities) {
    for (Object entity : entities) {
      entityManager.refresh(entity);
    }
  }
}
