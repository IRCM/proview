package ca.qc.ircm.proview.time.web;

import ca.qc.ircm.proview.time.PredictedDate;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.data.HasValue;
import com.vaadin.shared.Registration;
import com.vaadin.ui.CustomComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Expected date component.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PredictedDateComponent extends CustomComponent
    implements BaseComponent, HasValue<PredictedDate> {
  private static final long serialVersionUID = -3045547104643681748L;
  protected PredictedDateComponentDesign design = new PredictedDateComponentDesign();
  @Inject
  private PredictedDateComponentPresenter presenter;

  protected PredictedDateComponent() {
  }

  protected PredictedDateComponent(PredictedDateComponentPresenter presenter) {
    this.presenter = presenter;
  }

  /**
   * Initializes component.
   */
  @PostConstruct
  public void init() {
    setCompositionRoot(design);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  @Override
  public boolean isReadOnly() {
    return design.date.isReadOnly();
  }

  @Override
  public void setReadOnly(boolean readOnly) {
    design.date.setReadOnly(readOnly);
    design.predicted.setReadOnly(readOnly);
  }

  @Override
  public boolean isRequiredIndicatorVisible() {
    return design.date.isRequiredIndicatorVisible();
  }

  @Override
  public void setRequiredIndicatorVisible(boolean visible) {
    design.date.setRequiredIndicatorVisible(visible);
  }

  @Override
  public PredictedDate getValue() {
    return presenter.getValue();
  }

  @Override
  public void setValue(PredictedDate value) {
    presenter.setValue(value);
  }

  void fireValueChangeEvent(PredictedDate oldPredictedDate, boolean userOriginated) {
    super.fireEvent(new ValueChangeEvent<>(this, oldPredictedDate, userOriginated));
  }

  @Override
  @SuppressWarnings("deprecation")
  public Registration addValueChangeListener(ValueChangeListener<PredictedDate> listener) {
    return this.addListener(ValueChangeEvent.class, listener,
        ValueChangeListener.VALUE_CHANGE_METHOD);
  }
}
