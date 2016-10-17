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

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleSupport;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import pl.exsio.plupload.Plupload;

import java.util.HashMap;
import java.util.Map;

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
  public static final MassDetectionInstrumentSource[] SOURCES =
      new MassDetectionInstrumentSource[] { ESI, NSI };
  public static final MassDetectionInstrument[] INSTRUMENTS =
      new MassDetectionInstrument[] { VELOS, Q_EXACTIVE, TSQ_VANTAGE, ORBITRAP_FUSION };
  public static final Quantification[] QUANTIFICATION = new Quantification[] { LABEL_FREE, SILAC };
  private SubmissionFormPresenter presenter;
  protected Plupload structureUploader;
  protected Plupload gelImagesUploader;
  protected Map<Solvent, CheckBox> solventsFields = new HashMap<Solvent, CheckBox>();

  public void setPresenter(SubmissionFormPresenter presenter) {
    this.presenter = presenter;
  }

  /**
   * Creates SubmissionForm.
   */
  public SubmissionForm() {
    structureUploader = new Plupload();
    gelImagesUploader = new Plupload();
    solventsFields.put(Solvent.ACETONITRILE, acetonitrileSolventsField);
    solventsFields.put(Solvent.METHANOL, methanolSolventsField);
    solventsFields.put(Solvent.CHCL3, chclSolventsField);
    solventsFields.put(Solvent.OTHER, otherSolventsField);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
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
