package ca.qc.ircm.proview.web.filter;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.component.BaseComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.Instant;

import javax.inject.Inject;

/**
 * Instant filter component.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InstantFilterComponent extends InstantFilterComponentDesign implements BaseComponent {
  private static final long serialVersionUID = -5938290034747610261L;
  @Inject
  private InstantFilterComponentPresenter presenter;

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public void fireSaveEvent(Range<Instant> range) {
    fireEvent(new SaveEvent(this, range));
  }
}
