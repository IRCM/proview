package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.files.web.CategoryComponent.CATEGORY;

import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;
import java.util.List;

/**
 * {@link GuidelinesView} element.
 */
@Element("vaadin-vertical-layout")
@Attribute(name = "id", value = GuidelinesView.ID)
public class GuidelinesViewElement extends VerticalLayoutElement {
  public List<CategoryComponentElement> categories() {
    return $(CategoryComponentElement.class).attributeContains("class", CATEGORY).all();
  }
}
