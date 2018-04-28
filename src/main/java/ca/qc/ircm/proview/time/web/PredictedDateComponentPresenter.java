package ca.qc.ircm.proview.time.web;

import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;

import ca.qc.ircm.proview.time.PredictedDate;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Binder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Expected date component presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PredictedDateComponentPresenter {
  public static final String PREDICTED = "predicted";
  public static final String DATE = "date";
  public static final String DESCRIPTION = "description";
  private PredictedDateComponent view;
  private PredictedDateComponentDesign design;
  private Binder<PredictedDate> binder = new Binder<>(PredictedDate.class);

  /**
   * Initializes presenter.
   */
  public void init(PredictedDateComponent view) {
    this.view = view;
    design = view.design;
    prepareComponents();
    if (binder.getBean() == null) {
      binder.setBean(new PredictedDate());
    }
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    design.predicted.addStyleName(PREDICTED);
    design.predicted.setDescription(resources.message(property(PREDICTED, DESCRIPTION)));
    design.predicted.addValueChangeListener(event -> view.fireValueChangeEvent(
        new PredictedDate(design.date.getValue(), event.getOldValue()), event.isUserOriginated()));
    binder.forField(design.predicted).bind(ed -> ed.expected, (ed, value) -> ed.expected = value);
    design.date.addStyleName(DATE);
    design.date.addValueChangeListener(event -> view.fireValueChangeEvent(
        new PredictedDate(event.getOldValue(), design.predicted.getValue()),
        event.isUserOriginated()));
    binder.forField(design.date).bind(ed -> ed.date, (ed, value) -> ed.date = value);
  }

  PredictedDate getValue() {
    return binder.getBean();
  }

  void setValue(PredictedDate value) {
    binder.setBean(value != null ? value : new PredictedDate());
  }
}
