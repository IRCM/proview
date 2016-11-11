/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.msanalysis.InjectionType.DIRECT_INFUSION;
import static ca.qc.ircm.proview.msanalysis.InjectionType.LC_MS;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.ORBITRAP_FUSION;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.Q_EXACTIVE;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.TSQ_VANTAGE;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.VELOS;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource.ESI;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource.NSI;
import static ca.qc.ircm.proview.sample.ProteinIdentification.REFSEQ;
import static ca.qc.ircm.proview.sample.ProteinIdentification.UNIPROT;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.DIGESTED;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.TRYPSIN;
import static ca.qc.ircm.proview.sample.SampleSupport.DRY;
import static ca.qc.ircm.proview.sample.SampleSupport.GEL;
import static ca.qc.ircm.proview.sample.SampleSupport.SOLUTION;
import static ca.qc.ircm.proview.submission.GelSeparation.ONE_DIMENSION;
import static ca.qc.ircm.proview.submission.GelSeparation.TWO_DIMENSION;
import static ca.qc.ircm.proview.submission.GelThickness.ONE;
import static ca.qc.ircm.proview.submission.GelThickness.ONE_HALF;
import static ca.qc.ircm.proview.submission.GelThickness.TWO;
import static ca.qc.ircm.proview.submission.Quantification.LABEL_FREE;
import static ca.qc.ircm.proview.submission.Quantification.SILAC;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.StorageTemperature.LOW;
import static ca.qc.ircm.proview.submission.StorageTemperature.MEDIUM;

import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.web.PlateLayout;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleSupport;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStartedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Submission form.
 */
public class SubmissionForm extends SubmissionFormDesign implements MessageResourcesComponent {
  private static final long serialVersionUID = 7586918222688019429L;
  public static final ProteolyticDigestion[] DIGESTIONS =
      new ProteolyticDigestion[] { TRYPSIN, DIGESTED, ProteolyticDigestion.OTHER };
  public static final ProteinIdentification[] PROTEIN_IDENTIFICATIONS =
      new ProteinIdentification[] { REFSEQ, UNIPROT, ProteinIdentification.OTHER };
  public static final Service[] SERVICES =
      new Service[] { LC_MS_MS, SMALL_MOLECULE, INTACT_PROTEIN };
  public static final SampleSupport[] SUPPORT = new SampleSupport[] { SOLUTION, DRY, GEL };
  public static final StorageTemperature[] STORAGE_TEMPERATURES =
      new StorageTemperature[] { MEDIUM, LOW };
  public static final GelSeparation[] SEPARATION =
      new GelSeparation[] { ONE_DIMENSION, TWO_DIMENSION };
  public static final GelThickness[] THICKNESS = new GelThickness[] { ONE, ONE_HALF, TWO };
  public static final GelColoration[] COLORATION =
      new GelColoration[] { GelColoration.COOMASSIE, GelColoration.SYPRO, GelColoration.SILVER,
          GelColoration.SILVER_INVITROGEN, GelColoration.OTHER };
  public static final InjectionType[] INJECTION_TYPES =
      new InjectionType[] { LC_MS, DIRECT_INFUSION };
  public static final MassDetectionInstrumentSource[] SOURCES =
      new MassDetectionInstrumentSource[] { ESI, NSI };
  public static final ProteinContent[] PROTEIN_CONTENTS = new ProteinContent[] {
      ProteinContent.SMALL, ProteinContent.MEDIUM, ProteinContent.LARGE, ProteinContent.XLARGE };
  public static final MassDetectionInstrument[] INSTRUMENTS =
      new MassDetectionInstrument[] { VELOS, Q_EXACTIVE, TSQ_VANTAGE, ORBITRAP_FUSION };
  public static final Quantification[] QUANTIFICATION = new Quantification[] { LABEL_FREE, SILAC };
  private SubmissionFormPresenter presenter;
  protected UploadStateWindow uploadStateWindow;
  protected MultiFileUpload structureUploader;
  protected MultiFileUpload gelImagesUploader;
  protected MultiFileUpload filesUploader;
  protected PlateLayout samplesPlateLayout;
  protected List<List<TextField>> plateSampleNameFields = new ArrayList<>();

  public void setPresenter(SubmissionFormPresenter presenter) {
    this.presenter = presenter;
  }

  /**
   * Creates SubmissionForm.
   */
  public SubmissionForm() {
    uploadStateWindow = new UploadStateWindow();
    initPlateLayout();
    samplesPlateContainer.addComponent(samplesPlateLayout);
  }

  private void initPlateLayout() {
    int columns = Plate.Type.SUBMISSION.getColumnCount();
    int rows = Plate.Type.SUBMISSION.getRowCount();
    samplesPlateLayout = new PlateLayout(columns, rows);
    IntStream.range(0, columns).forEach(column -> {
      List<TextField> columnPlateSampleNameFields = new ArrayList<>();
      IntStream.range(0, rows).forEach(row -> {
        TextField nameField = new TextField();
        columnPlateSampleNameFields.add(nameField);
        samplesPlateLayout.addWellComponent(nameField, column, row);
      });
      plateSampleNameFields.add(columnPlateSampleNameFields);
    });
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public MultiFileUpload createStructureUploader(UploadStartedHandler startedHandler,
      UploadFinishedHandler finishedHandler, boolean multi) {
    structureUploader =
        new MultiFileUpload(startedHandler, finishedHandler, uploadStateWindow, multi);
    structureUploaderLayout.addComponent(structureUploader);
    return structureUploader;
  }

  public MultiFileUpload createGelImagesUploader(UploadStartedHandler startedHandler,
      UploadFinishedHandler finishedHandler, boolean multi) {
    gelImagesUploader =
        new MultiFileUpload(startedHandler, finishedHandler, uploadStateWindow, multi);
    gelImagesUploaderLayout.addComponent(gelImagesUploader);
    return gelImagesUploader;
  }

  public MultiFileUpload createFilesUploader(UploadStartedHandler startedHandler,
      UploadFinishedHandler finishedHandler, boolean multi) {
    filesUploader = new MultiFileUpload(startedHandler, finishedHandler, uploadStateWindow, multi);
    filesUploaderLayout.addComponent(filesUploader);
    return filesUploader;
  }

  public void showError(String message) {
    Notification.show(message, Notification.Type.ERROR_MESSAGE);
  }

  public void showWarning(String message) {
    Notification.show(message, Notification.Type.WARNING_MESSAGE);
  }

  public void afterSuccessfulSave(String message) {
    Notification.show(message, Notification.Type.TRAY_NOTIFICATION);
    getUI().getNavigator().navigateTo(SubmissionsView.VIEW_NAME);
  }
}
