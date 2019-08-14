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
  private static final long serialVersionUID = -6917758838566523871L;
  protected final Map<Solvent, Checkbox> fields = new LinkedHashMap<>();

  /**
   * Creates solvents field.
   */
  public SolventsField() {
    Stream.of(Solvent.values()).forEach(solvent -> {
      Checkbox field = new Checkbox();
      field.addClassName(solvent.name());
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
    values.stream().forEach(solvent -> fields.get(solvent).setValue(true));
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    fields.entrySet()
        .forEach(entry -> entry.getValue().setLabelAsHtml(entry.getKey().getLabel(getLocale())));
  }
}
