package ca.qc.ircm.proview.treatment;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ProtocolActivityServiceImpl implements ProtocolActivityService {
  @Inject
  private AuthorizationService authorizationService;

  protected ProtocolActivityServiceImpl() {
  }

  protected ProtocolActivityServiceImpl(AuthorizationService authorizationService) {
    this.authorizationService = authorizationService;
  }

  @Override
  public Activity insert(final Protocol protocol) {
    User user = authorizationService.getCurrentUser();

    Activity activity = new Activity();
    activity.setActionType(ActionType.INSERT);
    activity.setRecordId(protocol.getId());
    activity.setUser(user);
    activity.setTableName("protocol");
    activity.setJustification(null);
    activity.setUpdates(null);
    return activity;
  }
}
