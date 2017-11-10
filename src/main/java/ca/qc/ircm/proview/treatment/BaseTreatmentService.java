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

import static ca.qc.ircm.proview.fractionation.QFraction.fraction;
import static ca.qc.ircm.proview.fractionation.QFractionation.fractionation;
import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.transfer.QTransfer.transfer;
import static ca.qc.ircm.proview.transfer.QTransferedSample.transferedSample;
import static ca.qc.ircm.proview.treatment.QTreatment.treatment;
import static ca.qc.ircm.proview.treatment.QTreatmentSample.treatmentSample;

import ca.qc.ircm.proview.fractionation.Fraction;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.transfer.TransferedSample;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Make some methods available to log treatment service.
 */
public abstract class BaseTreatmentService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;

  protected BaseTreatmentService() {
  }

  protected BaseTreatmentService(EntityManager entityManager, JPAQueryFactory queryFactory) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
  }

  protected void chechSameUserForAllSamples(Treatment<?> treatment)
      throws IllegalArgumentException {
    User expectedUser = null;
    for (TreatmentSample ts : treatment.getTreatmentSamples()) {
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
    JPAQuery<Long> query = queryFactory.select(treatment.id);
    query.from(treatment, treatmentSample);
    query.where(treatmentSample.in(treatment.treatmentSamples));
    query.where(treatmentSample.container.eq(source));
    query.where(treatment.deleted.eq(false));
    return query.fetchCount() > 0;
  }

  private boolean containerUsedByAnalysis(SampleContainer source) {
    JPAQuery<Long> query = queryFactory.select(msAnalysis.id);
    query.from(msAnalysis, acquisition);
    query.where(acquisition.in(msAnalysis.acquisitions));
    query.where(acquisition.container.eq(source));
    query.where(msAnalysis.deleted.eq(false));
    return query.fetchCount() > 0;
  }

  protected void banDestinations(SampleContainer source,
      Collection<SampleContainer> bannedContainers) {
    // Ban destination for transfers.
    List<TransferedSample> transferedSamples = selectTransfersBySource(source);
    for (TransferedSample transferedSample : transferedSamples) {
      SampleContainer destination = transferedSample.getDestinationContainer();
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
    List<Fraction> fractions = selectFractionationsBySource(source);
    for (Fraction fraction : fractions) {
      SampleContainer destination = fraction.getDestinationContainer();
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

  private List<TransferedSample> selectTransfersBySource(SampleContainer source) {
    JPAQuery<TransferedSample> query = queryFactory.select(transferedSample);
    query.from(transfer, transferedSample);
    query.where(transferedSample._super.in(transfer.treatmentSamples));
    query.where(transferedSample.container.eq(source));
    return query.fetch();
  }

  private List<Fraction> selectFractionationsBySource(SampleContainer source) {
    JPAQuery<Fraction> query = queryFactory.select(fraction);
    query.from(fractionation, fraction);
    query.where(fraction._super.in(fractionation.treatmentSamples));
    query.where(fraction.container.eq(source));
    return query.fetch();
  }
}
