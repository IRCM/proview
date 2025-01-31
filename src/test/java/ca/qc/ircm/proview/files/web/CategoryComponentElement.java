package ca.qc.ircm.proview.files.web;

import com.vaadin.flow.component.html.testbench.AnchorElement;
import com.vaadin.flow.component.html.testbench.H3Element;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;
import java.util.List;

/**
 * {@link CategoryComponent} element.
 */
@Element("vaadin-vertical-layout")
@Attribute(name = "class", value = CategoryComponent.CATEGORY)
public class CategoryComponentElement extends VerticalLayoutElement {

  public H3Element header() {
    return $(H3Element.class).first();
  }

  public List<AnchorElement> guidelines() {
    return $(AnchorElement.class).all();
  }
}
