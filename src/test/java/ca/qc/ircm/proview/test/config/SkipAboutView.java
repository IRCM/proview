package ca.qc.ircm.proview.test.config;

import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;

@SpringView(name = SkipAboutView.VIEW_NAME)
@SuppressWarnings("serial")
public class SkipAboutView extends CustomComponent implements BaseView {
  public static final String VIEW_NAME = "skipabout";
}
