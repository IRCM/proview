package ca.qc.ircm.proview;

import java.io.Serial;
import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Comparator for named objects.
 */
public class NamedComparator implements Comparator<Named>, Serializable {

  @Serial
  private static final long serialVersionUID = 3617602908990386176L;
  private final Locale locale;

  public NamedComparator(Locale locale) {
    this.locale = locale;
  }

  @Override
  public int compare(Named o1, Named o2) {
    Collator collator = Collator.getInstance(locale);
    return collator.compare(o1.getName(), o2.getName());
  }
}
