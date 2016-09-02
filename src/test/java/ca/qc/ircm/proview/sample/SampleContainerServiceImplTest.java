package ca.qc.ircm.proview.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SampleContainerServiceImplTest {
  private SampleContainerServiceImpl sampleContainerServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private AuthorizationService authorizationService;

  @Before
  public void beforeTest() {
    sampleContainerServiceImpl =
        new SampleContainerServiceImpl(entityManager, queryFactory, authorizationService);
  }

  private <D extends Data> D find(Collection<D> datas, long id) {
    for (D data : datas) {
      if (data.getId() == id) {
        return data;
      }
    }
    return null;
  }

  @Test
  public void get_Id() throws Throwable {
    SampleContainer container = sampleContainerServiceImpl.get(1L);

    verify(authorizationService).checkSampleReadPermission(container.getSample());
    assertEquals((Long) 1L, container.getId());
    assertEquals((Long) 1L, container.getSample().getId());
    assertEquals(null, container.getTreatmentSample());
    assertEquals(SampleContainer.Type.TUBE, container.getType());
    assertEquals(
        LocalDateTime.of(2010, 10, 15, 10, 44, 27, 0).atZone(ZoneId.systemDefault()).toInstant(),
        container.getTimestamp());
  }

  @Test
  public void get_NullId() throws Throwable {
    SampleContainer container = sampleContainerServiceImpl.get((Long) null);

    assertNull(container);
  }

  @Test
  public void last() throws Throwable {
    Sample sample = new GelSample(1L);

    SampleContainer container = sampleContainerServiceImpl.last(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals((Long) 129L, container.getId());
    assertEquals((Long) 1L, container.getSample().getId());
    assertEquals((Long) 9L, container.getTreatmentSample().getId());
    assertEquals(SampleContainer.Type.SPOT, container.getType());
    assertEquals(
        LocalDateTime.of(2011, 11, 16, 15, 7, 34, 0).atZone(ZoneId.systemDefault()).toInstant(),
        container.getTimestamp());
  }

  @Test
  public void last_Null() throws Throwable {
    SampleContainer container = sampleContainerServiceImpl.last(null);

    assertNull(container);
  }

  @Test
  public void all() throws Throwable {
    Sample sample = new GelSample(1L);

    List<SampleContainer> containers = sampleContainerServiceImpl.all(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(5, containers.size());
    assertNotNull(find(containers, 1L));
    assertNotNull(find(containers, 6L));
    assertNotNull(find(containers, 7L));
    assertNotNull(find(containers, 128L));
    assertNotNull(find(containers, 129L));
  }

  @Test
  public void all_Null() throws Throwable {
    List<SampleContainer> containers = sampleContainerServiceImpl.all(null);

    assertEquals(0, containers.size());
  }
}
