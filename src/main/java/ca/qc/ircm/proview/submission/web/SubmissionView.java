package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.PRIMARY;
import static ca.qc.ircm.proview.web.WebConstants.SAVE;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Submission view.
 */
@Route(value = SubmissionView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ UserRole.USER })
public class SubmissionView extends VerticalLayout
    implements HasDynamicTitle, HasUrlParameter<Long>, LocaleChangeObserver, NotificationComponent {
  public static final String VIEW_NAME = "submission";
  public static final String ID = styleName(VIEW_NAME, "view");
  public static final String HEADER = "header";
  public static final String SAVED = "saved";
  private static final long serialVersionUID = 7704703308278059432L;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionView.class);
  protected H2 header = new H2();
  protected Tabs service = new Tabs();
  protected Tab lcmsms = new Tab();
  protected Tab smallMolecule = new Tab();
  protected Tab intactProtein = new Tab();
  protected TextArea comment = new TextArea();
  protected Button save = new Button();
  protected LcmsmsSubmissionForm lcmsmsSubmissionForm;
  protected SmallMoleculeSubmissionForm smallMoleculeSubmissionForm;
  protected IntactProteinSubmissionForm intactProteinSubmissionForm;
  private Map<Tab, Component> tabsToComponents = new HashMap<>();
  private SubmissionViewPresenter presenter;

  @Autowired
  protected SubmissionView(SubmissionViewPresenter presenter,
      LcmsmsSubmissionForm lcmsmsSubmissionForm,
      SmallMoleculeSubmissionForm smallMoleculeSubmissionForm,
      IntactProteinSubmissionForm intactProteinSubmissionForm) {
    this.presenter = presenter;
    this.lcmsmsSubmissionForm = lcmsmsSubmissionForm;
    this.smallMoleculeSubmissionForm = smallMoleculeSubmissionForm;
    this.intactProteinSubmissionForm = intactProteinSubmissionForm;
  }

  @PostConstruct
  void init() {
    logger.debug("Submission view");
    setId(ID);
    add(header, service, lcmsmsSubmissionForm, smallMoleculeSubmissionForm,
        intactProteinSubmissionForm, comment, save);
    expand(lcmsmsSubmissionForm, smallMoleculeSubmissionForm, intactProteinSubmissionForm, comment);
    header.setId(HEADER);
    service.setId(SERVICE);
    service.add(lcmsms, smallMolecule, intactProtein);
    service.setSelectedTab(lcmsms);
    tabsToComponents.put(lcmsms, lcmsmsSubmissionForm);
    tabsToComponents.put(smallMolecule, smallMoleculeSubmissionForm);
    tabsToComponents.put(intactProtein, intactProteinSubmissionForm);
    service.addSelectedChangeListener(e -> {
      tabsToComponents.get(e.getPreviousTab()).setVisible(false);
      tabsToComponents.get(e.getSelectedTab()).setVisible(true);
    });
    lcmsms.setId(LC_MS_MS.name());
    smallMolecule.setId(SMALL_MOLECULE.name());
    intactProtein.setId(INTACT_PROTEIN.name());
    smallMoleculeSubmissionForm.setVisible(false);
    intactProteinSubmissionForm.setVisible(false);
    comment.setId(COMMENT);
    comment.setMinHeight("20em");
    comment.setMinWidth("40em");
    save.setId(SAVE);
    save.addThemeName(PRIMARY);
    save.setIcon(VaadinIcon.CHECK.create());
    save.addClickListener(e -> presenter.save(getLocale()));
    presenter.init(this);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final MessageResource resources = new MessageResource(SubmissionView.class, getLocale());
    final MessageResource submissionResources = new MessageResource(Submission.class, getLocale());
    final MessageResource webResources = new MessageResource(WebConstants.class, getLocale());
    header.setText(resources.message(HEADER));
    lcmsms.setLabel(LC_MS_MS.getLabel(getLocale()));
    smallMolecule.setLabel(SMALL_MOLECULE.getLabel(getLocale()));
    intactProtein.setLabel(INTACT_PROTEIN.getLabel(getLocale()));
    comment.setLabel(submissionResources.message(COMMENT));
    save.setText(webResources.message(SAVE));
    presenter.localeChange(getLocale());
  }

  @Override
  public String getPageTitle() {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    final MessageResource generalResources = new MessageResource(WebConstants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
    presenter.setParameter(parameter);
  }
}
