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

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SampleContainerTest {
  @PersistenceContext
  private EntityManager entityManager;

  @Test(expected = OptimisticLockException.class)
  public void update_FailVersion() throws Throwable {
    Sample sample1 = entityManager.find(Sample.class, 442L);
    Sample sample2 = entityManager.find(Sample.class, 443L);
    SampleContainer sampleContainer1 = entityManager.find(SampleContainer.class, 130L);
    entityManager.detach(sampleContainer1);
    SampleContainer sampleContainer2 = entityManager.find(SampleContainer.class, 130L);
    entityManager.detach(sampleContainer2);
    sampleContainer1.setSample(sample1);
    sampleContainer2.setSample(sample2);
    entityManager.merge(sampleContainer1);
    entityManager.flush();
    entityManager.merge(sampleContainer2);
  }
}
