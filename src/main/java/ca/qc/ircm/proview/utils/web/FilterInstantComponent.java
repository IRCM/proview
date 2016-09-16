package ca.qc.ircm.proview.utils.web;

/**
 * Filters instant based on a day resolution.
 */
public class FilterInstantComponent extends FilterInstantComponentDesign
    implements MessageResourcesComponent {
  private static final long serialVersionUID = -4819761558400463539L;
  private FilterInstantComponentPresenter presenter;

  public FilterInstantComponent() {
    removeComponent(popupLayout);
    filterButton.setContent(popupLayout);
  }

  public void setPresenter(FilterInstantComponentPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.attach();
  }

  @Override
  public void addStyleName(String style) {
    super.addStyleName(style);
    filterButton.addStyleName(style);
  }

  @Override
  public void setStyleName(String style) {
    super.setStyleName(style);
    filterButton.setStyleName(style);
  }
}
