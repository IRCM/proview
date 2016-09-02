package ca.qc.ircm.proview.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class StandardServiceImplTest {
  private StandardServiceImpl standardServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private AuthorizationService authorizationService;
  @Captor
  private ArgumentCaptor<Sample> sampleCaptor;

  @Before
  public void beforeTest() {
    standardServiceImpl =
        new StandardServiceImpl(entityManager, queryFactory, authorizationService);
  }

  @Test
  public void get() {
    Standard standard = standardServiceImpl.get(4L);

    verify(authorizationService).checkSampleReadPermission(sampleCaptor.capture());
    assertEquals((Long) 445L, sampleCaptor.getValue().getId());
    assertEquals((Long) 4L, standard.getId());
    assertEquals("std1", standard.getName());
    assertEquals("2", standard.getQuantity());
    assertEquals(Standard.QuantityUnit.MICRO_GRAMS, standard.getQuantityUnit());
    assertEquals(null, standard.getComments());
  }

  @Test
  public void get_ControlSample() {
    Standard standard = standardServiceImpl.get(6L);

    verify(authorizationService).checkSampleReadPermission(sampleCaptor.capture());
    assertEquals((Long) 448L, sampleCaptor.getValue().getId());
    assertEquals((Long) 6L, standard.getId());
    assertEquals("cap_standard", standard.getName());
    assertEquals("3", standard.getQuantity());
    assertEquals(Standard.QuantityUnit.MICRO_GRAMS, standard.getQuantityUnit());
    assertEquals("some_comments", standard.getComments());
  }

  @Test
  public void get_Null() {
    Standard standard = standardServiceImpl.get(null);

    assertNull(standard);
  }

  @Test
  @Deprecated
  public void all() {
    Sample sample = new EluateSample(445L);

    List<Standard> standards = standardServiceImpl.all(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(1, standards.size());
    Standard standard = standards.get(0);
    assertEquals((Long) 4L, standard.getId());
    assertEquals("std1", standard.getName());
    assertEquals("2", standard.getQuantity());
    assertEquals(Standard.QuantityUnit.MICRO_GRAMS, standard.getQuantityUnit());
    assertEquals(null, standard.getComments());
  }

  @Test
  @Deprecated
  public void all_Control() {
    Sample sample = new Control(448L);

    List<Standard> standards = standardServiceImpl.all(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(1, standards.size());
    Standard standard = standards.get(0);
    assertEquals((Long) 6L, standard.getId());
  }

  @Test
  @Deprecated
  public void all_Null() {
    List<Standard> standards = standardServiceImpl.all(null);

    assertEquals(0, standards.size());
  }
}
