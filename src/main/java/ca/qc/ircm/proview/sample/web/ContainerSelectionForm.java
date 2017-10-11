package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.plate.web.PlateComponent;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.shared.Registration;
import com.vaadin.ui.CustomComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContainerSelectionForm extends CustomComponent implements BaseComponent {
  private static final long serialVersionUID = 7128874097968930586L;
  protected ContainerSelectionFormDesign design = new ContainerSelectionFormDesign();
  @Inject
  protected PlateComponent plateComponent;
  @Inject
  private ContainerSelectionFormPresenter presenter;

  protected ContainerSelectionForm() {
  }

  protected ContainerSelectionForm(ContainerSelectionFormPresenter presenter,
      PlateComponent plateComponent) {
    this.presenter = presenter;
    this.plateComponent = plateComponent;
  }

  @PostConstruct
  public void init() {
    setCompositionRoot(design);
    design.plateLayout.addComponent(plateComponent);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public Registration addSaveListener(SaveListener<List<SampleContainer>> listener) {
    return addListener(SaveEvent.class, listener, SaveListener.SAVED_METHOD);
  }

  protected void fireSaveEvent(List<SampleContainer> containers) {
    fireEvent(new SaveEvent<>(this, containers));
  }

  public List<Sample> getSamples() {
    return presenter.getSamples();
  }

  public void setSamples(List<Sample> samples) {
    presenter.setSamples(samples);
  }
}
