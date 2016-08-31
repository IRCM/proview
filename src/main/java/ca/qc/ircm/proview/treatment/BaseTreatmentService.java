package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.fractionation.QFractionation.fractionation;
import static ca.qc.ircm.proview.fractionation.QFractionationDetail.fractionationDetail;
import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.transfer.QSampleTransfer.sampleTransfer;
import static ca.qc.ircm.proview.transfer.QTransfer.transfer;
import static ca.qc.ircm.proview.treatment.QTreatment.treatment;
import static ca.qc.ircm.proview.treatment.QTreatmentSample.treatmentSample;

import ca.qc.ircm.proview.fractionation.FractionationDetail;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.transfer.SampleTransfer;
import ca.qc.ircm.proview.treatment.Treatment.DeletionType;
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
    List<SampleTransfer> sampleTransfers = selectTransfersBySource(source);
    for (SampleTransfer sampleTransfer : sampleTransfers) {
      SampleContainer destination = sampleTransfer.getDestinationContainer();
      // Failsafe: skip if destination is already in containers.
      if (bannedContainers.add(destination)) {
        destination.setBanned(true);

        this.banDestinations(destination, bannedContainers);
      }
    }

    // Ban destinations for fractionation
    List<FractionationDetail> fractionationDetails = selectFractionationsBySource(source);
    for (FractionationDetail fractionationDetail : fractionationDetails) {
      SampleContainer destination = fractionationDetail.getDestinationContainer();
      // Failsafe: skip if destination is already in containers.
      if (bannedContainers.add(destination)) {
        destination.setBanned(true);

        this.banDestinations(destination, bannedContainers);
      }
    }
  }

  private List<SampleTransfer> selectTransfersBySource(SampleContainer source) {
    JPAQuery<SampleTransfer> query = queryFactory.select(sampleTransfer);
    query.from(transfer, sampleTransfer);
    query.where(sampleTransfer._super.in(transfer.treatmentSamples));
    query.where(sampleTransfer.container.eq(source));
    query.where(transfer.deleted.eq(false).or(transfer.deletionType.ne(DeletionType.ERRONEOUS)));
    return query.fetch();
  }

  private List<FractionationDetail> selectFractionationsBySource(SampleContainer source) {
    JPAQuery<FractionationDetail> query = queryFactory.select(fractionationDetail);
    query.from(fractionation, fractionationDetail);
    query.where(fractionationDetail._super.in(fractionation.treatmentSamples));
    query.where(fractionationDetail.container.eq(source));
    query.where(
        fractionation.deleted.eq(false).or(fractionation.deletionType.ne(DeletionType.ERRONEOUS)));
    return query.fetch();
  }
}
