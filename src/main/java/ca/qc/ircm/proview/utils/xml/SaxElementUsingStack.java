package ca.qc.ircm.proview.utils.xml;

import org.xml.sax.Attributes;

import java.util.ListIterator;
import java.util.Stack;

/**
 * {@link SaxElement} containing its parent inside a {@link Stack}.
 */
public class SaxElementUsingStack implements SaxElement {
  private final String name;
  private final Attributes attributes;
  private final Stack<? extends SaxElement> parents;
  final StringBuilder text;

  /**
   * Creates {@link SaxElement} using {@link Stack}.
   *
   * @param name
   *          element's name
   * @param attributes
   *          element's attributes
   * @param parents
   *          element's parents
   */
  @SuppressWarnings("unchecked")
  public SaxElementUsingStack(String name, Attributes attributes,
      Stack<? extends SaxElement> parents) {
    this.name = name;
    this.attributes = attributes;
    this.parents = (Stack<? extends SaxElement>) parents.clone();
    this.text = new StringBuilder();
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public boolean name(String expectedName) {
    return name.equals(expectedName);
  }

  @Override
  public Attributes attributes() {
    return attributes;
  }

  @Override
  public String attribute(String name) {
    return attributes.getValue(name);
  }

  @Override
  public boolean hasAttribute(String name) {
    return attributes.getValue(name) != null;
  }

  @Override
  public SaxElement parent() {
    if (!parents.isEmpty()) {
      return parents.peek();
    } else {
      return null;
    }
  }

  @Override
  public boolean parent(String expectedParent) {
    SaxElement parent = parent();
    return parent != null && parent.name(expectedParent);
  }

  @Override
  public SaxElement ancestor(String name) {
    ListIterator<? extends SaxElement> elements = parents.listIterator(parents.size());
    while (elements.hasPrevious()) {
      SaxElement element = elements.previous();
      if (element.name().equals(name)) {
        return element;
      }
    }
    return null;
  }

  @Override
  public boolean hasAncestor(String name) {
    return ancestor(name) != null;
  }

  @Override
  public String getText() {
    return text.toString();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("SaxElement");
    builder.append("_");
    builder.append(name);
    return builder.toString();
  }
}
