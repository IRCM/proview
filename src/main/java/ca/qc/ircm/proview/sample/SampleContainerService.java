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

import javax.inject.Inject;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for sample containers.
 */
@Service
@Transactional
public class SampleContainerService {
  @Inject
  private SampleContainerRepository repository;

  protected SampleContainerService() {
  }

  /**
   * Selects sample container from database.
   *
   * @param id
   *          database identifier of sample container
   * @return sample container
   */
  @PostAuthorize("returnObject == null || hasPermission(returnObject.sample, 'read')")
  public SampleContainer get(Long id) {
    if (id == null) {
      return null;
    }

    return repository.findOne(id);
  }

  /**
   * Selects last sample container in which sample was.
   *
   * @param sample
   *          sample
   * @return last sample container in which sample was
   */
  @PreAuthorize("hasPermission(#sample, 'read')")
  public SampleContainer last(Sample sample) {
    if (sample == null) {
      return null;
    }

    return repository.findFirstBySampleOrderByTimestampDesc(sample);
  }
}
