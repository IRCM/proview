package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Sample selection window.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SampleSelectionWindow extends Window implements BaseComponent {
  public static final String WINDOW_STYLE = "samples-selection-window";
  public static final String TITLE = "title";
  private static final long serialVersionUID = 988315877226604037L;
  private static final Logger logger = LoggerFactory.getLogger(SampleSelectionWindow.class);
  private SampleSelectionForm view = new SampleSelectionForm();
  private Panel panel;
  @Inject
  private SampleSelectionFormPresenter presenter;

  @PostConstruct
  protected void init() {
    view.setPresenter(presenter);
    addStyleName(WINDOW_STYLE);
    panel = new Panel();
    setContent(panel);
    panel.setContent(view);
    view.setMargin(true);
    setHeight("700px");
    setWidth("1200px");
    panel.setSizeFull();
  }

  @Override
  public void attach() {
    super.attach();
    logger.debug("Sample selection window");
    setCaption(getResources().message(TITLE));
  }

  public List<Sample> getSelectedSamples() {
    return presenter.getSelectedSamples();
  }

  public void setSelectedSamples(List<Sample> samples) {
    presenter.setSelectedSamples(samples);
  }

  public ObjectProperty<List<Sample>> selectedSamplesProperty() {
    return presenter.selectedSamplesProperty();
  }
}
