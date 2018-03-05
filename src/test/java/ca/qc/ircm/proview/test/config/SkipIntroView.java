package ca.qc.ircm.proview.test.config;

import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;

@SpringView(name = SkipIntroView.VIEW_NAME)
@SuppressWarnings("serial")
public class SkipIntroView extends CustomComponent implements BaseView {
  public static final String VIEW_NAME = "skipintro";
}
