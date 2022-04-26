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

package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.user.LaboratoryProperties.NAME;
import static ca.qc.ircm.proview.user.web.LaboratoryDialog.SAVED;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.security.Permission;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.LaboratoryService;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Laboratory dialog presenter.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LaboratoryDialogPresenter {
  private LaboratoryDialog dialog;
  private Binder<Laboratory> binder = new BeanValidationBinder<>(Laboratory.class);
  private LaboratoryService service;
  private AuthorizationService authorizationService;

  @Autowired
  LaboratoryDialogPresenter(LaboratoryService service, AuthorizationService authorizationService) {
    this.service = service;
    this.authorizationService = authorizationService;
  }

  void init(LaboratoryDialog dialog) {
    this.dialog = dialog;
    setLaboratory(null);
  }

  void localeChange(Locale locale) {
    final AppResources webResources = new AppResources(Constants.class, locale);
    binder.forField(dialog.name).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(NAME);
  }

  private void setReadOnly() {
    boolean readOnly = !authorizationService.hasPermission(binder.getBean(), Permission.WRITE);
    binder.setReadOnly(readOnly);
    dialog.save.setVisible(!readOnly);
    dialog.cancel.setVisible(!readOnly);
  }

  BinderValidationStatus<Laboratory> validate() {
    return binder.validate();
  }

  private boolean isValid() {
    return validate().isOk();
  }

  void save(Locale locale) {
    if (isValid()) {
      Laboratory laboratory = binder.getBean();
      final AppResources resources = new AppResources(LaboratoryDialog.class, locale);
      service.save(laboratory);
      dialog.showNotification(resources.message(SAVED, laboratory.getName()));
      dialog.close();
      dialog.fireSavedEvent();
    }
  }

  void cancel() {
    dialog.close();
  }

  Laboratory getLaboratory() {
    return binder.getBean();
  }

  void setLaboratory(Laboratory laboratory) {
    if (laboratory == null) {
      laboratory = new Laboratory();
    }
    binder.setBean(laboratory);
    setReadOnly();
  }
}
