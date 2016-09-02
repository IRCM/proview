package ca.qc.ircm.proview.treatment;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.digestion.DigestionProtocol;
import ca.qc.ircm.proview.enrichment.EnrichmentProtocol;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.LogTestUtils;
import ca.qc.ircm.proview.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ProtocolActivityServiceImplTest {
  private ProtocolActivityServiceImpl protocolActivityServiceImpl;
  @Mock
  private AuthorizationService authorizationService;
  private User user;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    protocolActivityServiceImpl = new ProtocolActivityServiceImpl(authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void insert_DigestionProtocol() {
    DigestionProtocol protocol = new DigestionProtocol();
    protocol.setId(123456L);
    protocol.setName("unit_test_digestion_protocol");

    Activity activity = protocolActivityServiceImpl.insert(protocol);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("protocol", activity.getTableName());
    assertEquals(protocol.getId(), activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void insert_EnrichmentProtocol() {
    EnrichmentProtocol protocol = new EnrichmentProtocol();
    protocol.setId(123456L);
    protocol.setName("unit_test_digestion_protocol");

    Activity activity = protocolActivityServiceImpl.insert(protocol);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("protocol", activity.getTableName());
    assertEquals(protocol.getId(), activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }
}
