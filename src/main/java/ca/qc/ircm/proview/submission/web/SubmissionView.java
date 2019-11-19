package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.SubmissionFileProperties.FILENAME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FILES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.PRIMARY;
import static ca.qc.ircm.proview.web.WebConstants.SAVE;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;
import static ca.qc.ircm.proview.web.WebConstants.UPLOAD;
import static ca.qc.ircm.proview.web.WebConstants.uploadI18N;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFile;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.ByteArrayStreamResourceWriter;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
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
  public static final String FILES_IOEXCEPTION = property(FILES, "ioexception");
  public static final String FILES_OVER_MAXIMUM = property(FILES, "overmaximum");
  public static final String REMOVE = "remove";
  public static final String SAVED = "saved";
  public static final int MAXIMUM_FILES_SIZE = 20 * 1024 * 1024; // 20MB
  public static final int MAXIMUM_FILES_COUNT = 6;
  private static final long serialVersionUID = 7704703308278059432L;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionView.class);
  protected H2 header = new H2();
  protected Tabs service = new Tabs();
  protected Tab lcmsms = new Tab();
  protected Tab smallMolecule = new Tab();
  protected Tab intactProtein = new Tab();
  protected TextArea comment = new TextArea();
  protected MultiFileMemoryBuffer uploadBuffer = new MultiFileMemoryBuffer();
  protected Upload upload = new Upload(uploadBuffer);
  protected Grid<SubmissionFile> files = new Grid<>();
  protected Column<SubmissionFile> filename;
  protected Column<SubmissionFile> remove;
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
        intactProteinSubmissionForm, comment, upload, files, save);
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
    comment.setMinHeight("10em");
    comment.setMinWidth("40em");
    upload.setId(UPLOAD);
    upload.setMaxFileSize(MAXIMUM_FILES_SIZE);
    upload.setMaxFiles(MAXIMUM_FILES_COUNT);
    upload.setMinHeight("2.5em");
    upload.addSucceededListener(event -> presenter.addFile(event.getFileName(),
        uploadBuffer.getInputStream(event.getFileName()), getLocale()));
    files.setId(FILES);
    files.setHeight("15em");
    files.setMinHeight("15em");
    files.setWidth("45em");
    files.setMinWidth("45em");
    filename = files.addColumn(new ComponentRenderer<>(file -> filenameAnchor(file)), FILENAME)
        .setKey(FILENAME);
    remove =
        files.addColumn(new ComponentRenderer<>(file -> removeButton(file)), REMOVE).setKey(REMOVE);
    save.setId(SAVE);
    save.addThemeName(PRIMARY);
    save.setIcon(VaadinIcon.CHECK.create());
    save.addClickListener(e -> presenter.save(getLocale()));
    presenter.init(this);
  }

  private Anchor filenameAnchor(SubmissionFile file) {
    Anchor link = new Anchor();
    link.setText(file.getFilename());
    link.setHref(new StreamResource(file.getFilename(),
        new ByteArrayStreamResourceWriter(file.getContent())));
    return link;
  }

  private Button removeButton(SubmissionFile file) {
    Button button = new Button();
    button.setIcon(VaadinIcon.TRASH.create());
    button.addClickListener(e -> presenter.removeFile(file));
    return button;
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final AppResources resources = new AppResources(SubmissionView.class, getLocale());
    final AppResources submissionResources = new AppResources(Submission.class, getLocale());
    final AppResources webResources = new AppResources(WebConstants.class, getLocale());
    header.setText(resources.message(HEADER));
    lcmsms.setLabel(LC_MS_MS.getLabel(getLocale()));
    smallMolecule.setLabel(SMALL_MOLECULE.getLabel(getLocale()));
    intactProtein.setLabel(INTACT_PROTEIN.getLabel(getLocale()));
    comment.setLabel(submissionResources.message(COMMENT));
    upload.setI18n(uploadI18N(getLocale()));
    filename.setHeader(resources.message(FILENAME));
    remove.setHeader(resources.message(REMOVE));
    save.setText(webResources.message(SAVE));
    presenter.localeChange(getLocale());
  }

  @Override
  public String getPageTitle() {
    final AppResources resources = new AppResources(getClass(), getLocale());
    final AppResources generalResources = new AppResources(WebConstants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
    presenter.setParameter(parameter);
  }
}
