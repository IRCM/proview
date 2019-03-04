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

package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.treatment.QTreatedSample.treatedSample;

import ca.qc.ircm.proview.fractionation.Fractionation;
import ca.qc.ircm.proview.msanalysis.AcquisitionRepository;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.user.User;
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

/**
 * Make some methods available to log treatment service.
 */
public abstract class BaseTreatmentService {
  @Inject
  private TreatedSampleRepository treatedSampleRepository;
  @Inject
  private AcquisitionRepository acquisitionRepository;

  protected BaseTreatmentService() {
  }

  protected BaseTreatmentService(TreatedSampleRepository treatedSampleRepository,
      AcquisitionRepository acquisitionRepository) {
    this.treatedSampleRepository = treatedSampleRepository;
    this.acquisitionRepository = acquisitionRepository;
  }

  protected void chechSameUserForAllSamples(Treatment treatment) throws IllegalArgumentException {
    User expectedUser = null;
    for (TreatedSample ts : treatment.getTreatedSamples()) {
      if (ts.getSample() instanceof SubmissionSample) {
        SubmissionSample sample = (SubmissionSample) ts.getSample();
        if (expectedUser == null) {
          expectedUser = sample.getUser();
        } else if (!expectedUser.getId().equals(sample.getUser().getId())) {
          throw new IllegalArgumentException(
              "Cannot add treatment with samples from multiple users");
        }
      }
    }
  }

  protected boolean containerUsedByTreatmentOrAnalysis(SampleContainer source) {
    return containerUsedByTreatment(source) | containerUsedByAnalysis(source);
  }

  private boolean containerUsedByTreatment(SampleContainer source) {
    BooleanExpression predicate =
        treatedSample.container.eq(source).and(treatedSample.treatment.deleted.eq(false));
    return treatedSampleRepository.count(predicate) > 0;
  }

  private boolean containerUsedByAnalysis(SampleContainer source) {
    BooleanExpression predicate =
        acquisition.container.eq(source).and(acquisition.msAnalysis.deleted.eq(false));
    return acquisitionRepository.count(predicate) > 0;
  }

  protected void banDestinations(SampleContainer source,
      Collection<SampleContainer> bannedContainers) {
    // Ban destination for transfers.
    List<TreatedSample> treatedSamples = selectTransfersBySource(source);
    for (TreatedSample treatedSample : treatedSamples) {
      SampleContainer destination = treatedSample.getDestinationContainer();
      if (destination.getSample() != null
          && source.getSample().getId().equals(destination.getSample().getId())) {
        // Failsafe: skip if destination is already in containers.
        if (bannedContainers.add(destination)) {
          destination.setBanned(true);

          this.banDestinations(destination, bannedContainers);
        }
      }
    }

    // Ban destinations for fractionation
    List<TreatedSample> fractions = selectFractionationsBySource(source);
    for (TreatedSample treatedSample : fractions) {
      SampleContainer destination = treatedSample.getDestinationContainer();
      if (destination.getSample() != null
          && source.getSample().getId().equals(destination.getSample().getId())) {
        // Failsafe: skip if destination is already in containers.
        if (bannedContainers.add(destination)) {
          destination.setBanned(true);

          this.banDestinations(destination, bannedContainers);
        }
      }
    }
  }

  private List<TreatedSample> selectTransfersBySource(SampleContainer source) {
    BooleanExpression predicate =
        treatedSample.container.eq(source).and(treatedSample.treatment.instanceOf(Transfer.class));
    return Lists.newArrayList(treatedSampleRepository.findAll(predicate));
  }

  private List<TreatedSample> selectFractionationsBySource(SampleContainer source) {
    BooleanExpression predicate = treatedSample.container.eq(source)
        .and(treatedSample.treatment.instanceOf(Fractionation.class));
    return Lists.newArrayList(treatedSampleRepository.findAll(predicate));
  }
}
