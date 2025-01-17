package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.UPLOAD;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.security.Permission.WRITE;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.SubmissionFileProperties.FILENAME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FILES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.web.UploadInternationalization.uploadI18N;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFile;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.text.NormalizedComparator;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.ByteArrayStreamResourceWriter;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.ViewLayoutChild;
import ca.qc.ircm.proview.web.component.NotificationComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;

/**
 * Submission view.
 */
@Route(value = SubmissionView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ UserRole.USER })
public class SubmissionView extends VerticalLayout implements HasDynamicTitle,
    HasUrlParameter<Long>, LocaleChangeObserver, NotificationComponent, ViewLayoutChild {
  public static final String VIEW_NAME = "submission";
  public static final String ID = "submission-view";
  public static final String HEADER = "header";
  public static final String FILES_IOEXCEPTION = property(FILES, "ioexception");
  public static final String FILES_OVER_MAXIMUM = property(FILES, "overmaximum");
  public static final String REMOVE = "remove";
  public static final String SAVED = "saved";
  public static final int MAXIMUM_FILES_SIZE = 20 * 1024 * 1024; // 20MB
  public static final int MAXIMUM_FILES_COUNT = 6;
  private static final String MESSAGES_PREFIX = messagePrefix(SubmissionView.class);
  private static final String SUBMISSION_PREFIX = messagePrefix(Submission.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String SERVICE_PREFIX = messagePrefix(Service.class);
  private static final long serialVersionUID = 7704703308278059432L;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionView.class);
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
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private ListDataProvider<SubmissionFile> filesDataProvider =
      DataProvider.ofCollection(new ArrayList<>());
  private transient SubmissionService submissionService;
  private transient AuthenticatedUser authenticatedUser;

  @Autowired
  protected SubmissionView(SubmissionService submissionService, AuthenticatedUser authenticatedUser,
      LcmsmsSubmissionForm lcmsmsSubmissionForm,
      SmallMoleculeSubmissionForm smallMoleculeSubmissionForm,
      IntactProteinSubmissionForm intactProteinSubmissionForm) {
    this.submissionService = submissionService;
    this.authenticatedUser = authenticatedUser;
    this.lcmsmsSubmissionForm = lcmsmsSubmissionForm;
    this.smallMoleculeSubmissionForm = smallMoleculeSubmissionForm;
    this.intactProteinSubmissionForm = intactProteinSubmissionForm;
  }

  @PostConstruct
  void init() {
    logger.debug("submission view");
    setId(ID);
    add(service, lcmsmsSubmissionForm, smallMoleculeSubmissionForm, intactProteinSubmissionForm,
        comment, upload, files, save);
    expand(lcmsmsSubmissionForm, smallMoleculeSubmissionForm, intactProteinSubmissionForm, comment);
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
    upload.addSucceededListener(
        event -> addFile(event.getFileName(), uploadBuffer.getInputStream(event.getFileName())));
    files.setId(FILES);
    files.setHeight("15em");
    files.setWidth("45em");
    filename = files.addColumn(new ComponentRenderer<>(this::filenameAnchor)).setKey(FILENAME)
        .setSortProperty(FILENAME)
        .setComparator(NormalizedComparator.of(SubmissionFile::getFilename)).setFlexGrow(3);
    remove = files.addColumn(new ComponentRenderer<>(this::removeButton)).setKey(REMOVE);
    save.setId(SAVE);
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    save.setIcon(VaadinIcon.CHECK.create());
    save.addClickListener(e -> save());
    files.setItems(filesDataProvider);
    setSubmission(null);
  }

  private Anchor filenameAnchor(SubmissionFile file) {
    Anchor link = new Anchor();
    link.getElement().setAttribute("download", file.getFilename());
    link.setText(file.getFilename());
    link.setHref(new StreamResource(file.getFilename(),
        new ByteArrayStreamResourceWriter(file.getContent())));
    return link;
  }

  private Button removeButton(SubmissionFile file) {
    Button button = new Button();
    button.setIcon(VaadinIcon.TRASH.create());
    button.addClickListener(e -> removeFile(file));
    return button;
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    lcmsms.setLabel(getTranslation(SERVICE_PREFIX + LC_MS_MS.name()));
    smallMolecule.setLabel(getTranslation(SERVICE_PREFIX + SMALL_MOLECULE.name()));
    intactProtein.setLabel(getTranslation(SERVICE_PREFIX + INTACT_PROTEIN.name()));
    comment.setLabel(getTranslation(SUBMISSION_PREFIX + COMMENT));
    upload.setI18n(uploadI18N(getLocale()));
    filename.setHeader(getTranslation(MESSAGES_PREFIX + FILENAME));
    remove.setHeader(getTranslation(MESSAGES_PREFIX + REMOVE));
    save.setText(getTranslation(CONSTANTS_PREFIX + SAVE));
    binder.forField(comment).withNullRepresentation("").bind(COMMENT);
    setReadOnly();
    updateHeader();
  }

  private void updateHeader() {
    Submission submission = binder.getBean();
    viewLayout().ifPresent(layout -> layout.setHeaderText(getTranslation(MESSAGES_PREFIX + HEADER,
        submission.getId() != 0 ? 1 : 0, submission.getExperiment())));
  }

  @Override
  public String getPageTitle() {
    return getTranslation(MESSAGES_PREFIX + TITLE,
        getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME));
  }

  Service service() {
    Tab tab = service.getSelectedTab();
    if (tab == smallMolecule) {
      return Service.SMALL_MOLECULE;
    } else if (tab == intactProtein) {
      return Service.INTACT_PROTEIN;
    }
    return Service.LC_MS_MS;
  }

  void addFile(String filename, InputStream input) {
    logger.debug("received file {}", filename);
    SubmissionFile file = new SubmissionFile();
    file.setFilename(filename);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      FileCopyUtils.copy(input, output);
    } catch (IOException e) {
      showNotification(getTranslation(MESSAGES_PREFIX + FILES_IOEXCEPTION, filename));
      return;
    }
    file.setContent(output.toByteArray());
    if (filesDataProvider.getItems().size() >= MAXIMUM_FILES_COUNT) {
      showNotification(getTranslation(MESSAGES_PREFIX + FILES_OVER_MAXIMUM, MAXIMUM_FILES_COUNT));
      return;
    }
    filesDataProvider.getItems().add(file);
    filesDataProvider.refreshAll();
  }

  void removeFile(SubmissionFile file) {
    filesDataProvider.getItems().remove(file);
    filesDataProvider.refreshAll();
  }

  boolean valid() {
    boolean valid;
    Service service = service();
    valid = switch (service) {
      case LC_MS_MS -> lcmsmsSubmissionForm.isValid();
      case SMALL_MOLECULE -> smallMoleculeSubmissionForm.isValid();
      case INTACT_PROTEIN -> intactProteinSubmissionForm.isValid();
      default -> false;
    };
    valid = binder.isValid() && valid;
    return valid;
  }

  void save() {
    if (valid()) {
      Submission submission = binder.getBean();
      submission.setService(service());
      submission.setFiles(new ArrayList<>(filesDataProvider.getItems()));
      if (submission.getId() == 0) {
        logger.debug("save new submission {}", submission);
        submissionService.insert(submission);
      } else {
        logger.debug("save submission {}", submission);
        submissionService.update(submission, null);
      }
      showNotification(getTranslation(MESSAGES_PREFIX + SAVED, submission.getExperiment()));
      UI.getCurrent().navigate(SubmissionsView.class);
    }
  }

  Submission getSubmission() {
    return binder.getBean();
  }

  private void setSubmission(@Nullable Submission submission) {
    if (submission == null) {
      submission = new Submission();
      submission.setService(Service.LC_MS_MS);
      submission.setStorageTemperature(StorageTemperature.MEDIUM);
      submission.setSeparation(GelSeparation.ONE_DIMENSION);
      submission.setThickness(GelThickness.ONE);
      submission.setDigestion(ProteolyticDigestion.TRYPSIN);
      submission.setProteinContent(ProteinContent.SMALL);
      submission.setInjectionType(InjectionType.LC_MS);
      submission.setSource(MassDetectionInstrumentSource.ESI);
      submission.setIdentification(ProteinIdentification.REFSEQ);
      submission.setSamples(new ArrayList<>());
      submission.setFiles(new ArrayList<>());
      submission.setSolvents(new ArrayList<>());
    }
    if (submission.getSamples().isEmpty()) {
      SubmissionSample sample = new SubmissionSample();
      sample.setType(SampleType.SOLUTION);
      submission.getSamples().add(sample);
    }
    binder.setBean(submission);
    filesDataProvider.getItems().clear();
    filesDataProvider.getItems().addAll(submission.getFiles());
    filesDataProvider.refreshAll();
    lcmsmsSubmissionForm.setSubmission(submission);
    smallMoleculeSubmissionForm.setSubmission(submission);
    intactProteinSubmissionForm.setSubmission(submission);
    setReadOnly();
    updateHeader();
  }

  private void setReadOnly() {
    boolean readOnly = !authenticatedUser.hasPermission(binder.getBean(), WRITE);
    binder.setReadOnly(readOnly);
    upload.setVisible(!readOnly);
    files.getColumnByKey(REMOVE).setVisible(!readOnly);
    save.setEnabled(!readOnly);
  }

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter @Nullable Long parameter) {
    if (parameter != null) {
      setSubmission(submissionService.get(parameter).orElse(null));
    }
  }

  @Override
  public Optional<Component> getParent() {
    return super.getParent();
  }
}
