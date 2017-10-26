package ca.qc.ircm.proview.solubilisation.web;

import ca.qc.ircm.proview.standard.web.StandardAdditionView;
import ca.qc.ircm.proview.web.component.SavedContainersComponent;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;

import javax.annotation.security.RolesAllowed;

/**
 * Solubilisation view.
 */
@SpringView(name = StandardAdditionView.VIEW_NAME)
@RolesAllowed({ "ADMIN" })
public class SolubilisationView extends CustomComponent
    implements BaseView, SavedContainersComponent {
  public static final String VIEW_NAME = "solubilisation";
  private static final long serialVersionUID = -7690840457260181052L;
}
