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

import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SampleContainerVersionTest extends AbstractServiceTestCase {
  @Inject
  private SampleContainerRepository repository;
  @Inject
  private SampleRepository sampleRepository;

  @Test(expected = JpaOptimisticLockingFailureException.class)
  public void update_FailVersion() throws Throwable {
    final Sample sample1 = sampleRepository.findOne(442L);
    final Sample sample2 = sampleRepository.findOne(443L);
    SampleContainer sampleContainer1 = repository.findOne(130L);
    SampleContainer sampleContainer2 = repository.findOne(130L);
    detach(sampleContainer1, sampleContainer2);
    sampleContainer1.setSample(sample1);
    sampleContainer2.setSample(sample2);
    repository.saveAndFlush(sampleContainer1);
    repository.save(sampleContainer2);
  }
}