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

import ca.qc.ircm.proview.treatment.Solvent;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Small molecule solvents field.
 */
public class SolventsField extends CustomField<List<Solvent>> implements LocaleChangeObserver {
  public static final String CLASS_NAME = "solvents";
  private static final long serialVersionUID = -6917758838566523871L;
  protected final Map<Solvent, Checkbox> fields = new LinkedHashMap<>();

  /**
   * Creates solvents field.
   */
  public SolventsField() {
    addClassName(CLASS_NAME);
    Stream.of(Solvent.values()).forEach(solvent -> {
      Checkbox field = new Checkbox();
      field.addClassName(solvent.name());
      field.addValueChangeListener(e -> updateValue());
      add(field);
      fields.put(solvent, field);
    });
  }

  @Override
  protected List<Solvent> generateModelValue() {
    return fields.entrySet().stream().filter(entry -> entry.getValue().getValue())
        .map(entry -> entry.getKey()).collect(Collectors.toList());
  }

  @Override
  protected void setPresentationValue(List<Solvent> values) {
    fields.values().forEach(field -> field.setValue(false));
    if (values != null) {
      values.stream().forEach(solvent -> fields.get(solvent).setValue(true));
    }
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    fields.entrySet()
        .forEach(entry -> entry.getValue().setLabelAsHtml(entry.getKey().getLabel(getLocale())));
  }
}
