package ca.qc.ircm.proview.history;

import ca.qc.ircm.proview.history.DatabaseLogUtil.DatabaseBoolean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseConverterImpl implements DatabaseConverter {
  @Override
  public String convert(Object value) {
    if (value == null) {
      return null;
    }

    String converterValue;
    if (value instanceof Boolean) {
      converterValue = DatabaseBoolean.get((Boolean) value).databaseValue;
    } else if (value instanceof Date) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      converterValue = dateFormat.format((Date) value);
    } else {
      converterValue = String.valueOf(value);
    }
    converterValue = DatabaseLogUtil.reduceLength(converterValue, 255);
    return converterValue;
  }
}
