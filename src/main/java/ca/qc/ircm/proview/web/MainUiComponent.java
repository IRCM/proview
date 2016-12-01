package ca.qc.ircm.proview.web;

import com.vaadin.ui.Component;

public interface MainUiComponent extends Component {
  default MainUi getMainUi() {
    return (MainUi) getUI();
  }

  default String getUrl(String viewName) {
    return getMainUi().getUrl(viewName);
  }
}
