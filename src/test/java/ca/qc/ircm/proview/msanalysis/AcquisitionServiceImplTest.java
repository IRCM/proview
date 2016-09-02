package ca.qc.ircm.proview.msanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class AcquisitionServiceImplTest {
  private AcquisitionServiceImpl acquisitionServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AuthorizationService authorizationService;

  @Before
  public void beforeTest() {
    acquisitionServiceImpl = new AcquisitionServiceImpl(entityManager, authorizationService);
  }

  @Test
  public void get() {
    Acquisition acquisition = acquisitionServiceImpl.get(1L);

    verify(authorizationService).checkRobotRole();
    assertEquals((Long) 1L, acquisition.getId());
    assertEquals((Long) 1L, acquisition.getSample().getId());
    assertEquals((Long) 1L, acquisition.getContainer().getId());
    assertEquals((Integer) 1, acquisition.getNumberOfAcquisition());
    assertEquals("XL_20100614_02", acquisition.getSampleListName());
    assertEquals("XL_20100614_COU_09", acquisition.getAcquisitionFile());
    assertEquals((Integer) 1, acquisition.getPosition());
    assertEquals((Integer) 1, acquisition.getListIndex());
    assertEquals(null, acquisition.getComments());
  }

  @Test
  public void get_Null() {
    Acquisition acquisition = acquisitionServiceImpl.get(null);

    assertNull(acquisition);
  }
}
