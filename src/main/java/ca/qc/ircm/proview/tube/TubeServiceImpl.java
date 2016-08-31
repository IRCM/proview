package ca.qc.ircm.proview.tube;

import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.tube.QTube.tube;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.security.AuthorizationService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service for digestion tubes.
 */
@Service
@Transactional
public class TubeServiceImpl implements TubeService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private AuthorizationService authorizationService;

  protected TubeServiceImpl() {
  }

  protected TubeServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  @Override
  public Tube get(Long id) {
    if (id == null) {
      return null;
    }

    Tube tube = entityManager.find(Tube.class, id);
    if (tube != null) {
      authorizationService.checkSampleReadPermission(tube.getSample());
    }
    return tube;
  }

  @Override
  public Tube get(String name) {
    if (name == null) {
      return null;
    }

    JPAQuery<Tube> query = queryFactory.select(tube);
    query.from(tube);
    query.where(tube.name.eq(name));
    Tube ret = query.fetchOne();
    if (ret != null) {
      authorizationService.checkSampleReadPermission(ret.getSample());
    }
    return ret;
  }

  @Override
  public Tube original(Sample sampleParam) {
    if (sampleParam == null) {
      return null;
    }

    authorizationService.checkSampleReadPermission(sampleParam);
    JPAQuery<Tube> query = queryFactory.select(tube);
    query.from(sample, tube);
    query.where(tube.eq(sample.originalContainer));
    query.where(sample.eq(sampleParam));
    return query.fetchOne();
  }

  @Override
  public Tube last(Sample sample) {
    if (sample == null) {
      return null;
    }
    authorizationService.checkSampleReadPermission(sample);

    JPAQuery<Tube> query = queryFactory.select(tube);
    query.from(tube);
    query.where(tube.sample.eq(sample));
    query.orderBy(tube.timestamp.desc());
    query.limit(1);
    return query.fetchOne();
  }

  @Override
  public List<Tube> all(Sample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }
    authorizationService.checkSampleReadPermission(sample);

    JPAQuery<Tube> query = queryFactory.select(tube);
    query.from(tube);
    query.where(tube.sample.eq(sample));
    return query.fetch();
  }

  @Override
  public List<String> selectNameSuggestion(String beginning) {
    if (beginning == null) {
      return new ArrayList<>();
    }
    authorizationService.checkAdminRole();

    JPAQuery<String> query = queryFactory.select(tube.name);
    query.from(tube);
    query.where(tube.name.startsWith(beginning));
    return query.fetch();
  }

  private boolean exists(String name) {
    JPAQuery<Long> query = queryFactory.select(tube.id);
    query.from(tube);
    query.where(tube.name.eq(name));
    return query.fetchCount() > 0;
  }

  @Override
  public String generateTubeName(Sample sample, Collection<String> excludes) {
    if (sample == null) {
      return null;
    }
    if (excludes == null) {
      excludes = Collections.emptySet();
    }
    authorizationService.checkUserRole();

    if (!exists(sample.getName()) && !excludes.contains(sample.getName())) {
      return sample.getName();
    }

    int index = 0;
    String name = sample.getName() + "_" + ++index;
    while (exists(name) || excludes.contains(name)) {
      name = sample.getName() + "_" + ++index;
    }
    return name;
  }
}
