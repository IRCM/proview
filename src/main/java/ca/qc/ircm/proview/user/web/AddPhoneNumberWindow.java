package ca.qc.ircm.proview.user.web;

import com.vaadin.ui.Window;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Add phone number form.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AddPhoneNumberWindow extends Window {
  private static final long serialVersionUID = -2176758855631601065L;
  private AddPhoneNumberForm view = new AddPhoneNumberForm();
  @Inject
  private AddPhoneNumberFormPresenter presenter;

  @PostConstruct
  protected void init() {
    presenter.init(view);
    setContent(view);
    presenter.addCancelClickListener(e -> close());
  }

  @Override
  public void attach() {
    super.attach();
    setCaption(view.getResources().message(AddPhoneNumberFormPresenter.HEADER_PROPERTY));
  }

  public AddPhoneNumberFormPresenter getPresenter() {
    return presenter;
  }
}
