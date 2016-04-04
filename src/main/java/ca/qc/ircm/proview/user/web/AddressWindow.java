package ca.qc.ircm.proview.user.web;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Window;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Address window.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AddressWindow extends Window {
  private static final long serialVersionUID = 9032686080431923743L;
  private AddressForm view = new AddressForm();
  @Inject
  private AddressFormPresenter presenter;

  @PostConstruct
  protected void init() {
    presenter.init(view);
    setContent(view);
    view.setMargin(new MarginInfo(false, true, true, true));
    presenter.addCancelClickListener(e -> close());
  }

  public AddressFormPresenter getPresenter() {
    return presenter;
  }
}
